package com.github.aglassman.cardengine.games.sheepshead

import com.github.aglassman.cardengine.*
import org.slf4j.LoggerFactory

typealias Team = Pair<String, List<Player>>

typealias TeamList = List<Team>

class Teams(
    defaultStyle: PartnerStyle,
    val picker: Player? = null,
    val allPlayers: List<Player>
) {

  private var partnerStyle = defaultStyle

  companion object {
    val LOGGER = LoggerFactory.getLogger(Teams::class.java)
  }

  private var calledSuit: Suit? = null
  private var _partner: Player? = null

  fun partner() = _partner

  fun partnerKnown() = when (partnerStyle) {
    PartnerStyle.jackOfDiamonds -> {
      allPlayers.filter { it.hand().contains(Card(Suit.DIAMOND, Face.JACK)) }.isEmpty()
    }
    PartnerStyle.calledAce -> {
      val calledSuit = calledSuit
          ?: throw GameException("Suit must be called before the partner can be known or unknown.")
      allPlayers.filter { it.hand().contains(Card(calledSuit, Face.ACE)) }.isEmpty()
    }
    PartnerStyle.goAlone -> {
      true
    }
  }

  fun needToCallPartner(): Boolean {
    return partnerStyle == PartnerStyle.goAlone || partnerStyle == PartnerStyle.calledAce && _partner == null
  }

  fun goAlone() {
    partnerStyle = PartnerStyle.goAlone
  }

  fun callPartner(suit: Suit? = null) {
    when (partnerStyle) {
      PartnerStyle.jackOfDiamonds -> {
        _partner = allPlayers.first { it.hand().contains(Card(Suit.DIAMOND, Face.JACK)) }
        LOGGER.info("Jack of Diamands partner is ${_partner}.")
      }
      PartnerStyle.calledAce -> {
        val calledSuit = suit
            ?: throw GameException("Must call a suit when playing calledAce partner style.")
        _partner = allPlayers.first { it.hand().contains(Card(calledSuit, Face.ACE)) }
        LOGGER.info("Ace of ${calledSuit} partner is ${_partner}.")
      }
      PartnerStyle.goAlone -> {
        LOGGER.info("Picker has gone alone.")
      }
    }
  }

  fun teamList(): List<Team> {
    val teamList = mutableListOf<Team>()

    if (picker != null && _partner != null) {
      teamList.add("pickers" to listOf(picker, _partner!!).distinct())
      teamList.add("setters" to allPlayers.filter { it != picker || it != _partner })
    } else if (picker != null) {
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