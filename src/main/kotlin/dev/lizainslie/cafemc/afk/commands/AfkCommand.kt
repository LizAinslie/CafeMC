package dev.lizainslie.cafemc.afk.commands

import dev.lizainslie.cafemc.afk.AfkModule
import dev.lizainslie.cafemc.core.cmd.CommandContext
import dev.lizainslie.cafemc.core.cmd.PluginCommand

object AfkCommand : PluginCommand(
    command = "afk",
    permission = "cafe.afk",
) {
    override fun CommandContext.onCommand() {
        AfkModule.toggleAfk(player)
    }
}