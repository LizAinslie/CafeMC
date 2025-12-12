package dev.lizainslie.cafemc.chat.commands

import dev.lizainslie.cafemc.chat.ChatUtil
import dev.lizainslie.cafemc.chat.nms.NicknameUtil
import dev.lizainslie.cafemc.chat.sendRichMessage
import dev.lizainslie.cafemc.chat.toPlainText
import dev.lizainslie.cafemc.chat.validateNickname
import dev.lizainslie.cafemc.core.cmd.AllowedSender
import dev.lizainslie.cafemc.core.cmd.CommandContext
import dev.lizainslie.cafemc.core.cmd.PluginCommand
import dev.lizainslie.cafemc.core.modules.OnlinePlayerCacheModule
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

            OnlinePlayerCacheModule.refreshPlayerSettings(player)
            NicknameUtil.updateNickname(player, null)

            player.sendRichMessage {
                text("Your nickname has been reset.") {
                    color = NamedTextColor.GRAY
                }
            }
        } else {
            val nickname = args.joinToString(" ")
            val nicknameComponent = ChatUtil.translateAmpersand(nickname)

            if (!validateNickname(nicknameComponent)) {
                sendError("Nickname must be 16 characters or shorter (not including color codes)")
                return
            }

            transaction {
                val playerSettings = PlayerSettings.findOrCreate(player)
                playerSettings.nickname = nickname
            }

            OnlinePlayerCacheModule.refreshPlayerSettings(player)
            NicknameUtil.updateNickname(player, nicknameComponent)

            player.sendRichMessage {
                text("Your nickname has been set to ") {
                    color = NamedTextColor.GRAY
                }
                component(nicknameComponent)
                text(".") {
                    color = NamedTextColor.GRAY
                }
            }
        }
    }
}