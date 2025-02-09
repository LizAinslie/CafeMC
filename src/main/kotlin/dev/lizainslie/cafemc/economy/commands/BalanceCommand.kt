package dev.lizainslie.cafemc.economy.commands

import dev.lizainslie.cafemc.core.cmd.AllowedSender
import dev.lizainslie.cafemc.core.cmd.CommandContext
import dev.lizainslie.cafemc.core.cmd.PluginCommand
import dev.lizainslie.cafemc.economy.CafeEconomy
import dev.lizainslie.cafemc.economy.EconomyModule
import dev.lizainslie.cafemc.util.AccountUtils
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.OfflinePlayer

object BalanceCommand : PluginCommand(
    command = "balance",
    description = "Check your balance or that of another player",
    aliases = listOf("bal"),
    usage = "[user]",
    allowedSender = AllowedSender.PLAYER,
    minArgs = 0,
    maxArgs = 1,
    permission = "cafe.balance",
) {
    override fun CommandContext.onCommand() {
        var target: OfflinePlayer = player
        var targetIsSender = true
        
        if (args.isNotEmpty()) {
            if (!sender.hasPermission("cafe.balance.others"))
                return sendError("You do not have permission to check other players' balances")
            
            AccountUtils.getUuidForAccountName(args[0])?.let { 
                target = Bukkit.getOfflinePlayer(it)
                targetIsSender = false
            } ?: return sendError("Player ${args[0]} not found")
        }
        
        val balance = CafeEconomy.getBalance(target)
        val formattedBalance = CafeEconomy.format(balance)
        val formattedTargetName = 
            if (targetIsSender) "Your"
            else "${ChatColor.BLUE}${target.name}${ChatColor.GRAY}'s"
        
        player.sendMessage("${ChatColor.GRAY}$formattedTargetName balance is ${ChatColor.GOLD}$formattedBalance${ChatColor.GRAY}.")
    }

    override fun CommandContext.tabComplete(): List<String> {
        return when (args.size) {
            0 ->
                if (sender.hasPermission("cafe.economy.balance.others"))
                    Bukkit.getOnlinePlayers().map { it.name }
                else emptyList()
            1 -> 
                if (sender.hasPermission("cafe.economy.balance.others"))
                    Bukkit.getOnlinePlayers().map { it.name }.filter { it.startsWith(args[0], ignoreCase = true) }
                else emptyList()
            else -> emptyList()
        }
    }
}