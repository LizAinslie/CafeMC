package dev.lizainslie.cafemc.protect

import dev.lizainslie.cafemc.CafeMC
import dev.lizainslie.cafemc.auditing.AuditModule
import dev.lizainslie.cafemc.core.PluginModule
import dev.lizainslie.cafemc.protect.commands.LockCommand
import dev.lizainslie.cafemc.protect.commands.UnlockCommand
import dev.lizainslie.cafemc.protect.data.LockedBlock
import dev.lizainslie.cafemc.protect.data.LockedBlockBreakIncident
import dev.lizainslie.cafemc.util.ItemUtils
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.jetbrains.exposed.sql.transactions.transaction

object ProtectionModule : PluginModule(), Listener {
    val LOCKABLE_BLOCKS = mutableSetOf(
        // Signs
        Material.OAK_SIGN,
        Material.SPRUCE_SIGN,
        Material.BIRCH_SIGN,
        Material.JUNGLE_SIGN,
        Material.ACACIA_SIGN,
        Material.DARK_OAK_SIGN,
        Material.MANGROVE_SIGN,
        Material.CHERRY_SIGN,
        Material.PALE_OAK_SIGN,
        Material.BAMBOO_SIGN,
        Material.CRIMSON_SIGN,
        Material.WARPED_SIGN,
        
        // Wall Signs
        Material.OAK_WALL_SIGN,
        Material.SPRUCE_WALL_SIGN,
        Material.BIRCH_WALL_SIGN,
        Material.JUNGLE_WALL_SIGN,
        Material.ACACIA_WALL_SIGN,
        Material.DARK_OAK_WALL_SIGN,
        Material.MANGROVE_WALL_SIGN,
        Material.CHERRY_WALL_SIGN,
        Material.PALE_OAK_WALL_SIGN,
        Material.BAMBOO_WALL_SIGN,
        Material.CRIMSON_WALL_SIGN,
        Material.WARPED_WALL_SIGN,
        
        // Hanging Signs
        Material.OAK_HANGING_SIGN,
        Material.SPRUCE_HANGING_SIGN,
        Material.BIRCH_HANGING_SIGN,
        Material.JUNGLE_HANGING_SIGN,
        Material.ACACIA_HANGING_SIGN,
        Material.DARK_OAK_HANGING_SIGN,
        Material.MANGROVE_HANGING_SIGN,
        Material.CHERRY_HANGING_SIGN,
        Material.PALE_OAK_HANGING_SIGN,
        Material.BAMBOO_HANGING_SIGN,
        Material.CRIMSON_HANGING_SIGN,
        Material.WARPED_HANGING_SIGN,
    )
    
    init {
        commands += LockCommand
        commands += UnlockCommand
    }
    
    
    
    // region Module Lifecycle
    
    override fun onEnable(cafeMC: CafeMC) {
        super.onEnable(cafeMC)
        
        AuditModule.trackIncident(LockedBlockBreakIncident)
    }
    
    // endregion
    
    
    
    // region Event Handlers
    
    @EventHandler
    fun playerInteract(event: PlayerInteractEvent) {
        val player = event.player
        val block = event.clickedBlock

        if (block != null) {
            if (block.type in LOCKABLE_BLOCKS && block.hasMetadata("locked")) {
                if (!player.hasPermission("cafemc.lock.bypass")) {
                    event.isCancelled = true
                    player.sendMessage("${ChatColor.RED}You do not have permission to interact with this ${ItemUtils.getDefaultName(block.type)}.")
                }
            }
        }
    }
    
    @EventHandler
    fun blockBreak(event: BlockBreakEvent) {
        val block = event.block
        
        if (block.type !in LOCKABLE_BLOCKS) return
        
        transaction { 
            val lockedBlock = LockedBlock.findFromBlock(block) ?: return@transaction
            if (lockedBlock.ownerId != event.player.uniqueId) {
                LockedBlockBreakIncident.create(lockedBlock, event.player.uniqueId)
                
                val owner = Bukkit.getOfflinePlayer(lockedBlock.ownerId)
                
                event.player.sendMessage("${ChatColor.YELLOW}${ChatColor.BOLD}Warning:${ChatColor.RESET}${ChatColor.GRAY} You have broken a ${ChatColor.LIGHT_PURPLE}${ItemUtils.getDefaultName(block.type)}${ChatColor.GRAY} locked by ${ChatColor.BLUE}${owner.name}${ChatColor.GRAY}. This incident has been logged & reported.")
            }
        }
        
    }
    
    // endregion
}