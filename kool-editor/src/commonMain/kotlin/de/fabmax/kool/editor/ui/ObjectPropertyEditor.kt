package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.EditorState
import de.fabmax.kool.editor.actions.AddComponentAction
import de.fabmax.kool.editor.actions.EditorActions
import de.fabmax.kool.editor.actions.RenameNodeAction
import de.fabmax.kool.editor.actions.SetTransformAction
import de.fabmax.kool.editor.components.*
import de.fabmax.kool.editor.data.ScriptComponentData
import de.fabmax.kool.editor.model.EditorNodeModel
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.math.Mat3d
import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.math.MutableVec3d
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ui2.*

class ObjectPropertyEditor(ui: EditorUi) : EditorPanel("Object Properties", ui) {

    private val transformProperties = TransformProperties()

    private val tmpNodePos = MutableVec3d()
    private val tmpNodeRot = MutableVec3d()
    private val tmpNodeScale = MutableVec3d()
    private val tmpNodeRotMat = Mat3d()

    private val transformGizmo = NodeTransformGizmo(ui.editor)

    init {
        transformProperties.editHandlers += object : ValueEditHandler<Mat4d> {
            override fun onEdit(value: Mat4d) {
                val selectedNd = EditorState.selectedNode.value as? SceneNodeModel
                selectedNd?.node?.transform?.set(value)
            }

            override fun onEditEnd(startValue: Mat4d, endValue: Mat4d) {
                val selectedNd = EditorState.selectedNode.value as? SceneNodeModel
                if (selectedNd != null) {
                    applyTransformAction(selectedNd, startValue, endValue)
                }
            }
        }
        ui.editor.editorContent += transformGizmo
    }

