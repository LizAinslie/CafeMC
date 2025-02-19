package dev.lizainslie.cafemc.item

import dev.lizainslie.cafemc.chat.sendError
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface CustomItemUseResult {
    
    fun apply(player: Player, itemStack: ItemStack)
    
    data object None : CustomItemUseResult {
        override fun apply(player: Player, itemStack: ItemStack) {}
    }
    
    data object ConsumeItem : CustomItemUseResult {
        override fun apply(player: Player, itemStack: ItemStack) {
            if (itemStack.amount == 1) player.inventory.removeItem(itemStack)
            else itemStack.amount--
        }
    }
    
    data class Error(val message: String) : CustomItemUseResult {
        override fun apply(player: Player, itemStack: ItemStack) {
            player.sendError(message)
        }
    }
}