package de.fabmax.kool.util

/**
 * A somewhat mutable list: Items can be scheduled for addition and removal, but the actual list content only changes
 * after [update] is called.
 */
class BufferedList<T> private constructor(private val backingList: MutableList<T>) : List<T> by backingList {
    constructor() : this(mutableListOf())

    private val staged = mutableSetOf<T>()
    private val stagedRemove = mutableSetOf<T>()

    override fun isEmpty(): Boolean {
        return backingList.isEmpty() && staged.isEmpty()
    }

    fun clear() {
        backingList.clear()
        staged.clear()
        stagedRemove.clear()
    }

    /**
     * Schedules the given element for addition to the list. If the item was staged for removal previously but
     * the list was not yet updated, it is removed from the removal staging buffer.
     */
    fun stageAdd(item: T) {
        stagedRemove -= item
        staged += item
    }

    /**
     * Schedules the given element for removal from the list. If the item was staged for addition previously but
     * the list was not yet updated, it is removed from the staging buffer.
     */
    fun stageRemove(item: T) {
        staged -= item
        stagedRemove += item
    }

    operator fun plusAssign(item: T) {
        stageAdd(item)
    }

    operator fun minusAssign(item: T) {
        stageRemove(item)
    }

    /**
     * Applies any staged add / remove operations.
     * @return true if list content changed, false otherwise
     */
    fun update(): Boolean {
        return if (staged.isEmpty() && stagedRemove.isEmpty()) {
            false
        } else {
            backingList.removeAll(stagedRemove)
            backingList.addAll(staged)
            stagedRemove.clear()
            staged.clear()
            true
        }
    }

}