package com.github.aglassman.cardengine.games.sheepshead

import com.github.aglassman.cardengine.*

private val excludedCards = listOf(Face.TWO, Face.THREE, Face.FOUR, Face.FIVE, Face.SIX)

class SheepsheadDeck : Deck(
    deck = StandardDeck()
        .let { it.deal(it.cardsLeft()) }
        .filterNot { excludedCards.contains(it.face) }
        .shuffled()
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

private val pointMap = mapOf(
    Face.SEVEN to 0,
    Face.EIGHT to 0,
    Face.NINE to 0,
    Face.JACK to 2,
    Face.QUEEN to 3,
    Face.KING to 4,
    Face.TEN to 10,
    Face.ACE to 11
)

private val powerList = listOf(
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
