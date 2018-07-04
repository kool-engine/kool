package de.fabmax.kool.scene.doubleprec

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Mat4dStack
import de.fabmax.kool.math.MutableVec3d
import de.fabmax.kool.math.RayTest
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.BoundingBox

abstract class NodeDp(name: String? = null) : Node(name) {

    open fun preRenderDp(ctx: KoolContext, modelMatDp: Mat4dStack) {
        preRender(ctx)
    }

    open fun renderDp(ctx: KoolContext, modelMatDp: Mat4dStack) {
        render(ctx)
    }

    open fun toGlobalCoordsDp(vec: MutableVec3d, w: Double = 1.0): MutableVec3d {
        val p = parent ?: return vec
        if (p is NodeDp) {
            p.toGlobalCoordsDp(vec, w)
        }
        return vec
    }

    open fun toLocalCoordsDp(vec: MutableVec3d, w: Double = 1.0): MutableVec3d {
        val p = parent ?: return vec
        if (p is NodeDp) {
            p.toLocalCoordsDp(vec, w)
        }
        return vec
    }
}

class NodeProxy(val node: Node) : NodeDp(node.name) {

    override val bounds: BoundingBox
        get() = node.bounds

    override val globalCenter: Vec3f
        get() = node.globalCenter

    override var globalRadius: Float
        get() = node.globalRadius
        set(_) { }

    init {
        node.parent = this
    }

    override fun onSceneChanged(oldScene: Scene?, newScene: Scene?) {
        super.onSceneChanged(oldScene, newScene)
        node.scene = newScene
    }

    override fun preRender(ctx: KoolContext) {
        node.preRender(ctx)
        super.preRender(ctx)
    }

    override fun render(ctx: KoolContext) {
        node.render(ctx)
        super.render(ctx)
    }

    override fun postRender(ctx: KoolContext) {
        node.postRender(ctx)
        super.postRender(ctx)
    }

    override fun dispose(ctx: KoolContext) {
        node.dispose(ctx)
        super.dispose(ctx)
    }

    override fun rayTest(test: RayTest) {
        node.rayTest(test)
    }

    override fun get(name: String): Node? {
        return node[name]
    }
}
