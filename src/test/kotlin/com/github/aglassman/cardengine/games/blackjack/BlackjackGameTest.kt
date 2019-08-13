package com.github.aglassman.cardengine.games.blackjack

import com.github.aglassman.cardengine.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail


class BlackjackGameTest {

  @Test
  fun notEnoughPlayers() {
    try {
      Blackjack(
          players = emptyList(),
          gameOptions = BlackjackGameOptions(availableSeats = 5)
      )
      fail("Should have thrown an exception.")
    } catch (e: Exception) {
      assertEquals("Must have between 1 and 5 players. Currently 0 players.", e.message)
    }
  }

  @Test
  fun tooManyPlayers() {
    try {
      Blackjack(
          players = List(6, { Player("$it") }),
          gameOptions = BlackjackGameOptions(availableSeats = 5)
      )
      fail("Should have thrown an exception.")
    } catch (e: Exception) {
      assertEquals("Must have between 1 and 5 players. Currently 6 players.", e.message)
    }
  }

  @Test
  fun basicGame() {

    val dealer = Player("dealer")
    val andy = Player("andy")
    val brad = Player("brad")
    val carl = Player("carl")

    val testDeck = Deck(
        listOf(
            Card(Suit.HEART, Face.THREE),
            Card(Suit.SPADE, Face.FIVE),
            Card(Suit.SPADE, Face.EIGHT),
            Card(Suit.HEART, Face.TWO),
            Card(Suit.SPADE, Face.TWO),
            Card(Suit.DIAMOND, Face.ACE),
            Card(Suit.HEART, Face.KING),
            Card(Suit.DIAMOND, Face.FOUR),
            Card(Suit.CLUB, Face.EIGHT),
            Card(Suit.CLUB, Face.TEN),
            Card(Suit.DIAMOND, Face.NINE),
            Card(Suit.HEART, Face.SEVEN),
            Card(Suit.CLUB, Face.TWO),
            Card(Suit.SPADE, Face.QUEEN),
            Card(Suit.DIAMOND, Face.QUEEN),
            Card(Suit.SPADE, Face.SEVEN),
            Card(Suit.SPADE, Face.KING),
            Card(Suit.CLUB, Face.SEVEN),
            Card(Suit.DIAMOND, Face.SIX),
            Card(Suit.CLUB, Face.KING),
            Card(Suit.HEART, Face.FIVE),
            Card(Suit.HEART, Face.SIX),
            Card(Suit.SPADE, Face.NINE)))

    val game = Blackjack(
        players = listOf(andy, brad, carl),
        deck = { testDeck },
        gameOptions = BlackjackGameOptions()
    )

    with(game.availableActions()) {
      println(this)
      assertTrue(get(dealer)!!.equals(listOf("deal")), "Dealer should be able to deal.")
      assertTrue(get(andy)!!.equals(listOf("bet", "sit")), "Andy should be able to bet or sit.")
      assertTrue(get(brad)!!.equals(listOf("bet", "sit")), "Brad should be able to bet or sit.")
      assertTrue(get(carl)!!.equals(listOf("bet", "sit")), "Carl should be able to bet or sit.")
    }

    with(game) {
      performAction<Any?>(andy, "bet", 5)
      performAction<Any?>(brad, "bet", 3)
      performAction<Any?>(carl, "bet", 6)
    }


    with(game.availableActions()) {
      println(this)
      assertTrue(get(dealer)!!.equals(listOf("deal")), "Dealer should be able to deal.")
      assertTrue(get(andy)!!.isEmpty(), "Andy should have no actions.")
      assertTrue(get(brad)!!.isEmpty(), "Brad should have no actions.")
      assertTrue(get(carl)!!.isEmpty(), "Carl should have no actions.")
    }
  }

}