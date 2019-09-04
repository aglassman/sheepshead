package com.github.aglassman.cardengine.games.blackjack

import com.github.aglassman.cardengine.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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
  fun basicCasinoGame() {

    val dealer = Player("dealer")
    val andy = Player("andy")
    val brad = Player("brad")
    val carl = Player("carl")

    val testDeck = Deck(
        listOf(
            Card(Suit.HEART, Face.THREE), // Andy
            Card(Suit.SPADE, Face.FIVE), // Brad
            Card(Suit.SPADE, Face.EIGHT), // Carl
            Card(Suit.HEART, Face.TWO), // Dealer

            Card(Suit.SPADE, Face.TWO), // Andy
            Card(Suit.DIAMOND, Face.ACE), // Brad
            Card(Suit.HEART, Face.KING), // Carl
            Card(Suit.DIAMOND, Face.FOUR), // Dealer

            Card(Suit.CLUB, Face.EIGHT), // Andy Hit
            Card(Suit.CLUB, Face.TEN), // Andy Hit

            Card(Suit.DIAMOND, Face.NINE), // Brad Hit
            Card(Suit.HEART, Face.SEVEN), // Brad Hit

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
        gameOptions = BlackjackGameOptions(
            dealerStrategy = DealerStrategy.casino
        )
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

    game.performAction<Any?>(dealer, "deal")

    // Verify hand state after deal

    with(game.state<BlackjackHandBet>("hand", andy)) {
      println(this.toString())
      assertEquals(andy, player)
      assertEquals(5, initialBet)
      assertEquals(listOf(
          Card(Suit.HEART, Face.THREE),
          Card(Suit.SPADE, Face.TWO)),
          blackjackHand.showingCards())
      assertFalse(blackjackHand.stay)
      assertEquals(listOf(5), blackjackHand.points())
      assertEquals(PointType.HARD, blackjackHand.pointType())
      assertFalse(blackjackHand.isBusted())
      assertFalse(blackjackHand.isNatural21())
    }

    with(game.state<BlackjackHandBet>("hand", brad)) {
      println(this.toString())
      assertEquals(brad, player)
      assertEquals(3, initialBet)
      assertEquals(listOf(
          Card(Suit.SPADE, Face.FIVE),
          Card(Suit.DIAMOND, Face.ACE)),
          blackjackHand.showingCards())
      assertFalse(blackjackHand.stay)
      assertEquals(listOf(6, 16), blackjackHand.points())
      assertEquals(PointType.SOFT, blackjackHand.pointType())
      assertFalse(blackjackHand.isBusted())
      assertFalse(blackjackHand.isNatural21())
    }

    with(game.state<BlackjackHandBet>("hand", carl)) {
      println(this.toString())
      assertEquals(carl, player)
      assertEquals(6, initialBet)
      assertEquals(listOf(
          Card(Suit.SPADE, Face.EIGHT),
          Card(Suit.HEART, Face.KING)),
          blackjackHand.showingCards())
      assertFalse(blackjackHand.stay)
      assertEquals(listOf(18), blackjackHand.points())
      assertEquals(PointType.HARD, blackjackHand.pointType())
      assertFalse(blackjackHand.isBusted())
      assertFalse(blackjackHand.isNatural21())
    }

    with(game.state<BlackjackHandBet>("hand", dealer)) {
      println(this.toString())
      assertEquals(dealer, player)
      assertEquals(0, initialBet)
      assertEquals(listOf(
          Card(Suit.DIAMOND, Face.FOUR)),
          blackjackHand.showingCards())
      assertTrue(blackjackHand.hideFirstCard)
      assertFalse(blackjackHand.stay)
      assertEquals(listOf(4), blackjackHand.points())
      assertEquals(PointType.HARD, blackjackHand.pointType())
      assertFalse(blackjackHand.isBusted())
      assertFalse(blackjackHand.isNatural21())
    }

    // Verify available actions after deal.
    with(game.availableActions()) {
      println(this)
      assertTrue(get(dealer)!!.isEmpty(), "Dealer should have no actions.")
      assertTrue(get(andy)!!.containsAll(listOf("hit", "stay")), "Andy should be able to hit or stay.")
      assertTrue(get(brad)!!.isEmpty(), "Brad should have no actions.")
      assertTrue(get(carl)!!.isEmpty(), "Carl should have no actions.")
    }

    // Attempt out of order hit
    try {
      game.performAction<Any?>(brad, "hit")
      fail("Should not be allowed to perform hit.")
    } catch (e: Exception) {
      assertEquals("Player: brad is not currently allowed to perform Action: hit.", e.message)
    }

    game.performAction<Any?>(andy, "hit")
    game.performAction<Any?>(andy, "hit")

    // Verify available actions after Andy's hits.
    with(game.availableActions()) {
      println(this)
      assertTrue(get(dealer)!!.isEmpty(), "Dealer should have no actions.")
      assertTrue(get(andy)!!.isEmpty(), "Andy should have no actions.")
      assertTrue(get(brad)!!.containsAll(listOf("hit", "stay")), "Brad should be able to hit or stay.")
      assertTrue(get(carl)!!.isEmpty(), "Carl should have no actions.")
    }

    with(game.state<BlackjackHandBet>("hand", andy)) {
      println(this.toString())
      assertEquals(andy, player)
      assertEquals(5, initialBet)
      assertEquals(listOf(
          Card(Suit.HEART, Face.THREE),
          Card(Suit.SPADE, Face.TWO),
          Card(Suit.CLUB, Face.EIGHT),
          Card(Suit.CLUB, Face.TEN)),
          blackjackHand.showingCards())
      assertFalse(blackjackHand.stay)
      assertEquals(listOf(23), blackjackHand.points())
      assertEquals(PointType.HARD, blackjackHand.pointType())
      assertTrue(blackjackHand.isBusted())
      assertFalse(blackjackHand.isNatural21())
    }

    game.performAction<Any?>(brad, "hit")
    game.performAction<Any?>(brad, "hit")

    // Verify available actions after Brad's hits.
    with(game.availableActions()) {
      println(this)
      assertTrue(get(dealer)!!.isEmpty(), "Dealer should have no actions.")
      assertTrue(get(andy)!!.isEmpty(), "Andy should have no actions.")
      assertTrue(get(brad)!!.isEmpty(), "Brad should have no actions.")
      assertTrue(get(carl)!!.containsAll(listOf("hit", "stay")), "Carl should be able to hit or stay.")
    }

    with(game.state<BlackjackHandBet>("hand", brad)) {
      println(this.toString())
      assertEquals(brad, player)
      assertEquals(3, initialBet)
      assertEquals(listOf(
          Card(Suit.SPADE, Face.FIVE),
          Card(Suit.DIAMOND, Face.ACE),
          Card(Suit.DIAMOND, Face.NINE),
          Card(Suit.HEART, Face.SEVEN)),
          blackjackHand.showingCards())
      assertFalse(blackjackHand.stay)
      assertEquals(listOf(22, 32), blackjackHand.points())
      assertEquals(PointType.SOFT, blackjackHand.pointType())
      assertTrue(blackjackHand.isBusted())
      assertFalse(blackjackHand.isNatural21())
    }

    game.performAction<Any?>(carl, "stay")

    // Verify available actions after Carl's hits.
    with(game.availableActions()) {
      println(this)
      assertTrue(get(dealer)!!.containsAll(listOf("hit", "stay")), "Dealer should be able to hit or stay.")
      assertTrue(get(andy)!!.isEmpty(), "Andy should have no actions.")
      assertTrue(get(brad)!!.isEmpty(), "Brad should have no actions.")
      assertTrue(get(carl)!!.isEmpty(), "Carl should have no actions.")
    }

    with(game.state<BlackjackHandBet>("hand", carl)) {
      println(this.toString())
      assertEquals(carl, player)
      assertEquals(6, initialBet)
      assertEquals(listOf(
          Card(Suit.SPADE, Face.EIGHT),
          Card(Suit.HEART, Face.KING)),
          blackjackHand.showingCards())
      assertTrue(blackjackHand.stay)
      assertEquals(listOf(18), blackjackHand.points())
      assertEquals(PointType.HARD, blackjackHand.pointType())
      assertFalse(blackjackHand.isBusted())
      assertFalse(blackjackHand.isNatural21())
    }
  }

}