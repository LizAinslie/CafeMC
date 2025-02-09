package dev.lizainslie.cafemc.core.cmd

import org.bukkit.ChatColor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class CommandMap : CommandExecutor, TabCompleter {
    private val commands = mutableMapOf<String, PluginCommand>()
    private val logger = Bukkit.getLogger()

    operator fun plusAssign(command: PluginCommand) {
        commands[command.command] = command
    }
    
    operator fun plusAssign(commands: List<PluginCommand>) {
        commands.forEach { this += it }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        try {
            val pluginCommand = commands[command.name]
            
            if (pluginCommand == null) { // Command not found
                sender.sendMessage("$ERROR_PREFIX Command /$label not found.")
                return true
            }

            if (pluginCommand.permission != null && !sender.hasPermission(pluginCommand.permission)) {
                sender.sendMessage("$ERROR_PREFIX You do not have permission to use this command.")
                return true
            }

            if (!pluginCommand.allowedSender.check(sender)) {
                sender.sendMessage("$ERROR_PREFIX ${pluginCommand.allowedSender.errorMessage}")
                return true
            }

            if (((pluginCommand.minArgs != -1) && args.size < pluginCommand.minArgs) || ((pluginCommand.maxArgs != -1) && args.size > pluginCommand.maxArgs)) {
                sender.sendMessage("$ERROR_PREFIX Invalid arguments. Usage: ${ChatColor.GOLD}/$label${if (pluginCommand.usage.isNotEmpty()) " " else ""}${pluginCommand.usage}${ChatColor.GRAY}.")
                return true
            }

            pluginCommand.handle(sender, args.asList(), label)
        } catch (e: Exception) {
            sender.sendMessage("$ERROR_PREFIX An error occurred while executing the command. ${e.message}")
            logger.warning(e.message)
            logger.warning(e.stackTraceToString())
        }

        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>?
    ): List<String>? {
        val pluginCommand = commands[command.name] ?: return null

        return pluginCommand.complete(sender, args?.asList() ?: emptyList(), label)
    }

    fun register() {
        commands.forEach { (command, pluginCommand) ->
            Bukkit.getPluginCommand(command)?.setExecutor(this)
            Bukkit.getPluginCommand(command)?.tabCompleter = this
        }
    }
    
    companion object {
        val ERROR_PREFIX = "${ChatColor.RED}${ChatColor.BOLD}Error:${ChatColor.RESET}${ChatColor.GRAY}"
    }
}