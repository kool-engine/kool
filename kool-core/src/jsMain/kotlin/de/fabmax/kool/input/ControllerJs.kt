package de.fabmax.kool.input

import de.fabmax.kool.platform.navigator

actual fun getGamepadState(index: Int): de.fabmax.kool.Gamepad? = navigator.getGamepads()?.get(index)