# Backend Fix - GET Paid Services Method

## Problem
After purchase, the Android app still shows "Get Service" button because the GET endpoint doesn't check if the user has purchased the service.

## Solution
Update your GET `/api/paid-services` endpoint in your controller to include purchase status for each service.

---

## Complete Backend Code

### In your AuthController.php (or wherever paid-services endpoint is)

```php
public function getPaidServices()
{
    try {
        $user = auth()->user();
        
        if (!$user) {
            return response()->json([
                'success' => false,
                'message' => 'Unauthorized'
            ], 401);
        }
        
        // Get all active services
        $services = \App\Models\PaidService::where('is_active', 1)->get();
        
        // Add purchase status for each service
        $services = $services->map(function($service) use ($user) {
            // Check if user has purchased this service
            $purchase = \App\Models\PaidServiceSale::where('user_id', $user->id)
                ->where('service_id', $service->id)
                ->where('status', 'active')
                ->first();
            
            // Add has_purchased field
            $service->has_purchased = $purchase ? true : false;
            
            // Add show_buy_button field
            $service->show_buy_button = !$purchase;
            
            // Add golden_text only if purchased
            if ($purchase) {
                // Generate or get lucky numbers for this user
                // Option 1: Store in purchase record
                if (isset($purchase->golden_numbers)) {
                    $service->golden_text = $purchase->golden_numbers;
                } else {
                    // Option 2: Generate random numbers
                    $service->golden_text = $this->generateLuckyNumbers();
                }
            } else {
                $service->golden_text = null;
            }
            
            // Return only needed fields
            return [
                'id' => $service->id,
                'title' => $service->title,
                'price' => $service->price,
                'description' => $service->description,
                'image' => $service->image,
                'is_active' => $service->is_active,
                'has_purchased' => $service->has_purchased,
                'show_buy_button' => $service->show_buy_button,
                'golden_text' => $service->golden_text
            ];
        });
        
        return response()->json([
            'success' => true,
            'data' => $services
        ]);
        
    } catch (\Exception $e) {
        \Log::error('Get Paid Services API Error: ' . $e->getMessage());
        return response()->json([
            'success' => false,
            'message' => 'Failed to fetch services',
            'error' => $e->getMessage()
        ], 500);
    }
}

// Helper method to generate lucky numbers
private function generateLuckyNumbers()
{
    $numbers = [];
    for ($i = 0; $i < 5; $i++) {
        $numbers[] = str_pad(rand(1, 99), 2, '0', STR_PAD_LEFT);
    }
    return implode(', ', $numbers);
}
```

---

## Option: Store Lucky Numbers in Purchase Record

If you want to save the lucky numbers so they're the same every time the user views them:

### 1. Add Migration

```php
// In your existing paid_service_sales table migration or create new migration
public function up()
{
    Schema::table('paid_service_sales', function (Blueprint $table) {
        $table->text('golden_numbers')->nullable()->after('amount');
    });
}
```

### 2. Update Purchase Method

