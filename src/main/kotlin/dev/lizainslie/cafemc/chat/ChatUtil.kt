package dev.lizainslie.cafemc.chat

import dev.lizainslie.cafemc.util.DiscordUtils
import dev.lizainslie.cafemc.util.EmbedBuilderDsl
import github.scarsz.discordsrv.DiscordSRV
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed
import github.scarsz.discordsrv.util.DiscordUtil
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object ChatUtil {
    val ERROR_PREFIX = "${ChatColor.RED}${ChatColor.BOLD}Error:${ChatColor.RESET}${ChatColor.GRAY}"

    /**
     * Broadcast a [message] to all online players excluding those that do not pass the [filter].
     *
     * @param sendToDiscord Optionally send the message to Discord as well.
     */
    fun broadcast(message: BaseComponent, sendToDiscord: Boolean = true, filter: (player: Player) -> Boolean = { true }) {
        if(sendToDiscord) broadcastTextToDiscord(message.toPlainText())
        
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
        DiscordUtil.sendMessage(DiscordSRV.getPlugin().mainTextChannel, message)
    }
    
    fun broadcastEmbedToDiscord(embed: MessageEmbed) {
        DiscordSRV.getPlugin().mainTextChannel.sendMessageEmbeds(embed).queue()
    }
    
    fun broadcastEmbedToDiscord(builder: EmbedBuilderDsl.() -> Unit) {
        broadcastEmbedToDiscord(DiscordUtils.buildEmbed(builder))
    }
    
    fun sendError(commandSender: CommandSender, message: String) {
        commandSender.sendMessage("$ERROR_PREFIX $message")
    }
}

fun CommandSender.sendError(message: String) = ChatUtil.sendError(this, message)