package dev.lizainslie.cafemc.chat.commands

import dev.lizainslie.cafemc.ItemUtils
import dev.lizainslie.cafemc.chat.AllowedSender
import dev.lizainslie.cafemc.chat.PluginCommand
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object RenameCommand : PluginCommand(
    command = "rename",
    description = "Rename the item in your hand",
    usage = "/rename <name>",
    allowedSender = AllowedSender.PLAYER,
    minArgs = 1,
    maxArgs = -1
) {
    override fun onCommand(args: List<String>) {
        val player = sender as Player
        val item = player.inventory.itemInMainHand
        val meta = item.itemMeta ?: return

        val defaultName = ItemUtils.getDefaultName(item.type)
        val displayName =
            if (args.isEmpty()) defaultName
            else ChatColor.translateAlternateColorCodes('&', args.joinToString(" "))

        meta.setDisplayName(displayName)
        item.itemMeta = meta

        player.sendMessage("${ChatColor.GRAY}Your ${ChatColor.AQUA}$defaultName${ChatColor.GRAY} has been renamed to $displayName.")
    }
}