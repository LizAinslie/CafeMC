package dev.lizainslie.cafemc.item

import dev.lizainslie.cafemc.core.PluginModule
import dev.lizainslie.cafemc.item.items.SpawnerRangeUpgrade
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.PlayerInteractEvent

object CustomItemsModule : PluginModule(), Listener {
    private val customItems = listOf<CustomItemBase>(
        SpawnerRangeUpgrade,
    )
    
    fun getItem(id: String) = customItems.firstOrNull { it.id == id }
    
    @EventHandler
    fun onPlaceBlock(event: BlockPlaceEvent) {
        val customItem = customItems.firstOrNull { it.checkStack(event.itemInHand) } ?: return
        
        if (!customItem.canPlace) {
            event.isCancelled = true
            return
        }
    }
    
    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (event.action == Action.RIGHT_CLICK_BLOCK && event.hasItem() && event.hasBlock()) {
            val item = event.hand?.let { event.player.inventory.getItem(it) } ?: return
            val block = event.clickedBlock ?: return

            val customItem = customItems.firstOrNull { it.checkStack(item) && it.canUseOnBlock(block) } ?: return

            event.setUseItemInHand(Event.Result.ALLOW)
            val result = customItem.useOnBlock(event.player, block)
            result.apply(event.player, item)
        }
    }
}