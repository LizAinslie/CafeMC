package dev.lizainslie.cafemc.teleport.commands

import dev.lizainslie.cafemc.core.cmd.AllowedSender
import dev.lizainslie.cafemc.core.cmd.CommandContext
import dev.lizainslie.cafemc.core.cmd.PluginCommand
import dev.lizainslie.cafemc.data.location.SavedLocation
import dev.lizainslie.cafemc.data.player.PlayerSettings
import dev.lizainslie.cafemc.teleport.setLastLocation
import org.bukkit.ChatColor
import org.jetbrains.exposed.sql.transactions.transaction

internal val SUBCOMMANDS = listOf("set", "clear")

object HomeCommand : PluginCommand(
    command = "home",
    description = "Teleport to your home",
    usage = "/home [${SUBCOMMANDS.joinToString("|")}]",
    allowedSender = AllowedSender.PLAYER,
    minArgs = 0,
    maxArgs = 1,
    permission = "cafe.home",
) {
    override fun CommandContext.onCommand() {
        transaction {
            val settings = PlayerSettings.findOrCreate(player)

            if (args.isEmpty()) {
                val home = settings.home
                if (home == null) {
                    sendError("You do not have a home set.")
                    return@transaction
                }
                
                if (player.hasPermission("cafe.tpa.back")) {
                    player.setLastLocation(player.location)
                }

                player.teleport(home.location)
                player.sendMessage("${ChatColor.GRAY}Teleported to your home.")
                return@transaction
            }
            
            when (args[0]) {
                "set" -> {
                    settings.home?.let { 
                        it.delete()
                        settings.home = null
                    }
                    
                    settings.home = SavedLocation.findOrCreate(player.location)
                    player.sendMessage("${ChatColor.GRAY}Home set.")
                }

                "clear" -> {
                    if (settings.home == null) {
                        sendError("You do not have a home set.")
                        return@transaction
                    }

                    settings.home!!.delete()
                    settings.home = null

                    player.sendMessage("${ChatColor.GRAY}Home cleared.")
                }

                else -> sendError("Invalid subcommand ${args[0]}.")
            }
        }
    }

    override fun CommandContext.tabComplete(): List<String> = 
        when (args.size) {
            0 -> SUBCOMMANDS
            1 -> SUBCOMMANDS.filter { it.startsWith(args[0], ignoreCase = true) }
            else -> emptyList()
        }
}
