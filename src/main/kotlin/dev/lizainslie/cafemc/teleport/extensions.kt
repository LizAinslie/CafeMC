package dev.lizainslie.cafemc.teleport

import dev.lizainslie.cafemc.core.cmd.CommandContext
import dev.lizainslie.cafemc.data.location.SavedLocation
import dev.lizainslie.cafemc.data.player.PlayerSettings
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.transactions.transaction

internal fun CommandContext.tabCompleteRequests(): List<String> {
    val openRequests = TeleportModule.getRequestsTo(player)

    return when (args.size) {
        0 -> openRequests.map { it.sender.name }
        1 -> openRequests.map { it.sender.name }.filter { it.startsWith(args[0], ignoreCase = true) }
        else -> emptyList()
    }
}

internal fun CommandContext.withTpaRequest(handleRequest: (request: TpaRequest) -> Unit) {
    handleRequest(
        (if (args.isNotEmpty())
            TeleportModule.getRequest(
                Bukkit.getServer().getPlayerExact(args[0])
                    ?: return sendError("Player ${args[0]} not found"),
                player
            )
        else TeleportModule.getRequest(player))
            ?: return sendError("You do not have any pending teleport request.")
    )
}

internal fun Player.setLastLocation(from: Location) {
    transaction {
        val settings = PlayerSettings.findOrCreate(this@setLastLocation)
        
        if (settings.lastLocation != null) {
            settings.lastLocation!!.delete()
            settings.lastLocation = null
        }
        
        settings.lastLocation = SavedLocation.findOrCreate(from)

        spigot().sendMessage(
            ComponentBuilder("Last location saved. ").color(ChatColor.GRAY)
                .append("[Go Back]").color(ChatColor.GOLD)
                .event(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/back"))
                .event(HoverEvent(HoverEvent.Action.SHOW_TEXT, ComponentBuilder("/back").create()))
                .build()
        )
    }
}