package com.github.aglassman.cardengine.games.sheepshead

import com.github.aglassman.cardengine.*
import org.slf4j.LoggerFactory
import java.io.Serializable

/**
 * Describes the name of the team, and the list of players on that team.
 */
data class Team(
    val name: String,
    val members: List<Player>
): Serializable

/**
 * Class to keep track of who is on what team.  This is a bit tricky in Sheepshead as it is
 * determined by the number of players, PartnerStyle, who picked the blind, or if nobody picked the
 * blind.
 */
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

  /**
   * Returns the partner if they have been set.
   */
  fun partner() = _partner

  /**
   * Returns true if the partner can be determined.
   */
  fun partnerKnown() = when (partnerStyle) {
    PartnerStyle.jackOfDiamonds -> {
      allPlayers.none { it.hand().contains(Card(Suit.DIAMOND, Face.JACK)) }
    }
    PartnerStyle.calledAce -> {
      val calledSuit = calledSuit
          ?: throw GameException("Suit must be called before the partner can be known or unknown.")
      allPlayers.none { it.hand().contains(Card(calledSuit, Face.ACE)) }
    }
    PartnerStyle.goAlone -> {
      true
    }
  }

  /**
   * Returns true if a partner needs to be called for this game
   */
  fun needToCallPartner(): Boolean {
    return partnerStyle == PartnerStyle.goAlone || partnerStyle == PartnerStyle.calledAce && _partner == null
  }

  /**
   * Tell the Teams object that the partner is going alone.
   */
  fun goAlone() {
    partnerStyle = PartnerStyle.goAlone
  }

  /**
   * Call the partner for this hand.
   */
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

  /**
   * Return the current state of the teams.
   */
  fun teams(): List<Team> {
    var teamList = emptyList<Team>()

    if (picker != null && partnerKnown()) {
      teamList = teamList.plus(Team("pickers", listOf(picker, _partner!!).distinct()))
      teamList = teamList.plus(Team("setters", allPlayers.filter { it != picker || it != _partner }))
    } else if (picker != null) {
      teamList = teamList.plus(Team("pickers", listOf(picker)))
      teamList = teamList.plus(Team("setters", allPlayers.filter { it != picker }))
    } else {
      teamList = teamList.plus(allPlayers.map { Team(it.name, listOf(it)) } )
    }

    return teamList

  }

  /**
   * Return the pickers team if it can be determined yet
   */
  fun pickers(): Team? {
    return teams().firstOrNull { it.name == "pickers" }
  }

  /**
   * Return the setters team if it can be determiend yet
   */
  fun setters(): Team? {
    return teams().firstOrNull { it.name == "setters" }
  }

}