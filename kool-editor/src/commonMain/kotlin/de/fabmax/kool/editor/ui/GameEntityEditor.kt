package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.actions.AddComponentAction
import de.fabmax.kool.editor.actions.RenameNodeAction
import de.fabmax.kool.editor.actions.fused
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.components.*
import de.fabmax.kool.editor.data.BehaviorComponentData
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.editor.data.ModelComponentData
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ui2.*

class GameEntityEditor(ui: EditorUi) : EditorPanel("Object Properties", IconMap.medium.properties, ui) {

    override val windowSurface: UiSurface = editorPanelWithPanelBar {
        val selObjs = KoolEditor.instance.selectionOverlay.selectionState.use().toMutableList()

        if (selObjs.size > 1) {
            selObjs.removeAll { it.isSceneRoot }
        } else if (selObjs.isEmpty()) {
            editor.activeScene.value?.let { selObjs += it.sceneEntity }
        }

        val title = if (selObjs.size == 1 && selObjs[0].isSceneRoot) "Scene Properties" else "Object Properties"

        Column(Grow.Std, Grow.Std) {
            editorTitleBar(windowDockable, icon, title)
            objectProperties(selObjs)
        }
    }

    private fun UiScope.objectProperties(objects: List<GameEntity>) = ScrollArea(
        containerModifier = { it.backgroundColor(null) },
        vScrollbarModifier = { it.width(sizes.scrollbarWidth) },
        isScrollableHorizontal = false
    ) {
        modifier.width(Grow.Std)

        val scopeName = objects.joinToString("") { "${it.id}" }
        Column(Grow.Std, Grow.Std, scopeName = scopeName) {
            objectNameOrCount(objects)
            if (objects.isEmpty()) return@Column

            componentEditors(objects)
            addComponentSelector(objects)
        }
    }

