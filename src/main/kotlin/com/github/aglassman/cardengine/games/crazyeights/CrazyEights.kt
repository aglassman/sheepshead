package com.github.aglassman.cardengine.games.crazyeights

import com.github.aglassman.cardengine.*
import com.github.aglassman.cardengine.games.crazyeights.Action.*
import com.github.aglassman.cardengine.games.crazyeights.State.*


class CrazyEightsDeck(
    cards: List<Card> = StandardDeck().deal(52)
) : Deck(
    deck = cards
) {

  private var pile: List<Card> = emptyList()

  fun cardsDealt() = this.cardsLeft() < 52

  fun topCard() = pile.firstOrNull()

  fun playCard(card: Card) {
    pile = pile.plus(card)
  }

  fun shufflePile() {
    if(cardsGone()) {
      _deck = pile.subList(0, pile.size).shuffled()
      pile = emptyList()
    }
  }


}

fun Card.isCrazy(): Boolean {
  return this.face == Face.EIGHT
}

enum class Action { deal, playCard, drawCard }

enum class State { hand, currentCard, cardsRemaining, reshuffleCount }

/**
 *
 * Bicycle Rules
 * https://www.bicyclecards.com/how-to-play/crazy-eights/
 *
 * THE PACK
 * The standard 52-card pack is used.
 *
 * OBJECT OF THE GAME
 * The goal is to be the first player to get rid of all the cards in his hand.
 *
 * CARD VALUES/SCORING
 * The player who is the first to have no cards left wins the game.
 * The winning player collects from each other player the value of the cards remaining in that
 * player’s hand as follows:
 *
 * Each eight = 50 points
 * Each K, Q, J or 10 = 10 points
 * Each ace = 1 point
 * Each other card is the pip value
 *
 * THE DEAL
 * Deal 5 cards one at a time, face down, beginning with the player to the left.
 * The balance of the pack is placed face down in the center of the table and forms the stock.
 * The dealer turns up the top card and places it in a separate pile; this card is the “starter.”
 * If an eight is turned, it is buried in the middle of the pack and the next card is turned.
 *
 * The Play
 * Starting to the dealer’s left, each player must place one card face up on the starter pile.
 * Each card played (other than an eight) must match the card showing on the starter pile,
 * either in suit or in denomination.
 *
 * Example: If the Q of Clubs is the starter, any club may be played on it or any Queen.
 *
 * If unable to play, cards are drawn from the top of the stock until a play is possible, or until
 * the stock is exhausted. If unable to play when the stock is exhausted, the player must pass. A
 * player may draw from the stock, even though there may be a playable card in the player’s hand.
 *
 * All eights are wild! That is, an eight may be played at any time in turn, and the player need
 * only specify a suit for it (but never a number). The next player must play either a card of the
 * specified suit or an eight.
 *
 */
class CrazyEights(
    players: List<Player>,
    dealerIndex: Int = 0,
    private val deck: CrazyEightsDeck = CrazyEightsDeck()
) : Game {

  private val players = players.map { StandardPlayer(name = it.name) }
  private val dealer: Player
  private var currentPlayerIndex = dealerIndex + 1

  init {
    val playerRange = IntRange(2, 7)
    if (!playerRange.contains(players.size)) {
      throw GameException("Number of players must be within: $playerRange")
    }

    try {
      dealer = players[dealerIndex]
    } catch (e: IndexOutOfBoundsException) {
      throw GameException("dealerIndex must be within bounds of players(size=${players.size}).")
    }
  }

  override fun gameType() = "crazyeights"

  private fun toStandardPlayer(player: Player) =
      players.first { it.name == player.name }

  private fun gameComplete(): Boolean {
    return false
  }

  override fun availableStates(): List<String> {
    return calculateAvailableStates().map { it.name }
  }

  private fun calculateAvailableStates(): List<State> {
    return State.values().asList()
  }

  @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
  override fun <T> state(key: String, forPlayer: Player?): T {

    val state = try {
      State.valueOf(key)
    } catch (e: Exception) {
      throw GameException("($key) is not a valid state")
    }

    return when (state) {
      hand -> forPlayer?.let { toStandardPlayer(it).hand() }
      cardsRemaining -> deck.cardsLeft()
      currentCard -> deck.topCard()
      else -> "unsupported"
    } as T

  }

  override fun dealer() = dealer

  override fun deal() =
      repeat(5, { players.forEach { it.recieveCard(deck.deal()) } } )
          .also {
            deck.playCard(deck.deal())
          }

  override fun currentPlayer(): Player {
    return if(!deck.cardsDealt()) {
      dealer
    } else {
      players.get(currentPlayerIndex % players.size)
    }
  }

  override fun availableActions(player: Player) =
      calculateAvailableActions(player)
          .map { it.name }

  private fun calculateAvailableActions(player: Player): List<Action> {

    val isTurn = player == currentPlayer()

    return when {
      gameComplete() -> emptyList()
      !deck.cardsDealt() -> listOf(deal)
      isTurn && canPlayCard(player) -> listOf(playCard)
      isTurn -> listOf(Action.drawCard)
      else -> emptyList()
    }
  }

  private fun canPlayCard(player: Player): Boolean {
    return false
  }

  override fun describeAction(action: String): String {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }


  @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
  override fun <T> performAction(player: Player, action: String, parameters: Any?): T {

    val actionToPerform = try {
      Action.valueOf(action)
    } catch (e: Exception) {
      throw GameException("($action) is not a valid action.")
    }

    if (!calculateAvailableActions(player).contains(actionToPerform)) {
      throw GameException("Player $player cannot currently perform $actionToPerform")
    }

    return when (actionToPerform) {
      deal -> deal()
      playCard -> ""
      drawCard -> ""
      else -> throw GameException("Unsupported action: $actionToPerform")
    } as T

  }

  override fun isComplete(): Boolean {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

}