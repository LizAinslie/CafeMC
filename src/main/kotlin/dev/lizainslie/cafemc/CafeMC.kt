package dev.lizainslie.cafemc

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.lizainslie.cafemc.afk.AfkModule
import dev.lizainslie.cafemc.auditing.AuditModule
import dev.lizainslie.cafemc.chat.ChatModule
import dev.lizainslie.cafemc.core.cmd.CommandMap
import dev.lizainslie.cafemc.commands.RenameCommand
import dev.lizainslie.cafemc.data.commands.MigrateCommand
import dev.lizainslie.cafemc.data.migrate
import dev.lizainslie.cafemc.economy.EconomyModule
import dev.lizainslie.cafemc.item.CustomItemsModule
import dev.lizainslie.cafemc.elytra.ElytraModule
import dev.lizainslie.cafemc.protect.ProtectionModule
import dev.lizainslie.cafemc.slime.SlimeFinderModule
import dev.lizainslie.cafemc.spawner.SpawnerModule
import dev.lizainslie.cafemc.teleport.TeleportModule
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.Database

internal val DB_TYPE_TO_DRIVER = mapOf(
    "mysql" to "com.mysql.cj.jdbc.Driver",
    "sqlite" to "org.sqlite.JDBC",
)

class CafeMC : JavaPlugin() {
    private val commandMap = CommandMap()
    private val config = getConfig()
    private val modules = listOf(
        AuditModule,
        ChatModule,
        TeleportModule,
        AfkModule,
        SlimeFinderModule,
        EconomyModule,
        ProtectionModule,
        CustomItemsModule,
        SpawnerModule,
        ElytraModule,
    )
    
    private lateinit var hikariConfig: HikariConfig
    
    private val hikariDataSource by lazy { HikariDataSource(hikariConfig) }
    
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
            driverClassName = DB_TYPE_TO_DRIVER[config.getString("dbType", "sqlite")]!!
        }

        Database.connect(hikariDataSource)
        
        migrate()
        
        modules.forEach { 
            it.register(this)
            commandMap += it.commands
        }

        commandMap += RenameCommand
        
        commandMap += MigrateCommand

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
