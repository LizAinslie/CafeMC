package dev.lizainslie.cafemc.tpa

import dev.lizainslie.cafemc.chat.cmd.CommandContext
import org.bukkit.Bukkit

internal fun CommandContext.tabCompleteRequests(): List<String> {
    val openRequests = TpaModule.getRequestsTo(player)

    return when (args.size) {
        0 -> openRequests.map { it.sender.name }
        1 -> openRequests.map { it.sender.name }.filter { it.startsWith(args[0], ignoreCase = true) }
        else -> emptyList()
    }
}

internal fun CommandContext.withTpaRequest(handleRequest: (request: TpaRequest) -> Unit) {
    handleRequest(
        (if (args.isNotEmpty())
            TpaModule.getRequest(
                Bukkit.getServer().getPlayerExact(args[0])
                    ?: return sendError("Player ${args[0]} not found"),
                player
            )
        else TpaModule.getRequest(player))
            ?: return sendError("You do not have any pending teleport request.")
    )
}