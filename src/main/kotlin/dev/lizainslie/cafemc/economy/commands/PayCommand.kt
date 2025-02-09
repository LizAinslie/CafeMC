package dev.lizainslie.cafemc.economy.commands

import dev.lizainslie.cafemc.core.cmd.AllowedSender
import dev.lizainslie.cafemc.core.cmd.CommandContext
import dev.lizainslie.cafemc.core.cmd.PluginCommand
import dev.lizainslie.cafemc.economy.CafeEconomy
import dev.lizainslie.cafemc.economy.EconomyModule
import dev.lizainslie.cafemc.economy.data.PlayerTransaction
import dev.lizainslie.cafemc.util.AccountUtils
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.jetbrains.exposed.sql.transactions.transaction

object PayCommand : PluginCommand(
    command = "pay",
    description = "Pay another player",
    usage = "<player> <amount>",
    allowedSender = AllowedSender.PLAYER,
    minArgs = 2,
    maxArgs = 2,
    permission = "cafe.pay",
) {
    override fun CommandContext.onCommand() {
        val target = AccountUtils.getUuidForAccountName(args[0])?.let { Bukkit.getOfflinePlayer(it) }
            ?: return sendError("Player not found")

        val amount = args[1].toDoubleOrNull()
        if (amount == null || amount <= 0) {
            sendError("Invalid amount")
            return
        }

        if (!CafeEconomy.has(player, amount)) return sendError("You do not have enough money. Run ${ChatColor.GOLD}/balance${ChatColor.GRAY} to check your balance.")

        CafeEconomy.withdrawPlayer(player, amount)
        CafeEconomy.depositPlayer(target, amount)
        
        val formattedAmount = CafeEconomy.format(amount)

        player.sendMessage("You paid ${target.name} $formattedAmount")
        val targetPlayer = Bukkit.getPlayer(target.uniqueId)
        
        var notified = false
        
        if (targetPlayer != null) {
            targetPlayer.sendMessage(EconomyModule.getNotificationMessage(amount, player.name))
            notified = true
        }
        
        transaction { 
            PlayerTransaction.create(player.uniqueId, target.uniqueId, amount, notified)
        }
    }

    override fun CommandContext.tabComplete(): List<String> {
        return when (args.size) {
            0 -> Bukkit.getOnlinePlayers().map { it.name }
            1 -> Bukkit.getOnlinePlayers().map { it.name }.filter { it.startsWith(args[0], ignoreCase = true) }
            else -> emptyList()
        }
    }
}