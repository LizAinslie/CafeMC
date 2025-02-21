package dev.lizainslie.cafemc.spawner

import dev.lizainslie.cafemc.CafeMC
import dev.lizainslie.cafemc.core.PluginModule
import dev.lizainslie.cafemc.item.customItem
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.block.CreatureSpawner
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.persistence.PersistentDataType
import java.util.logging.Logger

object SpawnerModule : PluginModule(), Listener {
    private lateinit var spawnerTypeKey: NamespacedKey
    private lateinit var logger: Logger
    
    override fun onEnable(cafeMC: CafeMC) {
        super.onEnable(cafeMC)
        logger = cafeMC.logger
        spawnerTypeKey = NamespacedKey(cafeMC, "spawner_type")
    }
    
    @EventHandler
    fun onSpawnerBroken(event: BlockBreakEvent) {
        val player = event.player
        
        if (event.block.type == Material.SPAWNER && player.inventory.itemInMainHand.containsEnchantment(Enchantment.SILK_TOUCH)) {
            val spawner = event.block.state as CreatureSpawner
            
            spawner.spawnedType?.let {
                event.isCancelled = true
                event.block.type = Material.AIR
                spawner.world.dropItemNaturally(spawner.location, customItem(Material.SPAWNER) {
                    name {
                        text(Component.translatable(spawner.spawnedType!!.translationKey())) {
                            color = NamedTextColor.DARK_PURPLE
                        }

                        text(" Spawner") {
                            color = NamedTextColor.GRAY
                        }
                    }

                    extendItemMeta {
                        persistentDataContainer.set(spawnerTypeKey, PersistentDataType.STRING, spawner.spawnedType!!.key().asString())
                    }
                }.createItem(1))
            }
        }
    }
    
    @EventHandler
    fun onSpawnerPlaced(event: BlockPlaceEvent) {
        val item = event.itemInHand
        val block = event.block
        
        logger.info("Placed block: $block")
        logger.info("Item in hand: $item")
        logger.info("Item in hand meta: ${item.itemMeta}")
        
        if (block.type == Material.SPAWNER && item.hasItemMeta()) {
            logger.info("Item has meta & block is spawner")
            val meta = item.itemMeta
            
            if (meta.persistentDataContainer.has(spawnerTypeKey, PersistentDataType.STRING)) {
                logger.info("Item has spawner type key")
                val spawnerType = meta.persistentDataContainer.get(spawnerTypeKey, PersistentDataType.STRING)!!
                
                logger.info("Spawner type: $spawnerType")
                val spawner = block.state as CreatureSpawner
                
                val entityType = EntityType.entries.first { it.key().asString() == spawnerType }
                logger.info("Entity type: $entityType")

                spawner.spawnedType = entityType
                spawner.update(true)
            }
        }
    }
}