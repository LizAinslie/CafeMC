package dev.lizainslie.cafemc.core.cmd

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

abstract class PluginCommand(
    val command: String,
    val description: String,
    val usage: String = "",
    val permission: String? = null,
    val aliases: List<String> = emptyList(),
    val minArgs: Int = 0,
    val maxArgs: Int = 0,
    val allowedSender: AllowedSender = AllowedSender.ALL,
) {
    fun handle(s: CommandSender, args: List<String>, alias: String) {
        val ctx = CommandContext(s, args, this, alias)
        ctx.onCommand()
    }
    
    fun complete(s: CommandSender,  args: List<String>, alias: String): List<String> {
        val ctx = CommandContext(s, args, this, alias)
        return ctx.tabComplete()
    }

    abstract fun CommandContext.onCommand()
    open fun CommandContext.tabComplete(): List<String> = emptyList()
}

data class CommandContext(
    val sender: CommandSender,
    val args: List<String>,
    val command: PluginCommand,
    val alias: String
) {
    val player by lazy { sender as Player }

    fun sendError(message: String) {
        sender.sendMessage("${CommandMap.ERROR_PREFIX} $message")
    }
}