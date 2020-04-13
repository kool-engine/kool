package de.fabmax.kool.scene

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.RayTest
import de.fabmax.kool.pipeline.RenderPass
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
    protected val intChildren = mutableListOf<Node>()
    protected val childrenBounds = BoundingBox()

    val children: List<Node> get() = intChildren
    val size: Int get() = intChildren.size

    override fun onSceneChanged(oldScene: Scene?, newScene: Scene?) {
        super.onSceneChanged(oldScene, newScene)
        for (i in intChildren.indices) {
            intChildren[i].scene = newScene
        }
    }

    override fun update(ctx: KoolContext) {
        // call preRender on all children and update group bounding box
        childrenBounds.clear()
        for (i in intChildren.indices) {
            intChildren[i].update(ctx)
            childrenBounds.add(intChildren[i].bounds)
        }
        setLocalBounds()

        // compute global position and size based on group bounds and current model transform
        super.update(ctx)
    }

    protected open fun setLocalBounds() {
        bounds.set(childrenBounds)
    }

    override fun collectDrawCommands(renderPass: RenderPass, ctx: KoolContext) {
        super.collectDrawCommands(renderPass, ctx)

        if (isRendered) {
            for (i in intChildren.indices) {
                if (renderPass.type != RenderPass.Type.SHADOW || intChildren[i].isCastingShadow) {
                    intChildren[i].collectDrawCommands(renderPass, ctx)
                }
            }
        }
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