package de.fabmax.kool.scene

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.*
import de.fabmax.kool.math.spatial.BoundingBox
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.util.LazyMat4d

/**
 * @author fabmax
 */

fun group(name: String? = null, block: Group.() -> Unit): Group {
    val tg = Group(name)
    tg.block()
    return tg
}

open class Group(name: String? = null) : Node(name) {
    protected val intChildren = mutableListOf<Node>()
    protected val childrenBounds = BoundingBox()
    val children: List<Node> get() = intChildren
    val size: Int get() = intChildren.size

    val transform = Mat4d()

    protected val invTransform = LazyMat4d { transform.invert(it) }
    protected var isIdentity = false

    private val tmpTransformVec = MutableVec3f()
    private val tmpBounds = BoundingBox()

    init {
        setIdentity()
    }

    fun setDirty() {
        invTransform.isDirty = true
        isIdentity = false
    }

    override fun update(updateEvent: RenderPass.UpdateEvent) {
        // update must be called before children are updated, otherwise children use a potentially outdated
        // parent model mat and toGlobalCoords() might yield wrong results
        super.update(updateEvent)

        // call update on all children and update group bounding box
        childrenBounds.clear()
        for (i in intChildren.indices) {
            intChildren[i].update(updateEvent)
            childrenBounds.add(intChildren[i].bounds)
        }

        // update bounds based on updated children bounds
        setLocalBounds()
        globalCenterMut.set(bounds.center)
        globalExtentMut.set(bounds.max)
        modelMat.transform(globalCenterMut)
        modelMat.transform(globalExtentMut)
        globalRadius = globalCenter.distance(globalExtentMut)

        // transform group bounds
        if (!bounds.isEmpty && !isIdentity) {
            tmpBounds.clear()
            tmpBounds.add(transform.transform(tmpTransformVec.set(bounds.min.x, bounds.min.y, bounds.min.z), 1f))
            tmpBounds.add(transform.transform(tmpTransformVec.set(bounds.min.x, bounds.min.y, bounds.max.z), 1f))
            tmpBounds.add(transform.transform(tmpTransformVec.set(bounds.min.x, bounds.max.y, bounds.min.z), 1f))
            tmpBounds.add(transform.transform(tmpTransformVec.set(bounds.min.x, bounds.max.y, bounds.max.z), 1f))
            tmpBounds.add(transform.transform(tmpTransformVec.set(bounds.max.x, bounds.min.y, bounds.min.z), 1f))
            tmpBounds.add(transform.transform(tmpTransformVec.set(bounds.max.x, bounds.min.y, bounds.max.z), 1f))
            tmpBounds.add(transform.transform(tmpTransformVec.set(bounds.max.x, bounds.max.y, bounds.min.z), 1f))
            tmpBounds.add(transform.transform(tmpTransformVec.set(bounds.max.x, bounds.max.y, bounds.max.z), 1f))
            bounds.set(tmpBounds)
        }
    }

    protected open fun setLocalBounds() {
        bounds.set(childrenBounds)
    }

    override fun updateModelMat() {
        super.updateModelMat()
        if (!isIdentity) {
            modelMat.mul(transform)
        }
    }

    override fun rayTest(test: RayTest) {
        if (!isIdentity) {
            // transform ray to local coordinates
            test.transformBy(invTransform.get())
        }

        for (i in intChildren.indices) {
            val child = intChildren[i]
            if (child.isPickable && child.isVisible) {
                val d = child.bounds.hitDistanceSqr(test.ray)
                if (d < Float.MAX_VALUE && d <= test.hitDistanceSqr) {
                    child.rayTest(test)
                }
            }
        }

        if (!isIdentity) {
            // transform ray back to previous coordinates
            test.transformBy(transform)
        }
    }

    fun getTransform(result: Mat4f): Mat4f = result.set(transform)

    fun getInverseTransform(result: Mat4f): Mat4f {
        return result.set(invTransform.get())
    }

    override fun collectDrawCommands(updateEvent: RenderPass.UpdateEvent) {
        super.collectDrawCommands(updateEvent)

        if (isRendered) {
            for (i in intChildren.indices) {
                intChildren[i].collectDrawCommands(updateEvent)
            }
        }
    }

