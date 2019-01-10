package com.github.aglassman.cardengine


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