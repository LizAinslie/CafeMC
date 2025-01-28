package dev.lizainslie.cafemc.tpa

import org.bukkit.entity.Player

data class TpaRequest(
    val sender: Player,
    val target: Player,
)