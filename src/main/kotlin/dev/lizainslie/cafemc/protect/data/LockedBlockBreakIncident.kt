package dev.lizainslie.cafemc.protect.data

import dev.lizainslie.cafemc.auditing.*
import dev.lizainslie.cafemc.data.location.SavedLocation
import kotlinx.datetime.Clock
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import java.util.UUID

class LockedBlockBreakIncident(id: EntityID<UUID>) : AuditIncidentEntity(id) {
    companion object : AuditIncidentEntityClass<LockedBlockBreakIncident>(LockedBlockBreakIncidentsTable) {
        fun create(block: LockedBlock, player: UUID) = new(UUID.randomUUID()) {
            this.block = block
            this.subject = player
        }

        override val manager = object : AuditEntityManager<LockedBlockBreakIncident> {
            override val entityClass = LockedBlockBreakIncident
            override val tag = "locked_block_break"
            override val table = LockedBlockBreakIncidentsTable
            override val searchableParameters = mutableListOf(
                SearchableParameter.PPlayerOrUuid("subject", LockedBlockBreakIncidentsTable.subject)
            )

            override fun isAuthorized(caller: CommandSender, incident: LockedBlockBreakIncident) =
                super.isAuthorized(caller, incident) || (caller is Player && incident.block.ownerId == caller.uniqueId)
            
            override fun getAllIncidentsForSubject(caller: CommandSender, subject: UUID) = entityClass
                .find { LockedBlockBreakIncidentsTable.subject eq subject }
                .filter { isAuthorized(caller, it) }
                .toList()
            
            override fun getAllIncidents(caller: CommandSender) = entityClass
                .all()
                .filter { isAuthorized(caller, it) }
                .toList()
        }
    }

    var block by LockedBlock referencedOn LockedBlockBreakIncidentsTable.block
    
    override var subject by LockedBlockBreakIncidentsTable.subject
    override var timestamp by LockedBlockBreakIncidentsTable.timestamp
    override var notified by LockedBlockBreakIncidentsTable.notified
}

object LockedBlockBreakIncidentsTable : AuditIncidentTable("locked_block_break_incidents") {
    val block = reference("block", LockedBlocksTable)
    
    override val subject = uuid("subject")
    override val timestamp = timestamp("timestamp").default(Clock.System.now())
    override val notified = bool("notified").default(false)
}