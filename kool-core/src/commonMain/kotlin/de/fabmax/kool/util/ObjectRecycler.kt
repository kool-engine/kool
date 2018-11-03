package de.fabmax.kool.util

/**
 * A simple helper class for recycling temporary objects. Object recycling can avoid flooding the heap with temporary
 * objects. However using recycled objects requires much more care:
 *  - ObjectRecycler is not thread-safe
 *  - Objects retrieved from the recycler have an unknown state
 *  - Objects which aren't used anymore should be returned in the recycling pool by calling [recycle]
 *  - Objects must not be used after they were recycled
 */
open class ObjectRecycler<T: Any>(private val maxSize: Int, private val factory: () -> T) {

    private val recyclingStack = mutableListOf<T>()

    constructor(factory: () -> T) : this(DEFAULT_MAX_SIZE, factory)

    /**
     * Returns an object from the recycling stack or creates a new one if the stack is empty. Returned objects have
     * an unknown state.
     */
    open fun get(): T =
        if (recyclingStack.isNotEmpty()) {
            recyclingStack.removeAt(recyclingStack.lastIndex)
        } else {
            factory()
        }

    /**
     * Recycles the given object by putting it on the recycling stack. Do not use the object anymore after passing it
     * to this function!
     */
    open fun recycle(obj: T): ObjectRecycler<T> {
        if (recyclingStack.size < maxSize) {
            recyclingStack += obj
        } else {
            logD { "Discarding recycled object ${obj::class}, stack is full: ${recyclingStack.size}" }
        }
        return this
    }

    companion object {
        const val DEFAULT_MAX_SIZE = 10000
    }
}

class ObjectPool<T: Any>(factory: () -> T) : ObjectRecycler<T>(factory) {
    private val liveObjects = mutableListOf<T>()

    val size: Int
        get() = liveObjects.size

    operator fun get(index: Int): T = liveObjects[index]

    override fun get(): T {
        val obj = super.get()
        liveObjects += obj
        return obj
    }

    override fun recycle(obj: T): ObjectRecycler<T> {
        liveObjects -= obj
        return super.recycle(obj)
    }

    fun recycleAll() {
        for (i in liveObjects.indices) {
            super.recycle(liveObjects[i])
        }
        liveObjects.clear()
    }
}

class AutoRecycler<T: Any>(maxSize: Int = DEFAULT_MAX_SIZE, factory: () -> T) : ObjectRecycler<T>(maxSize, factory) {

    val contextRecycler = ObjectRecycler { Context() }

    inline fun use(block: Context.(T) -> Unit) {
        val ctx = contextRecycler.get()
        ctx.block(ctx.get())
        ctx.free()
        contextRecycler.recycle(ctx)
    }


    inner class Context {
        private val liveObjects = mutableListOf<T>()

        fun get(): T {
            val o = this@AutoRecycler.get()
            liveObjects += o
            return o
        }

        fun free() {
            for (i in liveObjects.indices) {
                recycle(liveObjects[i])
            }
            liveObjects.clear()
        }
    }
}
