package dev.lizainslie.cafemc.economy

import dev.lizainslie.cafemc.CafeMC
import dev.lizainslie.cafemc.chat.TableDsl
import dev.lizainslie.cafemc.chat.component
import dev.lizainslie.cafemc.chat.nicknameOrDisplayName
import dev.lizainslie.cafemc.chat.sendRichMessage
import dev.lizainslie.cafemc.chat.table
import dev.lizainslie.cafemc.core.PluginModule
import dev.lizainslie.cafemc.economy.commands.BalanceCommand
import dev.lizainslie.cafemc.economy.commands.DepositCommand
import dev.lizainslie.cafemc.economy.commands.PayCommand
import dev.lizainslie.cafemc.economy.data.PlayerTransaction
import dev.lizainslie.cafemc.economy.data.PlayerTransactionsTable
import dev.lizainslie.cafemc.util.ItemMaps
import dev.lizainslie.cafemc.util.giveItem
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.ServicePriority
import org.jetbrains.exposed.sql.transactions.transaction

object EconomyModule : PluginModule(), Listener {
    val DEPOSIT_BOX_TITLE = component { text("Deposit Box") { color = NamedTextColor.GOLD } }
    
    init {
        commands += BalanceCommand
        commands += PayCommand
        commands += DepositCommand
    }
    
    // region Module Lifecycle
    
    override fun onEnable(cafeMC: CafeMC) {
        super.onEnable(cafeMC)

//        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
//            cafeMC.logger.severe("Vault not initialized, disabling!")
//            Bukkit.getPluginManager().disablePlugin(cafeMC)
//        }

        Bukkit.getServicesManager().register(Economy::class.java, CafeEconomy, cafeMC, ServicePriority.Highest)
    }
    
    // endregion



    // region Public API
    
    fun getNotificationMessage(amount: Double, sender: OfflinePlayer) = component {
        text("You have received ") { color = NamedTextColor.GRAY }
        text(CafeEconomy.format(amount)) { color = NamedTextColor.GREEN }
        text(" from ") { color = NamedTextColor.GRAY }
        text(sender.nicknameOrDisplayName()) { color = NamedTextColor.GOLD }
        text(".") { color = NamedTextColor.GRAY }
    }

    fun getNotificationMessage(amount: Double, sender: Player) = component {
        text("You have received ") { color = NamedTextColor.GRAY }
        text(CafeEconomy.format(amount)) { color = NamedTextColor.GREEN }
        text(" from ") { color = NamedTextColor.GRAY }
        text(sender.nicknameOrDisplayName()) { color = NamedTextColor.GOLD }
        text(".") { color = NamedTextColor.GRAY }
    }
    
    fun openDepositBox(player: Player) {
        val depositBox = Bukkit.createInventory(null, 27, DEPOSIT_BOX_TITLE)
        player.openInventory(depositBox)
    }
    
    // endregion
    
    
    
    // region Private API

    private const val ITEM_HEADER = "Item"
    private const val AMOUNT_HEADER = "Amount"
    private const val VALUE_HEADER = "Value"
    
    private fun generateDepositSummary(depositedItems: Map<Material, Pair<Int, Double>>) = component {
        text("Deposit Summary") { 
            color = NamedTextColor.GOLD
            bold = true
        }
        
        newline()
        newline()
        
        val summaryTable = table(
            listOf(
                TableDsl.Alignment.LEFT,
                TableDsl.Alignment.RIGHT,
                TableDsl.Alignment.RIGHT,
            )
        ) {
            row {
                cell {
                    text(ITEM_HEADER) {
                        color = NamedTextColor.WHITE
                        underlined = true
                    }
                }
                cell {
                    text(AMOUNT_HEADER) {
                        color = NamedTextColor.WHITE
                        underlined = true
                    }
                }
                cell {
                    text(VALUE_HEADER) {
                        color = NamedTextColor.WHITE
                        underlined = true
                    }
                }
            }
            
            depositedItems.forEach { material, (amount, value) ->
                val valuable = ItemMaps.Valuables.fromMaterial(material) ?: return@forEach
                
                row {
                    cell {
                        text(ItemMaps.Valuables.getDisplayName(material) ?: "Unknown") {
                            color = valuable.color
                        }
                    }
                    cell {
                        text(amount.toString()) {
                            color = NamedTextColor.WHITE
                        }
                    }
                    cell {
                        text(CafeEconomy.format(value)) {
                            color = NamedTextColor.GREEN
                        }
                    }
                }
            }
        }
        
        text(summaryTable)
    }
    
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

                recipient.sendMessage(getNotificationMessage(transaction.amount, sender))
                transaction.notified = true
            }
        }
    }
    
    @EventHandler
    fun onDepositBoxClose(event: InventoryCloseEvent) {
        if (event.view.title() != DEPOSIT_BOX_TITLE) return
        
        val player = event.player as Player
        
        var totalAmount = 0.0
        var invalidItems = false
        
        // Material -> pair of amount to value
        val depositedItems = mutableMapOf<Material, Pair<Int, Double>>()
        
        event.inventory.storageContents.forEach { item ->
            if (item == null) return@forEach
            
            val valuable = ItemMaps.Valuables.fromMaterial(item.type) ?: run { 
                player.giveItem(item)
                invalidItems = true
                return@forEach
            }

            val value = valuable.getValueFor(item.type, item.amount)
            
            // add the item to the map or update the existing entry
            depositedItems[item.type] = depositedItems[item.type]?.let {
                (it.first + item.amount) to (it.second + value)
            } ?: (item.amount to value)
            
            totalAmount += value
        }

        if (invalidItems) player.sendRichMessage { 
            text("Some items were not accepted and have been returned to you.") { color = NamedTextColor.GRAY }
        }
        
        if (totalAmount > 0) {
            CafeEconomy.depositPlayer(player, totalAmount)
            
            player.sendRichMessage { 
                text("You have deposited ") { color = NamedTextColor.GRAY }
                text(CafeEconomy.format(totalAmount)) { color = NamedTextColor.GOLD }
                text(" into your account.") { color = NamedTextColor.GRAY }
                
                newline()
                newline()
                
                text("[Click to view a breakdown of your deposit]") { 
                    color = NamedTextColor.GOLD
                    
                    events {
                        click = ClickEvent.callback {
                            it.sendMessage(generateDepositSummary(depositedItems))
                        }
                    }
                }
            }
        }
    }
    
    // endregion
}