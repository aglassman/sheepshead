package com.github.aglassman.cardengine

import com.github.aglassman.cardengine.games.blackjack.Blackjack
import com.github.aglassman.cardengine.games.blackjack.BlackjackGameOptions
import com.github.aglassman.cardengine.games.crazyeights.CrazyEights
import com.github.aglassman.cardengine.games.sheepshead.Sheepshead
import com.github.aglassman.cardengine.games.sheepshead.SheepsheadGameOptions


class GameSession(
    val gameType: String,
    val gameConfigurations: Map<String, String> = emptyMap(),
    val players: List<Player>
) {

  companion object {
    val gameMap: Map<String, (players: List<Player>, gameConfigurations: Map<String, String>) -> Game> = mapOf(
        "sheepshead" to { players, gameConfigurations -> Sheepshead(players = players, gameOptions = SheepsheadGameOptions(gameConfigurations)) },
        "crazyeights" to { players, gameConfigurations -> CrazyEights(players = players) },
        "blackjack" to { players, gameConfigurations -> Blackjack(players = players, gameOptions = BlackjackGameOptions(gameConfigurations)) }
    )
  }

  init {
    if(players.distinct().size != players.size) throw GameException("There are duplicate players in the GameSession.")
    if (!gameMap.containsKey(gameType)) throw GameException("Unsupported gameType: ($gameType).")
  }

  private var currentGame: Game? = null

  fun getCurrentGame(): Game? {
    return currentGame
  }

  fun startNewGame() {
    if (currentGame == null) {
      currentGame = gameMap[gameType]?.invoke(players, gameConfigurations)
    } else {
      throw GameException("Could not start new game while a current game is still active.")
    }
  }
}