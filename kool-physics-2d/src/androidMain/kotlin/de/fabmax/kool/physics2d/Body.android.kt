package de.fabmax.kool.physics2d

import box2dandroid.*
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.util.ScopedMemory
import de.fabmax.kool.util.scopedMem

internal actual fun createBody(bodyDef: BodyDef, worldId: WorldId): BodyId = scopedMem {
    val b2BodyDef = allocBodyDef()
    B2_Body.defaultBodyDef(b2BodyDef)

    b2BodyDef.type = when (bodyDef.type) {
        BodyType.Static -> b2BodyType.b2_staticBody
        BodyType.Kinematic -> b2BodyType.b2_kinematicBody
        BodyType.Dynamic -> b2BodyType.b2_dynamicBody
    }
    b2BodyDef.position = allocVec2(bodyDef.position)
    b2BodyDef.rotation = allocRotation(bodyDef.rotation)
    b2BodyDef.linearVelocity = allocVec2(bodyDef.linearVelocity)
    b2BodyDef.angularVelocity = bodyDef.angularVelocity
    b2BodyDef.linearDamping = bodyDef.linearDamping
    b2BodyDef.angularDamping = bodyDef.angularDamping
    b2BodyDef.gravityScale = bodyDef.gravityScale
    b2BodyDef.sleepThreshold = bodyDef.sleepThreshold
    b2BodyDef.enableSleep = bodyDef.enableSleep
    b2BodyDef.isAwake = bodyDef.isAwake
    b2BodyDef.fixedRotation = bodyDef.fixedRotation
    b2BodyDef.isBullet = bodyDef.isBullet
    b2BodyDef.isEnabled = bodyDef.isEnabled
    b2BodyDef.allowFastRotation = bodyDef.allowFastRotation
    bodyDef.name?.let { b2BodyDef.name = it }

    BodyId(B2_Body.createBody(worldId.id, b2BodyDef))
}

internal actual fun BodyId.destroy() = B2_Body.destroyBody(id)

internal actual fun BodyId.addShape(geometry: Geometry, shapeDef: ShapeDef): ShapeId = scopedMem {
    val b2ShapeDef = allocShapeDef()
    B2_Shape.defaultShapeDef(b2ShapeDef)
    b2ShapeDef.material = allocMaterial(shapeDef.material)
    b2ShapeDef.density = shapeDef.density
    b2ShapeDef.filter = allocFilter(shapeDef.filter)
    b2ShapeDef.isSensor = shapeDef.isSensor
    b2ShapeDef.enableSensorEvents = shapeDef.enableSensorEvents
    b2ShapeDef.enableContactEvents = shapeDef.enableContactEvents
    b2ShapeDef.enableHitEvents = shapeDef.enableHitEvents
    b2ShapeDef.enablePreSolveEvents = shapeDef.enablePreSolveEvents
    b2ShapeDef.invokeContactCreation = shapeDef.invokeContactCreation
    b2ShapeDef.updateBodyMass = shapeDef.updateBodyMass

    val shapeId = when (geometry) {
        is Geometry.Box -> createPolygonShape(b2ShapeDef) { polygon ->
            B2_Geometry.makeBox(geometry.halfWidth, geometry.halfHeight, polygon)
        }
        is Geometry.Circle -> {
            B2_Shape.createCircleShape(id, b2ShapeDef, allocCircle(geometry.radius))
        }
        is Geometry.Square -> createPolygonShape(b2ShapeDef) { polygon ->
            B2_Geometry.makeSquare(geometry.halfSize, polygon)
        }
    }
    ShapeId(shapeId)
}

internal actual fun BodyId.getPose(result: MutablePose2f) {
    val pos = B2_Body.getPosition(id)
    val rot = B2_Body.getRotation(id)
    result.position.set(pos.x, pos.y)
    result.rotation.set(rot.s, rot.c)
}

internal actual fun BodyId.setPose(pose: Pose2f) = scopedMem {
    B2_Body.setTransform(id, allocVec2(pose.position), allocRotation(pose.rotation))
}

internal actual fun BodyId.getLinearVelocity(result: MutableVec2f): MutableVec2f =
    B2_Body.getLinearVelocity(id).toVec2f(result)

internal actual fun BodyId.setLinearVelocity(linearVelocity: Vec2f) = scopedMem {
    B2_Body.setLinearVelocity(id, allocVec2(linearVelocity))
}

internal actual fun BodyId.getAngularVelocity(): Float = B2_Body.getAngularVelocity(id)

internal actual fun BodyId.setAngularVelocity(angularVelocity: Float) = B2_Body.setAngularVelocity(id, angularVelocity)

internal actual fun BodyId.setTargetTransform(target: Pose2f, duration: Float) {
    scopedMem {
        val target = allocTransform(target)
        B2_Body.setTargetTransform(id, target, duration)
    }
}

context(ms: ScopedMemory)
private inline fun BodyId.createPolygonShape(shapeDef: b2ShapeDef, block: (b2Polygon) -> Unit): Long {
    val polygon = ms.allocPolygon()
    block(polygon)
    return B2_Shape.createPolygonShape(id, shapeDef, polygon)
}

internal actual object BodyDefDefaults {
    private val defaults = b2BodyDef().also { B2_Body.defaultBodyDef(it) }

    actual val linearVelocity: Vec2f = Vec2f(defaults.linearVelocity.x, defaults.linearVelocity.y)
    actual val angularVelocity: Float = defaults.angularVelocity
    actual val linearDamping: Float = defaults.linearDamping
    actual val angularDamping: Float = defaults.angularDamping
    actual val gravityScale: Float = defaults.gravityScale
    actual val sleepThreshold: Float = defaults.sleepThreshold
    actual val enableSleep: Boolean = defaults.enableSleep
    actual val isAwake: Boolean = defaults.isAwake
    actual val fixedRotation: Boolean = defaults.fixedRotation
    actual val isBullet: Boolean = defaults.isBullet
    actual val isEnabled: Boolean = defaults.isEnabled
    actual val allowFastRotation: Boolean = defaults.allowFastRotation
}