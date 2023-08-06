package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.actions.SetTransformAction
import de.fabmax.kool.editor.components.TransformComponent
import de.fabmax.kool.editor.data.TransformData
import de.fabmax.kool.editor.data.Vec3Data
import de.fabmax.kool.editor.data.Vec4Data
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ui2.*


class TransformEditor(component: TransformComponent) : ComponentEditor<TransformComponent>(component) {

    private val transformProperties = TransformProperties()

    init {
        transformProperties.editHandlers += object : ValueEditHandler<TransformData> {
            override fun onEdit(value: TransformData) {
                value.toTransform(component.nodeModel.drawNode.transform)
            }

            override fun onEditEnd(startValue: TransformData, endValue: TransformData) {
                SetTransformAction(
                    editedNodeModel = component.nodeModel,
                    oldTransform = startValue,
                    newTransform = endValue
                ).apply()
            }
        }
    }

    override fun UiScope.compose() = collapsapsablePanel("Transform", IconMap.small.TRANSFORM) {
        Column(width = Grow.Std) {
            modifier
                .padding(horizontal = sizes.gap)
                .margin(bottom = sizes.gap)

            position()
            rotation()
            scale()
        }

        val transformData = component.transformState.use()
        transformProperties.setPosition(transformData.position.toVec3d())
        transformProperties.setScale(transformData.scale.toVec3d())
        val rotMat = Mat3d().setRotate(transformData.rotation.toVec4d())
        transformProperties.setRotation(rotMat.getEulerAngles(MutableVec3d()))
    }

    private fun UiScope.position() = xyzRow(
        label = "Position:",
        xyz = Vec3d(transformProperties.px.use(), transformProperties.py.use(), transformProperties.pz.use()),
        dragChangeSpeed = DragChangeRates.POSITION_VEC3,
        editHandler = transformProperties.posEditHandler
    )

    private fun UiScope.rotation() = xyzRow(
        label = "Rotation:",
        xyz = Vec3d(transformProperties.rEulerX.use(), transformProperties.rEulerY.use(), transformProperties.rEulerZ.use()),
        dragChangeSpeed = DragChangeRates.ROTATION_VEC3,
        editHandler = transformProperties.rotEulerEditHandler
    )

    private fun UiScope.scale() = xyzRow(
        label = "Scale:",
        xyz = Vec3d(transformProperties.sx.use(), transformProperties.sy.use(), transformProperties.sz.use()),
        dragChangeSpeed = DragChangeRates.SCALE_VEC3,
        editHandler = transformProperties.scaleEditHandler
    )

}

class TransformProperties {
    val px = mutableStateOf(0.0)
    val py = mutableStateOf(0.0)
    val pz = mutableStateOf(0.0)

    val rEulerX = mutableStateOf(0.0)
    val rEulerY = mutableStateOf(0.0)
    val rEulerZ = mutableStateOf(0.0)

    val rQuatX = mutableStateOf(0.0)
    val rQuatY = mutableStateOf(0.0)
    val rQuatZ = mutableStateOf(0.0)
    val rQuatW = mutableStateOf(0.0)

    val sx = mutableStateOf(0.0)
    val sy = mutableStateOf(0.0)
    val sz = mutableStateOf(0.0)

    val editHandlers = mutableListOf<ValueEditHandler<TransformData>>()

    private var startTransformData = TransformData(Mat4d())

    private fun captureTransform() {
        startTransformData = TransformData(
            Vec3Data(px.value, py.value, pz.value),
            Vec4Data(rQuatX.value, rQuatY.value, rQuatZ.value, rQuatW.value),
            Vec3Data(sx.value, sy.value, sz.value)
        )
    }

    val posEditHandler = object : ValueEditHandler<Vec3d> {
        override fun onEditStart(startValue: Vec3d) {
            captureTransform()
            editHandlers.forEach { it.onEditStart(startTransformData) }
        }
        override fun onEdit(value: Vec3d) {
            setPosition(value)
            val editData = startTransformData.copy(position = Vec3Data(value))
            editHandlers.forEach { it.onEdit(editData) }
        }
        override fun onEditEnd(startValue: Vec3d, endValue: Vec3d) {
            setPosition(endValue)
            val editData = startTransformData.copy(position = Vec3Data(endValue))
            editHandlers.forEach { it.onEditEnd(startTransformData, editData) }
        }
    }

