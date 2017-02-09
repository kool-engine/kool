package de.fabmax.kool.util

/**
 * @author fabmax
 */
class Property<T>(value: T) {
    var value: T = value
        set(value) {
            field = value
            valueChanged = true
        }

    val clear: T
        get() {
            valueChanged = false
            return value
        }

    var valueChanged = true
        private set
}
