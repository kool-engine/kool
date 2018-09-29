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
    private val intChildren = mutableListOf<Node>()
    protected val tmpBounds = BoundingBox()

    val children: List<Node> get() = intChildren
    val size: Int get() = intChildren.size

    override fun onSceneChanged(oldScene: Scene?, newScene: Scene?) {
        super.onSceneChanged(oldScene, newScene)
        for (i in intChildren.indices) {
            intChildren[i].scene = newScene
        }
    }

    override fun preRender(ctx: KoolContext) {
        // call preRender on all children and update group bounding box
        tmpBounds.clear()
        for (i in intChildren.indices) {
            intChildren[i].preRender(ctx)
            tmpBounds.add(intChildren[i].bounds)
        }
        bounds.set(tmpBounds)

        // compute global position and size based on group bounds and current model transform
        super.preRender(ctx)
    }

    override fun render(ctx: KoolContext) {
        super.render(ctx)

        if (isRendered) {
            for (i in intChildren.indices) {
                if (ctx.renderPass != RenderPass.SHADOW || intChildren[i].isCastingShadow) {
                    intChildren[i].render(ctx)
                }
            }
        }
    }

    override fun postRender(ctx: KoolContext) {
        for (i in intChildren.indices) {
            intChildren[i].postRender(ctx)
        }
        super.postRender(ctx)
    }

    override fun dispose(ctx: KoolContext) {
        super.dispose(ctx)
        for (i in intChildren.indices) {
            intChildren[i].dispose(ctx)
        }
    }

    override operator fun get(name: String): Node? {
        if (name == this.name) {
            return this
        }
        for (i in intChildren.indices) {
            val node = intChildren[i][name]
            if (node != null) {
                return node
            }
        }
        return null
    }

    override fun rayTest(test: RayTest) {
        for (i in intChildren.indices) {
            val child = intChildren[i]
            if (child.isPickable && child.isVisible) {
                val d = child.bounds.hitDistanceSqr(test.ray)
                if (d < Float.MAX_VALUE && d <= test.hitDistanceSqr) {
                    child.rayTest(test)
                }
            }
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

    open fun removeNode(node: Node): Boolean {
        if (intChildren.remove(node)) {
            node.parent = null
            return true
        }
        return false
    }

    /**
     * Removes BUT DOESN'T DISPOSE all children of this group.
     */
    open fun removeAllChildren() {
        for (i in intChildren.indices) {
            intChildren[i].parent = null
        }
        intChildren.clear()
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
}