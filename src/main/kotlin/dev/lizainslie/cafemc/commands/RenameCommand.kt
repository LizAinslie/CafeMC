package dev.lizainslie.cafemc.commands

import dev.lizainslie.cafemc.chat.ChatUtil
import dev.lizainslie.cafemc.chat.sendRichMessage
import dev.lizainslie.cafemc.util.ItemUtils
import dev.lizainslie.cafemc.core.cmd.AllowedSender
import dev.lizainslie.cafemc.core.cmd.CommandContext
import dev.lizainslie.cafemc.core.cmd.PluginCommand
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player

object RenameCommand : PluginCommand(
    command = "rename",
    usage = "<name>",
    permission = "cafe.rename",
    minArgs = 1,
    maxArgs = -1,
    allowedSender = AllowedSender.PLAYER,
) {
    override fun CommandContext.onCommand() {
        val player = sender as Player
        val item = player.inventory.itemInMainHand
        val meta = item.itemMeta ?: return

        val defaultName = ItemUtils.getDefaultName(item.type)
        val displayName =
            if (args.isEmpty()) defaultName
            else ChatUtil.translateAmpersand(args.joinToString(" "))

        meta.itemName(displayName)
        item.itemMeta = meta

        player.sendRichMessage { 
            text("Your ") { color = NamedTextColor.GRAY }
            text(defaultName) { color = NamedTextColor.AQUA }
            text(" has been renamed to ") { color = NamedTextColor.GRAY }
            text(displayName)
            text(".") { color = NamedTextColor.GRAY }
        }
    }
}