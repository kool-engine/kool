package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.EditorState
import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.actions.AddComponentAction
import de.fabmax.kool.editor.actions.RenameNodeAction
import de.fabmax.kool.editor.actions.SetTransformAction
import de.fabmax.kool.editor.components.*
import de.fabmax.kool.editor.data.ModelComponentData
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
                // todo: support transforming multiple objects at once
                val selectedNd = EditorState.selection.firstOrNull() as? SceneNodeModel
                selectedNd?.drawNode?.transform?.set(value)
            }

            override fun onEditEnd(startValue: Mat4d, endValue: Mat4d) {
                // todo: support transforming multiple objects at once
                val selectedNd = EditorState.selection.firstOrNull() as? SceneNodeModel
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

        val selObjs = EditorState.selection.use()
        val selectedObject = if (selObjs.size == 1) selObjs[0] else null
        val title = when (selectedObject) {
            is SceneModel -> "Scene Properties"
            is SceneNodeModel -> "Object Properties"
            null -> "Object Properties"
            else -> "Object Properties <unknown type>"
        }

        Column(Grow.Std, Grow.Std) {
            editorTitleBar(windowDockable, IconMap.PROPERTIES, title)
            objectProperties(selectedObject)
        }

        surface.onEachFrame {
            val selectedNd = EditorState.selection.firstOrNull() as? SceneNodeModel
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

        Column(Grow.Std, Grow.Std, scopeName = "node-${selectedObject?.nodeId}") {
            Row(width = Grow.Std, height = sizes.baseSize) {
                modifier
                    .padding(horizontal = sizes.gap)

                if (selectedObject == null) {
                    val n = EditorState.selection.size
                    val txt = if (n == 0) "Nothing selected" else "$n objects selected"
                    Text(txt) {
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
                    is DiscreteLightComponent -> componentEditor(component) { LightEditor(component) }
                    is MaterialComponent -> componentEditor(component) { MaterialEditor(component) }
                    is MeshComponent -> componentEditor(component) { MeshEditor(component) }
                    is SceneBackgroundComponent -> componentEditor(component) { SceneBackgroundEditor(component) }
                    is ScriptComponent -> componentEditor(component) { ScriptEditor(component) }
                    is ShadowMapComponent -> componentEditor(component) { ShadowMapEditor(component) }
                    is SsaoComponent -> componentEditor(component) { SsaoEditor(component) }
                    is TransformComponent -> transformComponent(selectedObject)
                }
            }

            addComponentSelector(selectedObject)
        }
    }

    private inline fun <reified T: EditorModelComponent> UiScope.componentEditor(component: T, editorProvider: () -> ComponentEditor<T>) {
        Box(width = Grow.Std, scopeName = "comp-${T::class.simpleName}") {
            val editor = remember(editorProvider)
            editor.component = component
            editor()
        }
    }

    private fun UiScope.transformComponent(nodeModel: EditorNodeModel) {
        (nodeModel as? SceneNodeModel)?.let {
            // todo add button to enable gizmo
            //transformGizmo.setTransformObject(it)
            transformEditor(transformProperties)
        }
    }

    private fun UiScope.addComponentSelector(nodeModel: EditorNodeModel) {
        val popup = remember { ContextPopupMenu<EditorNodeModel>() }

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
        addComponentOptions.filter { it.accept(node) }.forEach { it.addMenuItems(this) }
    }

    companion object {
        fun applyTransformAction(nodeModel: SceneNodeModel, oldTransform: Mat4d, newTransform: Mat4d) {
            SetTransformAction(
                editedNodeModel = nodeModel,
                oldTransform = oldTransform,
                newTransform = newTransform
            ).apply()
        }

        private val addComponentOptions = listOf(
            ComponentAdder.AddMeshComponent,
            ComponentAdder.AddModelComponent,
            ComponentAdder.AddMaterialComponent,
            ComponentAdder.AddLightComponent,
            ComponentAdder.AddShadowMapComponent,
            ComponentAdder.AddScriptComponent,
            ComponentAdder.AddSsaoComponent,
        )
    }

    private sealed class ComponentAdder<T: EditorModelComponent>(val name: String) {
        abstract fun accept(nodeModel: EditorNodeModel): Boolean

        open fun addMenuItems(parentMenu: SubMenuItem<EditorNodeModel>) = parentMenu.item(name) { addComponent(it) }
        open fun createComponent(): T? = null

        fun addComponent(target: EditorNodeModel) {
            createComponent()?.let { AddComponentAction(target, it).apply() }
        }


        object AddSsaoComponent : ComponentAdder<SsaoComponent>("Screen-space Ambient Occlusion") {
            override fun createComponent(): SsaoComponent = SsaoComponent()
            override fun accept(nodeModel: EditorNodeModel) =
                nodeModel is SceneModel && !nodeModel.hasComponent<SsaoComponent>()
        }


        object AddLightComponent : ComponentAdder<DiscreteLightComponent>("Light") {
            override fun createComponent(): DiscreteLightComponent = DiscreteLightComponent()
            override fun accept(nodeModel: EditorNodeModel) =
                nodeModel is SceneNodeModel && !nodeModel.hasComponent<ContentComponent>()
        }

        object AddShadowMapComponent : ComponentAdder<ShadowMapComponent>("Shadow") {
            override fun createComponent(): ShadowMapComponent = ShadowMapComponent()
            override fun accept(nodeModel: EditorNodeModel) =
                nodeModel.hasComponent<DiscreteLightComponent>() && !nodeModel.hasComponent<ShadowMapComponent>()
        }

        object AddMeshComponent : ComponentAdder<MeshComponent>("Mesh") {
            override fun createComponent(): MeshComponent = MeshComponent()
            override fun accept(nodeModel: EditorNodeModel) =
                nodeModel is SceneNodeModel && !nodeModel.hasComponent<ContentComponent>()
        }

        object AddModelComponent : ComponentAdder<ModelComponent>("Model") {
            override fun accept(nodeModel: EditorNodeModel) =
                nodeModel is SceneNodeModel && !nodeModel.hasComponent<ContentComponent>()

            override fun addMenuItems(parentMenu: SubMenuItem<EditorNodeModel>) {
                val models = KoolEditor.instance.availableAssets.modelAssets
                if (models.isNotEmpty()) {
                    parentMenu.subMenu(name) {
                        models.forEach { model ->
                            item(model.name) {
                                AddComponentAction(it, ModelComponent(ModelComponentData(model.path))).apply()
                            }
                        }
                    }
                }
            }
        }

        object AddMaterialComponent : ComponentAdder<MaterialComponent>("Material") {
            override fun createComponent(): MaterialComponent = MaterialComponent()
            override fun accept(nodeModel: EditorNodeModel) = !nodeModel.hasComponent<MaterialComponent>()
                    && (nodeModel.hasComponent<MeshComponent>() || nodeModel.hasComponent<ModelComponent>())
        }

        object AddScriptComponent : ComponentAdder<ScriptComponent>("Script") {
            override fun accept(nodeModel: EditorNodeModel) = true

            override fun addMenuItems(parentMenu: SubMenuItem<EditorNodeModel>) {
                val scriptClasses = EditorState.loadedApp.value?.scriptClasses?.values ?: emptyList()
                if (scriptClasses.isNotEmpty()) {
                    parentMenu.subMenu(name) {
                        scriptClasses.forEach { script ->
                            item(script.prettyName) {
                                AddComponentAction(it, ScriptComponent(ScriptComponentData(script.qualifiedName))).apply()
                            }
                        }
                    }
                }
            }
        }

    }
}