package de.fabmax.kool.editor.ui.componenteditors

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.actions.FusedAction
import de.fabmax.kool.editor.actions.SetComponentDataAction
import de.fabmax.kool.editor.actions.fused
import de.fabmax.kool.editor.components.TransformComponent
import de.fabmax.kool.editor.components.globalToLocalD
import de.fabmax.kool.editor.components.localToGlobalD
import de.fabmax.kool.editor.data.TransformData
import de.fabmax.kool.editor.data.Vec3Data
import de.fabmax.kool.editor.data.Vec4Data
import de.fabmax.kool.editor.ui.*
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.gizmo.GizmoFrame
import de.fabmax.kool.modules.ui2.*
import kotlin.math.abs


class TransformEditor : ComponentEditor<TransformComponent>() {

    private val transformProperties = TransformProperties()

    private val lastTranslation = MutableVec3d()
    private val lastRotation = MutableQuatD()
    private val lastScale = MutableVec3d()

    private val currentTranslation = MutableVec3d()
    private val currentRotation = MutableQuatD()
    private val currentScale = MutableVec3d()

    init {
        transformProperties.editHandlers += object : ValueEditHandler<List<TransformData>> {
            override fun onEdit(value: List<TransformData>) {
                value.forEachIndexed { i, transformData ->
                    transformData.toTransform(components[i].gameEntity.transform.transform)
                }
            }

            override fun onEditEnd(startValue: List<TransformData>, endValue: List<TransformData>) {
                val actions = buildList {
                    for (i in startValue.indices) {
                        add(setTransformAction(components[i], startValue[i], endValue[i]))
                    }
                }
                FusedAction(actions).apply()
            }
        }
    }

    private fun setTransformAction(component: TransformComponent, undoData: TransformData, applyData: TransformData) =
        SetComponentDataAction(component, component.data.copy(transform = undoData), component.data.copy(transform = applyData))

    override fun UiScope.compose() = componentPanel("Transform", Icons.small.transform) {
        position()
        rotation()
        scale()

        val transformData = components.map { it.dataState.use().transform }
        transformProperties.setTransformData(transformData, KoolEditor.instance.gizmoOverlay.transformFrame.use())

        surface.onEachFrame {
            components[0].gameEntity.localToGlobalD.decompose(currentTranslation, currentRotation, currentScale)
            if (!currentTranslation.isFuzzyEqual(lastTranslation, 1e-3) || !currentRotation.isFuzzyEqual(lastRotation, 1e-4) || !currentScale.isFuzzyEqual(lastScale, 1e-3)) {
                components.forEach { it.updateDataFromTransform() }
                lastTranslation.set(currentTranslation)
                lastRotation.set(currentRotation)
                lastScale.set(currentScale)
            }
        }
    }

    private fun ColumnScope.position() = labeledXyzRow(
        label = "Position:",
        xyz = Vec3d(transformProperties.px.use(), transformProperties.py.use(), transformProperties.pz.use()),
        dragChangeSpeed = DragChangeRates.POSITION_VEC3,
        editHandler = transformProperties.posEditHandler
    )

    private fun ColumnScope.rotation() = labeledXyzRow(
        label = "Rotation:",
        xyz = Vec3d(transformProperties.rEulerX.use(), transformProperties.rEulerY.use(), transformProperties.rEulerZ.use()),
        dragChangeSpeed = DragChangeRates.ROTATION_VEC3,
        editHandler = transformProperties.rotEulerEditHandler
    )

