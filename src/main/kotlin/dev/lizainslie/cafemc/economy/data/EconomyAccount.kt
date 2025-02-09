package dev.lizainslie.cafemc.economy.data

import org.bukkit.entity.Player
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.UUID

class EconomyAccount(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<EconomyAccount>(EconomyAccountsTable) {
        fun findOrCreate(playerId: UUID) = findById(playerId) ?: new(playerId) {}
    }
    
    var balance by EconomyAccountsTable.balance
}

object EconomyAccountsTable : UUIDTable("economy_accounts") {
    val balance = double("balance").default(0.0)
}