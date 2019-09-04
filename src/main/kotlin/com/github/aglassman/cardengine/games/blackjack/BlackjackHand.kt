package com.github.aglassman.cardengine.games.blackjack

import com.github.aglassman.cardengine.Card
import com.github.aglassman.cardengine.Face
import com.github.aglassman.cardengine.games.blackjack.PointType.HARD
import com.github.aglassman.cardengine.games.blackjack.PointType.SOFT


enum class PointType {HARD, SOFT}

data class BlackjackHand(
    private val cards: List<Card> = emptyList(),
    val hideFirstCard: Boolean = false,
    val stay: Boolean = false
) {

  fun showingCards() = cards.drop(if(hideFirstCard) 1 else 0)

  fun points(): List<Int> {
    if(cards.size == 0) {
      return listOf(0)
    }

    return points(showingCards())
  }

  fun pointType() = when (showingCards().indexOfFirst { it.face == Face.ACE }) {
    -1 -> HARD
    else -> SOFT
  }

  fun isHard() = HARD.equals(pointType())

  fun isSoft() = SOFT.equals(pointType())

  fun isBusted() = points().none { it <= 21 }

  fun isNatural21() =
      cards.size == 2
          && cards.map { it.face }.contains(Face.ACE)
          && cards.filter { it.face != Face.ACE }
          .first()
          .pointValue()
          .contains(10)

  fun addCard(card: Card) = this.copy(
      cards = this.cards.plus(card)
  )

  fun hit(card: Card) = addCard(card)

  fun stay() = this.copy(stay = true)

  fun flipFirstCard() = this.copy(hideFirstCard = false)

  override fun toString(): String {
    return "BlackjackHand(hiddenCard=$hideFirstCard showingCards=${showingCards()}, points=${points()})"
  }

}

private fun points(cards: List<Card>): List<Int>  {
  return if(cards.size > 0) {
    cards
        .map { it.pointValue() }
        .reduce { points: List<Int>, acc: List<Int> -> points
            .flatMap { p1 -> acc.map { p2 -> p1 + p2 } }
            .distinct() }
  } else {
    listOf(0)
  }
}

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