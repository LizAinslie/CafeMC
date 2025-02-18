package dev.lizainslie.cafemc.chat

import dev.lizainslie.cafemc.afk.AfkModule
import dev.lizainslie.cafemc.chat.commands.TestComponentCommand
import dev.lizainslie.cafemc.core.PluginModule
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.SignChangeEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object ChatModule : PluginModule(), Listener {
    
//    init {
//        commands += TestComponentCommand
//    }
    
    // region Event Handlers
    
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.joinMessage = "${ChatColor.GREEN}${ChatColor.BOLD}[+]${ChatColor.RESET} ${ChatColor.GRAY}${event.player.displayName}"
    }

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
        event.quitMessage = "${ChatColor.RED}${ChatColor.BOLD}[-]${ChatColor.RESET} ${ChatColor.GRAY}${event.player.displayName}"
    }

    @EventHandler
    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        val afkStatus = if (AfkModule.isAfk(event.player)) "${ChatColor.BOLD}AFK${ChatColor.RESET} " else ""

        event.message = ChatColor.translateAlternateColorCodes('&', event.message)

        event.format = "${ChatColor.GRAY}[$afkStatus${ChatColor.GOLD}%1\$s${ChatColor.GRAY}]${ChatColor.RESET} %2\$s"
    }
    
    @EventHandler
    fun onSignChange(event: SignChangeEvent) {
        event.getLine(0)?.let { event.setLine(0, ChatColor.translateAlternateColorCodes('&', it)) }
        event.getLine(1)?.let { event.setLine(1, ChatColor.translateAlternateColorCodes('&', it)) }
        event.getLine(2)?.let { event.setLine(2, ChatColor.translateAlternateColorCodes('&', it)) }
        event.getLine(3)?.let { event.setLine(3, ChatColor.translateAlternateColorCodes('&', it)) }
    }
    
    // endregion
}