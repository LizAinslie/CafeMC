package dev.lizainslie.cafemc.teleport

import dev.lizainslie.cafemc.CafeMC
import dev.lizainslie.cafemc.chat.nicknameOrDisplayName
import dev.lizainslie.cafemc.chat.sendRichError
import dev.lizainslie.cafemc.chat.sendRichMessage
import dev.lizainslie.cafemc.core.PluginModule
import dev.lizainslie.cafemc.teleport.commands.*
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerTeleportEvent

object TeleportModule : PluginModule(), Listener {
    private val requests = mutableListOf<TpaRequest>()
    
    private const val REQUEST_TIMEOUT = 120000L // 2 minutes
    
    init {
        // Register commands
        commands += TpaCommand
        commands += HomeCommand
        commands += BackCommand
    }
    
    // region Event Handlers
    
    @EventHandler
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        if (event.player.hasPermission("cafe.tpa.back") && event.cause == PlayerTeleportEvent.TeleportCause.COMMAND) {
            val player = event.player
            
            // Check if player is teleporting back already and ignore
            if (player.hasMetadata("teleporting_back")) {
                player.removeMetadata("teleporting_back", CafeMC.instance)
                return
            }
            
            // Save last location
            player.saveLastLocation(event.from)
        }
    }
    
    // endregion

    
    
    // region Public API
    
    /**
     * Add a teleport request from [sender] to [target]
     */
    fun addRequest(sender: Player, target: Player) {
        requests += TpaRequest(sender, target)
        
        target.sendRichMessage { 
            text(sender.nicknameOrDisplayName()) { color = NamedTextColor.GOLD }
            text(" has requested to teleport to you. ") { color = NamedTextColor.GRAY }
            text("[Accept]") { 
                color = NamedTextColor.GREEN
                events { 
                    click = ClickEvent.callback {
                        getRequest(sender, target)?.let { request ->
                            target.sendRichMessage {
                                text("Accepted teleport request from ") { color = NamedTextColor.GRAY }
                                text(request.sender.nicknameOrDisplayName()) { color = NamedTextColor.GOLD }
                                text(".") { color = NamedTextColor.GRAY }
                            }
                            
                            sender.sendRichMessage { 
                                text(target.nicknameOrDisplayName()) { color = NamedTextColor.GOLD }
                                text(" has accepted your teleport request, teleporting you there.") { color = NamedTextColor.GRAY }
                            }
                            
                            removeRequest(sender, target)
                            sender.teleport(target)
                        } ?: run {
                            target.sendRichError {
                                text("Teleport request from ") { color = NamedTextColor.GRAY }
                                text(sender.nicknameOrDisplayName()) { color = NamedTextColor.GOLD }
                                text(" has expired.") { color = NamedTextColor.GRAY }
                            }
                        }
                    }
                }
            }
            text(" or ") { color = NamedTextColor.GRAY }
            text("[Deny]") { 
                color = NamedTextColor.RED
                events { 
                    click = ClickEvent.callback {
                        getRequest(sender, target)?.let { request ->
                            target.sendRichMessage {
                                text("Denied teleport request from ") { color = NamedTextColor.GRAY }
                                text(request.sender.nicknameOrDisplayName()) { color = NamedTextColor.GOLD }
                                text(".") { color = NamedTextColor.GRAY }
                            }
                            
                            sender.sendRichMessage { 
                                text(target.nicknameOrDisplayName()) { color = NamedTextColor.GOLD }
                                text(" has denied your teleport request.") { color = NamedTextColor.GRAY }
                            }
                            
                            removeRequest(sender, target)
                        } ?: run {
                            target.sendRichError {
                                text("Teleport request from ") { color = NamedTextColor.GRAY }
                                text(sender.nicknameOrDisplayName()) { color = NamedTextColor.GOLD }
                                text(" has expired.") { color = NamedTextColor.GRAY }
                            }
                        }
                    }
                }
            }
        }

        // Send confirmation to sender
        sender.sendRichMessage { 
            text("Sent a teleport request to ") { color = NamedTextColor.GRAY }
            text(target.nicknameOrDisplayName()) { color = NamedTextColor.GOLD }
            text(".") { color = NamedTextColor.GRAY }
        }

        Bukkit.getScheduler().runTaskAsynchronously(CafeMC.instance) { _ ->
            Thread.sleep(REQUEST_TIMEOUT)

            // Check if request still exists & remove it then notify sender that it expired
            if (getRequest(sender, target) != null) {
                removeRequest(sender, target)
                sender.sendRichMessage { 
                    text("Teleport request to ") { color = NamedTextColor.GRAY }
                    text(target.nicknameOrDisplayName()) { color = NamedTextColor.GOLD }
                    text(" has expired.") { color = NamedTextColor.GRAY }
                }
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