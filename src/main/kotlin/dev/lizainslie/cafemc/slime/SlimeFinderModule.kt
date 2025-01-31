package dev.lizainslie.cafemc.slime

import dev.lizainslie.cafemc.CafeMC
import dev.lizainslie.cafemc.core.PluginModule
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.util.Vector

object SlimeFinderModule : PluginModule(), Listener {
    
    // region Event Handlers
    
    @EventHandler
    fun onSlimeballThrow(event: PlayerDropItemEvent) {
//        if (event.itemDrop.itemStack.type == Material.SLIME_BALL) {
//            if (event.itemDrop.location.chunk.isSlimeChunk) {
//                event.player.world.playSound(event.itemDrop.location, Sound.ENTITY_SLIME_JUMP, .5f, 1f)
//                event.itemDrop.velocity = Vector(0f, .33f, 0f)
//            }
//        }
    }
    
    // endregion
    
    

    // region Module Lifecycle
    
    override fun register(cafeMC: CafeMC) {
        super.register(cafeMC)
        
        Bukkit.getPluginManager().registerEvents(this, cafeMC)
        
        Bukkit.getScheduler().runTaskTimer(cafeMC, ::bounceSlimesTask, 0, 100)
    }
    
    // endregion
    
    
    
    // region Tasks
    
    private fun bounceSlimesTask() {
        Bukkit.getWorlds()[0].entities
            .filterIsInstance<Item>()
            .filter { it.location.chunk.isSlimeChunk  && it.isValid && it.itemStack.type == Material.SLIME_BALL }
            .forEach {
                it.world.playSound(it.location, Sound.ENTITY_SLIME_JUMP, .5f, 1f)
                it.velocity = Vector(0f, .33f, 0f)
            }
    }
    
    // endregion
}