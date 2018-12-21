package com.github.aglassman.cardengine.games.sheepshead

enum class PartnerStyle {
  goAlone,
  calledAce,
  jackOfDiamonds }

enum class Action(
    val describe: (game: Sheepshead) -> String = { "no description" }
) {
  deal({"Deal the cards for the game. Current dealer is ${it.dealer()}."}),
  pick({"Pick the cards in the blind."}),
  pass({"Pass the option to pick the blind to the next player."}),
  bury({"Bury the same number of cards you picked up from the blind."}),
  callLeaster({"Call a leaster if you have the last option on the blind."}),
  callDoubler({"Call a doubler if you have the last option on the blind."}),
  playCard({"Play a card if it is your turn."}),
  partnerStyle({""}),
  goAlone({"Declare you want no partner after you pick"}),
  startPlay({"If you're playig jackOfDiamonds, and you don't want to go alone, you can startPlay."}),
  declareUndercard({
    "If the picker has all trump, but would still like a partner, they may pick a " +
      "trump to play face down to proxy as a card of the suit they call."}),
  callAce({"After a blind pick, the picker can choose to call a partner by picking a fail suit. " +
      "The picker must have at least one of that fail suit.  The player that holds the Ace of " +
      "the called suit will be the partner."})
}

enum class Scoring {
  normal,
  leaster,
  doubler }

enum class SheepsheadSuit {
  Trump,
  Club,
  Spade,
  Heart }
