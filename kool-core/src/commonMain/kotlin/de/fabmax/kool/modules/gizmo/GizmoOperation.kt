package de.fabmax.kool.modules.gizmo

import de.fabmax.kool.input.Pointer
import de.fabmax.kool.math.MutableVec3d
import de.fabmax.kool.math.RayD
import de.fabmax.kool.math.Vec3d
import kotlin.math.sign

interface GizmoOperation {
    fun onDragStart(pointer: Pointer, globalRay: RayD, gizmo: GizmoNode)
    fun onDrag(pointer: Pointer, globalRay: RayD, gizmo: GizmoNode)
    fun onDragEnd(pointer: Pointer, globalRay: RayD, gizmo: GizmoNode)
}

class AxisTranslation(val axis: GizmoHandle.Axis) : GizmoOperation {

    private val dragAxis = RayD()
    private var dragDistanceOffset = 0.0

    override fun onDragStart(pointer: Pointer, globalRay: RayD, gizmo: GizmoNode) {
        dragAxis.origin.set(Vec3d.ZERO)
        dragAxis.direction.set(axis.axis)
        dragAxis.transformBy(gizmo.modelMatD)

        // shift drag axis origin to current (start-) pointer position
        val point = dragAxis.closestPointOnRay(globalRay, MutableVec3d())
        dragAxis.origin.set(point)

        gizmo.startManipulation()
    }

    override fun onDrag(pointer: Pointer, globalRay: RayD, gizmo: GizmoNode) {
        val point = dragAxis.closestPointOnRay(globalRay, MutableVec3d())
        val direction = sign((point - dragAxis.origin) dot dragAxis.direction)
        val distance = point.distance(dragAxis.origin) * direction
        gizmo.manipulateAxisTranslation(axis, distance)
    }

    override fun onDragEnd(pointer: Pointer, globalRay: RayD, gizmo: GizmoNode) {
        gizmo.finishManipulation()
    }
}
