package de.fabmax.kool.modules.gizmo

import de.fabmax.kool.math.*
import kotlin.math.atan2

class AxisRotation(val axis: GizmoHandle.Axis) : GizmoOperationBase() {
    private val dragPlane = PlaneD(Vec3d.ZERO, axis.axis)
    private val dragStart = MutableVec3d()

    override fun onDragStart(dragCtx: DragContext) {
        if (dragPlane.intersection(dragCtx.localRay, dragStart)) {
            dragCtx.startManipulation()
        }
    }

    override fun onDrag(dragCtx: DragContext) {
        val point = MutableVec3d()
        if (dragPlane.intersection(dragCtx.localRay, point)) {
            val a0 = angle(dragStart)
            val a1 = angle(point)
            val aTicked = applyTick(a1 - a0, dragCtx.rotationTick)
            dragCtx.manipulateAxisRotation(axis.axis, aTicked.deg)
        }
    }

    private fun angle(point: Vec3d): Double {
        val a = when {
            axis.axis.x != 0.0 -> atan2(point.z, point.y)
            axis.axis.y != 0.0 -> atan2(point.x, point.z)
            else -> atan2(point.y, point.x)
        }
        return a.toDeg()
    }
}

class CamPlaneRotation : GizmoOperationBase() {
    private val dragPlane = PlaneD()
    private val dragStart = MutableVec3d()

    override fun onDragStart(dragCtx: DragContext) {
        dragCtx.globalToLocal.transform(dragCtx.camera.dataD.globalLookDir, 0.0, dragPlane.n)
        dragPlane.n.norm()
        dragPlane.intersection(dragCtx.localRay, dragStart)
        dragCtx.startManipulation()
    }

    override fun onDrag(dragCtx: DragContext) {
        val point = MutableVec3d()
        if (dragPlane.intersection(dragCtx.localRay, point)) {
            val a0 = angle(dragStart, dragCtx)
            val a1 = angle(point, dragCtx)
            dragCtx.manipulateAxisRotation(dragPlane.n, a0 - a1)
        }
    }

    private fun angle(point: Vec3d, dragCtx: DragContext): AngleD {
        val cam = dragCtx.camera.dataD
        val vec = MutableVec3d()
        val x = point dot dragCtx.globalToLocal.transform(cam.globalRight, 0.0, vec)
        val y = point dot dragCtx.globalToLocal.transform(cam.globalUp, 0.0, vec)
        return atan2(y, x).rad
    }
}

class FreeRotation : GizmoOperationBase() {
    private val rotation = MutableQuatD()
    private val startPointerPos = MutableVec2d()

    override fun onDragStart(dragCtx: DragContext) {
        startPointerPos.set(dragCtx.virtualPointerPos)
        dragCtx.startManipulation()
    }

    override fun onDrag(dragCtx: DragContext) {
        val dx = dragCtx.virtualPointerPos.x - startPointerPos.x
        val dy = dragCtx.virtualPointerPos.y - startPointerPos.y

        val axX = dragCtx.globalToLocal.transform(dragCtx.camera.dataD.globalUp, 0.0, MutableVec3d())
        val axY = dragCtx.globalToLocal.transform(dragCtx.camera.dataD.globalRight, 0.0, MutableVec3d())

        rotation
            .setIdentity()
            .rotate(dx.deg * 0.3, axX)
            .rotate(dy.deg * 0.3, axY)
        dragCtx.manipulateRotation(rotation)

    }
}