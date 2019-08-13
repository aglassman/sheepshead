package com.github.aglassman.cardengine

typealias GameOptions = Map<String, GameOption<*>>
typealias AvailableGameOptions = List<GameOptionDescriptor>

data class GameOptionDescriptor(
    val name: String,
    val key: String,
    val description: String,
    val availableValues: List<String>?
)

data class GameOption<T>(
    val key: String,
    val value: T? = null
)

interface GameOptionProvider {
  fun availableOptions(): AvailableGameOptions
  fun options(): GameOptions
}