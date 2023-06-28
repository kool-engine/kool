package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.EditorState
import de.fabmax.kool.editor.actions.AddComponentAction
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
                selectedNd?.drawNode?.transform?.set(value)
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
            is SceneNodeModel -> "Scene Object Properties"
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
                selectedNd.drawNode.transform.getPosition(tmpNodePos)
                transformProperties.setPosition(tmpNodePos)
                selectedNd.drawNode.transform.matrix.getRotation(tmpNodeRotMat)
                transformProperties.setRotation(tmpNodeRotMat.getEulerAngles(tmpNodeRot))
                selectedNd.drawNode.transform.matrix.getScale(tmpNodeScale)
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
                                RenameNodeAction(selectedObject, it, selectedObject.name).apply()
                            }
                    }
                }
            }

            if (selectedObject == null) {
                return@Column
            }

            for (component in selectedObject.components.use()) {
                when (component) {
                    is MaterialComponent -> materialComponent(component)
                    is MeshComponent -> meshComponent(component)
                    is SceneBackgroundComponent -> sceneBackgroundComponent(component)
                    is ScriptComponent -> scriptComponent(component)
                    is TransformComponent -> transformComponent(selectedObject)
                }
            }

            addComponentSelector(selectedObject)
        }
    }

    private fun UiScope.materialComponent(materialComponent: MaterialComponent) {
        val editor = remember { MaterialEditor(materialComponent) }
        editor.materialComponent = materialComponent
        editor()
    }

    private fun UiScope.meshComponent(meshComponent: MeshComponent) {
        meshTypeProperties(meshComponent)
    }

    private fun UiScope.sceneBackgroundComponent(backgroundComponent: SceneBackgroundComponent) {
        val editor = remember { SceneBackgroundEditor(backgroundComponent) }
        editor.backgroundComponent = backgroundComponent
        editor()
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
        val popup = remember { ContextPopupMenu<EditorNodeModel>(false) }

        Button("Add component") {
            defaultButtonStyle()
            modifier
                .width(sizes.baseSize * 5)
                .margin(top = sizes.gap)
                .alignX(AlignmentX.Center)
                .onClick {
                    if (!popup.isVisible.use()) {
                        popup.show(Vec2f(uiNode.leftPx, uiNode.bottomPx), makeAddComponentMenu(nodeModel), nodeModel)
                    } else {
                        popup.hide()
                    }
                }
        }
        popup()
    }

    private fun makeAddComponentMenu(node: EditorNodeModel): SubMenuItem<EditorNodeModel> = SubMenuItem {
        if (node !is SceneModel && node.getComponent<MeshComponent>() == null && node.getComponent<ModelComponent>() == null) {
            item("Mesh") {
                AddComponentAction(it, MeshComponent()).apply()
            }
        }
        if (node !is SceneModel && node.getComponent<MaterialComponent>() == null) {
            item("Material") {
                AddComponentAction(it, MaterialComponent()).apply()
            }
        }
        val scriptClasses = EditorState.loadedApp.value?.scriptClasses?.values ?: emptyList()
        if (scriptClasses.isNotEmpty()) {
            subMenu("Scripts") {
                scriptClasses.forEach { script ->
                    item(script.prettyName) {
                        AddComponentAction(node, ScriptComponent(ScriptComponentData(script.qualifiedName))).apply()
                    }
                }
            }
        }
    }

    companion object {
        fun applyTransformAction(nodeModel: SceneNodeModel, oldTransform: Mat4d, newTransform: Mat4d) {
            SetTransformAction(
                editedNodeModel = nodeModel,
                oldTransform = oldTransform,
                newTransform = newTransform
            ).apply()
        }
    }
}