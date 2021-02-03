package de.fabmax.kool.physics

import de.fabmax.kool.math.*
import de.fabmax.kool.pipeline.shading.PbrMaterialConfig
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.scene.colorMesh
import de.fabmax.kool.scene.group
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.Color

expect open class RigidActor : CommonRigidActor {
    val worldBounds: BoundingBox

    fun setSimulationFilterData(simulationFilterData: FilterData)

    fun setQueryFilterData(queryFilterData: FilterData)

    open fun release()
}

abstract class CommonRigidActor {
    abstract var position: Vec3f
    abstract var rotation: Vec4f

    val transform = Mat4f()

    val onFixedUpdate = mutableListOf<(Float) -> Unit>()

    protected val mutShapes = mutableListOf<Shape>()
    val shapes: List<Shape>
        get() = mutShapes


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

    internal open fun fixedUpdate(timeStep: Float) {
        for (i in onFixedUpdate.indices) {
            onFixedUpdate[i](timeStep)
        }
    }

    fun toMesh(meshColor: Color, materialCfg: PbrMaterialConfig.() -> Unit = { }) = group {
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