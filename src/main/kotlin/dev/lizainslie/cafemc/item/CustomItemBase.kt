package dev.lizainslie.cafemc.item

import dev.lizainslie.cafemc.util.ItemUtils
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

/**
 * Base class for custom item definitions.
 */
abstract class CustomItemBase(
    val id: String,
    val material: Material,
    val name: String = ItemUtils.getDefaultName(material),
    val lore: List<String> = emptyList(),
    val glint: Boolean = false,
    val canPlace: Boolean = true,
) {
    /**
     * Create a new ItemStack of this custom item.
     * 
     * @param amount The amount of items to create.
     */
    open fun create(amount: Int): ItemStack {
        val stack = ItemStack(material, amount).apply {
            itemMeta = itemMeta?.apply {
                setDisplayName(name)
                lore = this@CustomItemBase.lore

                if (glint) setEnchantmentGlintOverride(true)

                modifyMeta()
            }

            modifyStack()
        }
        
//        val craftStack = net.minecraft.world.item.ItemStack.fromBukkitCopy(stack)
        
        return stack
    }

    /**
     * 
     */
    open fun ItemStack.modifyStack() {}
    
    open fun ItemMeta.modifyMeta() {}

    /**
     * 
     */
    open fun canUseOnBlock(block: Block) = false

    open fun useOnBlock(player: Player,  block: Block): CustomItemUseResult = CustomItemUseResult.None

    /**
     * Check if the given ItemStack is a valid instance of this custom item.
     */
    open fun checkStack(stack: ItemStack): Boolean = stack.type == material
}