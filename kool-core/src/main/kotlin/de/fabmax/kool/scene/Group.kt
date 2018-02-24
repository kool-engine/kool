package de.fabmax.kool.scene

import de.fabmax.kool.RenderContext
import de.fabmax.kool.RenderPass
import de.fabmax.kool.math.RayTest
import de.fabmax.kool.util.BoundingBox

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

    // frustum check is disabled for groups because it relies on the node's bounding box, however group bounds depend
    // on children bounds and are therefore not known before render
    override var isFrustumChecked: Boolean
        get() = false
        set(value) {}

    override fun onSceneChanged(oldScene: Scene?, newScene: Scene?) {
        super.onSceneChanged(oldScene, newScene)
        for (i in children.indices) {
            children[i].scene = newScene
        }
    }

    override fun render(ctx: RenderContext) {
        if (!isVisible) {
            // group is hidden
            return
        }
        super.render(ctx)

        // isRendered flag is ignored because this group's bounds aren't valid before children are rendered
        // therefore the frustum check is not reliable

        tmpBounds.clear()
        for (i in children.indices) {
            if (ctx.renderPass != RenderPass.SHADOW || children[i].isCastingShadow) {
                children[i].render(ctx)
            }
            tmpBounds.add(children[i].bounds)
        }
        bounds.set(tmpBounds)
    }

    override fun dispose(ctx: RenderContext) {
        super.dispose(ctx)
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
            if (child.isPickable) {
                val d = child.bounds.hitDistanceSqr(test.ray)
                if (d < Float.POSITIVE_INFINITY && d <= test.hitDistanceSqr) {
                    child.rayTest(test)
                }
            }
        }
    }

    open fun addNode(node: Node, index: Int = -1) {
        if (index >= 0) {
            children.add(index, node)
        } else {
            children.add(node)
        }
        node.parent = this
        bounds.add(node.bounds)
        bounds.add(node.bounds)
    }

    open fun removeNode(node: Node): Boolean {
        if (children.remove(node)) {
            node.parent = null
            return true
        }
        return false
    }

    open fun containsNode(node: Node): Boolean = children.contains(node)

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