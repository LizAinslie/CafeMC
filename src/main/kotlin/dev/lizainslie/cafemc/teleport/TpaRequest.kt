package dev.lizainslie.cafemc.teleport

import org.bukkit.entity.Player

data class TpaRequest(
    val sender: Player,
    val target: Player,
)