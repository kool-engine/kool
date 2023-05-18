package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.EditorState
import de.fabmax.kool.editor.actions.EditorActions
import de.fabmax.kool.editor.actions.SetTransformAction
import de.fabmax.kool.editor.model.*
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
            EditorState.selectedObject.value?.let { selectedNd ->
                transformProperties.getPosition(tmpNodePos)
                transformProperties.getRotation(tmpNodeRot)
                transformProperties.getScale(tmpNodeScale)

                selectedNd.creatable.getOrNull()?.let { sceneNode ->
                    val oldTransform = Mat4d().set(sceneNode.transform.matrix)
                    val newTransform = Mat4d()
                        .setRotate(tmpNodeRot.x, tmpNodeRot.y, tmpNodeRot.z)
                        .scale(tmpNodeScale)
                        .setOrigin(tmpNodePos)

                    applyTransformAction(selectedNd, oldTransform, newTransform)
                }
            }
        }
        ui.editor.editorContent += transformGizmo
    }

    override val windowSurface: UiSurface = WindowSurface(
        windowDockable,
        colors = EditorUi.EDITOR_THEME_COLORS
    ) {
        modifier.backgroundColor(colors.background.withAlpha(0.8f))

        // clear gizmo transform object, will be set below if transform editor is available
        transformGizmo.setTransformObject(null)

        Column(Grow.Std, Grow.Std) {
            TitleBar(windowDockable)
            objectProperties()
        }

        surface.onEachFrame {
            EditorState.selectedObject.value?.created?.let { selectedNd ->
                selectedNd.transform.getPosition(tmpNodePos)
                transformProperties.setPosition(tmpNodePos)
                selectedNd.transform.matrix.getRotation(tmpNodeRotMat)
                transformProperties.setRotation(tmpNodeRotMat.getEulerAngles(tmpNodeRot))
                selectedNd.transform.matrix.getScale(tmpNodeScale)
                transformProperties.setScale(tmpNodeScale)
            }
        }
    }

    fun UiScope.objectProperties() = Column(Grow.Std, Grow.Std) {
        val selectedObject = EditorState.selectedObject.use()

        Row(width = Grow.Std, height = sizes.gap * 3f) {
            modifier
                .backgroundColor(colors.selectionBg)
                .padding(horizontal = sizes.gap)

            if (selectedObject == null) {
                Text("Nothing selected") {
                    modifier.alignY(AlignmentY.Center)
                }
            } else {
                Text(selectedObject.name) {
                    modifier.alignY(AlignmentY.Center)
                }
            }
        }
        selectedObject ?: return@Column

        when (selectedObject) {
            is MScene -> sceneProperties(selectedObject)
            is MGroup -> groupProperties(selectedObject)
            is MMesh -> meshProperties(selectedObject)
            is MModel -> modelProperties(selectedObject)
        }
    }

    fun UiScope.sceneProperties(nodeModel: MScene) {
        // - clear color
        // - lighting
        // - skybox
        // - camera

        // place holder
        Text(nodeModel.name) {  }
    }

    fun UiScope.groupProperties(nodeModel: MGroup) {
        transformGizmo.setTransformObject(nodeModel)
        transformEditor(transformProperties)
    }

    fun UiScope.meshProperties(nodeModel: MMesh) {
        transformGizmo.setTransformObject(nodeModel)
        transformEditor(transformProperties)
        meshTypeProperties(nodeModel)

        // - material (simple)
    }

    fun UiScope.modelProperties(nodeModel: MModel) {
        transformGizmo.setTransformObject(nodeModel)
        transformEditor(transformProperties)
    }

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