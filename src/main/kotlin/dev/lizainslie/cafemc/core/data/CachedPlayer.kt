package dev.lizainslie.cafemc.core.data

import dev.lizainslie.cafemc.data.player.PlayerSettings

data class CachedPlayer(
    val realName: String,
    var settings: PlayerSettings?
)
