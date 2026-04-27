# Changelog

## [2.0-final] - 2026-04-27

### Added
- **Clean Architecture Foundation**: Established Domain layer with pure models, repository interfaces, and mappers to decouple business logic from framework details.
- **Cloud Integration**: Integrated Firebase Authentication and Firestore with real-time synchronization capabilities.
- **Real-time Services**: Implemented a Foreground Service for instant announcement delivery and background workers for automated data sync.
- **Persistent Dark Mode**: Comprehensive dark theme support across the entire app, including Calendar, Task Manager, Settings, and Department views.
- **Reusable UI Components**: Added `EmptyState` and `ErrorMessage` components to improve UX consistency across Admin and Student flows.
- **Data Persistence**: Implemented `DataStore` for lifecycle-aware user preferences and Room for local caching.

### Changed
- **Architecture Migration**: Refactored all UI screens and ViewModels to observe StateFlows and interact with Domain-level use cases.
- **UI Refinement**: Polished the Announcement module CRUD interface and modernized the Settings screen using Material 3 components.
- **Navigation**: Integrated a theme-aware Bottom Navigation Bar across all primary modules.
- **State Management**: Migrated logic to use ViewModel Factories and updated repositories to support offline-first synchronization.

### Fixed
- **Authentication UX**: Resolved missing loading indicators and fixed state hanging when Google Sign-In is cancelled.
- **Notification Logic**: Fixed duplicate/admin notification triggers and corrected real-time synchronization errors.
- **Stability**: Resolved migration-related crashes and fixed calendar display issues in the Dashboard.
- **Admin Tools**: Corrected CRUD behavior for announcement management and improved admin login transitions.
