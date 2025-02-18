package dev.lizainslie.cafemc.auditing.commands

import dev.lizainslie.cafemc.auditing.AuditModule
import dev.lizainslie.cafemc.core.cmd.CommandContext
import dev.lizainslie.cafemc.core.cmd.PluginCommand

val ACTIONS = listOf("get", "list", "find")

object AuditCommand : PluginCommand(
    command = "audit",
    description = "Audit logged incidents",
    permission = "cafe.admin",
    minArgs = 2,
    maxArgs = -1,
    usage = "<incident_type|all> <${ACTIONS.joinToString { "|" }}> [id|param=value] [param=value]...",
) {
    
    override fun CommandContext.onCommand() {
        when (args.size) {
            2 -> {
                val type = args[0]
                
                if ((type !in AuditModule.incidentTypes) && type != "all")
                    return sendError("Invalid incident type")
                
                val action = args[1]
                
                when (action) {
                    "list" -> {
                        AuditModule.getIncidentsOfType(sender, type).forEach {
//                            sender.sendMessage(it.toString())
                        }
                    }
                    "get" -> {
//                        sender.sendMessage("Please provide an incident ID")
                    }
                    "find" -> {
//                        sender.sendMessage("Please provide a parameter to search for")
                    }
                    else -> {
                        sendError("Invalid action")
                    }
                }
            }
//            else -> {
//                val type = args[0]
//                val action = args[1]
//                val params = args.drop(2).map { it.split("=") }.associate { it[0] to it[1] }
//                
//                AuditModule.getIncidents(type, action, params).forEach {
//                    player.sendMessage(it.toString())
//                }
//            }
        }

    }
    
    override fun CommandContext.tabComplete(): List<String> {
        TODO() // this is gonna be a fun one
    }
}