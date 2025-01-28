package dev.lizainslie.cafemc.afk.commands

import dev.lizainslie.cafemc.afk.AfkMap
import dev.lizainslie.cafemc.chat.PluginCommand
import org.bukkit.entity.Player

object AfkCommand : PluginCommand(
    command = "afk",
    description = "Toggle AFK status",
    usage = "/afk"
) {
    override fun onCommand(args: List<String>) {
        val player = sender as Player

        AfkMap.toggleAfk(player)
    }
}