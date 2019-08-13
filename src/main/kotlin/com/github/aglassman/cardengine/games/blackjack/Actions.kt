package com.github.aglassman.cardengine.games.blackjack

import com.github.aglassman.cardengine.Player


enum class Action(
    val description: String,
    val actionAllowed: (blackjack: Blackjack, player: Player) -> Boolean = { bj, p -> false },
    val performAction: (blackjack: Blackjack, player: Player) -> Any? = {bj, p ->  }
) {
  bet(
      description = "Place a bet before the deal.",
      actionAllowed = blockDealer),

  sit(
      description = "Sit out for this hand.",
      actionAllowed = blockDealer),

  deal(
      description = "Deal cards to everyone that has placed a bet.",
      actionAllowed = allowDealer),

  hit("Request another card from the dealer."),

  stay("Signal the dealer that you do not want any more cards."),

  doubleDown("Double your bet and recieve only one more card."),

  split("If you have matching cards on the initial deal, you can play two hands if you place another bet.")

}


val blockDealer = { blackjack: Blackjack, player: Player ->  !player.isDealer() }
val allowDealer = { blackjack: Blackjack, player: Player ->  player.isDealer() }