    val rotEulerEditHandler = object : ValueEditHandler<Vec3d> {
        override fun onEditStart(startValue: Vec3d) {
            captureTransform()
            editHandlers.forEach { it.onEditStart(startTransformData) }
        }
        override fun onEdit(value: Vec3d) {
            setRotation(value)
            val editData = startTransformData.copy(rotation = Vec4Data(rQuatX.value, rQuatY.value, rQuatZ.value, rQuatW.value))
            editHandlers.forEach { it.onEdit(editData) }
        }
        override fun onEditEnd(startValue: Vec3d, endValue: Vec3d) {
            setRotation(endValue)
            val editData = startTransformData.copy(rotation = Vec4Data(rQuatX.value, rQuatY.value, rQuatZ.value, rQuatW.value))
            editHandlers.forEach { it.onEditEnd(startTransformData, editData) }
        }
    }

    val rotQuaternionEditHandler = object : ValueEditHandler<Vec4d> {
        override fun onEditStart(startValue: Vec4d) {
            captureTransform()
            editHandlers.forEach { it.onEditStart(startTransformData) }
        }
        override fun onEdit(value: Vec4d) {
            setRotation(value)
            val editData = startTransformData.copy(rotation = Vec4Data(rQuatX.value, rQuatY.value, rQuatZ.value, rQuatW.value))
            editHandlers.forEach { it.onEdit(editData) }
        }
        override fun onEditEnd(startValue: Vec4d, endValue: Vec4d) {
            setRotation(endValue)
            val editData = startTransformData.copy(rotation = Vec4Data(rQuatX.value, rQuatY.value, rQuatZ.value, rQuatW.value))
            editHandlers.forEach { it.onEditEnd(startTransformData, editData) }
        }
    }

    val scaleEditHandler = object : ValueEditHandler<Vec3d> {
        override fun onEditStart(startValue: Vec3d) {
            captureTransform()
            editHandlers.forEach { it.onEditStart(startTransformData) }
        }
        override fun onEdit(value: Vec3d) {
            setScale(value)
            val editData = startTransformData.copy(scale = Vec3Data(value))
            editHandlers.forEach { it.onEdit(editData) }
        }
        override fun onEditEnd(startValue: Vec3d, endValue: Vec3d) {
            setScale(endValue)
            val editData = startTransformData.copy(scale = Vec3Data(endValue))
            editHandlers.forEach { it.onEditEnd(startTransformData, editData) }
        }
    }

    fun setPosition(position: Vec3d) = setPosition(position.x, position.y, position.z)

    fun setPosition(x: Double, y: Double, z: Double) {
        px.set(x)
        py.set(y)
        pz.set(z)
    }

    fun setRotation(eulerRotation: Vec3d) = setRotation(eulerRotation.x, eulerRotation.y, eulerRotation.z)

    fun setRotation(x: Double, y: Double, z: Double) {
        rEulerX.set(x)
        rEulerY.set(y)
        rEulerZ.set(z)
        val mat = Mat3d().setRotate(x, y, z)
        val q = mat.getRotation(MutableVec4d())
        rQuatX.set(q.x)
        rQuatY.set(q.y)
        rQuatZ.set(q.z)
        rQuatW.set(q.w)
    }

    fun setRotation(quaternionRotation: Vec4d) =
        setRotation(quaternionRotation.x, quaternionRotation.y, quaternionRotation.z, quaternionRotation.w)

    fun setRotation(x: Double, y: Double, z: Double, w: Double) {
        rQuatX.set(x)
        rQuatY.set(y)
        rQuatZ.set(z)
        rQuatW.set(w)
        val mat = Mat3d().setRotate(Vec4d(x, y, z, w))
        val e = mat.getEulerAngles(MutableVec3d())
        rEulerX.set(e.x)
        rEulerY.set(e.y)
        rEulerZ.set(e.z)
    }

    fun setScale(scale: Vec3d) = setScale(scale.x, scale.y, scale.z)

    fun setScale(x: Double, y: Double, z: Double) {
        sx.set(x)
        sy.set(y)
        sz.set(z)
    }
}
