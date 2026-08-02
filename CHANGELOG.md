# 📜 CHANGELOG

All notable changes to **Malankara Orthodox Liturgica** will be documented in this file.  
This project follows the principles of [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

---

## 🔮 [Unreleased]

- Unversioned changes

### Feature 
- Offline sync: App updates content when it is online instead of depending on app updates alone.
- Logging for search functionality in Index page

### Fix
- Dynamic Songs fixed to show actual live data instead of only default values.

### Feature

- Better UI for calendar.
- Added legend for calendar.
- Bold and Italics formatting for prose and song blocks.
- Improved onboarding flow: Divided different features into stages.
- Added dynamic image loading in prayer screen.
- Top prayer from pray now can be seen in home screen.
- Auto scroll to current date.
- Add scrollbar for calendar.
- Index for prayers: Prayers can be searched and ordered by Title.

### Fix
- Wrong anniversary count given in calendar.

### Change
- Move logger to dedicated module.


---

## [2.4.1] - 15/07/2026

### Added

- Prayers in sheema namaskaram (promiyon and sedra for each of the weekdays)

---

## [2.3.2] - 11/04/2026

### Added

- Calendar data for 2026 April and May

---

## [2.3.0] - Undecided

### Added

- New Hindi Songs
- Use Kotzilla monitoring

### Fixed

- Share bottom sheet caused the app to freeze and not respond fixed.
- Thread blocking behaviour fixed.
- Hosanna reference in calendar.

### Change

- Improve translation loading.

### Refactor

- Refactor to use single AppScaffold instead of overlapping scaffold.
- Refactored screens usage of ViewModels.
- Use koin for dependency injection instead of dagger/hilt dependency injection.
- Refactor modules to KMP modules for multiplatform support.
- QR generation in KMP module.

---

## [2.2.4] - 2026-03-16

### Fix

- Navigating to another chapter in Bible caused the app to crash

### Refactor

- Moved all prayer screens and ViewModels to :feature:prayer
- Moved all settings screens and ViewModels to :feature:settings

---

## [2.2.3] - 2026-03-15

### Added

- Promyion prayers in great lent for two weeks.
- QR module

### Fix

- Fixed pinch zoom logic which was broken before.
- Updated some corrections in prayers.
- Updated location of assets.
- Screens no longer have their own scaffolds, instead make use of AppScaffold used by navigation.

### Refactor

- Refactored platform module, features use interfaces for platform services.
- Refactored UI components to make them more reusable.
- Refactored navController usage so that screens no longer directly call navigation properties.
- Move UI theme, typography, colors to :core:ui.
- Move QR generation and decoding to :qr.

---

## [2.2.2] - 2026-02-22

### Added

- Pray now logic for Great Lent.

### Changed

- Updated the pray now logic so that it has more flexible boundaries.
- Write tests using `kotlin-test` in anticipation of multiplatform migration.

### Fixed

- Prayer corrections in Great Lent prayers.
- Fixed issue where next, prev, qr buttons were not loading when they were not supposed to.
- Fixed issue where qr was not showing when sibling buttons were deactivated.

### Refactor

- Refactored domain module to a separate kotlin module.
- Refactored models, repository interfaces, use cases and a few exceptions to domain module.
- Refactored data layer of following modules:
    - bible to :data:bible
    - calendar to :data:calendar
    - settings to :data:settings
    - prayer to :data:prayer
    - translations to :data:translations
- Created :data:core module for common dependencies.

### Test

- Added unit tests for domain module, for models and usecases.
- Added unit tests for :data:bible and :data:calendar.

---

## [2.2.1] - 2026-02-15

### Fixed

- Updated a few spellings within the app

### Changed

- Corrected locations of certain prayers

---

## [2.2.0] - 2026-02-10

### Added

- Prayers for Great lent, Passion Week in Malayalam, Manglish and Indic.
- New image for Great Lent.

---

## [2.1.1] - 2026-01-20

### Fixed

- Manglish translations of Nineveh Lent not getting displayed.

---

## [2.1.0] - 2026-01-19

### Added

- Nineveh Lent Prayers.

### Changed

- UI for calendar updated to show the current date better.

### Fixed

- Font size was not getting initialized properly due to unused job debounce.

---

## [2.0.1] - 2026-01-02

### Added

- Calendar data for up to April 2026.

---

## 🚀 [2.0.0] — 2025-12-12

### ✨ Added

- New **Sound Mode Manager** with clean internal → external mode mapping.
- **Song Player** powered by ExoPlayer.
- **Firebase Storage** for handling file downloads.
- New service layer abstractions:
    - `AnalyticsService`
    - `ShareService`
    - `InAppReviewManager`
    - `SoundModeService`

### 🔧 Changed

- **Complete architecture refactor** into Clean Architecture layers:
    - `domain/` – use cases, pure models
    - `data/` – repositories, JSON access
    - `ui/` – composables, navigation, screen state
    - `services/` – Android OS integrations
- More stable ViewModel scoping across screens.
- Improved startup flow and screen lifecycle handling.
- Rewrote navigation graph to avoid duplicate ViewModels and unexpected resets.
- Centralized screen-visit logging for analytics.
- Improved Bible preface loading system (cleaned & moved to domain use cases).

### 🐞 Fixed

- Sound mode setting preferences without respecting device settings.
- Sound mode not restoring correctly when leaving the app.
- Navigation stack inconsistencies caused by improper pops.

### 🗑️ Removed

- Old navigation logic and deprecated helper methods.
- Outdated utility functions scattered across UI and data layers.

---

## 🕊️ Versions Prior to **2.0.0**

Releases before **v2.0.0** were not formally documented.  
Major rewrite and restructuring began with version **2.0.0**.
