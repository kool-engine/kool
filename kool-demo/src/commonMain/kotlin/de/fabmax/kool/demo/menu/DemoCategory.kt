package de.fabmax.kool.demo.menu

import de.fabmax.kool.util.ColorGradient

class DemoCategory(val title: String, val colorSet: ColorGradient) {

    val entries = mutableListOf<Entry>()

    class Entry(val name: String)
}