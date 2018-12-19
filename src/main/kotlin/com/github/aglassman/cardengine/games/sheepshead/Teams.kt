package com.github.aglassman.cardengine.games.sheepshead

import com.github.aglassman.cardengine.Player


class Teams(
    val style: PartnerStyle,
    val picker: Player
) {

  private var _partner: Player? = null

  fun partner() = _partner

  fun setPartner(player: Player) {
    _partner = player
  }

}