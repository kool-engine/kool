package de.fabmax.kool.scene.doubleprec

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Mat4dStack
import de.fabmax.kool.math.RayTest
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene

fun doublePrecisionTransform(name: String? = null, block: TransformGroupDp.() -> Unit): DoublePrecisionRoot<TransformGroupDp> {
    val root = DoublePrecisionRoot(TransformGroupDp("${name ?: "DoublePrecisionRoot"}-rootGroup"), name)
    root.root.block()
    return root
}

class DoublePrecisionRoot<T: NodeDp>(val root: T, name: String? = null) : Node(name) {

    private val modelMatDp = Mat4dStack()

    init {
        modelMatDp.setIdentity()
        root.parent = this
    }

    override fun onSceneChanged(oldScene: Scene?, newScene: Scene?) {
        super.onSceneChanged(oldScene, newScene)
        root.scene = newScene
    }

    override fun preRender(ctx: KoolContext) {
        modelMatDp.set(ctx.mvpState.modelMatrix)
        root.preRenderDp(ctx, modelMatDp)
        super.preRender(ctx)
    }

    override fun render(ctx: KoolContext) {
        modelMatDp.set(ctx.mvpState.modelMatrix)
        root.renderDp(ctx, modelMatDp)
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