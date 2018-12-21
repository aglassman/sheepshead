package com.github.aglassman.cardengine.games.sheepshead

import com.github.aglassman.cardengine.*


class SheepsheadDeck : Deck(
    deck = Suit.values()
        .map { it to Face.values() }
        .flatMap { pair ->
          pair.second
              .filter {
                !listOf(
                    Face.TWO,
                    Face.THREE,
                    Face.FOUR,
                    Face.FIVE,
                    Face.SIX)
                    .contains(it)
              }
              .map {
                Card(pair.first, it)
              }
        }.shuffled()
)

val pointMap = mapOf(
    Face.SEVEN to 0,
    Face.EIGHT to 0,
    Face.NINE to 0,
    Face.JACK to 2,
    Face.QUEEN to 3,
    Face.KING to 4,
    Face.TEN to 10,
    Face.ACE to 11
)

val suitMap = mapOf(
    Suit.CLUB to "clubs",
    Suit.DIAMOND to "diamonds",
    Suit.HEART to "hearts",
    Suit.SPADE to "spades"
)

internal fun Card.sheepsheadSuit(): SheepsheadSuit {
  return when {
    (this.suit == Suit.DIAMOND)
        || (this.face == Face.QUEEN)
        || (this.face == Face.JACK) -> SheepsheadSuit.Trump
    this.suit == Suit.CLUB -> SheepsheadSuit.Club
    this.suit == Suit.SPADE -> SheepsheadSuit.Spade
    this.suit == Suit.HEART -> SheepsheadSuit.Heart
    else -> throw GameException("Suit.${this.suit} could not be mapped to a SheepsheadSuit")
  }
}

internal fun Card.isTrump() = this.sheepsheadSuit() == SheepsheadSuit.Trump

val powerList = listOf(
    Face.SEVEN,
    Face.EIGHT,
    Face.NINE,
    Face.KING,
    Face.TEN,
    Face.ACE,
    Face.JACK,
    Face.QUEEN)

internal fun Card.power() = powerList.indexOf(this.face)

internal fun Card.points() = pointMap.get(this.face) ?: 0


class CardComparitor: Comparator<Card> {
  override fun compare(o1: Card?, o2: Card?): Int {

    if(o1 == null || o2 == null) {
      throw GameException("Cannot sort null cards.")
    }

    return if (o1.isTrump() && !o2.isTrump()) {
      1
    } else {
      o1.power().compareTo(o2.power())
    }
  }

}