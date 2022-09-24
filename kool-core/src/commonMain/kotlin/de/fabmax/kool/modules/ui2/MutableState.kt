package de.fabmax.kool.modules.ui2

fun <T> mutableStateOf(value: T) = MutableValueState(value)
fun <T: Any> mutableListStateOf(vararg elements: T) = MutableListState<T>().apply { addAll(elements) }

abstract class MutableState {
    var isStateChanged = true
        private set
    private var usedBy = mutableListOf<UiSurface>()

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

open class MutableValueState<T: Any?>(initValue: T) : MutableState() {
    var value: T = initValue
        set(value) {
            if (value != field) {
                stateChanged()
            }
            field = value
        }

    fun set(value: T) {
        this.value = value
    }

    fun use(surface: UiSurface): T {
        usedBy(surface)
        return value
    }
}

class MutableListState<T>(private val values: MutableList<T> = mutableListOf()) :
    MutableState(), MutableList<T> by values
{
    fun use(surface: UiSurface): MutableListState<T> {
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