package dev.lizainslie.cafemc.economy.commands

import dev.lizainslie.cafemc.chat.sendRichMessage
import dev.lizainslie.cafemc.core.cmd.AllowedSender
import dev.lizainslie.cafemc.core.cmd.CommandContext
import dev.lizainslie.cafemc.core.cmd.PluginCommand
import dev.lizainslie.cafemc.economy.CafeEconomy
import dev.lizainslie.cafemc.economy.EconomyModule
import dev.lizainslie.cafemc.util.ItemMaps
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor

object DepositCommand : PluginCommand(
    command = "deposit",
    permission = "cafe.deposit",
    allowedSender = AllowedSender.PLAYER,
) {
    override fun CommandContext.onCommand() {
        
//        player.sendMessage("""
//            ${ChatColor.YELLOW}${ChatColor.BOLD}Warning!${ChatColor.RESET}${ChatColor.GRAY} Items deposited into the deposit box will be converted into money and deposited into your account.
//            ${ChatColor.RED}${ChatColor.UNDERLINE}You cannot get them back after you close the menu!${ChatColor.RESET}
//            
//            ${ChatColor.AQUA}Diamonds${ChatColor.GRAY} are worth ${ChatColor.GOLD}${CafeEconomy.format(1000.0)}${ChatColor.GRAY}
//            ${ChatColor.GREEN}Emeralds${ChatColor.GRAY} are worth ${ChatColor.GOLD}${CafeEconomy.format(500.0)}${ChatColor.GRAY}
//            ${ChatColor.YELLOW}Gold Ingots${ChatColor.GRAY} are worth ${ChatColor.GOLD}${CafeEconomy.format(250.0)}${ChatColor.GRAY}
//            ${ChatColor.GOLD}Copper Ingots${ChatColor.GRAY} are worth ${ChatColor.GOLD}${CafeEconomy.format(100.0)}${ChatColor.GRAY}
//            ${ChatColor.WHITE}Iron Ingots${ChatColor.GRAY} are worth ${ChatColor.GOLD}${CafeEconomy.format(50.0)}${ChatColor.GRAY}
//        """.trimIndent())
//        
//        
//        
//        val openButton = ComponentBuilder("[Click here to open the deposit box]")
//            .color(net.md_5.bungee.api.ChatColor.GOLD)
//            .event(ClickEvent(ClickEvent.Action.RUN_COMMAND, "/deposit open"))
//            .event(HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("/deposit open")))
//            .build()
        
//        return player.spigot().sendMessage(openButton)
        
        player.sendRichMessage { 
            text("Warning!") { 
                color = NamedTextColor.YELLOW
                bold = true
            }
            
            text(" Items deposited into the deposit box will be converted into money and deposited into your account.") {
                color = NamedTextColor.GRAY
            }
            
            newline()
            
            text("You cannot get them back after you close the menu!") {
                color = NamedTextColor.RED
                underlined = true
            }
            
            newline()
            newline()
            
            ItemMaps.Valuables.entries.forEach { 
                text("${it.itemDisplayName}s") { color = it.color }
                text(" are worth ") { color = NamedTextColor.GRAY }
                text(CafeEconomy.format(it.itemValue)) { color = NamedTextColor.GOLD }
                text(".") { color = NamedTextColor.GRAY }
                
                newline()
            }
            
            newline()
            
            text("[Click here to open the deposit box]") {
                color = NamedTextColor.GOLD
                bold = true
                
                events {
                    click = ClickEvent.callback { 
                        EconomyModule.openDepositBox(player)
                    }
                }
            }
        }
    }
}