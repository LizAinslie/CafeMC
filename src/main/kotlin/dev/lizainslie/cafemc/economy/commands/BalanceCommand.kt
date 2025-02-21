package dev.lizainslie.cafemc.economy.commands

import dev.lizainslie.cafemc.chat.sendRichMessage
import dev.lizainslie.cafemc.core.cmd.AllowedSender
import dev.lizainslie.cafemc.core.cmd.CommandContext
import dev.lizainslie.cafemc.core.cmd.PluginCommand
import dev.lizainslie.cafemc.economy.CafeEconomy
import dev.lizainslie.cafemc.util.AccountUtils
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
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
            } ?: return sendRichError {
                text("Player ") 
                text(args[0]) { color = NamedTextColor.BLUE }
                text(" not found.")
            }
        }
        
        val balance = CafeEconomy.getBalance(target)
        val formattedBalance = CafeEconomy.format(balance)
        
        player.sendRichMessage { 
            if (targetIsSender) text("Your") { color = NamedTextColor.GRAY }
            else text("${target.name}'s") { color = NamedTextColor.BLUE }
            
            text(" balance is ") { color = NamedTextColor.GRAY }
            text(formattedBalance) { color = NamedTextColor.GOLD }
            text(".") { color = NamedTextColor.GRAY }
        }
    }

    override fun CommandContext.tabComplete(): MutableList<String> {
        return when (args.size) {
            0 ->
                if (sender.hasPermission("cafe.economy.balance.others"))
                    Bukkit.getOnlinePlayers().map { it.name }.toMutableList()
                else mutableListOf()
            1 -> 
                if (sender.hasPermission("cafe.economy.balance.others"))
                    Bukkit.getOnlinePlayers()
                        .map { it.name }
                        .filter { it.startsWith(args[0], ignoreCase = true) }
                        .toMutableList()
                else mutableListOf()
            else -> mutableListOf()
        }
    }
}