package de.fabmax.kool.modules.gizmo

import de.fabmax.kool.input.InputStack
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.scene.MatrixTransformD
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.TrsTransformD
import de.fabmax.kool.scene.TrsTransformF
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor

class SimpleGizmo(name: String = "simple-gizmo") : Node(name), GizmoListener {

    val gizmoNode = GizmoNode()
    var dragSpeedModifier by gizmoNode::dragSpeedModifier
    var translationTick by gizmoNode::translationTick
    var rotationTick by gizmoNode::rotationTick
    var scaleTick by gizmoNode::scaleTick

    private val inputHandler = InputStack.InputHandler("gizmo-input-handler")

    private val clientStartTransformTrs = TrsTransformD()
    private val clientStartTransformMatrix = MatrixTransformD()
    private val clientGlobalToParent = MutableMat4d()
    private val clientTransformOffset = MutableMat4d()

    private val tmpMat4 = MutableMat4d()

    private var isInternalUpdate = false
    val translationState = mutableStateOf(Vec3d.ZERO).onChange { updateTransformFromUi(translation = it) }
    val rotationState = mutableStateOf(QuatD.IDENTITY).onChange { updateTransformFromUi(rotation = it) }
    val scaleState = mutableStateOf(Vec3d.ONES).onChange { updateTransformFromUi(scale = it) }

    var transformNode: Node? = null
        set(value) {
            if (value != field) {
                field = value
                updateGizmoFromClient()
            }
        }

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

    init {
        addNode(gizmoNode)
        gizmoNode.gizmoListeners += this
        gizmoNode.addTranslationHandles()

        inputHandler.pointerListeners += gizmoNode
        InputStack.pushTop(inputHandler)
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
        val client = transformNode ?: return

        clientGlobalToParent.set(client.parent?.invModelMatD ?: Mat4d.IDENTITY)
        clientTransformOffset.setIdentity()

        val translation = client.modelMatD.transform(MutableVec3d(), 1.0)
        val rotation = MutableQuatD(QuatD.IDENTITY)

        when (transformFrame) {
            GizmoFrame.LOCAL -> {
                client.modelMatD.decompose(rotation = rotation)
                gizmoNode.gizmoTransform.setCompositionOf(translation, rotation)
                val localScale = MutableVec3d()
                client.modelMatD.decompose(scale = localScale)
                clientTransformOffset.scale(localScale)
            }
            GizmoFrame.PARENT -> {
                client.parent?.modelMatD?.decompose(rotation = rotation)
                gizmoNode.gizmoTransform.setCompositionOf(translation, rotation)
                val localRotation = MutableQuatD()
                val localScale = MutableVec3d()
                client.transform.decompose(rotation = localRotation)
                client.modelMatD.decompose(scale = localScale)
                clientTransformOffset.rotate(localRotation).scale(localScale)
            }
            GizmoFrame.GLOBAL -> {
                gizmoNode.gizmoTransform.setCompositionOf(translation)
                val localRotation = MutableQuatD()
                val localScale = MutableVec3d()
                client.modelMatD.decompose(rotation = localRotation, scale = localScale)
                clientTransformOffset.rotate(localRotation).scale(localScale)
            }
        }
        updateUiStates(client)
    }

    override fun onGizmoUpdate(transform: TrsTransformD) {
        val client = transformNode ?: return

        val localTransform = tmpMat4.set(Mat4d.IDENTITY)
            .mul(clientGlobalToParent)
            .mul(transform.matrixD)
            .mul(clientTransformOffset)
        client.transform.setMatrix(localTransform)

        // force update of client's model matrix to make sure the updated transform is applied in this frame
        // otherwise there can be one frame lag between gizmo manipulation and node movement.
        client.updateModelMatRecursive()
        updateUiStates(client)
    }

    override fun onManipulationStart(startTransform: TrsTransformD) {
        InputStack.pushTop(inputHandler)
        val clientTransform = transformNode?.transform ?: return
        when (clientTransform) {
            is TrsTransformF -> clientStartTransformTrs.setCompositionOf(clientTransform.translation, clientTransform.rotation, clientTransform.scale)
            is TrsTransformD -> clientStartTransformTrs.setCompositionOf(clientTransform.translation, clientTransform.rotation, clientTransform.scale)
            else -> clientStartTransformMatrix.setMatrix(clientTransform.matrixD)
        }
    }

    override fun onManipulationFinished(startTransform: TrsTransformD, endTransform: TrsTransformD) {
        updateGizmoFromClient()
    }

    override fun onManipulationCanceled(startTransform: TrsTransformD) {
        val client = transformNode ?: return
        when (val clientTransform = client.transform) {
            is TrsTransformF -> clientTransform.setCompositionOf(clientStartTransformTrs.translation, clientStartTransformTrs.rotation, clientStartTransformTrs.scale)
            is TrsTransformD -> clientTransform.setCompositionOf(clientStartTransformTrs.translation, clientStartTransformTrs.rotation, clientStartTransformTrs.scale)
            else -> clientTransform.setMatrix(clientStartTransformMatrix.matrixD)
        }
        client.updateModelMatRecursive()
        updateGizmoFromClient()
    }

    private fun updateUiStates(client: Node) {
        isInternalUpdate = true

        val matrix = if (transformFrame == GizmoFrame.GLOBAL) {
            tmpMat4.set(client.modelMatD)
        } else {
            tmpMat4.set(client.transform.matrixD)
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
        val client = transformNode ?: return

        gizmoNode.startManipulation()

        val transform = gizmoNode.gizmoTransform
        transform.translation.set(translation)
        transform.rotation.set(rotation)
        transform.scale.set(scale)

        val localTransform = tmpMat4.set(Mat4d.IDENTITY)
        if (transformFrame == GizmoFrame.GLOBAL) {
            localTransform.mul(clientGlobalToParent)
        }
        localTransform.mul(transform.matrixD)

        client.transform.setMatrix(localTransform)
        client.updateModelMatRecursive()

        gizmoNode.finishManipulation()
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
            gizmoOperation = PlaneScale(Vec3d.X_AXIS),
            name = "scale-plane-x"
        )
    )
    addHandle(
        PlaneHandle(
            color = MdColor.LIGHT_GREEN,
            axis = GizmoHandle.Axis.POS_Y,
            gizmoOperation = PlaneScale(Vec3d.Y_AXIS),
            name = "scale-plane-y"
        )
    )
    addHandle(
        PlaneHandle(
            color = MdColor.BLUE,
            axis = GizmoHandle.Axis.POS_Z,
            gizmoOperation = PlaneScale(Vec3d.Z_AXIS),
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