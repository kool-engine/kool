package de.fabmax.kool.physics2d

import box2d.*
import de.fabmax.kool.math.Vec2f
import org.lwjgl.system.MemoryStack

fun MemoryStack.allocVec2(v: Vec2f) = allocVec2(v.x, v.y)
fun MemoryStack.allocVec2(x: Float, y: Float) = b2Vec2.createAt(this, MemoryStack::nmalloc).also {
    it.x = x
    it.y = y
}

fun MemoryStack.allocRotation(r: Rotation) = b2Rot.createAt(this, MemoryStack::nmalloc).also {
    it.s = r.sin
    it.c = r.cos
}

fun MemoryStack.allocTransform(t: Pose2f) = b2Transform.createAt(this, MemoryStack::nmalloc).also {
    it.p.x = t.position.x
    it.p.y = t.position.y
    it.q.s = t.rotation.sin
    it.q.c = t.rotation.cos
}

fun MemoryStack.allocMaterial(m: SurfaceMaterial) = b2SurfaceMaterial.createAt(this, MemoryStack::nmalloc).also {
    it.friction = m.friction
    it.restitution = m.restitution
    it.rollingResistance = m.rollingResistance
    it.tangentSpeed = m.tangentSpeed
    it.userMaterialId = m.userMaterialId
    it.customColor = m.customColor
}

fun MemoryStack.allocFilter(f: Filter) = b2Filter.createAt(this, MemoryStack::nmalloc).also {
    it.categoryBits = f.categoryBits
    it.groupIndex = f.groupIndex
    it.maskBits = f.maskBits
}

fun MemoryStack.allocPolygon() = b2Polygon.createAt(this, MemoryStack::nmalloc)
fun MemoryStack.allocCircle(radius: Float) = b2Circle.createAt(this, MemoryStack::nmalloc).also {
    it.radius = radius
}

fun MemoryStack.allocBodyDef() = b2BodyDef.createAt(this, MemoryStack::nmalloc)
fun MemoryStack.allocShapeDef() = b2ShapeDef.createAt(this, MemoryStack::nmalloc)
fun MemoryStack.allocWordDef() = b2WorldDef.createAt(this, MemoryStack::nmalloc)