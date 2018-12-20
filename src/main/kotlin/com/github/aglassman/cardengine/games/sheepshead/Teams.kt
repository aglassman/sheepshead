package com.github.aglassman.cardengine.games.sheepshead

import com.github.aglassman.cardengine.Player

typealias Team = Pair<String,List<Player>>

typealias TeamList = List<Team>

class Teams(
    val style: PartnerStyle,
    val picker: Player? = null,
    val allPlayers: List<Player>
) {

  private var _partner: Player? = null

  fun partner() = _partner

  fun setPartner(player: Player) {
    _partner = player
  }

  fun teamList(): List<Team> {
    val teamList = mutableListOf<Team>()

    if(picker != null && _partner != null) {
      teamList.add("pickers" to listOf(picker, _partner!!).distinct())
      teamList.add("setters" to allPlayers.filter { it != picker || it != _partner })
    } else if(picker != null){
      teamList.add("pickers" to listOf(picker))
      teamList.add("setters" to allPlayers.filter { it != picker })
    } else {
      allPlayers.forEach {
        teamList.add(it.name to listOf(it))
      }
    }

    return teamList.toList()

  }

  fun pickers(): List<Player> {
    return teamList().firstOrNull { it.first == "pickers" }?.second ?: emptyList()
  }

  fun setters(): List<Player> {
    return teamList().firstOrNull { it.first == "setters" }?.second ?: emptyList()
  }

}