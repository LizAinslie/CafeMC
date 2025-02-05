package dev.lizainslie.cafemc.tpa.commands

import dev.lizainslie.cafemc.chat.cmd.AllowedSender
import dev.lizainslie.cafemc.chat.cmd.CommandContext
import dev.lizainslie.cafemc.chat.cmd.PluginCommand
import dev.lizainslie.cafemc.tpa.TpaModule
import org.bukkit.Bukkit

object TpaCommand : PluginCommand(
    command = "tpa",
    description = "Request to teleport to a player",
    usage = "/tpa <player>",
    allowedSender = AllowedSender.PLAYER,
    minArgs = 1,
    maxArgs = 1,
    permission = "cafe.tpa.use",
) {
    override fun CommandContext.onCommand() {
        val target = Bukkit.getPlayer(args[0])
        if (target == null) {
            sendError("Player not found")
            return
        }

        TpaModule.addRequest(player, target)
    }

    override fun CommandContext.tabComplete() = when (args.size) {
        0 -> Bukkit.getOnlinePlayers().map { it.name }
        1 -> Bukkit.getOnlinePlayers().map { it.name }.filter { it.startsWith(args[0], ignoreCase = true) }
        else -> emptyList()
    }
}