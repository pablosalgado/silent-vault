# Silent Vault

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
![CI](https://github.com/pablosalgado/silent-vault/actions/workflows/ci.yml/badge.svg)

Silent Vault is an Android application designed to reduce distractions by capturing, silencing, and storing all incoming notifications in a local database. It provides a clean, focused UI for reviewing your notifications at your own convenience.

## Features

- **Notification Silencing**: Automatically captures and silences incoming notifications.
- **Local Storage**: Stores notification data (app name, title, text, timestamp) securely in a local Room database.
- **Persistent Status**: A low-priority persistent notification shows you exactly how many unreviewed items are waiting in the vault.
- **Easy Management**: Review and clear notifications directly from the app.
- **Privacy First**: All data stays on your device.

## Tech Stack

- **Language**: Kotlin 2.2.10
- **UI**: Jetpack Compose (Material 3)
- **Database**: Room + KSP
- **Architecture**: MVVM (Model-View-ViewModel)
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 36 (Android 15)

## Getting Started

### Prerequisites

1.  **Android Studio**: Ladybug (2024.2.1) or newer recommended.
2.  **JDK**: Java 17 or newer.
3.  **GitHub CLI**: Required for contributing via the project workflow.
    ```sh
    brew install gh      # macOS
    gh auth login        # follow the prompts
    ```

### Setup

1.  Clone the repository:
    ```sh
    git clone https://github.com/pablosalgado/silent-vault.git
    cd silent-vault
    ```
2.  Open the project in Android Studio.
3.  Sync Gradle and build the project.

## Usage

When you first open Silent Vault, you must grant **Notification Access** permission. A banner will appear on the main screen with a direct link to the system settings if the permission is missing.

Once granted, the app will start capturing notifications in the background. You can find the total count of unreviewed notifications in the persistent status notification.

## Development

### Build

To build the debug APK:
```sh
./gradlew assembleDebug
```

### Running Tests

Silent Vault emphasizes reliability with a comprehensive test suite.

#### Unit Tests
Run local JVM tests (View Models, Repositories, etc.):
```sh
./gradlew test
```

#### Instrumentation Tests
Run UI and Integration tests on a connected device or emulator:
```sh
./gradlew connectedAndroidTest
```

## Architecture

The project follows the **MVVM** pattern with a focused package structure:

- `data/`: Room entities, DAO, Database definition, and the Repository.
- `service/`: `NotificationListener` implementation.
- `ui/`: Compose screens, ViewModels, and theme definition.
- `MainActivity.kt`: App entry point and manual DI setup.

## Contributing

We follow a strict workflow for all changes. Please refer to [AGENTS.md](AGENTS.md) for detailed instructions on the issue-branch-PR process, especially if you are using AI agents.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
