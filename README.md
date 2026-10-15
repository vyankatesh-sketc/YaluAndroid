# Yalu India — Native Android Mobile Application

> **Package Name:** `com.yalu.india`  
> **Minimum SDK:** API 26 (Android 8.0 Oreo)  
> **Target SDK:** API 34 (Android 14) / Compile SDK 34  
> **UI Toolkit:** Jetpack Compose + Material 3 (Material You)  
> **Language:** Kotlin 1.9.24+  
> **Architecture:** Modern MVVM with Kotlin Coroutines & StateFlow  
> **Networking:** Retrofit 2.11 + OkHttp 4.12 + Gson  
> **Image Loading:** Coil 2.6.0 Compose  
> **Security:** AndroidX Security Crypto (`EncryptedSharedPreferences`)  
> **Base API URL:** `https://www.yalu.in/api/v1/`  

---

## 1. Project Overview & Architecture

The **Yalu India Native Android App** is a production-grade mobile application built to serve India's premier classifieds marketplace, local services, and transit booking platform. The Android application is completely native, decoupled from the web frontend, and communicates cleanly through verified REST API contracts.

### Architecture Highlights
- **MVVM Architecture:** Clean separation of UI (`Composable` screens), ViewModels (`StateFlow`), and Repositories (`Dispatchers.IO`).
- **Reactive UI:** 100% declarative UI built with Jetpack Compose and Material 3 design tokens.
- **Resilient Networking:** Custom `AuthInterceptor` automatically attaches Bearer tokens; sealed `NetworkResult<T>` handles loading, success, and error states gracefully with offline preview fallbacks.
- **Secure Token Storage:** Auth tokens and user credentials are encrypted using Android Keystore with `AES256-GCM` via `EncryptedSharedPreferences`.
- **Deep Linking:** Configured to handle web links from `https://www.yalu.in/ad/{slug}` and custom `yalu://listing` URIs.

---

## 2. Directory & Package Structure

```text
YaluAndroid/
├── .gitignore
├── README.md
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
├── gradle/wrapper/
│   └── gradle-wrapper.properties
└── app/
    ├── build.gradle.kts
    ├── proguard-rules.pro
    └── src/
        ├── main/
        │   ├── AndroidManifest.xml
        │   ├── java/com/yalu/india/
        │   │   ├── YaluApp.kt                     # Application context & Service locator
        │   │   ├── MainActivity.kt                # Single activity with edge-to-edge Compose
        │   │   ├── core/
        │   │   │   ├── Constants.kt               # Base URLs, preference keys, timeouts
        │   │   │   ├── NetworkResult.kt           # Sealed Result interface (Success/Error/Loading)
        │   │   │   └── TokenManager.kt            # EncryptedSharedPreferences token storage
        │   │   ├── network/
        │   │   │   ├── AuthInterceptor.kt        # Bearer token & JSON headers interceptor
        │   │   │   ├── RetrofitClient.kt          # OkHttp & Retrofit factory
        │   │   │   ├── YaluApiService.kt          # Verified Retrofit API endpoints
        │   │   │   └── models/
        │   │   │       ├── AuthModels.kt          # Login, Register, Profile, User DTOs
        │   │   │       ├── CategoryModels.kt      # Category & Subcategory models
        │   │   │       ├── AdModels.kt            # Ad summary, detail, seller, city models
        │   │   │       └── CommonModels.kt        # ApiResponse<T> & PaginationMeta
        │   │   ├── data/
        │   │   │   ├── repository/
        │   │   │   │   ├── AuthRepository.kt      # Login, Register, Profile, Logout
        │   │   │   │   └── AdRepository.kt        # Ads, Categories, Cities, Favorites
        │   │   │   └── sample/
        │   │   │       └── SampleData.kt          # Labeled offline sample data for UI previews
        │   │   ├── ui/
        │   │   │   ├── theme/
        │   │   │   │   ├── Color.kt               # Yalu Navy (#1D2363), Yalu Red (#F70B00)
        │   │   │   │   ├── Shape.kt               # Rounded corner tokens
        │   │   │   │   ├── Type.kt                # Material 3 Typography scale
        │   │   │   │   └── Theme.kt               # YaluTheme Material 3 composable
        │   │   │   ├── components/
        │   │   │   │   ├── CommonTopBar.kt        # Branded top bar with City selector
        │   │   │   │   ├── BottomNavigationBar.kt # 4 primary navigation destinations
        │   │   │   │   ├── AdCard.kt              # Marketplace item card with Coil image
        │   │   │   │   ├── CategoryItem.kt        # Grid / circular category icons
        │   │   │   │   ├── CitySelectionSheet.kt  # City picker dialog
        │   │   │   │   ├── LoadingView.kt         # Spinner loader
        │   │   │   │   └── ErrorStateView.kt      # Network error & empty state view with retry
        │   │   │   ├── navigation/
        │   │   │   │   ├── NavRoutes.kt           # Type-safe navigation routes
        │   │   │   │   └── YaluNavHost.kt         # Jetpack Compose Navigation Graph
        │   │   │   └── screens/
        │   │   │       ├── home/                  # HomeScreen & HomeViewModel
        │   │   │       ├── categories/            # CategoriesScreen & CategoriesViewModel
        │   │   │       ├── details/               # AdDetailScreen & AdDetailViewModel
        │   │   │       ├── search/                # SearchScreen & SearchViewModel
        │   │   │       ├── auth/                  # AuthScreen (Login/Register) & AuthViewModel
        │   │   │       ├── favorites/             # FavoritesScreen & FavoritesViewModel
        │   │   │       └── profile/               # ProfileScreen & ProfileViewModel
        │   └── res/
        │       ├── drawable/                      # Vector logo & launcher icons
        │       ├── values/                        # colors.xml, strings.xml, themes.xml
        │       └── xml/                           # network_security_config.xml, backup_rules.xml
        └── test/java/com/yalu/india/
            ├── AuthRepositoryTest.kt              # Repository unit test
            ├── AdRepositoryTest.kt                # Ad repository unit test
            └── HomeViewModelTest.kt               # ViewModel state flow test
```

