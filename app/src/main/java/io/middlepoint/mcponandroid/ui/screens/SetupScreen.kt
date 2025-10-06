package io.middlepoint.mcponandroid.ui.screens

import android.content.Intent
import android.content.res.Configuration.UI_MODE_TYPE_TELEVISION
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices.TV_1080p
import androidx.compose.ui.tooling.preview.Devices.TV_720p
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import io.middlepoint.mcponandroid.ui.theme.McpAndroidTheme

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SetupScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }

    Row(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier =
                Modifier
                    .padding(20.dp)
                    .fillMaxWidth(0.45f),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Setup",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onPrimary,
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = @Suppress("ktlint:standard:max-line-length")
                "For best results enable USB debugging.\nIf unsure please watch YouTube video with instructions.",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimary,
            )

            Button(
                onClick = {
                    val intent = Intent(Settings.ACTION_SETTINGS)
                    context.startActivity(intent)
                },
                modifier =
                    Modifier
                        .defaultMinSize(200.dp)
                        .padding(top = 40.dp)
                        .focusRequester(focusRequester),
            ) {
                Text(
                    text = "Open Settings",
                    modifier = Modifier.padding(10.dp),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }

            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF6650a4,
    uiMode = UI_MODE_TYPE_TELEVISION,
    device = TV_1080p,
)
@Composable
private fun SetupScreenPreview() {
    McpAndroidTheme {
        SetupScreen()
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF6650a4,
    uiMode = UI_MODE_TYPE_TELEVISION,
    device = TV_720p,
)
@Composable
private fun SetupScreenPreview2() {
    McpAndroidTheme {
        SetupScreen()
    }
}
