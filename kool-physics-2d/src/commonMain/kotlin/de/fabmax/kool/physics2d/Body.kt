package de.fabmax.kool.physics2d

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.checkIsNotReleased

data class BodyDef(
    val type: BodyType,
    val position: Vec2f,
    val rotation: Rotation = Rotation.IDENTITY,
    val linearVelocity: Vec2f = BodyDefDefaults.linearVelocity,
    val angularVelocity: Float = BodyDefDefaults.angularVelocity,
    val linearDamping: Float = BodyDefDefaults.linearDamping,
    val angularDamping: Float = BodyDefDefaults.angularDamping,
    val gravityScale: Float = BodyDefDefaults.gravityScale,
    val sleepThreshold: Float = BodyDefDefaults.sleepThreshold,
    val enableSleep: Boolean = BodyDefDefaults.enableSleep,
    val isAwake: Boolean = BodyDefDefaults.isAwake,
    val fixedRotation: Boolean = BodyDefDefaults.fixedRotation,
    val isBullet: Boolean = BodyDefDefaults.isBullet,
    val isEnabled: Boolean = BodyDefDefaults.isEnabled,
    val allowFastRotation: Boolean = BodyDefDefaults.allowFastRotation,
    val name: String? = null,
)

enum class BodyType {
    Static,
    Kinematic,
    Dynamic,
}

class Body internal constructor(bodyDef: BodyDef, world: Physics2dWorld) : BaseReleasable() {
    internal val bodyId = createBody(bodyDef, world.worldId)
    private val shapes = mutableMapOf<ShapeId, Pair<Geometry, ShapeDef>>()

    val type: BodyType = bodyDef.type

    private val posePrev = MutablePose2f().set(bodyDef.position, bodyDef.rotation)
    private val poseNext = MutablePose2f().set(bodyDef.position, bodyDef.rotation)
    private val poseLerp = MutablePose2f().set(bodyDef.position, bodyDef.rotation)

    val position: Vec2f get() = poseLerp.position
    val rotation: Rotation get() = poseLerp.rotation

    internal fun fetchPose() {
        checkIsNotReleased()
        posePrev.set(poseNext)
        bodyId.getPose(poseNext)
    }

    internal fun lerpPose(weightNext: Float) {
        posePrev.position.mix(poseNext.position, weightNext, poseLerp.position)
        posePrev.rotation.mix(poseNext.rotation, weightNext, poseLerp.rotation)
    }

    fun attachShape(geometry: Geometry, shapeDef: ShapeDef = ShapeDef.DEFAULT) {
        val shapeId = bodyId.addShape(geometry, shapeDef)
        shapes[shapeId] = geometry to shapeDef
    }

    fun setTargetTransform(target: Pose2f, duration: Float) {
        check(type == BodyType.Kinematic) { "Can only set target transform for kinematic bodies" }
        bodyId.setTargetTransform(target, duration)
    }

    override fun doRelease() {
        bodyId.destroy()
    }
}

internal expect fun createBody(bodyDef: BodyDef, worldId: WorldId): BodyId
internal expect fun BodyId.destroy()
internal expect fun BodyId.getPose(pose: MutablePose2f)
internal expect fun BodyId.setTargetTransform(target: Pose2f, duration: Float)
internal expect fun BodyId.addShape(geometry: Geometry, shapeDef: ShapeDef): ShapeId

internal expect object BodyDefDefaults {
    val linearVelocity: Vec2f
    val angularVelocity: Float
    val linearDamping: Float
    val angularDamping: Float
    val gravityScale: Float
    val sleepThreshold: Float
    val enableSleep: Boolean
    val isAwake: Boolean
    val fixedRotation: Boolean
    val isBullet: Boolean
    val isEnabled: Boolean
    val allowFastRotation: Boolean
}
