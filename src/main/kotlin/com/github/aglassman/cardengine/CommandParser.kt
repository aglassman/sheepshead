package com.github.aglassman.cardengine

/**
 * A class that contains the raw input from the user.  Provides convenience methods
 * to parse out the command, and parameters.
 */
data class Input(
    val rawInput: String
) {

  val command: String = rawInput.split(" ").first()
  val paramString: String = rawInput.substringAfter(command + " ")

  fun noParams() = paramString.length == 0

  fun paramsAsInt() = paramString.trim().toInt()
  fun paramsAsString() = paramString
  fun paramsAsIntList(delimiter: String = " ") = paramString.split(delimiter).map(String::toInt)
  fun paramsAsStrList(delimiter: String = " ") = paramString.split(delimiter)
}

/**
 * Process an input for a given game and player.
 */
fun processCommand(
    game: Game,
    player: Player,
    input: Input
): Any {

  with(input) {
    return when {
      game.availableActions(player).contains(command) -> {
        when (game.actionParameterType(command)) {
          ParamType.Integer -> game.performAction(player, command, paramsAsInt())
          ParamType.Str -> game.performAction(player, command, paramsAsString())
          ParamType.IntList -> game.performAction(player, command, paramsAsIntList())
          ParamType.StrList -> game.performAction(player, command, paramsAsStrList())
          null -> game.performAction(player, command)
          else -> throw Exception("Unsuported actionParameterType: ${game.actionParameterType(command)}")
        }
      }
      game.availableStates().contains(command) -> game.state(command, player)
      command == "availableStates" -> game.availableStates()
      command == "currentPlayer" -> game.currentPlayer()
      command == "playerActions" -> game.availableActions(player)
      command == "help" -> listOf(
          "availableStates",
          "currentPlayer",
          "playerActions"
      )
      command == "describe" -> game.describeAction(paramsAsString())
      else -> "unknown command: ${command}"
    }
  }
}