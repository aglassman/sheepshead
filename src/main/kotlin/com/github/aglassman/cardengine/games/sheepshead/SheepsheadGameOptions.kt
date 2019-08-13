package com.github.aglassman.cardengine.games.sheepshead

import com.github.aglassman.cardengine.GameOption
import com.github.aglassman.cardengine.GameOptionDescriptor
import com.github.aglassman.cardengine.GameOptionProvider
import java.io.Serializable


class SheepsheadGameOptions(
    val partnerStyle: PartnerStyle = PartnerStyle.jackOfDiamonds,
    val doubleOnTheBump: Boolean = false,
    val noTrickPickerPays: Boolean = false,
    val blitizing: Boolean = false,
    val crack: Boolean = false,
    val recrack: Boolean = false,
    val misdeal: Boolean = false
): GameOptionProvider, Serializable {

  constructor(optionMap: Map<String, String>): this(
      partnerStyle = optionMap["partnerStyle"]?.let { PartnerStyle.valueOf(it) } ?: PartnerStyle.jackOfDiamonds
  )

  override fun availableOptions() = listOf(
      GameOptionDescriptor(
          "Partner Style",
          "partnerStyle",
          "The default partner style for the table.",
          PartnerStyle.values().map { it.toString() }
      ),
      GameOptionDescriptor(
          "Double on the Bump",
          "doubleOnTheBump",
          "If the picking team loses, they pay double.",
          listOf("true", "false")
      ),
      GameOptionDescriptor(
          "No Trick - Picker Pays",
          "noTrickPickerPays",
          "If the picking team loses, and took no tricks, only the picker pays.  Partner is off the hook.",
          listOf("true", "false")
      ),
      GameOptionDescriptor(
          "Blitzing",
          "blitizing",
          "Allows black and red blitzing.",
          listOf("true", "false")
      ),
      GameOptionDescriptor(
          "Crack",
          "crack",
          "A player who did not exercise their option on the blind can crack, which doubles the scoring.",
          listOf("true", "false")
      ),
      GameOptionDescriptor(
          "Re-crack",
          "recrack",
          "If a picker is cracked, but still confident in their hand, they may recrack, doubling the score again.",
          listOf("true", "false")
      ),
      GameOptionDescriptor(
          "Allow Misdeal",
          "allowMisdeal",
          "If a player has no Ace, Face, or Trump, they may call a misdeal. The current hand is redealt.",
          listOf("true", "false")
      ))

  override fun options() = listOf(
      GameOption("partnerStyle", partnerStyle),
      GameOption("doubleOnTheBump", doubleOnTheBump),
      GameOption("noTrickPickerPays", noTrickPickerPays),
      GameOption("blitizing", blitizing),
      GameOption("crack", crack),
      GameOption("recrack", recrack),
      GameOption("misdeal", misdeal))
      .map { it.key to it }
      .toMap()

}