package dev.lizainslie.cafemc.tpa.commands

import dev.lizainslie.cafemc.chat.cmd.AllowedSender
import dev.lizainslie.cafemc.chat.cmd.PluginCommand
import dev.lizainslie.cafemc.tpa.TpaModule
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object TpDenyCommand : PluginCommand(
    command = "tpdeny",
    description = "Deny a teleport request",
    usage = "/tpdeny [player]",
    allowedSender = AllowedSender.PLAYER,
    minArgs = 0,
    maxArgs = 1,
) {
    override fun onCommand(args: List<String>) {
        val player = sender as Player
        
        val request = if (args.isNotEmpty())
            TpaModule.getRequest(
                Bukkit.getServer().getPlayerExact(args[0]) ?: return sendError("Player ${args[0]} not found"),
                player
            )
        else TpaModule.getRequest(player)

        if (request == null) {
            sendError("You do not have any pending teleport request.")
            return
        }

        TpaModule.removeRequest(request.sender, player)

        player.sendMessage("${ChatColor.GRAY}Denied teleport request from ${ChatColor.GOLD}${request.sender.displayName}${ChatColor.GRAY}.")
        request.sender.sendMessage("${ChatColor.GOLD}${player.displayName}${ChatColor.GRAY} has denied your teleport request.")
    }
    
    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        val openRequests = TpaModule.getRequestsTo(sender as Player)
        
        return when (args.size) {
            0 -> openRequests.map { it.sender.name }
            1 -> openRequests.map { it.sender.name }.filter { it.startsWith(args[0], ignoreCase = true) }
            else -> emptyList()
        }
    }
}