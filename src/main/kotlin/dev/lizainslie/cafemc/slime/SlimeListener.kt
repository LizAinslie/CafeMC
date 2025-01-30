package dev.lizainslie.cafemc.slime

import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent

object SlimeListener : Listener {
    
    @EventHandler
    fun onSlimeballThrow(event: PlayerDropItemEvent) {
        if (event.itemDrop.itemStack.type == Material.SLIME_BALL) {
            if (event.player.world.getChunkAt(event.player.location).isSlimeChunk) {
                event.itemDrop.velocity.y *= -0.5
                event.player.playSound(event.itemDrop, Sound.ENTITY_SLIME_JUMP, 1f, 1f)
            }
        }
    }
}