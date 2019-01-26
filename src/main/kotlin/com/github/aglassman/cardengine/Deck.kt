package com.github.aglassman.cardengine

import java.io.Serializable


enum class Suit(val unicode: String) {
  DIAMOND("♢"),
  HEART("♡"),
  SPADE("♠"),
  CLUB("♣")
}

enum class Face(val unicode: String) {
  TWO("2"),
  THREE("3"),
  FOUR("4"),
  FIVE("5"),
  SIX("6"),
  SEVEN("7"),
  EIGHT("8"),
  NINE("9"),
  TEN("10"),
  JACK("J"),
  QUEEN("Q"),
  KING("K"),
  ACE("A")
}

class Card(
    val suit: Suit,
    val face: Face
): Serializable {


  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as Card

    if (suit != other.suit) return false
    if (face != other.face) return false

    return true
  }

  override fun hashCode(): Int {
    var result = suit.hashCode()
    result = 31 * result + face.hashCode()
    return result
  }

  override fun toString(): String {
    return "Card(suit=$suit, face=$face)"
  }

  fun toUnicodeString(): String {
    return "${face.unicode}${suit.unicode}"
  }


}

open class StandardDeck : Deck(
    deck = Suit.values()
        .map { it to Face.values() }
        .flatMap { pair ->
          pair.second
              .map {
                Card(pair.first, it)
              }
        }
        .shuffled()
)

open class Deck(
    deck: List<Card>
): Serializable {

  protected var _deck: List<Card> = deck.toList()

  fun deal(): Card {
    val dealtCard = _deck.first()
    _deck = _deck.slice(1 until _deck.size)
    return dealtCard
  }

  fun deal(numberOfCards: Int = 1): List<Card> {
    if(numberOfCards < 0) {
      throw GameException("Invalid numberOfCards: $numberOfCards")
    }


    if (numberOfCards > _deck.size) {
      throw GameException("cannot deal $numberOfCards as only ${_deck.size} remain.")
    }

    val dealt = _deck.slice(0 until numberOfCards)
    _deck = _deck.slice(numberOfCards until _deck.size)

    return dealt
  }

  fun cardsLeft() = _deck.size

  fun cardsGone() = _deck.size == 0

}

fun cardString(cards: List<Card>) =
    cards
        .mapIndexed { index, card -> "$index:[${card.toUnicodeString()}]" }
        .joinToString { "$it " }

