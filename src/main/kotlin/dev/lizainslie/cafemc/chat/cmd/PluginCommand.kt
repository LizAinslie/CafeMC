package dev.lizainslie.cafemc.chat.cmd

import org.bukkit.ChatColor
import org.bukkit.command.CommandSender

abstract class PluginCommand(
    val command: String,
    val description: String,
    val usage: String,
    val permission: String? = null,
    val aliases: List<String> = emptyList(),
    val minArgs: Int = 0,
    val maxArgs: Int = 0,
    val allowedSender: AllowedSender = AllowedSender.ALL,
) {
    protected lateinit var sender: CommandSender

    fun handle(s: CommandSender, args: List<String>) {
        sender = s

        onCommand(args)
    }

    abstract fun onCommand(args: List<String>)
    open fun tabComplete(sender: CommandSender, args: List<String>): List<String> = emptyList()

    fun sendError(message: String) {
        sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}Error:${ChatColor.RESET}${ChatColor.GRAY} $message")
    }
}