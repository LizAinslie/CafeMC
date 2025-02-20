package dev.lizainslie.cafemc.core.cmd

import dev.lizainslie.cafemc.chat.ComponentDsl
import dev.lizainslie.cafemc.chat.sendError
import dev.lizainslie.cafemc.chat.sendRichError
import org.bukkit.block.Block
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
    
    fun complete(s: CommandSender,  args: List<String>, alias: String): MutableList<String> {
        val ctx = CommandContext(s, args, this, alias)
        return ctx.tabComplete().toMutableList()
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
    
    val player by lazy {
        assert(sender is Player) { "This command must be run by a player" }
        sender as Player
    }
    
    fun withLookingAt(errorMessage: String, distance: Int, block: (block: Block) -> Unit) {
        player.getTargetBlockExact(distance)?.let { block(it) } ?: sendError(errorMessage)
    }

    fun withLookingAt(errorMessage: String, block: (block: Block) -> Unit) {
        withLookingAt(errorMessage, 5, block)
    }
    
    fun withLookingAt(distance: Int, block: (block: Block) -> Unit) {
        withLookingAt("Look at a block", distance, block)
    }
    
    fun withLookingAt(block: (block: Block) -> Unit) {
        withLookingAt("Look at a block", block)
    }

    fun sendError(message: String) {
        sender.sendError(message)
    }
    
    fun sendRichError(block: ComponentDsl.() -> Unit) {
        sender.sendRichError(block)
    }
}