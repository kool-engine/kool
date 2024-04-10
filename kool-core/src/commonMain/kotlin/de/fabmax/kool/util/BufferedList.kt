package de.fabmax.kool.util

/**
 * A somewhat mutable list: Items can be scheduled for addition and removal, but the actual list content only changes
 * after [update] is called.
 */
class BufferedList<T> private constructor(private val backingList: MutableList<T>) : List<T> by backingList {
    constructor() : this(mutableListOf())

    private val stagedInsertions = mutableSetOf<StagedInsertion<T>>()
    private val stagedRemovals = mutableSetOf<T>()

    val hasItemsStagedForAdd: Boolean
        get() = stagedInsertions.isNotEmpty()
    val hasItemsStagedForRemoval: Boolean
        get() = stagedRemovals.isNotEmpty()
    val hasStagedItems: Boolean
        get() = hasItemsStagedForAdd || hasItemsStagedForRemoval

    fun clear() {
        backingList.clear()
        stagedInsertions.clear()
        stagedRemovals.clear()
    }

    /**
     * Schedules the given element for addition to the list. If the item was staged for removal previously but
     * the list was not yet updated, it is removed from the removal staging buffer.
     */
    fun stageAdd(item: T, index: Int = -1) {
        stagedRemovals -= item
        stagedInsertions += StagedInsertion(item, index)
    }

    /**
     * Schedules the given element for removal from the list. If the item was staged for addition previously but
     * the list was not yet updated, it is removed from the staging buffer.
     */
    fun stageRemove(item: T) {
        if (stagedInsertions.isNotEmpty()) {
            stagedInsertions.removeAll { it.item == item }
        }
        stagedRemovals += item
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
        return if (stagedInsertions.isEmpty() && stagedRemovals.isEmpty()) {
            false
        } else {
            backingList.removeAll(stagedRemovals)
            for ((item, index) in stagedInsertions) {
                if (index < 0) {
                    backingList.add(item)
                } else {
                    backingList.add(index, item)
                }
            }
            stagedRemovals.clear()
            stagedInsertions.clear()
            true
        }
    }

    fun updated(): BufferedList<T> {
        update()
        return this
    }

    private data class StagedInsertion<T>(val item: T, val index: Int)
}