<?php
/**
 * Admin Panel Update Script for Profile Update Functionality
 * Run this script in your admin panel directory: C:\xampp\htdocs\prize-bond-booking-system
 */

echo "Adding Profile Update Functionality to Admin Panel...\n\n";

// 1. Add route to routes/api.php
$apiRoutesFile = 'routes/api.php';
$newRoute = "Route::put('profile', [UserController::class, 'updateProfile'])->middleware('auth:sanctum');\n";

if (file_exists($apiRoutesFile)) {
    $content = file_get_contents($apiRoutesFile);
    if (strpos($content, "Route::put('profile'") === false) {
        $content .= "\n" . $newRoute;
        file_put_contents($apiRoutesFile, $content);
        echo "✅ Added profile update route to routes/api.php\n";
    } else {
        echo "⚠️  Profile update route already exists in routes/api.php\n";
    }
} else {
    echo "❌ routes/api.php not found\n";
}

// 2. Add method to UserController
$userControllerFile = 'app/Http/Controllers/UserController.php';
$updateMethod = '
    public function updateProfile(Request $request) {
        $user = auth()->user();
        
        $request->validate([
            \'full_name\' => \'sometimes|string|max:255\',
            \'phone\' => \'sometimes|string|max:20\',
            \'city\' => \'sometimes|string|max:100\',
            \'password\' => \'sometimes|string|min:6\'
        ]);
        
        if ($request->has(\'full_name\')) {
            $user->full_name = $request->full_name;
        }
        if ($request->has(\'phone\')) {
            $user->phone = $request->phone;
        }
        if ($request->has(\'city\')) {
            $user->city = $request->city;
        }
        if ($request->has(\'password\')) {
            $user->password = Hash::make($request->password);
        }
        
        $user->save();
        
        return response()->json([
            \'success\' => true,
            \'message\' => \'Profile updated successfully\'
        ]);
    }';

if (file_exists($userControllerFile)) {
    $content = file_get_contents($userControllerFile);
    if (strpos($content, 'public function updateProfile') === false) {
        $content .= $updateMethod;
        file_put_contents($userControllerFile, $content);
        echo "✅ Added updateProfile method to UserController.php\n";
    } else {
        echo "⚠️  updateProfile method already exists in UserController.php\n";
    }
} else {
    echo "❌ UserController.php not found\n";
}

// 3. Check User model fillable fields
$userModelFile = 'app/Models/User.php';
if (file_exists($userModelFile)) {
    $content = file_get_contents($userModelFile);
    if (strpos($content, "'full_name'") === false || strpos($content, "'phone'") === false || strpos($content, "'city'") === false) {
        echo "⚠️  Please add these fields to User model fillable array:\n";
        echo "   'full_name', 'phone', 'city', 'avatar'\n";
    } else {
        echo "✅ User model has required fillable fields\n";
    }
} else {
    echo "❌ User model not found\n";
}

echo "\n🎉 Admin panel update completed!\n";
echo "Now test the Android app profile update functionality.\n";
?>
