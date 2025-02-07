package dev.lizainslie.cafemc

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.lizainslie.cafemc.afk.AfkModule
import dev.lizainslie.cafemc.chat.ChatHandler
import dev.lizainslie.cafemc.chat.cmd.CommandMap
import dev.lizainslie.cafemc.commands.RenameCommand
import dev.lizainslie.cafemc.home.commands.HomeCommand
import dev.lizainslie.cafemc.slime.SlimeFinderModule
import dev.lizainslie.cafemc.tpa.TpaModule
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.Database

class CafeMC : JavaPlugin() {
    private val commandMap = CommandMap()
    private val config = getConfig()
    private val modules = listOf(
        TpaModule,
        AfkModule,
        SlimeFinderModule,
    )
    
    private lateinit var hikariConfig: HikariConfig
    
    val hikariDataSource by lazy { HikariDataSource(hikariConfig) }

    override fun onEnable() {
        instance = this

        saveDefaultConfig()

        if (config.getString("db") == "changeme") {
            logger.warning("Please set the database connection string in config.yml")
            server.pluginManager.disablePlugin(this)
            return
        }
        
        hikariConfig = HikariConfig().apply {
            jdbcUrl = config.getString("db")!!
            driverClassName = "org.sqlite.JDBC"
        }

        Database.connect(hikariDataSource)

        Bukkit.getPluginManager().registerEvents(ChatHandler, this)
        
        modules.forEach { 
            it.register(this)
            commandMap += it.commands
        }

        commandMap += RenameCommand
        
        commandMap += HomeCommand

        commandMap.register()
        
        modules.forEach { it.onEnable(this) }
    }

    override fun onDisable() {
        modules.forEach { it.onDisable(this) }
    }

    companion object {
        lateinit var instance: CafeMC
    }
}
