package dev.lizainslie.cafemc.data.player

import dev.lizainslie.cafemc.data.location.SavedLocation
import dev.lizainslie.cafemc.data.location.SavedLocationsTable
import org.bukkit.entity.Player
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.UUID

class PlayerSettings(id: EntityID<UUID>): UUIDEntity(id) {
    companion object : UUIDEntityClass<PlayerSettings>(PlayerSettingsTable) {
        fun findOrCreate(player: Player) = findById(player.uniqueId) ?: new(player.uniqueId) {}
        fun find(player: Player) = find(player.uniqueId)
        fun find(uuid: UUID) = findById(uuid)
    }

    var home by SavedLocation optionalReferencedOn PlayerSettingsTable.home
    var lastLocation by SavedLocation optionalReferencedOn PlayerSettingsTable.lastLocation
    var nickname by PlayerSettingsTable.nickname
}

object PlayerSettingsTable : UUIDTable("player_settings") {
    val home = reference("home", SavedLocationsTable).nullable()
    val lastLocation = reference("last_location", SavedLocationsTable).nullable()
    val nickname = varchar("nickname", 128).nullable()
}