    override val windowSurface: UiSurface = EditorPanelWindow {
        // clear gizmo transform object, will be set below if transform editor is available
        transformGizmo.setTransformObject(null)

        val selectedObject = EditorState.selectedNode.use()
        val title = when (selectedObject) {
            is SceneModel -> "Scene Properties"
            is SceneNodeModel -> "Node Properties"
            null -> "Object Properties"
            else -> "Object Properties <unknown type>"
        }

        Column(Grow.Std, Grow.Std) {
            editorTitleBar(windowDockable, title)
            objectProperties(selectedObject)
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

    private fun UiScope.objectProperties(selectedObject: EditorNodeModel?) = ScrollArea(
        containerModifier = { it.backgroundColor(null) },
        isScrollableHorizontal = false
    ) {
        modifier.width(Grow.Std)

        Column(Grow.Std, Grow.Std, scopeName = selectedObject?.nameState?.value) {
            Row(width = Grow.Std, height = sizes.baseSize) {
                modifier
                    .padding(horizontal = sizes.gap)

                if (selectedObject == null) {
                    Text("Nothing selected") {
                        modifier
                            .width(Grow.Std)
                            .alignY(AlignmentY.Center)
                            .textAlignX(AlignmentX.Center)
                            .font(sizes.italicText)
                    }
                } else {
                    Text("Name:") {
                        modifier
                            .alignY(AlignmentY.Center)
                            .margin(end = sizes.largeGap)
                    }

                    var editName by remember(selectedObject.name)
                    TextField(editName) {
                        if (!isFocused.use()) {
                            editName = selectedObject.name
                        }

                        defaultTextfieldStyle()
                        modifier
                            .hint("Object name")
                            .width(Grow.Std)
                            .alignY(AlignmentY.Center)
                            .padding(vertical = sizes.smallGap)
                            .onChange {
                                editName = it
                            }
                            .onEnterPressed {
                                EditorActions.applyAction(RenameNodeAction(selectedObject, it, selectedObject.name))
                            }
                    }
                }
            }

            if (selectedObject == null) {
                return@Column
            }

            for (component in selectedObject.components.use()) {
                when (component) {
                    is MaterialComponent -> materialComponent(selectedObject, component)
                    is MeshComponent -> meshComponent(selectedObject, component)
                    is SceneBackgroundComponent -> sceneBackgroundComponent(selectedObject)
                    is ScriptComponent -> scriptComponent(component)
                    is TransformComponent -> transformComponent(selectedObject)
                }
            }

            addComponentSelector(selectedObject)
        }
    }

    private fun UiScope.materialComponent(nodeModel: EditorNodeModel, materialComponent: MaterialComponent) {
        (nodeModel as? SceneNodeModel)?.let {
            val editor = remember { MaterialEditor(nodeModel, materialComponent) }
            editor.materialComponent = materialComponent
            editor()
        }
    }

    private fun UiScope.meshComponent(nodeModel: EditorNodeModel, meshComponent: MeshComponent) {
        (nodeModel as? SceneNodeModel)?.let {
            meshTypeProperties(nodeModel, meshComponent)
        }
    }

    private fun UiScope.sceneBackgroundComponent(nodeModel: EditorNodeModel) {
        (nodeModel as? SceneModel)?.let {
            val editor = remember { SceneBackgroundEditor(it) }
            editor.sceneModel = it
            editor()
        }
    }

    private fun UiScope.scriptComponent(scriptComponent: ScriptComponent) {
        val title = remember {
            val simpleName = scriptComponent.scriptClassNameState.value
                .replaceBeforeLast('.', "")
                .removePrefix(".")
            ScriptEditor.camelCaseToWords(simpleName)
        }
        collapsapsablePanel(
            title = title
        ) {
            val scriptEditor = remember { ScriptEditor(scriptComponent) }
            scriptEditor()
        }
    }

    private fun UiScope.transformComponent(nodeModel: EditorNodeModel) {
        (nodeModel as? SceneNodeModel)?.let {
            transformGizmo.setTransformObject(it)
            transformEditor(transformProperties)
        }
    }

    private fun UiScope.addComponentSelector(nodeModel: EditorNodeModel) {
        val popup = remember {
            val popup = AutoPopup(hideOnOutsideClick = false)
            popup.popupContent = Composable {
                Column {
                    modifier
                        .background(RoundRectBackground(colors.background, sizes.smallGap))
                        .border(RoundRectBorder(colors.secondaryVariant, sizes.smallGap, sizes.borderWidth))
                        .padding(sizes.smallGap)

                    // fixme: only for testing...
                    Button("Material") {
                        modifier
                            .width(Grow.Std)
                            .margin(bottom = sizes.smallGap)
                            .onClick {
                                EditorActions.applyAction(AddComponentAction(nodeModel, MaterialComponent()))
                            }
                    }

                    var hoverIdx by remember(-1)
                    val scriptClasses = EditorState.loadedApp.use()?.scriptClasses?.values ?: emptyList()
                    scriptClasses.forEachIndexed { i, scriptClass ->
                        scriptClass.klass.qualifiedName?.let { scriptFqn ->
                            Text(scriptFqn) {
                                modifier
                                    .size(Grow.Std, sizes.lineHeight)
                                    .padding(horizontal = sizes.gap)
                                    .onEnter { hoverIdx = i }
                                    .onExit { hoverIdx = -1 }
                                    .onClick {
                                        EditorActions.applyAction(
                                            AddComponentAction(
                                                nodeModel,
                                                ScriptComponent(ScriptComponentData(scriptFqn))
                                            )
                                        )
                                        popup.hide()
                                    }

                                if (i == hoverIdx) {
                                    modifier.background(RoundRectBackground(colors.hoverBg, sizes.smallGap))
                                }
                            }
                        }
                    }
                }
            }
            popup
        }

        // make this some kind of combobox / menu, once there are more options
        Button("Add component") {
            defaultButtonStyle()
            modifier
                .width(sizes.baseSize * 5)
                .margin(top = sizes.gap)
                .alignX(AlignmentX.Center)
                .onClick {
                    popup.toggleVisibility(Vec2f(uiNode.leftPx, uiNode.bottomPx))
                }
        }
        popup()
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