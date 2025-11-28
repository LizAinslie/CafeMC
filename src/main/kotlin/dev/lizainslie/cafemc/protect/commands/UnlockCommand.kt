package dev.lizainslie.cafemc.protect.commands

import dev.lizainslie.cafemc.chat.sendRichMessage
import dev.lizainslie.cafemc.core.cmd.AllowedSender
import dev.lizainslie.cafemc.core.cmd.CommandContext
import dev.lizainslie.cafemc.core.cmd.PluginCommand
import dev.lizainslie.cafemc.protect.data.LockedBlock
import dev.lizainslie.cafemc.util.ItemUtils
import net.kyori.adventure.text.format.NamedTextColor
import org.jetbrains.exposed.sql.transactions.transaction

object UnlockCommand : PluginCommand(
    command = "unlock",
    permission = "cafe.lock",
    allowedSender = AllowedSender.PLAYER,
) {
    override fun CommandContext.onCommand() {
        withLookingAt("Look at a block to unlock it") { block ->
            transaction {
                val lock = LockedBlock.findFromBlock(block) 
                    ?: return@transaction sendRichError {
                        text("This ") { color = NamedTextColor.GRAY }
                        text(ItemUtils.getDefaultName(block.type)) { color = NamedTextColor.AQUA }
                        text(" is not locked.")
                    }

                if (lock.ownerId != player.uniqueId && !player.hasPermission("cafe.lock.bypass"))
                    return@transaction sendError("You do not own this lock.")

                lock.delete()
//                player.sendMessage("${ChatColor.GRAY}${ItemUtils.getDefaultName(block.type)} unlocked.")
                player.sendRichMessage {
                    text(ItemUtils.getDefaultName(block.type)) { color = NamedTextColor.AQUA }
                    text(" unlocked.") { color = NamedTextColor.GRAY }
                }
            }
        }
    }
}