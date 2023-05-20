package de.fabmax.kool.editor.ui

import de.fabmax.kool.KoolContext
import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.Gizmo

class NodeTransformGizmo(editor: KoolEditor) : Node("Node transform gizmo") {

    private var transformNodeModel: SceneNodeModel? = null
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
            transformNodeModel?.let {
                val transformNode = it.node
                startTransform.set(transformNode.transform.matrix)
                hasTransformAuthority = true
            }
        }

        override fun onDragFinished(ctx: KoolContext) {
            hasTransformAuthority = false
            transformNodeModel?.let {
                val transformNode = it.node
                ObjectProperties.applyTransformAction(it, startTransform, transformNode.transform.matrix)
            }
        }
    }

    init {
        gizmo.isVisible = false
        addNode(gizmo)

        gizmo.gizmoListener = gizmoListener
        editor.editorInputContext.pointerListeners += gizmo

        gizmo.onUpdate {
            transformNodeModel?.node?.let {
                if (hasTransformAuthority) {
                    val gizmoTransform = Mat4d()
                    val tmp = Mat4d()
                    gizmo.getGizmoTransform(gizmoTransform)
                    it.parent?.transform?.matrixInverse?.let { toLocal ->
                        toLocal.mul(gizmoTransform, tmp)
                        gizmoTransform.set(tmp)
                    }
                    it.transform.set(gizmoTransform)
                    it.updateModelMat(true)
                } else {
                    gizmo.transform.set(it.modelMat)
                    gizmo.transform.matrix.resetScale()
                    gizmo.setFixedScale(it.globalRadius + 0.5f)
                }
            }
        }
    }

    fun setTransformObject(nodeModel: SceneNodeModel?) {
        transformNodeModel = nodeModel
        gizmo.isVisible = nodeModel != null
        nodeModel?.node?.let {
            gizmo.setFixedScale(it.globalRadius + 0.5f)
        }
    }
}