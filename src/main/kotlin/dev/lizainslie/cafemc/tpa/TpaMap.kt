package dev.lizainslie.cafemc.tpa

import dev.lizainslie.cafemc.CafeMC
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object TpaMap {
    private val requests = mutableListOf<TpaRequest>()

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

        sender.sendMessage("${ChatColor.GRAY}Sent a teleport request to ${ChatColor.GOLD}${target.displayName}${ChatColor.GRAY}.")

        Bukkit.getScheduler().runTaskAsynchronously(CafeMC.instance) { _ ->
            Thread.sleep(120000) // 2 minutes

            removeRequest(sender, target)
            sender.sendMessage("${ChatColor.GRAY}Teleport request to ${ChatColor.GOLD}${target.displayName}${ChatColor.GRAY} has expired.")
        }
    }

    fun getRequest(target: Player) = requests.find { it.target == target }
    fun getRequest(sender: Player, target: Player) = requests.find { it.sender == sender && it.target == target }
    
    fun getRequestsTo(target: Player) = requests.filter { it.target == target }

    fun removeRequest(sender: Player, target: Player) {
        requests.removeIf { it.sender == sender && it.target == target }
    }
}