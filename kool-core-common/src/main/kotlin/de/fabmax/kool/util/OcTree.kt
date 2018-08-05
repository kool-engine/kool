package de.fabmax.kool.util

import de.fabmax.kool.KoolException
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import kotlin.math.max

class OcTree<T: Any>(itemAdapter: ItemAdapter<T>, items: List<T> = emptyList(), bounds: BoundingBox = BoundingBox(), padding: Float = 0.1f, bucketSz: Int = 20) :
        SpatialTree<T>(itemAdapter) {

    override val root: OcNode
    override val size: Int
        get() = root.size

    private val emptyItems = mutableListOf<T>()

    init {
        // determine bounds of items
        val tmpPt = MutableVec3f()
        bounds.batchUpdate {
            for (i in items.indices){
                bounds.add(itemAdapter.getMin(items[i], tmpPt))
                bounds.add(itemAdapter.getMax(items[i], tmpPt))
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

    operator fun plusAssign(item: T) {
        add(item)
    }

    fun add(item: T) {
        if (!root.bounds.isIncluding(itemAdapter.getCenterX(item), itemAdapter.getCenterY(item), itemAdapter.getCenterZ(item))) {
            throw KoolException("Item not in tree bounds: (${itemAdapter.getX(item)}, ${itemAdapter.getY(item)}, ${itemAdapter.getZ(item)}), bounds: ${root.bounds}")
        }
        root.add(item)
    }

    operator fun minusAssign(item: T) { remove(item) }
    fun remove(item: T): Boolean {
        val success = root.remove(item)
        if (!success) {
            logW { "Failed to remove: $item" }
            forEach {
                if (it == item) {
                    logE { "found in tree!" }
                }
            }
        }
        return success
    }

    override fun iterator(): Iterator<T> = object : Iterator<T> {
        val leafs = mutableListOf<OcTree<T>.OcNode>()
        val leafIt: Iterator<OcTree<T>.OcNode>
        var currentLeaf: OcTree<T>.OcNode? = null
        var i = 0

        init {
            collectLeafs(root)
            leafIt = leafs.iterator()
            if (leafIt.hasNext()) {
                currentLeaf = leafIt.next()
            }
        }

        fun collectLeafs(node: OcTree<T>.OcNode) {
            if (node.isLeaf) {
                if (!node.items.isEmpty()) {
                    leafs += node
                }
            } else {
                for (i in node.children.indices) {
                    collectLeafs(node.children[i])
                }
            }
        }

        override fun hasNext(): Boolean = leafIt.hasNext() || i < currentLeaf?.size ?: 0

        override fun next(): T {
            if (currentLeaf == null) {
                throw NoSuchElementException()
            }
            if (i == currentLeaf!!.size) {
                currentLeaf = leafIt.next()
                i = 0
            }
            return currentLeaf!!.items[i++]
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

    inner class OcNode(bounds: BoundingBox, depth: Int, val bucketSz: Int) : Node(depth) {
        override var size = 0
            private set
        override val children = mutableListOf<OcNode>()

        private var mutItems = mutableListOf<T>()
        override val items
            get() = mutItems

        override val nodeRange: IntRange
            get() = items.indices

        init {
            if (depth > MAX_DEPTH) {
                throw KoolException("Octree is too deep")
            }
            this.bounds.set(bounds)
        }

        internal fun add(item: T) {
            if (!isInNode(itemAdapter.getCenterX(item), itemAdapter.getCenterY(item), itemAdapter.getCenterZ(item))) {
                logE { "item is out of node bounds:\n  $bounds\n  $item" }
            }

            size++
            if (isLeaf) {
                if (mutItems.size < bucketSz || depth == MAX_DEPTH) {
                    mutItems.add(item)
                    itemAdapter.setNode(item, this)
                } else {
                    split()
                    children[childIndexForItem(item)].add(item)
                }
            } else {
                children[childIndexForItem(item)].add(item)
            }
        }

        internal fun remove(item: T): Boolean {
            val success = if (isLeaf) {
                mutItems.remove(item)
            } else {
                children[childIndexForItem(item)].remove(item)
            }

            if (success) {
                size--
                if (!isLeaf && size < bucketSz) {
                    merge()
                }
            }

            return success
        }

        fun contains(item: T): Boolean {
            return if (isLeaf) {
                mutItems.contains(item)
            } else {
                children[childIndexForItem(item)].contains(item)
            }
        }

        fun isInNode(center: Vec3f) = bounds.isIncluding(center)

        fun isInNode(centerX: Float, centerY: Float, centerZ: Float) = bounds.isIncluding(centerX, centerY, centerZ)

        private fun split() {
            val x0 = bounds.min.x
            val x1 = bounds.center.x
            val x2 = bounds.max.x

            val y0 = bounds.min.y
            val y1 = bounds.center.y
            val y2 = bounds.max.y

            val z0 = bounds.min.z
            val z1 = bounds.center.z
            val z2 = bounds.max.z

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
            return if (itemAdapter.getCenterX(item) < bounds.center.x) { 0 } else { 4 } or
                    if (itemAdapter.getCenterY(item) < bounds.center.y) { 0 } else { 2 } or
                    if (itemAdapter.getCenterZ(item) < bounds.center.z) { 0 } else { 1 }
        }
    }

    companion object {
        const val MAX_DEPTH = 20
    }
}