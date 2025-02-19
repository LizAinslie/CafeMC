package dev.lizainslie.cafemc.item.data

import dev.lizainslie.cafemc.data.location.SavedLocation
import dev.lizainslie.cafemc.data.location.SavedLocationsTable
import dev.lizainslie.cafemc.data.location.toDb
import org.bukkit.block.Block
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class UpgradedSpawner(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UpgradedSpawner>(UpgradedSpawnersTable) {
        fun findOrCreate(block: Block): UpgradedSpawner = transaction {
            val dbLocation = block.location.toDb()
            find { UpgradedSpawnersTable.location eq dbLocation.id }.firstOrNull()
                ?: new(UUID.randomUUID()) {
                    location = dbLocation
                    upgradesApplied = 0
                }
        }
    }

    var location by SavedLocation referencedOn UpgradedSpawnersTable.location
    
    var upgradesApplied by UpgradedSpawnersTable.upgradesApplied
}

object UpgradedSpawnersTable : UUIDTable("upgraded_spawners") {
    val location = reference("location", SavedLocationsTable)
    
    val upgradesApplied = integer("upgrades_applied").default(0)
}