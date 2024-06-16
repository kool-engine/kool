package de.fabmax.kool.modules.gizmo

import de.fabmax.kool.math.MutableVec3d
import de.fabmax.kool.math.PlaneD
import de.fabmax.kool.math.RayD
import de.fabmax.kool.math.Vec3d

interface GizmoTranslation {
    val translationDistance: Double
}

class AxisTranslation(val axis: GizmoHandle.Axis) : GizmoOperationBase(), GizmoTranslation {
    private val dragAxis = RayD(Vec3d.ZERO, axis.axis)
    private var dragDistanceOffset = 0.0

    override var translationDistance: Double = 0.0
        private set

    override fun onDragStart(dragCtx: DragContext) {
        super.onDragStart(dragCtx)
        translationDistance = 0.0
        dragDistanceOffset = dragAxis.signedDistance(dragCtx.localRay) ?: return
        dragCtx.startManipulation(this)
    }

    override fun onDrag(dragCtx: DragContext) {
        super.onDrag(dragCtx)
        val dist = dragAxis.signedDistance(dragCtx.localRay) ?: return
        projectedPointerPos.set(axis.axis).mul(dist).add(dragAxis.origin)
        translationDistance = applyTick(dist - dragDistanceOffset, dragCtx.translationTick)
        dragCtx.manipulateAxisTranslation(axis, translationDistance)
        dragCtx.localToGlobal.transform(projectedPointerPos)
    }
}

class PlaneTranslation(planeNormal: Vec3d) : GizmoOperationBase(), GizmoTranslation {
    private val dragPlane = PlaneD(Vec3d.ZERO, planeNormal)
    private val dragPointerOffset = MutableVec3d()

    override var translationDistance: Double = 1.0
        private set

    override fun onDragStart(dragCtx: DragContext) {
        super.onDragStart(dragCtx)
        translationDistance = 0.0
        if (dragPlane.intersection(dragCtx.localRay, dragPointerOffset)) {
            dragCtx.startManipulation(this)
        }
    }

    override fun onDrag(dragCtx: DragContext) {
        super.onDrag(dragCtx)
        val point = projectedPointerPos
        if (dragPlane.intersection(dragCtx.localRay, point)) {
            point -= dragPlane.p + dragPointerOffset
            point.x = applyTick(point.x, dragCtx.translationTick)
            point.y = applyTick(point.y, dragCtx.translationTick)
            point.z = applyTick(point.z, dragCtx.translationTick)
            translationDistance = point.length()
            dragCtx.manipulateTranslation(point)
            dragCtx.localToGlobal.transform(point)
        }
    }
}

class CamPlaneTranslation : GizmoOperationBase(), GizmoTranslation {
    private val dragPlane = PlaneD()
    private val dragPointerOffset = MutableVec3d()

    override var translationDistance: Double = 1.0
        private set

    override fun onDragStart(dragCtx: DragContext) {
        super.onDragStart(dragCtx)
        translationDistance = 0.0
        dragCtx.globalToLocal.transform(dragCtx.camera.dataD.globalLookDir, 0.0, dragPlane.n)
        dragPlane.n.norm()
        dragPlane.intersection(dragCtx.localRay, dragPointerOffset)
        dragCtx.startManipulation(this)
    }

    override fun onDrag(dragCtx: DragContext) {
        super.onDrag(dragCtx)
        val point = projectedPointerPos
        if (dragPlane.intersection(dragCtx.localRay, point)) {
            point -= dragPlane.p + dragPointerOffset
            point.x = applyTick(point.x, dragCtx.translationTick)
            point.y = applyTick(point.y, dragCtx.translationTick)
            point.z = applyTick(point.z, dragCtx.translationTick)
            translationDistance = point.length()
            dragCtx.manipulateTranslation(point)
            dragCtx.localToGlobal.transform(point)
        }
    }
}