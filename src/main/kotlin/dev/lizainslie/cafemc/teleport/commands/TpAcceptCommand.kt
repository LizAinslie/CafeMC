package dev.lizainslie.cafemc.teleport.commands

import dev.lizainslie.cafemc.core.cmd.AllowedSender
import dev.lizainslie.cafemc.core.cmd.CommandContext
import dev.lizainslie.cafemc.core.cmd.PluginCommand
import dev.lizainslie.cafemc.teleport.TeleportModule
import dev.lizainslie.cafemc.teleport.setLastLocation
import dev.lizainslie.cafemc.teleport.tabCompleteRequests
import dev.lizainslie.cafemc.teleport.withTpaRequest
import net.md_5.bungee.api.ChatColor

object TpAcceptCommand : PluginCommand(
    command = "tpaccept",
    description = "Accept a teleport request",
    usage = "[player]",
    allowedSender = AllowedSender.PLAYER,
    minArgs = 0,
    maxArgs = 1,
    permission = "cafe.tpa.use",
) {
    override fun CommandContext.onCommand() {
        withTpaRequest { request ->
            player.sendMessage("${ChatColor.GRAY}Accepted teleport request from ${ChatColor.GOLD}${request.sender.displayName}${ChatColor.GRAY}.")
            request.sender.sendMessage("${ChatColor.GOLD}${player.displayName}${ChatColor.GRAY} has accepted your teleport request, teleporting you there.")

            // remove the request from the module
            TeleportModule.removeRequest(request.sender, player)
            
            // set the last location of the request sender if they have the permission to teleport back
            if (request.sender.hasPermission("cafe.tpa.back")) 
                request.sender.setLastLocation(request.sender.location)
            
            // teleport the request sender to the player
            request.sender.teleport(player)
        }
    }

    override fun CommandContext.tabComplete() = tabCompleteRequests()
}