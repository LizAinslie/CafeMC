package dev.lizainslie.cafemc.chat.cmd

import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

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
    fun handle(s: CommandSender, args: List<String>) {
        val ctx = CommandContext(s, args, this)
        ctx.onCommand()
    }
    
    fun complete(s: CommandSender,  args: List<String>): List<String> {
        val ctx = CommandContext(s, args, this)
        return ctx.tabComplete()
    }

    abstract fun CommandContext.onCommand()
    open fun CommandContext.tabComplete(): List<String> = emptyList()
}

data class CommandContext(
    val sender: CommandSender,
    val args: List<String>,
    val command: PluginCommand,
) {
    val player by lazy { sender as Player }

    fun sendError(message: String) {
        sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}Error:${ChatColor.RESET}${ChatColor.GRAY} $message")
    }
}