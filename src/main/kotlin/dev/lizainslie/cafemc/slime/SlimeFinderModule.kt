package dev.lizainslie.cafemc.slime

import dev.lizainslie.cafemc.CafeMC
import dev.lizainslie.cafemc.core.PluginModule
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Item
import org.bukkit.util.Vector

object SlimeFinderModule : PluginModule() {

    // region Module Lifecycle
    
    override fun register(cafeMC: CafeMC) {
        super.register(cafeMC)
        
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