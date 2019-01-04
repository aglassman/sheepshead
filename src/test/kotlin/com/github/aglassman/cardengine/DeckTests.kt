package com.github.aglassman.cardengine

import com.github.aglassman.cardengine.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test

class DeckTests {

  val card_one   = Card(Suit.CLUB, Face.KING)
  val card_two   = Card(Suit.SPADE, Face.QUEEN)
  val card_three = Card(Suit.DIAMOND, Face.TWO)
  val card_four  = Card(Suit.HEART, Face.JACK)
  val card_five   = Card(Suit.CLUB, Face.ACE)
  val card_six = Card(Suit.HEART, Face.FIVE)

  val simpleDeck = listOf(
      card_one,
      card_two,
      card_three,
      card_four,
      card_five,
      card_six)

  @Test
  fun testDeckCreation_verifyCardsLeft() {
    with(Deck(simpleDeck)) {
      assertEquals(6, cardsLeft())
    }
  }

  @Test
  fun testDeckCreation_dealSingeAndVerify() {
    with(Deck(simpleDeck)) {
      assertEquals(6, cardsLeft())
      assertEquals(card_one, deal())
      assertEquals(5, cardsLeft())
      assertEquals(card_two, deal())
      assertEquals(4, cardsLeft())
      assertEquals(card_three, deal())
      assertEquals(3, cardsLeft())
      assertEquals(card_four, deal())
      assertEquals(2, cardsLeft())
      assertEquals(card_five, deal())
      assertEquals(1, cardsLeft())
      assertEquals(card_six, deal())
      assertEquals(0, cardsLeft())
    }
  }

  @Test
  fun testDeckCreation_multiDeal() {
    with(Deck(simpleDeck)) {
      assertEquals(6, cardsLeft())
      assertEquals(listOf(card_one, card_two, card_three), deal(3))
      assertEquals(3, cardsLeft())
      assertEquals(listOf(card_four, card_five, card_six), deal(3))
      assertEquals(0, cardsLeft())
    }
  }

  @Test
  fun testDeckCreation_overDeal() {
    with(Deck(simpleDeck)) {
      assertEquals(6, cardsLeft())
      assertEquals(listOf(card_one, card_two, card_three), deal(3))
      assertEquals(3, cardsLeft())

      try {
        deal(10)
        fail<Any?>("Should have failed due to dealing more cars than are left in the deck.")
      } catch (e: Exception) {
        assertEquals("cannot deal 10 as only 3 remain.", e.message)
      }

    }
  }

  @Test
  fun testStandardDeck_creation() {
    with(StandardDeck()) {
      assertEquals(52, cardsLeft())
    }
  }

}