    private fun ColumnScope.objectNameOrCount(objects: List<GameEntity>) = Row(Grow.Std, sizes.baseSize) {
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
                        RenameNodeAction(objects[0].id, it, objects[0].name).apply()
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

    private fun ColumnScope.componentEditors(objects: List<GameEntity>) {
        val primary = objects[0]
        val componentTypes = buildSet {
            primary.components.use()
                .filter { primary.isSceneChild || it !is TransformComponent }
                .forEach { add(it.componentType) }
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
                is SceneComponent -> componentEditor(objects) { ScenePropertiesEditor() }
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

    private inline fun <reified T: GameEntityComponent> UiScope.componentEditor(gameEntities: List<GameEntity>, editorProvider: () -> ComponentEditor<T>) {
        Box(width = Grow.Std, scopeName = gameEntities[0].requireComponent<T>().componentType) {
            val editor = remember(editorProvider)
            editor.components = gameEntities.map { it.requireComponent() }
            editor()
        }
    }

    private fun UiScope.addComponentSelector(objects: List<GameEntity>) {
        val popup = remember { ContextPopupMenu<List<GameEntity>>("add-component") }
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

    private fun makeAddComponentMenu(objects: List<GameEntity>): SubMenuItem<List<GameEntity>> = SubMenuItem {
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

    private sealed class ComponentAdder<T: GameEntityComponent>(val name: String) {
        abstract fun hasComponent(gameEntity: GameEntity): Boolean
        abstract fun accept(gameEntity: GameEntity): Boolean

        open fun addMenuItems(targetObjs: List<GameEntity>, parentMenu: SubMenuItem<List<GameEntity>>) {
            parentMenu.item(name) { addComponent(it) }
        }

        open fun createComponent(target: GameEntity): T? = null

        fun addComponent(targetObjs: List<GameEntity>) {
            targetObjs
                .filter { target ->
                    !hasComponent(target)
                }
                .mapNotNull { target ->
                    createComponent(target)?.let { AddComponentAction(target.id, it) }
                }
                .fused().apply()
        }

        data object AddSsaoComponent : ComponentAdder<SsaoComponent>("Screen-space Ambient Occlusion") {
            override fun hasComponent(gameEntity: GameEntity) = gameEntity.hasComponent<SsaoComponent>()
            override fun accept(gameEntity: GameEntity) = gameEntity.isSceneRoot
            override fun createComponent(target: GameEntity): SsaoComponent = SsaoComponent(target)
        }

        data object AddCameraComponent : ComponentAdder<CameraComponent>("Camera") {
            override fun hasComponent(gameEntity: GameEntity) = gameEntity.hasComponent<DrawNodeComponent>()
            override fun accept(gameEntity: GameEntity) = gameEntity.isSceneRoot
            override fun createComponent(target: GameEntity): CameraComponent = CameraComponent(target)
        }


        data object AddLightComponent : ComponentAdder<DiscreteLightComponent>("Light") {
            override fun hasComponent(gameEntity: GameEntity) = gameEntity.hasComponent<DrawNodeComponent>()
            override fun accept(gameEntity: GameEntity) = gameEntity.isSceneChild
            override fun createComponent(target: GameEntity): DiscreteLightComponent = DiscreteLightComponent(target)
        }

        data object AddShadowMapComponent : ComponentAdder<ShadowMapComponent>("Shadow") {
            override fun hasComponent(gameEntity: GameEntity) = gameEntity.hasComponent<ShadowMapComponent>()
            override fun accept(gameEntity: GameEntity) = gameEntity.hasComponent<DiscreteLightComponent>()
            override fun createComponent(target: GameEntity): ShadowMapComponent = ShadowMapComponent(target)
        }

        data object AddMeshComponent : ComponentAdder<MeshComponent>("Mesh") {
            override fun hasComponent(gameEntity: GameEntity) = gameEntity.hasComponent<DrawNodeComponent>()
            override fun accept(gameEntity: GameEntity) = gameEntity.isSceneChild
            override fun createComponent(target: GameEntity): MeshComponent = MeshComponent(target)
        }

        data object AddModelComponent : ComponentAdder<ModelComponent>("Model") {
            override fun hasComponent(gameEntity: GameEntity) = gameEntity.hasComponent<DrawNodeComponent>()
            override fun accept(gameEntity: GameEntity) = gameEntity.isSceneChild

            override fun addMenuItems(targetObjs: List<GameEntity>, parentMenu: SubMenuItem<List<GameEntity>>) {
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
                                        val modelComp = ModelComponent(target, ComponentInfo(ModelComponentData(model.path)))
                                        AddComponentAction(target.id, modelComp)
                                    }
                                    .fused().apply()
                            }
                        }
                    }
                }
            }
        }

        data object AddMaterialComponent : ComponentAdder<MaterialComponent>("Material") {
            override fun hasComponent(gameEntity: GameEntity) = gameEntity.hasComponent<MaterialComponent>()
            override fun accept(gameEntity: GameEntity) = gameEntity.hasComponent<MeshComponent>() || gameEntity.hasComponent<ModelComponent>()
            override fun createComponent(target: GameEntity): MaterialComponent = MaterialComponent(target)
        }

        data object AddPhysicsWorldComponent : ComponentAdder<PhysicsWorldComponent>("Physics World") {
            override fun hasComponent(gameEntity: GameEntity) = gameEntity.hasComponent<PhysicsWorldComponent>()
            override fun accept(gameEntity: GameEntity) = gameEntity.isSceneRoot
            override fun createComponent(target: GameEntity): PhysicsWorldComponent = PhysicsWorldComponent(target)
        }

        data object AddRigidActorComponent : ComponentAdder<RigidActorComponent>("Rigid Actor") {
            override fun hasComponent(gameEntity: GameEntity) = gameEntity.hasComponent<RigidActorComponent>()
            override fun accept(gameEntity: GameEntity) = gameEntity.isSceneChild && !gameEntity.hasComponent<CharacterControllerComponent>()
            override fun createComponent(target: GameEntity): RigidActorComponent = RigidActorComponent(target)
        }

        data object AddCharacterControllerComponent : ComponentAdder<CharacterControllerComponent>("Character Controller") {
            override fun hasComponent(gameEntity: GameEntity) = gameEntity.hasComponent<CharacterControllerComponent>()
            override fun accept(gameEntity: GameEntity) = gameEntity.isSceneChild && !gameEntity.hasComponent<RigidActorComponent>()
            override fun createComponent(target: GameEntity): CharacterControllerComponent = CharacterControllerComponent(target)
        }

        data object AddScriptComponent : ComponentAdder<BehaviorComponent>("Behavior") {
            override fun hasComponent(gameEntity: GameEntity) = false
            override fun accept(gameEntity: GameEntity) = true

            override fun addMenuItems(targetObjs: List<GameEntity>, parentMenu: SubMenuItem<List<GameEntity>>) {
                val behaviorClasses = KoolEditor.instance.loadedApp.value?.behaviorClasses?.values ?: emptyList()
                if (behaviorClasses.isNotEmpty()) {
                    parentMenu.subMenu(name) {
                        behaviorClasses.forEach { script ->
                            item(script.prettyName) { objs ->
                                objs
                                    .map { target ->
                                        val behaviorComp = BehaviorComponent(target, ComponentInfo(BehaviorComponentData(script.qualifiedName)))
                                        AddComponentAction(target.id, behaviorComp)
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