package dev.lizainslie.cafemc.item.commands

import dev.lizainslie.cafemc.core.cmd.CommandContext
import dev.lizainslie.cafemc.core.cmd.PluginCommand
import dev.lizainslie.cafemc.item.CustomItemsModule
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minecraft.ChatFormatting
import org.bukkit.Bukkit

object GiveItemCommand : PluginCommand(
    command = "giveitem",
    description = "Give a custom item to a player",
    usage = "<item> [amount] [player]",
    minArgs = 1,
    maxArgs = 3,
    permission = "cafe.giveitem",
) {
    override fun CommandContext.onCommand() {
        val itemId = args[0]
        val amount = args.getOrNull(1)?.toIntOrNull() ?: 1
        val target = args.getOrNull(2)?.let { Bukkit.getPlayer(it) } ?: player
        
        val item = CustomItemsModule.getItem(itemId) ?: run {
            sendError("Item $itemId not found")
            return
        }

        target.inventory.addItem(item.create(amount))
        
        val itemText = Component
            .text("${amount}x", NamedTextColor.BLUE)
            .appendSpace()
            .append(Component.text(item.name))
        
        if (target == player) player.sendMessage(Component
            .text("Gave you ")
            .append(itemText)
            .append(Component.text(".", NamedTextColor.GRAY))
        )
        else {
//            target.sendMessage("${ChatFormatting.GOLD}${player.displayName()} gave you ${player.displayName}.")
            
            target.sendMessage(
                player
                    .displayName().color(NamedTextColor.GOLD)
                    .append(Component.text(" gave you ", NamedTextColor.GRAY))
                    .append(itemText)
                    .append(Component.text(".", NamedTextColor.GRAY))
            )
        }
    }
}