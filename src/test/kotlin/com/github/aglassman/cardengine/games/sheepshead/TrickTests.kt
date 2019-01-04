package com.github.aglassman.cardengine.games.sheepshead

import com.github.aglassman.cardengine.Card
import com.github.aglassman.cardengine.Face
import com.github.aglassman.cardengine.StandardPlayer
import com.github.aglassman.cardengine.Suit
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TrickTests {

  @Test
  fun testGetTrickWinner_higherTrumpPlayed() {

    val andy = StandardPlayer("Andy")
    val brad = StandardPlayer("Brad")
    val carl = StandardPlayer("Carl")
    val deryl = StandardPlayer("Deryl")
    val earl = StandardPlayer("Earl")

    val trick = Trick(5)

    andy.recieveCards(listOf(Card(Suit.DIAMOND, Face.TEN)))
    brad.recieveCards(listOf(Card(Suit.HEART, Face.NINE)))
    carl.recieveCards(listOf(Card(Suit.CLUB, Face.JACK)))
    earl.recieveCards(listOf(Card(Suit.SPADE, Face.SEVEN)))
    deryl.recieveCards(listOf(Card(Suit.HEART, Face.ACE)))

    trick.playCard(andy,  0)
    trick.playCard(brad,  0)
    trick.playCard(carl,  0)
    trick.playCard(deryl, 0)
    trick.playCard(earl,  0)

    assertEquals(carl, trick.trickWinner(),"Carl should be the trick winner.")

  }

  @Test
  fun testGetTrickWinner_failSuitTrumped() {

    val andy = StandardPlayer("Andy")
    val brad = StandardPlayer("Brad")
    val carl = StandardPlayer("Carl")
    val deryl = StandardPlayer("Deryl")
    val earl = StandardPlayer("Earl")

    val trick = Trick(5)

    andy.recieveCards(listOf(Card(Suit.SPADE, Face.TEN)))
    brad.recieveCards(listOf(Card(Suit.SPADE, Face.ACE)))
    carl.recieveCards(listOf(Card(Suit.CLUB, Face.SEVEN)))
    deryl.recieveCards(listOf(Card(Suit.SPADE, Face.SEVEN)))
    earl.recieveCards(listOf(Card(Suit.DIAMOND, Face.ACE)))

    trick.playCard(andy,  0)
    trick.playCard(brad,  0)
    trick.playCard(carl,  0)
    trick.playCard(deryl, 0)
    trick.playCard(earl,  0)

    assertEquals(earl, trick.trickWinner(),"Earl should be the trick winner.")

  }

  @Test
  fun testGetTrickWinner_noTrumpPlayed() {

    val andy = StandardPlayer("Andy")
    val brad = StandardPlayer("Brad")
    val carl = StandardPlayer("Carl")
    val deryl = StandardPlayer("Deryl")
    val earl = StandardPlayer("Earl")

    val trick = Trick(5)

    andy.recieveCards(listOf(Card(Suit.SPADE, Face.TEN)))
    brad.recieveCards(listOf(Card(Suit.SPADE, Face.ACE)))
    carl.recieveCards(listOf(Card(Suit.CLUB, Face.SEVEN)))
    earl.recieveCards(listOf(Card(Suit.SPADE, Face.SEVEN)))
    deryl.recieveCards(listOf(Card(Suit.HEART, Face.ACE)))

    trick.playCard(andy,  0)
    trick.playCard(brad,  0)
    trick.playCard(carl,  0)
    trick.playCard(deryl, 0)
    trick.playCard(earl,  0)

    assertEquals(brad, trick.trickWinner(),"Brad should be the trick winner.")

  }

  @Test
  fun testGetTrickWinner_leadWins() {

    val andy = StandardPlayer("Andy")
    val brad = StandardPlayer("Brad")
    val carl = StandardPlayer("Carl")
    val deryl = StandardPlayer("Deryl")
    val earl = StandardPlayer("Earl")

    val trick = Trick(5)

    andy.recieveCards(listOf(Card(Suit.DIAMOND, Face.SEVEN)))
    brad.recieveCards(listOf(Card(Suit.SPADE, Face.ACE)))
    carl.recieveCards(listOf(Card(Suit.CLUB, Face.EIGHT)))
    earl.recieveCards(listOf(Card(Suit.SPADE, Face.EIGHT)))
    deryl.recieveCards(listOf(Card(Suit.HEART, Face.ACE)))

    trick.playCard(andy,  0)
    trick.playCard(brad,  0)
    trick.playCard(carl,  0)
    trick.playCard(deryl, 0)
    trick.playCard(earl,  0)

    assertEquals(andy, trick.trickWinner(),"Andy should be the trick winner.")

  }

}