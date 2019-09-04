package com.github.aglassman.cardengine.bank

import com.github.aglassman.cardengine.Player
import java.time.OffsetDateTime
import java.util.*

typealias AccountID = UUID

data class Balance(
    val accountId: AccountID,
    val player: Player,
    val balance: Int,
    val dateTime: OffsetDateTime)

interface Bank {

  /**
   * Initialize account.  If account exists, exception is thrown.
   */
  fun initializeAccount(player: Player, balance: Int): Balance

  fun deleteAccount(accountId: AccountID)

  fun accounts(player: Player): List<AccountID>

  /**
   * Check the balance
   */
  fun checkBalance(accountId: AccountID): Balance

  /**
   * Transfer amount from one player to another.  Return the resulting balances of those two
   * players in a map.
   */
  fun transfer(from: AccountID, to: AccountID, amount: Int): List<Balance>

  /**
   * Deposit the specified amount into a players account.
   */
  fun deposit(accountId: AccountID, amount: Int): Balance

  /**
   * Withdraw the specified amoun tinto a players account.
   */
  fun withdraw(accountId: AccountID, amount: Int): Balance

}