package dev.lizainslie.cafemc.data.location

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.UUID

class SavedLocation(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<SavedLocation>(SavedLocationsTable) {
        fun createFromBukkit(location: Location) = new(UUID.randomUUID()) {
            world = location.world!!
            x = location.x
            y = location.y
            z = location.z
            yaw = location.yaw
            pitch = location.pitch
        }
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

    fun getLocation() = Location(world, x, y, z, yaw, pitch)
}

object SavedLocationsTable : UUIDTable("saved_locations") {
    val world = varchar("world", 255)
    val x = double("x")
    val y = double("y")
    val z = double("z")
    val yaw = float("yaw")
    val pitch = float("pitch")
}