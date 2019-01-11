package com.github.aglassman.cardengine.games.sheepshead

import com.github.aglassman.cardengine.*
import com.github.aglassman.cardengine.games.sheepshead.Action.*
import com.github.aglassman.cardengine.games.sheepshead.Scoring.normal
import org.slf4j.LoggerFactory
import java.util.Collections.emptyList
import java.util.Collections.rotate

class Sheepshead(
    players: List<Player>,
    private val deck: Deck = SheepsheadDeck(),
    gameNumber: Int = 1,
    val partnerStyle: PartnerStyle = PartnerStyle.jackOfDiamonds,
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

  constructor(players: List<Player>, gameConfigurations: Map<String,String>) : this(
    players = players,
    partnerStyle =  PartnerStyle.valueOf(gameConfigurations.get("partnerStyle") ?: PartnerStyle.jackOfDiamonds.name)
  )

  override fun setEmitter(eventEmitter: EventEmitter) {
    emitter = eventEmitter
  }

  override fun gameType() = "sheepshead"

  private val scoring: Scoring = normal

  private val playerOrder = players
      .map { StandardPlayer(it.name) }
      .toMutableList()
      .apply { rotate(this, -1 * (gameNumber)) }

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
      trickTracker.waitingOnPlayer()
    }
  }

  override fun dealer() = playerOrder.last()

  var teams: Teams? = null

  var blind = Blind(
      playerOrder
  )

  var burriedCards = BurriedCards( numberOfPlayers = players.size )

  var cardsDealt = false

  override fun isComplete() = trickTracker.playIsComplete()

  override fun deal() {
    if (playerOrder.size == 5) {
      FiveHandDeal(deck, playerOrder, blind).deal()
      cardsDealt = true
    } else {
      throw GameException("Unsupported number of players.")
    }
  }

  private fun playCard(player: StandardPlayer, cardIndex: Int) {
    trickTracker.currentTrick().playCard(player, cardIndex)
    trickTracker.currentTrick() // Will trigger creation of next trick if applicable.
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
    return when(partnerStyle) {
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

    return when (actionToPerform) {
      deal -> {
        deal()
        LOGGER.info("$standardPlayer dealt")
        log(standardPlayer, actionToPerform)
      }
      pass -> {
        blind.pass(standardPlayer)
        LOGGER.info("$standardPlayer passed")
        log(standardPlayer, actionToPerform)
      }
      pick -> {
        blind.pick(standardPlayer)
        LOGGER.info("$standardPlayer picked")
        teams = Teams(partnerStyle, standardPlayer, playerOrder)
        log(standardPlayer, actionToPerform)
      }
      bury -> {
        burriedCards.bury(standardPlayer, parameters as List<Int>)
        if(partnerStyle == PartnerStyle.jackOfDiamonds) {
          teams?.callPartner()
        }
        LOGGER.info("$standardPlayer burried")
        log(standardPlayer, actionToPerform)
      }
      callAce -> {
        teams?.callPartner(Suit.valueOf(parameters as String))
        log(standardPlayer, actionToPerform)
      }
      goAlone -> {
        teams?.goAlone()
        log(standardPlayer, actionToPerform)
      }
      playCard -> {
        playCard(standardPlayer, parameters as Int)
        log(standardPlayer, actionToPerform)
      }
      else -> throw GameException("No mapping for action (${actionToPerform.name}).")
    }

  }

  private fun <T> log(player: Player, action: Action) = "${player.name} performed ${action.name}" as T

  override fun availableStates(): List<String> {
    return mutableListOf<String>()
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
                .plus("points")
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
      "currentTrick" -> trickTracker.currentTrick()
      "lastTrickDetails" -> trickTracker.lastTrickDetails()
      "lastTrickWinner" -> trickTracker.lastTrick()?.trickWinner()
      "teams" -> teams?.teams()
      "points" -> points().determinePoints()
      "gameWinner" -> points().determineWinner()
      "score" -> points().determineScore()
      "partnerStyle" -> partnerStyle
      "partnerKnown" -> teams?.partnerKnown()
      else -> throw GameStateException("No game state found for key: ($key)")
    } as T
  }

  private fun points() = Points(
      scoring,
      trickTracker,
      burriedCards,
      teams)

}



