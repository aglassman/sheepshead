package com.github.aglassman.cardengine.bank


open class BankException(message: String): RuntimeException(message)

class UnknownAccount(accoutId: AccountID): BankException("Unknown account: $accoutId")

class InsufficientFundsException(
    balance: Balance,
    withdraw: Int):
    BankException("Insufficient Funds: Withdraw($withdraw) from $balance")