    override fun dispose(ctx: KoolContext) {
        super.dispose(ctx)
        for (i in intChildren.indices) {
            intChildren[i].dispose(ctx)
        }
    }

    open fun addNode(node: Node, index: Int = -1) {
        if (index >= 0) {
            intChildren.add(index, node)
        } else {
            intChildren.add(node)
        }
        node.parent = this
        bounds.add(node.bounds)
    }

    open fun <R: Comparable<R>> sortChildrenBy(selector: (Node) -> R) {
        intChildren.sortBy(selector)
    }

    open fun removeNode(node: Node): Boolean {
        if (intChildren.remove(node)) {
            node.parent = null
            return true
        }
        return false
    }

    open fun removeAllChildren() {
        for (i in intChildren.indices) {
            intChildren[i].parent = null
        }
        intChildren.clear()
    }

    override fun findNode(name: String): Node? {
        if (name == this.name) {
            return this
        }
        for (i in children.indices) {
            val found = children[i].findNode(name)
            if (found != null) {
                return found
            }
        }
        return null
    }

    override fun collectTag(result: MutableList<Node>, tag: String, value: String?) {
        super.collectTag(result, tag, value)
        for (i in children.indices) {
            children[i].collectTag(result, tag, value)
        }
    }

    open fun containsNode(node: Node): Boolean = intChildren.contains(node)

    operator fun plusAssign(node: Node) {
        addNode(node)
    }

    operator fun minusAssign(node: Node) {
        removeNode(node)
    }

    operator fun Node.unaryPlus() {
        addNode(this)
    }

    fun translate(t: Vec3f) = translate(t.x, t.y, t.z)

    fun translate(tx: Float, ty: Float, tz: Float) = translate(tx.toDouble(), ty.toDouble(), tz.toDouble())

    fun translate(t: Vec3d) = translate(t.x, t.y, t.z)

    fun translate(tx: Double, ty: Double, tz: Double): Group {
        transform.translate(tx, ty, tz)
        setDirty()
        return this
    }

    fun rotate(angleDeg: Float, axis: Vec3f) = rotate(angleDeg, axis.x, axis.y, axis.z)

    fun rotate(angleDeg: Float, axX: Float, axY: Float, axZ: Float) =
            rotate(angleDeg.toDouble(), axX.toDouble(), axY.toDouble(), axZ.toDouble())

    fun rotate(angleDeg: Double, axis: Vec3d) = rotate(angleDeg, axis.x, axis.y, axis.z)

    fun rotate(angleDeg: Double, axX: Double, axY: Double, axZ: Double): Group {
        transform.rotate(angleDeg, axX, axY, axZ)
        setDirty()
        return this
    }

    fun rotate(eulerX: Float, eulerY: Float, eulerZ: Float): Group =
        rotate(eulerX.toDouble(), eulerY.toDouble(), eulerZ.toDouble())

    fun rotate(eulerX: Double, eulerY: Double, eulerZ: Double): Group {
        transform.rotate(eulerX, eulerY, eulerZ)
        setDirty()
        return this
    }

    fun scale(s: Float) = scale(s.toDouble(), s.toDouble(), s.toDouble())

    fun scale(sx: Float, sy: Float, sz: Float) = scale(sx.toDouble(), sy.toDouble(), sz.toDouble())

    fun scale(s: Double) = scale(s, s, s)

    fun scale(sx: Double, sy: Double, sz: Double): Group {
        transform.scale(sx, sy, sz)
        setDirty()
        return this
    }

    fun mul(mat: Mat4d): Group {
        transform.mul(mat)
        setDirty()
        return this
    }

    fun set(mat: Mat4f): Group {
        transform.set(mat)
        setDirty()
        return this
    }

    fun set(mat: Mat4d): Group {
        transform.set(mat)
        setDirty()
        return this
    }

    fun setIdentity(): Group {
        transform.setIdentity()
        invTransform.clear()
        isIdentity = true
        return this
    }
}