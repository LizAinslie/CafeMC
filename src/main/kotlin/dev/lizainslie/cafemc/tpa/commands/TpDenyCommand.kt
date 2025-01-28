package dev.lizainslie.cafemc.tpa.commands

import dev.lizainslie.cafemc.chat.AllowedSender
import dev.lizainslie.cafemc.chat.PluginCommand
import dev.lizainslie.cafemc.tpa.TpaMap
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object TpDenyCommand : PluginCommand(
    command = "tpdeny",
    description = "Deny a teleport request",
    usage = "/tpdeny",
    allowedSender = AllowedSender.PLAYER
) {
    override fun onCommand(args: List<String>) {
        val player = sender as Player
        
        val request = if (args.isNotEmpty())
            TpaMap.getRequest(
                Bukkit.getServer().getPlayerExact(args[0]) ?: return sendError("Player ${args[0]} not found"),
                player
            )
        else TpaMap.getRequest(player)

        if (request == null) {
            sendError("You do not have any pending teleport request.")
            return
        }

        TpaMap.removeRequest(request.sender, player)

        player.sendMessage("${ChatColor.GRAY}Denied teleport request from ${ChatColor.GOLD}${request.sender.displayName}${ChatColor.GRAY}.")
        request.sender.sendMessage("${ChatColor.GOLD}${player.displayName}${ChatColor.GRAY} has denied your teleport request.")
    }
    
    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        val openRequests = TpaMap.getRequestsTo(sender as Player)
        
        return when (args.size) {
            0 -> openRequests.map { it.sender.name }
            1 -> openRequests.map { it.sender.name }.filter { it.startsWith(args[0], ignoreCase = true) }
            else -> emptyList()
        }
    }
}