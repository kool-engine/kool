package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.EditorState
import de.fabmax.kool.editor.actions.SetTransformAction
import de.fabmax.kool.editor.components.TransformComponent
import de.fabmax.kool.editor.data.TransformData
import de.fabmax.kool.editor.data.Vec3Data
import de.fabmax.kool.editor.data.Vec4Data
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ui2.*
import kotlin.math.abs


class TransformEditor(component: TransformComponent) : ComponentEditor<TransformComponent>(component) {

    private val transformProperties = TransformProperties()

    init {
        transformProperties.editHandlers += object : ValueEditHandler<TransformData> {
            override fun onEdit(value: TransformData) {
                value.toTransform(component.nodeModel.drawNode.transform)
            }

            override fun onEditEnd(startValue: TransformData, endValue: TransformData) {
                SetTransformAction(
                    nodeModel = component.nodeModel,
                    undoTransform = startValue,
                    applyTransform = endValue
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
        transformProperties.setTransformData(transformData, EditorState.transformMode.use())
    }

    private fun UiScope.position() = labeledXyzRow(
        label = "Position:",
        xyz = Vec3d(transformProperties.px.use(), transformProperties.py.use(), transformProperties.pz.use()),
        dragChangeSpeed = DragChangeRates.POSITION_VEC3,
        editHandler = transformProperties.posEditHandler
    )

    private fun UiScope.rotation() = labeledXyzRow(
        label = "Rotation:",
        xyz = Vec3d(transformProperties.rEulerX.use(), transformProperties.rEulerY.use(), transformProperties.rEulerZ.use()),
        dragChangeSpeed = DragChangeRates.ROTATION_VEC3,
        editHandler = transformProperties.rotEulerEditHandler
    )

    private fun UiScope.scale() {
        menuRow {
            Text("Scale:") {
                modifier
                    .alignY(AlignmentY.Center)
                    .width(Grow.Std)
            }
            iconButton(
                icon = if (component.isFixedScaleRatio.use()) IconMap.small.LOCK else IconMap.small.LOCK_OPEN,
                tooltip = "Lock scale ratio",
                margin = Dp.ZERO
            ) {
                component.isFixedScaleRatio.set(!component.isFixedScaleRatio.value)
            }
        }
        xyzRow(
            xyz = Vec3d(transformProperties.sx.use(), transformProperties.sy.use(), transformProperties.sz.use()),
            dragChangeSpeed = DragChangeRates.SCALE_VEC3,
            editHandler = transformProperties.scaleEditHandler
        )
    }

    private inner class TransformProperties {
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

        private var editTransformData = TransformData(Mat4d())
        private var startTransformData = TransformData(Mat4d())
        private var startTransformDataParentFrame = TransformData(Mat4d())

        private fun captureTransform() {
            startTransformData = TransformData(
                Vec3Data(px.value, py.value, pz.value),
                Vec4Data(rQuatX.value, rQuatY.value, rQuatZ.value, rQuatW.value),
                Vec3Data(sx.value, sy.value, sz.value)
            )
            startTransformDataParentFrame = editTransformData
        }

        val posEditHandler = object : ValueEditHandler<Vec3d> {
            override fun onEditStart(startValue: Vec3d) {
                captureTransform()
                editHandlers.forEach { it.onEditStart(startTransformData) }
            }
            override fun onEdit(value: Vec3d) {
                setPosition(value)
                val editData = startTransformData.copy(position = Vec3Data(value))
                val parentFrameData = fromSelectedReferenceFrameToComponent(editData)
                editHandlers.forEach { it.onEdit(parentFrameData) }
            }
            override fun onEditEnd(startValue: Vec3d, endValue: Vec3d) {
                setPosition(endValue)
                val editData = startTransformData.copy(position = Vec3Data(endValue))
                val parentFrameData = fromSelectedReferenceFrameToComponent(editData)
                editHandlers.forEach { it.onEditEnd(startTransformDataParentFrame, parentFrameData) }
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
                val parentFrameData = fromSelectedReferenceFrameToComponent(editData)
                editHandlers.forEach { it.onEdit(parentFrameData) }
            }
            override fun onEditEnd(startValue: Vec3d, endValue: Vec3d) {
                setRotation(endValue)
                val editData = startTransformData.copy(rotation = Vec4Data(rQuatX.value, rQuatY.value, rQuatZ.value, rQuatW.value))
                val parentFrameData = fromSelectedReferenceFrameToComponent(editData)
                editHandlers.forEach { it.onEditEnd(startTransformDataParentFrame, parentFrameData) }
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
                val parentFrameData = fromSelectedReferenceFrameToComponent(editData)
                editHandlers.forEach { it.onEdit(parentFrameData) }
            }
            override fun onEditEnd(startValue: Vec4d, endValue: Vec4d) {
                setRotation(endValue)
                val editData = startTransformData.copy(rotation = Vec4Data(rQuatX.value, rQuatY.value, rQuatZ.value, rQuatW.value))
                val parentFrameData = fromSelectedReferenceFrameToComponent(editData)
                editHandlers.forEach { it.onEditEnd(startTransformDataParentFrame, parentFrameData) }
            }
        }

        val scaleEditHandler = object : ValueEditHandler<Vec3d> {
            private val lastEditVal = MutableVec3d()

            override fun onEditStart(startValue: Vec3d) {
                captureTransform()
                lastEditVal.set(startValue)
                editHandlers.forEach { it.onEditStart(startTransformData) }
            }

            override fun onEdit(value: Vec3d) {
                val editData = computeScale(value)
                setScale(editData.scale.toVec3d())
                val parentFrameData = fromSelectedReferenceFrameToComponent(editData)
                editHandlers.forEach { it.onEdit(parentFrameData) }
            }

            override fun onEditEnd(startValue: Vec3d, endValue: Vec3d) {
                val editData = computeScale(endValue)
                setScale(editData.scale.toVec3d())
                val parentFrameData = fromSelectedReferenceFrameToComponent(editData)
                editHandlers.forEach { it.onEditEnd(startTransformData, parentFrameData) }
            }

            private fun computeScale(scaleValue: Vec3d): TransformData {
                return if (component.isFixedScaleRatio.value) {
                    val fx = abs(scaleValue.x / lastEditVal.x - 1.0)
                    val fy = abs(scaleValue.y / lastEditVal.y - 1.0)
                    val fz = abs(scaleValue.y / lastEditVal.y - 1.0)
                    lastEditVal.set(scaleValue)
                    val s = when {
                        fx > fy && fx > fz -> scaleValue.x / startTransformData.scale.x
                        fy > fx && fy > fz -> scaleValue.y / startTransformData.scale.y
                        else -> scaleValue.z / startTransformData.scale.z
                    }
                    startTransformData.copy(scale = Vec3Data(startTransformData.scale.toVec3d().scale(s)))
                } else {
                    return startTransformData.copy(scale = Vec3Data(scaleValue))
                }
            }
        }

        fun setTransformData(transformData: TransformData, transformMode: EditorState.TransformOrientation) {
            editTransformData = transformData

            val translatedTd = fromComponentToSelectedReferenceFrame(transformData, transformMode)
            setPosition(translatedTd.position.toVec3d())
            setScale(translatedTd.scale.toVec3d())

            val rotMat = Mat3d().setRotate(translatedTd.rotation.toVec4d())
            setRotation(rotMat.getEulerAngles(MutableVec3d()))
        }

        private fun fromComponentToSelectedReferenceFrame(
            transformData: TransformData,
            transformMode: EditorState.TransformOrientation
        ): TransformData {
            return when (transformMode) {
                EditorState.TransformOrientation.LOCAL -> {
                    // local orientation doesn't make much sense for the transform editor -> use default (parent)
                    // frame instead
                    transformData
                    // todo: maybe local mode would make sense to apply relative orientation changes:
                    //  in idle, pos / rot are 0.0, scale is 1.0, entering a value then changes the property by that
                    //  amount within the local orientation
                }
                EditorState.TransformOrientation.PARENT -> {
                    // component transform data already is in parent frame -> no further transforming needed
                    transformData
                }
                EditorState.TransformOrientation.GLOBAL -> {
                    val parent = component.nodeModel.parent
                    if (parent is SceneNodeModel) {
                        TransformData(component.nodeModel.drawNode.modelMat)
                    } else {
                        // parent node is the scene -> parent reference frame == global reference frame
                        transformData
                    }
                }
            }
        }

        private fun fromSelectedReferenceFrameToComponent(transformData: TransformData): TransformData {
            // reverse transform transformData into component parent frame
            return when (EditorState.transformMode.value) {
                EditorState.TransformOrientation.LOCAL -> {
                    transformData
                }
                EditorState.TransformOrientation.PARENT -> {
                    transformData
                }
                EditorState.TransformOrientation.GLOBAL -> {
                    val parent = component.nodeModel.parent
                    if (parent is SceneNodeModel) {
                        val globalToParent = parent.drawNode.modelMatInverse
                        val m = globalToParent.mul(transformData.toMat4d(Mat4d()), Mat4d())
                        TransformData(m)
                    } else {
                        // parent node is the scene -> parent reference frame == global reference frame
                        transformData
                    }
                }
            }
        }

        private fun setPosition(position: Vec3d) {
            px.set(position.x)
            py.set(position.y)
            pz.set(position.z)
        }

        private fun setRotation(eulerRotation: Vec3d) {
            rEulerX.set(eulerRotation.x)
            rEulerY.set(eulerRotation.y)
            rEulerZ.set(eulerRotation.z)
            val mat = Mat3d().setRotate(eulerRotation.x, eulerRotation.y, eulerRotation.z)
            val q = mat.getRotation(MutableVec4d())
            rQuatX.set(q.x)
            rQuatY.set(q.y)
            rQuatZ.set(q.z)
            rQuatW.set(q.w)
        }

        private fun setRotation(quaternionRotation: Vec4d) {
            rQuatX.set(quaternionRotation.x)
            rQuatY.set(quaternionRotation.y)
            rQuatZ.set(quaternionRotation.z)
            rQuatW.set(quaternionRotation.w)
            val mat = Mat3d().setRotate(Vec4d(quaternionRotation.x, quaternionRotation.y, quaternionRotation.z, quaternionRotation.w))
            val e = mat.getEulerAngles(MutableVec3d())
            rEulerX.set(e.x)
            rEulerY.set(e.y)
            rEulerZ.set(e.z)
        }

        private fun setScale(scale: Vec3d) {
            sx.set(scale.x)
            sy.set(scale.y)
            sz.set(scale.z)
        }
    }
}

