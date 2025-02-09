package dev.lizainslie.cafemc.economy.data

import kotlinx.datetime.Clock
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import org.jetbrains.exposed.sql.or
import java.util.UUID

class PlayerTransaction(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<PlayerTransaction>(PlayerTransactionsTable) {
        fun create(senderId: UUID, recipientId: UUID, amount: Double, notified: Boolean = false) = new {
            this.senderId = senderId
            this.recipientId = recipientId
            this.amount = amount
            this.notified = notified
        }
        
        fun getForRecipient(recipientId: UUID, op: (SqlExpressionBuilder.() -> Op<Boolean>)? = null) = find {
            PlayerTransactionsTable.recipientId eq recipientId and (op?.let { it() } ?: Op.TRUE) // run the op if it's not null
        }
        
        fun getForRecipients(recipientIds: List<UUID>, op: (SqlExpressionBuilder.() -> Op<Boolean>)? = null) = find {
            PlayerTransactionsTable.recipientId inList recipientIds and (op?.let { it() } ?: Op.TRUE)
        }
        
        fun getForPlayer(playerId: UUID, op: (SqlExpressionBuilder.() -> Op<Boolean>)? = null) = find {
            (PlayerTransactionsTable.senderId eq playerId or (PlayerTransactionsTable.recipientId eq playerId)) and (op?.let { it() } ?: Op.TRUE)
        }
    }

    var amount by PlayerTransactionsTable.amount
        private set
    
    var senderId by PlayerTransactionsTable.senderId
        private set
    
    var recipientId by PlayerTransactionsTable.recipientId
        private set
    
    var timestamp by PlayerTransactionsTable.timestamp
        private set
    
    var notified by PlayerTransactionsTable.notified
}

object PlayerTransactionsTable : UUIDTable("player_transactions") {
    val amount = double("amount")
    
    val senderId = uuid("sender_id")
    val recipientId = uuid("recipient_id")
    
    val timestamp = timestamp("timestamp").default(Clock.System.now())
    
    val notified = bool("notified").default(false)
}