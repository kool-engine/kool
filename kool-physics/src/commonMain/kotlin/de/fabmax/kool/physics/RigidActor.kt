package de.fabmax.kool.physics

import de.fabmax.kool.math.*
import de.fabmax.kool.math.spatial.BoundingBox
import de.fabmax.kool.pipeline.shading.PbrMaterialConfig
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.scene.Tags
import de.fabmax.kool.scene.colorMesh
import de.fabmax.kool.scene.group
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.LazyMat4f

expect open class RigidActor : CommonRigidActor {
    val worldBounds: BoundingBox
    var simulationFilterData: FilterData
    var queryFilterData: FilterData
}

abstract class CommonRigidActor : Releasable {
    abstract var position: Vec3f
    abstract var rotation: Vec4f

    abstract var isTrigger: Boolean

    abstract val isActive: Boolean

    val transform = Mat4f()
    protected val invTransformLazy = LazyMat4f { transform.invert(it) }
    val invTransform: Mat4f
        get() = invTransformLazy.get()

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
        invTransformLazy.isDirty = true
        for (i in onPhysicsUpdate.indices) {
            onPhysicsUpdate[i](timeStep)
        }
    }

    fun toGlobal(vec: MutableVec3f, w: Float = 1f): MutableVec3f {
        return transform.transform(vec, w)
    }

    fun toLocal(vec: MutableVec3f, w: Float = 1f): MutableVec3f {
        return invTransform.transform(vec, w)
    }

    open fun toMesh(meshColor: Color, materialCfg: PbrMaterialConfig.() -> Unit = { }) = group {
        +colorMesh {
            generate {
                color = meshColor
                mutShapes.forEach { shape ->
                    withTransform {
                        transform.mul(shape.localPose)
                        shape.geometry.generateMesh(this)
                    }
                }
            }
            shader = pbrShader {
                materialCfg()
            }
            onUpdate += {
                this@group.transform.set(this@CommonRigidActor.transform)
                this@group.setDirty()
            }
        }
    }
}