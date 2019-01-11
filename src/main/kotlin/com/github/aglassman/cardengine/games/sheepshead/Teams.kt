package com.github.aglassman.cardengine.games.sheepshead

import com.github.aglassman.cardengine.*
import org.slf4j.LoggerFactory
import java.io.Serializable

data class Team(
    val name: String,
    val members: List<Player>
): Serializable

class Teams(
    defaultStyle: PartnerStyle,
    val picker: StandardPlayer? = null,
    val allPlayers: List<StandardPlayer>
): Serializable {

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

  fun teams(): List<Team> {
    val teamList = mutableListOf<Team>()

    if (picker != null && partnerKnown()) {
      teamList.add(Team("pickers", listOf(picker, _partner!!).distinct()))
      teamList.add(Team("setters", allPlayers.filter { it != picker || it != _partner }))
    } else if (picker != null) {
      teamList.add(Team("pickers", listOf(picker)))
      teamList.add(Team("setters", allPlayers.filter { it != picker }))
    } else {
      allPlayers.forEach {
        teamList.add(Team(it.name, listOf(it)))
      }
    }

    return teamList.toList()

  }

  fun pickers(): Team? {
    return teams().firstOrNull { it.name == "pickers" }
  }

  fun setters(): Team? {
    return teams().firstOrNull { it.name == "setters" }
  }

}