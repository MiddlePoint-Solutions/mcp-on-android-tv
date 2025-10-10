package io.middlepoint.mcponandroid.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import io.middlepoint.mcponandroid.R
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
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Image(
        painter = painterResource(id = R.drawable.logo_transparent),
        contentDescription = null
      )
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        val indicatorColor by animateColorAsState(
          targetValue = if (state.isRunning) Color.Green else Color.Red,
          label = stringResource(id = R.string.indicator_color_label)
        )
        Box(
          modifier = Modifier
            .size(12.dp)
            .background(indicatorColor, CircleShape)
        )
        val statusText = if (state.isRunning) {
          stringResource(id = R.string.status_running)
        } else {
          stringResource(id = R.string.status_not_running)
        }
        Text(
          text = stringResource(id = R.string.status_label, statusText),
          style = MaterialTheme.typography.titleMedium
        )
      }
      AnimatedVisibility(visible = state.isRunning, enter = fadeIn(), exit = fadeOut()) {
        Text(
          text = state.serverAddress ?: stringResource(id = R.string.address_not_available),
          style = MaterialTheme.typography.bodyMedium
        )
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
  Column(verticalArrangement = Arrangement.spacedBy(32.dp)) {
    HomeScreenContent(ServerState(isRunning = true, serverAddress = "192.168.1.100"))
    HomeScreenContent(ServerState(isRunning = false))
  }
}
