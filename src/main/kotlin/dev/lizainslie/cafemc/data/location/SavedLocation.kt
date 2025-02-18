package dev.lizainslie.cafemc.data.location

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.and
import java.util.UUID

class SavedLocation(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<SavedLocation>(SavedLocationsTable) {
        fun findOrCreate(location: Location) = find { 
            (SavedLocationsTable.x eq location.x) and
            (SavedLocationsTable.y eq location.y) and
            (SavedLocationsTable.z eq location.z) and
            (SavedLocationsTable.yaw eq location.yaw) and
            (SavedLocationsTable.pitch eq location.pitch) and
            (SavedLocationsTable.world eq location.world!!.name)
        }.firstOrNull() 
            ?: new(UUID.randomUUID()) {
                world = location.world ?: Bukkit.getServer().worlds[0]
                x = location.x
                y = location.y
                z = location.z
                yaw = location.yaw
                pitch = location.pitch
            }
        
        fun find(x: Double, y: Double, z: Double, world: World = Bukkit.getServer().worlds[0]) = find {
            (SavedLocationsTable.x eq x) and
            (SavedLocationsTable.y eq y) and
            (SavedLocationsTable.z eq z) and
            (SavedLocationsTable.world eq world.name)
        }.firstOrNull()
    }

    private var _world by SavedLocationsTable.world
    var x by SavedLocationsTable.x
    var y by SavedLocationsTable.y
    var z by SavedLocationsTable.z
    var yaw by SavedLocationsTable.yaw
    var pitch by SavedLocationsTable.pitch

    var world: World
        get() = Bukkit.getWorld(_world) ?: error("World $_world not found")
        set(value) {
            _world = value.name
        }

    val location get() = Location(world, x, y, z, yaw, pitch)
}

object SavedLocationsTable : UUIDTable("saved_locations") {
    val world = varchar("world", 255)
    val x = double("x")
    val y = double("y")
    val z = double("z")
    val yaw = float("yaw")
    val pitch = float("pitch")
}

// region Extensions
fun Location.toDb() = SavedLocation.findOrCreate(this)