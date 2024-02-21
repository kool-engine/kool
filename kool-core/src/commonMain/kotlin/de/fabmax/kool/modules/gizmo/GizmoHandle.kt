package de.fabmax.kool.modules.gizmo

import de.fabmax.kool.input.Pointer
import de.fabmax.kool.math.*
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.TrsTransformF

interface GizmoHandle {

    val handleTransform: TrsTransformF
    val drawNode: Node

    fun onHover(pointer: Pointer, globalRay: RayD)
    fun onHoverExit()
    fun onDrag(pointer: Pointer, dragStartPointerPos: Vec2d, globalRay: RayD)

    fun setAxis(axis: Axis) {
        handleTransform.rotation.set(axis.orientation)
        handleTransform.markDirty()
    }

    enum class Axis(val orientation: QuatD) {
        POS_X(QuatD.rotation(90.0.deg, Vec3d.X_AXIS)),
        POS_Y(MutableQuatD().rotate(90.0.deg, Vec3d.Z_AXIS).rotate(180.0.deg, Vec3d.X_AXIS)),
        POS_Z(QuatD.rotation(90.0.deg, Vec3d.NEG_Y_AXIS)),
        NEG_X(QuatD.rotation(180.0.deg, Vec3d.Z_AXIS)),
        NEG_Y(MutableQuatD().rotate(270.0.deg, Vec3d.Z_AXIS).rotate(270.0.deg, Vec3d.X_AXIS)),
        NEG_Z(MutableQuatD().rotate(90.0.deg, Vec3d.Y_AXIS).rotate(270.0.deg, Vec3d.X_AXIS))
    }
}