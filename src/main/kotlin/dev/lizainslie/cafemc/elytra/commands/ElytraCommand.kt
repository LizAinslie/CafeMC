package dev.lizainslie.cafemc.elytra.commands

import dev.lizainslie.cafemc.elytra.ElytraModule
import dev.lizainslie.cafemc.core.cmd.CommandContext
import dev.lizainslie.cafemc.core.cmd.PluginCommand
import org.bukkit.Bukkit

object ElytraCommand : PluginCommand(
    command = "elytra",
    usage = "<player>",
    permission = "cafe.elytra",
    minArgs = 1,
    maxArgs = 1,
    ) {
    override fun CommandContext.onCommand() {
        val targetPlayer = Bukkit.getPlayer(args[0])
        if (targetPlayer == null) {
            sendError("Player not found.")
            return
        }
        val target = args[0]
        ElytraModule.toggleElytra(player, target)
    }

    override fun CommandContext.tabComplete(): List<String> = when (args.size) {
        0 -> Bukkit.getOnlinePlayers().map { it.name }
        1 -> Bukkit.getOnlinePlayers().map { it.name }.filter { it.startsWith(args[0], ignoreCase = true) }
        else -> emptyList()
    }
}