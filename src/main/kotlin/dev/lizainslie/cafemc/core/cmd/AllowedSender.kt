package dev.lizainslie.cafemc.core.cmd

import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

/**
 * Enum class to determine who can run a command
 */
enum class AllowedSender(
    val errorMessage: String
) {
    PLAYER("You must be a player to run this command"),
    CONSOLE("This command can only be run from the server console"),
    ALL("")
    ;

    /**
     * Check if the [sender] is allowed to run the command
     */
    fun check(sender: CommandSender): Boolean {
        return when (this) {
            PLAYER -> sender is Player
            CONSOLE -> sender is ConsoleCommandSender
            ALL -> true
        }
    }

    companion object {
        fun fromSender(sender: CommandSender) {
            when (sender) {
                is Player -> PLAYER
                is ConsoleCommandSender -> CONSOLE
            }
        }
    }
}