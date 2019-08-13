package com.github.aglassman.cardengine.games.sheepshead

import com.github.aglassman.cardengine.GameException
import com.github.aglassman.cardengine.Player
import java.io.Serializable

typealias PlayerScores = List<PlayerPoints>

data class GameOutcome(
    val winners: TeamPoints,
    val losers: TeamPoints
) {
  fun byTeamName(name: String) = when(name) {
    winners.team.name -> winners
    losers.team.name -> losers
    else -> throw GameException("No team by name: $name was found.")
  }
}

data class PlayerPoints(
    val player: Player,
    val points: Int
)

data class TeamPoints(
    val team: Team,
    val points: Int
)

/**
 * Points are determined by the type of scoring, the tricks taken by the teams, and any
 * burried cards, or blind cards.
 */
class Points(
    private val scoring: Scoring,
    private val trickTracker: TrickTracker,
    private val burriedCards: BurriedCards,
    private val teams: Teams
): Serializable {

  /**
   * Returns the teams, and their tallied trick points.
   * Returned ranked in descending order by points.
   */
  fun determinePoints(): GameOutcome {

    val trickPoints = trickTracker
        .calculateCurrentPoints(teams)
        .toMutableMap()

    when(scoring) {
      Scoring.leaster -> {
        // need to figure out what to do with blind cards here
      }
      else -> {
        val pickerTeam: Team = teams.pickers()!!

        trickPoints.computeIfPresent(pickerTeam, { team, currentPoints -> currentPoints +  burriedCards.points()} )

      }
    }

    val teamPointList = trickPoints.toList()
        .sortedByDescending { it.second }
        .map { TeamPoints(it.first, it.second) }

    return GameOutcome(teamPointList[0], teamPointList[1])
  }

  fun determineWinner(): Team =
      when(scoring){
        Scoring.leaster -> {
          trickTracker.tricks()
              .filter { it.trickTaken() }
              .minBy { it.trickPoints() }
              ?.let { Team(it.trickWinner()!!.name, listOf(it.trickWinner()!!)) }
              ?: throw GameException("Could not determine winner for leaster scoring.")
        }
        else -> {
          determinePoints().winners.team
        }
      }

  fun determineScore(): PlayerScores  {

    val teamPoints = determinePoints()

    return when (scoring) {
      Scoring.normal -> {
        scoreNormal(teamPoints)
      }
      Scoring.doubler -> {
        scoreNormal(teamPoints)
            .map { PlayerPoints(it.player, it.points * 2) }
      }
      Scoring.leaster -> {
        scoreLeaster(teamPoints)
      }
    }
  }

  private fun scoreNormal(gameOutcome: GameOutcome): PlayerScores = when(teams.teams().size) {
    2 -> {
      // two teams, assumes there was a picker

      val winners = gameOutcome.winners.team.members
      val losers = gameOutcome.losers.team.members

      when(winners.size) {
        1 -> {
          listOf(PlayerPoints(teams.picker!!, 4))
              .plus(losers.map { PlayerPoints(it, -1) })
        }
        2 -> {
          listOf(PlayerPoints(teams.picker!!, 2))
              .plus(PlayerPoints(teams.partner()!!, 1))
              .plus(losers.map { PlayerPoints(it, -1) })
        }
        3 -> {
          listOf(PlayerPoints(teams.picker!!, -2))
              .plus(PlayerPoints(teams.partner()!!, -1))
              .plus(winners.map { PlayerPoints(it, 1) })
        }
        4 -> {
          listOf(PlayerPoints(teams.picker!!, -4))
              .plus(winners.map { PlayerPoints(it, 1) })
        }
        else -> emptyList()
      }
    }
    else -> {
      emptyList()
    }
  }

  // TODO: Tie options
  // 1) draw cards, trump must be drawn to win
  // 2) split pot
  // 3) one tie all tie
  private fun scoreLeaster(gameOutcome: GameOutcome): PlayerScores {

    val leasterWinner = trickTracker.tricks()
        .filter { it.trickTaken() }
        .groupBy { it.trickWinner() }
        .entries
        .map { Pair(it.key, it.value.map { it.trickPoints() }.sum()) }
        .minBy { it.second }

    val winner: Player = leasterWinner?.first!!

    val scores = teams.allPlayers
        .filter { it != winner }
        .map { PlayerPoints(it, -1) }

    return listOf(PlayerPoints(winner, 4))
        .plus(scores)
  }
}