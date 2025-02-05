package dev.lizainslie.cafemc.core

import dev.lizainslie.cafemc.CafeMC
import dev.lizainslie.cafemc.chat.cmd.PluginCommand
import org.bukkit.event.Listener

abstract class PluginModule {
    val commands: MutableList<PluginCommand> = mutableListOf()
    
    open fun register(cafeMC: CafeMC) {
        cafeMC.logger.info("Registering module: ${this::class.simpleName}")
        
        if (this is Listener)
            cafeMC.server.pluginManager.registerEvents(this, cafeMC)
    }
    
    open fun onEnable(cafeMC: CafeMC) {
        cafeMC.logger.info("Enabling module: ${this::class.simpleName}")
    }
    
    open fun onDisable(cafeMC: CafeMC) {
        cafeMC.logger.info("Disabling module: ${this::class.simpleName}")
    }
}