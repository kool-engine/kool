package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.actions.AddComponentAction
import de.fabmax.kool.editor.actions.RenameNodeAction
import de.fabmax.kool.editor.components.*
import de.fabmax.kool.editor.data.BehaviorComponentData
import de.fabmax.kool.editor.data.ModelComponentData
import de.fabmax.kool.editor.model.NodeModel
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ui2.*

class ObjectPropertyEditor(ui: EditorUi) : EditorPanel("Object Properties", IconMap.medium.properties, ui) {

    override val windowSurface: UiSurface = editorPanelWithPanelBar {
        val selObjs = KoolEditor.instance.selectionOverlay.selectionState.use()
        val selectedObject = if (selObjs.size == 1) selObjs.first() else null
        val title = when (selectedObject) {
            is SceneModel -> "Scene Properties"
            is SceneNodeModel -> "Object Properties"
            null -> "Object Properties"
            else -> "Object Properties <unknown type>"
        }

        Column(Grow.Std, Grow.Std) {
            editorTitleBar(windowDockable, icon, title)
            objectProperties(selectedObject)
        }
    }

    private fun UiScope.objectProperties(selectedObject: NodeModel?) = ScrollArea(
        containerModifier = { it.backgroundColor(null) },
        isScrollableHorizontal = false
    ) {
        modifier.width(Grow.Std)

        Column(Grow.Std, Grow.Std, scopeName = "node-${selectedObject?.nodeId}") {
            Row(width = Grow.Std, height = sizes.baseSize) {
                modifier
                    .padding(horizontal = sizes.gap)

                if (selectedObject == null) {
                    val n = KoolEditor.instance.selectionOverlay.selection.size
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
                                RenameNodeAction(selectedObject.nodeId, it, selectedObject.name).apply()
                            }
                    }
                }
            }

            if (selectedObject == null) {
                return@Column
            }

