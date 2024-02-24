package de.fabmax.kool.modules.gizmo

import de.fabmax.kool.input.Pointer
import de.fabmax.kool.math.*
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.TrsTransformD

interface GizmoHandle {

    val handleTransform: TrsTransformD
    val drawNode: Node

    val gizmoOperation: GizmoOperation

    fun onHover(pointer: Pointer, globalRay: RayD, gizmo: GizmoNode)
    fun onHoverExit(gizmo: GizmoNode)

    fun onDragStart(pointer: Pointer, globalRay: RayD, gizmo: GizmoNode) = gizmoOperation.onDragStart(pointer, globalRay, gizmo)
    fun onDrag(pointer: Pointer, globalRay: RayD, gizmo: GizmoNode) = gizmoOperation.onDrag(pointer, globalRay, gizmo)
    fun onDragEnd(pointer: Pointer, globalRay: RayD, gizmo: GizmoNode) = gizmoOperation.onDragEnd(pointer, globalRay, gizmo)

    fun setAxis(axis: Axis) {
        handleTransform.rotation.set(axis.orientation)
        handleTransform.markDirty()
    }

    enum class Axis(val axis: Vec3d, val orientation: QuatD) {
        POS_X(Vec3d.X_AXIS, QuatD.rotation(90.0.deg, Vec3d.X_AXIS)),
        POS_Y(Vec3d.Y_AXIS, MutableQuatD().rotate(90.0.deg, Vec3d.Z_AXIS).rotate(180.0.deg, Vec3d.X_AXIS)),
        POS_Z(Vec3d.Z_AXIS, QuatD.rotation(90.0.deg, Vec3d.NEG_Y_AXIS)),
        NEG_X(Vec3d.NEG_X_AXIS, QuatD.rotation(180.0.deg, Vec3d.Z_AXIS)),
        NEG_Y(Vec3d.NEG_Y_AXIS, MutableQuatD().rotate(270.0.deg, Vec3d.Z_AXIS).rotate(270.0.deg, Vec3d.X_AXIS)),
        NEG_Z(Vec3d.NEG_Z_AXIS, MutableQuatD().rotate(90.0.deg, Vec3d.Y_AXIS).rotate(270.0.deg, Vec3d.X_AXIS))
    }
}

