package dev.lizainslie.cafemc.auditing

import dev.lizainslie.cafemc.data.location.SavedLocation
import dev.lizainslie.cafemc.util.AccountUtils
import dev.lizainslie.cafemc.util.isUuid
import kotlinx.datetime.Instant
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.transactions.transaction
import java.lang.IllegalStateException
import java.util.*

interface AuditEntityManager<TIncident : AuditIncidentEntity> {
    val tag: String
    val table: AuditIncidentTable
    val entityClass: AuditIncidentEntityClass<TIncident>
    val searchableParameters: List<SearchableParameter<*>> get() = listOf(
        SearchableParameter.PUuid("subject", table.subject),
        SearchableParameter.PBool("notified", table.notified)
    )
    
    /**
     * Check if the given [caller] is authorized to view the given [incident].
     */
    fun isAuthorized(caller: CommandSender, incident: TIncident): Boolean =
        caller is ConsoleCommandSender || caller.hasPermission("cafe.audit.admin")

    fun getAllIncidentsForSubject(caller: CommandSender, subject: UUID): List<TIncident>
    fun getAllIncidents(caller: CommandSender): List<TIncident>
}

abstract class AuditIncidentEntity(id: EntityID<UUID>) : UUIDEntity(id) {
    abstract var subject: UUID
    abstract var timestamp: Instant
    abstract var notified: Boolean
}

abstract class AuditIncidentEntityClass<TEntity : AuditIncidentEntity>(table: AuditIncidentTable) : UUIDEntityClass<TEntity>(table) {
    abstract val manager: AuditEntityManager<TEntity>
}

abstract class AuditIncidentTable(name: String) : UUIDTable(name) {
    abstract val subject: Column<UUID>
    abstract val timestamp: Column<Instant>
    abstract val notified: Column<Boolean>
}

sealed interface SearchableParameter<T> {
    val name: String
    val column: Column<T>
    
    fun transform(value: String): T
    fun matches(value: String): Boolean
    
    class PString(override val name: String, override val column: Column<String>) : SearchableParameter<String> {
        override fun transform(value: String): String = value
        override fun matches(value: String) = true
    }
    
    class PInt(override val name: String, override val column: Column<Int>) : SearchableParameter<Int> {
        override fun transform(value: String): Int = value.toInt()
        override fun matches(value: String) = value.toIntOrNull() != null
    }
    
    class PLong(override val name: String, override val column: Column<Long>) : SearchableParameter<Long> {
        override fun transform(value: String): Long = value.toLong()
        override fun matches(value: String) = value.toLongOrNull() != null
    }
    
    class PDouble(override val name: String, override val column: Column<Double>) : SearchableParameter<Double> {
        override fun transform(value: String): Double = value.toDouble()
        override fun matches(value: String) = value.toDoubleOrNull() != null
    }
    
    class PBool(override val name: String, override val column: Column<Boolean>) : SearchableParameter<Boolean> {
        override fun transform(value: String): Boolean = value.lowercase().toBooleanStrict()
        override fun matches(value: String) = value.lowercase() in listOf("true", "false")
    }
    
    class PUuid(override val name: String, override val column: Column<UUID>) : SearchableParameter<UUID> {
        override fun transform(value: String): UUID = UUID.fromString(value)
        override fun matches(value: String) = isUuid(value)
    }
    
    class PPlayerOrUuid(override val name: String, override val column: Column<UUID>) : SearchableParameter<UUID> {
        override fun transform(value: String): UUID = 
            if (isUuid(value)) UUID.fromString(value)
            else AccountUtils.getUuidForAccountName(value) 
                ?: throw IllegalStateException("Player not found")
        override fun matches(value: String) = true
    }
}