package de.fabmax.kool.modules.ui2

import de.fabmax.kool.scene.Group

class UiSurface(
    name: String = "uiSurface",
    private val block: BoxScope.() -> Unit
) : Group(name) {

    private val uiCtx = UiContext(this)

    init {
        onUpdate += {
            uiCtx.updateUi(it, block)
        }
    }
}
