package de.fabmax.kool.modules.gizmo

import de.fabmax.kool.input.Pointer
import de.fabmax.kool.math.*
import de.fabmax.kool.scene.Camera
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sign

interface GizmoOperation {
    fun onDragStart(dragCtx: DragContext)
    fun onDrag(dragCtx: DragContext)
    fun onDragEnd(dragCtx: DragContext)
}

abstract class GizmoOperationBase : GizmoOperation {

    override fun onDragEnd(dragCtx: DragContext) {
        dragCtx.finishManipulation()
    }

    protected fun RayD.signedDistance(pointerRay: RayD): Double? {
        val point = closestPointOnRay(pointerRay, MutableVec3d())

        val pointerRayPoint = pointerRay.closestPositivePointOnRay(this, MutableVec3d())
        val isNearestPointBehindCamera = pointerRayPoint.isFuzzyEqual(pointerRay.origin)
        val isPointerRayParallelToAxis = abs(direction dot pointerRay.direction) > rayAxisDirectionThresh
        if (isNearestPointBehindCamera || isPointerRayParallelToAxis) {
            return null
        }

        val direction = sign((point - origin) dot direction)
        return point.distance(origin) * direction
    }

    protected fun PlaneD.intersection(ray: RayD, result: MutableVec3d): Boolean {
        if (abs(ray.direction dot n) < rayPlaneDirectionThresh) {
            return false
        }
        return intersectionPoint(ray, result)
    }

    companion object {
        protected val rayAxisDirectionThresh = cos(5.0.deg.rad)
        protected val rayPlaneDirectionThresh = cos(85.0.deg.rad)
    }
}

data class DragContext(
    val gizmo: GizmoNode,
    val pointer: Pointer,
    val globalRay: RayD,
    val localRay: RayD,
    val globalToLocal: Mat4d,
    val camera: Camera
) {
    val isManipulating: Boolean
        get() = gizmo.isManipulating

    fun startManipulation() {
        if (!isManipulating) {
            gizmo.startManipulation()
        }
    }

    fun finishManipulation() {
        if (isManipulating) {
            gizmo.finishManipulation()
        }
    }

    fun cancelManipulation() {
        if (isManipulating) {
            gizmo.cancelManipulation()
        }
    }

    fun manipulateAxisTranslation(axis: GizmoHandle.Axis, distance: Double) {
        if (isManipulating) {
            gizmo.manipulateAxisTranslation(axis, distance)
        }
    }

    fun manipulateTranslation(translationOffset: Vec3d) {
        if (isManipulating) {
            gizmo.manipulateTranslation(translationOffset)
        }
    }

    fun manipulateAxisRotation(axis: Vec3d, angleD: AngleD) {
        if (isManipulating) {
            gizmo.manipulateAxisRotation(axis, angleD)
        }
    }

    fun manipulateRotation(rotation: QuatD) {
        if (isManipulating) {
            gizmo.manipulateRotation(rotation)
        }
    }

    fun manipulateScale(scale: Vec3d) {
        if (isManipulating) {
            gizmo.manipulateScale(scale)
        }
    }
}
