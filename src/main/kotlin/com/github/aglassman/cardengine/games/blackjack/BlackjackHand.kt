package com.github.aglassman.cardengine.games.blackjack

import com.github.aglassman.cardengine.Card
import com.github.aglassman.cardengine.Face
import com.github.aglassman.cardengine.games.blackjack.PointType.HARD
import com.github.aglassman.cardengine.games.blackjack.PointType.SOFT


enum class PointType {HARD, SOFT}

class BlackjackHand {

  val cards: List<Card>

  constructor(cards: List<Card>) {
    this.cards = cards
  }

  fun points() = points(cards)

  fun pointType() = when(points().size) {
    1 -> HARD
    else -> SOFT
  }

  fun isHard() = HARD.equals(pointType())

  fun isSoft() = SOFT.equals(pointType())

  fun isBusted() = points().none { it <= 21 }

  fun isBlackjack() =
      cards.size == 2
          && cards.map { it.face }.contains(Face.ACE)
          && cards.filter { it.face != Face.ACE }
          .first()
          .pointValue()
          .contains(10)

  override fun toString(): String {
    return "BlackjackHand(cards=$cards, points=${points()})"
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as BlackjackHand

    if (cards != other.cards) return false

    return true
  }

  override fun hashCode(): Int {
    return cards.hashCode()
  }


}

fun points(cards: List<Card>): List<Int> = cards
    .map { it.pointValue() }
    .reduce { points: List<Int>, acc: List<Int> -> points
        .flatMap { p1 -> acc.map { p2 -> p1 + p2 } }
        .distinct() }

internal fun Card.pointValue(): List<Int> = pointValue(this.face)

fun pointValue(face: Face) = when(face) {
  Face.TWO -> listOf(2)
  Face.THREE -> listOf(3)
  Face.FOUR -> listOf(4)
  Face.FIVE -> listOf(5)
  Face.SIX -> listOf(6)
  Face.SEVEN -> listOf(7)
  Face.EIGHT -> listOf(8)
  Face.NINE -> listOf(9)
  Face.TEN -> listOf(10)
  Face.TEN, Face.JACK, Face.QUEEN, Face.KING -> listOf(10)
  Face.ACE -> listOf(1,11)
}