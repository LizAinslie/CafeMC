package dev.lizainslie.cafemc.chat

import dev.lizainslie.cafemc.afk.AfkModule
import dev.lizainslie.cafemc.chat.commands.TestComponentCommand
import dev.lizainslie.cafemc.core.PluginModule
import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.SignChangeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object ChatModule : PluginModule(), Listener {
    
    init {
        commands += TestComponentCommand
    }
    
    // region Event Handlers
    
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.joinMessage(component { 
            text("[+]") {
                color = NamedTextColor.GREEN
                bold = true
            }
            
            text(" ")
            
            text(event.player.displayName()) {
                color = NamedTextColor.GRAY
            }
        })
    }

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
        event.quitMessage(component { 
            text("[-]") {
                color = NamedTextColor.RED
                bold = true
            }
            
            text(" ")
            
            text(event.player.displayName()) {
                color = NamedTextColor.GRAY
            }
        })
    }

    @EventHandler
    fun onPlayerChat(event: AsyncChatEvent) {
        event.renderer { sender, senderDisplayName, message, viewer ->
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

                text(senderDisplayName) { color = NamedTextColor.GOLD }
                text("] ") { color = NamedTextColor.GRAY }

                text(ChatUtil.translateAmpersand(message))
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