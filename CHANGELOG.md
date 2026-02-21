# 📜 CHANGELOG

All notable changes to **Malankara Orthodox Liturgica** will be documented in this file.  
This project follows the principles of [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

---

## 🔮 [Unreleased]

- Unversioned changes

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
