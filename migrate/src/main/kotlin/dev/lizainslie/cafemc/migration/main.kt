package dev.lizainslie.cafemc.migration

import MigrationUtils
import dev.lizainslie.cafemc.data.location.SavedLocationsTable
import dev.lizainslie.cafemc.data.migrate
import dev.lizainslie.cafemc.data.player.PlayerSettingsTable
import dev.lizainslie.cafemc.economy.data.EconomyAccountsTable
import dev.lizainslie.cafemc.economy.data.PlayerTransactionsTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ExperimentalDatabaseMigrationApi
import org.jetbrains.exposed.sql.transactions.transaction

const val scriptDirectory = "src/main/resources/db/migration"

@OptIn(ExperimentalDatabaseMigrationApi::class)
fun main() {
    Database.connect("jdbc:sqlite:file:./cafemc.db", "org.sqlite.JDBC")
    transaction {
//        MigrationUtils.generateMigrationScript(
//            PlayerSettingsTable,
//            SavedLocationsTable,
//            scriptName = "V1_1__base",
//            scriptDirectory = scriptDirectory
//        )
//
//        MigrationUtils.generateMigrationScript(
//            EconomyAccountsTable,
//            PlayerTransactionsTable,
//            scriptName = "V1_2__econ",
//            scriptDirectory = scriptDirectory
//        )
        
        migrate()
    }
}