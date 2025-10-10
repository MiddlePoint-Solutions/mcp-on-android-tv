package io.middlepoint.mcponandroid.mcp

import android.content.Context
import co.touchlab.kermit.Logger
import io.middlepoint.mcponandroid.utils.ADB
import io.modelcontextprotocol.kotlin.sdk.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.ImageContent
import io.modelcontextprotocol.kotlin.sdk.TextContent
import io.modelcontextprotocol.kotlin.sdk.Tool
import io.modelcontextprotocol.kotlin.sdk.server.Server
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import java.io.File
import java.util.Base64

class McpServerTools(
    private val server: Server,
    private val context: Context,
    private val adb: ADB,
) {
    private val logger = Logger.withTag("McpServerTools")

    fun registerTools() {
        addLaunchTvAppTool()
        addGetInstalledTvAppsTool()
        addTakeScreenshotTool()
        addSendKeyEventTool()
        addSendNavKeyEventTool()
        addTypeTextTool()
        addTapScreenTool()
    }

    private fun addLaunchTvAppTool() {
        server.addTool(
            name = "launch_tv_app",
            description = "Launch a TV application by its package name",
            inputSchema =
            Tool.Input(
                properties =
                buildJsonObject {
                    putJsonObject("app_package") {
                        put("type", "string")
                        put("description", "The package of the app to be launched")
                    }
                },
                required = listOf("app_package"),
            ),
        ) { request ->
            val packageName =
                request.arguments["app_package"]?.jsonPrimitive?.content
                    ?: return@addTool CallToolResult(
                        content = listOf(TextContent("The 'app_package' parameter is required.")),
                    )

            val result = adb.adbCommand("shell monkey -p $packageName 1")
            logger.d { "launch_tv_app: $result" }

            CallToolResult(content = listOf(TextContent("Launching $packageName succesfully")))
        }
    }

    private fun addGetInstalledTvAppsTool() {
        server.addTool(
            name = "get_installed_tv_apps",
            description = "Get a list of installed TV applications",
            inputSchema = Tool.Input(),
        ) {
            val result = adb.adbCommand("shell pm list packages -3")
            logger.d { "list-tv-apps: $result" }

            CallToolResult(
                content =
                listOf(
                    TextContent(result),
                ),
            )
        }
    }

    private fun addTakeScreenshotTool() {
        server.addTool(
            name = "take_screenshot",
            description =
            "Takes a screenshot of the device screen and returns it as a base64 encoded image.",
            inputSchema = Tool.Input(),
        ) {
            val remotePath = "/sdcard/screenshot_${System.currentTimeMillis()}.png"
            val localFile = File(context.cacheDir, "screenshot.png")

            try {
                val captureResult = adb.adbCommand("shell screencap -p $remotePath")
                if (captureResult.startsWith("Error")) {
                    logger.e { "Failed to take screenshot: $captureResult" }
                    return@addTool CallToolResult(
                        content = listOf(TextContent("Failed to take screenshot: $captureResult")),
                    )
                }

                val pullResult = adb.adbCommand("pull $remotePath ${localFile.absolutePath}")
                if (pullResult.startsWith("Error")) {
                    logger.e { "Failed to pull screenshot: $pullResult" }
                    return@addTool CallToolResult(
                        content =
                        listOf(TextContent("Failed to retrieve screenshot from device: $pullResult")),
                    )
                }

                val imageBytes = localFile.readBytes()
                val encodedImage = Base64.getEncoder().encodeToString(imageBytes)

                CallToolResult(
                    content =
                    listOf(
                        ImageContent(
                            data = encodedImage,
                            mimeType = "image/png",
                        ),
                    ),
                )
            } catch (e: Exception) {
                logger.e(e) { "Error taking screenshot" }
                CallToolResult(
                    content =
                    listOf(
                        TextContent(
                            "An exception occurred while taking the screenshot: ${e.message}",
                        ),
                    ),
                )
            } finally {
                adb.adbCommand("shell rm $remotePath")
                if (localFile.exists()) {
                    localFile.delete()
                }
            }
        }
    }

    private fun addSendKeyEventTool() {
        server.addTool(
            name = "send_key_event",
            description = "Sends a key event to the device. A list of key codes can be found at https://developer.android.com/reference/android/view/KeyEvent",
            inputSchema = Tool.Input(
                properties = buildJsonObject {
                    putJsonObject("key_code") {
                        put("type", "string")
                        put("description", "The key code to send to the device - starting with KEYCODE_")
                    }
                },
                required = listOf("key_code")
            )
        ) { request ->
            val keyCode = request.arguments["key_code"]?.jsonPrimitive?.content
                ?: return@addTool CallToolResult(
                    content = listOf(TextContent("The 'key_code' parameter is required."))
                )
            val result = adb.adbCommand("shell input keyevent $keyCode")
            logger.d { "send_key_event: $result" }
            if (result.startsWith("Error")) {
                CallToolResult(
                    content = listOf(TextContent("Failed to send key event: $result"))
                )
            } else {
                CallToolResult(
                    content = listOf(TextContent("Key event '$keyCode' sent successfully."))
                )
            }
        }
    }

    private fun addSendNavKeyEventTool() {
        server.addTool(
            name = "send_nav_key_event",
            description = "Sends a navigational key event to the device. Allowed values are 'up', 'down', 'left', 'right', 'select', 'back', and 'home'.",
            inputSchema = Tool.Input(
                properties = buildJsonObject {
                    putJsonObject("nav_key") {
                        put("type", "string")
                        put(
                            "description",
                            "The navigational key to send. Allowed values: 'up', 'down', 'left', 'right', 'select', 'back', 'home'."
                        )
                    }
                },
                required = listOf("nav_key")
            )
        ) { request ->
            val navKey = request.arguments["nav_key"]?.jsonPrimitive?.content
                ?: return@addTool CallToolResult(
                    content = listOf(TextContent("The 'nav_key' parameter is required."))
                )

            val keyCode = when (navKey.lowercase()) {
                "up" -> "KEYCODE_DPAD_UP"
                "down" -> "KEYCODE_DPAD_DOWN"
                "left" -> "KEYCODE_DPAD_LEFT"
                "right" -> "KEYCODE_DPAD_RIGHT"
                "select" -> "KEYCODE_DPAD_CENTER"
                "back" -> "KEYCODE_BACK"
                "home" -> "KEYCODE_HOME"
                else -> null
            }

            if (keyCode == null) {
                return@addTool CallToolResult(
                    content = listOf(TextContent("Invalid nav_key: '$navKey'. Use one of 'up', 'down', 'left', 'right', 'select', 'back', 'home'."))
                )
            }

            val result = adb.adbCommand("shell input keyevent $keyCode")
            logger.d { "send_nav_key_event: $result" }

            if (result.startsWith("Error")) {
                CallToolResult(
                    content = listOf(TextContent("Failed to send nav key event: $result"))
                )
            } else {
                CallToolResult(
                    content = listOf(TextContent("Nav key event '$navKey' sent successfully."))
                )
            }
        }
    }

    private fun addTypeTextTool() {
        server.addTool(
            name = "type_text",
            description = "Types the given text into the current input field.",
            inputSchema = Tool.Input(
                properties = buildJsonObject {
                    putJsonObject("text") {
                        put("type", "string")
                        put("description", "The text to type.")
                    }
                },
                required = listOf("text")
            )
        ) { request ->
            val textToType = request.arguments["text"]?.jsonPrimitive?.content
                ?: return@addTool CallToolResult(
                    content = listOf(TextContent("The 'text' parameter is required."))
                )

            val sanitizedText = textToType.replace(" ", "%s")
            val result = adb.adbCommand("shell input text '$sanitizedText'")
            logger.d { "type_text: $result" }

            if (result.startsWith("Error")) {
                CallToolResult(
                    content = listOf(TextContent("Failed to type text: $result"))
                )
            } else {
                CallToolResult(
                    content = listOf(TextContent("Text typed successfully."))
                )
            }
        }
    }

    private fun addTapScreenTool() {
        server.addTool(
            name = "tap_screen",
            description = "Taps the screen at the given x and y coordinates.",
            inputSchema = Tool.Input(
                properties = buildJsonObject {
                    putJsonObject("x") {
                        put("type", "integer")
                        put("description", "The x coordinate to tap.")
                    }
                    putJsonObject("y") {
                        put("type", "integer")
                        put("description", "The y coordinate to tap.")
                    }
                },
                required = listOf("x", "y")
            )
        ) { request ->
            val x = request.arguments["x"]?.jsonPrimitive?.content?.toIntOrNull()
                ?: return@addTool CallToolResult(
                    content = listOf(TextContent("The 'x' parameter is required and must be an integer."))
                )
            val y = request.arguments["y"]?.jsonPrimitive?.content?.toIntOrNull()
                ?: return@addTool CallToolResult(
                    content = listOf(TextContent("The 'y' parameter is required and must be an integer."))
                )

            val result = adb.adbCommand("shell input tap $x $y")
            logger.d { "tap_screen: $result" }

            if (result.startsWith("Error")) {
                CallToolResult(
                    content = listOf(TextContent("Failed to tap screen: $result"))
                )
            } else {
                CallToolResult(
                    content = listOf(TextContent("Tapped screen at ($x, $y) successfully."))
                )
            }
        }
    }
}
