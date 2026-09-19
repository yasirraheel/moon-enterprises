# Backend Update Required

## Issue
Laravel doesn't properly parse `multipart/form-data` for PUT requests. File uploads only work with POST requests.

## Required Change in Laravel Backend

### Current Route (NOT WORKING for file uploads):
```php
Route::put('profile', [AuthController::class, 'updateProfile']);
```

### Change to (WORKING for file uploads):
```php
Route::post('profile', [AuthController::class, 'updateProfile']);
```

## Why?
- PUT requests with `multipart/form-data` result in empty `$request->all()` and `$request->hasFile()` returning false
- This is a known Laravel/HTTP limitation
- POST requests properly parse multipart form data including files

## What Changed in Android App
1. **SettingsActivity.java**: Changed `.put(requestBody)` to `.post(requestBody)`
2. **ApiService.java**: Changed `@PUT("profile")` to `@POST("profile")`

## After Backend Update
Once you change the Laravel route to POST, the profile update with avatar upload will work correctly.

## Alternative Solution (if you must keep PUT)
Add `_method` field with value "PUT" to the form data and use POST route:
```php
Route::post('profile', [AuthController::class, 'updateProfile']);
```
And in Android, add:
```java
builder.addFormDataPart("_method", "PUT");
```
But this is unnecessary - just use POST for both.
