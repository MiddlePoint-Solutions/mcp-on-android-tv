# Project: Mcp On Android

## Project Overview

This is an Android application that runs a Ktor server on an Android device. The server exposes an SSE (Server-Sent Events) endpoint to communicate with clients. The application utilizes ADB (Android Debug Bridge) to interact with the underlying Android system. The user interface is built with Jetpack Compose.

The main components of the application are:

*   **MainActivity.kt:** The main entry point of the application. It initializes the Ktor server and sets up the Jetpack Compose UI.
*   **McpServer.kt:** This file likely contains the implementation of the Ktor server and the SSE endpoint.
*   **ADB.kt:** A utility class for interacting with ADB.
*   **UI Screens:** The application has three screens: `ConnectingScreen`, `HomeScreen`, and `SetupScreen`, all built with Jetpack Compose.

## Building and Running

This is a standard Android project. You can build and run it using Android Studio or the Gradle wrapper.

### Using Android Studio

1.  Open the project in Android Studio.
2.  Connect an Android device or start an emulator.
3.  Click the "Run" button.

### Using Gradle

To build the project from the command line, use the following command:

```bash
./gradlew assembleDebug
```

To install the application on a connected device or emulator, use the following command:

```bash
./gradlew installDebug
```

## Development Conventions

*   **Language:** The project is written in Kotlin.
*   **UI:** The user interface is built with Jetpack Compose.
*   **Architecture:** The application uses a ViewModel to separate UI logic from business logic.
*   **Dependencies:** The project uses the following key libraries:
    *   Ktor for the web server.
    *   Jetpack Compose for the UI.
    *   ADB for interacting with the Android system.
