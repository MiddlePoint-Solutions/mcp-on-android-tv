package io.middlepoint.mcponandroid.ui.state

import io.middlepoint.mcponandroid.ui.state.HomeState.*
import io.middlepoint.mcponandroid.utils.AdbState

sealed class HomeState {
  data object Connecting : HomeState()

  data object Home : HomeState()

  data object Failed : HomeState()
}

fun AdbState.mapToHomeState(): HomeState =
  when (this) {
    AdbState.Connecting -> Connecting
    AdbState.Ready -> Home
    is AdbState.Failed -> Failed
  }
