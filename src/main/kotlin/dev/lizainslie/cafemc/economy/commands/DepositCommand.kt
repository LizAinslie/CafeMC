package dev.lizainslie.cafemc.economy.commands

import dev.lizainslie.cafemc.core.cmd.AllowedSender
import dev.lizainslie.cafemc.core.cmd.CommandContext
import dev.lizainslie.cafemc.core.cmd.PluginCommand
import dev.lizainslie.cafemc.economy.CafeEconomy
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.Bukkit
import org.bukkit.ChatColor

internal val SUBCOMMANDS = listOf("open")

object DepositCommand : PluginCommand(
    command = "deposit",
    description = "Deposit your valuables as money into your account",
    permission = "cafe.deposit",
    allowedSender = AllowedSender.PLAYER,
    minArgs = 0,
    maxArgs = 1,
) {
    override fun CommandContext.onCommand() {
        if (args.isEmpty()) {
            player.sendMessage("""
                ${ChatColor.YELLOW}${ChatColor.BOLD}Warning!${ChatColor.RESET}${ChatColor.GRAY} Items deposited into the deposit box will be converted into money and deposited into your account.
                ${ChatColor.RED}${ChatColor.UNDERLINE}You cannot get them back after you close the menu!${ChatColor.RESET}
                
                ${ChatColor.AQUA}Diamonds${ChatColor.GRAY} are worth ${ChatColor.GOLD}${CafeEconomy.format(1000.0)}${ChatColor.GRAY}
                ${ChatColor.GREEN}Emeralds${ChatColor.GRAY} are worth ${ChatColor.GOLD}${CafeEconomy.format(500.0)}${ChatColor.GRAY}
                ${ChatColor.YELLOW}Gold Ingots${ChatColor.GRAY} are worth ${ChatColor.GOLD}${CafeEconomy.format(250.0)}${ChatColor.GRAY}
                ${ChatColor.GOLD}Copper Ingots${ChatColor.GRAY} are worth ${ChatColor.GOLD}${CafeEconomy.format(100.0)}${ChatColor.GRAY}
                ${ChatColor.WHITE}Iron Ingots${ChatColor.GRAY} are worth ${ChatColor.GOLD}${CafeEconomy.format(50.0)}${ChatColor.GRAY}
                
            """.trimIndent())
            
            val openButton = ComponentBuilder("[Click here to open the deposit box]")
                .color(net.md_5.bungee.api.ChatColor.GOLD)
                .event(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/deposit open"))
                .event(HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("/deposit open")))
                .build()
            
            return player.spigot().sendMessage(openButton)
        } else {
            when (val subCommand = args[0].lowercase()) {
                "open" -> {
                    val depositBox = Bukkit.createInventory(null, 27, "${ChatColor.GOLD}Deposit Box")
                    player.openInventory(depositBox)
                }
                else -> sendError("Invalid subcommand $subCommand")
            }
        }
    }
    
    override fun CommandContext.tabComplete(): List<String> {
        return when (args.size) {
            0 -> SUBCOMMANDS
            1 -> SUBCOMMANDS.filter { it.startsWith(args[0], ignoreCase = true) }
            else -> emptyList()
        }
    }
}