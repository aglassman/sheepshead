package com.github.aglassman.cardengine.games

import com.github.aglassman.cardengine.Deck
import com.github.aglassman.cardengine.Player


class FiveHandDeal(
    private val deck: Deck,
    private val playerOrder: List<Player>,
    private val blind: Blind
) {
  fun deal() {
    while (deck.cardsLeft() > 0) {

      playerOrder.forEach {
        it.recieveCards(deck.deal(3))
      }

      if (deck.cardsLeft() == 0) break

      blind.setBlind(deck.deal(2))
    }
  }
}