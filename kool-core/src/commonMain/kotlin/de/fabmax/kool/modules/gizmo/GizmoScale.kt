package de.fabmax.kool.modules.gizmo

import de.fabmax.kool.math.MutableVec3d
import de.fabmax.kool.math.PlaneD
import de.fabmax.kool.math.RayD
import de.fabmax.kool.math.Vec3d

interface GizmoScale {
    val scaleValue: Double
}

class AxisScale(val axis: GizmoHandle.Axis) : GizmoOperationBase(), GizmoScale {
    private val dragAxis = RayD(Vec3d.ZERO, axis.axis)
    private var dragDistanceStart = 0.0

    override var scaleValue: Double = 1.0
        private set

    override fun onDragStart(dragCtx: DragContext) {
        super.onDragStart(dragCtx)
        scaleValue = 1.0
        dragDistanceStart = dragAxis.signedDistance(dragCtx.localRay) ?: return
        dragCtx.startManipulation(this)
    }

    override fun onDrag(dragCtx: DragContext) {
        super.onDrag(dragCtx)
        val dist = dragAxis.signedDistance(dragCtx.localRay) ?: return
        projectedPointerPos.set(axis.axis).mul(dist).add(dragAxis.origin)
        scaleValue = applyTick(dist / dragDistanceStart, dragCtx.scaleTick)
        dragCtx.manipulateAxisScale(axis, scaleValue)
        dragCtx.localToGlobal.transform(projectedPointerPos)
    }
}

class PlaneScale(val planeNormal: GizmoHandle.Axis) : GizmoOperationBase(), GizmoScale {
    private val dragPlane = PlaneD(Vec3d.ZERO, planeNormal.axis)
    private val dragStartPoint = MutableVec3d()

    override var scaleValue: Double = 1.0
        private set

    override fun onDragStart(dragCtx: DragContext) {
        super.onDragStart(dragCtx)
        scaleValue = 1.0
        if (dragPlane.intersection(dragCtx.localRay, dragStartPoint)) {
            dragCtx.startManipulation(this)
        }
    }

    override fun onDrag(dragCtx: DragContext) {
        super.onDrag(dragCtx)
        if (dragPlane.intersection(dragCtx.localRay, projectedPointerPos)) {
            val s = projectedPointerPos.length() / dragStartPoint.length()
            scaleValue = applyTick(s, dragCtx.scaleTick)
            dragCtx.manipulatePlaneScale(planeNormal, scaleValue)
            dragCtx.localToGlobal.transform(projectedPointerPos)
        }
    }
}

class UniformScale : GizmoOperationBase(), GizmoScale {
    private val dragPlane = PlaneD()
    private val dragStartPoint = MutableVec3d()

    override var scaleValue: Double = 1.0
        private set

    override fun onDragStart(dragCtx: DragContext) {
        super.onDragStart(dragCtx)
        scaleValue = 1.0
        dragCtx.globalToLocal.transform(dragCtx.camera.dataD.globalLookDir, 0.0, dragPlane.n)
        dragPlane.n.norm()
        dragPlane.intersection(dragCtx.localRay, dragStartPoint)
        dragCtx.startManipulation(this)
    }

    override fun onDrag(dragCtx: DragContext) {
        super.onDrag(dragCtx)
        if (dragPlane.intersection(dragCtx.localRay, projectedPointerPos)) {
            val s = projectedPointerPos.length() / dragStartPoint.length()
            scaleValue = applyTick(s, dragCtx.scaleTick)
            dragCtx.manipulateScale(Vec3d(scaleValue))
            dragCtx.localToGlobal.transform(projectedPointerPos)
        }
    }
}