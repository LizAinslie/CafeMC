package dev.lizainslie.cafemc.tpa.commands

import dev.lizainslie.cafemc.chat.AllowedSender
import dev.lizainslie.cafemc.chat.PluginCommand
import dev.lizainslie.cafemc.tpa.TpaMap
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object TpaCommand : PluginCommand(
    command = "tpa",
    description = "Request to teleport to a player",
    usage = "/tpa <player>",
    allowedSender = AllowedSender.PLAYER,
    minArgs = 1,
    maxArgs = 1,
) {
    override fun onCommand(args: List<String>) {
        val player = sender as Player

        val target = Bukkit.getPlayer(args[0])
        if (target == null) {
            sendError("Player not found")
            return
        }

        TpaMap.addRequest(player, target)
    }
}