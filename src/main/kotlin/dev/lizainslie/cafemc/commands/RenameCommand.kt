package dev.lizainslie.cafemc.commands

import dev.lizainslie.cafemc.chat.ChatUtil
import dev.lizainslie.cafemc.chat.sendRichMessage
import dev.lizainslie.cafemc.util.ItemUtils
import dev.lizainslie.cafemc.core.cmd.AllowedSender
import dev.lizainslie.cafemc.core.cmd.CommandContext
import dev.lizainslie.cafemc.core.cmd.PluginCommand
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player

object RenameCommand : PluginCommand(
    command = "rename",
    description = "Rename the item in your hand",
    usage = "<name>",
    allowedSender = AllowedSender.PLAYER,
    minArgs = 1,
    maxArgs = -1,
    permission = "cafe.rename",
) {
    override fun CommandContext.onCommand() {
        val player = sender as Player
        val item = player.inventory.itemInMainHand
        val meta = item.itemMeta ?: return

        val defaultName = ItemUtils.getDefaultName(item.type)
        val displayName =
            if (args.isEmpty()) defaultName
            else ChatUtil.translateAmpersand(args.joinToString(" "))

        meta.displayName(displayName)
        item.itemMeta = meta

//        player.sendMessage("${ChatColor.GRAY}Your ${ChatColor.AQUA}$defaultName${ChatColor.GRAY} has been renamed to $displayName.")
        player.sendRichMessage { 
            text("Your ") { color = NamedTextColor.GRAY }
            text(defaultName) { color = NamedTextColor.AQUA }
            text(" has been renamed to ") { color = NamedTextColor.GRAY }
            text(displayName)
            text(".") { color = NamedTextColor.GRAY }
        }
    }
}