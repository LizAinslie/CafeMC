package dev.lizainslie.cafemc.warp.data

import dev.lizainslie.cafemc.data.location.SavedLocation
import dev.lizainslie.cafemc.data.location.SavedLocationsTable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

class ServerWarp(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<ServerWarp>(ServerWarpTable) {
        fun findByName(name: String) = find { ServerWarpTable.name eq name }.firstOrNull()
    }

    var name by ServerWarpTable.name
    var location by SavedLocation referencedOn ServerWarpTable.location
}

object ServerWarpTable : UUIDTable("server_warps") {
    val name = varchar("name", 255).uniqueIndex()
    val location = reference("location", SavedLocationsTable)
}