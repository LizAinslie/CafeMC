package dev.lizainslie.cafemc.util

import net.kyori.adventure.text.format.TextColor
import org.bukkit.Material

object ItemMaps {
    enum class Valuables(
        val item: Material,
        val block: Material,
        val itemValue: Double,
        private val baseName: String,
        val color: TextColor
    ) {
        Diamond(Material.DIAMOND, Material.DIAMOND_BLOCK, 1000.0, "Diamond", TextColor.color(0x4bede6)),
        Emerald(Material.EMERALD, Material.EMERALD_BLOCK, 500.0, "Emerald", TextColor.color(0x17c544)),
        Gold(Material.GOLD_INGOT, Material.GOLD_BLOCK, 250.0, "Gold", TextColor.color(0xf5cc27)),
        Copper(Material.COPPER_INGOT, Material.COPPER_BLOCK, 100.0, "Copper", TextColor.color(0xc67355)),
        Iron(Material.IRON_INGOT, Material.IRON_BLOCK, 50.0, "Iron", TextColor.color(0xe7e7e7))
        ;
        
        val blockValue = itemValue * 9
        
        val itemDisplayName get() = when (this) {
            Diamond, Emerald -> baseName
            Gold, Copper, Iron -> "$baseName Ingot"
        }
        
        val blockDisplayName = "$baseName Block"
        
        fun getDisplayNameFor(material: Material) = when (material) {
            item -> itemDisplayName
            block -> blockDisplayName
            else -> null
        }
        
        fun getValueFor(material: Material, amount: Int) = (when (material) {
            item -> itemValue
            block -> blockValue
            else -> 0.0
        }) * amount
        
        companion object {
            fun fromMaterial(material: Material) = entries.find { it.item == material || it.block == material }
            
            fun getDisplayName(material: Material) = fromMaterial(material)?.getDisplayNameFor(material)
            
            fun getValue(material: Material, amount: Int = 1) = fromMaterial(material)?.getValueFor(material, amount) ?: 0.0
        }
    }
}