package de.fabmax.kool.editor

import de.fabmax.kool.KoolContext
import de.fabmax.kool.editor.actions.EditorActions
import de.fabmax.kool.editor.actions.SetTransformAction
import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.Gizmo

class NodeTransformGizmo(editor: KoolEditor) : Node("Node transform gizmo") {

    private var transformObject: Node? = null
    private var hasTransformAuthority = false

    private val gizmo = Gizmo()

    private val gizmoListener = object : Gizmo.GizmoListener {
        private val startTransform = Mat4d()

        override fun onDragAxis(axis: Vec3f, distance: Float, targetTransform: Mat4d, ctx: KoolContext) {
            targetTransform.translate(axis.x * distance, axis.y * distance, axis.z * distance)
        }

        override fun onDragPlane(planeNormal: Vec3f, dragPosition: Vec3f, targetTransform: Mat4d, ctx: KoolContext) {
            targetTransform.translate(dragPosition)
        }

        override fun onDragRotate(rotationAxis: Vec3f, angle: Float, targetTransform: Mat4d, ctx: KoolContext) {
            targetTransform.rotate(angle, rotationAxis)
        }

        override fun onDragStart(ctx: KoolContext) {
            transformObject?.let {
                startTransform.set(it.transform.matrix)
                hasTransformAuthority = true
            }
        }

        override fun onDragFinished(ctx: KoolContext) {
            hasTransformAuthority = false
            transformObject?.let {
                EditorActions.applyAction(SetTransformAction(it, startTransform, it.transform.matrix))
            }
        }
    }

    init {
        gizmo.isVisible = false
        addNode(gizmo)

        gizmo.gizmoListener = gizmoListener
        editor.editorInputContext.pointerListeners += gizmo

        gizmo.onUpdate {
            transformObject?.let {
                if (hasTransformAuthority) {
                    gizmo.getGizmoTransform(it)
                    it.updateModelMat(true)
                } else {
                    gizmo.transform.set(it.transform.matrix)
                }
            }
        }
    }

    fun setTransformObject(node: Node?) {
        transformObject = node
        gizmo.isVisible = node != null
        node?.let {
            gizmo.setFixedScale(it.globalRadius * 1.5f)
        }
    }
}