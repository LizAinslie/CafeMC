package dev.lizainslie.cafemc.teleport.commands

import dev.lizainslie.cafemc.core.cmd.AllowedSender
import dev.lizainslie.cafemc.core.cmd.CommandContext
import dev.lizainslie.cafemc.core.cmd.PluginCommand
import dev.lizainslie.cafemc.teleport.goToLastLocation

object BackCommand : PluginCommand(
    command = "back",
    description = "Teleport to your previous location",
    usage = "/back",
    allowedSender = AllowedSender.PLAYER,
    permission = "cafe.tpa.back",
) {
    override fun CommandContext.onCommand() {
        player.goToLastLocation()
    }
}