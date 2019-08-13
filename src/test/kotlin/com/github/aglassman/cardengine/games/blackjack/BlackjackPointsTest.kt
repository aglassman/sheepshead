package com.github.aglassman.cardengine.games.blackjack

import com.github.aglassman.cardengine.Card
import com.github.aglassman.cardengine.Face
import com.github.aglassman.cardengine.Suit
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class BlackjackPointsTest {

  @Test
  fun testHardCount() {

    val hardHand = BlackjackHand(listOf(Card(Suit.CLUB, Face.QUEEN)))

    assertTrue(hardHand.isHard())
    assertFalse(hardHand.isSoft())
    assertEquals(PointType.HARD, hardHand.pointType())
    assertEquals(1, hardHand.points().size)
    assertEquals(10, hardHand.points().first())
  }

  @Test
  fun testSoftCount() {
    val softHand = BlackjackHand(listOf(Card(Suit.CLUB, Face.ACE)))

    assertTrue(softHand.isSoft())
    assertFalse(softHand.isHard())
    assertEquals(PointType.SOFT, softHand.pointType())
    assertEquals(2, softHand.points().size)
    assertEquals(1, softHand.points().first())
    assertEquals(11, softHand.points().last())
  }

  @Test
  fun testMultiCardHard() {

    val hardHand = BlackjackHand(listOf(
        Card(Suit.DIAMOND, Face.TWO),
        Card(Suit.CLUB, Face.NINE),
        Card(Suit.CLUB, Face.THREE)))

    val points = hardHand.points()

    assertEquals(listOf(14), points)
  }

  @Test
  fun testMultiCardSoft() {

    val softHand = BlackjackHand(listOf(
        Card(Suit.DIAMOND, Face.ACE),
        Card(Suit.CLUB, Face.TWO)))

    val points = softHand.points()

    assertEquals(listOf(3, 13), points)
  }

  @Test
  fun testMultiCardSoft_TwoAces() {

    val softHand = BlackjackHand(listOf(
        Card(Suit.DIAMOND, Face.ACE),
        Card(Suit.HEART, Face.ACE),
        Card(Suit.CLUB, Face.TWO)))

    val points = softHand.points()

    assertEquals(listOf(4, 14, 24), points)
  }

  @Test
  fun testBlackjack() {

    val softHand = BlackjackHand(listOf(
        Card(Suit.DIAMOND, Face.ACE),
        Card(Suit.HEART, Face.ACE),
        Card(Suit.CLUB, Face.TWO)))

    val points = softHand.points()

    assertEquals(listOf(4, 14, 24), points)
  }

}