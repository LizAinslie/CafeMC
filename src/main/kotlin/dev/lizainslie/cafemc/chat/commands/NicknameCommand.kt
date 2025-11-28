package dev.lizainslie.cafemc.chat.commands

import dev.lizainslie.cafemc.chat.ChatUtil
import dev.lizainslie.cafemc.chat.sendRichMessage
import dev.lizainslie.cafemc.core.cmd.AllowedSender
import dev.lizainslie.cafemc.core.cmd.CommandContext
import dev.lizainslie.cafemc.core.cmd.PluginCommand
import dev.lizainslie.cafemc.data.player.PlayerSettings
import net.kyori.adventure.text.format.NamedTextColor
import org.jetbrains.exposed.sql.transactions.transaction

object NicknameCommand : PluginCommand(
    command = "nickname",
    usage = "[nickname]",
    permission = "cafe.nickname",
    allowedSender = AllowedSender.PLAYER,
    minArgs = 0,
    maxArgs = -1,
    aliases = listOf("nick")
) {
    override fun CommandContext.onCommand() {
        if (args.isEmpty()) {
            transaction {
                val playerSettings = PlayerSettings.find(player) ?: return@transaction
                playerSettings.nickname = null
            }

            player.sendRichMessage {
                text("Your nickname has been reset.") {
                    color = NamedTextColor.GRAY
                }
            }
        } else {
            val nickname = args.joinToString(" ")

            transaction {
                val playerSettings = PlayerSettings.findOrCreate(player)
                playerSettings.nickname = nickname
            }

            player.sendRichMessage {
                text("Your nickname has been set to ") {
                    color = NamedTextColor.GRAY
                }
                text(ChatUtil.translateAmpersand(nickname)) {
                    bold = true
                }
                text(".") {
                    color = NamedTextColor.GRAY
                }
            }
        }
    }
}