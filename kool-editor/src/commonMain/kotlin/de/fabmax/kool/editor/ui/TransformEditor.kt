package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.EditorState
import de.fabmax.kool.editor.actions.SetTransformAction
import de.fabmax.kool.editor.components.TransformComponent
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.math.Mat3d
import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.math.MutableVec3d
import de.fabmax.kool.math.Vec3d
import de.fabmax.kool.modules.ui2.*


class TransformEditor(component: TransformComponent) : ComponentEditor<TransformComponent>(component) {

    private val transformProperties = TransformProperties()

    private val tmpNodePos = MutableVec3d()
    private val tmpNodeRot = MutableVec3d()
    private val tmpNodeScale = MutableVec3d()
    private val tmpNodeRotMat = Mat3d()

    init {
        transformProperties.editHandlers += object : ValueEditHandler<Mat4d> {
            override fun onEdit(value: Mat4d) {
                component.nodeModel.drawNode.transform.set(value)
            }

            override fun onEditEnd(startValue: Mat4d, endValue: Mat4d) {
                SetTransformAction(
                    editedNodeModel = component.nodeModel,
                    oldTransform = startValue,
                    newTransform = endValue
                ).apply()
            }
        }
    }

    override fun UiScope.compose() = collapsapsablePanel("Transform", IconMap.small.TRANSFORM) {
        //KoolEditor.instance.gizmoOverlay.setTransformObject(nodeModel as? SceneNodeModel)

        Column(width = Grow.Std) {
            modifier
                .padding(horizontal = sizes.gap)
                .margin(bottom = sizes.gap)

            position()
            rotation()
            scale()
        }

        surface.onEachFrame {
            val selectedNd = EditorState.selection.firstOrNull() as? SceneNodeModel
            if (selectedNd != null) {
                selectedNd.drawNode.transform.getPosition(tmpNodePos)
                transformProperties.setPosition(tmpNodePos)
                selectedNd.drawNode.transform.matrix.getRotation(tmpNodeRotMat)
                transformProperties.setRotation(tmpNodeRotMat.getEulerAngles(tmpNodeRot))
                selectedNd.drawNode.transform.matrix.getScale(tmpNodeScale)
                transformProperties.setScale(tmpNodeScale)
            }
        }
    }

    private fun UiScope.position() = xyzRow(
        label = "Position:",
        xyz = Vec3d(transformProperties.px.use(), transformProperties.py.use(), transformProperties.pz.use()),
        dragChangeSpeed = DragChangeRates.POSITION_VEC3,
        editHandler = transformProperties.posEditHandler
    )

    private fun UiScope.rotation() = xyzRow(
        label = "Rotation:",
        xyz = Vec3d(transformProperties.rx.use(), transformProperties.ry.use(), transformProperties.rz.use()),
        dragChangeSpeed = DragChangeRates.ROTATION_VEC3,
        editHandler = transformProperties.rotEditHandler
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
