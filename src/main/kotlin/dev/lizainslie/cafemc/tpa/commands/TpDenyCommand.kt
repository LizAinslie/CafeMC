package dev.lizainslie.cafemc.tpa.commands

import dev.lizainslie.cafemc.chat.cmd.AllowedSender
import dev.lizainslie.cafemc.chat.cmd.CommandContext
import dev.lizainslie.cafemc.chat.cmd.PluginCommand
import dev.lizainslie.cafemc.tpa.TpaModule
import dev.lizainslie.cafemc.tpa.tabCompleteRequests
import dev.lizainslie.cafemc.tpa.withTpaRequest
import net.md_5.bungee.api.ChatColor

object TpDenyCommand : PluginCommand(
    command = "tpdeny",
    description = "Deny a teleport request",
    usage = "/tpdeny [player]",
    allowedSender = AllowedSender.PLAYER,
    minArgs = 0,
    maxArgs = 1,
    permission = "cafe.tpa",
) {
    override fun CommandContext.onCommand() {
        withTpaRequest { request ->
            TpaModule.removeRequest(request.sender, player)

            player.sendMessage("${ChatColor.GRAY}Denied teleport request from ${ChatColor.GOLD}${request.sender.displayName}${ChatColor.GRAY}.")
            request.sender.sendMessage("${ChatColor.GOLD}${player.displayName}${ChatColor.GRAY} has denied your teleport request.")
        }
    }
    
    override fun CommandContext.tabComplete() = tabCompleteRequests()
}