package com.github.aglassman.cardengine.games.sheepshead

import com.github.aglassman.cardengine.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SheepsheadDealTest {


  val testSheepsDeck = Deck(
      listOf(
          // Andy
          Card(Suit.CLUB, Face.TEN),
          Card(Suit.DIAMOND, Face.EIGHT),
          Card(Suit.DIAMOND, Face.TEN),

          // Brad
          Card(Suit.SPADE, Face.JACK),
          Card(Suit.CLUB, Face.JACK),
          Card(Suit.HEART, Face.KING),

          // Carl
          Card(Suit.CLUB, Face.ACE),
          Card(Suit.SPADE, Face.TEN),
          Card(Suit.SPADE, Face.SEVEN),

          // Deryl
          Card(Suit.CLUB, Face.EIGHT),
          Card(Suit.CLUB, Face.QUEEN),
          Card(Suit.DIAMOND, Face.KING),

          // Earl
          Card(Suit.SPADE, Face.ACE),
          Card(Suit.CLUB, Face.NINE),
          Card(Suit.HEART, Face.EIGHT),

          // blind
          Card(Suit.HEART, Face.ACE),
          Card(Suit.DIAMOND, Face.NINE),

          // Andy
          Card(Suit.HEART, Face.NINE),
          Card(Suit.HEART, Face.JACK),
          Card(Suit.DIAMOND, Face.SEVEN),

          // Brad
          Card(Suit.SPADE, Face.KING),
          Card(Suit.HEART, Face.SEVEN),
          Card(Suit.SPADE, Face.NINE),

          // Carl
          Card(Suit.HEART, Face.TEN),
          Card(Suit.SPADE, Face.EIGHT),
          Card(Suit.CLUB, Face.SEVEN),

          // Daryl
          Card(Suit.DIAMOND, Face.JACK),
          Card(Suit.DIAMOND, Face.ACE),
          Card(Suit.DIAMOND, Face.QUEEN),

          // Earl
          Card(Suit.SPADE, Face.QUEEN),
          Card(Suit.CLUB, Face.KING),
          Card(Suit.HEART, Face.QUEEN)
      )
  )

  @Test
  fun startSheepshead_deal_verifyDeal() {

    val andy = Player("Andy")
    val brad = Player("Brad")
    val carl = Player("Carl")
    val deryl = Player("Deryl")
    val earl = Player("Earl")

    val players = listOf(andy, brad, carl, deryl, earl)

    val game = Sheepshead(
        players = players,
        deck = testSheepsDeck,
        gameNumber = 1
    )

    game.deal()

    with(game.state<List<Card>>("hand", andy)) {
      assertEquals(6, size)
      containsAll(
          listOf(
              Card(Suit.CLUB, Face.TEN),
              Card(Suit.DIAMOND, Face.EIGHT),
              Card(Suit.DIAMOND, Face.TEN),
              Card(Suit.HEART, Face.NINE),
              Card(Suit.HEART, Face.JACK),
              Card(Suit.DIAMOND, Face.SEVEN)))
    }

    with(game.state<List<Card>>("hand", brad)) {
      assertEquals(6, size)
      containsAll(
          listOf(
              Card(Suit.SPADE, Face.JACK),
              Card(Suit.CLUB, Face.JACK),
              Card(Suit.HEART, Face.KING),
              Card(Suit.SPADE, Face.KING),
              Card(Suit.HEART, Face.SEVEN),
              Card(Suit.SPADE, Face.NINE)
          ))
    }

    with(game.state<List<Card>>("hand", carl)) {
      assertEquals(6, size)
      containsAll(
          listOf(
              Card(Suit.CLUB, Face.ACE),
              Card(Suit.SPADE, Face.TEN),
              Card(Suit.SPADE, Face.SEVEN),
              Card(Suit.HEART, Face.TEN),
              Card(Suit.SPADE, Face.EIGHT),
              Card(Suit.CLUB, Face.SEVEN)
          ))
    }

    with(game.state<List<Card>>("hand", deryl)) {
      assertEquals(6, size)
      containsAll(
          listOf(
              Card(Suit.CLUB, Face.EIGHT),
              Card(Suit.CLUB, Face.QUEEN),
              Card(Suit.DIAMOND, Face.KING),
              Card(Suit.DIAMOND, Face.JACK),
              Card(Suit.DIAMOND, Face.ACE),
              Card(Suit.DIAMOND, Face.QUEEN)
          ))
    }

    with(game.state<List<Card>>("hand", earl)) {
      assertEquals(6, size)
      containsAll(
          listOf(
              Card(Suit.SPADE, Face.ACE),
              Card(Suit.CLUB, Face.NINE),
              Card(Suit.HEART, Face.EIGHT),
              Card(Suit.SPADE, Face.QUEEN),
              Card(Suit.CLUB, Face.KING),
              Card(Suit.HEART, Face.QUEEN)
          ))
    }

    with(game.state<List<Card>>("blind")) {
      assertEquals(2, size)
      containsAll(
          listOf(
              Card(Suit.HEART, Face.ACE),
              Card(Suit.DIAMOND, Face.NINE)
          )
      )
    }
  }

  @Test
  fun startSheepshead_deal_secondDeal_verifyDeal() {

    val andy = Player("Andy")
    val brad = Player("Brad")
    val carl = Player("Carl")
    val deryl = Player("Deryl")
    val earl = Player("Earl")

    val players = listOf(andy, brad, carl, deryl, earl)

    val game = Sheepshead(
        players = players,
        deck = testSheepsDeck,
        gameNumber = 1
    )

    game.deal()

    with(game.state<List<Card>>("hand", andy)) {
      assertEquals(6, size)
      containsAll(
          listOf(
              Card(Suit.SPADE, Face.ACE),
              Card(Suit.CLUB, Face.NINE),
              Card(Suit.HEART, Face.EIGHT),
              Card(Suit.SPADE, Face.QUEEN),
              Card(Suit.CLUB, Face.KING),
              Card(Suit.HEART, Face.QUEEN)
          ))
    }

    with(game.state<List<Card>>("hand", brad)) {
      assertEquals(6, size)
      containsAll(
          listOf(
              Card(Suit.CLUB, Face.TEN),
              Card(Suit.DIAMOND, Face.EIGHT),
              Card(Suit.DIAMOND, Face.TEN),
              Card(Suit.HEART, Face.NINE),
              Card(Suit.HEART, Face.JACK),
              Card(Suit.DIAMOND, Face.SEVEN)
          ))
    }

    with(game.state<List<Card>>("hand", carl)) {
      assertEquals(6, size)
      containsAll(
          listOf(
              Card(Suit.SPADE, Face.JACK),
              Card(Suit.CLUB, Face.JACK),
              Card(Suit.HEART, Face.KING),
              Card(Suit.SPADE, Face.KING),
              Card(Suit.HEART, Face.SEVEN),
              Card(Suit.SPADE, Face.NINE)
          ))
    }

    with(game.state<List<Card>>("hand", deryl)) {
      assertEquals(6, size)
      containsAll(
          listOf(
              Card(Suit.CLUB, Face.ACE),
              Card(Suit.SPADE, Face.TEN),
              Card(Suit.SPADE, Face.SEVEN),
              Card(Suit.HEART, Face.TEN),
              Card(Suit.SPADE, Face.EIGHT),
              Card(Suit.CLUB, Face.SEVEN)
          ))
    }

    with(game.state<List<Card>>("hand", earl)) {
      assertEquals(6, size)
      containsAll(
          listOf(
              Card(Suit.CLUB, Face.EIGHT),
              Card(Suit.CLUB, Face.QUEEN),
              Card(Suit.DIAMOND, Face.KING),
              Card(Suit.DIAMOND, Face.JACK),
              Card(Suit.DIAMOND, Face.ACE),
              Card(Suit.DIAMOND, Face.QUEEN)
          ))
    }

    with(game.state<List<Card>>("blind")) {
      assertEquals(2, size)
      containsAll(
          listOf(
              Card(Suit.HEART, Face.ACE),
              Card(Suit.DIAMOND, Face.NINE)
          )
      )
    }
  }

}