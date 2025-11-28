package dev.lizainslie.cafemc.data.commands

import dev.lizainslie.cafemc.core.cmd.AllowedSender
import dev.lizainslie.cafemc.core.cmd.CommandContext
import dev.lizainslie.cafemc.core.cmd.PluginCommand
import dev.lizainslie.cafemc.data.migrate

object MigrateCommand : PluginCommand(
    command = "migrate",
    allowedSender = AllowedSender.CONSOLE,
) {
    override fun CommandContext.onCommand() {
        migrate()
    }
}