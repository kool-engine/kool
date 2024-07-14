package de.fabmax.kool.modules.gizmo

import de.fabmax.kool.input.InputStack
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import kotlin.properties.Delegates

class SimpleGizmo(
    name: String = "simple-gizmo",
    addOverlays: Boolean = true,
    val hideHandlesOnDrag: Boolean = true
) : Node(name), GizmoListener {

    val gizmoNode = GizmoNode()
    val dragSpeedModifier by gizmoNode::dragSpeedModifier
    val translationTick by gizmoNode::translationTick
    val rotationTick by gizmoNode::rotationTick
    val scaleTick by gizmoNode::scaleTick

    private val inputHandler = InputStack.InputHandler("gizmo-input-handler")

    private val clientStartTransformTrs = TrsTransformD()
    private val clientStartTransformMatrix = MatrixTransformD()
    private val clientGlobalToParent = MutableMat4d()
    private val clientTransformOffset = MutableMat4d()

    private val tmpMat4 = MutableMat4d()

    private var isInternalUpdate = false
    val translationState = mutableStateOf(Vec3d.ZERO).onChange { _, new -> updateTransformFromUi(translation = new) }
    val rotationState = mutableStateOf(QuatD.IDENTITY).onChange { _, new -> updateTransformFromUi(rotation = new) }
    val scaleState = mutableStateOf(Vec3d.ONES).onChange { _, new -> updateTransformFromUi(scale = new) }

    var transformClient: GizmoClient? by Delegates.observable(null) { _, _, _ -> updateGizmoFromClient() }

    var mode = GizmoMode.TRANSLATE
        set(value) {
            if (value != field) {
                field = value
                when (value) {
                    GizmoMode.TRANSLATE -> setupTranslationGizmo()
                    GizmoMode.ROTATE -> setupRotationGizmo()
                    GizmoMode.SCALE -> setupScaleGizmo()
                }
            }
        }

    var transformFrame = GizmoFrame.GLOBAL
        set(value) {
            field = value
            updateGizmoFromClient()
        }

    val translationOverlay = TranslationOverlay(gizmoNode)
    val rotationOverlay = RotationOverlay(gizmoNode)
    val scaleOverlay = ScaleOverlay(gizmoNode)

    init {
        addNode(gizmoNode)
        gizmoNode.gizmoListeners += this
        gizmoNode.addTranslationHandles()

        inputHandler.pointerListeners += gizmoNode
        InputStack.pushTop(inputHandler)

        if (addOverlays) {
            addNode(translationOverlay)
            addNode(rotationOverlay)
            addNode(scaleOverlay)
            gizmoNode.gizmoListeners += translationOverlay
            gizmoNode.gizmoListeners += rotationOverlay
            gizmoNode.gizmoListeners += scaleOverlay
        }
    }

    fun setTransformNode(node: Node) {
        transformClient = GizmoClientNode(node)
    }

    override fun release() {
        super.release()
        InputStack.remove(inputHandler)
    }

    private fun setupTranslationGizmo() {
        gizmoNode.clearHandles()
        gizmoNode.addTranslationHandles()
    }

    private fun setupRotationGizmo() {
        gizmoNode.clearHandles()
        gizmoNode.addRotationHandles()
    }

    private fun setupScaleGizmo() {
        gizmoNode.clearHandles()
        gizmoNode.addScaleHandles()
    }

    fun updateGizmoFromClient() {
        val client = transformClient ?: return
        val translation = client.localToGlobal.transform(MutableVec3d(), 1.0)
        val rotation = MutableQuatD(QuatD.IDENTITY)

        clientGlobalToParent.set(client.globalToParent)
        clientTransformOffset.setIdentity()

        when (transformFrame) {
            GizmoFrame.LOCAL -> {
                val localScale = MutableVec3d()
                client.localToGlobal.decompose(rotation = rotation, scale = localScale)
                gizmoNode.gizmoTransform.setCompositionOf(translation, rotation)
                clientTransformOffset.scale(localScale)
            }
            GizmoFrame.PARENT -> {
                client.parentToGlobal.decompose(rotation = rotation)
                gizmoNode.gizmoTransform.setCompositionOf(translation, rotation)
                val localRotation = MutableQuatD()
                val localScale = MutableVec3d()
                client.clientTransform.decompose(rotation = localRotation)
                client.localToGlobal.decompose(scale = localScale)
                clientTransformOffset.rotate(localRotation).scale(localScale)
            }
            GizmoFrame.GLOBAL -> {
                gizmoNode.gizmoTransform.setCompositionOf(translation)
                val localRotation = MutableQuatD()
                val localScale = MutableVec3d()
                client.localToGlobal.decompose(rotation = localRotation, scale = localScale)
                clientTransformOffset.rotate(localRotation).scale(localScale)
            }
        }
        updateUiStates(client)
    }

    override fun onGizmoUpdate(transform: TrsTransformD) {
        val client = transformClient ?: return

        val localTransform = tmpMat4.set(Mat4d.IDENTITY)
            .mul(clientGlobalToParent)
            .mul(transform.matrixD)
            .mul(clientTransformOffset)
        client.setTransformMatrix(localTransform)
        updateUiStates(client)
    }

    override fun onManipulationStart(startTransform: TrsTransformD) {
        InputStack.pushTop(inputHandler)
        val clientTransform = transformClient?.clientTransform ?: return
        when (clientTransform) {
            is TrsTransformF -> clientStartTransformTrs.setCompositionOf(clientTransform.translation, clientTransform.rotation, clientTransform.scale)
            is TrsTransformD -> clientStartTransformTrs.setCompositionOf(clientTransform.translation, clientTransform.rotation, clientTransform.scale)
            else -> clientStartTransformMatrix.setMatrix(clientTransform.matrixD)
        }

        if (hideHandlesOnDrag) {
            gizmoNode.handles.forEach { it.isHidden = true }
        }
    }

    override fun onManipulationFinished(startTransform: TrsTransformD, endTransform: TrsTransformD) {
        updateGizmoFromClient()

        if (hideHandlesOnDrag) {
            gizmoNode.handles.forEach { it.isHidden = false }
        }
    }

    override fun onManipulationCanceled(startTransform: TrsTransformD) {
        val client = transformClient ?: return
        when (val clientTransform = client.clientTransform) {
            is TrsTransformF -> clientTransform.setCompositionOf(clientStartTransformTrs.translation, clientStartTransformTrs.rotation, clientStartTransformTrs.scale)
            is TrsTransformD -> clientTransform.setCompositionOf(clientStartTransformTrs.translation, clientStartTransformTrs.rotation, clientStartTransformTrs.scale)
            else -> clientTransform.setMatrix(clientStartTransformMatrix.matrixD)
        }
        client.updateMatrices()
        updateGizmoFromClient()

        if (hideHandlesOnDrag) {
            gizmoNode.handles.forEach { it.isHidden = false }
        }
    }

    private fun updateUiStates(client: GizmoClient) {
        isInternalUpdate = true

        val matrix = if (transformFrame == GizmoFrame.GLOBAL) {
            tmpMat4.set(client.localToGlobal)
        } else {
            tmpMat4.set(client.clientTransform.matrixD)
        }

        val translation = MutableVec3d()
        val rotation = MutableQuatD()
        val scale = MutableVec3d()
        matrix.decompose(translation, rotation, scale)
        translationState.set(translation)
        rotationState.set(rotation)
        scaleState.set(scale)

        isInternalUpdate = false
    }

    private fun updateTransformFromUi(
        translation: Vec3d = translationState.value,
        rotation: QuatD = rotationState.value,
        scale: Vec3d = scaleState.value,
    ) {
        if (isInternalUpdate) {
            return
        }
        val client = transformClient ?: return

        gizmoNode.startManipulation(null)

        val transform = gizmoNode.gizmoTransform
        transform.translation.set(translation)
        transform.rotation.set(rotation)
        transform.scale.set(scale)

        val localTransform = tmpMat4.set(Mat4d.IDENTITY)
        if (transformFrame == GizmoFrame.GLOBAL) {
            localTransform.mul(clientGlobalToParent)
        }
        localTransform.mul(transform.matrixD)
        client.setTransformMatrix(localTransform)

        gizmoNode.finishManipulation()
    }
}

