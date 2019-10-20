package de.fabmax.kool.drawqueue

import de.fabmax.kool.util.MutableColor

class SceneSetup {

    val clearColor = MutableColor(0.05f, 0.15f, 0.25f, 1f)
    var clearMask = CLEAR_COLOR or CLEAR_DEPTH

    companion object {
        const val CLEAR_COLOR = 1
        const val CLEAR_DEPTH = 2
    }
}
