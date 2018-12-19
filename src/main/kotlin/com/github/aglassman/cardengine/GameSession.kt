package com.github.aglassman.cardengine

import com.github.aglassman.cardengine.games.sheepshead.Sheepshead


class GameSession(
    val gameType: String,
    players: List<Player>
) {

  private val gameMap: Map<String, () -> Game> = mapOf(
      "sheepshead" to { Sheepshead(players) as Game }
  )

  init {
    if(!gameMap.containsKey(gameType)) throw GameException("Unsupported gameType: ($gameType).")
  }

  private var currentGame: Game? = null;



  fun getCurrentGame(): Game? {
    return currentGame
  }

  fun startNewGame() {
    if(currentGame == null) {
      currentGame = gameMap[gameType]?.invoke()
    } else {
      throw GameException("Could not start new game while a current game is still active.")
    }
  }
}