package ru.arkharov.statemachine

interface StateRegistry {
	fun register(savable: Savable)
	fun unregister(savable: Savable)
}