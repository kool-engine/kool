package de.fabmax.kool.modules.ui2

fun <T> mutableStateOf(value: T) = MutableStateValue(value)
fun <T: Any> mutableStateListOf(vararg elements: T) = MutableStateList<T>().apply { addAll(elements) }

fun MutableStateValue<Boolean>.toggle() { value = !value }

abstract class MutableState {
    var isStateChanged = true
        private set
    private val usedBy = mutableListOf<UiSurface>()

    protected fun stateChanged() {
        if (!isStateChanged) {
            isStateChanged = true
            for (i in usedBy.indices) {
                usedBy[i].triggerUpdate()
            }
        }
    }

    protected fun usedBy(surface: UiSurface) {
        if (surface !in usedBy) {
            usedBy += surface
            surface.registerState(this)
            if (isStateChanged) {
                surface.triggerUpdate()
            }
        }
    }

    open fun clearUsage(surface: UiSurface) {
        usedBy -= surface
        isStateChanged = false
    }
}

open class MutableStateValue<T: Any?>(initValue: T) : MutableState() {
    private val stateListeners = mutableListOf<(T) -> Unit>()

    var value: T = initValue
        set(value) {
            if (value != field) {
                stateChanged()
                notifyListeners(value)
            }
            field = value
        }

    private fun notifyListeners(newState: T) {
        if (stateListeners.isNotEmpty()) {
            for (i in stateListeners.indices) {
                stateListeners[i](newState)
            }
        }
    }

    fun set(value: T) {
        this.value = value
    }

    fun use(surface: UiSurface): T {
        usedBy(surface)
        return value
    }

    fun onChange(block: (T) -> Unit): MutableStateValue<T> {
        stateListeners += block
        return this
    }

    override fun toString(): String {
        return "mutableStateOf($value)"
    }
}

/**
 * Auto-transforms a mutable state into a different type. Usage:
 * ```
 *     val count = mutableStateOf(0)
 *     val strCount = transformedState(count) { it.toString() }
 * ```
 */
fun <T, S> transformedStateOf(value: MutableStateValue<T>, transformer: (T) -> S) = TransformedStateValue(value, transformer)
class TransformedStateValue<T: Any?, S: Any?>(sourceState: MutableStateValue<T>, transformer: (T) -> S)
    : MutableStateValue<S>(transformer(sourceState.value)) {

    init {
        sourceState.onChange {
            set(transformer(it))
        }
    }
}

class MutableStateList<T>(private val values: MutableList<T> = mutableListOf()) :
    MutableState(), MutableList<T> by values
{
    fun use(surface: UiSurface): MutableStateList<T> {
        usedBy(surface)
        return this
    }

    override fun add(element: T): Boolean {
        stateChanged()
        return values.add(element)
    }

    override fun add(index: Int, element: T) {
        stateChanged()
        values.add(index, element)
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        stateChanged()
        return values.addAll(index, elements)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        stateChanged()
        return values.addAll(elements)
    }

    override fun clear() {
        if (isNotEmpty()) {
            stateChanged()
        }
        values.clear()
    }

    override fun remove(element: T): Boolean {
        val result = values.remove(element)
        if (result) {
            stateChanged()
        }
        return result
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        val result = values.removeAll(elements)
        if (result) {
            stateChanged()
        }
        return result
    }

    override fun removeAt(index: Int): T {
        stateChanged()
        return values.removeAt(index)
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        val result = values.retainAll(elements)
        if (result) {
            stateChanged()
        }
        return result
    }

    override fun set(index: Int, element: T): T {
        stateChanged()
        return values.set(index, element)
    }
}