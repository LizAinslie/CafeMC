package dev.lizainslie.cafemc.data

import dev.lizainslie.cafemc.data.location.SavedLocationsTable
import dev.lizainslie.cafemc.data.player.PlayerSettingsTable
import dev.lizainslie.cafemc.economy.data.EconomyAccountsTable
import dev.lizainslie.cafemc.economy.data.PlayerTransactionsTable
import dev.lizainslie.cafemc.protect.data.LockedBlockBreakIncidentsTable
import dev.lizainslie.cafemc.protect.data.LockedBlocksTable
import org.jetbrains.exposed.sql.transactions.transaction

fun migrate() {
    val tables = listOf(
        SavedLocationsTable,
        PlayerSettingsTable,
        EconomyAccountsTable,
        PlayerTransactionsTable,
        LockedBlocksTable,
        LockedBlockBreakIncidentsTable
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

        println("Migration script:\n$migrationScript")

        // just fucking run it. fuck you flyway you made me do this shit
        exec(migrationScript) {
            println("Migrated Successfully")
        }
    }
}