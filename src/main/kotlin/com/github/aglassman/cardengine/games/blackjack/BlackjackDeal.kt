package com.github.aglassman.cardengine.games.blackjack

import com.github.aglassman.cardengine.Deck
import com.github.aglassman.cardengine.Player

data class BlackjackHandBet(
    val player: Player,
    val initialBet: Int,
    val blackjackHand: BlackjackHand
)

class BlackjackDeal(
    val dealer: Player,
    val deck: Deck
) {

  private var initialCards = deck.cardsLeft()
  private var currentPlayerOffset: Int = 0

  private var bets: List<BlackjackHandBet> = emptyList()

  fun waitingOn(): Player = bets.get(currentPlayerOffset).player

  fun cardsDealt() = initialCards > deck.cardsLeft()

  fun canPlaceInitialBet(player: Player) = when {
    cardsDealt() -> false
    bets.any { it.player == player } -> false
    else -> true
  }

  fun placeInitialBet(player: Player, amount: Int) {
    bets = bets.plus(BlackjackHandBet(
        player = player,
        initialBet = amount,
        blackjackHand = BlackjackHand()))
  }

  fun dealerHand() = bets.firstOrNull {
    it.player == dealer
  }

  fun playerHand(player: Player) = bets.firstOrNull {
    it.player == player
  }

  fun currentHand(): BlackjackHandBet? = if(bets.size > 0) {
    bets[currentPlayerOffset]
  } else {
    null
  }

  fun hitCurrentHand() {
    val currentHand = bets[currentPlayerOffset]

    if(currentHand.blackjackHand.isBusted()) {
      return
    }

    val updatedHand = bets[currentPlayerOffset].copy(
        blackjackHand = bets[currentPlayerOffset].blackjackHand.hit(deck.deal())
    )

    bets = bets.toMutableList()
        .apply { set(currentPlayerOffset, updatedHand) }
        .toList()

    if(updatedHand.blackjackHand.isBusted()) {
      currentPlayerOffset++
    }

  }

  fun stayCurrentHand() {
    val currentHand = bets[currentPlayerOffset]

    if(currentHand.blackjackHand.isBusted() || currentHand.blackjackHand.stay) {
      return
    }

    val updatedHand = bets[currentPlayerOffset].copy(
        blackjackHand = bets[currentPlayerOffset].blackjackHand.stay()
    )

    bets = bets.toMutableList()
        .apply { set(currentPlayerOffset, updatedHand) }
        .toList()

    currentPlayerOffset++
  }

  fun flipAllCards() {
    bets = bets.map {
      it.copy(
          blackjackHand = it.blackjackHand.flipFirstCard()
      )
    }
  }

  fun flipCardsForPlayer(player: Player) {
    bets.forEachIndexed { index, bet ->
      if(bet.player == player) {
        bets = bets.toMutableList()
            .apply {
              set(
                  index,
                  bet.copy(blackjackHand = bet.blackjackHand.flipFirstCard()))
            }
      }
    }
  }

  fun deal() {
    bets = bets.plus(
        BlackjackHandBet(
        player = dealer,
        initialBet = 0,
        blackjackHand = BlackjackHand(hideFirstCard = true)
    ))

    if(!cardsDealt()) {
      repeat(2, {
        bets.forEachIndexed { index, bet ->
          bets = bets.toMutableList()
              .apply {
                set(
                    index,
                    bet.copy(blackjackHand = bet.blackjackHand.addCard(deck.deal())))
              }
        }
      })
    }

  }

  fun dealComplete() = currentPlayerOffset + 1 > bets.size

}