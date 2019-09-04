package com.github.aglassman.cardengine.games.blackjack

import com.github.aglassman.cardengine.*

/**
 * Blackjack rules and play options based off of Hoyle's Modern Encyclopedia of Card Games
 *  by Walter B. Gibson
 */
class Blackjack(
    players: List<Player>,
    private val deck: () -> Deck = { BlackjackDeck() },
    private val gameOptions: BlackjackGameOptions = BlackjackGameOptions(),
    private val emitter: EventEmitter = NoOpEmitter()
): Game {

  override fun gameType() = "blackjack"

  private val players: List<Player>

  internal fun currentDealer(): Player = currentDeal.dealer


  private var currentDeal: BlackjackDeal

  init {
    gameOptions.validate(players)

    if(gameOptions.dealerStrategy == DealerStrategy.casino) {
      this.players = listOf(Player("dealer")).plus(players)
      currentDeal = BlackjackDeal(this.players.first(), deck())
    } else {
      TODO("Non casino play not yet implemented.")
    }
  }

  internal fun currentDeal(): BlackjackDeal = currentDeal

  override fun availableStates(): List<String> {
    return listOf(
        "hand",
        "dealerHand")
  }

  override fun <T> state(key: String, forPlayer: Player?): T {
    return availableStates()
        .firstOrNull { it == key }
        .let {
          when(it) {
            "hand" -> currentDeal()
                .playerHand(forPlayer ?: throw GameStateException("State 'hand' requires a player to be specified."))
                ?: throw GameStateException("")
            "dealerHand" -> currentDeal()
                .dealerHand()
            else -> throw GameStateException("Currently Unsupported State")
          } as T
        }
        ?: throw GameStateException("Unknown State: $key")
  }

  override fun currentPlayer(): Player {
    return currentDeal().dealer
  }

  override fun availableActions() =
      players
          .map { it to availableActions(it) }
          .toMap()

  override fun availableActions(player: Player) =
      Action.values()
        .filter { it.actionAllowed(this, player) }
        .map { it.toString() }

  override fun describeAction(action: String) = try {
    Action.valueOf(action).let { it.description }
  } catch (e: Exception) {
    throw GameException("Unknown Action")
  }

  override fun actionParameterType(action: String): ParamType? {
    return null
  }

  override fun <T> performAction(player: Player, action: String, parameters: Any?): T = try {
    val toPerform = Action.valueOf(action) ?: throw GameException("Unknown Action")

    if(!toPerform.actionAllowed(this, player)){
      throw GameException("Player: ${player.name} is not currently allowed to perform Action: $action.")
    }

    toPerform.performAction(this, player, parameters) as T
  } catch (e: GameException) {
    throw e
  } catch (e: Exception) {
    throw GameException("Failed to perform action: ${e.message}")
  }

  override fun isComplete() = false

  override fun setEmitter(eventEmitter: EventEmitter) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

}