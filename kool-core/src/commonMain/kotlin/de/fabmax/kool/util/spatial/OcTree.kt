package de.fabmax.kool.util.spatial

import de.fabmax.kool.KoolException
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logW
import kotlin.math.max

class OcTree<T: Any>(itemAdapter: ItemAdapter<T>, items: List<T> = emptyList(),
                     bounds: BoundingBox = BoundingBox(), padding: Float = 0.1f, bucketSz: Int = 20,
                     val accurateBounds: Boolean = true) :
        SpatialTree<T>(itemAdapter), MutableCollection<T> {

    override val root: OcNode
    override val size: Int
        get() = root.size

    private val emptyItems = mutableListOf<T>()

    init {
        // determine bounds of items
        val tmpPt = MutableVec3f()
        if (items.isNotEmpty()) {
            bounds.batchUpdate {
                for (i in items.indices) {
                    bounds.add(itemAdapter.getMin(items[i], tmpPt))
                    bounds.add(itemAdapter.getMax(items[i], tmpPt))
                }
            }
        }

        if (bounds.isEmpty) {
            throw KoolException("OcTree bounds are empty, specify bounds manually")
        }

        // cubify bounds and add padding
        val edLen = max(bounds.size.x, max(bounds.size.y, bounds.size.z))
        val pad = edLen * padding
        bounds.set(bounds.min.x - pad, bounds.min.y - pad, bounds.min.z - pad,
                bounds.min.x + edLen + pad*2, bounds.min.y + edLen + pad*2, bounds.min.z + edLen + pad*2)

        root = OcNode(bounds, 0, bucketSz)
        for (i in items.indices) {
            root.add(items[i])
        }
    }

    override fun add(element: T): Boolean {
        if (!root.nodeBounds.contains(itemAdapter.getCenterX(element), itemAdapter.getCenterY(element), itemAdapter.getCenterZ(element))) {
            logE { "Item not in tree bounds: (${itemAdapter.getMinX(element)}, ${itemAdapter.getMinY(element)}, ${itemAdapter.getMinZ(element)}), bounds: ${root.bounds}" }
            return false
        }
        root.add(element)
        return true
    }

    override fun remove(element: T): Boolean {
        val success = root.remove(element, true)
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
            collectElements(root)
            elemIt = if (elementIts.isNotEmpty()) {
                elementIts.removeAt(elementIts.lastIndex)
            } else {
                mutableListOf<T>().iterator()
            }
        }

        fun collectElements(node: OcTree<T>.OcNode) {
            if (node.isLeaf) {
                if (!node.items.isEmpty()) {
                    elementIts += node.items.iterator()
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

    override fun contains(element: T) = root.contains(element)

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

    override fun clear() = root.clear()

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

    inner class OcNode(val nodeBounds: BoundingBox, depth: Int, val bucketSz: Int) : Node(depth) {
        override var size = 0
            private set
        override val children = mutableListOf<OcNode>()

        private val tmpVec = MutableVec3f()
        private var mutItems = mutableListOf<T>()
        override val items
            get() = mutItems

        override val nodeRange: IntRange
            get() = items.indices

        init {
            if (depth > MAX_DEPTH) {
                throw KoolException("Octree is too deep")
            }
            if (!accurateBounds) {
                bounds.add(nodeBounds)
            }
        }

        internal fun clear() {
            if (depth != 0) {
                throw KoolException("clear() is only allowed for root node")
            }
            mutItems.clear()
            children.clear()
            size = 0

            if (accurateBounds) {
                bounds.clear()
            }
        }

        internal fun add(item: T) {
            size++
            if (isLeaf) {
                if (accurateBounds) {
                    bounds.add(itemAdapter.getMin(item, tmpVec))
                    bounds.add(itemAdapter.getMax(item, tmpVec))
                }

                if (mutItems.size < bucketSz || depth == MAX_DEPTH) {
                    mutItems.add(item)
                    itemAdapter.setNode(item, this)
                } else {
                    split()
                    children[childIndexForItem(item)].add(item)
                }
            } else {
                val child = children[childIndexForItem(item)]
                child.add(item)

                if (accurateBounds) {
                    bounds.add(child.bounds)
                }
            }
        }

        internal fun remove(item: T, canMerge: Boolean): Boolean {
            val success = if (isLeaf) {
                mutItems.remove(item)
            } else {
                children[childIndexForItem(item)].remove(item, canMerge)
            }

            if (success) {
                size--
                if (!isLeaf && size < bucketSz && canMerge) {
                    merge()
                }

                if (accurateBounds && isBorderItem(item)) {
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

        fun isInBounds(center: Vec3f) = isInBounds(center.x, center.y, center.z)

        fun isInBounds(centerX: Float, centerY: Float, centerZ: Float): Boolean {
            // Do not use BoundingBox.isIncluding() here: It tests inclusive max bounds (x >= min && x <= max) which is
            // problematic if an item is on the border of neighboring nodes (x == max and x == min of next node)
            return centerX >= nodeBounds.min.x && centerX < nodeBounds.max.x &&
                    centerY >= nodeBounds.min.y && centerY < nodeBounds.max.y &&
                    centerZ >= nodeBounds.min.z && centerZ < nodeBounds.max.z
        }

        private fun split() {
            val x0 = nodeBounds.min.x
            val x1 = nodeBounds.center.x
            val x2 = nodeBounds.max.x

            val y0 = nodeBounds.min.y
            val y1 = nodeBounds.center.y
            val y2 = nodeBounds.max.y

            val z0 = nodeBounds.min.z
            val z1 = nodeBounds.center.z
            val z2 = nodeBounds.max.z

            // create sub-nodes
            children += OcNode(BoundingBox(Vec3f(x0, y0, z0), Vec3f(x1, y1, z1)), depth + 1, bucketSz)
            children += OcNode(BoundingBox(Vec3f(x0, y0, z1), Vec3f(x1, y1, z2)), depth + 1, bucketSz)
            children += OcNode(BoundingBox(Vec3f(x0, y1, z0), Vec3f(x1, y2, z1)), depth + 1, bucketSz)
            children += OcNode(BoundingBox(Vec3f(x0, y1, z1), Vec3f(x1, y2, z2)), depth + 1, bucketSz)

            children += OcNode(BoundingBox(Vec3f(x1, y0, z0), Vec3f(x2, y1, z1)), depth + 1, bucketSz)
            children += OcNode(BoundingBox(Vec3f(x1, y0, z1), Vec3f(x2, y1, z2)), depth + 1, bucketSz)
            children += OcNode(BoundingBox(Vec3f(x1, y1, z0), Vec3f(x2, y2, z1)), depth + 1, bucketSz)
            children += OcNode(BoundingBox(Vec3f(x1, y1, z1), Vec3f(x2, y2, z2)), depth + 1, bucketSz)

            for (i in mutItems.indices) {
                children[childIndexForItem(mutItems[i])].add(mutItems[i])
            }
            mutItems = emptyItems
        }

        private fun merge() {
            mutItems = mutableListOf()
            for (i in children.indices) {
                mutItems.addAll(children[i].mutItems)
            }
            children.clear()
        }

        private fun childIndexForItem(item: T): Int {
            return if (itemAdapter.getCenterX(item) < nodeBounds.center.x) { 0 } else { 4 } or
                    if (itemAdapter.getCenterY(item) < nodeBounds.center.y) { 0 } else { 2 } or
                    if (itemAdapter.getCenterZ(item) < nodeBounds.center.z) { 0 } else { 1 }
        }
    }

    companion object {
        const val MAX_DEPTH = 20
    }
}