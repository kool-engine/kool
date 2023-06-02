package de.fabmax.kool.editor.ui

import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.math.Vec3d
import de.fabmax.kool.modules.ui2.*

fun UiScope.transformEditor(props: TransformProperties) = collapsapsablePanel("Transform") {
    Column(width = Grow.Std) {
        modifier
            .padding(horizontal = sizes.gap)
            .margin(bottom = sizes.smallGap)

        position(props)
        rotation(props)
        scale(props)
    }
}

private fun UiScope.position(props: TransformProperties) = xyzRow(
    label = "Position:",
    xyz = Vec3d(props.px.use(), props.py.use(), props.pz.use()),
    dragChangeSpeed = DragChangeRates.POSITION_VEC3,
    editHandler = props.posEditHandler
)

private fun UiScope.rotation(props: TransformProperties) = xyzRow(
    label = "Rotation:",
    xyz = Vec3d(props.rx.use(), props.ry.use(), props.rz.use()),
    dragChangeSpeed = DragChangeRates.ROTATION_VEC3,
    editHandler = props.rotEditHandler
)

private fun UiScope.scale(props: TransformProperties) = xyzRow(
    label = "Scale:",
    xyz = Vec3d(props.sx.use(), props.sy.use(), props.sz.use()),
    dragChangeSpeed = DragChangeRates.SCALE_VEC3,
    editHandler = props.scaleEditHandler
)

class TransformProperties {
    val px = mutableStateOf(0.0)
    val py = mutableStateOf(0.0)
    val pz = mutableStateOf(0.0)

    val rx = mutableStateOf(0.0)
    val ry = mutableStateOf(0.0)
    val rz = mutableStateOf(0.0)

    val sx = mutableStateOf(0.0)
    val sy = mutableStateOf(0.0)
    val sz = mutableStateOf(0.0)

    val editHandlers = mutableListOf<ValueEditHandler<Mat4d>>()

    private val editStartTransform = Mat4d()
    private val editTransform = Mat4d()

    private fun Mat4d.captureTransform() {
        setRotate(rx.value, ry.value, rz.value)
        scale(sx.value, sy.value, sz.value)
        setOrigin(Vec3d(px.value, py.value, pz.value))
    }

    val posEditHandler = object : ValueEditHandler<Vec3d> {
        override fun onEditStart(startValue: Vec3d) {
            editStartTransform.captureTransform()
            editHandlers.forEach { it.onEditStart(editStartTransform) }
        }
        override fun onEdit(value: Vec3d) {
            setPosition(value)
            editTransform.captureTransform()
            editHandlers.forEach { it.onEdit(editTransform) }
        }
        override fun onEditEnd(startValue: Vec3d, endValue: Vec3d) {
            setPosition(endValue)
            editTransform.captureTransform()
            editHandlers.forEach { it.onEditEnd(editStartTransform, editTransform) }
        }
    }

    val rotEditHandler = object : ValueEditHandler<Vec3d> {
        override fun onEditStart(startValue: Vec3d) {
            editStartTransform.captureTransform()
            editHandlers.forEach { it.onEditStart(editStartTransform) }
        }
        override fun onEdit(value: Vec3d) {
            setRotation(value)
            editTransform.captureTransform()
            editHandlers.forEach { it.onEdit(editTransform) }
        }
        override fun onEditEnd(startValue: Vec3d, endValue: Vec3d) {
            setRotation(endValue)
            editTransform.captureTransform()
            editHandlers.forEach { it.onEditEnd(editStartTransform, editTransform) }
        }
    }

    val scaleEditHandler = object : ValueEditHandler<Vec3d> {
        override fun onEditStart(startValue: Vec3d) {
            editStartTransform.captureTransform()
            editHandlers.forEach { it.onEditStart(editStartTransform) }
        }
        override fun onEdit(value: Vec3d) {
            setScale(value)
            editTransform.captureTransform()
            editHandlers.forEach { it.onEdit(editTransform) }
        }
        override fun onEditEnd(startValue: Vec3d, endValue: Vec3d) {
            setScale(endValue)
            editTransform.captureTransform()
            editHandlers.forEach { it.onEditEnd(editStartTransform, editTransform) }
        }
    }

    fun setPosition(position: Vec3d) = setPosition(position.x, position.y, position.z)

    fun setPosition(x: Double, y: Double, z: Double) {
        px.set(x)
        py.set(y)
        pz.set(z)
    }

    fun setRotation(position: Vec3d) = setRotation(position.x, position.y, position.z)

    fun setRotation(x: Double, y: Double, z: Double) {
        rx.set(x)
        ry.set(y)
        rz.set(z)
    }

    fun setScale(scale: Vec3d) = setScale(scale.x, scale.y, scale.z)

    fun setScale(x: Double, y: Double, z: Double) {
        sx.set(x)
        sy.set(y)
        sz.set(z)
    }
}
