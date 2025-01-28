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
    companion object : UUIDEntityClass<SavedLocation>(SavedLocationTable) {
        fun createFromBukkit(location: Location) = new(UUID.randomUUID()) {
            world = location.world!!
            x = location.x
            y = location.y
            z = location.z
            yaw = location.yaw
            pitch = location.pitch
        }
    }

    private var _world by SavedLocationTable.world
    var x by SavedLocationTable.x
    var y by SavedLocationTable.y
    var z by SavedLocationTable.z
    var yaw by SavedLocationTable.yaw
    var pitch by SavedLocationTable.pitch

    var world: World
        get() = Bukkit.getWorld(_world) ?: error("World $_world not found")
        set(value) {
            _world = value.name
        }

    fun getLocation() = Location(world, x, y, z, yaw, pitch)
}

object SavedLocationTable : UUIDTable("saved_locations") {
    val world = varchar("world", 255)
    val x = double("x")
    val y = double("y")
    val z = double("z")
    val yaw = float("yaw")
    val pitch = float("pitch")
}