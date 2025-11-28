package dev.lizainslie.cafemc.protect.commands

import dev.lizainslie.cafemc.chat.sendRichMessage
import dev.lizainslie.cafemc.core.cmd.AllowedSender
import dev.lizainslie.cafemc.core.cmd.CommandContext
import dev.lizainslie.cafemc.core.cmd.PluginCommand
import dev.lizainslie.cafemc.protect.ProtectionModule
import dev.lizainslie.cafemc.protect.data.LockedBlock
import dev.lizainslie.cafemc.util.ItemUtils
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.jetbrains.exposed.sql.transactions.transaction

object LockCommand : PluginCommand(
    command = "lock",
    permission = "cafe.lock",
    allowedSender = AllowedSender.PLAYER,
) {
    override fun CommandContext.onCommand() {
        player.getTargetBlockExact(5)?.let { block ->
            val blockName = ItemUtils.getDefaultName(block.type)
            if (block.type !in ProtectionModule.LOCKABLE_BLOCKS)
                return sendRichError {
                    text("You cannot lock this ") { color = NamedTextColor.GRAY }
                    text(blockName) { color = NamedTextColor.AQUA }
                }
            
            transaction {
                val preexistingLock = LockedBlock.findFromBlock(block)
                if (preexistingLock != null) {
                    if (preexistingLock.ownerId != player.uniqueId)
                        return@transaction sendRichError {
                            text("This ") { color = NamedTextColor.GRAY }
                            text(blockName) { color = NamedTextColor.AQUA }
                            text(" is already locked by ") { color = NamedTextColor.GRAY }
                            text(Bukkit.getOfflinePlayer(preexistingLock.ownerId).name ?: "") {
                                color = NamedTextColor.GOLD
                            }
                        }
                    
                    return@transaction sendRichError {
                        text("This ") { color = NamedTextColor.GRAY }
                        text(blockName) { color = NamedTextColor.AQUA }
                        text(" is already locked, use ") { color = NamedTextColor.GRAY }
                        text("/unlock") { color = NamedTextColor.GOLD }
                        text(" to unlock it")
                    }
                }

                LockedBlock.create(block, player)
                
                player.sendRichMessage { 
                    text(blockName) { color = NamedTextColor.AQUA }
                    text(" locked.") { color = NamedTextColor.GRAY }
                }
            }
        } ?: return sendError("Look at a block to lock it")
    }
}