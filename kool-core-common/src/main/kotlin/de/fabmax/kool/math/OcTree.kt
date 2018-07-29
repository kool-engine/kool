package de.fabmax.kool.math

import de.fabmax.kool.util.BoundingBox
import kotlin.math.max

fun <T: Vec3f> pointOcTree(items: List<T>, bucketSz: Int = 20): OcTree<T> {
    return OcTree(items, Vec3fSize, bucketSz)
}

class OcTree<T: Any>(items: List<T>, itemSize: ItemSize<T>, bucketSz: Int = 20, padding: Float = 0.1f) : SpatialTree<T>(itemSize) {

    override val root: OcNode

    private val emptyItems = mutableListOf<T>()

    init {
        // determine bounds of items
        val bounds = BoundingBox()
        val tmpPt = MutableVec3f()
        items.forEach {
            bounds.add(itemSize.getMin(it, tmpPt))
            bounds.add(itemSize.getMax(it, tmpPt))
        }

        // cubify bounds and add padding
        val edLen = max(bounds.size.x, max(bounds.size.y, bounds.size.z))
        val pad = edLen * padding
        bounds.set(bounds.min.x - pad, bounds.min.y - pad, bounds.min.z - pad,
                bounds.min.x + edLen + pad*2, bounds.min.y + edLen + pad*2, bounds.min.z + edLen + pad*2)

        root = OcNode(bounds, 0, bucketSz)
        items.forEach(root::add)
    }

    inner class OcNode(bounds: BoundingBox, val depth: Int, val bucketSz: Int) : SpatialTree<T>.Node() {
        override val children = mutableListOf<OcNode>()

        private var mutItems = mutableListOf<T>()
        override val items
            get() = mutItems
        var numItems = 0
            private set

        override val nodeRange: IntRange
            get() = items.indices

        init {
            this.bounds.set(bounds)
        }

        operator fun plusAssign(item: T) = add(item)

        fun add(item: T) {
            numItems++
            if (isLeaf) {
                if (mutItems.size < bucketSz) {
                    mutItems.add(item)
                } else {
                    split()
                    addInChild(item)
                }
            } else {
                addInChild(item)
            }
        }

        operator fun minusAssign(item: T) {
            remove(item)
        }

        fun remove(item: T): Boolean {
            val success = if (isLeaf) {
                mutItems.remove(item)
            } else {
                children[childIndexForItem(item)].remove(item)
            }

            if (success && --numItems < bucketSz) {
                merge()
            }

            return success
        }

        private fun addInChild(item: T) {
            children[childIndexForItem(item)].add(item)
        }

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

            mutItems.forEach(this::addInChild)
            mutItems = emptyItems
        }

        private fun merge() {
            mutItems = mutableListOf()
            children.forEach {
                // assert(it.isLeaf)
                mutItems.addAll(it.mutItems)
            }
            children.clear()
        }

        private fun childIndexForItem(item: T): Int {
            return if (itemSize.getX(item) < bounds.center.x) { 0 } else { 4 } or
                    if (itemSize.getY(item) < bounds.center.y) { 0 } else { 2 } or
                    if (itemSize.getZ(item) < bounds.center.z) { 0 } else { 1 }
        }
    }
}