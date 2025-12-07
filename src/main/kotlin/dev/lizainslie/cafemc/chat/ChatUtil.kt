package dev.lizainslie.cafemc.chat

import dev.lizainslie.cafemc.util.DiscordUtils
import dev.lizainslie.cafemc.util.EmbedBuilderDsl
import github.scarsz.discordsrv.DiscordSRV
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed
import github.scarsz.discordsrv.util.DiscordUtil
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object ChatUtil {
    /**
     * Broadcast a message to all online players excluding those that do not pass the [filter].
     *
     * @param sendToDiscord Optionally send the message to Discord as well.
     */
    fun broadcast(sendToDiscord: Boolean = true, filter: (player: Player) -> Boolean = { true }, block: ComponentDsl.() -> Unit) {
        if(sendToDiscord) broadcastTextToDiscord(component(block).toPlainText())
        
        Bukkit.getServer().onlinePlayers.filter(filter).forEach {
            it.sendRichMessage(block)
        }
    }

    /**
     * Broadcast a [message] to all online players excluding those that do not pass the [filter].
     * 
     * @param sendToDiscord Optionally send the message to Discord as well.
     */
    fun broadcast(message: String, sendToDiscord: Boolean = true, filter: (player: Player) -> Boolean = { true }) {
        if (sendToDiscord) broadcastTextToDiscord(message)

        Bukkit.getServer().onlinePlayers.filter(filter).forEach {
            it.sendMessage(message)
        }
    }
    
    fun broadcast(message: String, filter: (player: Player) -> Boolean) = 
        broadcast(message, true, filter)
    
    fun broadcast(filter: (player: Player) -> Boolean, block: ComponentDsl.() -> Unit) = 
        broadcast(true, filter, block)
    
    fun broadcastTextToDiscord(message: String) {
        DiscordUtil.sendMessage(DiscordSRV.getPlugin().mainTextChannel, message)
    }
    
    fun broadcastEmbedToDiscord(embed: MessageEmbed) {
        DiscordSRV.getPlugin().mainTextChannel.sendMessageEmbeds(embed).queue()
    }
    
    fun broadcastEmbedToDiscord(builder: EmbedBuilderDsl.() -> Unit) {
        broadcastEmbedToDiscord(DiscordUtils.buildEmbed(builder))
    }
    
    fun sendError(audience: Audience, message: String) {
        audience.sendRichMessage { 
            text("Error:") {
                color = NamedTextColor.RED
                bold = true
            }
            
            space()
            
            text(message) {
                color = NamedTextColor.GRAY
            }
        }
    }
    
    fun sendRichError(audience: Audience, block: ComponentDsl.() -> Unit) {
        audience.sendRichMessage { 
            text("Error:") {
                color = NamedTextColor.RED
                bold = true
            }
            
            space()
            
            component(component(block))
        }
    }
    
    fun translateAmpersand(message: String) = 
        LegacyComponentSerializer.legacyAmpersand().deserialize(message)
    
    fun translateAmpersand(message: Component) = 
        LegacyComponentSerializer.legacyAmpersand().deserialize(message.toPlainText())
}

fun Component.toPlainText() = PlainTextComponentSerializer.plainText().serialize(this)

fun Audience.sendError(message: String) = ChatUtil.sendError(this, message)
fun Audience.sendRichError(block: ComponentDsl.() -> Unit) = ChatUtil.sendRichError(this, block)