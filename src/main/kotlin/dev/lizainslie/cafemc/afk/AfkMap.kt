package dev.lizainslie.cafemc.afk

import dev.lizainslie.cafemc.chat.broadcast
import me.neznamy.tab.api.TabAPI
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object AfkMap {
    private val afkMap = mutableMapOf<Player, Boolean>()

    fun toggleAfk(player: Player) {
        val newAfkStatus = !isAfk(player)

        afkMap[player] = newAfkStatus

        // Ignore player for sleep checks
        player.isSleepingIgnored = newAfkStatus

        // Update Tab List
        val tab = TabAPI.getInstance()
        val tabPlayer = tab.getPlayer(player.uniqueId) ?: return
        (tab.tabListFormatManager ?: return).setPrefix(tabPlayer, if (newAfkStatus) "${ChatColor.GRAY}[AFK]${ChatColor.RESET} " else null)

        // Send message
        player.sendMessage("${ChatColor.GRAY}You are ${if (newAfkStatus) "now" else "no longer"} AFK.")

        // Broadcast message
        broadcast("${ChatColor.GOLD}${player.displayName}${ChatColor.GRAY} is ${if (newAfkStatus) "now" else "no longer"} AFK.") {
            it != player // Don't send to the player who toggled AFK
        }
    }

    fun isAfk(player: Player): Boolean {
        return afkMap[player] ?: false
    }
}