package dev.lizainslie.cafemc.data.commands

import MigrationUtils
import dev.lizainslie.cafemc.CafeMC
import dev.lizainslie.cafemc.core.cmd.AllowedSender
import dev.lizainslie.cafemc.core.cmd.CommandContext
import dev.lizainslie.cafemc.core.cmd.PluginCommand
import dev.lizainslie.cafemc.data.location.SavedLocationsTable
import dev.lizainslie.cafemc.data.player.PlayerSettingsTable
import dev.lizainslie.cafemc.economy.data.EconomyAccountsTable
import dev.lizainslie.cafemc.economy.data.PlayerTransactionsTable
import org.jetbrains.exposed.sql.transactions.transaction

object MigrateCommand : PluginCommand(
    command = "migrate",
    description = "Migrate plugin database changes",
    allowedSender = AllowedSender.CONSOLE,
) {
    override fun CommandContext.onCommand() {
        val tables = listOf(
            SavedLocationsTable,
            PlayerSettingsTable,
            EconomyAccountsTable,
            PlayerTransactionsTable,
        )
        
        transaction {
            val allStatements =
                MigrationUtils.statementsRequiredForDatabaseMigration(*tables.toTypedArray(), withLogs = true)

            var migrationScript = ""

            // Append statements
            allStatements.forEach { statement ->
                // Add semicolon only if it's not already there
                val conditionalSemicolon = if (statement.last() == ';') "" else ";"

                migrationScript += "$statement$conditionalSemicolon\n"
            }
            
            CafeMC.instance.logger.info("Migration script:\n$migrationScript")
            
            // just fucking run it. fuck you flyway you made me do this shit
            exec(migrationScript) {
                println(it)
            }
        }
    }
}