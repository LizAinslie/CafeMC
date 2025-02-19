package dev.lizainslie.cafemc.util

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object ItemUtils {
    fun getDefaultName(type: Material) = ItemStack(type).itemMeta?.displayName 
        ?: TextUtils.capitalizeEnumConstant(type.name)
    
}

fun Player.giveItem(item: ItemStack) {
    val result = inventory.addItem(item)

    if (result.isNotEmpty())
        result.values.forEach {
            world.dropItem(location, it)
        }
}
