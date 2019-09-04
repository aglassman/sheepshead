package com.github.aglassman.cardengine.games.blackjack

import com.github.aglassman.cardengine.Player


enum class Action(
    val description: String,
    val actionAllowed: (blackjack: Blackjack, player: Player) -> Boolean = { bj, p -> false },
    val performAction: (blackjack: Blackjack, player: Player, parameters: Any?) -> Any? = {bj, pl, pa ->  }
) {
  bet(
      description = "Place a bet before the deal.",
      actionAllowed = and(
          listOf(
              blockDealer,
              playerCanPlaceInitialBet)),
      performAction = { bj, player, parameters ->
        bj.currentDeal().placeInitialBet(player, parameters as Int)
      }),

  sit(
      description = "Sit out for this hand.",
      actionAllowed = and(
          listOf(
              blockDealer,
              playerCanPlaceInitialBet))),

  deal(
      description = "Deal cards to everyone that has placed a bet.",
      actionAllowed = and(
          listOf(
              allowDealer,
              { bj, p ->
                !bj.currentDeal().cardsDealt()
              })),
      performAction = { bj, player, params ->
        bj.currentDeal().deal()
      }),

  hit(
      description = "Request another card from the dealer.",
      actionAllowed = and(listOf(
          cardsDealt,
          isCurrentPlayer
      )),
      performAction = { bj, player, params ->
        bj.currentDeal().hitCurrentHand()
      }),

  stay(
      description = "Signal the dealer that you do not want any more cards.",
      actionAllowed = and(listOf(
          cardsDealt,
          isCurrentPlayer
      )),
      performAction = { bj, player, params ->
        bj.currentDeal().stayCurrentHand()
      }),

  doubleDown("Double your bet and recieve only one more card."),

  split("If you have matching cards on the initial deal, you can play two hands if you place another bet.")

}

val cardsDealt  = { blackjack: Blackjack, player: Player ->
  blackjack.currentDeal().cardsDealt()
}

val blockDealer = { blackjack: Blackjack, player: Player ->
  !(blackjack.currentDealer() == player)
}

val allowDealer = { blackjack: Blackjack, player: Player ->
  blackjack.currentDealer() == player
}

val isCurrentPlayer = { blackjack: Blackjack, player: Player ->
  player.equals(blackjack.currentDeal().currentHand()?.player)
}

val playerCanPlaceInitialBet = { blackjack: Blackjack, player: Player ->
  blackjack.currentDeal().canPlaceInitialBet(player)
}

fun and(toReduce: List<(blackjack: Blackjack, player: Player) -> Boolean>)
    : (blackjack: Blackjack, player: Player) -> Boolean = { blackjack: Blackjack, player: Player ->
  toReduce.map { it.invoke(blackjack, player) }.reduce(Boolean::and)
}