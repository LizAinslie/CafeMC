package dev.lizainslie.cafemc.economy

import dev.lizainslie.cafemc.CafeMC
import dev.lizainslie.cafemc.economy.data.EconomyAccount
import dev.lizainslie.cafemc.util.AccountUtils
import net.milkbowl.vault.economy.Economy
import net.milkbowl.vault.economy.EconomyResponse
import org.bukkit.OfflinePlayer
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

object CafeEconomy : Economy {
    override fun isEnabled() = CafeMC.instance.isEnabled
    override fun getName() = "CafeMC"
    override fun hasBankSupport() = false
    override fun fractionalDigits() = -1
    override fun format(amount: Double) = "${amount.toString().trimEnd { it == '0' }.trimEnd { it == '.' }} ${if (amount == 1.0) currencyNameSingular() else currencyNamePlural()}"
    override fun currencyNamePlural() = "Credits"
    override fun currencyNameSingular() = "Credit"

    @Deprecated("Deprecated in Java", ReplaceWith("hasAccount(OfflinePlayer)"))
    override fun hasAccount(playerName: String) = true

    override fun hasAccount(player: OfflinePlayer) = true
    
    @Deprecated("Deprecated in Java", ReplaceWith("hasAccount(OfflinePlayer)"))
    override fun hasAccount(playerName: String, worldName: String) = true

    override fun hasAccount(player: OfflinePlayer, worldName: String) = true

    
    private fun getBalance(playerId: UUID) = transaction {
        EconomyAccount.findOrCreate(playerId).balance
    }
    
    @Deprecated("Deprecated in Java")
    override fun getBalance(playerName: String) =
        AccountUtils.getUuidForAccountName(playerName)?.let { getBalance(it) } ?: 0.0

    override fun getBalance(player: OfflinePlayer) = getBalance(player.uniqueId)

    @Deprecated("Deprecated in Java")
    override fun getBalance(playerName: String, worldName: String) =
        AccountUtils.getUuidForAccountName(playerName)?.let { getBalance(it) } ?: 0.0

    override fun getBalance(player: OfflinePlayer, worldName: String) = getBalance(player.uniqueId)

    @Deprecated("Deprecated in Java", ReplaceWith("getBalance(OfflinePlayer)"))
    override fun has(playerName: String, amount: Double) = getBalance(playerName) > amount
    
    override fun has(player: OfflinePlayer, amount: Double) = getBalance(player) > amount
    
    @Deprecated("Deprecated in Java", ReplaceWith("getBalance(OfflinePlayer)"))
    override fun has(playerName: String, worldName: String, amount: Double) = getBalance(playerName) > amount

    override fun has(player: OfflinePlayer, worldName: String, amount: Double) = getBalance(player) > amount
    
    private fun withdraw(playerId: UUID, amount: Double): EconomyResponse {
        if (amount < 0) 
            return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Cannot withdraw negative funds")
        
        return transaction { 
            val account = EconomyAccount.findOrCreate(playerId)
            
            if (account.balance > amount) {
                account.balance -= amount
                
                EconomyResponse(amount, account.balance, EconomyResponse.ResponseType.SUCCESS, "")
            } else EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Insufficient funds")
        }
    }

    @Deprecated("Deprecated in Java")
    override fun withdrawPlayer(playerName: String, amount: Double) = 
        AccountUtils.getUuidForAccountName(playerName)?.let { withdraw(it, amount) }

    override fun withdrawPlayer(player: OfflinePlayer, amount: Double) = withdraw(player.uniqueId, amount)

    @Deprecated("Deprecated in Java")
    override fun withdrawPlayer(playerName: String, worldName: String, amount: Double) =
        AccountUtils.getUuidForAccountName(playerName)?.let { withdraw(it, amount) }

    override fun withdrawPlayer(player: OfflinePlayer, worldName: String, amount: Double) =
        withdraw(player.uniqueId, amount)

    private fun deposit(playerId: UUID, amount: Double): EconomyResponse {
        if (amount < 0)
            return EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.FAILURE, "Cannot deposit negative funds")

        return transaction {
            val account = EconomyAccount.findOrCreate(playerId)

            account.balance += amount
            EconomyResponse(amount, account.balance, EconomyResponse.ResponseType.SUCCESS, "")
        }
    }

    @Deprecated("Deprecated in Java")
    override fun depositPlayer(playerName: String, amount: Double) = 
        AccountUtils.getUuidForAccountName(playerName)?.let { deposit(it, amount) }
    
    override fun depositPlayer(player: OfflinePlayer, amount: Double) = deposit(player.uniqueId, amount)

    @Deprecated("Deprecated in Java")
    override fun depositPlayer(playerName: String, worldName: String, amount: Double) =
        AccountUtils.getUuidForAccountName(playerName)?.let { deposit(it, amount) }

    override fun depositPlayer(player: OfflinePlayer, worldName: String, amount: Double) =
        deposit(player.uniqueId, amount)

    @Deprecated("Deprecated in Java", ReplaceWith("createBank(String, OfflinePlayer"))
    override fun createBank(bankName: String, ownerName: String) = 
        EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null)

    override fun createBank(bankName: String, owner: OfflinePlayer) =
        EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null)

    override fun deleteBank(bankName: String) =
        EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null)

    override fun bankBalance(bankName: String) =
        EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null)

    override fun bankHas(bankName: String, amount: Double) =
        EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null)

    override fun bankWithdraw(bankName: String, amount: Double) =
        EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null)

    override fun bankDeposit(bankName: String, amount: Double) =
        EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null)

    @Deprecated("Deprecated in Java", ReplaceWith("isBankOwner(String, OfflinePlayer)"))
    override fun isBankOwner(bankName: String, playerName: String) =
        EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null)

    override fun isBankOwner(bankName: String, player: OfflinePlayer) =
        EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null)

    @Deprecated("Deprecated in Java", ReplaceWith("isBankMember(String, OfflinePlayer)"))
    override fun isBankMember(bankName: String, playerName: String) =
        EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null)

    override fun isBankMember(bankName: String, player: OfflinePlayer) =
        EconomyResponse(0.0, 0.0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, null)

    override fun getBanks() = emptyList<String>()
    
    @Deprecated("Deprecated in Java", ReplaceWith("createPlayerAccount(OfflinePlayer)"))
    override fun createPlayerAccount(playerName: String) = true
    override fun createPlayerAccount(player: OfflinePlayer) = true
    @Deprecated("Deprecated in Java", ReplaceWith("createPlayerAccount(OfflinePlayer, String)"))
    override fun createPlayerAccount(playerName: String, worldName: String) = true
    override fun createPlayerAccount(player: OfflinePlayer, worldName: String) = true
}
