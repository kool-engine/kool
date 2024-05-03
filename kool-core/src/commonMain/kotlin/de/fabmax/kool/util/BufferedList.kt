package de.fabmax.kool.util

/**
 * A somewhat mutable list: Items can be scheduled for addition and removal, but the actual list content only changes
 * after [update] is called. Additions and removals are executed in the order they were issued.
 */
class BufferedList<T> private constructor(private val backingList: MutableList<T>) : List<T> by backingList {
    constructor() : this(mutableListOf())

    private val stagedMutations = mutableSetOf<StagedMutation<T>>()
    val hasStagedMutations: Boolean
        get() = stagedMutations.isNotEmpty()

    fun clear() {
        backingList.clear()
        stagedMutations.clear()
    }

    /**
     * Schedules the given element for addition to the list.
     */
    fun stageAdd(item: T, index: Int = -1) {
        stagedMutations += Insert(item, index)
    }

    /**
     * Schedules the given element for removal from the list.
     */
    fun stageRemove(item: T) {
        stagedMutations += Remove(item)
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
        return if (stagedMutations.isEmpty()) {
            false
        } else {
            for (mutation in stagedMutations) {
                when (mutation) {
                    is Insert -> {
                        if (mutation.index < 0) {
                            backingList.add(mutation.item)
                        } else {
                            backingList.add(mutation.index, mutation.item)
                        }
                    }
                    is Remove -> {
                        backingList.remove(mutation.item)
                    }
                }
            }
            stagedMutations.clear()
            true
        }
    }

    fun updated(): BufferedList<T> {
        update()
        return this
    }

    private sealed class StagedMutation<T>(val item: T)
    private class Insert<T>(item: T, val index: Int) : StagedMutation<T>(item)
    private class Remove<T>(item: T) : StagedMutation<T>(item)
}