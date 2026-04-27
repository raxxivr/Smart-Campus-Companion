# Smart Campus Companion

## App Description
**App Name:** Smart Campus Companion

The Smart Campus Companion is a mobile application designed to assist university students by providing campus-related features such as secure authentication, a personalized dashboard, task management, and real-time campus announcements.

---

## 1. Architecture

### Overview
Smart Campus Companion is built using **Clean Architecture** principles combined with the **MVVM (Model-View-ViewModel)** pattern. This ensures a strict separation of concerns, making the app scalable, testable, and maintainable.

```
┌───────────────────────────────────────────────────┐
│                 Presentation Layer                │
│ (UI: Jetpack Compose | ViewModels: StateFlow)     │
│ Screens: Dashboard, Announcements, Tasks, etc.    │
└────────────────────────┬──────────────────────────┘
                         │
┌────────────────────────▼──────────────────────────┐
│                   Domain Layer                    │
│ (Business Logic: Use Cases | Repository Interfaces)│
│ Entities: User, Task, Announcement                │
└────────────────────────┬──────────────────────────┘
                         │
┌────────────────────────▼──────────────────────────┐
│                    Data Layer                     │
│ (Implementation: Repositories | Mappers | DAOs)   │
│ Sources: Room DB (Local) | Firebase (Remote)      │
└───────────────────────────────────────────────────┘
```

### Key Components

**Presentation (UI Layer)**
- **Jetpack Compose**: Modern, declarative UI toolkit.
- **Navigation Compose**: Handles all screen transitions via a single `MainActivity` host.
- **ViewModels**: Manage UI state using `StateFlow` and interact only with the Domain layer.

**Domain Layer**
- **Entities**: Pure data models independent of any database or network framework.
- **Use Cases**: Encapsulate specific business rules (e.g., `GetTasksUseCase`).
- **Repository Interfaces**: Define the contract for data operations.

**Data Layer**
- **Repositories**: Concrete implementations that decide between local (Room) or remote (Firebase) data.
- **Mappers**: Transform Data Entities (DB/Network) into Domain Models.
- **Room Database**: Local "Single Source of Truth" for offline functionality.
- **Firebase Firestore**: Remote storage with real-time sync for announcements and user profiles.
- **DataStore**: Modern alternative to SharedPreferences for persisting user preferences (Dark Mode, Notifications).

---

## 2. Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose (Material 3) |
| Architecture | Clean Architecture + MVVM |
| Dependency Injection| ViewModel Factories |
| Local Database | Room (Offline-first) |
| Cloud Storage | Firebase Firestore |
| Authentication | Firebase Auth + Google Sign-In |
| Background Work | WorkManager & Foreground Services |
| Preferences | Jetpack DataStore |
| Image Loading | Painter Resources |

**Metadata**
- **Version:** 2.0.0 (Final)
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 35 (Android 15)

---

## 3. Git Workflow

### Branch Strategy
 
The project uses a simplified Gitflow model:

Example:
```
main
 └── develop
      ├── feature/login-ui
      ├── feature/dashboard
      ├── feature/campus-info
      └── feature/announcements
```
 
**`main`** — Protected. Only receives merges from `develop` after review. No direct commits allowed.
 
**`develop`** — Active integration branch. All feature branches are merged here via Pull Requests.
 
**`feature/*`** — Short-lived branches for individual features or fixes. Named using the convention: `feature/<short-description>` (e.g., `feature/login-ui`, `feature/task-manager`) or `bugfix/<short-description>` for bug fixes.
 
### Commit Messages
 
The development team follows a standard of providing meaningful and understandable commit messages. Every commit includes a clear and descriptive summary that explains the purpose and context of the changes rather than just listing modified files.

This practice ensures a transparent project history, simplifies the peer review process, and makes the codebase easier to navigate and maintain for all team members.
### Pull Request Process
 
1. Developer creates a feature branch from `develop`.
2. Code is written, tested locally on an emulator.
3. A Pull Request is opened targeting `develop`.
4. The Team Leader reviews and approves before merging.
### Merge Conflict Resolution
 
When a merge conflict occurs:
1. The conflict is resolved locally by examining both versions.
2. The app is tested after resolution.
3. The resolved merge is committed
### Version Tags
 
- `v1.0-midterm` — Stable release at the end of Midterm period.
- `v2.0-final` — Final release submitted for the Finals period.
---

## 4. Contribution Report

## Task Distribution

| Member | Role | Responsibilities |
|--------|------|------------------|
| **Rainier Ibo** | Cloud Integration | • Firebase Integration: Set up Firebase Firestore and Authentication infrastructure.<br>• Repository Migration: Migrated User, Task, and Announcement repositories from local-only to cloud-synced.<br>• Data Synchronization: Implemented an Offline-first approach using Room as a local cache for cloud data.<br>• Architecture: Refactored the entire project into Clean Architecture layers (Domain, Data, and Presentation). |
| **Clark Hilotin** | Authentication & User Roles | • Google Sign-In: Implemented the "Continue with Google" feature for both Login and Registration screens.<br>• User Role Logic: Updated User entities and database logic to support role-based access (STUDENT vs. ADMIN).<br>• Admin Dashboard: Developed a dedicated Admin interface focused exclusively on announcement management (CRUD tools).<br>• Navigation Guard: Secured the navigation graph to prevent students from accessing administrative screens. |
| **Althea Hapa** | System Settings | • Dark Mode: Integrated SettingsViewModel state with the SmartCampusCompanionTheme for real-time theme switching.<br>• Preferences Persistence: Implemented Jetpack DataStore to persist user theme and notification preferences.<br>• UI Polish: Refined the SettingsScreen to include profile editing and account management features. |
| **June Aryll Genotiva** | UI/UX | • Empty States: Created the reusable EmptyStateComponent for task and announcement lists.<br>• Error Handling: Implemented standardized components for ErrorMessages (Snackbars/Dialogs) to handle network and authentication failures.<br>• Branding Polish: Ensured design consistency (fonts, colors, and button styles) across both Admin and Student flows. |
| **Yale Kenneth Iligan** | Real-time Services & Notifications | • Foreground Service: Implemented a Foreground Service to maintain real-time awareness for new campus announcements.<br>• WorkManager: Integrated WorkManager to handle periodic data syncing and background cleanup tasks.<br>• Real-time Notifications: Developed a system-level notification trigger for new announcements using categorized Notification Channels. |

---

## Features Implemented (Final Build)

- **Offline-First Caching:** All modules (Tasks/Announcements) work without internet by using Room as the single source of truth.
- **Real-time Sync:** Instant cloud updates for Announcements delivered via Foreground Services.
- **Google Authentication:** Integrated secure Google Sign-In alongside traditional email/password auth.
- **Dark Mode Support:** Comprehensive Material 3 theme that respects both system settings and manual user toggles.
- **Task Management:** Full CRUD capability with progress tracking, due-date picking, and category filtering.
- **Admin Portal:** Dedicated management environment for administrators to post and update campus news.
