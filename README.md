# Malankara Orthodox Liturgica
![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-4285F4?logo=jetpackcompose&logoColor=white)
![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin%20Multiplatform-7F52FF?logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?logo=android&logoColor=white)
![iOS](https://img.shields.io/badge/iOS-000000?logo=apple&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?logo=firebase&logoColor=white)

## Overview  

**Malankara Orthodox Liturgica** is a modern, cross-platform prayer companion built using **Kotlin Multiplatform (KMP)** and **Compose Multiplatform (CMP)**. It provides a structured collection of liturgical prayers, sacramental resources, and spiritual content for the faithful of the Malankara Orthodox Syrian Church.

The app is designed for high performance and offline-first usage, ensuring that prayers are accessible anytime, anywhere.

---

## Features  

- 📖 **Extensive Prayer Collection** – Daily Prayers, Sacramental Prayers, and seasonal content.
- 📂 **Modular Navigation** – Organized hierarchical structure for easy access to various sections.
- 🔍 **Index Based Prayer Searching** – Quick search functionality to find prayers by their index or title.
- 🔤 **Multilingual Support** – Malayalam, Manglish, and English.
- 🔍 **Customizable Reading Experience** – Adjustable font sizes and auto-landscape support for optimal legibility.
- 🕰️ **Pray Now** – Dynamic recommendations based on the liturgical time of day.
- 📖 **Bible Module** – Integrated Bible with chapter navigation and feast-based references.
- 📅 **Liturgical Calendar** – Comprehensive calendar with feasts, fasts, and daily readings.
- 🎵 **Song Player** – Integrated audio playback for liturgical hymns with caching.
- 📷 **QR Code Support** – Quick access to specific prayers and sections via QR scanning.
- ☁️ **Cloud Sync** – Efficient synchronization of liturgical content and resources via Firebase.
- 📊 **Performance & Analytics** – Anonymized monitoring using **Firebase** and **Kotzilla** to ensure a smooth experience.

---

## Architecture  

The project follows **Clean Architecture** principles and a **Modular Multi-module** structure to ensure scalability and maintainability across platforms.

- **Native Navigation**: Navigation is handled natively on both Android and iOS to provide the best platform-specific user experience.
- **Shared Logic**: Business logic, data repositories, and domain models are shared across platforms using Kotlin Multiplatform.
- **Compose Multiplatform**: The UI layer is built using Compose Multiplatform, allowing for a consistent design system while respecting platform nuances.

### Module Structure  
📂 `core/` – Foundation modules (DI, Design System, UI Common, Platform-specific abstractions)  
📂 `feature/` – Domain-specific UI features (Prayer, Bible, Calendar, Settings, Onboarding)  
📂 `data/` – Data sources and repositories (Prayer, Bible, Calendar, Settings, Sync, Translations)  
📂 `shared/` – The main KMP module that integrates all features and core components for consumption by platform apps.  
📂 `androidApp/` & `iosApp/` – Platform-specific entry points and native navigation hosting.

---

## Technical Stack  

- **Kotlin Multiplatform (KMP)** – Cross-platform code sharing.
- **Compose Multiplatform** – Shared UI framework.
- **Koin** – Dependency Injection across all modules.
- **ZXing (Android) / Native (iOS)** – High-performance QR code generation and scanning.
- **Play Core (Android)** – Integrated In-App Reviews and Update management.
- **Firebase** – Content Sync, Anonymized Analytics, and Crashlytics.
- **Kotzilla** – Performance monitoring and optimization.
- **Kotlinx Serialization** – Type-safe JSON parsing.
- **DataStore Preferences** – Multiplatform persistent settings storage.
- **Media3 (Android) / Native (iOS)** – High-performance audio handling.

---

## 🚀 Upcoming Features

- 📑 **Bookmarks & Favorites** – Save frequently used prayers for quick access.
- 🔎 **Global Search** – Search across the entire liturgical library, Bible, and calendar.
- 🎤 **Expanded Audio Support** – More recorded hymns and liturgical chants.
- 📲 **Enhanced Offline Support** – Improved caching and offline synchronization.

---

## 📜 Credits & Contributors  

- [@TheParadox543](https://github.com/TheParadox543) – Lead Developer, UI/UX Design, and Translations.  
- [@ShriGaneshPurohit](https://github.com/ShriGaneshPurohit) – Architectural Guidance and Insights.
- [@praneethm](https://github.com/praneethm05) – iOS Implementation Support.
- **Jerin M George** – Design refinement and Content selection.
- **Shaun John, Lisa Shibu George, Sabu John, Saira Susan Koshy, Sunitha Mathew, Nohan George, Anoop Alex Koshy, & Prasad Joseph Cheeran** – Content Preparation and Translations.

🙏 **Glory to God!**  
