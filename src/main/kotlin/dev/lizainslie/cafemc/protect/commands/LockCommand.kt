package dev.lizainslie.cafemc.protect.commands

import dev.lizainslie.cafemc.core.cmd.AllowedSender
import dev.lizainslie.cafemc.core.cmd.CommandContext
import dev.lizainslie.cafemc.core.cmd.PluginCommand
import dev.lizainslie.cafemc.protect.ProtectionModule
import dev.lizainslie.cafemc.protect.data.LockedBlock
import dev.lizainslie.cafemc.util.ItemUtils
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.jetbrains.exposed.sql.transactions.transaction

object LockCommand : PluginCommand(
    command = "lock",
    description = "Lock a block",
    allowedSender = AllowedSender.PLAYER,
    permission = "cafe.lock",
) {
    override fun CommandContext.onCommand() {
        player.getTargetBlockExact(5)?.let { block ->
            if (block.type !in ProtectionModule.LOCKABLE_BLOCKS)
                return sendError("You cannot lock this ${ItemUtils.getDefaultName(block.type)}")
            
            transaction {
                val preexistingLock = LockedBlock.findFromBlock(block)
                if (preexistingLock != null) {
                    if (preexistingLock.ownerId != player.uniqueId)
                        return@transaction sendError(
                            "This block is already locked by ${Bukkit.getOfflinePlayer(preexistingLock.ownerId).name}"
                        )
                    
                    return@transaction sendError("This block is already locked, use ${ChatColor.GOLD}/unlock${ChatColor.GRAY} to unlock it")
                }

                LockedBlock.create(block, player)
                player.sendMessage("${ChatColor.GRAY}${ItemUtils.getDefaultName(block.type)} locked.")
            }
        } ?: return sendError("Look at a block to lock it")
    }
}