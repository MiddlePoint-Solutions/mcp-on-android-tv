package io.middlepoint.mcponandroid.ui.screens

import io.middlepoint.mcponandroid.ui.state.HomeState
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen

@Serializable
data object Home : Screen()

@Serializable
data object SetupADB : Screen()

@Serializable
data object Connecting : Screen()

fun HomeState.mapToScreen(): Screen =
    when (this) {
        HomeState.Connecting -> Connecting
        HomeState.Failed -> SetupADB
        HomeState.Home -> Home
    }
