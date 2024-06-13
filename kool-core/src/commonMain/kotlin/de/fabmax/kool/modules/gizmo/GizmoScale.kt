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
        val scale = MutableVec3d(1.0)
        when {
            axis.axis.x != 0.0 -> scale.x = applyTick(dist / dragDistanceStart, dragCtx.scaleTick)
            axis.axis.y != 0.0 -> scale.y = applyTick(dist / dragDistanceStart, dragCtx.scaleTick)
            axis.axis.z != 0.0 -> scale.z = applyTick(dist / dragDistanceStart, dragCtx.scaleTick)
        }
        dragCtx.manipulateScale(scale)
    }
}

class PlaneScale(planeNormal: Vec3d) : GizmoOperationBase() {
    private val dragPlane = PlaneD(Vec3d.ZERO, planeNormal)
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
            val scale = MutableVec3d(s)
            scale.x = applyTick(scale.x, dragCtx.scaleTick)
            scale.y = applyTick(scale.y, dragCtx.scaleTick)
            scale.z = applyTick(scale.z, dragCtx.scaleTick)
            when {
                dragPlane.n.x != 0.0 -> scale.x = 1.0
                dragPlane.n.y != 0.0 -> scale.y = 1.0
                dragPlane.n.z != 0.0 -> scale.z = 1.0
            }
            dragCtx.manipulateScale(scale)
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