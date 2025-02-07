package dev.lizainslie.cafemc.util

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object ItemUtils {
    fun getDefaultName(type: Material) = ItemStack(type).itemMeta?.displayName
}