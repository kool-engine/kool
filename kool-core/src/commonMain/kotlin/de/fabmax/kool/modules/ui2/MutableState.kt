package de.fabmax.kool.modules.ui2

open class MutableState {
    private var isStateChanged = true
    private var usedBy: UiSurface? = null

    protected fun stateChanged() {
        if (!isStateChanged) {
            isStateChanged = true
            usedBy?.triggerUpdate(this)
        }
    }

    protected fun usedBy(surface: UiSurface) {
        usedBy = surface
        if (isStateChanged) {
            surface.triggerUpdate(this)
        }
    }

    fun clear() {
        usedBy = null
        isStateChanged = false
    }
}

class MutableValueState<T: Any?>(initValue: T) : MutableState() {
    var value: T = initValue
        set(value) {
            if (value != field) {
                stateChanged()
            }
            field = value
        }

    fun use(surface: UiSurface): T {
        usedBy(surface)
        return value
    }
}

fun <T> mutableStateOf(value: T) = MutableValueState(value)
