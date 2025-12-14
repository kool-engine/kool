package de.fabmax.kool.physics2d

import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.Vec2f

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

class Body internal constructor(bodyDef: BodyDef, internal val bodyId: BodyId) {
    private val shapes = mutableMapOf<ShapeId, Pair<Geometry, ShapeDef>>()
    val type: BodyType = bodyDef.type

    var isValid = true; private set

    private val posePrev = MutablePose2f().set(bodyDef.position, bodyDef.rotation)
    private val poseNext = MutablePose2f().set(bodyDef.position, bodyDef.rotation)
    private val poseLerp = MutablePose2f().set(bodyDef.position, bodyDef.rotation)
    val position: Vec2f get() = poseLerp.position
    val rotation: Rotation get() = poseLerp.rotation

    private val linVelPrev = MutableVec2f()
    private val linVelNext = MutableVec2f()
    private val linVelLerp = MutableVec2f()
    val linearVelocity: Vec2f get() = linVelLerp

    private var angVelPrev = 0f
    private var angVelNext = 0f
    private var angVelLerp = 0f
    val angularVelocity: Float get() = angVelLerp

    private fun checkIsValid() {
        check(isValid) { "Body has been removed destroyed" }
    }

    internal fun destroy() {
        isValid = false
        bodyId.destroy()
    }

    internal fun fetchData() {
        checkIsValid()
        posePrev.set(poseNext)
        linVelPrev.set(linVelNext)
        angVelPrev = angVelNext
        bodyId.getPose(poseNext)
        bodyId.getLinearVelocity(linVelNext)
        angVelNext = bodyId.getAngularVelocity()
    }

    internal fun lerpData(weightNext: Float) {
        posePrev.position.mix(poseNext.position, weightNext, poseLerp.position)
        posePrev.rotation.mix(poseNext.rotation, weightNext, poseLerp.rotation)
        linVelPrev.mix(linVelNext, weightNext, linVelLerp)
        angVelLerp = (angVelNext - angVelPrev) * weightNext + angVelPrev
    }

    fun attachShape(geometry: Geometry, shapeDef: ShapeDef = ShapeDef.DEFAULT) {
        val shapeId = bodyId.addShape(geometry, shapeDef)
        shapes[shapeId] = geometry to shapeDef
    }

    fun setPose(position: Vec2f, rotation: Rotation) {
        checkIsValid()
        bodyId.setPose(Pose2f(position, rotation))
    }

    fun setLinearVelocity(linearVelocity: Vec2f) {
        checkIsValid()
        bodyId.setLinearVelocity(linearVelocity)
    }

    fun setAngularVelocity(angularVelocity: Float) {
        checkIsValid()
        bodyId.setAngularVelocity(angularVelocity)
    }

    fun setTargetTransform(target: Pose2f, duration: Float) {
        checkIsValid()
        check(type == BodyType.Kinematic) { "Can only set target transform for kinematic bodies" }
        bodyId.setTargetTransform(target, duration)
    }
}

internal expect fun createBody(bodyDef: BodyDef, worldId: WorldId): BodyId
internal expect fun BodyId.destroy()

internal expect fun BodyId.addShape(geometry: Geometry, shapeDef: ShapeDef): ShapeId

internal expect fun BodyId.getPose(result: MutablePose2f)
internal expect fun BodyId.setPose(pose: Pose2f)

internal expect fun BodyId.getLinearVelocity(result: MutableVec2f): MutableVec2f
internal expect fun BodyId.setLinearVelocity(linearVelocity: Vec2f)

internal expect fun BodyId.getAngularVelocity(): Float
internal expect fun BodyId.setAngularVelocity(angularVelocity: Float)

internal expect fun BodyId.setTargetTransform(target: Pose2f, duration: Float)

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
