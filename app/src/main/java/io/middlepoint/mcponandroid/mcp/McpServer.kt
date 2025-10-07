package io.middlepoint.mcponandroid.mcp

import co.touchlab.kermit.Logger
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.cors.routing.CORS
import io.middlepoint.mcponandroid.utils.ADB
import io.modelcontextprotocol.kotlin.sdk.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.Implementation
import io.modelcontextprotocol.kotlin.sdk.ServerCapabilities
import io.modelcontextprotocol.kotlin.sdk.TextContent
import io.modelcontextprotocol.kotlin.sdk.Tool
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.server.mcp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

class McpServer(private val adb: ADB) {

  private val logger = Logger.withTag("McpServer")

  private fun configureServer(): Server {
    val server = Server(
      Implementation(
        name = "Android TV MCP",
        version = "0.1.0",
      ),
      ServerOptions(
        capabilities = ServerCapabilities(
          prompts = ServerCapabilities.Prompts(listChanged = true),
          resources = ServerCapabilities.Resources(subscribe = true, listChanged = true),
          tools = ServerCapabilities.Tools(listChanged = true),
          logging = null
        ),
      ),
    )

    // Add a tool
    server.addTool(
      name = "launch_tv_app",
      description = "Launch a TV application by its package name",
      inputSchema = Tool.Input(
        properties = buildJsonObject {
          putJsonObject("app_package") {
            put("type", "string")
            put("description", "The package of the app to be launched")
          }
        },
        required = listOf("app_package")
      ),
    ) { request ->

      val packageName =
        request.arguments["app_package"]?.jsonPrimitive?.content ?: return@addTool CallToolResult(
          content = listOf(TextContent("The 'app_package' parameter is required."))
        )

      val result = adb.adbCommand("shell monkey -p $packageName 1")
      logger.d { "list-tv-apps: $result" }

      CallToolResult(content = listOf(TextContent("Launching $packageName succesfully")))
    }


    // Add a tool
    server.addTool(
      name = "get_installed_tv_apps",
      description = "Get a list of installed TV applications",
      inputSchema = Tool.Input(),
    ) {
      val result = adb.adbCommand("shell pm list packages -3")
      logger.d { "list-tv-apps: $result" }

      CallToolResult(
        content = listOf(
          TextContent(
            result
          )
        ),
      )
    }

    return server
  }

  /**
   * Starts an SSE (Server Sent Events) MCP server using the Ktor framework and the specified port.
   *
   * The url can be accessed in the MCP inspector at [http://localhost:$port]
   *
   * @param port The port number on which the SSE MCP server will listen for client connections.
   * @return Unit This method does not return a value.
   */
  suspend fun runSseMcpServerUsingKtorPlugin(port: Int) {
    logger.d("Starting sse server on port $port")
    logger.d("Use inspector to connect to the http://localhost:$port/sse")
    withContext(Dispatchers.IO) {
      embeddedServer(CIO, host = "0.0.0.0", port = port) {

        install(CORS) {
          anyHost()
          allowNonSimpleContentTypes = true
          allowHeader(HttpHeaders.ContentType)
          allowHeader(HttpHeaders.AcceptEncoding)
          allowMethod(HttpMethod.Get)
          allowMethod(HttpMethod.Head)
          allowMethod(HttpMethod.Options)
        }

        mcp {
          return@mcp configureServer()
        }
      }.start(wait = true)
    }
  }
}
