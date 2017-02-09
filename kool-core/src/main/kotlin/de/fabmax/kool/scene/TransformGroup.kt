package de.fabmax.kool.scene

import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.util.Mat4f

fun transformGroup(block: TransformGroup.() -> Unit): TransformGroup {
    val tg = TransformGroup()
    tg.block()
    return tg
}

/**
 * @author fabmax
 */
open class TransformGroup : Node() {
    val children: MutableList<Node> = mutableListOf()

    val transform = Mat4f()
    var animation: (TransformGroup.(RenderContext) -> Unit)? = null

    override fun render(ctx: RenderContext) {
        val anim = animation
        if (anim != null) {
            this.anim(ctx)
        }

        ctx.mvpState.modelMatrix.push()
        ctx.mvpState.modelMatrix.mul(transform)
        ctx.mvpState.update(ctx)

        for (i in children.indices) {
            children[i].render(ctx)
        }

        ctx.mvpState.modelMatrix.pop()
    }

    override fun delete(ctx: RenderContext) {
        for (i in children.indices) {
            children[i].delete(ctx)
        }
    }

    operator fun plusAssign(node: Node) {
        children.add(node)
    }

    operator fun minusAssign(node: Node) {
        children.remove(node)
    }

    operator fun Node.unaryPlus() {
        children.add(this)
    }
}