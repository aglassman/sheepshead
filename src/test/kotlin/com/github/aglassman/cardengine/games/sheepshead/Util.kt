package com.github.aglassman.cardengine.games.sheepshead

import com.github.aglassman.cardengine.Deck

fun generateTestDeckInKotlin(deck: Deck) {

  val cards = deck.deal(deck.cardsLeft())

  val cardString = cards.joinToString(separator = ",\n", transform = { "Card(Suit.${it.suit.name}, Face.${it.face.name})" })

  print("""
      val testDeck = Deck(
        listOf(
          $cardString
        )
      )
    """.trimIndent())
}