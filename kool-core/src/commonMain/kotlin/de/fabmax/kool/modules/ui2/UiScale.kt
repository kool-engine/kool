package de.fabmax.kool.modules.ui2

object UiScale {

    var uiScale = mutableStateOf(1f)
    var windowScale = mutableStateOf(1f)

    var measuredScale = 1f
        internal set

    fun updateScale(surface: UiSurface) {
        measuredScale = uiScale.use(surface) * windowScale.use(surface)
    }
}