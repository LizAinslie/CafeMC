package dev.lizainslie.cafemc

import dev.lizainslie.cafemc.afk.AfkModule
import dev.lizainslie.cafemc.afk.commands.AfkCommand
import dev.lizainslie.cafemc.chat.ChatHandler
import dev.lizainslie.cafemc.chat.cmd.CommandMap
import dev.lizainslie.cafemc.commands.RenameCommand
import dev.lizainslie.cafemc.home.commands.HomeCommand
import dev.lizainslie.cafemc.slime.SlimeListener
import dev.lizainslie.cafemc.tpa.TpaModule
import dev.lizainslie.cafemc.tpa.commands.TpAcceptCommand
import dev.lizainslie.cafemc.tpa.commands.TpDenyCommand
import dev.lizainslie.cafemc.tpa.commands.TpaCommand
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.Database

class CafeMC : JavaPlugin() {
    private val commandMap = CommandMap()
    private val config = getConfig()
    private val modules = listOf(
        TpaModule,
        AfkModule,
    )

    override fun onEnable() {
        instance = this

        saveDefaultConfig()

        if (config.getString("db") == "changeme") {
            logger.warning("Please set the database connection string in config.yml")
            server.pluginManager.disablePlugin(this)
            return
        }

        Database.connect(config.getString("db")!!, driver = "org.sqlite.JDBC")

        Bukkit.getPluginManager().registerEvents(ChatHandler, this)
        Bukkit.getPluginManager().registerEvents(SlimeListener, this)
        
        modules.forEach { 
            it.register(this)
            commandMap += it.commands
        }

        commandMap += RenameCommand
        
        commandMap += HomeCommand

        commandMap.register()
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    companion object {
        lateinit var instance: CafeMC
    }
}
