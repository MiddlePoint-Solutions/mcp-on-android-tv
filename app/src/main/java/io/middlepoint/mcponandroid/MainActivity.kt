package io.middlepoint.mcponandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import androidx.tv.material3.Text
import co.touchlab.kermit.Logger
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.cors.routing.CORS
import io.middlepoint.mcponandroid.ui.screens.Connecting
import io.middlepoint.mcponandroid.ui.screens.ConnectingScreen
import io.middlepoint.mcponandroid.ui.screens.Home
import io.middlepoint.mcponandroid.ui.screens.HomeScreen
import io.middlepoint.mcponandroid.ui.screens.SetupADB
import io.middlepoint.mcponandroid.ui.screens.SetupScreen
import io.middlepoint.mcponandroid.ui.screens.mapToScreen
import io.middlepoint.mcponandroid.ui.theme.McpAndroidTheme
import io.middlepoint.mcponandroid.utils.ADB
import io.modelcontextprotocol.kotlin.sdk.CallToolResult
import io.modelcontextprotocol.kotlin.sdk.GetPromptResult
import io.modelcontextprotocol.kotlin.sdk.Implementation
import io.modelcontextprotocol.kotlin.sdk.PromptArgument
import io.modelcontextprotocol.kotlin.sdk.PromptMessage
import io.modelcontextprotocol.kotlin.sdk.ReadResourceResult
import io.modelcontextprotocol.kotlin.sdk.Role
import io.modelcontextprotocol.kotlin.sdk.ServerCapabilities
import io.modelcontextprotocol.kotlin.sdk.TextContent
import io.modelcontextprotocol.kotlin.sdk.TextResourceContents
import io.modelcontextprotocol.kotlin.sdk.Tool
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.server.mcp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

@Suppress("ktlint:standard:no-consecutive-comments")
class MainActivity : ComponentActivity() {

  private var serverJob: Job? = null
  private val serverScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
  private lateinit var adb: ADB

  private val logger = Logger.withTag("MainActivity")

  @OptIn(ExperimentalTvMaterial3Api::class)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    adb = ADB.getInstance(applicationContext)

    serverJob = serverScope.launch {
      runSseMcpServerUsingKtorPlugin(8081)
    }

    setContent {
      val viewModel by viewModels<MainActivityViewModel>()
      val homeState by viewModel.homeState.collectAsState()
      val navController = rememberNavController()
      val isDebug = remember { BuildConfig.DEBUG }

      McpAndroidTheme {
        Surface(
          modifier =
            Modifier
              .fillMaxSize()
              .safeContentPadding(),
          colors =
            SurfaceDefaults.colors(
              containerColor = MaterialTheme.colorScheme.background,
            ),
        ) {
          NavHost(
            navController = navController,
            startDestination = Connecting,
            enterTransition = { fadeIn(spring(stiffness = Spring.StiffnessMedium)) },
            exitTransition = { fadeOut(spring(stiffness = Spring.StiffnessMedium)) },
          ) {
            composable<Connecting> {
              ConnectingScreen {
                viewModel.startADBServer()
              }
            }

            composable<Home> {
              HomeScreen()
            }
            composable<SetupADB> {
              SetupScreen()
            }
          }

          if (isDebug) {
            val status by viewModel.outputText.collectAsState("Waiting Status")
            Text(
              status,
              modifier = Modifier.align(Alignment.TopStart),
            )
          }

          BackHandler {
            finish()
          }

          LaunchedEffect(homeState) {
            homeState.let { state ->
              navController.navigate(
                route = state.mapToScreen(),
                navOptions =
                  navOptions {
                    popUpTo(Connecting) { inclusive = true }
                  },
              )
            }
          }
        }
      }
    }
  }

  fun configureServer(): Server {
    val server = Server(
      Implementation(
        name = "AndroidTV MCP",
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

    server.addPrompt(
      name = "List installed apps",
      description = "Develop small kotlin applications",
      arguments = listOf(
        PromptArgument(
          name = "Project Name",
          description = "Project name for the new project",
          required = true,
        ),
      ),
    ) { request ->
      GetPromptResult(
        "Description for ${request.name}",
        messages = listOf(
          PromptMessage(
            role = Role.user,
            content = TextContent(
              "Develop a kotlin project named <name>${request.arguments?.get("Project Name")}</name>",
            ),
          ),
        ),
      )
    }

    // Add a tool
    server.addTool(
      name = "list-tv-apps",
      description = "A tool to list installed apps on the TV",
      inputSchema = Tool.Input(),
    ) { request ->

      val result = adb.executeCommand("pm list packages -3")
      logger.d { "list-tv-apps: $result" }

      CallToolResult(
        content = listOf(TextContent(result)),
      )
    }

    // Add a resource
    server.addResource(
      uri = "https://search.com/",
      name = "Web Search",
      description = "Web search engine",
      mimeType = "text/html",
    ) { request ->
      ReadResourceResult(
        contents = listOf(
          TextResourceContents("Placeholder content for ${request.uri}", request.uri, "text/html"),
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
    println("Starting sse server on port $port")
    println("Use inspector to connect to the http://localhost:$port/sse")
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
    }.startSuspend(wait = true)
  }

}


