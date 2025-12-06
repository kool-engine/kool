package de.fabmax.kool.physics2d

import box2dandroid.*
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.util.MemoryStack

fun MemoryStack.allocVec2(v: Vec2f) = allocVec2(v.x, v.y)
fun MemoryStack.allocVec2(x: Float, y: Float) = autoDelete(b2Vec2(), b2Vec2::destroy).also {
    it.x = x
    it.y = y
}

fun MemoryStack.allocRotation(r: Rotation) = autoDelete(b2Rot(), b2Rot::destroy).also {
    it.s = r.sin
    it.c = r.cos
}

fun MemoryStack.allocTransform(t: Pose2f) = autoDelete(b2Transform(), b2Transform::destroy).also {
    it.p.x = t.position.x
    it.p.y = t.position.y
    it.q.s = t.rotation.sin
    it.q.c = t.rotation.cos
}

fun MemoryStack.allocMaterial(m: SurfaceMaterial) = autoDelete(b2SurfaceMaterial(), b2SurfaceMaterial::destroy).also {
    it.friction = m.friction
    it.restitution = m.restitution
    it.rollingResistance = m.rollingResistance
    it.tangentSpeed = m.tangentSpeed
    it.userMaterialId = m.userMaterialId
    it.customColor = m.customColor
}

fun MemoryStack.allocFilter(f: Filter) = autoDelete(b2Filter(), b2Filter::destroy).also {
    it.categoryBits = f.categoryBits
    it.groupIndex = f.groupIndex
    it.maskBits = f.maskBits
}

fun MemoryStack.allocPolygon() = autoDelete(b2Polygon(), b2Polygon::destroy)
fun MemoryStack.allocCircle(radius: Float) = autoDelete(b2Circle(), b2Circle::destroy).also {
    it.radius = radius
}

fun MemoryStack.allocBodyDef() = autoDelete(b2BodyDef(), b2BodyDef::destroy)
fun MemoryStack.allocShapeDef() = autoDelete(b2ShapeDef(), b2ShapeDef::destroy)
fun MemoryStack.allocWordDef() = autoDelete(b2WorldDef(), b2WorldDef::destroy)