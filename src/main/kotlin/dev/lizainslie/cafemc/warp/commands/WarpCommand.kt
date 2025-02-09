package dev.lizainslie.cafemc.warp.commands

import dev.lizainslie.cafemc.core.cmd.AllowedSender
import dev.lizainslie.cafemc.core.cmd.CommandContext
import dev.lizainslie.cafemc.core.cmd.PluginCommand

object WarpCommand : PluginCommand(
    command = "warp",
    description = "Teleport to a warp",
    usage = "/warp <name>",
    allowedSender = AllowedSender.PLAYER,
    minArgs = 1,
    maxArgs = 1,
    permission = "cafe.warp",
) {
    override fun CommandContext.onCommand() {
        val warpName = args[0]

    }
}