package dev.lizainslie.cafemc.teleport.commands

import dev.lizainslie.cafemc.core.cmd.AllowedSender
import dev.lizainslie.cafemc.core.cmd.CommandContext
import dev.lizainslie.cafemc.core.cmd.PluginCommand
import dev.lizainslie.cafemc.teleport.goToLastLocation

object BackCommand : PluginCommand(
    command = "back",
    usage = "/back",
    permission = "cafe.tpa.back",
    allowedSender = AllowedSender.PLAYER,
) {
    override fun CommandContext.onCommand() {
        player.goToLastLocation()
    }
}