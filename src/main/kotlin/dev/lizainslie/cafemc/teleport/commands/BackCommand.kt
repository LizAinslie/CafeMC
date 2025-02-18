package dev.lizainslie.cafemc.teleport.commands

import dev.lizainslie.cafemc.CafeMC
import dev.lizainslie.cafemc.core.cmd.AllowedSender
import dev.lizainslie.cafemc.core.cmd.CommandContext
import dev.lizainslie.cafemc.core.cmd.PluginCommand
import dev.lizainslie.cafemc.data.player.PlayerSettings
import org.bukkit.ChatColor
import org.bukkit.metadata.FixedMetadataValue
import org.jetbrains.exposed.sql.transactions.transaction

object BackCommand : PluginCommand(
    command = "back",
    description = "Teleport to your previous location",
    usage = "/back",
    allowedSender = AllowedSender.PLAYER,
    permission = "cafe.tpa.back",
) {
    override fun CommandContext.onCommand() {
        transaction { 
            val settings = PlayerSettings.find(player)
            settings?.lastLocation?.let { 
                val location = it.location // get a Bukkit location
                
                // Delete the previous location in db
                it.delete()
                settings.lastLocation = null

                // Set metadata to prevent teleporting back again
                player.setMetadata("teleporting_back", FixedMetadataValue(CafeMC.instance, true))
                
                // Teleport the player to the previous location
                player.teleport(location)
                player.sendMessage("${ChatColor.GRAY}Teleported to your previous location.")
            } ?: sendError("No previous location saved.")
        }
    }
}