package de.fabmax.kool.scene.doubleprec

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Mat4dStack
import de.fabmax.kool.math.RayTest
import de.fabmax.kool.scene.Node

fun doublePrecisionTransform(name: String? = null, block: TransformGroupDp.() -> Unit): DoublePrecisionRoot {
    val root = DoublePrecisionRoot(name)
    (root.root as TransformGroupDp).block()
    return root
}

class DoublePrecisionRoot(name: String? = null) : Node(name) {

    private val modelMatDp = Mat4dStack()

    var root: NodeDp = TransformGroupDp("${name ?: "DoublePrecisionRoot"}-rootGroup")

    init {
        modelMatDp.setIdentity()
    }

    override fun preRender(ctx: KoolContext) {
        modelMatDp.set(ctx.mvpState.modelMatrix)
        root.preRenderDp(ctx, modelMatDp)
        super.preRender(ctx)
    }

    override fun render(ctx: KoolContext) {
        root.render(ctx)
        super.render(ctx)
    }

    override fun postRender(ctx: KoolContext) {
        root.postRender(ctx)
        super.postRender(ctx)
    }

    override fun dispose(ctx: KoolContext) {
        root.dispose(ctx)
        super.dispose(ctx)
    }

    override fun rayTest(test: RayTest) {
        root.rayTest(test)
        super.rayTest(test)
    }

    override fun get(name: String): Node? = super.get(name) ?: root[name]
}