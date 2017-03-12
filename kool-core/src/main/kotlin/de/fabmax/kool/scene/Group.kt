package de.fabmax.kool.scene

import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.RayTest

/**
 * @author fabmax
 */

fun group(name: String? = null, block: Group.() -> Unit): Group {
    val grp = Group(name)
    grp.block()
    return grp
}

open class Group(name: String? = null) : Node(name) {
    protected val children: MutableList<Node> = mutableListOf()
    protected val tmpBounds = BoundingBox()

    override fun render(ctx: RenderContext) {
        if (!isVisible) {
            return
        }
        super.render(ctx)

        tmpBounds.clear()
        for (i in children.indices) {
            children[i].render(ctx)
            tmpBounds.add(children[i].bounds)
        }
        bounds.set(tmpBounds)
    }

    override fun dispose(ctx: RenderContext) {
        for (i in children.indices) {
            children[i].dispose(ctx)
        }
    }

    override operator fun get(name: String): Node? {
        if (name == this.name) {
            return this
        }
        for (i in children.indices) {
            val node = children[i][name]
            if (node != null) {
                return node
            }
        }
        return null
    }

    override fun rayTest(test: RayTest) {
        for (i in children.indices) {
            val child = children[i]
            if (child.isPickable &&
                    child.bounds.computeHitDistanceSqr(test.ray) < test.hitDistanceSqr) {
                child.rayTest(test)
            }
        }
    }

    fun addNode(node: Node, index: Int = -1) {
        if (index >= 0) {
            children.add(index, node)
        } else {
            children.add(node)
        }
        node.parent = this
        bounds.add(node.bounds)
        bounds.add(node.bounds)
    }

    fun removeNode(node: Node): Boolean {
        if (children.remove(node)) {
            node.parent = null
            return true
        }
        return false
    }

    operator fun plusAssign(node: Node) {
        addNode(node)
    }

    operator fun minusAssign(node: Node) {
        removeNode(node)
    }

    operator fun Node.unaryPlus() {
        addNode(this)
    }
}