package dev.lizainslie.cafemc.teleport.commands

import dev.lizainslie.cafemc.core.cmd.AllowedSender
import dev.lizainslie.cafemc.core.cmd.CommandContext
import dev.lizainslie.cafemc.core.cmd.PluginCommand
import dev.lizainslie.cafemc.teleport.TeleportModule
import dev.lizainslie.cafemc.teleport.tabCompleteRequests
import dev.lizainslie.cafemc.teleport.withTpaRequest
import net.md_5.bungee.api.ChatColor

object TpDenyCommand : PluginCommand(
    command = "tpdeny",
    description = "Deny a teleport request",
    usage = "[player]",
    allowedSender = AllowedSender.PLAYER,
    minArgs = 0,
    maxArgs = 1,
    permission = "cafe.tpa.use",
) {
    override fun CommandContext.onCommand() {
        withTpaRequest { request ->
            TeleportModule.removeRequest(request.sender, player)

            player.sendMessage("${ChatColor.GRAY}Denied teleport request from ${ChatColor.GOLD}${request.sender.displayName}${ChatColor.GRAY}.")
            request.sender.sendMessage("${ChatColor.GOLD}${player.displayName}${ChatColor.GRAY} has denied your teleport request.")
        }
    }
    
    override fun CommandContext.tabComplete(): List<String> = tabCompleteRequests()
}