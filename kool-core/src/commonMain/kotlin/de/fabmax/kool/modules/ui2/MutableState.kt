package de.fabmax.kool.modules.ui2

class MutableState<T: Any?>(initValue: T) {
    var isChanged = true

    var value: T = initValue
        set(value) {
            if (value != field) {
                isChanged = true
            }
            field = value
        }

    fun use(uiCtx: UiContext): T {
        uiCtx.usedState += this
        return value
    }
}

fun <T> mutableStateOf(value: T) = MutableState(value)
