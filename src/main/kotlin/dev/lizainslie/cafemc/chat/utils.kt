package dev.lizainslie.cafemc.chat

import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.Bukkit
import org.bukkit.entity.Player

fun broadcast(message: BaseComponent, filter: (player: Player) -> Boolean = { true }) {
    Bukkit.getServer().onlinePlayers.filter(filter).forEach {
        it.spigot().sendMessage(message)
    }
}

/**
 * Broadcast a [message] to all online players excluding those that do not pass the [filter].
 */
fun broadcast(message: String, filter: (player: Player) -> Boolean = { true }) {
    // todo: hook DiscordSRV
    
    Bukkit.getServer().onlinePlayers.filter(filter).forEach {
        it.sendMessage(message)
    }
}