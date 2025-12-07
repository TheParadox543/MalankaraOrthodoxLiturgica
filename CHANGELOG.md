# 📜 CHANGELOG

All notable changes to **Malankara Orthodox Liturgica** will be documented in this file.  
This project follows the principles of [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

---

## 🔮 [Unreleased]

_Add new sections here as future changes are completed._

---

## 🚀 [2.0.0] — 2025-12-XX

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
