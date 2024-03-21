package de.fabmax.kool.editor

import de.fabmax.kool.KoolContext
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.input.InputStack
import de.fabmax.kool.input.PointerState
import de.fabmax.kool.math.MutableVec3d
import de.fabmax.kool.math.Vec3d
import de.fabmax.kool.math.clamp
import de.fabmax.kool.math.spatial.BoundingBoxF
import de.fabmax.kool.math.toVec3d
import de.fabmax.kool.scene.OrbitInputTransform
import de.fabmax.kool.util.Time
import kotlin.math.max

class EditorCamTransform(val editor: KoolEditor) : OrbitInputTransform("Editor cam transform") {

    private var panTarget: Vec3d? = null

    init {
        minZoom = 1.0
        maxZoom = 1000.0
        zoomAnimator.stiffness = 500.0
        horiRotAnimator.stiffness = 500.0
        vertRotAnimator.stiffness = 500.0
        setMouseRotation(20f, -30f)
        InputStack.defaultInputHandler.pointerListeners += this

        middleDragMethod = DragMethod.ROTATE

        onUpdate {
            panTarget?.let { animatePan(it) }
        }
    }

    private fun animatePan(target: Vec3d, speed: Double = 10.0) {
        val pos = transform.getTranslationD(MutableVec3d())
        val diff = MutableVec3d(target).subtract(pos)
        if (diff.length() < 0.001) {
            // target reached
            setMouseTranslation(target.x, target.y, target.z)
            panTarget = null

        } else {
            val fac = (Time.deltaT * speed).clamp()
            val dx = diff.x * fac
            val dy = diff.y * fac
            val dz = diff.z * fac
            setMouseTranslation(pos.x + dx, pos.y + dy, pos.z + dz)
        }
    }

    fun focusSelectedObject() = focusObjects(editor.selectionOverlay.getSelectedSceneNodes())

    fun focusObject(objectModel: SceneNodeModel) = focusObjects(listOf(objectModel))

    fun focusObjects(objects: List<SceneNodeModel>) {
        val bounds = BoundingBoxF()
        objects.forEach { nodeModel ->
            val c = nodeModel.drawNode.globalCenter
            val r = max(1f, nodeModel.drawNode.globalRadius)
            bounds.add(c, r)
        }

        if (bounds.isNotEmpty) {
            panTarget = bounds.center.toVec3d()
            zoom = bounds.size.length().toDouble() * 0.7
        }
    }

    override fun handlePointer(pointerState: PointerState, ctx: KoolContext) {
        if (pointerState.primaryPointer.isAnyButtonDown) {
            // stop any ongoing animated pan
            panTarget = null
        }
        super.handlePointer(pointerState, ctx)
    }
}