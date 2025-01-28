package dev.lizainslie.cafemc.chat

import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

enum class AllowedSender(
    val errorMessage: String
) {
    PLAYER("You must be a player to run this command"),
    CONSOLE("This command can only be run from the server console"),
    ALL("")
    ;

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