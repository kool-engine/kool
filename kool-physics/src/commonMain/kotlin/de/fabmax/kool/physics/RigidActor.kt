package de.fabmax.kool.physics

import de.fabmax.kool.math.*
import de.fabmax.kool.math.spatial.BoundingBox
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.scene.ColorMesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Tags
import de.fabmax.kool.scene.TrsTransformF
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Releasable

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class RigidActorHolder

interface RigidActor : Releasable {
    val holder: RigidActorHolder

    var simulationFilterData: FilterData
    var queryFilterData: FilterData

    var position: Vec3f
    var rotation: QuatF
    val worldBounds: BoundingBox

    var isTrigger: Boolean

    var isActive: Boolean

    val transform: TrsTransformF

    val onPhysicsUpdate: MutableList<(Float) -> Unit>

    val shapes: List<Shape>

    val tags: Tags

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

    fun attachShape(shape: Shape)

    fun detachShape(shape: Shape)

    fun onPhysicsUpdate(timeStep: Float) {
        for (i in onPhysicsUpdate.indices) {
            onPhysicsUpdate[i](timeStep)
        }
    }

    fun toGlobal(vec: MutableVec3f, w: Float = 1f): MutableVec3f {
        return transform.transform(vec, w)
    }

    fun toLocal(vec: MutableVec3f, w: Float = 1f): MutableVec3f {
        return transform.invMatrixF.transform(vec, w)
    }

    fun toMesh(meshColor: Color, materialCfg: KslPbrShader.Config.() -> Unit = { }): Node = ColorMesh().apply {
        generate {
            color = meshColor
            shapes.forEach { shape ->
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
        transform = this@RigidActor.transform
    }
}