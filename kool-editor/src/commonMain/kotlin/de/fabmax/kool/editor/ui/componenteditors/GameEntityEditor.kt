package de.fabmax.kool.editor.ui.componenteditors

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.actions.*
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.api.project
import de.fabmax.kool.editor.components.*
import de.fabmax.kool.editor.data.BehaviorComponentData
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.ui.*
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ui2.*
import kotlin.math.roundToInt

class GameEntityEditor(ui: EditorUi) :
    EditorPanel(
        name = "Object Properties",
        icon = Icons.medium.properties,
        ui = ui,
        defaultWidth = ui.dock.dockingSurface.sizes.baseSize * 9
    )
{

    val panelCollapseStates = mutableMapOf<EntityId, MutableMap<String, Boolean>>()

    override val windowSurface: UiSurface = editorPanelWithPanelBar {
        val selObjs = KoolEditor.instance.selectionOverlay.selectionState.use().toMutableList()

        val allMaterials = selObjs.isNotEmpty() && selObjs.all { it.scene == it.project.materialScene}
        if (!allMaterials) {
            selObjs.removeAll { it.scene == it.project.materialScene }
        }

        if (selObjs.size > 1) {
            selObjs.removeAll { it.isSceneRoot }
        } else if (selObjs.isEmpty()) {
            editor.activeScene.value?.let { selObjs += it.sceneEntity }
        }

        val title = when {
            selObjs.size == 1 && selObjs[0].isSceneRoot -> "Scene Settings"
            allMaterials -> "Material Settings"
            else -> "Entity Settings"
        }

        Column(Grow.Std, Grow.Std) {
            editorTitleBar(windowDockable, icon, title)
            if (allMaterials) {
                materialProperties(selObjs)
            } else {
                objectProperties(selObjs)
            }
        }
    }

    private fun UiScope.materialProperties(objects: List<GameEntity>) = ScrollArea(
        containerModifier = { it.backgroundColor(null) },
        vScrollbarModifier = defaultScrollbarModifierV(),
        isScrollableHorizontal = false
    ) {
        modifier.width(Grow.Std)

        val scopeName = objects.joinToString("") { "${it.id}" }
        Column(Grow.Std, Grow.Std, scopeName = scopeName) {
            val materialEntities = objects.filter { it.hasComponent<MaterialComponent>() }
            componentEditor(materialEntities) { MaterialComponentEditor() }
        }
    }

    private fun UiScope.objectProperties(objects: List<GameEntity>) = ScrollArea(
        containerModifier = { it.backgroundColor(null) },
        vScrollbarModifier = defaultScrollbarModifierV(),
        isScrollableHorizontal = false
    ) {
        modifier.width(Grow.Std)

        val scopeName = objects.joinToString("") { "${it.id}" }
        Column(Grow.Std, Grow.Std, scopeName = scopeName) {
            if (objects.size == 1 && objects[0].isSceneRoot) {
                objectName(objects[0])
            } else if (objects.isEmpty()) {
                Text("Nothing selected") {
                    modifier
                        .size(Grow.Std, sizes.baseSize)
                        .alignY(AlignmentY.Center)
                        .textAlignX(AlignmentX.Center)
                        .font(sizes.italicText)
                }
                return@Column
            }

            if (objects.none { it.isSceneRoot }) {
                entitySettings(objects)
            }

            componentEditors(objects)
            addComponentSelector(objects)
        }
    }

    private fun ColumnScope.objectName(obj: GameEntity) = Row(Grow.Std, sizes.lineHeightLarge) {
        modifier.padding(horizontal = sizes.gap)

        Text("Name:") {
            modifier
                .alignY(AlignmentY.Center)
                .margin(end = sizes.largeGap)
                .width(sizes.baseSize * 2.75f)
        }

        var editName by remember(obj.name)
        TextField(editName) {
            if (!isFocused.use()) {
                editName = obj.name
            }
            defaultTextfieldStyle()
            modifier
                .hint("Name")
                .width(Grow.Std)
                .alignY(AlignmentY.Center)
                .padding(vertical = sizes.smallGap)
                .onChange { editName = it }
                .onEnterPressed {
                    RenameEntityAction(obj.id, it, obj.name).apply()
                    surface.unfocus(this)
                }
        }
    }

    private fun ColumnScope.entitySettings(objects: List<GameEntity>) = entityEditorPanel(
        title = "Object",
        imageIcon = Icons.small.emptyObject,
        titleWidth = sizes.baseSize * 2.3f,
        startExpanded = objects[0].getPanelState("entitySettings", false),
        onCollapseChanged = { objects[0].setPanelState("entitySettings", it) },
        headerContent = {
            if (objects.size == 1) {
                var editName by remember(objects[0].name)
                TextField(editName) {
                    if (!isFocused.use()) {
                        editName = objects[0].name
                    }
                    defaultTextfieldStyle()
                    modifier
                        .hint("Name")
                        .width(Grow.Std)
                        .margin(end = sizes.gap)
                        .alignY(AlignmentY.Center)
                        .padding(vertical = sizes.smallGap)
                        .onChange { editName = it }
                        .onEnterPressed {
                            RenameEntityAction(objects[0].id, it, objects[0].name).apply()
                            surface.unfocus(this)
                        }
                }
            } else {
                Text("${objects.size} selected") {
                    modifier
                        .width(Grow.Std)
                        .margin(end = sizes.gap)
                        .alignY(AlignmentY.Center)
                        .textAlignX(AlignmentX.End)
                        .font(sizes.italicText)
                }
            }
        }
    ) {
        val isVisible = objects.all { it.isVisible }
        labeledCheckbox("Visible:", isVisible) {
            SetVisibilityAction(objects, it).apply()
        }

        val drawGroupId = if (objects.any { it.drawGroupId != objects[0].drawGroupId }) Double.NaN else {
            objects[0].drawGroupId.toDouble()
        }
        labeledDoubleTextField("Draw group:", drawGroupId, precision = 0) {
            SetDrawGroupAction(objects, it.roundToInt()).apply()
        }
    }

    private fun ColumnScope.componentEditors(objects: List<GameEntity>) {
        val primary = objects[0]
        val componentTypes = buildSet {
            primary.components
                .filter { primary.isSceneChild || it !is TransformComponent }
                .forEach { add(it.componentType) }
            for (i in 1 until objects.size) {
                retainAll(objects[i].components.map { it.componentType }.toSet())
            }
        }

        primary.components
            .filter { it.componentType in componentTypes }
            .sortedBy { (it as? GameEntityDataComponent<*>)?.componentInfo?.displayOrder }
            .forEach { component ->
                when (component) {
                    is CameraComponent -> componentEditor(objects) { CameraEditor() }
                    is DiscreteLightComponent -> componentEditor(objects) { LightEditor() }
                    is MaterialReferenceComponent -> componentEditor(objects) { MaterialReferenceEditor() }
                    is MaterialComponent -> componentEditor(objects) { MaterialComponentEditor() }
                    is MeshComponent -> componentEditor(objects) { MeshEditor() }
                    is SceneComponent -> componentEditor(objects) { ScenePropertiesEditor() }
                    is SceneBackgroundComponent -> componentEditor(objects) { SceneBackgroundEditor() }
                    is ShadowMapComponent -> componentEditor(objects) { ShadowMapEditor() }
                    is SsaoComponent -> componentEditor(objects) { SsaoEditor() }
                    is TransformComponent -> componentEditor(objects) { TransformEditor() }
                    is PhysicsWorldComponent -> componentEditor(objects) { PhysicsWorldEditor() }
                    is RigidActorComponent -> componentEditor(objects) { RigidActorEditor() }
                    is CharacterControllerComponent -> componentEditor(objects) { CharacterControllerEditor() }
                    is JointComponent -> componentEditor(objects) { JointEditor() }
                    is BehaviorComponent -> behaviorComponentEditor(objects, component.data.behaviorClassName)
                }
            }
    }

    private inline fun <reified T: GameEntityDataComponent<*>> UiScope.componentEditor(gameEntities: List<GameEntity>, editorProvider: () -> ComponentEditor<T>) {
        Box(width = Grow.Std, scopeName = gameEntities[0].requireComponent<T>().componentType) {
            val editor = remember(editorProvider)
            editor.components = gameEntities.map { it.requireComponent() }
            editor.entityEditor = this@GameEntityEditor
            editor()
        }
    }

    private fun UiScope.behaviorComponentEditor(gameEntities: List<GameEntity>, behaviorClassName: String) {
        val components = gameEntities.map {
            it.getComponents<BehaviorComponent>().first { c -> c.data.behaviorClassName == behaviorClassName }
        }
        Box(width = Grow.Std, scopeName = components[0].componentType) {
            val editor = remember { BehaviorEditor() }
            editor.components = components
            editor.entityEditor = this@GameEntityEditor
            editor()
        }
    }

    private fun UiScope.addComponentSelector(objects: List<GameEntity>) {
        val popup = remember { ContextPopupMenu<List<GameEntity>>("add-component") }
        var popupPos by remember(Vec2f.ZERO)

        val button = iconTextButton(
            icon = Icons.small.plus,
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

    private fun GameEntity.getPanelState(panelKey: String, default: Boolean = true): Boolean {
        return panelCollapseStates.getOrElse(id) { emptyMap() }.getOrElse(panelKey) { default }
    }

    private fun GameEntity.setPanelState(panelKey: String, state: Boolean) {
        panelCollapseStates.getOrPut(id) { mutableMapOf() }[panelKey] = state
    }

    companion object {
        private val addComponentOptions = listOf(
            ComponentAdder.AddMeshComponent,
            ComponentAdder.AddMaterialComponent,
            ComponentAdder.AddLightComponent,
            ComponentAdder.AddShadowMapComponent,
            ComponentAdder.AddBehaviorComponent,
            ComponentAdder.AddSsaoComponent,
            ComponentAdder.AddCameraComponent,
            ComponentAdder.AddPhysicsWorldComponent,
            ComponentAdder.AddRigidActorComponent,
            ComponentAdder.AddCharacterControllerComponent,
            ComponentAdder.AddJointComponent
        ).sortedBy { it.name }
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
            override fun hasComponent(gameEntity: GameEntity) = gameEntity.hasComponent<CameraComponent>()
            override fun accept(gameEntity: GameEntity) = gameEntity.isSceneChild
            override fun createComponent(target: GameEntity): CameraComponent = CameraComponent(target)
        }


        data object AddLightComponent : ComponentAdder<DiscreteLightComponent>("Light") {
            override fun hasComponent(gameEntity: GameEntity) = gameEntity.hasComponent<DiscreteLightComponent>()
            override fun accept(gameEntity: GameEntity) = gameEntity.isSceneChild
            override fun createComponent(target: GameEntity): DiscreteLightComponent = DiscreteLightComponent(target)
        }

        data object AddShadowMapComponent : ComponentAdder<ShadowMapComponent>("Shadow") {
            override fun hasComponent(gameEntity: GameEntity) = gameEntity.hasComponent<ShadowMapComponent>()
            override fun accept(gameEntity: GameEntity) = gameEntity.hasComponent<DiscreteLightComponent>()
            override fun createComponent(target: GameEntity): ShadowMapComponent = ShadowMapComponent(target)
        }

        data object AddMeshComponent : ComponentAdder<MeshComponent>("Mesh") {
            override fun hasComponent(gameEntity: GameEntity) = gameEntity.hasComponent<MeshComponent>()
            override fun accept(gameEntity: GameEntity) = gameEntity.isSceneChild
            override fun createComponent(target: GameEntity): MeshComponent = MeshComponent(target)
        }

        data object AddMaterialComponent : ComponentAdder<MaterialReferenceComponent>("Material") {
            override fun hasComponent(gameEntity: GameEntity) = gameEntity.hasComponent<MaterialReferenceComponent>()
            override fun accept(gameEntity: GameEntity) = gameEntity.hasComponent<MeshComponent>()
            override fun createComponent(target: GameEntity): MaterialReferenceComponent = MaterialReferenceComponent(target)
        }

        data object AddPhysicsWorldComponent : ComponentAdder<PhysicsWorldComponent>("Physics World") {
            override fun hasComponent(gameEntity: GameEntity) = gameEntity.hasComponent<PhysicsWorldComponent>()
            override fun accept(gameEntity: GameEntity) = !gameEntity.hasComponent<PhysicsComponent>()
            override fun createComponent(target: GameEntity): PhysicsWorldComponent = PhysicsWorldComponent(target)
        }

        data object AddRigidActorComponent : ComponentAdder<RigidActorComponent>("Rigid Actor") {
            override fun hasComponent(gameEntity: GameEntity) = gameEntity.hasComponent<RigidActorComponent>()
            override fun accept(gameEntity: GameEntity) = gameEntity.isSceneChild && !gameEntity.hasComponent<PhysicsComponent>()
            override fun createComponent(target: GameEntity): RigidActorComponent = RigidActorComponent(target)
        }

        data object AddCharacterControllerComponent : ComponentAdder<CharacterControllerComponent>("Character Controller") {
            override fun hasComponent(gameEntity: GameEntity) = gameEntity.hasComponent<CharacterControllerComponent>()
            override fun accept(gameEntity: GameEntity) = gameEntity.isSceneChild && !gameEntity.hasComponent<PhysicsComponent>()
            override fun createComponent(target: GameEntity): CharacterControllerComponent = CharacterControllerComponent(target)
        }

        data object AddJointComponent : ComponentAdder<JointComponent>("Joint") {
            override fun hasComponent(gameEntity: GameEntity) = gameEntity.hasComponent<JointComponent>()
            override fun accept(gameEntity: GameEntity) = gameEntity.isSceneChild &&
                    !gameEntity.hasComponent<JointComponent>() && !gameEntity.hasComponent<PhysicsComponent>()
            override fun createComponent(target: GameEntity): JointComponent = JointComponent(target)
        }

        data object AddBehaviorComponent : ComponentAdder<BehaviorComponent>("Behavior") {
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