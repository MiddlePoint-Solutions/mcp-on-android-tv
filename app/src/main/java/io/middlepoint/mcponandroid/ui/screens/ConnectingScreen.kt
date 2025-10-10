package io.middlepoint.mcponandroid.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.abdullahalhakimi.smoothmotion.animations.RotatingCircleProgress
import io.middlepoint.mcponandroid.R

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ConnectingScreen(
  modifier: Modifier = Modifier,
  startAdbServer: () -> Unit,
) {
  Column(
    modifier = modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    RotatingCircleProgress(
      modifier = Modifier.size(90.dp),
      color = Color.White,
    )

    Spacer(modifier = Modifier.height(30.dp))

    Text(
      text = stringResource(R.string.checking_device_setup),
      style = MaterialTheme.typography.headlineLarge,
    )
  }

  LaunchedEffect(Unit) {
    startAdbServer()
  }
}
