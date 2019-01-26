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

interface Game: Serializable {
  fun gameType(): String

  fun availableStates(): List<String>

  @Throws(GameStateException::class)
  fun <T> state(key: String, forPlayer: Player? = null): T

  fun currentPlayer(): Player

  fun availableActions(player: Player): List<String>

  fun describeAction(action: String): String

  fun actionParameterType(action: String): ParamType?

  fun <T> performAction(player: Player, action: String, parameters: Any? = null): T

  fun isComplete(): Boolean

  fun setEmitter(eventEmitter: EventEmitter)

}