```php
public function purchasePaidService(Request $request)
{
    try {
        $request->validate([
            'service_id' => 'required|exists:paid_services,id'
        ]);

        $user = auth()->user();
        $service = \App\Models\PaidService::findOrFail($request->service_id);

        // Check if service is active
        if (!$service->is_active) {
            return response()->json([
                'success' => false,
                'message' => 'This service is not available'
            ], 400);
        }

        // Check if user already purchased this service
        $existingPurchase = \App\Models\PaidServiceSale::where('user_id', $user->id)
            ->where('service_id', $service->id)
            ->where('status', 'active')
            ->first();

        if ($existingPurchase) {
            return response()->json([
                'success' => false,
                'message' => 'You have already purchased this service'
            ], 400);
        }

        // Check if user has enough balance
        if ($user->balance < $service->price) {
            return response()->json([
                'success' => false,
                'message' => 'Insufficient balance. You need Rs. ' . number_format($service->price, 2)
            ], 400);
        }

        // Deduct amount from user balance
        $user->balance -= $service->price;
        $user->save();

        // Generate lucky numbers
        $luckyNumbers = $this->generateLuckyNumbers();

        // Create purchase record with lucky numbers
        $purchase = \App\Models\PaidServiceSale::create([
            'user_id' => $user->id,
            'service_id' => $service->id,
            'amount' => $service->price,
            'status' => 'active',
            'golden_numbers' => $luckyNumbers, // Save lucky numbers
            'purchased_at' => now()
        ]);

        return response()->json([
            'success' => true,
            'message' => 'Service purchased successfully',
            'data' => [
                'purchase_id' => $purchase->id,
                'remaining_balance' => $user->balance
            ]
        ]);

    } catch (\Exception $e) {
        \Log::error('Purchase Paid Service API Error: ' . $e->getMessage());
        return response()->json([
            'success' => false,
            'message' => 'Failed to purchase service',
            'error' => $e->getMessage()
        ], 500);
    }
}

// Helper method to generate lucky numbers
private function generateLuckyNumbers()
{
    $numbers = [];
    for ($i = 0; $i < 5; $i++) {
        $numbers[] = str_pad(rand(1, 99), 2, '0', STR_PAD_LEFT);
    }
    return implode(', ', $numbers);
}
```

---

## Testing the Fix

### 1. Test GET Endpoint Without Auth
```bash
curl -X GET http://yourdomain.com/api/paid-services
```
**Expected**: 401 Unauthorized

### 2. Test GET Endpoint With Auth (No Purchases)
```bash
curl -X GET http://yourdomain.com/api/paid-services \
  -H "Authorization: Bearer YOUR_TOKEN"
```
**Expected Response**:
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "title": "Golden Guess",
      "price": "500",
      "description": "Get lucky numbers",
      "image": "/images/golden.png",
      "is_active": 1,
      "has_purchased": false,
      "show_buy_button": true,
      "golden_text": null
    }
  ]
}
```

### 3. Purchase a Service
```bash
curl -X POST http://yourdomain.com/api/paid-service/purchase \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"service_id": 1}'
```

### 4. Test GET Endpoint Again (After Purchase)
```bash
curl -X GET http://yourdomain.com/api/paid-services \
  -H "Authorization: Bearer YOUR_TOKEN"
```
**Expected Response**:
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "title": "Golden Guess",
      "price": "500",
      "description": "Get lucky numbers",
      "image": "/images/golden.png",
      "is_active": 1,
      "has_purchased": true,
      "show_buy_button": false,
      "golden_text": "12, 34, 56, 78, 90"
    }
  ]
}
```

---

## Checklist

After implementing this fix:

- [ ] GET endpoint requires authentication
- [ ] GET endpoint checks user's purchase status for each service
- [ ] Returns `has_purchased: true` for purchased services
- [ ] Returns `show_buy_button: false` for purchased services
- [ ] Returns `golden_text` with lucky numbers for purchased services
- [ ] Returns `golden_text: null` for non-purchased services
- [ ] Lucky numbers are consistent (saved in database)
- [ ] Android app correctly displays purchased state
- [ ] Buy button disappears after purchase
- [ ] Purchased badge appears
- [ ] Golden text card appears

---

## Quick Fix Summary

**The issue is**: Your backend GET endpoint doesn't check if the authenticated user has purchased each service.

**The solution is**: 
1. Accept authentication token in GET endpoint
2. Check `paid_service_sales` table for each service
3. Add `has_purchased`, `show_buy_button`, and `golden_text` fields to response
4. Return these fields based on purchase status

**This will make**: Android app correctly show/hide buttons and premium content based on actual purchase status.

---

**Implementation Time**: ~10 minutes
**Testing Time**: ~5 minutes
**Impact**: Fixes the entire purchase UI issue
