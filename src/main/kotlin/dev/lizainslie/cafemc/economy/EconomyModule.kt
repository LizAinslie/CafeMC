package dev.lizainslie.cafemc.economy

import dev.lizainslie.cafemc.CafeMC
import dev.lizainslie.cafemc.core.PluginModule
import dev.lizainslie.cafemc.economy.commands.BalanceCommand
import dev.lizainslie.cafemc.economy.commands.DepositCommand
import dev.lizainslie.cafemc.economy.commands.PayCommand
import dev.lizainslie.cafemc.economy.data.PlayerTransaction
import dev.lizainslie.cafemc.economy.data.PlayerTransactionsTable
import dev.lizainslie.cafemc.util.giveItem
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.RegisteredServiceProvider
import org.bukkit.plugin.ServicePriority
import org.jetbrains.exposed.sql.transactions.transaction

object EconomyModule : PluginModule(), Listener {
    
    init {
        commands += BalanceCommand
        commands += PayCommand
        commands += DepositCommand
    }
    
    // region Module Lifecycle
    
    override fun onEnable(cafeMC: CafeMC) {
        super.onEnable(cafeMC)

        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            cafeMC.logger.severe("Vault not initialized, disabling!")
            Bukkit.getPluginManager().disablePlugin(cafeMC)
        }

        Bukkit.getServicesManager().register(Economy::class.java, CafeEconomy, cafeMC, ServicePriority.Highest)
    }
    
    // endregion
    
    // region Utility
    
    fun getNotificationMessage(amount: Double, sender: String) = 
        "${ChatColor.GRAY}You have received ${ChatColor.GREEN}${CafeEconomy.format(amount)}${ChatColor.GRAY} from ${ChatColor.GOLD}$sender${ChatColor.GRAY}."
    
    // endregion
    
    
    
    // region Event Handlers
    
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player

        transaction {
            val playerTransactions = PlayerTransaction.getForRecipient(player.uniqueId) { PlayerTransactionsTable.notified eq false }

            playerTransactions.forEach { transaction ->
                val recipient = Bukkit.getPlayer(transaction.recipientId) ?: return@forEach
                val sender = Bukkit.getOfflinePlayer(transaction.senderId)

                recipient.sendMessage(getNotificationMessage(transaction.amount, sender.name!!))
                transaction.notified = true
            }
        }
    }
    
    @EventHandler
    fun onDepositBoxClose(event: InventoryCloseEvent) {
        if (event.view.title != "${ChatColor.GOLD}Deposit Box") return
        
        val player = event.player as Player
        
        var totalAmount = 0.0
        var invalidItems = false
        
        event.inventory.storageContents.forEach { item ->
            if (item == null) return@forEach
            
            when (item.type) {
                Material.DIAMOND -> {
                    val amount = item.amount * 1000.0
                    totalAmount += amount
                }
                Material.EMERALD -> {
                    val amount = item.amount * 500.0
                    totalAmount += amount
                }
                Material.GOLD_INGOT -> {
                    val amount = item.amount * 250.0
                    totalAmount += amount
                }
                Material.COPPER_INGOT -> {
                    val amount = item.amount * 100.0
                    totalAmount += amount
                }
                Material.IRON_INGOT -> {
                    val amount = item.amount * 50.0
                    totalAmount += amount
                }
                else -> {
                    player.giveItem(item)
                    invalidItems = true
                }
            }
        }
        
        if (invalidItems) {
            player.sendMessage("${ChatColor.GRAY}Some items were not accepted and have been returned to you.")
        }
        
        if (totalAmount > 0) {
            CafeEconomy.depositPlayer(player, totalAmount)
            player.sendMessage("${ChatColor.GRAY}You have deposited ${ChatColor.GOLD}${CafeEconomy.format(totalAmount)}${ChatColor.GRAY} into your account.")
        }
    }
    
    // endregion
}