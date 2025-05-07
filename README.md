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

---

## Upcoming Features  
🚀 **Bookmark & Favorites** – Save frequently used prayers.  
🚀 **Pray Now** – Navigate to the prayer to be used based on the time of the day.   
🚀 **Liturgical Calendar** – Provide users with the Liturgical Calendar.  
🚀 **Search Bar** - Implement a search bar that allows users to navigate to the screen and section of their choice with a few keywords.  
🚀 **QR code scanner** - Give a QR code that allows users to navigate to the required page.  
🚀 **Auto silent / DND mode** - Put the device to the relevant notification settings based on user preference.  
🚀 **Bible** - Read Bible and excerpts within the required pages.  

---

## 📜 Credits & Contributors  

- **[Samuel Alex Koshy]** – Development, Implementation, UI Design, and Text Translations  
- **[Shriganesh Keshrimal Purohit]** – Guidance, Structural Planning, and Development Insights  
- **[Shaun John] & [Lisa Shibu George]** – Additional Text Translations and Preparation  

🙏 **Glory to God!**  

