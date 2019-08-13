package com.github.aglassman.cardengine

import java.io.Serializable

enum class ParamType(
    val type: String
) {
  Integer("Int"),
  Str("String"),
  Any("Map<String, Any>"),
  IntList("List<Int>"),
  StrList("List<String>"),
  StrMap("Map<String,String>"),
  StrIntMap("Map<String,Int>"),
  IntStrMap("Map<Int,String>"),
  AnyMap("Map<String,Any>")
}

interface Game: GameStateProvider, Serializable {
  /**
   * Returns the type of game being played.
   */
  fun gameType(): String

  /**
   * Returns players that currently can perform an action.
   */
  fun currentPlayer(): Player

  /**
   * Returns all available actions for a given player.
   */
  fun availableActions(player: Player): List<String>

  /**
   * Return a description for a given action.
   */
  fun describeAction(action: String): String

  /**
   * Return the type of a provided action.  This helps the client determine how to parse user input
   * for a given action.
   */
  fun actionParameterType(action: String): ParamType?

  /**
   * Perform an action as a player, with the provided parameters.
   */
  fun <T> performAction(player: Player, action: String, parameters: Any? = null): T

  /**
   * Returns true if the game has completed.
   */
  fun isComplete(): Boolean

  /**
   * Set the emitter for the game.
   */
  fun setEmitter(eventEmitter: EventEmitter)

  fun availableActions(): Map<Player, List<String>> = emptyMap()
}

interface GameStateProvider {
  /**
   * List the available states for this game.
   */
  fun availableStates(): List<String>

  /**
   * Return the state of the game for a given player.
   */
  @Throws(GameStateException::class)
  fun <T> state(key: String, forPlayer: Player? = null): T

}