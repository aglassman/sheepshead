package com.github.aglassman.cardengine.games.sheepshead

import com.github.aglassman.cardengine.GameException
import com.github.aglassman.cardengine.Player
import java.io.Serializable

typealias TeamPoints = List<Pair<Team, Int>>
typealias PlayerScores = List<Pair<Player, Int>>
typealias PlayerScore = Pair<Player, Int>

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
  fun determinePoints(): TeamPoints {

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

    return trickPoints.toList()
        .sortedByDescending { it.second }
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
          determinePoints().maxBy { it.second }?.first ?: throw GameException("Could not determine winner for normal scoring.")
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
            .map { it.first to it.second * 2 }
      }
      Scoring.leaster -> {
        scoreLeaster(teamPoints)
      }
    }
  }

  private fun scoreNormal(teamPoints: TeamPoints): PlayerScores = when(teams.teams().size) {
    2 -> {
      // two teams, assumes there was a picker

      val winners = teamPoints.get(0).first.members
      val losers = teamPoints.get(1).first.members

      val pickerWon = winners.contains(teams.picker as Player)

      val scoreList = mutableListOf<PlayerScore>()

      when(winners.size) {
        1 -> {
          scoreList.add(teams.picker!! to 4)
          losers.forEach { player -> scoreList.add(player to  -1) }
        }
        2 -> {
          scoreList.add(teams.picker!! to 2)
          scoreList.add(teams.partner()!! to 1)
          losers.forEach { player -> scoreList.add(player to  -1) }
        }
        3 -> {
          scoreList.add(teams.picker!! to -2)
          scoreList.add(teams.partner()!! to -1)
          winners.forEach { player -> scoreList.add(player to  1) }
        }
        4 -> {
          scoreList.add(teams.picker!! to -4)
          winners.forEach { player -> scoreList.add(player to  1) }
        }
        else -> {}
      }

      scoreList.toList()
    }
    else -> {
      emptyList()
    }
  }

  // TODO: Tie options
  // 1) draw cards, trump must be drawn to win
  // 2) split pot
  // 3) one tie all tie
  private fun scoreLeaster(teamPoints: TeamPoints): PlayerScores {
    val leasterWinner = trickTracker.tricks()
        .filter { it.trickTaken() }
        .groupBy { it.trickWinner() }
        .entries
        .map { Pair(it.key, it.value.map { it.trickPoints() }.sum()) }
        .minBy { it.second }

    val scoreList = mutableListOf<Pair<Player, Int>>()

    val winner: Player = leasterWinner?.first!!

    scoreList.add(winner to 4)

    teams.allPlayers
        .filter { it != winner }
        .forEach { scoreList.add( it to -1) }

    return scoreList.toList()
  }
}