package de.fabmax.kool.physics2d

import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.Vec2f

internal actual fun createBody(bodyDef: BodyDef, worldId: WorldId): BodyId = TODO()

internal actual fun BodyId.destroy(): Unit = TODO()

internal actual fun BodyId.addShape(geometry: Geometry, shapeDef: ShapeDef): ShapeId = TODO()

internal actual fun BodyId.getPose(result: MutablePose2f): Unit = TODO()

internal actual fun BodyId.setPose(pose: Pose2f): Unit = TODO()

internal actual fun BodyId.getLinearVelocity(result: MutableVec2f): MutableVec2f = TODO()

internal actual fun BodyId.setLinearVelocity(linearVelocity: Vec2f): Unit = TODO()

internal actual fun BodyId.getAngularVelocity(): Float = TODO()

internal actual fun BodyId.setAngularVelocity(angularVelocity: Float): Unit = TODO()

internal actual fun BodyId.setTargetTransform(target: Pose2f, duration: Float): Unit = TODO()

internal actual object BodyDefDefaults {
    actual val linearVelocity: Vec2f = TODO()
    actual val angularVelocity: Float = TODO()
    actual val linearDamping: Float = TODO()
    actual val angularDamping: Float = TODO()
    actual val gravityScale: Float = TODO()
    actual val sleepThreshold: Float = TODO()
    actual val enableSleep: Boolean = TODO()
    actual val isAwake: Boolean = TODO()
    actual val fixedRotation: Boolean = TODO()
    actual val isBullet: Boolean = TODO()
    actual val isEnabled: Boolean = TODO()
    actual val allowFastRotation: Boolean = TODO()
}