package com.github.aglassman.cardengine


interface Game {
  fun gameType(): String

  fun availableStates(): List<String>

  @Throws(GameStateException::class)
  fun <T> state(key: String): T

  fun dealer(): Player

  fun deal()

  fun currentPlayer(): Player

  fun availableActions(player: Player): List<String>

  fun describeAction(action: String): String

  fun <T> performAction(player: Player, action: String, parameters: Any? = null): T

  fun isComplete(): Boolean
}