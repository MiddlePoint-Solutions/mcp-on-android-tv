package io.middlepoint.mcponandroid.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import io.middlepoint.mcponandroid.mcp.McpService
import io.middlepoint.mcponandroid.mcp.ServerState

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun HomeScreen(
  modifier: Modifier = Modifier,
  viewModel: ServerViewModel = viewModel(),
) {
  val context = LocalContext.current
  LaunchedEffect(context) {
    val intent = Intent(context, McpService::class.java)
    context.startService(intent)
  }

  val state by viewModel.uiState.collectAsState()
  HomeScreenContent(state)

}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun HomeScreenContent(state: ServerState) {
  Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
  ) {
    if (state.isRunning) {
      Text("MCP Server is running at ${state.serverAddress}")
    } else {
      Text("MCP Server is not running.")
    }
  }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
  HomeScreenContent(ServerState(isRunning = true, serverAddress = "192.168.1.100"))
}
