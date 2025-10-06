package io.middlepoint.mcponandroid.utils

import io.middlepoint.mcponandroid.utils.HomeState.*

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
