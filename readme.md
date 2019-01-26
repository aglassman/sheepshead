# KtCards

KtCards is a game engine written in Kotlin that is focused on card games.  The intention is to provide a flexible
engine that can support many types of card games.  This includes single, and multiple player games.

The goal is to allow the game engine to be easily wrapped multiple different UI layers.  This would
allow players to play games across many types of devices

Standard Deck Games:
* Poker: 5 Card Stud, Texas Holdem
* Solitaire 

Non Standard Deck Games:
* Sheepshead
* Euchre
* Hearts

Custom Deck Games similar to:
* Uno
* Love Letter
* Coup

Interface Guide
* [GameSession](#gamesession)
* [Game](#game)
* [ConsolePlayer](#consoleplayer)

## GameSession
A GameSession is a long lived object where multiple games can occur.  If you were to 
play multiple rounds of a particular game, this would be managed by a GameSession.  For example, in
Texas Hold'em

When creating a game session, you supply the list of players, the game type, and a map of 
gameConfigurations.  Unknown games, invalid number of 
players, or invalid gameConfigurations will throw a ```GameException``` with a message describing
the reason for the exception.

```GameSession``` does not allow for duplicate players to be within the same instance.

```kotlin
  @Test
  fun createGameSession_sheepsheadGame_verifyType() {
    val players = listOf(
        Player("Andy"),
        Player("Brad"),
        Player("Carl"),
        Player("Deryl"),
        Player("Earl"))

    val gameSession = GameSession(
        gameType = "sheepshead",
        gameConfigurations = mapOf(
            "partnerStyle" to "calledAce"
        ),
        players = players
    )

    gameSession.startNewGame()
    
    val currentGame = gameSession.getCurrentGame()

    assertEquals("sheepshead", currentGame?.gameType())

  }

```

## Player
Player is a basic object that is used only to identify the player within the ```GameSession``` and
```Game```.  Any authentication, and authorization would be handled by the code wrapping the 
```GameSession```.  

For example, a web based UI may use an object such as 
```AuthenticatedPlayer(id = "12345", name="Andrea")``` which extends Player.  This could be passed 
directly to GameSession and Game instances.

No game state, or game session state should ever be stored on instances of the Player object.  The 
```Game``` and ```GameSession``` should manage all state.

## Card
Card provides any game the ability to use a classical card that has a suit, and face value.

## Deck
Deck is provided for games that use a classical deck of cards.  You can easily customize the cards
that are within it, and specify their order.  Allowing cards to be in a specific makes 
it possible to write repeatable test scenarios when developing games.  In a real game, you would
pass the Deck object a pre-shuffled list.


```kotlin
val deck = StandardDeck()

// deal one card
deck.deal()

// deal multiple cards
deck.deal(5)


```

## StandardDeck

## Game

## GameEvents

## CommandParser

## ConsolePlayer