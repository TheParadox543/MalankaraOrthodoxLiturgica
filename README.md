# Malankara Orthodox Church Prayer App
![Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![Python](https://img.shields.io/badge/Python-3776AB?logo=python&logoColor=white)

## Overview  

This mobile app provides a structured collection of prayers from the Malankara Orthodox Syrian Church. It includes daily prayers and sacramental prayers, with support for multiple languages and customizable font sizes.  
Version **2.0** introduces a major architectural overhaul, improved performance, and significant feature expansions.

---

## Features  

- 📖 **Extensive Prayer Collection** – Daily Prayers, Sacramental Prayers, seasonal content, and more.
- 📂 **Hierarchical Section Navigation** – Organized using a static node-based navigation tree.
- 🔤 **Multilingual Support** – **English, Malayalam, Manglish**.
- 🔍 **Adjustable Font Size** – Five levels of text scaling.
- ↔️ **Auto Landscape Mode** for large text.
- 🕰️ **Pray Now** – Recommends prayers based on time of day.
- 📖 **Bible Module** – Book/chapter navigation plus feast-based references.
- 📅 **Liturgical Calendar** – Includes feasts, fasts, and readings.
- 🎵 **Song Player** – Audio playback with caching and controls.
- 📷 **QR Code Support** – Scan QR to jump directly to sections.
- 📊 **Analytics Tracking** – Screen usage and error insight for improvements.
- 🔕 **Auto Silent/DND feature** - Automatically trigger based on user preference.
- ⭐ **In-App Review Prompts** – Smartly triggered based on meaningful usage.

---

## Technical Details  

### Architecture  
![Architecture](https://img.shields.io/badge/Clean%20Architecture-6A1B9A?logo=diagram&logoColor=white)

- **Clean Architecture** – Domain, data, and UI layers clearly separated.
- **MVVM** – ViewModels handle logic and UI state.
- **Jetpack Compose** – Declarative UI for improved maintainability.
- **Hilt DI** – Dependency injection for ViewModels, services, and repositories.
- **Navigation Compose** – Deep links, nested graphs, dynamic routing.

### Key Components  
📂 `domain/` – Core business logic, models, and use cases  
📂 `data/` – Bible loader, calendar loader, prayer repositories, JSON parsing  
📂 `ui/` – Screens, components, navigation, theming  
📂 `services/` – Platform-specific operations (analytics, review, sharing, sound mode, etc.)  

📂 Screens Included  
- `PrayerScreen.kt` – Displays prayers, handles navigation and text adjustments.  
- `SettingsScreen.kt` – Allows users to modify language and font size preferences.  
- `DataStoreManager.kt` – Manages persistent settings storage.  
- `PrayerViewModel.kt` – Handles business logic and data fetching.  
- `NavViewModel.kt` – Manages section navigation and sequential navigation logic.  
- `NodeTree.kt` – Represents the prayer structure and routes as a static tree within the app.

### 📦 Core Dependencies  
![Compose](https://img.shields.io/badge/Compose-4285F4?logo=jetpackcompose&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android%20SDK-3DDC84?logo=android&logoColor=white)

- **Jetpack Compose** – For building declarative UIs.  
- **Navigation Compose** – For in-app navigation with argument passing.  
- **Hilt** – For dependency injection and ViewModel management.  
- **DataStore Preferences** – To persist user settings like language and font size.  
- **JSON** – For parsing JSON-based prayer content.  
- **Accompanist System UI Controller** – For status bar and navigation bar customization.  
- **Material Icons Extended** – For Compose-ready icon assets.  
- **Lifecycle ViewModel Compose** – To integrate ViewModels cleanly with Compose screens.
- **ExoPlayer** – For high-performance audio playback and media handling.
- **ZXing** – For QR code generation and scanning.
- **Firebase Analytics** – For anonymized screen usage and error tracking.
- **Play Core In-App Review** – For triggering native review prompts inside the app.


---

## 🚀 Upcoming Features

- 📑 **Bookmarks & Favorites**
- 🔎 **Global Search** across prayers, Bible, and feasts
- 🎤 **Expanded Audio Support**
- 🧭 **Navigation 3 Migration** (Compose Multiplatform–friendly)
- 🍎 **iOS Expansion** via Kotlin Multiplatform + Compose Multiplatform
- 📲 **Potential Cloud Sync** (optional future feature)

---

## 📜 Credits & Contributors  

- [@TheParadox543](https://github.com/TheParadox543) – Development, Implementation, UI Design, and Text Translations.  
- [@ShriGaneshPurohit](https://github.com/ShriGaneshPurohit) – Guidance, Structural Planning, and Development Insights.
- **Jerin M George** – Assistance with Color Theme Fixes and Image Selection.  
- **Shaun John, Lisa Shibu George, Sabu John, Saira Susan Koshy, Sunitha Mathew, Nohan George & Anoop Alex Koshy** – Additional Text Translations, Content and Preparation.  

🙏 **Glory to God!**  
