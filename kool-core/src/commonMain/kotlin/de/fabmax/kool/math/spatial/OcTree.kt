package de.fabmax.kool.math.spatial

import de.fabmax.kool.math.MutableVec3d
import de.fabmax.kool.math.Vec3d
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logW
import kotlin.math.max

open class OcTree<T: Any>(
    itemAdapter: ItemAdapter<T>,
    items: List<T> = emptyList(),
    bounds: BoundingBoxD?,
    paddingFactor: Double = 0.01,
    val bucketSz: Int = 16,
    val maxDepth: Int = 20
) : SpatialTree<T>(itemAdapter), MutableCollection<T> {

    override val root: OcNode
        get() = mutRoot
    override val size: Int
        get() = root.size

    private var mutRoot: OcNode

    var isAutoResize = true

    private val emptyItems = mutableListOf<T>()
    private val emptyNode = OcNode(BoundingBoxD(), -1)

    init {
        val rootBounds = BoundingBoxD()
        if (bounds != null) {
            rootBounds.set(bounds)
        } else {
            val tmpPt = MutableVec3d()
            items.forEach {
                rootBounds.add(itemAdapter.getMin(it, tmpPt))
                rootBounds.add(itemAdapter.getMax(it, tmpPt))
            }
        }

        check(rootBounds.isNotEmpty) {
            "Unable to determine initial OcTree bounds: Neither bounds specified nor initial list of items given"
        }

        // cubify bounds and add padding
        val cubeSize = max(rootBounds.size.x, max(rootBounds.size.y, rootBounds.size.z))
        val pad = cubeSize * paddingFactor
        val cubeMin = Vec3d(rootBounds.min) - Vec3d(pad)
        val cubeMax = Vec3d(rootBounds.min) + Vec3d(cubeSize + pad)
        rootBounds.set(cubeMin, cubeMax)

        mutRoot = OcNode(rootBounds, 0)
        for (i in items.indices) {
            mutRoot.add(items[i])
        }
    }

    override fun add(element: T): Boolean {
        if (!mutRoot.nodeBounds.contains(itemAdapter.getCenterX(element), itemAdapter.getCenterY(element), itemAdapter.getCenterZ(element))) {
            if (!isAutoResize) {
                logE { "Item not in tree bounds: (${itemAdapter.getCenterX(element)}, ${itemAdapter.getCenterY(element)}, ${itemAdapter.getCenterZ(element)}), bounds: ${root.nodeBounds}" }
                return false
            } else {
                growTree(Vec3d(itemAdapter.getCenterX(element), itemAdapter.getCenterY(element), itemAdapter.getCenterZ(element)))
            }
        }
        mutRoot.add(element)
        return true
    }

    private fun growTree(pt: Vec3d, maxIterations: Int = 10): Boolean {
        var its = 0
        while (!mutRoot.nodeBounds.contains(pt) && its++ < maxIterations) {
            // create new root node, which contains old root as child and grows towards requested point
            val aabb = mutRoot.nodeBounds
            val dirX = if (pt.x < aabb.min.x) -1 else 1
            val dirY = if (pt.y < aabb.min.y) -1 else 1
            val dirZ = if (pt.z < aabb.min.z) -1 else 1

            val growAabb = BoundingBoxD(aabb.min, aabb.max)
            growAabb.signedExpand(Vec3d(aabb.size.x * dirX, aabb.size.y * dirY, aabb.size.z * dirZ))

            // depth of new root will become negative - should be fine
            val newRoot = OcNode(growAabb, mutRoot.depth - 1)
            newRoot.split()
            newRoot.children[newRoot.childIndexForPoint(aabb.center.x, aabb.center.y, aabb.center.z)] = mutRoot
            newRoot.bounds.set(mutRoot.bounds)
            mutRoot = newRoot
        }
        return mutRoot.nodeBounds.contains(pt)
    }

    override fun remove(element: T): Boolean {
        val success = mutRoot.remove(element, true)
        if (!success) {
            logW { "Failed to remove: $element" }
            // try brute force removal
            val it = iterator()
            while (it.hasNext()) {
                if (it.next() == element) {
                    logW { "Removed via brute force, did element change it's position?" }
                    it.remove()
                }
            }
        }
        return success
    }

    override fun iterator(): MutableIterator<T> = object : MutableIterator<T> {
        val elementIts = mutableListOf<MutableIterator<T>>()
        var elemIt: MutableIterator<T>

        init {
            collectElements(mutRoot)
            elemIt = if (elementIts.isNotEmpty()) {
                elementIts.removeAt(elementIts.lastIndex)
            } else {
                mutableListOf<T>().iterator()
            }
        }

        fun collectElements(node: OcTree<T>.OcNode) {
            if (node.isLeaf) {
                if (node.itemsUnbounded.isNotEmpty()) {
                    elementIts += node.itemsUnbounded.iterator()
                }
            } else {
                for (i in node.children.indices) {
                    collectElements(node.children[i])
                }
            }
        }

        override fun hasNext(): Boolean {
            if (elemIt.hasNext()) {
                return true
            } else {
                while (elementIts.isNotEmpty()) {
                    elemIt = elementIts.removeAt(elementIts.lastIndex)
                    if (elemIt.hasNext()) {
                        return true
                    }
                }
                return false
            }
        }

        override fun next(): T  = elemIt.next()

        override fun remove() {
            elemIt.remove()
        }
    }

    override fun contains(element: T) = mutRoot.contains(element)

    override fun containsAll(elements: Collection<T>): Boolean {
        for (elem in elements) {
            if (!contains(elem)) {
                return false
            }
        }
        return true
    }

    override fun isEmpty(): Boolean = size == 0

    override fun addAll(elements: Collection<T>): Boolean {
        var anyAdded = false
        for (elem in elements) {
            anyAdded = anyAdded || add(elem)
        }
        return anyAdded
    }

    override fun clear() = mutRoot.clear()

    override fun removeAll(elements: Collection<T>): Boolean {
        var anyRemoved = false
        for (elem in elements) {
            anyRemoved = anyRemoved || remove(elem)
        }
        return anyRemoved
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        var anyRemoved = false
        val retainSet = mutableSetOf<T>()
        retainSet.addAll(elements)
        val it = iterator()
        while (it.hasNext()) {
            if (it.next() !in retainSet) {
                it.remove()
                anyRemoved = true
            }
        }
        return anyRemoved
    }

    inner class OcNode(val nodeBounds: BoundingBoxD, val depth: Int) : Node() {
        override var size = 0
            private set
        override val children = mutableListOf<OcNode>()

        private val tmpVec = MutableVec3d()
        private var mutItems = mutableListOf<T>()
        override val itemsUnbounded
            get() = mutItems

        override val nodeRange: IntRange
            get() = itemsUnbounded.indices

        init {
            check (depth <= maxDepth) { "Octree is too deep" }
        }

        fun clear() {
            mutItems = mutableListOf()
            children.clear()
            size = 0

            bounds.clear()
        }

        fun add(item: T): OcNode {
            if (this === emptyNode) {
                throw IllegalStateException("Adding items to empty node is not allowed!")
            }

            bounds.add(itemAdapter.getMin(item, tmpVec))
            bounds.add(itemAdapter.getMax(item, tmpVec))
            size++

            return if (isLeaf) {
                if (mutItems.size < bucketSz || depth >= maxDepth) {
                    mutItems.add(item)
                    itemAdapter.setNode(item, this)
                    this

                } else {
                    split()
                    getChildOrCreateIfEmpty(childIndexForItem(item)).add(item)
                }
            } else {
                getChildOrCreateIfEmpty(childIndexForItem(item)).add(item)
            }
        }

        fun remove(item: T, canMerge: Boolean): Boolean {
            val success = if (isLeaf) {
                mutItems.remove(item)
            } else {
                children[childIndexForItem(item)].remove(item, canMerge)
            }

            if (success) {
                size--
                if (!isLeaf && size < bucketSz && canMerge) {
                    mutItems = mutableListOf()
                    collectAndClear(mutItems)
                }
                if (isBorderItem(item)) {
                    recomputeBounds()
                }
            }

            return success
        }

        private fun isBorderItem(item: T): Boolean {
            itemAdapter.getMin(item, tmpVec)
            if (tmpVec.x <= bounds.min.x || tmpVec.y <= bounds.min.y || tmpVec.z <= bounds.min.z) {
                return true
            }
            itemAdapter.getMax(item, tmpVec)
            if (tmpVec.x >= bounds.max.x || tmpVec.y >= bounds.max.y || tmpVec.z >= bounds.max.z) {
                return true
            }
            return false
        }

        private fun recomputeBounds() {
            bounds.clear()
            if (isLeaf) {
                for (i in mutItems.indices) {
                    bounds.add(itemAdapter.getMin(mutItems[i], tmpVec))
                    bounds.add(itemAdapter.getMax(mutItems[i], tmpVec))
                }
            } else {
                for (i in children.indices) {
                    bounds.add(children[i].bounds)
                }
            }
        }

        fun contains(item: T): Boolean {
            return if (isLeaf) {
                mutItems.contains(item)
            } else {
                children[childIndexForItem(item)].contains(item)
            }
        }

        fun isCenterInNode(center: Vec3f) = isCenterInNode(center.x, center.y, center.z)

        fun isCenterInNode(x: Float, y: Float, z: Float): Boolean {
            // Do not use BoundingBox.isIncluding() here: It tests inclusive max bounds (x >= min && x <= max) which is
            // problematic if an item is on the border of neighboring nodes (x == max and x == min of next node)
            return x >= bounds.min.x && x < bounds.max.x &&
                    y >= bounds.min.y && y < bounds.max.y &&
                    z >= bounds.min.z && z < bounds.max.z
        }

        private fun getChildOrCreateIfEmpty(i: Int): OcNode {
            var c = children[i]
            if (c === emptyNode) {
                val minX = if (i and 4 == 0) nodeBounds.min.x else nodeBounds.center.x
                val minY = if (i and 2 == 0) nodeBounds.min.y else nodeBounds.center.y
                val minZ = if (i and 1 == 0) nodeBounds.min.z else nodeBounds.center.z

                val maxX = minX + nodeBounds.size.x * 0.5
                val maxY = minY + nodeBounds.size.y * 0.5
                val maxZ = minZ + nodeBounds.size.z * 0.5

                c = OcNode(BoundingBoxD(Vec3d(minX, minY, minZ), Vec3d(maxX, maxY, maxZ)), depth + 1)
                children[i] = c
            }
            return c
        }

        internal fun split() {
            // create sub-nodes
            for (i in 1..8) {
                children += emptyNode
            }
            for (i in mutItems.indices) {
                getChildOrCreateIfEmpty(childIndexForItem(mutItems[i])).add(mutItems[i])
            }
            mutItems = emptyItems
        }

        private fun collectAndClear(result: MutableList<T>) {
            result.addAll(mutItems)
            for (i in children.indices) {
                children[i].collectAndClear(result)
            }
            children.clear()
        }

        fun childIndexForItem(item: T) = childIndexForPoint(itemAdapter.getCenterX(item), itemAdapter.getCenterY(item), itemAdapter.getCenterZ(item))

        fun childIndexForPoint(x: Double, y: Double, z: Double): Int {
            return if (x < nodeBounds.center.x) { 0 } else { 4 } or
                    if (y < nodeBounds.center.y) { 0 } else { 2 } or
                    if (z < nodeBounds.center.z) { 0 } else { 1 }
        }
    }
}