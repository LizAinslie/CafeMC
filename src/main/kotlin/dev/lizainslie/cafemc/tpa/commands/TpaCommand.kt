package dev.lizainslie.cafemc.tpa.commands

import dev.lizainslie.cafemc.chat.cmd.AllowedSender
import dev.lizainslie.cafemc.chat.cmd.PluginCommand
import dev.lizainslie.cafemc.tpa.TpaModule
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
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

        TpaModule.addRequest(player, target)
    }

    override fun tabComplete(sender: CommandSender, args: List<String>) = when (args.size) {
        0 -> Bukkit.getOnlinePlayers().map { it.name }
        1 -> Bukkit.getOnlinePlayers().map { it.name }.filter { it.startsWith(args[0], ignoreCase = true) }
        else -> emptyList()
    }
}