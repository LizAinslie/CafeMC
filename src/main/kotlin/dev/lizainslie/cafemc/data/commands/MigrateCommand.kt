package dev.lizainslie.cafemc.data.commands

import dev.lizainslie.cafemc.CafeMC
import dev.lizainslie.cafemc.chat.cmd.AllowedSender
import dev.lizainslie.cafemc.chat.cmd.CommandContext
import dev.lizainslie.cafemc.chat.cmd.PluginCommand
import org.flywaydb.core.Flyway

object MigrateCommand : PluginCommand(
    command = "migrate",
    description = "Migrate plugin database changes",
    usage = "/migrate",
    allowedSender = AllowedSender.CONSOLE,
) {
    override fun CommandContext.onCommand() {
        Flyway.configure().dataSource(CafeMC.instance.hikariDataSource).load().migrate()
    }
}