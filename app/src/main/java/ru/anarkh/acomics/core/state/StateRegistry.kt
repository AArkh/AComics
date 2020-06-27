package ru.anarkh.acomics.core.state

interface StateRegistry {
	fun register(savable: Savable)
	fun unregister(savable: Savable)
}