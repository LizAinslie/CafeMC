package dev.lizainslie.cafemc.teleport.commands

import dev.lizainslie.cafemc.chat.sendRichMessage
import dev.lizainslie.cafemc.core.cmd.AllowedSender
import dev.lizainslie.cafemc.core.cmd.CommandContext
import dev.lizainslie.cafemc.core.cmd.PluginCommand
import dev.lizainslie.cafemc.core.modules.OnlinePlayerCacheModule
import dev.lizainslie.cafemc.data.location.SavedLocation
import dev.lizainslie.cafemc.data.player.PlayerSettings
import dev.lizainslie.cafemc.teleport.saveLastLocation
import net.kyori.adventure.text.format.NamedTextColor
import org.jetbrains.exposed.sql.transactions.transaction

internal val SUBCOMMANDS = listOf("set", "clear")

object HomeCommand : PluginCommand(
    command = "home",
    usage = "/home [${SUBCOMMANDS.joinToString("|")}]",
    permission = "cafe.home",
    maxArgs = 1,
    allowedSender = AllowedSender.PLAYER,
) {
    override fun CommandContext.onCommand() {
        val settings = transaction { PlayerSettings.findOrCreate(player) }

        if (args.isEmpty()) {
            val home = transaction { settings.home }
            if (home == null) {
                sendError("You do not have a home set.")
                return
            }
                
            player.saveLastLocation()

            player.teleport(home.location)
            player.sendRichMessage {
                text("Teleported to your home.") { color = NamedTextColor.GRAY }
            }
            return
        }
            
            when (args[0]) {
                "set" -> {
                    transaction { settings.home }?.let {
                        it.delete()
                        settings.home = null
                    }

                    transaction {
                        settings.home = SavedLocation.findOrCreate(player.location)
                    }

                    OnlinePlayerCacheModule.refreshPlayerSettings(player)

                    player.sendRichMessage {
                        text("Home set.") { color = NamedTextColor.GRAY }
                    }
                }

                "clear" -> {
                    if (transaction { settings.home } == null) {
                        sendError("You do not have a home set.")
                        return
                    }

                    transaction {
                        settings.home!!.delete()
                        settings.home = null
                    }

                    OnlinePlayerCacheModule.refreshPlayerSettings(player)

                    player.sendRichMessage {
                        text("Home cleared.") { color = NamedTextColor.GRAY }
                    }
                }

                else -> sendError("Invalid subcommand ${args[0]}.")
            }
        }


    override fun CommandContext.tabComplete(): List<String> =
        when (args.size) {
            0 -> SUBCOMMANDS
            1 -> SUBCOMMANDS.filter { it.startsWith(args[0], ignoreCase = true) }
            else -> emptyList()
        }
}
