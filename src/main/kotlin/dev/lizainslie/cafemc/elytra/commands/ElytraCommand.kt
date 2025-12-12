package dev.lizainslie.cafemc.elytra.commands

import dev.lizainslie.cafemc.elytra.ElytraModule
import dev.lizainslie.cafemc.core.cmd.CommandContext
import dev.lizainslie.cafemc.core.cmd.PluginCommand
import dev.lizainslie.cafemc.core.modules.OnlinePlayerCacheModule
import org.bukkit.Bukkit

object ElytraCommand : PluginCommand(
    command = "elytra",
    usage = "<player>",
    permission = "cafe.elytra",
    minArgs = 1,
    maxArgs = 1,
    ) {
    override fun CommandContext.onCommand() {
        val targetPlayer = Bukkit.getOfflinePlayer(args[0])
        ElytraModule.toggleElytra(player, targetPlayer)
    }

    override fun CommandContext.tabComplete(): List<String> {
        val playerNames = Bukkit.getOnlinePlayers().mapNotNull { OnlinePlayerCacheModule[it.uniqueId]?.realName }
        return when (args.size) {
            0 -> playerNames
            1 -> playerNames.filter { it.startsWith(args[0], ignoreCase = true) }
            else -> emptyList()
        }
    }
}