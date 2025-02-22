package dev.lizainslie.cafemc.elytra

import dev.lizainslie.cafemc.CafeMC
import dev.lizainslie.cafemc.chat.sendRichMessage
import dev.lizainslie.cafemc.core.PluginModule
import dev.lizainslie.cafemc.elytra.commands.ElytraCommand
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent

private fun enabledOrDisabled(isToggled: Boolean) = if (isToggled) "disabled" else "enabled"

object ElytraModule : PluginModule(), Listener {
    init {
        commands += ElytraCommand
    }
    // Create list for players to be added in the future
    private val elytraBanList= mutableListOf(
        "Notch",
    )

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent){
        if (event.player.isGliding && elytraBanList.contains(event.player.displayName)){
            event.player.sendRichMessage(){
                text("You are currently forbidden to use an Elytra.") { color = NamedTextColor.RED }
            }
            event.player.isGliding = false
        }
    }

    fun toggleElytra(player: Player, toggledPlayer: String){
        if(elytraBanList.contains(toggledPlayer)){
            elytraBanList.remove(toggledPlayer)
        } else {
            elytraBanList.add(toggledPlayer)
        }
        player.sendRichMessage {
            text("You have ${enabledOrDisabled(elytraBanList.contains(toggledPlayer))} ${toggledPlayer}'s Elytra.") { color = NamedTextColor.GRAY }
        }
    }

    // region Module Lifecycle

    override fun register(cafeMC: CafeMC) {
        super.register(cafeMC)
    }
}