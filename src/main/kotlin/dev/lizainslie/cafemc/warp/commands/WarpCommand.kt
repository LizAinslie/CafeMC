package dev.lizainslie.cafemc.warp.commands

import dev.lizainslie.cafemc.core.cmd.AllowedSender
import dev.lizainslie.cafemc.core.cmd.CommandContext
import dev.lizainslie.cafemc.core.cmd.PluginCommand

object WarpCommand : PluginCommand(
    command = "warp",
    usage = "/warp <name>",
    permission = "cafe.warp",
    minArgs = 1,
    maxArgs = 1,
    allowedSender = AllowedSender.PLAYER,
) {
    override fun CommandContext.onCommand() {
        val warpName = args[0]

    }
}