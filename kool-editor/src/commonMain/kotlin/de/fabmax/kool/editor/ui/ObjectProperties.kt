package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.EditorState
import de.fabmax.kool.editor.actions.EditorActions
import de.fabmax.kool.editor.actions.SetTransformAction
import de.fabmax.kool.editor.data.MeshComponentData
import de.fabmax.kool.editor.model.MNode
import de.fabmax.kool.editor.model.MScene
import de.fabmax.kool.editor.model.MSceneNode
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
            val selectedNd = EditorState.selectedNode.value as? MSceneNode
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
            val selectedNd = EditorState.selectedNode.value as? MSceneNode
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

    fun UiScope.objectProperties(selectedObject: MNode?) = Column(Grow.Std, Grow.Std, scopeName = selectedObject?.name) {
        Row(width = Grow.Std, height = sizes.lineHeightLarger) {
            modifier
                //.backgroundColor(colors.selectionBg)
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
        selectedObject ?: return@Column

        when (selectedObject) {
            is MScene -> sceneProperties(selectedObject)
            is MSceneNode -> nodeProperties(selectedObject)
        }
    }

    fun UiScope.nodeProperties(nodeModel: MSceneNode) {
        transformGizmo.setTransformObject(nodeModel)
        transformEditor(transformProperties)

        nodeModel.nodeData.components.forEach {
            when (it) {
                is MeshComponentData -> meshTypeProperties(nodeModel, it)
                else -> {}
            }
        }
    }

    fun UiScope.sceneProperties(sceneModel: MScene) {
        sceneBackground(sceneModel)

        // - lighting
        // - camera
    }

//    fun UiScope.groupProperties(nodeModel: MGroup) {
//        transformGizmo.setTransformObject(nodeModel)
//        transformEditor(transformProperties)
//    }
//
//    fun UiScope.meshProperties(nodeModel: MMesh) {
//        transformGizmo.setTransformObject(nodeModel)
//        transformEditor(transformProperties)
//        meshTypeProperties(nodeModel)
//
//        // - material
//    }
//
//    fun UiScope.modelProperties(nodeModel: MModel) {
//        transformGizmo.setTransformObject(nodeModel)
//        transformEditor(transformProperties)
//    }

    companion object {
        fun applyTransformAction(nodeModel: MSceneNode, oldTransform: Mat4d, newTransform: Mat4d) {
            val setTransform = SetTransformAction(
                editedNodeModel = nodeModel,
                oldTransform = oldTransform,
                newTransform = newTransform
            )
            EditorActions.applyAction(setTransform)
        }
    }
}