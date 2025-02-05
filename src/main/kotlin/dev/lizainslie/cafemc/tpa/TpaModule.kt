package dev.lizainslie.cafemc.tpa

import dev.lizainslie.cafemc.CafeMC
import dev.lizainslie.cafemc.core.PluginModule
import dev.lizainslie.cafemc.data.location.SavedLocation
import dev.lizainslie.cafemc.data.player.PlayerSettings
import dev.lizainslie.cafemc.tpa.commands.BackCommand
import dev.lizainslie.cafemc.tpa.commands.TpAcceptCommand
import dev.lizainslie.cafemc.tpa.commands.TpDenyCommand
import dev.lizainslie.cafemc.tpa.commands.TpaCommand
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent
import org.jetbrains.exposed.sql.transactions.transaction

object TpaModule : PluginModule(), Listener {
    private val requests = mutableListOf<TpaRequest>()
    
    private const val REQUEST_TIMEOUT = 120000L // 2 minutes
    
    init {
        // Register commands
        commands += TpaCommand
        commands += TpAcceptCommand
        commands += TpDenyCommand
        commands += BackCommand
    }
    
    // region Event Handlers
    
    @EventHandler
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        if (event.player.hasPermission("cafe.tpa.back")) {
            val player = event.player
            val location = event.from
            
            transaction { 
                val settings = PlayerSettings.findOrCreate(player)
                settings.lastLocation = SavedLocation.createFromBukkit(location)
                
                player.sendMessage("${ChatColor.GRAY}Last location saved. Use ${ChatColor.GOLD}/back${ChatColor.GRAY} to return.")
            }
        }
    }
    
    // endregion

    
    
    // region Public API
    
    /**
     * Add a teleport request from [sender] to [target]
     */
    fun addRequest(sender: Player, target: Player) {
        requests += TpaRequest(sender, target)

        // Send request to target
        target.spigot().sendMessage(
            ComponentBuilder(sender.displayName).color(ChatColor.GOLD)
                .append(" has requested to teleport to you. ").color(ChatColor.GRAY)
                .append("[Accept]")
                    .color(ChatColor.GREEN)
                    .event(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept ${sender.name}"))
                    .event(HoverEvent(HoverEvent.Action.SHOW_TEXT, ComponentBuilder("/tpaccept ${sender.name}").create()))
                .append(" or ").color(ChatColor.GRAY)
                .append("[Deny]")
                    .color(ChatColor.RED)
                    .event(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny ${sender.name}"))
                    .event(HoverEvent(HoverEvent.Action.SHOW_TEXT, ComponentBuilder("/tpdeny ${sender.name}").create()))
                .build()
        )

        // Send confirmation to sender
        sender.sendMessage("${ChatColor.GRAY}Sent a teleport request to ${ChatColor.GOLD}${target.displayName}${ChatColor.GRAY}.")

        Bukkit.getScheduler().runTaskAsynchronously(CafeMC.instance) { _ ->
            Thread.sleep(REQUEST_TIMEOUT)

            // Check if request still exists & remove it then notify sender that it expired
            if (getRequest(sender, target) != null) {
                removeRequest(sender, target)
                sender.sendMessage("${ChatColor.GRAY}Teleport request to ${ChatColor.GOLD}${target.displayName}${ChatColor.GRAY} has expired.")
            }
        }
    }

    /**
     * Get the first request from any sender to [target]
     */
    fun getRequest(target: Player) = requests.find { it.target == target }
    
    /**
     * Get the request from [sender] to [target]
     */
    fun getRequest(sender: Player, target: Player) = requests.find { it.sender == sender && it.target == target }
    
    /**
     * Get all requests to [target]
     */
    fun getRequestsTo(target: Player) = requests.filter { it.target == target }

    /** 
     * Remove request from [sender] to [target]
     */
    fun removeRequest(sender: Player, target: Player) {
        requests.removeIf { it.sender == sender && it.target == target }
    }
    
    // endregion
}