package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.actions.AddComponentAction
import de.fabmax.kool.editor.actions.RenameNodeAction
import de.fabmax.kool.editor.actions.fused
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
        val selObjs = KoolEditor.instance.selectionOverlay.selectionState.use().toMutableList()

        if (selObjs.size > 1) {
            selObjs.removeAll { it is SceneModel }
        } else if (selObjs.isEmpty()) {
            editor.activeScene.value?.let { selObjs += it }
        }

        val title = if (selObjs.size == 1 && selObjs[0] is SceneModel) "Scene Properties" else "Object Properties"

        Column(Grow.Std, Grow.Std) {
            editorTitleBar(windowDockable, icon, title)
            objectProperties(selObjs)
        }
    }

    private fun UiScope.objectProperties(objects: List<NodeModel>) = ScrollArea(
        containerModifier = { it.backgroundColor(null) },
        isScrollableHorizontal = false
    ) {
        modifier.width(Grow.Std)

        val scopeName = objects.joinToString("") { "${it.nodeId}" }
        Column(Grow.Std, Grow.Std, scopeName = scopeName) {
            objectNameOrCount(objects)
            if (objects.isEmpty()) return@Column

            componentEditors(objects)
            addComponentSelector(objects)
        }
    }

    private fun ColumnScope.objectNameOrCount(objects: List<NodeModel>) = Row(Grow.Std, sizes.baseSize) {
        modifier.padding(horizontal = sizes.gap)

        if (objects.size == 1) {
            Text("Name:") {
                modifier
                    .alignY(AlignmentY.Center)
                    .margin(end = sizes.largeGap)
            }

            var editName by remember(objects[0].name)
            TextField(editName) {
                if (!isFocused.use()) {
                    editName = objects[0].name
                }

                defaultTextfieldStyle()
                modifier
                    .hint("Object name")
                    .width(Grow.Std)
                    .alignY(AlignmentY.Center)
                    .padding(vertical = sizes.smallGap)
                    .onChange { editName = it }
                    .onEnterPressed {
                        RenameNodeAction(objects[0].nodeId, it, objects[0].name).apply()
                        surface.unfocus(this)
                    }
            }
        } else if (objects.isNotEmpty()) {
            Text("${objects.size} objects selected") {
                modifier
                    .width(Grow.Std)
                    .alignY(AlignmentY.Center)
                    .textAlignX(AlignmentX.Center)
                    .font(sizes.italicText)
            }
        } else {
            Text("Nothing selected") {
                modifier
                    .width(Grow.Std)
                    .alignY(AlignmentY.Center)
                    .textAlignX(AlignmentX.Center)
                    .font(sizes.italicText)
            }
        }
    }

    private fun ColumnScope.componentEditors(objects: List<NodeModel>) {
        val primary = objects[0]
        val componentTypes = buildSet {
            primary.components.use().forEach { add(it.componentType) }
            for (i in 1 until objects.size) {
                retainAll(objects[i].components.map { it.componentType })
            }
        }

        for (type in componentTypes) {
            val component = primary.components.first { it.componentType == type }
            when (component) {
                is CameraComponent -> componentEditor(objects) { CameraEditor() }
                is DiscreteLightComponent -> componentEditor(objects) { LightEditor() }
                is MaterialComponent -> componentEditor(objects) { MaterialEditor() }
                is MeshComponent -> componentEditor(objects) { MeshEditor() }
                is ModelComponent -> componentEditor(objects) { ModelEditor() }
                is ScenePropertiesComponent -> componentEditor(objects) { ScenePropertiesEditor() }
                is SceneBackgroundComponent -> componentEditor(objects) { SceneBackgroundEditor() }
                is BehaviorComponent -> componentEditor(objects) { BehaviorEditor() }
                is ShadowMapComponent -> componentEditor(objects) { ShadowMapEditor() }
                is SsaoComponent -> componentEditor(objects) { SsaoEditor() }
                is TransformComponent -> componentEditor(objects) { TransformEditor() }
                is PhysicsWorldComponent -> componentEditor(objects) { PhysicsWorldEditor() }
                is RigidActorComponent -> componentEditor(objects) { RigidActorEditor() }
                is CharacterControllerComponent -> componentEditor(objects) { CharacterControllerEditor() }
            }
        }
    }

    private inline fun <reified T: EditorModelComponent> UiScope.componentEditor(nodeModels: List<NodeModel>, editorProvider: () -> ComponentEditor<T>) {
        Box(width = Grow.Std, scopeName = nodeModels[0].requireComponent<T>().componentType) {
            val editor = remember(editorProvider)
            editor.components = nodeModels.map { it.requireComponent() }
            editor()
        }
    }

    private fun UiScope.addComponentSelector(objects: List<NodeModel>) {
        val popup = remember { ContextPopupMenu<List<NodeModel>>("add-component") }
        var popupPos by remember(Vec2f.ZERO)

        val button = iconTextButton(
            icon = IconMap.small.plus,
            text = "Add Component",
            width = sizes.baseSize * 5,
            margin = sizes.gap
        ) {
            if (!popup.isVisible.use()) {
                popup.show(popupPos, makeAddComponentMenu(objects), objects)
            } else {
                popup.hide()
            }
        }

        popupPos = Vec2f(button.uiNode.leftPx, button.uiNode.bottomPx)
        popup()
    }

    private fun makeAddComponentMenu(objects: List<NodeModel>): SubMenuItem<List<NodeModel>> = SubMenuItem {
        addComponentOptions
            .filter { option ->
                objects.any { !option.hasComponent(it) } && objects.all { option.accept(it) }
            }
            .forEach {
                it.addMenuItems(objects, this)
            }
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
            ComponentAdder.AddRigidActorComponent,
            ComponentAdder.AddCharacterControllerComponent,
        )
    }

    private sealed class ComponentAdder<T: EditorModelComponent>(val name: String) {
        abstract fun hasComponent(nodeModel: NodeModel): Boolean
        abstract fun accept(nodeModel: NodeModel): Boolean

        open fun addMenuItems(targetObjs: List<NodeModel>, parentMenu: SubMenuItem<List<NodeModel>>) {
            parentMenu.item(name) { addComponent(it) }
        }

        open fun createComponent(target: NodeModel): T? = null

        fun addComponent(targetObjs: List<NodeModel>) {
            targetObjs
                .filter { target ->
                    !hasComponent(target)
                }
                .mapNotNull { target ->
                    createComponent(target)?.let { AddComponentAction(target.nodeId, it) }
                }
                .fused().apply()
        }

        data object AddSsaoComponent : ComponentAdder<SsaoComponent>("Screen-space Ambient Occlusion") {
            override fun hasComponent(nodeModel: NodeModel) = nodeModel.hasComponent<SsaoComponent>()
            override fun accept(nodeModel: NodeModel) = nodeModel is SceneModel
            override fun createComponent(target: NodeModel): SsaoComponent = SsaoComponent(target as SceneModel)
        }

        data object AddCameraComponent : ComponentAdder<CameraComponent>("Camera") {
            override fun hasComponent(nodeModel: NodeModel) = nodeModel.hasComponent<ContentComponent>()
            override fun accept(nodeModel: NodeModel) = nodeModel is SceneModel
            override fun createComponent(target: NodeModel): CameraComponent = CameraComponent(target as SceneNodeModel)
        }


        data object AddLightComponent : ComponentAdder<DiscreteLightComponent>("Light") {
            override fun hasComponent(nodeModel: NodeModel) = nodeModel.hasComponent<ContentComponent>()
            override fun accept(nodeModel: NodeModel) = nodeModel is SceneNodeModel
            override fun createComponent(target: NodeModel): DiscreteLightComponent = DiscreteLightComponent(target as SceneNodeModel)
        }

        data object AddShadowMapComponent : ComponentAdder<ShadowMapComponent>("Shadow") {
            override fun hasComponent(nodeModel: NodeModel) = nodeModel.hasComponent<ShadowMapComponent>()
            override fun accept(nodeModel: NodeModel) = nodeModel.hasComponent<DiscreteLightComponent>()
            override fun createComponent(target: NodeModel): ShadowMapComponent = ShadowMapComponent(target as SceneNodeModel)
        }

        data object AddMeshComponent : ComponentAdder<MeshComponent>("Mesh") {
            override fun hasComponent(nodeModel: NodeModel) = nodeModel.hasComponent<ContentComponent>()
            override fun accept(nodeModel: NodeModel) = nodeModel is SceneNodeModel
            override fun createComponent(target: NodeModel): MeshComponent = MeshComponent(target as SceneNodeModel)
        }

        data object AddModelComponent : ComponentAdder<ModelComponent>("Model") {
            override fun hasComponent(nodeModel: NodeModel) = nodeModel.hasComponent<ContentComponent>()
            override fun accept(nodeModel: NodeModel) = nodeModel is SceneNodeModel

            override fun addMenuItems(targetObjs: List<NodeModel>, parentMenu: SubMenuItem<List<NodeModel>>) {
                val models = KoolEditor.instance.availableAssets.modelAssets
                if (models.isNotEmpty()) {
                    parentMenu.subMenu(name) {
                        models.forEach { model ->
                            item(model.name) { objs ->
                                objs
                                    .filter { target ->
                                        !hasComponent(target)
                                    }
                                    .map { target ->
                                        val modelComp = ModelComponent(target as SceneNodeModel, ModelComponentData(model.path))
                                        AddComponentAction(target.nodeId, modelComp)
                                    }
                                    .fused().apply()
                            }
                        }
                    }
                }
            }
        }

        data object AddMaterialComponent : ComponentAdder<MaterialComponent>("Material") {
            override fun hasComponent(nodeModel: NodeModel) = nodeModel.hasComponent<MaterialComponent>()
            override fun accept(nodeModel: NodeModel) = nodeModel.hasComponent<MeshComponent>() || nodeModel.hasComponent<ModelComponent>()
            override fun createComponent(target: NodeModel): MaterialComponent = MaterialComponent(target as SceneNodeModel)
        }

        data object AddPhysicsWorldComponent : ComponentAdder<PhysicsWorldComponent>("Physics World") {
            override fun hasComponent(nodeModel: NodeModel) = nodeModel.hasComponent<PhysicsWorldComponent>()
            override fun accept(nodeModel: NodeModel) = nodeModel is SceneModel
            override fun createComponent(target: NodeModel): PhysicsWorldComponent = PhysicsWorldComponent(target as SceneModel)
        }

        data object AddRigidActorComponent : ComponentAdder<RigidActorComponent>("Rigid Actor") {
            override fun hasComponent(nodeModel: NodeModel) = nodeModel.hasComponent<RigidActorComponent>()
            override fun accept(nodeModel: NodeModel) = nodeModel is SceneNodeModel && !nodeModel.hasComponent<CharacterControllerComponent>()
            override fun createComponent(target: NodeModel): RigidActorComponent = RigidActorComponent(target as SceneNodeModel)
        }

        data object AddCharacterControllerComponent : ComponentAdder<CharacterControllerComponent>("Character Controller") {
            override fun hasComponent(nodeModel: NodeModel) = nodeModel.hasComponent<CharacterControllerComponent>()
            override fun accept(nodeModel: NodeModel) = nodeModel is SceneNodeModel && !nodeModel.hasComponent<RigidActorComponent>()
            override fun createComponent(target: NodeModel): CharacterControllerComponent = CharacterControllerComponent(target as SceneNodeModel)
        }

        data object AddScriptComponent : ComponentAdder<BehaviorComponent>("Behavior") {
            override fun hasComponent(nodeModel: NodeModel) = false
            override fun accept(nodeModel: NodeModel) = true

            override fun addMenuItems(targetObjs: List<NodeModel>, parentMenu: SubMenuItem<List<NodeModel>>) {
                val behaviorClasses = KoolEditor.instance.loadedApp.value?.behaviorClasses?.values ?: emptyList()
                if (behaviorClasses.isNotEmpty()) {
                    parentMenu.subMenu(name) {
                        behaviorClasses.forEach { script ->
                            item(script.prettyName) { objs ->
                                objs
                                    .map { target ->
                                        val behaviorComp = BehaviorComponent(target, BehaviorComponentData(script.qualifiedName))
                                        AddComponentAction(target.nodeId, behaviorComp)
                                    }
                                    .fused().apply()
                            }
                        }
                    }
                }
            }
        }

    }
}