---

## 3. Verified Backend API Integration

The application integrates with the following REST API endpoints confirmed in the backend source (`routes/api.php`, `AuthController.php`, `AdApiController.php`, `CategoryController.php`):

| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/auth/login` | Email or phone login returning Sanctum Bearer token | No |
| `POST` | `/api/v1/auth/register` | New user registration | No |
| `GET` | `/api/v1/auth/profile` | Fetches active user profile and listing stats | Yes (Bearer) |
| `POST` | `/api/v1/auth/profile` | Updates user display name, phone, WhatsApp number | Yes (Bearer) |
| `POST` | `/api/v1/auth/logout` | Revokes current Sanctum token | Yes (Bearer) |
| `GET` | `/api/v1/categories` | Returns categories with nested subcategories | No |
| `GET` | `/api/v1/cities` | Returns top Indian cities (Hyderabad, Bangalore, etc.) | No |
| `GET` | `/api/v1/ads` | Paginated search (keywords, category_id, city, sort) | No |
| `GET` | `/api/v1/ads/{id}` | Detailed ad view with image gallery & seller details | No |
| `GET` | `/api/v1/user/ads` | User's active/pending listings | Yes (Bearer) |
| `POST` | `/api/v1/ads/{id}/favorite` | Bookmark or unbookmark listing | Yes (Bearer) |
| `GET` | `/api/v1/user/favorites` | List of user's saved favorites | Yes (Bearer) |

---

## 4. How to Open and Run in Android Studio

1. **Launch Android Studio** (Hedgehog, Iguana, Jellyfish, Koala, or Ladybug).
2. Select **File > Open…**
3. Navigate to and select the `YaluAndroid` folder:
   ```text
   d:\ANDROID_DEVELOPER_HANDOFF\YaluAndroid
   ```
4. Click **OK**. Android Studio will automatically detect the Gradle files, download the dependencies, and sync the project.
5. Select an Android Virtual Device (AVD with API 26+) or connect a physical device via USB Debugging.
6. Click the green **Run ▶** button (or press `Shift + F10`).

---

## 5. How to Build APK via Command Line

Open a terminal inside `YaluAndroid`:

### 5.1 Run Unit Tests
```bash
./gradlew test
# Windows PowerShell / CMD:
.\gradlew.bat test
```

### 5.2 Build Debug APK
```bash
./gradlew assembleDebug
# Windows PowerShell / CMD:
.\gradlew.bat assembleDebug
```
The generated APK will be available at:
`app/build/outputs/apk/debug/app-debug.apk`

### 5.3 Build Release APK
```bash
./gradlew assembleRelease
# Windows PowerShell / CMD:
.\gradlew.bat assembleRelease
```
The output APK will be placed in `app/build/outputs/apk/release/app-release-unsigned.apk`.

---

## 6. Security & Credentials Standards

- **Secure Storage:** Tokens are never stored in plaintext `SharedPreferences`. `EncryptedSharedPreferences` encrypts all access tokens using the device's hardware-backed Android Keystore.
- **Cloud Backup Exclusion:** `backup_rules.xml` explicitly excludes `yalu_secure_prefs.xml` so tokens are not exposed during Google Cloud Backup or device transfers.
- **HTTPS Enforcement:** `network_security_config.xml` mandates HTTPS for `yalu.in` and `www.yalu.in`.
- **Zero Hardcoded Secrets:** No API secret keys, database credentials, or server passwords exist in the application code.

---

## 7. Parity & Backend Notes

- **Metro Ticketing & Payments:** The backend defines Metro routes (`/api/v1/metro/*`), but online payment initiation requires live Razorpay / UPI gateway keys configured in the production environment. In this version, the app surfaces a travel shortcut banner that seamlessly transitions users to verified routes.
- **Offline Resilience:** If network connectivity is lost or backend endpoints return 500/timeout, the app gracefully falls back to cached responses or labeled sample data so that testers can preview all screens and UI flows without interruption.
