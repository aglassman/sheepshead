package com.github.aglassman.cardengine.games.blackjack

import com.github.aglassman.cardengine.*

/**
 * Blackjack rules and play options based off of Hoyle's Modern Encyclopedia of Card Games
 *  by Walter B. Gibson
 */
class Blackjack(
    private val players: List<Player>,
    private val deck: () -> Deck = { BlackjackDeck() },
    private val gameOptions: BlackjackGameOptions = BlackjackGameOptions(),
    private val emitter: EventEmitter = NoOpEmitter()
): Game {

  override fun gameType() = "blackjack"

  private val dealer = Player("dealer")

  init {
    gameOptions.validate(players)
  }

  override fun availableStates(): List<String> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun <T> state(key: String, forPlayer: Player?): T {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun currentPlayer(): Player {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun availableActions() =
      listOf(dealer)
          .plus(players)
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
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun <T> performAction(player: Player, action: String, parameters: Any?): T = try {
    val toPerform = Action.valueOf(action) ?: throw GameException("Unknown Action")

    if(!toPerform.actionAllowed(this, player)){
      throw GameException("Action not allowed.")
    }

    toPerform.performAction(this, player) as T
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