package dev.lizainslie.cafemc.chat.commands

import dev.lizainslie.cafemc.chat.addRichLoreLine
import dev.lizainslie.cafemc.chat.sendRichMessage
import dev.lizainslie.cafemc.chat.setRichDisplayName
import dev.lizainslie.cafemc.core.cmd.AllowedSender
import dev.lizainslie.cafemc.core.cmd.CommandContext
import dev.lizainslie.cafemc.core.cmd.PluginCommand
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack

object TestComponentCommand : PluginCommand(
    command = "testcomponent",
    maxArgs = 1,
    allowedSender = AllowedSender.PLAYER,

) {
    override fun CommandContext.onCommand() {
        when (args.size) {
            0 -> {
                player.sendRichMessage {
                    text("Command & Hover Content Test (Custom Color)") {
                        italic = true
                        color = TextColor.color(0xdc3545)

                        events {
                            hover {
                                text("/testcomponent") {
                                    bold = true
                                    color = NamedTextColor.GOLD
                                }

                                text(" ")

                                text("runcommandtest") {
                                    italic = true
                                    color = NamedTextColor.YELLOW
                                }
                            }

                            click = ClickEvent.runCommand("/testcomponent runcommandtest")
                        }
                    }

                    text(" ")

                    text("Open URL Test & Hover Entity Test") {
                        bold = true
                        underlined = true
                        color = NamedTextColor.GREEN

                        events {
                            hover(player) // entity

                            click = ClickEvent.openUrl("https://lizainslie.dev")
                        }
                    }

                    text(" ")

                    text("Hover ItemStack & Click Clipboard Test") {
                        strikethrough = true
                        obfuscated = true
                        color = NamedTextColor.BLUE

                        events {
                            hover(ItemStack(Material.DIAMOND).apply {
                                itemMeta = itemMeta?.apply {
                                    setRichDisplayName { 
                                        text("!i") {
                                            color = NamedTextColor.DARK_PURPLE
                                            obfuscated = true
                                        }
                                        text("Diamond") {
                                            color = NamedTextColor.LIGHT_PURPLE
                                        }
                                        text("!i") {
                                            color = NamedTextColor.DARK_PURPLE
                                            obfuscated = true
                                        }
                                    }
                                    
                                    addRichLoreLine {
                                        text("A shiny diamond") {
                                            color = NamedTextColor.AQUA
                                            italic = true
                                        }
                                    }
                                    
                                    addRichLoreLine {
                                        text("Oooooh, shiny!") {
                                            color = NamedTextColor.YELLOW
                                        }
                                    }
                                    
                                    addRichLoreLine {
                                        text("")
                                    }
                                    
                                    addRichLoreLine {
                                        text("Click to copy a hidden message") {
                                            color = NamedTextColor.GRAY
                                        }
                                    }
                                    
                                    addEnchant(Enchantment.FORTUNE, 100, true)
                                }
                            })

                            click = ClickEvent.copyToClipboard("im a baka")
                        }
                    }
                }
            }
            1 -> {
                when (args[0].lowercase()) {
                    "runcommandtest" -> {
                        player.sendRichMessage {
                            text("Command test successful! ")
                            text("Click here to run a callback") {
                                color = NamedTextColor.GOLD
                                
                                events {
                                    click = ClickEvent.callback { 
                                        it.sendRichMessage { 
                                            text("Callback test successful!") {
                                                color = NamedTextColor.GREEN
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}