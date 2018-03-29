package de.fabmax.kool.physics

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f

class ContactPoints {

    private val pointCache = mutableListOf<ContactPoint>()
    private val usedPoints = mutableListOf<ContactPoint>()

    val points: List<ContactPoint>
        get() = usedPoints

    fun addContactPoint(): ContactPoint {
        val pt = if (!pointCache.isEmpty()) {
            pointCache.removeAt(pointCache.size - 1)
        } else {
            ContactPoint()
        }
        usedPoints.add(pt)
        return pt
    }

    fun addContactPoint(normalOnBInWorld: Vec3f, pointInWorld: Vec3f, depth: Float) =
            addContactPoint().set(normalOnBInWorld, pointInWorld, depth)

    fun clear() {
        pointCache.addAll(usedPoints)
        usedPoints.clear()
    }
}

class ContactPoint {

    val normalOnBInWorld = MutableVec3f()
    val pointInWorld = MutableVec3f()
    var depth = 0f

    fun set(normalOnBInWorld: Vec3f, pointInWorld: Vec3f, depth: Float): ContactPoint {
        this.normalOnBInWorld.set(normalOnBInWorld)
        this.pointInWorld.set(pointInWorld)
        this.depth = depth
        return this
    }

}
