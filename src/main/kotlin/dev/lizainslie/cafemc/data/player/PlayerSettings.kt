package dev.lizainslie.cafemc.data.player

import dev.lizainslie.cafemc.data.location.SavedLocation
import dev.lizainslie.cafemc.data.location.SavedLocationTable
import org.bukkit.entity.Player
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.UUID

class PlayerSettings(id: EntityID<UUID>): UUIDEntity(id) {
    companion object : UUIDEntityClass<PlayerSettings>(PlayerSettingsTable) {
        fun findOrCreate(player: Player) = findById(player.uniqueId) ?: new(player.uniqueId) {}
        fun find(player: Player) = findById(player.uniqueId)
    }

    var home by SavedLocation optionalReferencedOn PlayerSettingsTable.home
}

object PlayerSettingsTable : UUIDTable("player_settings") {
    val home = reference("home", SavedLocationTable).nullable()
}