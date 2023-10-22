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
    abstract var rotation: Vec4f

    abstract var isTrigger: Boolean

    abstract val isActive: Boolean

    val transform = TrsTransform()
//    protected val invTransformLazy = LazyMat4f { transform.invert(it) }
//    val invTransform: Mat4f
//        get() = invTransformLazy.get()

    val onPhysicsUpdate = mutableListOf<(Float) -> Unit>()

    protected val mutShapes = mutableListOf<Shape>()
    val shapes: List<Shape>
        get() = mutShapes

    val tags = Tags()

    fun setRotation(eulerX: Float, eulerY: Float, eulerZ: Float) {
        setRotation(Mat3f().setRotate(eulerX, eulerY, eulerZ))
    }

    fun setRotation(rotation: Mat3f) {
        this.rotation = rotation.getRotation(MutableVec4f())
    }

    fun setTransform(transform: Mat4f) {
        this.position = transform.transform(MutableVec3f())
        this.rotation = transform.getRotation(MutableVec4f())
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
//        onUpdate += {
//            transform.set(this@CommonRigidActor.transform)
//            transform.markDirty()
//        }
    }
}