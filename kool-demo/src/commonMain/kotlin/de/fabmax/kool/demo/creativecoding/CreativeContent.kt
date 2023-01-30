package de.fabmax.kool.demo.creativecoding

import de.fabmax.kool.modules.ui2.UiScope
import de.fabmax.kool.scene.Group

abstract class CreativeContent(name: String) : Group(name) {

    abstract fun UiScope.settingsMenu()

    override fun toString(): String {
        return "$name"
    }
}