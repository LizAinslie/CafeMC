package dev.lizainslie.cafemc.chat

import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.Bukkit
import org.bukkit.entity.Player

fun broadcast(message: BaseComponent, filterPlayers: (player: Player) -> Boolean = { true }) {
    Bukkit.getServer().onlinePlayers.filter(filterPlayers).forEach {
        it.spigot().sendMessage(message)
    }
}

fun broadcast(message: String, filterPlayers: (player: Player) -> Boolean = { true }) {
    Bukkit.getServer().onlinePlayers.filter(filterPlayers).forEach {
        it.sendMessage(message)
    }
}