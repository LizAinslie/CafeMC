package dev.lizainslie.cafemc.chat

import dev.lizainslie.cafemc.afk.AfkMap
import dev.lizainslie.cafemc.data.player.PlayerSettings
import org.bukkit.ChatColor
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.jetbrains.exposed.sql.transactions.transaction

object ChatHandler : Listener {
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
        val afkStatus = if (AfkMap.isAfk(event.player)) "${ChatColor.BOLD}AFK${ChatColor.RESET} " else ""

        event.message = ChatColor.translateAlternateColorCodes('&', event.message)

        event.format = "${ChatColor.GRAY}[$afkStatus${ChatColor.GOLD}%1\$s${ChatColor.GRAY}]${ChatColor.RESET} %2\$s"
    }
}