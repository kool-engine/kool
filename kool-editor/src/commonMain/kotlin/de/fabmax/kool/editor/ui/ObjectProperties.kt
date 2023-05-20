package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.EditorState
import de.fabmax.kool.editor.actions.EditorActions
import de.fabmax.kool.editor.actions.SetTransformAction
import de.fabmax.kool.editor.model.EditorNodeModel
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.editor.model.ecs.MeshComponent
import de.fabmax.kool.editor.model.ecs.SceneBackgroundComponent
import de.fabmax.kool.editor.model.ecs.TransformComponent
import de.fabmax.kool.math.Mat3d
import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.math.MutableVec3d
import de.fabmax.kool.modules.ui2.*

class ObjectProperties(ui: EditorUi) : EditorPanel("Object Properties", ui) {

    private val transformProperties = TransformProperties()

    private val tmpNodePos = MutableVec3d()
    private val tmpNodeRot = MutableVec3d()
    private val tmpNodeScale = MutableVec3d()
    private val tmpNodeRotMat = Mat3d()

    private val transformGizmo = NodeTransformGizmo(ui.editor)

    init {
        transformProperties.onChangedByEditor += {
            val selectedNd = EditorState.selectedNode.value as? SceneNodeModel
            if (selectedNd != null) {
                transformProperties.getPosition(tmpNodePos)
                transformProperties.getRotation(tmpNodeRot)
                transformProperties.getScale(tmpNodeScale)

                val sceneNode = selectedNd.node
                val oldTransform = Mat4d().set(sceneNode.transform.matrix)
                val newTransform = Mat4d()
                    .setRotate(tmpNodeRot.x, tmpNodeRot.y, tmpNodeRot.z)
                    .scale(tmpNodeScale)
                    .setOrigin(tmpNodePos)

                applyTransformAction(selectedNd, oldTransform, newTransform)
            }
        }
        ui.editor.editorContent += transformGizmo
    }

    override val windowSurface: UiSurface = EditorPanelWindow {
        // clear gizmo transform object, will be set below if transform editor is available
        transformGizmo.setTransformObject(null)

        Column(Grow.Std, Grow.Std) {
            EditorTitleBar(windowDockable)
            objectProperties(EditorState.selectedNode.use())
        }

        surface.onEachFrame {
            val selectedNd = EditorState.selectedNode.value as? SceneNodeModel
            if (selectedNd != null) {
                selectedNd.node.transform.getPosition(tmpNodePos)
                transformProperties.setPosition(tmpNodePos)
                selectedNd.node.transform.matrix.getRotation(tmpNodeRotMat)
                transformProperties.setRotation(tmpNodeRotMat.getEulerAngles(tmpNodeRot))
                selectedNd.node.transform.matrix.getScale(tmpNodeScale)
                transformProperties.setScale(tmpNodeScale)
            }
        }
    }

    fun UiScope.objectProperties(selectedObject: EditorNodeModel?) = Column(Grow.Std, Grow.Std, scopeName = selectedObject?.name) {
        Row(width = Grow.Std, height = sizes.lineHeightLarger) {
            modifier
                .padding(horizontal = sizes.gap)

            if (selectedObject == null) {
                Text("Nothing selected") {
                    modifier
                        .width(Grow.Std)
                        .textAlignX(AlignmentX.Center)
                        .alignY(AlignmentY.Bottom)
                        .font(sizes.italicText)
                }
            } else {
                Text(selectedObject.name) {
                    modifier
                        .alignY(AlignmentY.Center)
                        .font(sizes.boldText)
                }
            }
        }

        if (selectedObject == null) {
            return@Column
        }

        for (component in selectedObject.components.use()) {
            when (component) {
                is MeshComponent -> meshComponent(selectedObject, component)
                is SceneBackgroundComponent -> sceneBackgroundComponent(selectedObject, component)
                is TransformComponent -> transformComponent(selectedObject)
            }
        }
    }

    fun UiScope.meshComponent(nodeModel: EditorNodeModel, meshComponent: MeshComponent) {
        (nodeModel as? SceneNodeModel)?.let {
            meshTypeProperties(nodeModel, meshComponent)
        }
    }

    fun UiScope.sceneBackgroundComponent(nodeModel: EditorNodeModel, sceneBackgroundComponent: SceneBackgroundComponent) {
        (nodeModel as? SceneModel)?.let {
            sceneBackground(it, sceneBackgroundComponent)
        }
    }

    fun UiScope.transformComponent(nodeModel: EditorNodeModel) {
        (nodeModel as? SceneNodeModel)?.let {
            transformGizmo.setTransformObject(it)
            transformEditor(transformProperties)
        }
    }

    companion object {
        fun applyTransformAction(nodeModel: SceneNodeModel, oldTransform: Mat4d, newTransform: Mat4d) {
            val setTransform = SetTransformAction(
                editedNodeModel = nodeModel,
                oldTransform = oldTransform,
                newTransform = newTransform
            )
            EditorActions.applyAction(setTransform)
        }
    }
}