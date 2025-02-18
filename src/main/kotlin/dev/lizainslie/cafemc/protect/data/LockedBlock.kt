package dev.lizainslie.cafemc.protect.data

import dev.lizainslie.cafemc.data.location.SavedLocation
import dev.lizainslie.cafemc.data.location.SavedLocationsTable
import dev.lizainslie.cafemc.data.location.toDb
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

class LockedBlock(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<LockedBlock>(LockedBlocksTable) {
        fun findFromBlock(block: Block) = find { LockedBlocksTable.location eq block.location.toDb().id }.firstOrNull()
        
        fun create(block: Block, player: Player) = new(UUID.randomUUID()) {
            location = block.location.toDb()
            ownerId = player.uniqueId
        }
    }
    
    var location by SavedLocation referencedOn LockedBlocksTable.location
    var ownerId by LockedBlocksTable.ownerId
}

object LockedBlocksTable : UUIDTable("locked_blocks") {
    val location = reference("location", SavedLocationsTable).uniqueIndex()
    val ownerId = uuid("owner_id")
}