interface GizmoClient {
    val clientTransform: Transform
    val localToGlobal: Mat4d
    val parentToGlobal: Mat4d
    val globalToParent: Mat4d

    fun updateMatrices()

    fun setTransformMatrix(matrix: Mat4d) {
        clientTransform.setMatrix(matrix)
        updateMatrices()
    }
}

class GizmoClientNode(val node: Node) : GizmoClient {
    override val clientTransform: Transform get() = node.transform
    override val localToGlobal: Mat4d get() = node.modelMatD
    override val parentToGlobal: Mat4d get() = node.parent?.modelMatD ?: Mat4d.IDENTITY
    override val globalToParent: Mat4d get() = node.parent?.invModelMatD ?: Mat4d.IDENTITY

    override fun updateMatrices() {
        node.updateModelMatRecursive()
    }
}

enum class GizmoFrame {
    GLOBAL,
    LOCAL,
    PARENT
}

enum class GizmoMode {
    TRANSLATE,
    ROTATE,
    SCALE
}

fun GizmoNode.addTranslationHandles() {
    addHandle(
        AxisHandle(
            color = MdColor.RED,
            axis = GizmoHandle.Axis.POS_X,
            handleShape = AxisHandle.HandleType.ARROW,
            name = "axis-POS_X"
        )
    )
    addHandle(
        AxisHandle(
            color = MdColor.LIGHT_GREEN,
            axis = GizmoHandle.Axis.POS_Y,
            handleShape = AxisHandle.HandleType.ARROW,
            name = "axis-POS_Y"
        )
    )
    addHandle(
        AxisHandle(
            color = MdColor.BLUE,
            axis = GizmoHandle.Axis.POS_Z,
            handleShape = AxisHandle.HandleType.ARROW,
            name = "axis-POS_Z"
        )
    )

    addHandle(
        PlaneHandle(
            color = MdColor.RED,
            axis = GizmoHandle.Axis.POS_X,
            name = "plane-POS_X"
        )
    )
    addHandle(
        PlaneHandle(
            color = MdColor.LIGHT_GREEN,
            axis = GizmoHandle.Axis.POS_Y,
            name = "plane-POS_X"
        )
    )
    addHandle(
        PlaneHandle(
            color = MdColor.BLUE,
            axis = GizmoHandle.Axis.POS_Z,
            name = "plane-POS_X"
        )
    )

    addHandle(
        CenterCircleHandle(
            color = Color.WHITE,
            radius = 0.2f
        )
    )
}

