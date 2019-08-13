package com.github.aglassman.cardengine.games.sheepshead

import com.github.aglassman.cardengine.*
import com.github.aglassman.cardengine.games.sheepshead.Action.*
import com.github.aglassman.cardengine.games.sheepshead.Scoring.normal
import org.slf4j.LoggerFactory
import java.util.Collections.emptyList
import java.util.Collections.rotate

/**
 *
 * players - The list of players involved in the game, in the table order they are sitting.
 * deck - The deck of cards to use. Game does not re-shuffle the deck
 * gameNumber - The number game in the round.  This determines dealer
 * partnerStyle - Specifies the type of partner style if applicable.
 * emitter - Reference to the game event emitter.
 *
 */
class Sheepshead(
    players: List<Player>,
    private val deck: Deck = SheepsheadDeck(),
    gameNumber: Int = 1,
    val gameOptions: SheepsheadGameOptions = SheepsheadGameOptions(),
    private var emitter: EventEmitter = NoOpEmitter()
) : Game {

  companion object {
    val LOGGER = LoggerFactory.getLogger(Sheepshead::class.java)
  }

  init {
    if(gameNumber < 1) {
      throw GameException("gameNumber cannot be < 1")
    }
  }

  override fun setEmitter(eventEmitter: EventEmitter) {
    emitter = eventEmitter
  }

  override fun gameType() = "sheepshead"

  private val scoring: Scoring = normal

  private val playerOrder = players
      .map { StandardPlayer(it.name) }
      .toMutableList()
      .apply { rotate(this, -1 * (gameNumber)) }
      .toList()


  private val trickTracker = TrickTracker(playerOrder = playerOrder)

  private fun toStandardPlayer(player: Player) =
      playerOrder.firstOrNull { it.name == player.name } ?: throw GameException("$player is not a member of the current game.")

  override fun currentPlayer(): Player {
    return if(!cardsDealt) {
      dealer()
    } else if(blind.option() != null) {
      blind.option()!!
    } else if(!burriedCards.cardsBurried()) {
      teams?.picker!!
    } else if(teams?.needToCallPartner() ?: false) {
      teams?.picker!!
    } else {
      trickTracker.waitingOnPlayer() ?: dealer()
    }
  }

  fun dealer() = playerOrder.last()

  var teams: Teams? = null

  var blind = Blind(
      playerOrder
  )

  var burriedCards = BurriedCards( numberOfPlayers = players.size )

  var cardsDealt = false

  override fun isComplete() = trickTracker.playIsComplete()

  fun deal() {
    if (playerOrder.size == 5) {
      FiveHandDeal(deck, playerOrder, blind).deal()
      cardsDealt = true
    } else {
      throw GameException("Unsupported number of players.")
    }
  }

  private fun playCard(player: StandardPlayer, cardIndex: Int) {
    trickTracker.newTrick()
    trickTracker.currentTrick()?.playCard(player, cardIndex)
  }

  override fun availableActions(player: Player) = listAvailableActions(toStandardPlayer(player)).map { it.name }

  override fun describeAction(action: String): String {
    return try {
      Action.valueOf(action).let { "${it.describe(this)}" }
    } catch (e: Exception) {
      throw GameException("($action) is not a valid action.")
    }
  }

  override fun actionParameterType(action: String): ParamType? {
    return try {
      Action.valueOf(action).let { it.paramType }
    } catch (e: Exception) {
      throw GameException("($action) is not a valid action.")
    }
  }

  private fun listAvailableActions(player: StandardPlayer): List<Action> {

    val isDealer = player.isPlayer(dealer())

    return when {
      // deal
      isDealer && !cardsDealt -> listOf(deal)

      // blind
      !blind.blindRoundComplete() -> blindActions(player)
      !burriedCards.cardsBurried() && player == teams?.picker -> listOf(Action.bury)
      teams?.needToCallPartner() ?: false -> partnerActions()
      player.isPlayer(trickTracker.waitingOnPlayer()) -> listOf(playCard)
      blind.blindRoundComplete() && !(teams?.needToCallPartner() ?: true) -> listOf(beginPlay)

      // play
      else -> emptyList()
    }

  }

  private fun blindActions(player: StandardPlayer): List<Action> {
    return when {
      blind.isAvailable() && blind.playerHasOption(player) && blind.hasLastOption(player) -> listOf(Action.pick, Action.callLeaster, Action.callDoubler)
      blind.isAvailable() && blind.playerHasOption(player) -> listOf(Action.pick, Action.pass)
      else -> emptyList()
    }
  }

  private fun partnerActions(): List<Action> {
    return when(gameOptions.partnerStyle) {
      PartnerStyle.calledAce -> listOf(callAce)
      else -> listOf(goAlone, startPlay)
    }
  }

  override fun <T> performAction(player: Player, action: String, parameters: Any?): T {

    val standardPlayer = toStandardPlayer(player)

    val actionToPerform = try {
      Action.valueOf(action)
    } catch (e: Exception) {
      throw GameException("($action) is not a valid action.")
    }

    if (!listAvailableActions(standardPlayer).contains(actionToPerform)) {
      throw GameException("Player ${standardPlayer.name} cannot perform $action at this time.")
    }

    emitter.emit(GameEvent(targetPlayer = standardPlayer, eventType = actionToPerform.name))

    val actions = mapOf(
        deal to {
          deal()
          LOGGER.info("$standardPlayer dealt")
          log<T>(standardPlayer, actionToPerform)
        },
        pass to {
          blind.pass(standardPlayer)
          LOGGER.info("$standardPlayer passed")
          log<T>(standardPlayer, actionToPerform)
        },
        pick to {
          blind.pick(standardPlayer)
          LOGGER.info("$standardPlayer picked")
          teams = Teams(gameOptions.partnerStyle, standardPlayer, playerOrder)
          log<T>(standardPlayer, actionToPerform)
        },
        bury to {
          burriedCards.bury(standardPlayer, parameters as List<Int>)
          if(gameOptions.partnerStyle == PartnerStyle.jackOfDiamonds) {
            teams?.callPartner()
          }
          LOGGER.info("$standardPlayer burried")
          log<T>(standardPlayer, actionToPerform)
        },
        callAce to {
          teams?.callPartner(Suit.valueOf(parameters as String))
          log<T>(standardPlayer, actionToPerform)
        },
        goAlone to {
          teams?.goAlone()
          log<T>(standardPlayer, actionToPerform)
        },
        playCard to {
          playCard(standardPlayer, parameters as Int)
          log<T>(standardPlayer, actionToPerform)
        },
        beginPlay to {
          trickTracker.newTrick()
          log<T>(standardPlayer, actionToPerform)
        }
    )

    return actions[actionToPerform]
        ?.invoke()
        ?: throw GameException("No mapping for action (${actionToPerform.name}).")


  }

  private fun <T> log(player: Player, action: Action) = "${player.name} performed ${action.name}" as T

  override fun availableStates(): List<String> {
    return listOf<String>()
        .plus("hand")
        .plus("blind")
        .plus("partnerStyle")
        .plus("partnerKnown")
        .plus("currentTrick")
        .let {
          if(trickTracker.lastTrick() != null)
            it
                .plus("lastTrickDetails")
                .plus("lastTrickWinner")
          else
            it
        }
        .let {
          if(teams != null) {
            it.plus("teams")
          } else {
            it
          }
        }
        .let {
          if(trickTracker.playIsComplete()) {
            it
                .plus("gameOutcome")
                .plus("gameWinner")
                .plus("score")
          } else {
            it
          }
        }
  }

  @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
  override fun <T> state(key: String, forPlayer: Player?): T {

    if(!availableStates().contains(key)) {
      throw GameException("state ($key) is unavailable")
    }

    return when (key) {
      "hand" -> toStandardPlayer(forPlayer ?: throw GameException("Must specify player for state (hand).")).hand()
      "blind" -> blind.peek()
      "currentTrick" -> trickTracker.currentTrick()?.state()
      "lastTrickDetails" -> trickTracker.lastTrickDetails()
      "lastTrickWinner" -> trickTracker.lastTrick()?.trickWinner()
      "teams" -> teams?.teams()
      "gameOutcome" -> points().determinePoints()
      "gameWinner" -> points().determineWinner()
      "score" -> points().determineScore()
      "partnerStyle" -> gameOptions.partnerStyle
      "partnerKnown" -> teams?.partnerKnown()
      else -> throw GameStateException("No game state found for key: ($key)")
    } as T
  }

  private fun points() = Points(
      scoring,
      trickTracker,
      burriedCards,
      teams ?: throw GameStateException("Cannot calculate points when teams are unknown."))

}



