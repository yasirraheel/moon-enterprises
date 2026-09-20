# 🎮 Moon Enterprises Android App

[![Version](https://img.shields.io/badge/version-2.3-blue.svg)](https://github.com/yasirraheel/geo-enterprises-android-app)
[![Android](https://img.shields.io/badge/Android-7.0%2B-green.svg)](https://developer.android.com)
[![License](https://img.shields.io/badge/license-MIT-orange.svg)](LICENSE)

A comprehensive Android application for Moon Enterprises - a modern mobile platform for managing gaming bonds, financial transactions, and user accounts with real-time updates and seamless backend integration.

**Current Version:** 2.3 (June 2026)

## 🚀 Features

### 🏢 Dealership System (New)
- **Become a Dealer** - Users can apply for dealership directly from the app
- **Dealer Badge** - Verified dealers get a special badge on the dashboard
- **Commission System** - Dealers earn commission on orders
- **Real-time Commission Display** - See potential savings and commission in real-time while placing orders
- **Commission Tracking** - Track total earned commission on the dashboard
- **Status Management** - Dynamic UI based on application status (Pending, Approved, Rejected)

### 🔐 Authentication & User Management
- **Secure Login** - Phone-based authentication with secure token management
- **User Registration** - Complete registration flow with validation
- **Profile Management** - Update profile information with avatar upload
- **Session Management** - Persistent login sessions with automatic token refresh
- **Balance Tracking** - Real-time user balance with refresh capability

### 🎮 Game Categories & Orders
- **Dynamic Game Categories** - Browse games fetched from admin panel
- **Subcategories** - Nested category structure with smooth navigation
- **Bond Selection** - Choose from available bonds within each category
- **Order Placement** - Place orders with RTTP, First, and Second values
- **Order Validation** - Smart validation (minimum 5, divisible by 5)
- **Order Tracking** - View order history with status filtering (pending/approved/rejected/ok)
- **Order Export** - Export orders to PDF format
- **Game-wise Orders** - View orders grouped by game

### 💰 Financial Management
- **Deposit System** - Submit deposit requests with payment proof
- **Payment Methods** - Dynamic payment methods from admin panel
- **Deposit History** - View all deposits with status filtering
- **Withdrawal Requests** - Request withdrawals with bank account details
- **Withdrawal History** - Track withdrawal status and history
- **Account Persistence** - Save bank account details for future use
- **Balance Refresh** - Manual balance refresh with animation

### 🔔 Notifications
- **Real-time Notifications** - Dynamic notifications from admin panel
- **Notification Count** - Badge counter on navigation drawer
- **Notification Details** - Full notification content with images
- **Mark as Read** - Interactive notification management

### 🎨 UI/UX Features
- **Material Design** - Modern UI following Material Design guidelines
- **Custom Snackbar System** - Consistent success/error/warning messages
- **Loading Dialogs** - Professional loading indicators
- **Shimmer Effects** - Skeleton loading for better UX
- **Smooth Animations** - Professional transitions and animations
- **Responsive Design** - Optimized for all screen sizes
- **Navigation Drawer** - Easy access to all app sections
- **Portrait Mode** - All screens locked to portrait orientation
- **Empty States** - User-friendly empty state designs

### 📹 Help & Support
- **Help Videos** - Integrated help videos for key features
- **YouTube Integration** - YouTube videos open in YouTube app
- **Direct Video Playback** - Local videos play fullscreen without top bar
- **Step-by-step Guides** - Tutorial videos for deposits, orders, and more

## 📝 Recent Updates (June 2026)

### Latest Changes (v2.3)
- **New App Icon** - Updated the app icon with the new Moon Enterprises logo.
- **Dynamic Splash Screen** - The splash screen now dynamically loads the logo from the server with a smooth gradient placeholder.

### Previous Updates (v2.2)
- **Custom Transaction Dialogs** - Replaced default Android alerts with beautiful, app-themed custom dialogs for Deposit and Withdrawal histories.
- **Bank Name Autocomplete** - Upgraded the Bank Name input to a Dropdown/Autocomplete field with common Pakistani banks pre-filled.
- **UI Padding & Font Fixes** - Fixed Noto Nastaliq Urdu font clipping issues and minimized excessive padding in withdrawal and deposit screens.
- **Amount Input Refinement** - Removed decimal points from the amount input keyboard to ensure whole number entries.
- **Dynamic What's New** - Integrated dynamic release notes display powered by the backend API.

### Previous Updates (v2.1)
- **Home Games Layout Fix** - Fixed home dashboard list rendering so game items no longer get hidden under action buttons.
- **Voucher Export Upgrade** - Improved game-order voucher export with stable canvas rendering and dedicated status column.
- **PDF/Table Fit Improvements** - Adjusted export spacing and measurements so right-side content (including totals/status) is not clipped.
- **Bulk Order Export Stability** - Fixed `OrdersActivity` PDF export where row text could disappear while status badge backgrounds were visible.
- **Top Bar Standardization** - Matched key activity headers to home top-bar design for a cleaner and more consistent UI.
- **Status Bar Insets Fix** - Reworked status bar padding logic to dynamic inset-based handling for better device compatibility.
- **UI Consistency Pass** - Unified route-level header behavior for Transactions, Orders, Deposits, Withdrawals, and Subcategory flows.

### Previous Updates (v1.8)
- ✅ **Dealership Security** - Implemented secure dealership application flow requiring admin-assigned Dealership ID
- ✅ **ID Validation** - Added smart validation dialog for dealership ID with simulated processing delay
- ✅ **Registration Flow** - Streamlined sign-up by auto-handling password confirmation (hidden from UI)
- ✅ **Notification UI** - Updated system notification badges to white text on green background for better visibility
- ✅ **Dealership System** - Complete dealership application and management flow
- ✅ **Dealer Commission** - Implemented commission calculation and display logic
- ✅ **Real-time Savings** - Show "You will pay" and "Commission" in real-time during order placement
- ✅ **Dashboard Updates** - Added dealer badge (with %), commission earned display, and status-based UI
- ✅ **Data Sync** - Optimized data synchronization between local storage and server
- ✅ **UI/UX Improvements** - Enhanced dialogs, badges, and status indicators
- ✅ **Bug Fixes** - Fixed API response handling, null safety checks, and AAPT errors

### Previous Updates (v1.7)
- ✅ **Video Guide Enhancement** - YouTube videos now open in YouTube app, direct videos play fullscreen without status bar
- ✅ **RTRTP Validation** - Enhanced input validation for RTRTP fields
- ✅ **About Dialog** - Fixed version display in About dialog
- ✅ **PDF Export** - Enhanced PDF export formatting with better layout
- ✅ **Share Button** - Added share button back for sharing orders
- ✅ **XML Parsing** - Resolved XML parsing errors and enhanced dialog responsiveness
- ✅ **App Update System** - Complete app update and share system implementation
- ✅ **Copy Account Number** - Added feature to copy account numbers in deposit requests
- ✅ **Timezone Display** - Fixed timezone display issues
- ✅ **UI Navigation** - Improved UI navigation and user workflow
- ✅ **Server URL** - Fixed server URL configuration
- ✅ **Centralized Configuration** - Replaced hardcoded API URLs with centralized configuration
- ✅ **Transactions Feature** - Added Transactions feature with custom UI
- ✅ **Subcategory Timing** - Added subcategory timing validation

### Bug Fixes
- Fixed XML parsing errors in layout files
- Resolved timezone display issues
- Fixed server URL configuration
- Improved dialog responsiveness
- Enhanced PDF export formatting

### Improvements
- Enhanced RTRTP input validation (minimum 5, divisible by 5)
- Better error handling and user feedback
- Improved UI/UX consistency across the app
- Optimized performance and loading times
- Better code organization with centralized configuration

## 🛠️ Technical Stack

### Frontend
- **Language**: Java 11
- **Min SDK**: Android 7.0 (API 24)
- **Target SDK**: Android 14 (API 36)
- **Architecture**: MVC Pattern with Activity-based navigation
- **UI Framework**: Android SDK with Material Design Components

### Networking & Data
- **HTTP Client**: Retrofit 2.9.0
- **JSON Parser**: Gson 2.10.1
- **Interceptor**: OkHttp 4.12.0 with logging
- **Image Loading**: Glide 4.16.0 with caching
- **Data Storage**: SharedPreferences with Gson serialization

### UI Components
- **Material Design**: 1.4.0+
- **RecyclerView**: 1.3.2
- **CardView**: 1.0.0
- **ConstraintLayout**: Latest
- **Navigation**: 2.7.6
- **Lifecycle**: 2.7.0 (ViewModel & LiveData)

### Key Libraries
```gradle
// Networking
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
implementation 'com.squareup.okhttp3:logging-interceptor:4.12.0'

// Image Loading
implementation 'com.github.bumptech.glide:glide:4.16.0'

// JSON Parsing
implementation 'com.google.code.gson:gson:2.10.1'

// UI Components
implementation 'androidx.recyclerview:recyclerview:1.3.2'
implementation 'androidx.cardview:cardview:1.0.0'
implementation 'com.google.android.material:material:1.4.0'

// Navigation & Lifecycle
implementation 'androidx.navigation:navigation-fragment:2.7.6'
implementation 'androidx.lifecycle:lifecycle-viewmodel:2.7.0'
```

### Backend Integration
- **API**: RESTful API
- **Base URL**: https://geoenterprises.shahabtech.com/api/
- **Authentication**: Bearer Token
- **File Upload**: Multipart form data
- **Connection Timeout**: 30 seconds
- **Read/Write Timeout**: 30 seconds

## 📁 Project Structure

```
app/
├── src/main/
│   ├── java/com/geo/enterprises/
│   │   ├── api/                        # API Layer
│   │   │   ├── ApiClient.java          # Retrofit singleton client
│   │   │   └── ApiService.java         # API endpoints interface
│   │   │
│   │   ├── auth/                       # Authentication
│   │   │   ├── LoginActivity.java      # Login screen
│   │   │   └── RegisterActivity.java   # Registration screen
│   │   │
│   │   ├── dashboard/                  # Main Dashboard
│   │   │   ├── DashboardActivity.java  # Main app screen with drawer
│   │   │   └── GameCategoryAdapter.java # Game categories RecyclerView
│   │   │
│   │   ├── orders/                     # Order Management
│   │   │   ├── OrdersActivity.java     # Place new order
│   │   │   ├── YourOrdersActivity.java # View order history
│   │   │   ├── GameOrdersActivity.java # Game-wise order view
│   │   │   └── OrdersAdapter.java      # Orders RecyclerView adapter
│   │   │
│   │   ├── subcategory/               # Subcategories (Bonds)
│   │   │   ├── SubcategoryActivity.java
│   │   │   └── SubcategoryAdapter.java
│   │   │
│   │   ├── deposit/                   # Deposit Management
│   │   │   ├── DepositActivity.java    # Submit deposit request
│   │   │   ├── DepositsActivity.java   # Deposit history
│   │   │   └── DepositAdapter.java     # Deposit list adapter
│   │   │
│   │   ├── withdrawal/                # Withdrawal Management
│   │   │   ├── WithdrawalActivity.java # Submit withdrawal request
│   │   │   ├── WithdrawalsActivity.java # Withdrawal history
│   │   │   └── WithdrawalAdapter.java  # Withdrawal list adapter
│   │   │
│   │   ├── notifications/             # Notifications
│   │   │   ├── NotificationsActivity.java
│   │   │   ├── NotificationAdapter.java
│   │   │   ├── NotificationItem.java
│   │   │   └── NotificationCount.java
│   │   │
│   │   ├── settings/                  # Settings & Profile
│   │   │   └── SettingsActivity.java   # Profile management
│   │   │
│   │   ├── models/                    # Data Models
│   │   │   ├── User.java              # User model
│   │   │   ├── GameCategory.java      # Game category
│   │   │   ├── Subcategory.java       # Bond model
│   │   │   ├── Order.java             # Order model
│   │   │   ├── Deposit.java           # Deposit model
│   │   │   ├── Withdrawal.java        # Withdrawal model
│   │   │   ├── AppSettings.java       # App settings
│   │   │   ├── PaymentMethod.java     # Payment method
│   │   │   ├── ApiResponse.java       # Generic API response
│   │   │   └── [Other response models]
│   │   │
│   │   ├── utils/                     # Utility Classes
│   │   │   ├── SnackbarUtils.java     # Custom snackbar system
│   │   │   ├── LoadingDialog.java     # Loading dialog
│   │   │   ├── ConfirmationDialog.java # Confirmation dialogs
│   │   │   ├── PreferenceManager.java  # SharedPreferences wrapper
│   │   │   ├── ShimmerView.java       # Shimmer loading effect
│   │   │   └── ActivityTransitionUtils.java
│   │   │
│   │   └── MainActivity.java          # Splash screen
│   │
│   ├── res/
│   │   ├── layout/                    # XML Layouts
│   │   │   ├── activity_main.xml      # Splash screen
│   │   │   ├── activity_login_compact.xml
│   │   │   ├── activity_dashboard_drawer.xml
│   │   │   ├── activity_orders.xml
│   │   │   ├── activity_subcategory.xml
│   │   │   ├── item_game_category.xml
│   │   │   ├── item_order.xml
│   │   │   ├── snackbar_success.xml   # Custom snackbar layouts
│   │   │   ├── snackbar_danger.xml
│   │   │   └── [Other layouts]
│   │   │
│   │   ├── drawable/                  # Icons & Drawables
│   │   │   ├── ic_refresh.xml         # Balance refresh icon
│   │   │   └── [Other icons]
│   │   │
│   │   ├── values/                    # Resources
│   │   │   ├── strings.xml            # String resources
│   │   │   ├── colors.xml             # Color palette
│   │   │   ├── themes.xml             # App themes
│   │   │   └── styles.xml             # Custom styles
│   │   │
│   │   └── mipmap/                    # App Icons
│   │       └── ic_launcher/
│   │
│   └── AndroidManifest.xml            # App manifest
│
├── build.gradle                       # App-level Gradle config
└── proguard-rules.pro                # ProGuard rules
```

## 🔧 Setup & Installation

### Prerequisites
- **Android Studio**: Hedgehog (2023.1.1) or later
- **Android SDK**: API 24+ (Android 7.0)
- **Java**: JDK 11 or later
- **Git**: Latest version
- **Minimum RAM**: 4GB (8GB recommended)
- **Internet Connection**: Required for API access

### Installation Steps

1. **Clone the Repository**
   ```bash
   git clone https://github.com/yasirraheel/geo-enterprises-android-app.git
   cd geo-enterprises-android-app
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select **File** → **Open**
   - Navigate to the cloned directory
   - Click **OK** and wait for indexing

3. **Configure the Project**
   - Android Studio will automatically detect the Gradle configuration
   - Wait for Gradle sync to complete
   - If prompted, accept SDK licenses

4. **API Configuration** (Optional)
   - Default API URL: `https://geoenterprises.shahabtech.com/api/`
   - To change: Edit `ApiClient.java` → `BASE_URL` constant
   
   ```java
   // app/src/main/java/com/geo/enterprises/api/ApiClient.java
   private static final String BASE_URL = "YOUR_API_URL";
   ```

5. **Build the Project**
   - Click **Build** → **Make Project** (Ctrl+F9)
   - Or sync with Gradle files (if not done automatically)

6. **Run the App**
   - Connect an Android device via USB (with USB debugging enabled)
   - Or start an Android Virtual Device (AVD)
   - Click **Run** → **Run 'app'** (Shift+F10)
   - Select your device and click **OK**

### Building APK

**Debug APK:**
```bash
./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk
```

**Release APK:**
```bash
./gradlew assembleRelease
# Output: app/build/outputs/apk/release/app-release.apk
```

### Signing Configuration (For Production)

1. **Generate Keystore** (if not exists):
   ```bash
   keytool -genkey -v -keystore geoenterprises.jks -keyalg RSA -keysize 2048 -validity 10000 -alias geoenterprises
   ```

2. **Configure in `build.gradle`**:
   ```gradle
   android {
       signingConfigs {
           release {
               storeFile file('geoenterprises.jks')
               storePassword 'your_store_password'
               keyAlias 'geoenterprises'
               keyPassword 'your_key_password'
           }
       }
       buildTypes {
           release {
               signingConfig signingConfigs.release
           }
       }
   }
   ```

## 🌐 API Integration

### Base URL
```
https://geoenterprises.shahabtech.com/api/
```

### Authentication
All protected endpoints require Bearer token authentication:
```
Authorization: Bearer <access_token>
```

### API Endpoints

#### Authentication
- **POST** `/login` - User authentication with phone/email
- **POST** `/register` - Create new user account
- **POST** `/logout` - Logout user (invalidate token)
- **GET** `/profile` - Get user profile data
- **PUT** `/profile` - Update user profile

#### App Settings
- **GET** `/settings` - Get app configuration (logo, title, currency)

#### Game Categories & Subcategories
- **GET** `/game-categories` - List all game categories
- **GET** `/subcategories?category_id={id}` - Get bonds for a category

#### Orders
- **POST** `/orders/create` - Place new order
- **GET** `/orders?per_page={n}&page={p}&status={s}` - Get user orders

#### Deposits
- **POST** `/deposits/create` - Submit deposit request with payment proof
- **GET** `/deposits?status={s}&per_page={n}` - Get deposit history

#### Withdrawals
- **POST** `/withdrawals/create` - Submit withdrawal request
- **GET** `/withdrawals?status={s}&per_page={n}` - Get withdrawal history

#### Payment Methods
- **GET** `/payment-methods` - Get available payment methods

#### Notifications
- **GET** `/notifications` - Get user notifications
- **GET** `/notifications/count` - Get unread notification count

#### Balance
- **GET** `/user/balance` - Get current user balance

### Request/Response Examples

**Login Request:**
```json
POST /login
{
  "phone": "1234567890",
  "password": "password123"
}
```

**Login Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "user": {
      "id": 1,
      "name": "John Doe",
      "phone": "1234567890",
      "balance": 1500.00
    },
    "access_token": "eyJ0eXAiOiJKV1QiLCJhbGc..."
  }
}
```

**Create Order Request:**
```json
POST /orders/create
Authorization: Bearer <token>
{
  "user_id": "1",
  "game_name": "Prize Bond",
  "bond_name": "100",
  "rttp": "10",
  "first": "15",
  "second": "20",
  "user_phone": "1234567890"
}
```

**API Response Structure:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { /* Response data */ },
  "errors": null
}
```

### Error Handling
The app handles various error scenarios:
- **Network Errors**: "Network error. Please check your connection."
- **API Errors**: Displays server error message
- **Validation Errors**: Client-side validation with snackbar messages
- **Session Expired**: Redirects to login with appropriate message

### Timeouts & Retry
- **Connection Timeout**: 30 seconds
- **Read Timeout**: 30 seconds
- **Write Timeout**: 30 seconds
- **Retry**: Manual retry through error snackbar actions

## 🎨 UI/UX Features

### Design Principles
- **Material Design 3** - Following Google's latest Material Design guidelines
- **Consistent Theming** - Unified color scheme across all screens
- **Responsive Layout** - Adapts to different screen sizes and densities
- **Smooth Animations** - 360° rotation, fade-in/out, slide transitions
- **Accessibility** - High contrast colors and readable fonts

### Custom Components

#### 1. Snackbar System (SnackbarUtils)
Professional snackbar messages with custom layouts:
- **Success**: Green background with checkmark icon
- **Error/Danger**: Red background with error icon
- **Warning**: Yellow background with warning icon
- **Info**: Blue background with info icon
- **Network Error**: Predefined network error message
- **Validation Error**: Field-specific validation messages

```java
// Usage examples
SnackbarUtils.showSuccess(view, "Order placed successfully!");
SnackbarUtils.showError(view, "Failed to submit order");
SnackbarUtils.showNetworkError(view);
SnackbarUtils.showValidationError(view, "phone number");
```

#### 2. Loading Dialog
- Customizable loading message
- Non-cancelable by default
- Professional circular progress indicator
- Used during API calls

#### 3. Confirmation Dialog
- Used for logout and delete actions
- Custom title, message, and action buttons
- Prevents accidental actions

#### 4. Shimmer Loading
- Skeleton loading effect while fetching data
- Used in list views (orders, deposits, withdrawals)
- Better perceived performance

### Key Screens

#### 1. Splash Screen
- Dynamic app logo from admin panel
- App title and tagline
- Auto-navigation after 2 seconds
- Checks login status

#### 2. Login/Register
- Phone-based authentication
- Input validation
- Password visibility toggle
- "Remember me" functionality
- Smooth transitions

#### 3. Dashboard
- **Navigation Drawer** with user info
- **Balance Card** with refresh icon
- **Game Categories** in RecyclerView
- **Notifications Badge** counter
- Menu items: Orders, Deposits, Withdrawals, Settings

#### 4. Orders Screen
- Game and bond information
- Three input fields: RTTP, First, Second
- Real-time total calculation
- Validation (min 5, divisible by 5)
- Snackbar error messages

#### 5. Order History
- Filter tabs: All, Pending, Approved, Rejected, OK
- Status color coding
- Export to PDF functionality
- Game-wise order grouping

#### 6. Deposit/Withdrawal
- Amount input with balance display
- Payment method selection (deposits)
- Bank account details (withdrawals)
- Image upload for payment proof
- Account detail persistence
- Status filtering (All, Pending, Approved, Rejected)

### Color Scheme
```xml
<color name="primary_color">#1976D2</color>
<color name="primary_dark">#1565C0</color>
<color name="accent_color">#FF4081</color>
<color name="success_color">#4CAF50</color>
<color name="error_color">#F44336</color>
<color name="warning_color">#FF9800</color>
<color name="pending_color">#FFC107</color>
<color name="approved_color">#4CAF50</color>
<color name="rejected_color">#F44336</color>
```

### Typography
- **Display**: Roboto Bold, 24sp
- **Headline**: Roboto Medium, 20sp
- **Body**: Roboto Regular, 16sp
- **Caption**: Roboto Light, 14sp

### Icons
- Material Icons for actions
- Custom SVG icons for game categories
- Vector drawables for scalability
- Proper tinting and sizing

## 📱 App Flow

### User Journey

1. **App Launch** → Splash Screen
   - Loads app settings from API
   - Checks authentication status
   - Navigates to Dashboard (logged in) or Login (guest)

2. **Authentication Flow**
   - Login with phone/email + password
   - OR Register new account
   - Receive auth token
   - Save user data locally
   - Navigate to Dashboard

3. **Dashboard**
   - View balance with refresh option
   - Browse game categories
   - Access navigation drawer menu
   - Check notifications

4. **Place Order Flow**
   - Select game category
   - Choose subcategory (bond)
   - Enter RTTP, First, Second values
   - Validate inputs
   - Confirm order
   - Receive confirmation

5. **Deposit Flow**
   - Navigate to Deposit screen
   - Select payment method
   - Enter amount
   - Upload payment proof
   - Submit request
   - View in deposit history

6. **Withdrawal Flow**
   - Navigate to Withdrawal screen
   - Enter amount (validated against balance)
   - Provide bank account details
   - Submit request
   - View in withdrawal history

7. **Profile Management**
   - View/edit profile information
   - Upload profile picture
   - Update account details
   - Logout

### Activity Hierarchy
```
MainActivity (Splash)
    ├── LoginActivity
    │   └── RegisterActivity
    │
    └── DashboardActivity (Main Hub)
        ├── SubcategoryActivity
        │   └── OrdersActivity
        │       └── YourOrdersActivity
        │           └── GameOrdersActivity
        │
        ├── DepositActivity
        │   └── DepositsActivity
        │
        ├── WithdrawalActivity
        │   └── WithdrawalsActivity
        │
        ├── NotificationsActivity
        │
        └── SettingsActivity
```

## 🔒 Security Features

### Authentication & Authorization
- **Token-based Authentication** - JWT Bearer tokens for API communication
- **Secure Token Storage** - Encrypted SharedPreferences
- **Session Management** - Auto-logout on token expiration
- **Password Security** - Passwords never stored locally

### Data Protection
- **HTTPS Only** - All API calls over secure connection
- **Input Validation** - Client-side and server-side validation
- **SQL Injection Prevention** - Parameterized API queries
- **XSS Protection** - Sanitized user inputs

### API Security
- **Bearer Token** - Required for all protected endpoints
- **Request Headers** - Custom User-Agent and Accept headers
- **Connection Timeout** - Prevents indefinite hanging
- **Error Handling** - Graceful error messages without exposing internals

### File Upload Security
- **Image Validation** - Only image files accepted for deposits/profile
- **File Size Limits** - Prevents large file uploads
- **Multipart Form Data** - Secure file transmission

### App Permissions
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" 
                 android:maxSdkVersion="32" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" 
                 android:maxSdkVersion="32" />
```

### ProGuard Configuration
- **Code Obfuscation** - Enabled for release builds
- **API Protection** - Retrofit and Gson rules configured
- **Crash Prevention** - Proper keep rules for models

### Best Practices
- Portrait-only orientation (prevents data loss)
- No sensitive data in logs (production)
- Proper activity lifecycle management
- Memory leak prevention with proper cleanup

## 🚀 Performance Optimizations

### Image Loading (Glide)
- **Memory Caching** - In-memory LRU cache
- **Disk Caching** - Persistent disk cache with strategies
- **Lazy Loading** - Images loaded on demand
- **Placeholder Images** - Smooth loading experience
- **Error Handling** - Fallback images on failure
- **Transform Options** - Circular avatars, center crop

```java
Glide.with(context)
    .load(imageUrl)
    .diskCacheStrategy(DiskCacheStrategy.ALL)
    .placeholder(R.drawable.placeholder)
    .error(R.drawable.error_image)
    .into(imageView);
```

### Network Optimization
- **Connection Pooling** - OkHttp connection reuse
- **Response Caching** - HTTP cache for API responses
- **Compression** - Gzip compression support
- **Logging** - Only in debug mode
- **Timeout Management** - Prevents resource waste

### RecyclerView Optimization
- **ViewHolder Pattern** - Efficient view recycling
- **DiffUtil** - Smart list updates (where applicable)
- **Item Animations** - Smooth additions/removals
- **Fixed Size** - `setHasFixedSize(true)` when applicable
- **Nested Scrolling** - Proper NestedScrollView configuration

### Memory Management
- **Lifecycle Awareness** - Proper cleanup in `onDestroy()`
- **Leak Prevention** - Static context avoided
- **Bitmap Optimization** - Glide handles bitmap pooling
- **Background Tasks** - Cancelled on activity destroy

### Data Storage
- **SharedPreferences** - Lightweight user data storage
- **Gson Serialization** - Efficient object → JSON conversion
- **Minimal Disk I/O** - Only when necessary
- **Preference Caching** - In-memory cache for frequent reads

### UI Rendering
- **View Recycling** - RecyclerView for all lists
- **Async Operations** - API calls on background threads
- **Shimmer Effect** - Better perceived performance
- **Debouncing** - Prevents rapid duplicate API calls
- **ConstraintLayout** - Flat view hierarchy

### APK Size Optimization
- **ProGuard** - Code shrinking and optimization
- **Resource Shrinking** - Removes unused resources
- **Vector Drawables** - Scalable icons with small size
- **WebP Images** - Compressed image format
- **Split APKs** - Density and ABI splits (when needed)

## 🐛 Debugging & Logging

### Log Tags
Application uses consistent log tags for debugging:

```java
// API & Network
"ApiClient"          - Retrofit client operations
"ApiService"         - API service calls
"NetworkError"       - Network-related errors

// Authentication
"LoginActivity"      - Login operations
"RegisterActivity"   - Registration operations
"AuthToken"          - Token management

// Dashboard & Orders
"Dashboard"          - Dashboard operations
"GameCategories"     - Game category loading
"OrdersActivity"     - Order placement
"OrderValidation"    - Order input validation
"SubcategoryActivity" - Subcategory loading

// Financial
"DepositActivity"    - Deposit submissions
"WithdrawalActivity" - Withdrawal requests
"BalanceRefresh"     - Balance update operations

// User & Profile
"UserData"           - User data operations
"ProfileUpdate"      - Profile updates
"AvatarLoad"         - Avatar image loading

// Utilities
"PreferenceManager"  - SharedPreferences operations
"SnackbarUtils"      - Snackbar messages
"LoadingDialog"      - Loading dialog operations
```

### Log Levels
```java
android.util.Log.d(TAG, "Debug message");      // Debug info
android.util.Log.i(TAG, "Info message");       // Important info
android.util.Log.w(TAG, "Warning message");    // Potential issues
android.util.Log.e(TAG, "Error message");      // Errors
```

### Debug Configuration
```gradle
buildTypes {
    debug {
        debuggable true
        minifyEnabled false
        // HTTP logging enabled
    }
    release {
        debuggable false
        minifyEnabled true
        // HTTP logging disabled
    }
}
```

### Network Logging
OkHttp interceptor logs all API requests/responses in debug mode:
```
→ POST /login
Authorization: Bearer eyJ0eXAiOiJKV1Qi...
Content-Type: application/json
{"phone":"1234567890","password":"***"}

← 200 OK (245ms)
{"success":true,"message":"Login successful",...}
```

### Common Issues & Solutions

#### 1. Network Errors
```
Error: Unable to resolve host
Solution: Check internet connection and API URL
```

#### 2. Image Loading Issues
```
Error: Failed to load image
Solution: Check URL format and network connectivity
Check Glide cache with .skipMemoryCache(true)
```

#### 3. Token Expiration
```
Error: 401 Unauthorized
Solution: Token expired - user auto-redirected to login
```

#### 4. Validation Errors
```
Error: Amount must be divisible by 5
Solution: Client-side validation prevents submission
```

### Testing Tools
- **Android Debug Bridge (ADB)**: Device logs and debugging
- **Network Profiler**: Monitor API calls
- **Layout Inspector**: Debug UI issues
- **Logcat**: Real-time log monitoring

## 📋 Requirements

### Minimum Requirements
- **OS**: Android 7.0 Nougat (API 24)
- **RAM**: 2GB
- **Storage**: 50MB available space
- **Display**: 4.5" screen, 480x800 resolution
- **Internet**: Active internet connection required
- **Camera**: Optional (for profile picture upload)

### Recommended Specifications
- **OS**: Android 10.0+ (API 29+)
- **RAM**: 4GB or higher
- **Storage**: 100MB available space
- **Display**: 5.5" screen, 1080x1920 resolution
- **Internet**: Stable Wi-Fi or 4G/5G connection
- **Camera**: 8MP or higher

### Device Compatibility
- ✅ **Smartphones**: All Android smartphones (API 24+)
- ✅ **Tablets**: Optimized layouts for tablets
- ✅ **Foldables**: Responsive design adapts to foldables
- ⚠️ **Watches**: Not optimized for Wear OS
- ⚠️ **TV**: Not designed for Android TV

### Network Requirements
- Stable internet connection for API access
- Minimum 2G/3G (4G recommended)
- HTTPS support required
- No proxy restrictions

### Backend Requirements
- Laravel-based REST API
- MySQL/PostgreSQL database
- Admin panel for content management
- File storage for images (local/S3/cloud)

## 🤝 Contributing

We welcome contributions to the Moon Enterprises Android App! Please follow these guidelines:

### How to Contribute

1. **Fork the Repository**
   ```bash
   # Click "Fork" on GitHub, then clone your fork
   git clone https://github.com/YOUR_USERNAME/geo-enterprises-android-app.git
   cd geo-enterprises-android-app
   ```

2. **Create a Feature Branch**
   ```bash
   git checkout -b feature/amazing-feature
   # or
   git checkout -b fix/bug-fix
   # or
   git checkout -b docs/documentation-update
   ```

3. **Make Your Changes**
   - Follow the existing code style
   - Add comments for complex logic
   - Update documentation if needed
   - Test your changes thoroughly

4. **Commit Your Changes**
   ```bash
   git add .
   git commit -m "Add amazing feature: description of changes"
   ```
   
   **Commit Message Format:**
   - `feat:` New feature
   - `fix:` Bug fix
   - `docs:` Documentation changes
   - `style:` Code style changes (formatting)
   - `refactor:` Code refactoring
   - `test:` Adding tests
   - `chore:` Build process or tooling changes

5. **Push to Your Fork**
   ```bash
   git push origin feature/amazing-feature
   ```

6. **Open a Pull Request**
   - Go to the original repository
   - Click "New Pull Request"
   - Select your branch
   - Fill in the PR template
   - Wait for review

### Code Style Guidelines

#### Java Conventions
```java
// Class names: PascalCase
public class OrdersActivity extends AppCompatActivity { }

// Method names: camelCase
private void submitOrder() { }

// Constants: UPPER_SNAKE_CASE
private static final String BASE_URL = "https://...";

// Variables: camelCase
private ApiService apiService;
```

#### XML Conventions
```xml
<!-- Layout IDs: snake_case with prefix -->
<TextView android:id="@+id/tv_user_name" />
<Button android:id="@+id/btn_submit" />
<ImageView android:id="@+id/iv_avatar" />

<!-- Resource names: snake_case -->
<color name="primary_color">#1976D2</color>
<string name="app_name">Moon Enterprises</string>
```

### Testing Guidelines
- Test on multiple devices/emulators
- Test with different Android versions (API 24-36)
- Test with slow/no internet connection
- Test edge cases and error scenarios
- Verify all API integrations work correctly

### Areas for Contribution
- 🐛 **Bug Fixes**: Fix reported issues
- ✨ **New Features**: Add new functionality
- 📝 **Documentation**: Improve docs and comments
- 🎨 **UI/UX**: Enhance user interface
- ⚡ **Performance**: Optimize code
- 🧪 **Testing**: Add unit/integration tests
- 🌐 **Localization**: Add language support
- ♿ **Accessibility**: Improve accessibility features

### Pull Request Checklist
- [ ] Code follows project style guidelines
- [ ] Comments added for complex logic
- [ ] No compiler warnings or errors
- [ ] Tested on physical device/emulator
- [ ] Documentation updated (if needed)
- [ ] Screenshots added (for UI changes)
- [ ] No merge conflicts with main branch

### Reporting Issues
When reporting bugs, please include:
- Android version
- Device model
- Steps to reproduce
- Expected vs actual behavior
- Screenshots/logs (if applicable)
- API response (if relevant)

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Team

- **Development**: Moon Enterprises Development Team
- **Backend**: Laravel API Integration
- **UI/UX**: Material Design Implementation

## 📞 Support

For support and questions:
- Email: support@geoenterprises.com
- GitHub Issues: [Create an issue](https://github.com/yasirraheel/geo-enterprises-android-app/issues)

## 🔄 Version History

### v2.1 (Current) - March 2026
**UI Consistency and Export Reliability Release**

#### Features
- Unified top-bar styling across major routed activities to match Home.
- Improved Game Orders voucher export with stable rendering and status column support.
- Fixed dashboard game-list overflow so cards are not covered by bottom actions.

#### Technical Improvements
- Added shared top-bar design tokens and reusable layout styling.
- Switched status-bar handling to dynamic inset-based calculation for better device fit.
- Tightened export layout spacing and width handling to reduce clipping risks.

#### Bug Fixes
- Fixed hidden game items on Home screen when categories exceed visible limit.
- Fixed missing/unstable text rendering in exported game-order vouchers.
- Fixed missing row text in bulk-order PDF export (`OrdersActivity`) by switching export rows to direct canvas rendering.
- Fixed inconsistent top-bar spacing across navigation routes.

---
### v1.7 - December 2025
**Enhanced Video Guides & Validation Release**

#### Features ✨
- Enhanced video guide system with YouTube integration
- Direct video playback in fullscreen mode without status bar
- RTRTP input validation (minimum 5, divisible by 5)
- Copy account number feature in deposit requests
- Share button for sharing orders
- Complete app update and share system

#### Technical Improvements 🛠️
- Enhanced PDF export formatting
- Improved dialog responsiveness
- Centralized API URL configuration
- Better error handling and user feedback
- Optimized performance and loading times

#### Bug Fixes 🐛
- Fixed XML parsing errors in layout files
- Resolved timezone display issues
- Fixed server URL configuration
- Fixed version display in About dialog
- Improved UI navigation workflow

---

### v1.6 - December 2025
**Transactions & Configuration Update**

#### Features ✨
- Added Transactions feature with custom UI
- Subcategory timing validation
- Improved user workflow

#### Bug Fixes 🐛
- Fixed timezone display issues
- Fixed server URL configuration

---

### v1.0.0 - October 2025
**Initial Production Release**

#### Features ✨
- User authentication (login/register) with phone/email
- Dynamic game categories and subcategories
- Order placement with validation
- Order history with status filtering and PDF export
- Deposit system with payment proof upload
- Withdrawal system with bank account management
- Real-time balance tracking with manual refresh
- Notifications system with badge counter
- Profile management with avatar upload
- Navigation drawer with user info
- Dynamic app settings from admin panel

#### Technical Improvements 🛠️
- Retrofit 2.9.0 for API integration
- Glide 4.16.0 for image loading
- Custom SnackbarUtils for consistent messaging
- Loading dialogs and confirmation dialogs
- Shimmer loading effects
- Material Design components
- ProGuard configuration for release builds

#### Bug Fixes 🐛
- Fixed subcategory RecyclerView display issue
- Fixed status color mapping for "ok" status
- Fixed login hint text (phone-based)
- Fixed amount validation (min 5, divisible by 5)
- Standardized all Snackbar messages to use SnackbarUtils

#### UI/UX Enhancements 🎨
- Added balance refresh icon with animation
- Improved order status color coding
- Enhanced error handling with user-friendly messages
- Added empty states for all list views
- Optimized layouts for different screen sizes

---

### Planned Updates 🎯

#### v1.1.0 (Upcoming)
- [ ] Push notifications (FCM integration)
- [ ] Offline mode with local caching
- [ ] Dark theme support
- [ ] Multi-language support (English, Urdu, Hindi)
- [ ] Order search and filtering
- [ ] Transaction history export (Excel, CSV)
- [ ] Biometric authentication
- [ ] In-app announcements

#### v1.2.0 (Future)
- [ ] Social sharing of wins
- [ ] Referral system
- [ ] In-app chat support
- [ ] Advanced analytics dashboard
- [ ] Prize bond number checker
- [ ] Results notification system
- [ ] Payment gateway integration
- [ ] Auto-withdrawal scheduling

#### v2.0.0 (Long-term)
- [ ] Jetpack Compose migration
- [ ] Kotlin conversion
- [ ] MVVM architecture implementation
- [ ] Room database for offline support
- [ ] Coroutines for async operations
- [ ] WorkManager for background tasks
- [ ] Material You (Material Design 3)
- [ ] Widget support

## 🎯 Future Enhancements

### Short-term Goals (v1.x)
- 🔔 **Push Notifications** - FCM integration for real-time updates
- 🌙 **Dark Theme** - System-wide dark mode support
- 🌐 **Localization** - Multi-language support (Urdu, Hindi, Arabic)
- 💾 **Offline Mode** - Cache data for offline viewing
- 🔍 **Advanced Search** - Search orders by date, status, game
- 📊 **Analytics** - User behavior tracking and insights
- 🔐 **Biometric Auth** - Fingerprint/Face unlock
- 💬 **Live Chat** - Customer support integration

### Mid-term Goals (v2.x)
- 🎨 **Jetpack Compose** - Modern declarative UI framework
- 🔄 **Kotlin Migration** - Convert Java codebase to Kotlin
- 🏗️ **MVVM Architecture** - ViewModel + LiveData pattern
- 💾 **Room Database** - Local database for offline support
- ⚡ **Coroutines** - Structured concurrency for async operations
- 🔔 **WorkManager** - Background job scheduling
- 🎁 **Rewards System** - Loyalty points and referral bonuses
- 📱 **App Widgets** - Home screen widgets for balance

### Long-term Vision (v3.x)
- 🤖 **AI Assistant** - Smart recommendations and predictions
- 🔗 **Blockchain** - Transparent transaction ledger
- 📈 **Advanced Charts** - Interactive data visualization
- 🎮 **Gamification** - Achievements, leaderboards, badges
- 🌍 **Global Expansion** - Multi-currency support
- 🔐 **2FA** - Two-factor authentication
- 📲 **Progressive Web App** - Cross-platform web version
- 🖥️ **Desktop App** - Windows/macOS companion app

### Community Requested Features
- Export transaction history to Excel/CSV
- Auto-withdrawal scheduling
- Prize bond number checker
- Results notification system
- Social media integration
- Video tutorials within app
- FAQ and help center
- Rate and review system

---

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

### MIT License Summary

```
MIT License

Copyright (c) 2025 Moon Enterprises

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

### Third-Party Licenses

This project uses the following open-source libraries:
- **Retrofit** - Apache License 2.0
- **OkHttp** - Apache License 2.0
- **Gson** - Apache License 2.0
- **Glide** - BSD, MIT, and Apache 2.0 licenses
- **Material Components** - Apache License 2.0
- **AndroidX Libraries** - Apache License 2.0

---

## 👥 Team

### Development Team
- **Lead Developer**: Yasir Raheel (@yasirraheel)
- **UI/UX Design**: Moon Enterprises Design Team
- **Backend Integration**: Laravel API Team
- **Project Manager**: Moon Enterprises Management
- **QA Testing**: Quality Assurance Team

### Contributors
We appreciate all contributors to this project. See [CONTRIBUTING.md](CONTRIBUTING.md) for how to get involved.

### Acknowledgments
- Android Development Community
- Stack Overflow Contributors
- Material Design Guidelines by Google
- Open Source Library Maintainers

---

## 📞 Support & Contact

### Support Channels

#### 🐛 Bug Reports & Issues
- **GitHub Issues**: [Create an issue](https://github.com/yasirraheel/geo-enterprises-android-app/issues)
- **Email**: support@geoenterprises.com
- **Priority**: High priority for critical bugs

#### 💡 Feature Requests
- **GitHub Discussions**: [Start a discussion](https://github.com/yasirraheel/geo-enterprises-android-app/discussions)
- **Email**: features@geoenterprises.com

#### 📚 Documentation & Help
- **README**: You're reading it!
- **Wiki**: [GitHub Wiki](https://github.com/yasirraheel/geo-enterprises-android-app/wiki)
- **FAQ**: Coming soon

#### 📧 Contact Information
- **General Inquiries**: info@geoenterprises.com
- **Technical Support**: support@geoenterprises.com
- **Business Inquiries**: business@geoenterprises.com
- **Website**: https://geoenterprises.shahabtech.com

#### 🌐 Social Media
- **GitHub**: [@yasirraheel](https://github.com/yasirraheel)
- **LinkedIn**: Moon Enterprises
- **Twitter**: @geoenterprises (coming soon)

### Response Times
- **Critical Bugs**: Within 24 hours
- **General Issues**: 2-3 business days
- **Feature Requests**: Reviewed weekly
- **Pull Requests**: Reviewed within 3-5 days

---

## 📊 Project Statistics

![GitHub stars](https://img.shields.io/github/stars/yasirraheel/geo-enterprises-android-app?style=social)
![GitHub forks](https://img.shields.io/github/forks/yasirraheel/geo-enterprises-android-app?style=social)
![GitHub issues](https://img.shields.io/github/issues/yasirraheel/geo-enterprises-android-app)
![GitHub pull requests](https://img.shields.io/github/issues-pr/yasirraheel/geo-enterprises-android-app)
![GitHub last commit](https://img.shields.io/github/last-commit/yasirraheel/geo-enterprises-android-app)
![GitHub repo size](https://img.shields.io/github/repo-size/yasirraheel/geo-enterprises-android-app)

---

## 🙏 Acknowledgments

Special thanks to:
- **Android Community** for excellent documentation and support
- **Open Source Contributors** for maintaining amazing libraries
- **Stack Overflow** for solving countless development challenges
- **Google** for Material Design guidelines and Android SDK
- **Moon Enterprises Team** for project vision and support
- **Beta Testers** for valuable feedback and bug reports

---

<div align="center">

**Made with ❤️ by Moon Enterprises Development Team**

© 2025 Moon Enterprises. All Rights Reserved.

[⬆ Back to Top](#-geo-enterprises-android-app)

</div>


