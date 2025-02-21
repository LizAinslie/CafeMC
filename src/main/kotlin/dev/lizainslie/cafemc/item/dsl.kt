package dev.lizainslie.cafemc.item

import dev.lizainslie.cafemc.chat.ComponentDsl
import dev.lizainslie.cafemc.util.ItemUtils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.components.CustomModelDataComponent

class CustomItemDsl(private val material: Material) {
    private var name = ItemUtils.getDefaultName(material)
    private var lore = mutableListOf<Component>()
    
    var glint = false
    var unbreakable = false
    
    private val itemStackExtensions = mutableListOf<ItemStack.() -> Unit>()
    private val itemMetaExtensions = mutableListOf<ItemMeta.() -> Unit>()
    
    private val enchantments = mutableListOf<EnchantDsl>()
    
    private var customModelDataInt: Int? = null
    private var customModelDataComponentDsl: (CustomModelDataDsl.() -> Unit)? = null
    
    fun customModelData(value: Int) {
        if (customModelDataComponentDsl != null)
            throw IllegalStateException("Cannot set both customModelData and customModelDataComponent")
        
        customModelDataInt = value
    }
    
    fun customModelData(block: CustomModelDataDsl.() -> Unit) {
        if (customModelDataInt != null)
            throw IllegalStateException("Cannot set both customModelData and customModelDataComponent")
        
        customModelDataComponentDsl = block
    }
    
    fun name(block: ComponentDsl.() -> Unit) {
        name = ComponentDsl().apply(block).component
    }
    
    fun loreLine(block: ComponentDsl.() -> Unit) {
        lore += ComponentDsl().apply(block).component
    }
    
    fun enchant(enchantment: Enchantment, level: Int = 1, block: EnchantDsl.() -> Unit = {}) {
        val enchantDsl = EnchantDsl(enchantment, level).apply(block)
        enchantments += enchantDsl
    }
    
    fun extendItemStack(block: ItemStack.() -> Unit) {
        itemStackExtensions += block
    }
    
    fun extendItemMeta(block: ItemMeta.() -> Unit) {
        itemMetaExtensions += block
    }
    
    fun createItem(amount: Int) = ItemStack(material, amount).apply {
        lore(this@CustomItemDsl.lore)
        
        editMeta { meta ->
            meta.displayName(this@CustomItemDsl.name)
            
            if (glint) meta.setEnchantmentGlintOverride(true)
            meta.isUnbreakable = unbreakable
            
            customModelDataInt?.let { meta.setCustomModelData(it) }
            customModelDataComponentDsl?.let { meta.customModelData(it) }
            
            itemMetaExtensions.forEach { meta.it() }
        }
        
        this@CustomItemDsl.enchantments.forEach { 
            if (it.unsafe) addUnsafeEnchantment(it.enchantment, it.level)
            else addEnchantment(it.enchantment, it.level)
        }
        
        itemStackExtensions.forEach { it() }
    }
}

fun customItem(material: Material, block: CustomItemDsl.() -> Unit) = CustomItemDsl(material).apply(block)

class EnchantDsl(val enchantment: Enchantment, val level: Int = 1) {
    var unsafe = false
}

fun ItemStack.enchant(enchantment: Enchantment, level: Int = 1, block: EnchantDsl.() -> Unit = {}) {
    val enchantDsl = EnchantDsl(enchantment, level).apply(block)
    if (enchantDsl.unsafe) addUnsafeEnchantment(enchantDsl.enchantment, enchantDsl.level)
    else addEnchantment(enchantDsl.enchantment, enchantDsl.level)
}

// So much experimental stuff below this line, but we move fast and break things in this house
class CustomModelDataDsl(cmp: CustomModelDataComponent? = null) {
    val floats = cmp?.floats?.toMutableList() ?: mutableListOf()
    val flags = cmp?.flags?.toMutableList() ?: mutableListOf()
    val strings = cmp?.strings?.toMutableList() ?: mutableListOf()
    val colors = cmp?.colors?.toMutableList() ?: mutableListOf()
    
    fun applyToComponent(cmp: CustomModelDataComponent) {
        cmp.floats = floats
        cmp.flags = flags
        cmp.strings = strings
        cmp.colors = colors
    }
}

fun ItemMeta.customModelData(block: CustomModelDataDsl.() -> Unit) {
    val cmp = customModelDataComponent
    CustomModelDataDsl(cmp).apply(block).applyToComponent(cmp)
    setCustomModelDataComponent(cmp)
}
// phew

@Suppress("unused")
fun dslExample() {
    customItem(Material.DIAMOND_SWORD) {
        glint = true
        unbreakable = true
        
        name {
            text("Custom Diamond Sword") {
                color = TextColor.color(0x00FF00)
            }
        }
        
        loreLine {
            text("This is a custom diamond sword") {
                color = TextColor.color(0x00FFFF)
            }
        }
        
        enchant(Enchantment.FIRE_ASPECT, 2)
        
        enchant(Enchantment.FORTUNE, 1000) {
            unsafe = true
        }
        
        customModelData(12345)
        
        customModelData { // experimental API
            floats += 1.0f
            flags += true
            strings += "hello"
            colors += Color.RED
        }
        
        extendItemStack {}
        
        extendItemMeta {}
    }
}