fun GizmoNode.addRotationHandles() {
    addHandle(
        AxisRotationHandle(
            color = MdColor.RED,
            axis = GizmoHandle.Axis.POS_X
        )
    )
    addHandle(
        AxisRotationHandle(
            color = MdColor.LIGHT_GREEN,
            axis = GizmoHandle.Axis.POS_Y
        )
    )
    addHandle(
        AxisRotationHandle(
            color = MdColor.BLUE,
            axis = GizmoHandle.Axis.POS_Z
        )
    )

    addHandle(
        CenterCircleHandle(
            color = Color.WHITE.withAlpha(0f),
            colorIdle = Color.WHITE.withAlpha(0f),
            coveredColor = Color.WHITE.withAlpha(0.2f),
            coveredColorIdle = Color.WHITE.withAlpha(0f),
            radius = 0.75f,
            drawMode = CenterCircleHandle.CircleMode.SOLID,
            gizmoOperation = FreeRotation()
        )
    )
    addHandle(
        CenterCircleHandle(
            color = Color.WHITE,
            radius = 1f,
            hitTestMode = CenterCircleHandle.CircleMode.LINE,
            gizmoOperation = CamPlaneRotation()
        )
    )
}

fun GizmoNode.addScaleHandles() {
    addHandle(
        AxisHandle(
            color = MdColor.RED,
            axis = GizmoHandle.Axis.POS_X,
            handleShape = AxisHandle.HandleType.SPHERE,
            gizmoOperation = AxisScale(GizmoHandle.Axis.POS_X),
            name = "scale-axis-x"
        )
    )
    addHandle(
        AxisHandle(
            color = MdColor.LIGHT_GREEN,
            axis = GizmoHandle.Axis.POS_Y,
            handleShape = AxisHandle.HandleType.SPHERE,
            gizmoOperation = AxisScale(GizmoHandle.Axis.POS_Y),
            name = "scale-axis-y"
        )
    )
    addHandle(
        AxisHandle(
            color = MdColor.BLUE,
            axis = GizmoHandle.Axis.POS_Z,
            handleShape = AxisHandle.HandleType.SPHERE,
            gizmoOperation = AxisScale(GizmoHandle.Axis.POS_Z),
            name = "scale-axis-z"
        )
    )

    addHandle(
        PlaneHandle(
            color = MdColor.RED,
            axis = GizmoHandle.Axis.POS_X,
            gizmoOperation = PlaneScale(GizmoHandle.Axis.POS_X),
            name = "scale-plane-x"
        )
    )
    addHandle(
        PlaneHandle(
            color = MdColor.LIGHT_GREEN,
            axis = GizmoHandle.Axis.POS_Y,
            gizmoOperation = PlaneScale(GizmoHandle.Axis.POS_Y),
            name = "scale-plane-y"
        )
    )
    addHandle(
        PlaneHandle(
            color = MdColor.BLUE,
            axis = GizmoHandle.Axis.POS_Z,
            gizmoOperation = PlaneScale(GizmoHandle.Axis.POS_Z),
            name = "scale-plane-z"
        )
    )

    addHandle(
        CenterCircleHandle(
            color = Color.WHITE,
            radius = 1f,
            innerRadius = 0.2f,
            gizmoOperation = UniformScale(),
            name = "scale-uniform"
        )
    )
}