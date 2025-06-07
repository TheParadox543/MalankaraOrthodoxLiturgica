# Malankara Orthodox Church Prayer App

## Overview  

This mobile app provides a structured collection of prayers from the Malankara Orthodox Syrian Church. It includes daily prayers and sacramental prayers, with support for multiple languages and customizable font sizes.  

---

## Features  

- 📖 **Prayer Collection** – Includes Daily Prayers and Sacramental Prayers.  
- 📂 **Categorized Sections** – Prayers are structured by themes and occasions.  
- 🔤 **Multilingual Support** – Users can choose between **English, Malayalam, and Manglish**.  
- 🔍 **Adjustable Font Size** – Choose from five font sizes for better readability.  
- 📜 **Scrollable & Navigable** – Easily navigate between prayer sections with next/previous buttons.  
- 📱 **Landscape Mode for Larger Text** – If the font size is large, the screen switches to landscape mode for better visibility.  
- 🕰️ **Pray Now** - Context-aware feature that navigates directly to the appropriate prayer based on the current time of day.  
- 📖 **In-App Bible** - Read Bible passages and excerpts within the app, alongside prayers where relevant.

---

## Technical Details  

### Architecture  

- **MVVM (Model-View-ViewModel)** – Organizes code for better maintainability.  
- **Jetpack Compose** – Fully declarative UI implementation.  
- **DataStore** – Stores language and font size preferences persistently.  

### Key Components  

📂 `PrayerScreen.kt` – Displays prayers, handles navigation and text adjustments.  
📂 `SettingsScreen.kt` – Allows users to modify language and font size preferences.  
📂 `DataStoreManager.kt` – Manages persistent settings storage.  
📂 `PrayerViewModel.kt` – Handles business logic and data fetching.  
📂 `NavViewModel.kt` – Manages section navigation and sequential navigation logic.  
📂 `NodeTree.kt` – Represents the prayer structure and routes as a static tree within the app.

### 📦 Core Dependencies  

- **Jetpack Compose** – For building declarative UIs.  
- **Navigation Compose** – For in-app navigation with argument passing.  
- **Hilt** – For dependency injection and ViewModel management.  
- **DataStore Preferences** – To persist user settings like language and font size.  
- **Gson** – For parsing JSON-based prayer content.  
- **Accompanist System UI Controller** – For status bar and navigation bar customization.  
- **Material Icons Extended** – For Compose-ready icon assets.  
- **Lifecycle ViewModel Compose** – To integrate ViewModels cleanly with Compose screens.

---

## 🚀 Upcoming Features

- 📑 **Bookmarks & Favorites**  
  Quickly access and save frequently used prayers for easy reference.

- 📅 **Liturgical Calendar Integration**  
  View the Malankara Orthodox Church's liturgical calendar for feasts, fasts, and special occasions.

- 🔍 **Search Bar**  
  Search for prayers or sections by keywords and instantly jump to them within the app.

- 📷 **QR Code Scanner**  
  Scan QR codes (from church displays or documents) to instantly navigate to a specific prayer or section within the app.

- 📵 **Auto Silent / DND Mode**  
  Automatically set the phone to silent or Do Not Disturb during prayers, based on user preferences.

---

## 📜 Credits & Contributors  

- **[Samuel Alex Koshy]** – Development, Implementation, UI Design, and Text Translations  
- **[Shriganesh Keshrimal Purohit]** – Guidance, Structural Planning, and Development Insights  
- **[Shaun John], [Lisa Shibu George] & [Sabu John]** – Additional Text Translations. Content and Preparation  

🙏 **Glory to God!**  
