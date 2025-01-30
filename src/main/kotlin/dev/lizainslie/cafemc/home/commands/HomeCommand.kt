package dev.lizainslie.cafemc.home.commands

import dev.lizainslie.cafemc.chat.AllowedSender
import dev.lizainslie.cafemc.chat.PluginCommand
import dev.lizainslie.cafemc.data.location.SavedLocation
import dev.lizainslie.cafemc.data.player.PlayerSettings
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.transactions.transaction

internal val SUBCOMMANDS = listOf("set", "clear")

object HomeCommand : PluginCommand(
    command = "home",
    description = "Teleport to your home",
    usage = "/home [${SUBCOMMANDS.joinToString("|")}]",
    minArgs = 0,
    maxArgs = 1,
    allowedSender = AllowedSender.PLAYER,
) {
    override fun onCommand(args: List<String>) {
        val player = sender as Player

        transaction {
            val settings = PlayerSettings.findOrCreate(player)

            if (args.isEmpty()) {
                val home = settings.home
                if (home == null) {
                    sendError("You do not have a home set.")
                    return@transaction
                }

                player.teleport(home.getLocation())
                player.sendMessage("${ChatColor.GRAY}Teleported to your home.")
                return@transaction
            }
            
            when (args[0]) {
                "set" -> {
                    settings.home?.let { 
                        it.delete()
                        settings.home = null
                    }
                    
                    settings.home = SavedLocation.createFromBukkit(player.location)
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

    override fun tabComplete(sender: CommandSender, args: List<String>) = 
        when (args.size) {
            0 -> SUBCOMMANDS
            1 -> SUBCOMMANDS.filter { it.startsWith(args[0], ignoreCase = true) }
            else -> emptyList()
        }
}
