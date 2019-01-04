package com.github.aglassman.cardengine


class ConsolePlayer {


  fun begin() {

    println("Welcome to Console Cards!")
    val gameType =  println("Pick a game type ${GameSession.gameMap.keys}:")
        .let {
          var gameTypeInput = readLine() ?: "unspecified"
          while(!GameSession.gameMap.containsKey(gameTypeInput)) {
            gameTypeInput = println("invalid option").let { readLine() ?: "unspecified" }
          }
          gameTypeInput
        }

    val players = println("Number of players?")
        .let { readLine()!!.toInt() }
        .let { List(it, { index ->  println("Player ${index + 1}:").let { Player(readLine()!!) } }) }

    val gameSession = GameSession(
        gameType = gameType,
        players = players)

    gameSession.startNewGame()
    val game = gameSession.getCurrentGame()!!

    var exit = false


    while (!exit) {
      try {
        val currentPlayer = game.currentPlayer()

        val hand = game.state<List<Card>>("hand", currentPlayer)

        println("${currentPlayer}'s turn. cards: ${cardString(hand)} actions: ${game.availableActions(currentPlayer)}")

        val input: String = readLine()!!

        val commands = input.split(" ")
        val command = commands[0]
        val params = commands.subList(1,commands.size)
        val noParams = params.size == 0

        when {
          game.availableActions(currentPlayer).contains(command) -> {
            when(game.actionParameterType(command)) {
              ParamType.Integer -> game.performAction(currentPlayer, command, params[0].toInt())
              ParamType.IntList -> game.performAction(currentPlayer, command, params.map(String::toInt))
              else -> game.performAction(currentPlayer, command)
            }
          }
          game.availableStates().contains(command) -> game.state(command, if(noParams) { currentPlayer } else { Player(params[0]) })
          command == "availableStates" -> game.availableStates()
          command == "currentPlayer" -> currentPlayer
          command == "playerActions" -> game.availableActions(currentPlayer)
          command == "help" -> listOf(
              "availableStates",
              "currentPlayer",
              "playerActions",
              "exit"
          )
          command == "exit" -> {
            exit = true
            "exiting"
          }
          command == "describe" -> game.describeAction(params[0])
          else -> "unknown command: ${input}"
        }.apply(::println)
      } catch (e: Exception) {
        println(e)
      }

    }

  }

}