            for (component in selectedObject.components.use()) {
                when (component) {
                    is CameraComponent -> componentEditor(component) { CameraEditor(component) }
                    is DiscreteLightComponent -> componentEditor(component) { LightEditor(component) }
                    is MaterialComponent -> componentEditor(component) { MaterialEditor(component) }
                    is MeshComponent -> componentEditor(component) { MeshEditor(component) }
                    is ModelComponent -> componentEditor(component) { ModelEditor(component) }
                    is ScenePropertiesComponent -> componentEditor(component) { ScenePropertiesEditor(component) }
                    is SceneBackgroundComponent -> componentEditor(component) { SceneBackgroundEditor(component) }
                    is BehaviorComponent -> componentEditor(component) { BehaviorEditor(component) }
                    is ShadowMapComponent -> componentEditor(component) { ShadowMapEditor(component) }
                    is SsaoComponent -> componentEditor(component) { SsaoEditor(component) }
                    is TransformComponent -> componentEditor(component) { TransformEditor(component) }
                    is PhysicsWorldComponent -> componentEditor(component) { PhysicsWorldEditor(component) }
                    is RigidBodyComponent -> componentEditor(component) { RigidBodyEditor(component) }
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

    private fun UiScope.addComponentSelector(nodeModel: NodeModel) {
        val popup = remember { ContextPopupMenu<NodeModel>() }
        var popupPos by remember(Vec2f.ZERO)

        val button = iconTextButton(
            icon = IconMap.small.plus,
            text = "Add Component",
            width = sizes.baseSize * 5,
            margin = sizes.gap
        ) {
            if (!popup.isVisible.use()) {
                popup.show(popupPos, makeAddComponentMenu(nodeModel), nodeModel)
            } else {
                popup.hide()
            }
        }

        popupPos = Vec2f(button.uiNode.leftPx, button.uiNode.bottomPx)
        popup()
    }

    private fun makeAddComponentMenu(node: NodeModel): SubMenuItem<NodeModel> = SubMenuItem {
        addComponentOptions
            .filter { it.accept(node) }
            .forEach { it.addMenuItems(node, this) }
    }

    companion object {
        private val addComponentOptions = listOf(
            ComponentAdder.AddMeshComponent,
            ComponentAdder.AddModelComponent,
            ComponentAdder.AddMaterialComponent,
            ComponentAdder.AddLightComponent,
            ComponentAdder.AddShadowMapComponent,
            ComponentAdder.AddScriptComponent,
            ComponentAdder.AddSsaoComponent,
            ComponentAdder.AddCameraComponent,
            ComponentAdder.AddPhysicsWorldComponent,
            ComponentAdder.AddRigidBodyComponent,
        )
    }

    private sealed class ComponentAdder<T: EditorModelComponent>(val name: String) {
        abstract fun accept(nodeModel: NodeModel): Boolean

        open fun addMenuItems(target: NodeModel, parentMenu: SubMenuItem<NodeModel>) {
            parentMenu.item(name) { addComponent(it) }
        }
        open fun createComponent(target: NodeModel): T? = null

        fun addComponent(target: NodeModel) {
            createComponent(target)?.let { AddComponentAction(target.nodeId, it).apply() }
        }

        data object AddSsaoComponent : ComponentAdder<SsaoComponent>("Screen-space Ambient Occlusion") {
            override fun createComponent(target: NodeModel): SsaoComponent = SsaoComponent(target as SceneModel)
            override fun accept(nodeModel: NodeModel) =
                nodeModel is SceneModel && !nodeModel.hasComponent<SsaoComponent>()
        }

        data object AddCameraComponent : ComponentAdder<CameraComponent>("Camera") {
            override fun createComponent(target: NodeModel): CameraComponent = CameraComponent(target as SceneNodeModel)
            override fun accept(nodeModel: NodeModel) =
                nodeModel is SceneNodeModel && !nodeModel.hasComponent<ContentComponent>()
        }


        data object AddLightComponent : ComponentAdder<DiscreteLightComponent>("Light") {
            override fun createComponent(target: NodeModel): DiscreteLightComponent = DiscreteLightComponent(target as SceneNodeModel)
            override fun accept(nodeModel: NodeModel) =
                nodeModel is SceneNodeModel && !nodeModel.hasComponent<ContentComponent>()
        }

        data object AddShadowMapComponent : ComponentAdder<ShadowMapComponent>("Shadow") {
            override fun createComponent(target: NodeModel): ShadowMapComponent = ShadowMapComponent(target as SceneNodeModel)
            override fun accept(nodeModel: NodeModel) =
                nodeModel.hasComponent<DiscreteLightComponent>() && !nodeModel.hasComponent<ShadowMapComponent>()
        }

        data object AddMeshComponent : ComponentAdder<MeshComponent>("Mesh") {
            override fun createComponent(target: NodeModel): MeshComponent = MeshComponent(target as SceneNodeModel)
            override fun accept(nodeModel: NodeModel) =
                nodeModel is SceneNodeModel && !nodeModel.hasComponent<ContentComponent>()
        }

        data object AddModelComponent : ComponentAdder<ModelComponent>("Model") {
            override fun accept(nodeModel: NodeModel) =
                nodeModel is SceneNodeModel && !nodeModel.hasComponent<ContentComponent>()

            override fun addMenuItems(target: NodeModel, parentMenu: SubMenuItem<NodeModel>) {
                val models = KoolEditor.instance.availableAssets.modelAssets
                if (models.isNotEmpty()) {
                    parentMenu.subMenu(name) {
                        models.forEach { model ->
                            item(model.name) {
                                AddComponentAction(it.nodeId, ModelComponent(target as SceneNodeModel, ModelComponentData(model.path))).apply()
                            }
                        }
                    }
                }
            }
        }

        data object AddMaterialComponent : ComponentAdder<MaterialComponent>("Material") {
            override fun createComponent(target: NodeModel): MaterialComponent = MaterialComponent(target as SceneNodeModel)
            override fun accept(nodeModel: NodeModel) = !nodeModel.hasComponent<MaterialComponent>()
                    && (nodeModel.hasComponent<MeshComponent>() || nodeModel.hasComponent<ModelComponent>())
        }

        data object AddPhysicsWorldComponent : ComponentAdder<PhysicsWorldComponent>("Physics World") {
            override fun createComponent(target: NodeModel): PhysicsWorldComponent = PhysicsWorldComponent(target as SceneModel)
            override fun accept(nodeModel: NodeModel) =
                nodeModel is SceneModel && !nodeModel.hasComponent<PhysicsWorldComponent>()
        }

        data object AddRigidBodyComponent : ComponentAdder<RigidBodyComponent>("Rigid Body") {
            override fun createComponent(target: NodeModel): RigidBodyComponent = RigidBodyComponent(target as SceneNodeModel)
            override fun accept(nodeModel: NodeModel) =
                nodeModel is SceneNodeModel && !nodeModel.hasComponent<RigidBodyComponent>()
        }

        data object AddScriptComponent : ComponentAdder<BehaviorComponent>("Behavior") {
            override fun accept(nodeModel: NodeModel) = true

            override fun addMenuItems(target: NodeModel, parentMenu: SubMenuItem<NodeModel>) {
                val scriptClasses = KoolEditor.instance.loadedApp.value?.behaviorClasses?.values ?: emptyList()
                if (scriptClasses.isNotEmpty()) {
                    parentMenu.subMenu(name) {
                        scriptClasses.forEach { script ->
                            item(script.prettyName) {
                                AddComponentAction(it.nodeId, BehaviorComponent(target, BehaviorComponentData(script.qualifiedName))).apply()
                            }
                        }
                    }
                }
            }
        }

    }
}