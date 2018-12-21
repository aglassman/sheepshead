package com.github.aglassman.cardengine


class ConsolePlayer {

  val gameSession = GameSession(
      gameType = "sheepshead",
      players = listOf(Player("Andy"), Player("Brad"), Player("Carl"), Player("Deryl"), Player("Earl")))

  fun begin() {

    gameSession.startNewGame()
    val game = gameSession.getCurrentGame()!!

    println("Welcome to Sheepshead!")


    var exit = false


    while (!exit) {
      try {
        val currentPlayer = game.currentPlayer()
        println("${currentPlayer}'s turn. cards: ${cards(currentPlayer.hand())} actions: ${game.availableActions(currentPlayer)}")

        val input: String = readLine()!!

        when {
          input == "hand" -> cards(currentPlayer.hand())
          input.startsWith("playCard ") -> game.performAction(currentPlayer, "playCard", input.substringAfter("playCard ").toInt())
          input.startsWith("bury ") -> game.performAction(currentPlayer, "bury", input.substringAfter("bury ").split(" ").map(String::toInt))
          game.availableActions(currentPlayer).contains(input) -> game.performAction(currentPlayer, input)
          game.availableStates().contains(input) -> game.state(input)
          input == "availableStates" -> game.availableStates()
          input == "currentPlayer" -> currentPlayer
          input == "playerActions" -> game.availableActions(currentPlayer)
          input == "help" -> listOf(
              "availableStates",
              "currentPlayer",
              "playerActions",
              "exit"
          )
          input == "exit" -> {
            exit = true
            "exiting"
          }
          input?.startsWith("describe")
              ?: false -> input?.let { game.describeAction(it.substring(it.indexOf(" ") + 1, it.length)) }
          else -> "unknown command: ${input}"
        }.apply(::println)
      } catch (e: Exception) {
        println(e)
      }

    }

  }

  fun cards(cards: List<Card>) =
      cards
        .mapIndexed { index, card -> "$index:[${card.toUnicodeString()}]" }
        .joinToString { "$it " }

}