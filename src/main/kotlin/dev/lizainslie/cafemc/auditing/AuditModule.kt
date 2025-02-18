package dev.lizainslie.cafemc.auditing

import dev.lizainslie.cafemc.CafeMC
import dev.lizainslie.cafemc.auditing.commands.AuditCommand
import dev.lizainslie.cafemc.core.PluginModule
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.UUID

object AuditModule : PluginModule() {
    
    init {
        commands += AuditCommand
    }
    
    // region Incident Tracking
    
    private val trackedIncidents = mutableMapOf<String, AuditEntityManager<*>>()
    
    val incidentTypes get() = trackedIncidents.values.map { it.tag }
    
    fun trackIncident(incident: AuditIncidentEntityClass<*>) {
        trackedIncidents[incident.manager.tag] = incident.manager
    }
    
    fun getAllIncidentsForSubject(caller: CommandSender, subject: UUID): List<AuditIncidentEntity> {
        return trackedIncidents.values.flatMap { it.getAllIncidentsForSubject(caller, subject) }.sortedBy { it.timestamp }
    }
    
    fun getAllIncidents(caller: CommandSender): List<AuditIncidentEntity> {
        return trackedIncidents.values.flatMap { it.getAllIncidents(caller) }.sortedBy { it.timestamp }
    }
    
    fun getIncidentsOfTypeForSubject(caller: CommandSender, type: String, subject: UUID): List<AuditIncidentEntity> {
        return trackedIncidents[type]?.getAllIncidentsForSubject(caller, subject) ?: emptyList()
    }
    
    fun getIncidentsOfType(caller: CommandSender, type: String): List<AuditIncidentEntity> {
        return trackedIncidents[type]?.getAllIncidents(caller) ?: emptyList()
    }
    
    // endregion
    
    
    
    // region Module Lifecycle

    override fun onEnable(cafeMC: CafeMC) {
        super.onEnable(cafeMC)
    }

    override fun onDisable(cafeMC: CafeMC) {
        super.onDisable(cafeMC)
    }
    
    // endregion
}