package de.fabmax.kool.modules.gizmo

import de.fabmax.kool.math.*
import kotlin.math.atan2

interface GizmoRotation {
    val deltaRotation: AngleD
    val rotationPlane: PlaneD?
    val startProjectedPos: MutableVec3d
}

class AxisRotation(val axis: GizmoHandle.Axis) : GizmoOperationBase(), GizmoRotation {
    override val rotationPlane = PlaneD(Vec3d.ZERO, axis.axis)
    override val startProjectedPos = MutableVec3d()
    override var deltaRotation = 0.0.deg

    override fun onDragStart(dragCtx: DragContext) {
        super.onDragStart(dragCtx)
        if (rotationPlane.intersection(dragCtx.localRay, startProjectedPos)) {
            dragCtx.startManipulation(this)
        }
    }

    override fun onDrag(dragCtx: DragContext) {
        super.onDrag(dragCtx)
        val point = projectedPointerPos
        if (rotationPlane.intersection(dragCtx.localRay, point)) {
            val a0 = angle(startProjectedPos)
            val a1 = angle(point)
            deltaRotation = applyTick(a1 - a0, dragCtx.rotationTick).deg
            dragCtx.manipulateAxisRotation(axis.axis, deltaRotation)
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

class CamPlaneRotation : GizmoOperationBase(), GizmoRotation {
    override val rotationPlane = PlaneD()
    override val startProjectedPos = MutableVec3d()
    override var deltaRotation = 0.0.deg

    override fun onDragStart(dragCtx: DragContext) {
        super.onDragStart(dragCtx)
        dragCtx.globalToLocal.transform(dragCtx.camera.dataD.globalLookDir, 0.0, rotationPlane.n)
        rotationPlane.n.norm()
        rotationPlane.intersection(dragCtx.localRay, startProjectedPos)
        dragCtx.startManipulation(this)
    }

    override fun onDrag(dragCtx: DragContext) {
        super.onDrag(dragCtx)
        val point = MutableVec3d()
        if (rotationPlane.intersection(dragCtx.localRay, point)) {
            val a0 = angle(startProjectedPos, dragCtx)
            val a1 = angle(point, dragCtx)
            deltaRotation = applyTick(a0 - a1, dragCtx.rotationTick).deg
            dragCtx.manipulateAxisRotation(rotationPlane.n, deltaRotation)
        }
    }

    private fun angle(point: Vec3d, dragCtx: DragContext): Double {
        val cam = dragCtx.camera.dataD
        val vec = MutableVec3d()
        val x = point dot dragCtx.globalToLocal.transform(cam.globalRight, 0.0, vec)
        val y = point dot dragCtx.globalToLocal.transform(cam.globalUp, 0.0, vec)
        return atan2(y, x).toDeg()
    }
}

class FreeRotation : GizmoOperationBase() {
    private val rotation = MutableQuatD()

    override fun onDragStart(dragCtx: DragContext) {
        super.onDragStart(dragCtx)
        dragCtx.startManipulation(this)
    }

    override fun onDrag(dragCtx: DragContext) {
        super.onDrag(dragCtx)
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