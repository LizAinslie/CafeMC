package dev.lizainslie.cafemc.chat

import github.scarsz.discordsrv.DiscordSRV
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed
import github.scarsz.discordsrv.util.DiscordUtil
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object ChatUtil {

    /**
     * Broadcast a [message] to all online players excluding those that do not pass the [filter].
     *
     * @param sendToDiscord Optionally send the message to Discord as well.
     */
    fun broadcast(message: BaseComponent, sendToDiscord: Boolean = true, filter: (player: Player) -> Boolean = { true }) {
        broadcastTextToDiscord(message.toPlainText())
        
        Bukkit.getServer().onlinePlayers.filter(filter).forEach {
            it.spigot().sendMessage(message)
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
    
    fun broadcast(message: String, filter: (player: Player) -> Boolean) = broadcast(message, true, filter)
    fun broadcast(message: BaseComponent, filter: (player: Player) -> Boolean) = broadcast(message, true, filter)
    
    fun broadcastTextToDiscord(message: String) {
        DiscordUtil.sendMessage(
            DiscordUtil.getTextChannelById(DiscordSRV.getPlugin().mainChatChannel),
            message
        )
    }
    
    fun broadcastEmbedToDiscord(embed: MessageEmbed) {
        DiscordUtil.getTextChannelById(DiscordSRV.getPlugin().mainChatChannel).sendMessageEmbeds(embed).queue()
    }
}