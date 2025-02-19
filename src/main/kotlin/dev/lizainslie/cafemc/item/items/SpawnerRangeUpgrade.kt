package dev.lizainslie.cafemc.item.items

import dev.lizainslie.cafemc.CafeMC
import dev.lizainslie.cafemc.item.CustomItemBase
import dev.lizainslie.cafemc.item.CustomItemUseResult
import dev.lizainslie.cafemc.item.data.UpgradedSpawner
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.Block
import org.bukkit.block.CreatureSpawner
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.spawner.Spawner

internal const val SPAWNER_UPGRADE_RANGE = 16

object SpawnerRangeUpgrade : CustomItemBase(
    "spawner_range_upgrade",
    Material.LIGHTNING_ROD,
    name = "${ChatColor.LIGHT_PURPLE}Spawner Range Upgrade",
    lore = listOf("Increase the range of any spawner by $SPAWNER_UPGRADE_RANGE blocks"),
    glint = true,
    canPlace = false,
) {
    val SPAWNER_UPGRADE_KEY by lazy { NamespacedKey(CafeMC.instance, "spawner_upgrade") }
    val SPAWNER_APPLIED_UPGRADES_KEY by lazy { NamespacedKey(CafeMC.instance, "spawner_applied_upgrades") }
    val SPAWNER_UPGRADE_MAX = 5
    
    override fun ItemMeta.modifyMeta() {
        persistentDataContainer.set(SPAWNER_UPGRADE_KEY, PersistentDataType.BOOLEAN, true)
    }

    override fun canUseOnBlock(block: Block) = block.type == Material.SPAWNER && block.state is CreatureSpawner

    override fun useOnBlock(player: Player, block: Block): CustomItemUseResult {
        val spawner = block.state as CreatureSpawner
        
        val appliedUpgrades = getAppliedUpgrades(spawner)
        
        if (appliedUpgrades == SPAWNER_UPGRADE_MAX)
            return CustomItemUseResult.Error("This spawner has reached the maximum upgrade limit ($appliedUpgrades/$SPAWNER_UPGRADE_MAX)")
        
        val newUpgradeCount = appliedUpgrades + 1
        
        spawner.persistentDataContainer.set(SPAWNER_APPLIED_UPGRADES_KEY, PersistentDataType.INTEGER, newUpgradeCount)
        spawner.requiredPlayerRange += SPAWNER_UPGRADE_RANGE
        
        player.sendMessage("${ChatColor.GRAY}Spawner range increased by $SPAWNER_UPGRADE_RANGE blocks. ($newUpgradeCount/$SPAWNER_UPGRADE_MAX upgrades applied)")
        player.sendMessage("${ChatColor.GRAY}Its new range is ${ChatColor.GOLD}${spawner.requiredPlayerRange} blocks${ChatColor.GRAY}.")
        
        return CustomItemUseResult.ConsumeItem
    }
    
    override fun checkStack(stack: ItemStack) = 
        super.checkStack(stack) && 
            stack.itemMeta?.persistentDataContainer?.getOrDefault(
                SPAWNER_UPGRADE_KEY,
                PersistentDataType.BOOLEAN, 
                false
            ) ?: false
    
    private fun getAppliedUpgrades(spawner: CreatureSpawner) = 
        spawner.persistentDataContainer.getOrDefault(
            SPAWNER_APPLIED_UPGRADES_KEY,
            PersistentDataType.INTEGER,
            0
        )
}