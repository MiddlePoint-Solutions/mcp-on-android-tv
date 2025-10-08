# MCP on Android TV

A Model Context Protocol (MCP) server running directly on your Android TV devices.

By leveraging ADB (Android Debug Bridge), it allows local network control of your Android TV through any LLM agent or client that supports remote MCP servers. This makes it possible to open apps, list apps, and run other commands on your TV using natural language.

> ⚠️ **Work in Progress**  
> This project is in an early stage of development. Features may be incomplete, unstable, or subject to change.  
> Contributions and feedback are very welcome as the project evolves.


## Why Android TV?

- **Simpler ADB setup**: On Android TV, ADB debugging can be activated and connected to without requiring pairing.
- **Stable local connection**: Unlike wireless debugging on phones, the connection does not cycle, giving you a direct link to the device.
- **MCP integration**: With MCP layered on top of ADB, any LLM client supporting custom MCP servers (such as [Goose](https://github.com/goose-ai) or [Claude Desktop](https://claude.ai)) can act as a controller for your TV.

⚠️ **Note**: For security reasons this should only be made to work on your local network, exposing your TV to the internet is a big risk.

## Features

- Direct **ADB integration** without pairing.
- MCP server running on Android TV devices.
- Works with the **MCP Kotlin SDK 0.7.2** (official SDK).
- Local network device control via LLM clients.
- Current communication method: **SSE (deprecated)**.

## Installation & Usage

1. **Build & install** the app on your Android TV device or set-top box.
2. Ensure **ADB debugging** is enabled and working.
3. Launch the app — it will display the **MCP Ready** screen with:
    - Device details
    - MCP server address to connect to
4. On your LLM client (e.g., Goose, Claude Desktop):
    - Create a new connector/extension.
    - Enter the server address from your TV.
5. Start using natural language to control your TV:
    - Open apps
    - List installed apps
    - More commands coming soon


## Contributing

Contributions are very welcome!

- Submit a **pull request** with new features, tools, or fixes.
- Ideas for extended control and utilities are encouraged.
- Please follow typical open-source etiquette when submitting changes.

## License

This project is licensed under the MIT License—see the [LICENSE](./LICENSE) file for details.
