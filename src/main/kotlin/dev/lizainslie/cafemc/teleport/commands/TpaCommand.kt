package dev.lizainslie.cafemc.teleport.commands

import dev.lizainslie.cafemc.core.cmd.AllowedSender
import dev.lizainslie.cafemc.core.cmd.CommandContext
import dev.lizainslie.cafemc.core.cmd.PluginCommand
import dev.lizainslie.cafemc.teleport.TeleportModule
import org.bukkit.Bukkit

object TpaCommand : PluginCommand(
    command = "tpa",
    description = "Request to teleport to a player",
    usage = "<player>",
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

        TeleportModule.addRequest(player, target)
    }

    override fun CommandContext.tabComplete(): List<String> = when (args.size) {
        0 -> Bukkit.getOnlinePlayers().map { it.name }
        1 -> Bukkit.getOnlinePlayers().map { it.name }.filter { it.startsWith(args[0], ignoreCase = true) }
        else -> emptyList()
    }
}