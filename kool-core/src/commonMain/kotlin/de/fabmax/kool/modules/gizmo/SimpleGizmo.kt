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

    private val inputHandler = InputStack.InputHandler("gizmo-input-handler")

    private val clientStartTransformTrs = TrsTransformD()
    private val clientStartTransformMatrix = MatrixTransformD()
    private val clientGlobalToParent = MutableMat4d()
    private val clientTransformOffset = MutableMat4d()

    private val tmpMat3 = MutableMat3d()
    private val tmpMat4 = MutableMat4d()

    private var isInternalUpdate = false
    val translationStateX = mutableStateOf(0.0).onChange { updateTransformFromUi(tx = it) }
    val translationStateY = mutableStateOf(0.0).onChange { updateTransformFromUi(ty = it) }
    val translationStateZ = mutableStateOf(0.0).onChange { updateTransformFromUi(tz = it) }

    val rotationStateX = mutableStateOf(0.0).onChange { updateTransformFromUi(rx = it) }
    val rotationStateY = mutableStateOf(0.0).onChange { updateTransformFromUi(ry = it) }
    val rotationStateZ = mutableStateOf(0.0).onChange { updateTransformFromUi(rz = it) }

    val scaleStateX = mutableStateOf(1.0).onChange { updateTransformFromUi(sx = it) }
    val scaleStateY = mutableStateOf(1.0).onChange { updateTransformFromUi(sy = it) }
    val scaleStateZ = mutableStateOf(1.0).onChange { updateTransformFromUi(sz = it) }

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

        updateUiStates(gizmoNode.gizmoTransform)
    }

    override fun onGizmoUpdate(transform: TrsTransformD) {
        updateUiStates(transform)
        val client = transformNode ?: return

        val localTransform = tmpMat4.set(Mat4d.IDENTITY)
            .mul(clientGlobalToParent)
            .mul(transform.matrixD)
            .mul(clientTransformOffset)
        client.transform.setMatrix(localTransform)

        // force update of client's model matrix to make sure the updated transform is applied in this frame
        // otherwise there can be one frame lag between gizmo manipulation and node movement.
        client.updateModelMatRecursive()
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
        val clientTransform = client.transform
        when (clientTransform) {
            is TrsTransformF -> clientTransform.setCompositionOf(clientStartTransformTrs.translation, clientStartTransformTrs.rotation, clientStartTransformTrs.scale)
            is TrsTransformD -> clientTransform.setCompositionOf(clientStartTransformTrs.translation, clientStartTransformTrs.rotation, clientStartTransformTrs.scale)
            else -> clientTransform.setMatrix(clientStartTransformMatrix.matrixD)
        }
        client.updateModelMatRecursive()
        updateGizmoFromClient()
    }

    private fun updateUiStates(transform: TrsTransformD) {
        isInternalUpdate = true

        translationStateX.set(transform.translation.x)
        translationStateY.set(transform.translation.y)
        translationStateZ.set(transform.translation.z)

        tmpMat3.setIdentity().rotate(transform.rotation)
        val eulers = tmpMat3.getEulerAngles()
        rotationStateX.set(eulers.x)
        rotationStateY.set(eulers.y)
        rotationStateZ.set(eulers.z)

        scaleStateX.set(transform.scale.x)
        scaleStateY.set(transform.scale.y)
        scaleStateZ.set(transform.scale.z)

        isInternalUpdate = false
    }

    private fun updateTransformFromUi(
        tx: Double = translationStateX.value, ty: Double = translationStateY.value, tz: Double = translationStateZ.value,
        rx: Double = rotationStateX.value, ry: Double = rotationStateY.value, rz: Double = rotationStateZ.value,
        sx: Double = scaleStateX.value, sy: Double = scaleStateY.value, sz: Double = scaleStateZ.value,
    ) {
        if (isInternalUpdate) {
            return
        }

        gizmoNode.startManipulation(false)

        tmpMat3.setIdentity().rotate(rx.deg, ry.deg, rz.deg)
        val quat = tmpMat3.getRotation()

        val transform = gizmoNode.gizmoTransform
        transform.translation.set(tx, ty, tz)
        transform.rotation.set(quat.x, quat.y, quat.z, quat.w)
        transform.scale.set(sx, sy, sz)
        onGizmoUpdate(transform)

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
            color = Color.WHITE,
            radius = 0.2f,
            gizmoOperation = FreeRotation()
        )
    )
    addHandle(
        CenterCircleHandle(
            color = Color.WHITE,
            radius = 1f,
            hitTestMode = CenterCircleHandle.HitTestMode.LINE,
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