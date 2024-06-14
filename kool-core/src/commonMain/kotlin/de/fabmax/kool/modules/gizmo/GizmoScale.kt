package de.fabmax.kool.modules.gizmo

import de.fabmax.kool.math.MutableVec3d
import de.fabmax.kool.math.PlaneD
import de.fabmax.kool.math.RayD
import de.fabmax.kool.math.Vec3d

class AxisScale(val axis: GizmoHandle.Axis) : GizmoOperationBase() {
    private val dragAxis = RayD(Vec3d.ZERO, axis.axis)
    private var dragDistanceStart = 0.0

    override fun onDragStart(dragCtx: DragContext) {
        dragDistanceStart = dragAxis.signedDistance(dragCtx.localRay) ?: return
        dragCtx.startManipulation()
    }

    override fun onDrag(dragCtx: DragContext) {
        val dist = dragAxis.signedDistance(dragCtx.localRay) ?: return
        val scale = applyTick(dist / dragDistanceStart, dragCtx.scaleTick)
        dragCtx.manipulateAxisScale(axis, scale)
    }
}

class PlaneScale(val planeNormal: GizmoHandle.Axis) : GizmoOperationBase() {
    private val dragPlane = PlaneD(Vec3d.ZERO, planeNormal.axis)
    private val dragStartPoint = MutableVec3d()

    override fun onDragStart(dragCtx: DragContext) {
        if (dragPlane.intersection(dragCtx.localRay, dragStartPoint)) {
            dragCtx.startManipulation()
        }
    }

    override fun onDrag(dragCtx: DragContext) {
        val point = MutableVec3d()
        if (dragPlane.intersection(dragCtx.localRay, point)) {
            val s = point.length() / dragStartPoint.length()
            val scale = applyTick(s, dragCtx.scaleTick)
            dragCtx.manipulatePlaneScale(planeNormal, scale)
        }
    }
}

class UniformScale : GizmoOperationBase() {
    private val dragPlane = PlaneD()
    private val dragStartPoint = MutableVec3d()

    override fun onDragStart(dragCtx: DragContext) {
        dragCtx.globalToLocal.transform(dragCtx.camera.dataD.globalLookDir, 0.0, dragPlane.n)
        dragPlane.n.norm()
        dragPlane.intersection(dragCtx.localRay, dragStartPoint)
        dragCtx.startManipulation()
    }

    override fun onDrag(dragCtx: DragContext) {
        val point = MutableVec3d()
        if (dragPlane.intersection(dragCtx.localRay, point)) {
            val s = point.length() / dragStartPoint.length()
            val tickedS = applyTick(s, dragCtx.scaleTick)
            dragCtx.manipulateScale(Vec3d(tickedS))
        }
    }
}