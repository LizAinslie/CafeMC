package dev.lizainslie.cafemc.teleport

import dev.lizainslie.cafemc.CafeMC
import dev.lizainslie.cafemc.chat.sendRichError
import dev.lizainslie.cafemc.chat.sendRichMessage
import dev.lizainslie.cafemc.data.location.SavedLocation
import dev.lizainslie.cafemc.data.player.PlayerSettings
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import org.jetbrains.exposed.sql.transactions.transaction

fun Player.saveLastLocation(from: Location = location) {
    if (hasPermission("cafe.tpa.back"))
        transaction {
            val settings = PlayerSettings.findOrCreate(this@saveLastLocation)
            
            if (settings.lastLocation != null) {
                settings.lastLocation!!.delete()
                settings.lastLocation = null
            }
            
            settings.lastLocation = SavedLocation.findOrCreate(from)
            
            sendRichMessage { 
                text("Last location saved. ") { color = NamedTextColor.GRAY }
                text("[Go Back]") {
                    color = NamedTextColor.GOLD

                    events {
                        click = ClickEvent.callback {
                            goToLastLocation()
                        }
                    }
                }
            }
        }
}

internal fun Player.goToLastLocation() {
    val me = this
    transaction {
        val settings = PlayerSettings.find(me)
        settings?.lastLocation?.let {
            val location = it.location // get a Bukkit location

            // Delete the previous location in db
            it.delete()
            settings.lastLocation = null

            // Set metadata to prevent teleporting back again
            me.setMetadata("teleporting_back", FixedMetadataValue(CafeMC.instance, true))

            // Teleport the player to the previous location
            me.teleport(location)

            me.sendRichMessage {
                text("Teleported to your previous location.") { color = NamedTextColor.GRAY }
            }
        } ?: me.sendRichError { text("No previous location saved.") }
    }
}