package com.github.aglassman.cardengine.games.crazyeights

import com.github.aglassman.cardengine.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


val testDeckCards = listOf(
        Card(Suit.CLUB, Face.QUEEN),
        Card(Suit.HEART, Face.NINE),
        Card(Suit.DIAMOND, Face.THREE),
        Card(Suit.SPADE, Face.SEVEN),
        Card(Suit.DIAMOND, Face.SIX),
        Card(Suit.CLUB, Face.TWO),
        Card(Suit.HEART, Face.TWO),
        Card(Suit.SPADE, Face.ACE),
        Card(Suit.DIAMOND, Face.TWO),
        Card(Suit.CLUB, Face.ACE),
        Card(Suit.SPADE, Face.THREE),
        Card(Suit.DIAMOND, Face.TEN),
        Card(Suit.CLUB, Face.SIX),
        Card(Suit.HEART, Face.SIX),
        Card(Suit.CLUB, Face.SEVEN),
        Card(Suit.CLUB, Face.FOUR),
        Card(Suit.CLUB, Face.JACK),
        Card(Suit.CLUB, Face.TEN),
        Card(Suit.DIAMOND, Face.QUEEN),
        Card(Suit.HEART, Face.THREE),
        Card(Suit.DIAMOND, Face.NINE),
        Card(Suit.DIAMOND, Face.SEVEN),
        Card(Suit.CLUB, Face.NINE),
        Card(Suit.HEART, Face.FIVE),
        Card(Suit.DIAMOND, Face.FOUR),
        Card(Suit.HEART, Face.FOUR),
        Card(Suit.SPADE, Face.FIVE),
        Card(Suit.HEART, Face.TEN),
        Card(Suit.HEART, Face.ACE),
        Card(Suit.DIAMOND, Face.ACE),
        Card(Suit.DIAMOND, Face.KING),
        Card(Suit.SPADE, Face.TWO),
        Card(Suit.DIAMOND, Face.FIVE),
        Card(Suit.SPADE, Face.JACK),
        Card(Suit.HEART, Face.KING),
        Card(Suit.HEART, Face.SEVEN),
        Card(Suit.DIAMOND, Face.JACK),
        Card(Suit.SPADE, Face.NINE),
        Card(Suit.CLUB, Face.KING),
        Card(Suit.SPADE, Face.SIX),
        Card(Suit.CLUB, Face.FIVE),
        Card(Suit.DIAMOND, Face.EIGHT),
        Card(Suit.SPADE, Face.TEN),
        Card(Suit.CLUB, Face.THREE),
        Card(Suit.HEART, Face.QUEEN),
        Card(Suit.HEART, Face.JACK),
        Card(Suit.SPADE, Face.FOUR),
        Card(Suit.HEART, Face.EIGHT),
        Card(Suit.SPADE, Face.EIGHT),
        Card(Suit.CLUB, Face.EIGHT),
        Card(Suit.SPADE, Face.KING),
        Card(Suit.SPADE, Face.QUEEN))

class CrazyEightsTestPlay {


  @Test
  fun testDeal_testPlay() {
    val players = List(2,{ Player("player:$it") })

    val game = CrazyEights(
        players = players,
        deck = CrazyEightsDeck(testDeckCards)
    )

    assertEquals(players[0], game.currentPlayer())

    game.availableActions(players[0]).contains("deal")

    assertEquals(52, game.state("cardsRemaining"))

    game.performAction<Any?>(players[0], "deal")

    players.forEach { println("${it.name}(${cardString(game.state("hand", it))})") }

    assertEquals(41, game.state("cardsRemaining"))

    assertEquals(players[1], game.currentPlayer())

    assertEquals(Card(Suit.SPADE, Face.THREE), game.state("currentCard"))

  }
}