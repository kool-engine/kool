package de.fabmax.kool.scene

import de.fabmax.kool.KoolContext
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

    override fun onSceneChanged(oldScene: Scene?, newScene: Scene?) {
        super.onSceneChanged(oldScene, newScene)
        for (i in children.indices) {
            children[i].scene = newScene
        }
    }

    override fun preRender(ctx: KoolContext) {
        // call preRender on all children and update group bounding box
        tmpBounds.clear()
        for (i in children.indices) {
            children[i].preRender(ctx)
            tmpBounds.add(children[i].bounds)
        }
        bounds.set(tmpBounds)

        // compute global position and size based on group bounds and current model transform
        super.preRender(ctx)
    }

    override fun render(ctx: KoolContext) {
        super.render(ctx)

        if (isRendered) {
            for (i in children.indices) {
                if (ctx.renderPass != RenderPass.SHADOW || children[i].isCastingShadow) {
                    children[i].render(ctx)
                }
            }
        }
    }

    override fun postRender(ctx: KoolContext) {
        for (i in children.indices) {
            children[i].postRender(ctx)
        }
        super.postRender(ctx)
    }

    override fun dispose(ctx: KoolContext) {
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