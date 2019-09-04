package com.github.aglassman.cardengine.bank

import com.github.aglassman.cardengine.Player
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail


class BankTest {

  @Test
  fun createDepositAndTransferTest() {

    val bank = InMemoryBank()

    val andy = Player("andy")
    val brad = Player("brad")

    val account1 = bank.initializeAccount(andy,100).accountId
    val account2 = bank.initializeAccount(brad,100).accountId


    with (bank.checkBalance(account1)) {
      assertEquals(100, balance)
    }

    with (bank.checkBalance(account2)) {
      assertEquals(100, balance)
    }

    with (bank.transfer(account1, account2, 50) ) {
      with (first { it.accountId == account1 } ) {
        assertEquals(50, balance)
      }
      with (first { it.accountId == account2 } ) {
        assertEquals(150, balance)
      }
    }

    with (bank.checkBalance(account1)) {
      assertEquals(50, balance)
    }

    with (bank.checkBalance(account2)) {
      assertEquals(150, balance)
    }

  }

  @Test
  fun createDepositAndTransferTest_InsufficientFunds() {

    val bank = InMemoryBank()

    val andy = Player("andy")
    val brad = Player("brad")

    val account1 = bank.initializeAccount(andy, 100).accountId
    val account2 = bank.initializeAccount(brad, 100).accountId


    with (bank.checkBalance(account1)) {
      assertEquals(100, balance)
    }

    with (bank.checkBalance(account2)) {
      assertEquals(100, balance)
    }

    try {
      bank.transfer(account1, account2, 101)
      fail("Expecting InsufficientFundsException")
    } catch (e: Exception) {
      assertTrue(e.message?.contains("Insufficient Funds: Withdraw(101) from Balance(accountId=$account1") ?: false)
      assertTrue(e.message?.contains("player=andy") ?: false)
    }

    with (bank.checkBalance(account1)) {
      assertEquals(100, balance)
    }

    with (bank.checkBalance(account2)) {
      assertEquals(100, balance)
    }

  }

}