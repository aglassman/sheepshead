package com.github.aglassman.cardengine.bank

import com.github.aglassman.cardengine.Player
import java.time.OffsetDateTime
import java.util.*


class InMemoryBank: Bank {

  private val accounts: MutableMap<AccountID, Balance> = mutableMapOf()

  override fun initializeAccount(player: Player, balance: Int): Balance {
    return UUID.randomUUID()
        .let { Balance(
          accountId = it,
          player = player,
          balance = balance,
          dateTime = OffsetDateTime.now()) }
        .let {
          accounts.put(it.accountId, it)
          it
        }
  }

  override fun deleteAccount(accountId: AccountID) {
    synchronized(accounts) {
      accounts.remove(accountId)
    }
  }

  override fun accounts(player: Player): List<AccountID> {
    return accounts.values
        .filter { it.player == player }
        .map { it.accountId }
  }

  override fun checkBalance(accountId: AccountID) = find(accountId)

  override fun transfer(from: AccountID, to: AccountID, amount: Int): List<Balance> {
    synchronized(accounts) {
      val fromBal = find(from)
      val toBal = find(to)

      when {
        (fromBal.balance - amount) < 0 -> throw InsufficientFundsException(fromBal, amount)
        else -> {
          accounts.replace(
              fromBal.accountId,
              fromBal.copy(
                  balance = fromBal.balance - amount))
          accounts.replace(
              toBal.accountId,
              toBal.copy(
                  balance = toBal.balance + amount))
        }
      }

      return listOf(from, to).map { find(it) }
    }
  }

  override fun deposit(accountId: AccountID, amount: Int): Balance {
    synchronized(accounts) {
      find(accountId).also {
        accounts.replace(
            it.accountId,
            it.copy(
                balance = it.balance + amount
            ))
      }
      return find(accountId)
    }
  }

  override fun withdraw(accountId: AccountID, amount: Int): Balance {
    synchronized(accounts) {
      find(accountId).also {

        if((it.balance - amount) < 0) {
          throw InsufficientFundsException(it, amount)
        }

        accounts.replace(
            it.accountId,
            it.copy(
                balance = it.balance - amount
            ))
      }
      return find(accountId)
    }
  }

  private fun find(accountId: AccountID) = accounts[accountId] ?: throw UnknownAccount(accountId)

}