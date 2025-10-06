package io.middlepoint.mcponandroid.utils

sealed class AdbState {
    data object Connecting : AdbState()

    data object Ready : AdbState()

    data class Failed(
        val reason: String,
    ) : AdbState()
}
