package dev.lizainslie.cafemc.chat

import dev.lizainslie.cafemc.afk.AfkModule
import dev.lizainslie.cafemc.chat.commands.NicknameCommand
import dev.lizainslie.cafemc.chat.commands.TestComponentCommand
import dev.lizainslie.cafemc.chat.nms.NicknameUtil
import dev.lizainslie.cafemc.core.PluginModule
import dev.lizainslie.cafemc.data.player.PlayerSettings
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.SignChangeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object ChatModule : PluginModule(), Listener {
    val log: Logger = LoggerFactory.getLogger(javaClass)
    init {
        commands += TestComponentCommand
        commands += NicknameCommand
    }
    
    // region Event Handlers
    
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        log.info("player joined ${event.player.name}")
        event.joinMessage(component { 
            text("[+]") {
                color = NamedTextColor.GREEN
                bold = true
            }
            
            text(" ")
            
            component(event.player.nicknameOrDisplayName(NamedTextColor.GRAY))
        })

        val nickname = transaction {
            PlayerSettings.find(event.player)?.nickname
        }

        if (nickname != null) {
            val nicknameComponent = ChatUtil.translateAmpersand(nickname)
            log.info("updating nickname for player ${nicknameComponent.toPlainText()}")
            NicknameUtil.updateNickname(event.player, nicknameComponent)
        }
    }

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
        event.quitMessage(component { 
            text("[-]") {
                color = NamedTextColor.RED
                bold = true
            }
            
            text(" ")
            
            component(event.player.nicknameOrDisplayName(NamedTextColor.GRAY))
        })
    }

    @EventHandler
    fun onPlayerChat(event: AsyncChatEvent) {
        event.renderer { sender, _, message, _ ->
            component {
                text("[") {
                    color = NamedTextColor.GRAY
                }

                if (AfkModule.isAfk(sender)) {
                    text("AFK") {
                        color = NamedTextColor.DARK_GRAY
                        bold = true
                    }

                    text(" ")
                }

                component(sender.nicknameOrDisplayName(NamedTextColor.GOLD))
                text("] ") { color = NamedTextColor.GRAY }

                component(ChatUtil.translateAmpersand(message))
            }
        }
    }
    
    @EventHandler
    fun onSignChange(event: SignChangeEvent) {
        for (lineNum in 0..3) {
            event.line(lineNum, event.line(lineNum)?.let { ChatUtil.translateAmpersand(it) })
        }
//        event.getLine(0)?.let { event.setLine(0, ChatColor.translateAlternateColorCodes('&', it)) }
//        event.getLine(1)?.let { event.setLine(1, ChatColor.translateAlternateColorCodes('&', it)) }
//        event.getLine(2)?.let { event.setLine(2, ChatColor.translateAlternateColorCodes('&', it)) }
//        event.getLine(3)?.let { event.setLine(3, ChatColor.translateAlternateColorCodes('&', it)) }
    }
    
    // endregion
}