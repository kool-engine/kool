package de.fabmax.kool.math

import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Node

class RayTest(isCollectHits: Boolean = false) {
    val ray = RayD()
    var camera: Camera? = null

    val hitPositionGlobal = MutableVec3d()
    val hitNormalGlobal = MutableVec3d()

    private val tmpRayF = RayF()
    private val tmpRayD = RayD()
    private val tmpHitPoint = MutableVec3d()
    private val tmpVec = MutableVec3d()

    var maxDistance = Double.POSITIVE_INFINITY
    var hitNode: Node? = null
        private set
    var hitDistance = Double.POSITIVE_INFINITY
        private set
    val isHit: Boolean
        get() = hitDistance < Double.POSITIVE_INFINITY

    val hits: MutableList<HitNode>? = if (isCollectHits) mutableListOf() else null

    fun clear(maxDistance: Double = Double.POSITIVE_INFINITY, camera: Camera? = null) {
        this.maxDistance = maxDistance
        this.camera = camera
        hitPositionGlobal.set(Vec3d.ZERO)
        hitNormalGlobal.set(Vec3d.ZERO)
        hitNode = null
        hitDistance = Double.POSITIVE_INFINITY
        hits?.clear()
    }

    fun setHit(node: Node, hitDistanceGlobal: Double, hitNormalGlobal: Vec3d? = null) {
        if (hitDistanceGlobal < maxDistance) {
            hitPositionGlobal.set(ray.direction).mul(hitDistanceGlobal).add(ray.origin)
            setHit(node, hitPositionGlobal, hitNormalGlobal)
        }
    }

    fun setHit(node: Node, hitPositionGlobal: Vec3d, hitNormalGlobal: Vec3d? = null) {
        val dist = hitPositionGlobal.distance(ray.origin)
        if (dist < maxDistance) {
            this.hitPositionGlobal.set(hitPositionGlobal)
            this.hitNormalGlobal.set(hitNormalGlobal ?: Vec3d.ZERO)
            hitNode = node
            hitDistance = dist
        }
    }

    /**
     * Returns true if this [ray] hits the [node]'s bounding sphere AND the hit is closer than any previous hit.
     */
    fun isIntersectingBoundingSphere(node: Node) = isIntersectingBoundingSphere(node.globalCenter, node.globalRadius)

    /**
     * Returns true if this [ray] hits the specified bounding sphere (in global coordinates) AND the hit is closer than
     * any previous hit.
     */
    fun isIntersectingBoundingSphere(globalCenter: Vec3f, globalRadius: Float): Boolean {
        val isSphereHit = ray.sphereIntersection(tmpVec.set(globalCenter), globalRadius.toDouble(), tmpHitPoint)
        return isSphereHit && tmpHitPoint.distance(ray.origin) <= hitDistance
    }

    fun getRayTransformed(matrix: Mat4f): RayF {
        return ray.toRayF(tmpRayF).transformBy(matrix)
    }

    fun getRayTransformed(matrix: Mat4d): RayF {
        return ray.transformBy(matrix, tmpRayD).toRayF(tmpRayF)
    }

    fun collectHitBoundingSphere(node: Node) {
        hits?.add(HitNode(node, HitType.BOUNDING_SPHERE))
    }

    fun collectHitBoundingBox(node: Node) {
        hits?.add(HitNode(node, HitType.BOUNDING_BOX))
    }

    fun collectHitGeometry(node: Node) {
        hits?.add(HitNode(node, HitType.GEOMETRY))
    }

    fun collectNoHit(node: Node) {
        hits?.add(HitNode(node, HitType.NO_HIT))
    }

    data class HitNode(
        val node: Node,
        val hitType: HitType
    ) {
        override fun toString(): String = "${node.name}: $hitType"
    }

    enum class HitType {
        NO_HIT,
        BOUNDING_SPHERE,
        BOUNDING_BOX,
        GEOMETRY
    }
}