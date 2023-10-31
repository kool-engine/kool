package de.fabmax.kool.physics

import de.fabmax.kool.math.*
import de.fabmax.kool.math.spatial.BoundingBox
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.scene.ColorMesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Tags
import de.fabmax.kool.scene.TrsTransform
import de.fabmax.kool.util.Color

expect abstract class RigidActor : CommonRigidActor {
    val worldBounds: BoundingBox
    var simulationFilterData: FilterData
    var queryFilterData: FilterData
}

abstract class CommonRigidActor : Releasable {
    abstract var position: Vec3f
    abstract var rotation: QuatF

    abstract var isTrigger: Boolean

    abstract val isActive: Boolean

    val transform = TrsTransform()

    val onPhysicsUpdate = mutableListOf<(Float) -> Unit>()

    protected val mutShapes = mutableListOf<Shape>()
    val shapes: List<Shape>
        get() = mutShapes

    val tags = Tags()

    fun setRotation(eulerX: AngleF, eulerY: AngleF, eulerZ: AngleF) {
        setRotation(MutableMat3f().rotate(eulerX, eulerY, eulerZ))
    }

    fun setRotation(rotation: Mat3f) {
        val q = MutableQuatF()
        rotation.decompose(q)
        this.rotation = q
    }

    fun setTransform(transform: Mat4f) {
        val q = MutableQuatF()
        transform.decompose(rotation = q)
        this.position = transform.transform(MutableVec3f())
        this.rotation = q
    }

    open fun attachShape(shape: Shape) {
        mutShapes += shape
    }

    open fun detachShape(shape: Shape) {
        mutShapes -= shape
    }

    override fun release() {
        mutShapes.clear()
    }

    internal open fun onPhysicsUpdate(timeStep: Float) {
        for (i in onPhysicsUpdate.indices) {
            onPhysicsUpdate[i](timeStep)
        }
    }

    fun toGlobal(vec: MutableVec3f, w: Float = 1f): MutableVec3f {
        return transform.transform(vec, w)
    }

    fun toLocal(vec: MutableVec3f, w: Float = 1f): MutableVec3f {
        return transform.matrixInverse.transform(vec, w)
    }

    open fun toMesh(meshColor: Color, materialCfg: KslPbrShader.Config.() -> Unit = { }): Node = ColorMesh().apply {
        generate {
            color = meshColor
            mutShapes.forEach { shape ->
                withTransform {
                    transform.mul(shape.localPose)
                    shape.geometry.generateMesh(this)
                }
            }
        }
        shader = KslPbrShader {
            color { vertexColor() }
            materialCfg()
        }
        transform = this@CommonRigidActor.transform
    }
}