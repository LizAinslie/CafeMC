package dev.lizainslie.cafemc.afk.commands

import dev.lizainslie.cafemc.afk.AfkModule
import dev.lizainslie.cafemc.chat.cmd.CommandContext
import dev.lizainslie.cafemc.chat.cmd.PluginCommand

object AfkCommand : PluginCommand(
    command = "afk",
    description = "Toggle AFK status",
    usage = "/afk",
    permission = "cafe.afk",
) {
    override fun CommandContext.onCommand() {
        AfkModule.toggleAfk(player)
    }
}