    private fun ColumnScope.scale() {
        menuRow {
            Text("Scale:") {
                modifier
                    .alignY(AlignmentY.Center)
                    .width(Grow.Std)
            }
            iconButton(
                icon = if (components.all { it.data.isFixedScaleRatio }) Icons.small.lock else Icons.small.lockOpen,
                tooltip = "Lock scale ratio",
                margin = Dp.ZERO
            ) {
                val toggleVal = !components.all { it.data.isFixedScaleRatio }
                components.map {
                    SetComponentDataAction(it, it.data, it.data.copy(isFixedScaleRatio = toggleVal))
                }.fused().apply()
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

        val editHandlers = mutableListOf<ValueEditHandler<List<TransformData>>>()

        private var editTransformFrame = GizmoFrame.GLOBAL
        private var editTransformData = listOf(TransformData.IDENTITY)
        private var startTransformData = listOf(TransformData.IDENTITY)
        private var startTransformDataParentFrame = listOf(TransformData.IDENTITY)

        private fun captureTransform() {
            startTransformData = fromComponentToSelectedReferenceFrame(editTransformData, editTransformFrame)
            startTransformDataParentFrame = editTransformData
        }

        val posEditHandler = object : ValueEditHandler<Vec3d> {
            override fun onEditStart(startValue: Vec3d) {
                captureTransform()
                editHandlers.forEach { it.onEditStart(startTransformData) }
            }
            override fun onEdit(value: Vec3d) {
                setPosition(value)

                val editVals = startTransformData.mapIndexed { i, transformData ->
                    val mergePos = mergeVec3(value, transformData.position.toVec3d())
                    val editData = transformData.copy(position = Vec3Data(mergePos))
                    fromSelectedReferenceFrameToComponent(i, editData)
                }
                editHandlers.forEach { it.onEdit(editVals) }
            }
            override fun onEditEnd(startValue: Vec3d, endValue: Vec3d) {
                setPosition(endValue)

                val editVals = startTransformData.mapIndexed { i, transformData ->
                    val mergePos = mergeVec3(endValue, transformData.position.toVec3d())
                    val editData = transformData.copy(position = Vec3Data(mergePos))
                    fromSelectedReferenceFrameToComponent(i, editData)
                }
                editHandlers.forEach { it.onEditEnd(startTransformDataParentFrame, editVals) }
            }
        }

        val rotEulerEditHandler = object : ValueEditHandler<Vec3d> {
            override fun onEditStart(startValue: Vec3d) {
                captureTransform()
                editHandlers.forEach { it.onEditStart(startTransformData) }
            }
            override fun onEdit(value: Vec3d) {
                setRotation(value)

                val editVals = startTransformData.mapIndexed { i, transformData ->
                val mergeRot = mergeVec3(value, transformData.rotation.toQuatD().toEulers())
                    val mat = Mat3d.rotation(mergeRot.x.deg, mergeRot.y.deg, mergeRot.z.deg)
                    val q = MutableQuatD()
                    mat.decompose(q)
                    val editData = transformData.copy(rotation = Vec4Data(q))
                    fromSelectedReferenceFrameToComponent(i, editData)
                }
                editHandlers.forEach { it.onEdit(editVals) }
            }
            override fun onEditEnd(startValue: Vec3d, endValue: Vec3d) {
                setRotation(endValue)

                val editVals = startTransformData.mapIndexed { i, transformData ->
                    val mergeRot = mergeVec3(endValue, transformData.rotation.toQuatD().toEulers())
                    val mat = Mat3d.rotation(mergeRot.x.deg, mergeRot.y.deg, mergeRot.z.deg)
                    val q = MutableQuatD()
                    mat.decompose(q)
                    val editData = transformData.copy(rotation = Vec4Data(q))
                    fromSelectedReferenceFrameToComponent(i, editData)
                }
                editHandlers.forEach { it.onEditEnd(startTransformDataParentFrame, editVals) }
            }
        }

        val scaleEditHandler = object : ValueEditHandler<Vec3d> {
            private val lastEditVal = mutableListOf<MutableVec3d>()

            override fun onEditStart(startValue: Vec3d) {
                captureTransform()
                lastEditVal.clear()
                startTransformData.forEach { lastEditVal += MutableVec3d(mergeVec3(startValue, it.scale.toVec3d())) }
                editHandlers.forEach { it.onEditStart(startTransformData) }
            }

            override fun onEdit(value: Vec3d) {
                val editVals = List(startTransformData.size) { i ->
                    val editData = computeScale(i, value)
                    setScale(editData.scale.toVec3d())
                    fromSelectedReferenceFrameToComponent(i, editData)
                }
                editHandlers.forEach { it.onEdit(editVals) }
            }

            override fun onEditEnd(startValue: Vec3d, endValue: Vec3d) {
                val editVals = List(startTransformData.size) { i ->
                    val editData = computeScale(i, endValue)
                    setScale(editData.scale.toVec3d())
                    fromSelectedReferenceFrameToComponent(i, editData)
                }
                editHandlers.forEach { it.onEditEnd(startTransformData, editVals) }
            }

            private fun computeScale(componentI: Int, scaleValue: Vec3d): TransformData {
                val mergedScale = mergeVec3(scaleValue, startTransformData[componentI].scale.toVec3d())
                val scaleVec = if (components[componentI].data.isFixedScaleRatio) {
                    val last = lastEditVal[componentI]
                    val fx = abs(mergedScale.x / last.x - 1.0)
                    val fy = abs(mergedScale.y / last.y - 1.0)
                    val fz = abs(mergedScale.z / last.z - 1.0)
                    last.set(mergedScale)
                    val s = when {
                        fx > fy && fx > fz -> mergedScale.x / startTransformData[componentI].scale.x
                        fy > fx && fy > fz -> mergedScale.y / startTransformData[componentI].scale.y
                        else -> mergedScale.z / startTransformData[componentI].scale.z
                    }
                    startTransformData[componentI].scale.toVec3d().mul(s)
                } else {
                    mergedScale
                }
                return startTransformData[componentI].copy(scale = Vec3Data(scaleVec))
            }
        }

        fun setTransformData(transformData: List<TransformData>, transformFrame: GizmoFrame) {
            editTransformFrame = transformFrame
            editTransformData = transformData

            val translatedTd = fromComponentToSelectedReferenceFrame(transformData, transformFrame)
            setPosition(condenseVec3(translatedTd.map { it.position.toVec3d() }, eps = 1e-4))
            setScale(condenseVec3(translatedTd.map { it.scale.toVec3d() }, eps = 1e-4))
            setRotation(condenseVec3(translatedTd.map { it.rotation.toQuatD().toEulers() }, eps = 1e-4))
        }

        private fun fromComponentToSelectedReferenceFrame(
            transformData: List<TransformData>,
            transformFrame: GizmoFrame
        ): List<TransformData> {
            return transformData.mapIndexed { i, td ->
                when (transformFrame) {
                    GizmoFrame.LOCAL -> {
                        // local orientation doesn't make much sense for the transform editor -> use default (parent)
                        // frame instead
                        td
                        // todo: maybe local mode would make sense to apply relative orientation changes:
                        //  in idle, pos / rot are 0.0, scale is 1.0, entering a value then changes the property by that
                        //  amount within the local orientation
                    }
                    GizmoFrame.PARENT -> {
                        // component transform data already is in parent frame -> no further transforming needed
                        td
                    }
                    GizmoFrame.GLOBAL -> {
                        val parent = components[i].gameEntity.parent
                        if (parent?.isSceneChild == true) {
                            TransformData(components[i].gameEntity.localToGlobalD)
                        } else {
                            // parent node is the scene -> parent reference frame == global reference frame
                            td
                        }
                    }
                }
            }
        }

        private fun fromSelectedReferenceFrameToComponent(componentI: Int, transformData: TransformData): TransformData {
            // reverse transform transformData into component parent frame
            return when (KoolEditor.instance.gizmoOverlay.transformFrame.value) {
                GizmoFrame.LOCAL -> {
                    transformData
                }
                GizmoFrame.PARENT -> {
                    transformData
                }
                GizmoFrame.GLOBAL -> {
                    val parent = components[componentI].gameEntity.parent
                    if (parent?.isSceneChild == true) {
                        TransformData(parent.globalToLocalD.mul(transformData.toMat4d(MutableMat4d()), MutableMat4d()))
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
            val mat = Mat3d.rotation(eulerRotation.x.deg, eulerRotation.y.deg, eulerRotation.z.deg)
            val q = MutableQuatD()
            mat.decompose(q)
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
            val mat = Mat3d.rotation(QuatD(quaternionRotation.x, quaternionRotation.y, quaternionRotation.z, quaternionRotation.w))
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

