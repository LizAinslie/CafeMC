package dev.lizainslie.cafemc.core.modules

import dev.lizainslie.cafemc.core.PluginModule
import dev.lizainslie.cafemc.core.data.CachedPlayer
import dev.lizainslie.cafemc.data.player.PlayerSettings
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.UUID

object OnlinePlayerCacheModule : PluginModule(), Listener {
    val cachedUsers = mutableMapOf<UUID, CachedPlayer>()
    val log: Logger = LoggerFactory.getLogger(javaClass)

    @EventHandler(priority = EventPriority.LOWEST) // this should run before EVERYTHING else
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val player = e.player
        log.info("Player joined: ${player.name} (${player.uniqueId}). Adding to cache")

        val playerSettings = transaction {
            PlayerSettings.find(player)
        }

        cachedUsers[player.uniqueId] = CachedPlayer(
            realName = player.name,
            settings = playerSettings
        )
        log.info("Cache entry: {}", cachedUsers[player.uniqueId])
    }

    @EventHandler(priority = EventPriority.HIGHEST) // cleanup can happen after everything else
    fun onPlayerQuit(e: PlayerQuitEvent) {
        val player = e.player
        log.info("Player left: ${player.name} (${player.uniqueId}). Removing from cache")
        cachedUsers.remove(player.uniqueId)
    }

    fun getPlayer(uuid: UUID) = cachedUsers[uuid]
    fun getPlayer(player: Player) = getPlayer(player.uniqueId)

    operator fun get(uuid: UUID) = getPlayer(uuid)
    operator fun get(player: Player) = getPlayer(player)

    fun refreshPlayerSettings(uuid: UUID) {
        log.info("refreshing player settings for $uuid")
        cachedUsers[uuid]?.settings = transaction {
            PlayerSettings.find(uuid)
        }

        log.info("refreshed! cache entry: {}", cachedUsers[uuid])
    }

    fun refreshPlayerSettings(player: Player) = refreshPlayerSettings(player.uniqueId)
}