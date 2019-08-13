package com.github.aglassman.cardengine

/**
 * A basic UI that allows all users to play from the command line.  Basically just for local
 * testing.
 */
class ConsolePlayer {


  fun begin() {

    println("Welcome to Console Cards!")
    val gameType = println("Pick a game type ${GameSession.gameMap.keys}:")
        .let {
          var gameTypeInput = readLine() ?: "unspecified"
          while (!GameSession.gameMap.containsKey(gameTypeInput)) {
            gameTypeInput = println("invalid option").let { readLine() ?: "unspecified" }
          }
          gameTypeInput
        }

    val players = println("Number of players?")
        .let { readLine()!!.toInt() }
        .let { List(it, { index -> println("Player ${index + 1}:").let { Player(readLine()!!) } }) }

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

        val input = Input(readLine()!!)

        processCommand(game, currentPlayer, input).apply(::println)
      } catch (e: Exception) {
        println(e)
      }

    }

  }

}