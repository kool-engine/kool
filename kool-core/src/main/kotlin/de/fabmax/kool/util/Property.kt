package de.fabmax.kool.util

import kotlin.reflect.KProperty

/**
 * @author fabmax
 */
class Property<T>(private var value: T) {

    val clear: T
        get() {
            valueChanged = false
            return value
        }

    var valueChanged = true
        private set

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        if (value != this.value) {
            this.value = value
            valueChanged = true
        }
    }
}
