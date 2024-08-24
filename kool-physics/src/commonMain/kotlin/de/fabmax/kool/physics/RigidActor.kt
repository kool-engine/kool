package de.fabmax.kool.physics

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.PoseF
import de.fabmax.kool.math.QuatF
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.spatial.BoundingBoxF
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.physics.character.HitActorBehavior
import de.fabmax.kool.scene.ColorMesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Tags
import de.fabmax.kool.scene.TrsTransformF
import de.fabmax.kool.util.BufferedList
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Releasable

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class RigidActorHolder

fun RigidActor.setPosition(pos: Vec3f) {
    pose = PoseF(position = pos, rotation = pose.rotation)
}

fun RigidActor.setRotation(rot: QuatF) {
    pose = PoseF(position = pose.position, rotation = rot)
}

interface RigidActor : Releasable {
    val holder: RigidActorHolder

    var simulationFilterData: FilterData
    var queryFilterData: FilterData
    var characterControllerHitBehavior: HitActorBehavior

    var pose: PoseF
    val worldBounds: BoundingBoxF
    val transform: TrsTransformF

    var isTrigger: Boolean

    var isActive: Boolean

    val onPhysicsUpdate: BufferedList<PhysicsStepListener>

    val shapes: List<Shape>

    val tags: Tags

    fun attachShape(shape: Shape)

    fun detachShape(shape: Shape)

    fun onPhysicsUpdate(timeStep: Float) {
        onPhysicsUpdate.update()
        for (i in onPhysicsUpdate.indices) {
            onPhysicsUpdate[i].onPhysicsStep(timeStep)
        }
    }

    fun toGlobal(vec: MutableVec3f, w: Float = 1f): MutableVec3f {
        return transform.transform(vec, w)
    }

    fun toLocal(vec: MutableVec3f, w: Float = 1f): MutableVec3f {
        return transform.invMatrixF.transform(vec, w)
    }

    fun toMesh(meshColor: Color, materialCfg: KslPbrShader.Config.Builder.() -> Unit = { }): Node = ColorMesh().apply {
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