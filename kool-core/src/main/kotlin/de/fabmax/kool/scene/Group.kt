package de.fabmax.kool.scene

import de.fabmax.kool.platform.RenderContext

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

    override fun render(ctx: RenderContext) {
        for (i in children.indices) {
            children[i].render(ctx)
        }
    }

    override fun delete(ctx: RenderContext) {
        for (i in children.indices) {
            children[i].delete(ctx)
        }
    }

    override fun findByName(name: String): Node? {
        if (name == this.name) {
            return this
        }
        for (i in children.indices) {
            val nd = children[i].findByName(name)
            if (nd != null) {
                return nd
            }
        }
        return null
    }

    fun addNode(node: Node) {
        children.add(node)
        node.parent = this
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