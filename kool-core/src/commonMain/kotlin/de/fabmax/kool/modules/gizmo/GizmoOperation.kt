package de.fabmax.kool.modules.gizmo

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
    var isDrag = false
        protected set

    val startPointerPos = MutableVec2d()
    val dragPointerPos = MutableVec2d()
    val projectedPointerPos = MutableVec3d()

    override fun onDragStart(dragCtx: DragContext) {
        isDrag = true
        startPointerPos.set(dragCtx.virtualPointerPos)
        dragPointerPos.set(dragCtx.virtualPointerPos)
    }

    override fun onDrag(dragCtx: DragContext) {
        dragPointerPos.set(dragCtx.virtualPointerPos)
    }

    override fun onDragEnd(dragCtx: DragContext) {
        dragCtx.finishManipulation()
        isDrag = false
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

    protected fun applyTick(value: Double, tick: Double): Double {
        if (tick <= 0) {
            return value
        }
        val v = value + tick * 0.5
        return v - v.mod(tick)
    }

    companion object {
        protected val rayAxisDirectionThresh = cos(5.0.deg.rad)
        protected val rayPlaneDirectionThresh = cos(85.0.deg.rad)
    }
}

data class DragContext(
    val gizmo: GizmoNode,
    val virtualPointerPos: Vec2f,
    val globalRay: RayD,
    val localRay: RayD,
    val globalToLocal: Mat4d,
    val localToGlobal: Mat4d,
    val camera: Camera
) {
    val isManipulating: Boolean get() = gizmo.isManipulating

    val translationTick: Double get() = gizmo.translationTick.value
    val rotationTick: Double get() = gizmo.rotationTick.value
    val scaleTick: Double get() = gizmo.scaleTick.value

    val overwriteValue: Double? get() = gizmo.overwriteManipulatorValue.value

    fun startManipulation(operation: GizmoOperation?) {
        if (isManipulating) {
            cancelManipulation()
        }
        gizmo.startManipulation(operation)
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
            val dist = overwriteValue ?: distance
            gizmo.manipulateAxisTranslation(axis, dist)
        }
    }

    fun manipulateTranslation(translationOffset: Vec3d) {
        if (isManipulating) {
            gizmo.manipulateTranslation(translationOffset)
        }
    }

    fun manipulateAxisRotation(axis: Vec3d, angleD: AngleD) {
        if (isManipulating) {
            val angle = overwriteValue?.deg ?: angleD
            gizmo.manipulateAxisRotation(axis, angle)
        }
    }

    fun manipulateRotation(rotation: QuatD) {
        if (isManipulating) {
            gizmo.manipulateRotation(rotation)
        }
    }

    fun manipulateAxisScale(axis: GizmoHandle.Axis, scale: Double) {
        if (isManipulating) {
            val s = overwriteValue ?: scale
            val sVec = when (axis) {
                GizmoHandle.Axis.POS_X -> Vec3d(s, 1.0, 1.0)
                GizmoHandle.Axis.POS_Y -> Vec3d(1.0, s, 1.0)
                GizmoHandle.Axis.POS_Z -> Vec3d(1.0, 1.0, s)
                GizmoHandle.Axis.NEG_X -> Vec3d(s, 1.0, 1.0)
                GizmoHandle.Axis.NEG_Y -> Vec3d(1.0, s, 1.0)
                GizmoHandle.Axis.NEG_Z -> Vec3d(1.0, 1.0, s)
            }
            gizmo.manipulateScale(sVec)
        }
    }

    fun manipulatePlaneScale(plane: GizmoHandle.Axis, scale: Double) {
        if (isManipulating) {
            val s = overwriteValue ?: scale
            val sVec = when (plane) {
                GizmoHandle.Axis.POS_X -> Vec3d(1.0, s, s)
                GizmoHandle.Axis.POS_Y -> Vec3d(s, 1.0, s)
                GizmoHandle.Axis.POS_Z -> Vec3d(s, s, 1.0)
                GizmoHandle.Axis.NEG_X -> Vec3d(1.0, s, s)
                GizmoHandle.Axis.NEG_Y -> Vec3d(s, 1.0, s)
                GizmoHandle.Axis.NEG_Z -> Vec3d(s, s, 1.0)
            }
            gizmo.manipulateScale(sVec)
        }
    }

    fun manipulateScale(scale: Vec3d) {
        if (isManipulating) {
            val s = overwriteValue?.let { Vec3d(it) } ?: scale
            gizmo.manipulateScale(s)
        }
    }
}

sealed class ManipulatorValue {
    data class ManipulatorValue1d(val value: Double) : ManipulatorValue()
    data class ManipulatorValue3d(val value: Vec3d) : ManipulatorValue()
    data class ManipulatorValue4d(val value: Vec4d) : ManipulatorValue()
}
