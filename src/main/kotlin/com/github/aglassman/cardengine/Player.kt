package com.github.aglassman.cardengine

import java.io.Serializable


open class Player(
    val name: String
): Serializable {



  override fun equals(other: Any?) = when {
    this === other -> true
    other is Player -> name == other.name
    else -> false
  }

  override fun hashCode(): Int {
    return name.hashCode()
  }

  override fun toString(): String {
    return name
  }

}

class StandardPlayer(name: String) : Player(name = name) {
  private var _hand: MutableList<Card> = mutableListOf()

  fun hand() = _hand.toList()

  fun recieveCard(card: Card) {
    _hand.addAll(listOf(card))
  }

  fun recieveCards(cards: List<Card>) {
    // println("$name recieved ${cards.joinToString { "${it.toUnicodeString()}" }}")
    _hand.addAll(cards)
  }

  fun requestCard(cardIndex: Int) = requestCards(listOf(cardIndex)).first()

  fun peekCard(cardIndex: Int): Card {
    return _hand.get(cardIndex)
  }

  fun requestCards(requestedCards: List<Int>): List<Card> {

    requestedCards
        .distinct()
        .forEach {
          if (it > _hand.size) throw GameException("Requested card at index $it, but hand only has ${_hand.size} cards.")
        }

    val foundCards = requestedCards
        .distinct()
        .map {
          _hand[it]
        }

    _hand.removeAll(foundCards)

    return foundCards

  }

  fun isPlayer(player: Player?) = this.name == player?.name

  override fun toString(): String {
    return name
  }
}