package dev.lizainslie.cafemc.item.items

import dev.lizainslie.cafemc.CafeMC
import dev.lizainslie.cafemc.chat.sendRichMessage
import dev.lizainslie.cafemc.item.CustomItemBase
import dev.lizainslie.cafemc.item.CustomItemUseResult
import net.kyori.adventure.text.format.NamedTextColor
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
    glint = true,
    canPlace = false,
) {
    init {
        name { 
            text("Spawner Range Upgrade") { color = NamedTextColor.LIGHT_PURPLE }
        }
        
        loreLine {
            text("Increase the range of any spawner by ") { color = NamedTextColor.GRAY }
            text("$SPAWNER_UPGRADE_RANGE") { color = NamedTextColor.GOLD }
            text(" blocks") { color = NamedTextColor.GRAY }
        }
    }
    
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
        
        player.sendRichMessage { 
            text("Spawner range increased by ") { color = NamedTextColor.GRAY }
            text("$SPAWNER_UPGRADE_RANGE") { color = NamedTextColor.GOLD }
            text(" blocks. (") { color = NamedTextColor.GRAY }
            text("$newUpgradeCount") { 
                color = 
                    if (newUpgradeCount == SPAWNER_UPGRADE_MAX) NamedTextColor.RED
                    else NamedTextColor.GREEN
            }
            text("/") { color = NamedTextColor.GRAY }
            text("$SPAWNER_UPGRADE_MAX") { color = NamedTextColor.GOLD }
            text(" upgrades applied)") { color = NamedTextColor.GRAY }
            newline()
            text("Its new range is ") { color = NamedTextColor.GRAY }
            text("${spawner.requiredPlayerRange} blocks") { color = NamedTextColor.GOLD }
            text(".") { color = NamedTextColor.GRAY }
        }
        
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