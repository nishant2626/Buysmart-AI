# BuySmart AI - Setup Guide

This guide will help you set up the BuySmart AI Android application.

## Prerequisites

Before you begin, ensure you have the following installed:

1. **Android Studio** (Arctic Fox or later)
   - Download from: https://developer.android.com/studio

2. **Java Development Kit (JDK)** 17 or later
   - Download from: https://adoptium.net/

3. **Firebase Account**
   - Sign up at: https://firebase.google.com/

4. **Google Cloud Account** (for Maps API)
   - Sign up at: https://cloud.google.com/

## Step-by-Step Setup

### 1. Clone or Download the Project

```bash
git clone https://github.com/yourusername/buysmart-ai.git
cd buysmart-ai
```

Or download and extract the ZIP file.

### 2. Open in Android Studio

1. Launch Android Studio
2. Click "Open an existing Android Studio project"
3. Select the `BuySmartAI` folder
4. Wait for Gradle sync to complete

### 3. Firebase Configuration

#### Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Click "Add project"
3. Enter project name: "BuySmart AI"
4. Enable Google Analytics (optional)
5. Click "Create project"

#### Add Android App

1. In Firebase Console, click the Android icon to add an app
2. Enter package name: `com.buysmart.ai`
3. Enter app nickname: "BuySmart AI"
4. Click "Register app"
5. Download `google-services.json`
6. Move the file to `app/` directory in your project

#### Enable Firebase Services

1. **Authentication**:
   - Go to Authentication → Sign-in method
   - Enable "Email/Password"
   - Enable "Google" (add SHA-1 certificate)

2. **Firestore Database**:
   - Go to Firestore Database
   - Click "Create database"
   - Start in "test mode" or use the provided rules
   - Copy rules from `firestore.rules` file

3. **Storage**:
   - Go to Storage
   - Click "Get started"
   - Copy rules from `storage.rules` file

4. **Cloud Messaging**:
   - Go to Cloud Messaging
   - Note the Server Key for backend integration

### 4. Google Maps API Setup

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing
3. Enable the following APIs:
   - Maps SDK for Android
   - Places API
   - Geocoding API

4. Create API credentials:
   - Go to Credentials → Create credentials → API key
   - Restrict the key for Android apps
   - Add your app's SHA-1 fingerprint

5. Add API key to the project:
   - Open `app/src/main/res/values/strings.xml`
   - Replace `YOUR_GOOGLE_MAPS_API_KEY` with your actual API key

### 5. Build Configuration

#### Update Build Files (if needed)

The project uses:
- compileSdk: 34
- minSdk: 24
- targetSdk: 34
- Java 17

These are already configured in `app/build.gradle`.

#### Sync Project

1. In Android Studio, click "Sync Now" in the notification bar
2. Wait for sync to complete
3. Resolve any dependency issues

### 6. Run the App

#### On Emulator

1. Create a new virtual device:
   - Tools → AVD Manager → Create Virtual Device
   - Select Pixel 4 or similar
   - Download Android 14 (API 34) system image

2. Run the app:
   - Click the green play button
   - Select your emulator

#### On Physical Device

1. Enable Developer Options on your device
2. Enable USB Debugging
3. Connect device via USB
4. Click the green play button
5. Select your device

### 7. Testing

#### Authentication Test
- Register with email/password
- Login with email/password
- Login with Google
- Reset password

#### Core Features Test
- Search for products
- Compare prices
- Add to cart
- Set price alerts
- View price history
- Find nearby stores

#### Push Notifications Test
- Set a price alert
- Wait for price drop notification
- Check notification delivery

## Troubleshooting

### Common Issues

#### 1. Gradle Sync Failed
```
Solution: Check internet connection and retry
File → Invalidate Caches / Restart
```

#### 2. Firebase Connection Error
```
Solution: Verify google-services.json is in correct location
Check package name matches Firebase configuration
```

#### 3. Maps Not Loading
```
Solution: Verify API key is correct
Check if Maps SDK is enabled in Google Cloud Console
Verify SHA-1 fingerprint is added
```

#### 4. Build Errors
```
Solution: Update Android Studio to latest version
Update Gradle plugin
Clean and rebuild project: Build → Clean Project → Rebuild Project
```

### Getting Help

If you encounter issues:

1. Check the [Firebase Documentation](https://firebase.google.com/docs)
2. Check the [Android Developer Documentation](https://developer.android.com/docs)
3. Search for similar issues on Stack Overflow
4. Create an issue in the project repository

## Next Steps

After successful setup:

1. **Customize the app**:
   - Update app name and branding
   - Modify colors in `colors.xml`
   - Update API endpoints

2. **Add more features**:
   - Integrate with real product APIs
   - Add more e-commerce platforms
   - Implement advanced analytics

3. **Deploy to Play Store**:
   - Generate signed APK
   - Create Play Store listing
   - Upload and publish

## Configuration Files

### Key Files to Update

1. `app/google-services.json` - Firebase configuration
2. `app/src/main/res/values/strings.xml` - API keys and strings
3. `firestore.rules` - Database security rules
4. `storage.rules` - Storage security rules

### Environment Variables

For production, consider using:
- `local.properties` for sensitive data
- Build variants for different environments
- Firebase Remote Config for dynamic values

---

For more information, refer to the main [README.md](README.md) file.
