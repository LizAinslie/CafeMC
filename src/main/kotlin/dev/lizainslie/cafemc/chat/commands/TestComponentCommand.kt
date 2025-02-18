package dev.lizainslie.cafemc.chat.commands

import dev.lizainslie.cafemc.chat.sendMessage
import dev.lizainslie.cafemc.core.cmd.AllowedSender
import dev.lizainslie.cafemc.core.cmd.CommandContext
import dev.lizainslie.cafemc.core.cmd.PluginCommand
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.hover.content.Item
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack

object TestComponentCommand : PluginCommand(
    command = "testcomponent",
    description = "Test the component system",
    allowedSender = AllowedSender.PLAYER,
    minArgs = 0,
    maxArgs = 1,
    
) {
    override fun CommandContext.onCommand() {
        when (args.size) {
            0 -> {
                player.sendMessage {
                    text("Command & Hover Content Test") {
                        italic = true
                        color = ChatColor.RED

                        events {
                            hover {
                                textContent {
                                    text("/testcomponent") {
                                        bold = true
                                        color = ChatColor.GOLD
                                    }

                                    text(" ") {
                                        reset()
                                    }

                                    text("runcommandtest") {
                                        italic = true
                                        color = ChatColor.YELLOW
                                    }
                                }
                            }

                            click {
                                action = ClickEvent.Action.RUN_COMMAND
                                value = "/testcomponent runcommandtest"
                            }
                        }
                    }

                    text(" ")

                    text("Open URL Test & Hover Entity Test") {
                        bold = true
                        underlined = true
                        color = ChatColor.GREEN

                        events {
                            hover {
                                entityContent("minecraft:player", player.uniqueId.toString(), player.name)
                            }

                            click {
                                action = ClickEvent.Action.OPEN_URL
                                value = "https://lizainslie.dev"
                            }
                        }
                    }

                    text(" ")

                    text("Hover ItemStack & Click Clipboard Test") {
                        strikethrough = true
                        obfuscated = true
                        color = ChatColor.BLUE

                        events {
                            hover {
                                val item = ItemStack(Material.DIAMOND)
                                val meta = item.itemMeta?.apply {
                                    setDisplayName("${ChatColor.DARK_PURPLE}${ChatColor.MAGIC}!i${ChatColor.RESET}${ChatColor.LIGHT_PURPLE}Diamond${ChatColor.DARK_PURPLE}${ChatColor.MAGIC}!i${ChatColor.RESET}")
                                    lore = listOf(
                                        "${ChatColor.AQUA}${ChatColor.ITALIC}A shiny diamond",
                                        "${ChatColor.YELLOW}Oooooh, shiny!",
                                        "",
                                        "${ChatColor.GRAY}Click to copy a hidden message"
                                    )
                                    addEnchant(Enchantment.FORTUNE, 100, true)
                                }
                                item.itemMeta = meta

                                itemContent(item)
                            }

                            click {
                                action = ClickEvent.Action.COPY_TO_CLIPBOARD
                                value = "im a baka"
                            }
                        }
                    }
                }
            }
            1 -> {
                when (args[0].lowercase()) {
                    "runcommandtest" -> {
                        player.sendMessage("Command test successful!")
                    }
                }
            }
        }
    }
}