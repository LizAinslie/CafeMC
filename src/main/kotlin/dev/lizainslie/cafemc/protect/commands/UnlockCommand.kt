package dev.lizainslie.cafemc.protect.commands

import dev.lizainslie.cafemc.core.cmd.AllowedSender
import dev.lizainslie.cafemc.core.cmd.CommandContext
import dev.lizainslie.cafemc.core.cmd.PluginCommand
import dev.lizainslie.cafemc.protect.data.LockedBlock
import dev.lizainslie.cafemc.util.ItemUtils
import org.bukkit.ChatColor
import org.jetbrains.exposed.sql.transactions.transaction

object UnlockCommand : PluginCommand(
    command = "unlock",
    description = "Unlock a locked block",
    allowedSender = AllowedSender.PLAYER,
    permission = "cafe.lock",
) {
    override fun CommandContext.onCommand() {
        withLookingAt("Look at a block to unlock it") { block ->
            transaction {
                val lock = LockedBlock.findFromBlock(block) 
                    ?: return@transaction sendError("This ${ItemUtils.getDefaultName(block.type)} is not locked")

                if (lock.ownerId != player.uniqueId && !player.hasPermission("cafe.lock.bypass"))
                    return@transaction sendError("You do not own this lock")

                lock.delete()
                player.sendMessage("${ChatColor.GRAY}${ItemUtils.getDefaultName(block.type)} unlocked.")
            }
        }
    }
}