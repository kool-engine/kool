package de.fabmax.kool.editor.ui.componenteditors

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.actions.EditorAction
import de.fabmax.kool.editor.actions.FusedAction
import de.fabmax.kool.editor.actions.SetComponentDataAction
import de.fabmax.kool.editor.actions.fused
import de.fabmax.kool.editor.components.*
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.editor.ui.*
import de.fabmax.kool.math.Vec2d
import de.fabmax.kool.math.Vec3d
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.physics.character.HitActorBehavior

class RigidActorEditor : ComponentEditor<RigidActorComponent>() {

    private val physicsWorldComponent: PhysicsWorldComponent? get() = component.getPhysicsWorldComponent(component.gameEntity.scene)

    override fun UiScope.compose() = componentPanel(
        title = "Rigid Actor",
        imageIcon = Icons.small.physics,
        onRemove = ::removeComponent,
    ) {
        modifier.padding(0.dp)

        val (typeItems, typeIdx) = typeOptions.getOptionsAndIndex(components.map { it.dataState.use().typeOption })
        labeledCombobox(
            label = "Type:",
            items = typeItems,
            selectedIndex = typeIdx,
            labelWidth = sizes.editorLabelWidthMedium
        ) { selected ->
            selected.item?.type?.let { actorType ->
                components.map {
                    val isTrigger = if (actorType == RigidActorType.STATIC) it.data.isTrigger else false
                    SetComponentDataAction(it, it.data, it.data.copy(actorType = actorType, isTrigger = isTrigger))
                }.fused().apply()
            }
        }

        val isDynamicActor = components.any { it.data.actorType == RigidActorType.DYNAMIC }
        val isKinematicActor = components.any { it.data.actorType == RigidActorType.KINEMATIC }
        shapeEditor(if (isDynamicActor) shapeOptionsDynamic else shapeOptions)

        if (isDynamicActor || isKinematicActor) {
            choicePropertyEditor(
                choices = charHitOptions,
                dataGetter = { it.data },
                valueGetter = { it.characterControllerHitBehavior },
                valueSetter = { oldData, newValue -> oldData.copy(characterControllerHitBehavior = newValue) },
                actionMapper = setActorActionMapper,
                label = "Controller hit behavior:",
                labelWidth = sizes.editorLabelWidthMedium
            )
        }
        if (isDynamicActor) {
            actorDoubleEditor(
                valueGetter = { it.mass },
                valueSetter = { oldData, newValue -> oldData.copy(mass = newValue) },
                label = "Mass:",
                minValue = 0.001,
            )
        } else {
            booleanPropertyEditor(
                dataGetter = { it.data },
                valueGetter = { it.isTrigger },
                valueSetter = { oldData, newValue -> oldData.copy(isTrigger = newValue) },
                actionMapper = setActorActionMapper,
                label = "Is trigger:",
            )
        }

        materialEditor()
    }

    private fun ColumnScope.materialEditor() {
        val allTheSameMaterial = components.all { it.dataState.use().materialId == components[0].data.materialId }
        collapsablePanelLvl2(
            title = "Material",
            startExpanded = false,
            titleWidth = sizes.editorLabelWidthSmall - sizes.gap * 2f,
            headerContent = {
                val (items, idx) = makeMaterialItemsAndIndex(allTheSameMaterial)
                ComboBox {
                    defaultComboBoxStyle()
                    modifier
                        .margin(end = sizes.gap)
                        .width(Grow.Std)
                        .alignY(AlignmentY.Center)
                        .items(items)
                        .selectedIndex(idx)
                        .onItemSelected { index ->
                            if (allTheSameMaterial || index > 0) {
                                val mat = items[index]
                                val matId = when {
                                    mat.physicsMaterial != null -> mat.physicsMaterial.id
                                    mat.itemText == "New material" -> createNewMaterial()
                                    else -> EntityId.NULL
                                }
                                val actions = components.map { SetComponentDataAction(it, it.data, it.data.copy(materialId = matId)) }
                                if (actions.isNotEmpty()) {
                                    actions.fused().apply()
                                }
                            }
                        }
                }
            }
        ) {
            if (allTheSameMaterial) {
                materialProperties()
            }
        }
    }

    private fun ColumnScope.materialProperties() {
        physicsWorldComponent?.let { world ->
            val materialState = remember {
                mutableStateOf(world.dataState.use().materials.find { it.id == component.data.materialId })
                    .onChange { _, newData -> newData?.let { makeMaterialUpdateAction(it).apply() } }
            }
            materialState.set(world.dataState.use().materials.find { it.id == component.data.materialId })
            val mat = materialState.use()
            if (mat != null) {
                labeledTextField("Name:", mat.name, labelWidth = sizes.editorLabelWidthSmall) {
                    materialState.set(materialState.value!!.copy(name = it))
                }
                labeledDoubleTextField(
                    label = "Static friction:",
                    value = mat.staticFriction.toDouble(),
                    dragChangeSpeed = DragChangeRates.RANGE_0_TO_1,
                    minValue = 0.0,
                    editHandler = ActionValueEditHandler { undoValue, applyValue ->
                        val undoMat = materialState.value!!.copy(staticFriction = undoValue.toFloat())
                        val applyMat = materialState.value!!.copy(staticFriction = applyValue.toFloat())
                        makeMaterialUpdateAction(applyMat, undoMat)
                    }
                )
                labeledDoubleTextField(
                    label = "Dynamic friction:",
                    value = mat.dynamicFriction.toDouble(),
                    dragChangeSpeed = DragChangeRates.RANGE_0_TO_1,
                    minValue = 0.0,
                    editHandler = ActionValueEditHandler { undoValue, applyValue ->
                        val undoMat = materialState.value!!.copy(dynamicFriction = undoValue.toFloat())
                        val applyMat = materialState.value!!.copy(dynamicFriction = applyValue.toFloat())
                        makeMaterialUpdateAction(applyMat, undoMat)
                    }
                )
                labeledDoubleTextField(
                    label = "Restitution:",
                    value = mat.restitution.toDouble(),
                    dragChangeSpeed = DragChangeRates.RANGE_0_TO_1,
                    minValue = 0.0,
                    maxValue = 1.0,
                    editHandler = ActionValueEditHandler { undoValue, applyValue ->
                        val undoMat = materialState.value!!.copy(restitution = undoValue.toFloat())
                        val applyMat = materialState.value!!.copy(restitution = applyValue.toFloat())
                        makeMaterialUpdateAction(applyMat, undoMat)
                    }
                )
            }
        }
    }

    private fun makeMaterialUpdateAction(data: PhysicsMaterialData, undoData: PhysicsMaterialData? = null) = with(component) {
        val world = checkNotNull(physicsWorldComponent)
        val undo = if (undoData != null) {
            val undoMats = world.data.materials.filter { it.id != undoData.id } + undoData
            world.data.copy(materials = undoMats)
        } else {
            world.data
        }
        val newMats = world.data.materials.filter { it.id != data.id } + data
        SetComponentDataAction(world, undo, world.data.copy(materials = newMats))
    }

    private fun createNewMaterial(): EntityId {
        with(component) {
            physicsWorldComponent?.let { world ->
                val matId = project.nextId()
                val matData = PhysicsMaterialData(matId, "Material-${matId.value}")
                SetComponentDataAction(world, world.data, world.data.copy(materials = world.data.materials + matData)).apply()
                return matId
            }
        }
        return EntityId.NULL
    }

    private fun UiScope.makeMaterialItemsAndIndex(allTheSameMaterial: Boolean): Pair<List<MaterialItem>, Int> {
        val items = mutableListOf(
            MaterialItem("Default", null),
            MaterialItem("New material", null)
        )
        if (!allTheSameMaterial) {
            items.add(0, MaterialItem("", null))
        }

        var index = 0
        with(component) {
            physicsWorldComponent?.let { world ->
                world.dataState.use().materials.forEachIndexed { i, material ->
                    if (allTheSameMaterial && components[0].data.materialId == material.id) {
                        index = i + 2
                    }
                    items += MaterialItem(material.name, world.materials[material.id])
                }
            }
        }
        return items to index
    }

    private fun ColumnScope.shapeEditor(choices: ComboBoxItems<ShapeOption>) {
        menuDivider()

        val (shapeItems, shapeIdx) = choices.getOptionsAndIndex(components.map { it.data.shapeOption })
        labeledCombobox(
            label = "Shape:",
            items = shapeItems,
            selectedIndex = shapeIdx,
            labelWidth = sizes.editorLabelWidthMedium
        ) { selected -> selected.item?.let { applyNewShape(it) } }

        if (components.all { it.data.shapes == components[0].data.shapes }) {
            when (components[0].data.shapes.firstOrNull()) {
                is ShapeData.Box -> boxShapeEditor()
                is ShapeData.Capsule -> capsuleEditor()
                is ShapeData.Cylinder -> cylinderEditor()
                is ShapeData.Sphere -> sphereEditor()
                is ShapeData.Heightmap -> heightmapEditor()
                is ShapeData.Rect -> { }    // todo: triangle mesh geometry
                else -> { }
            }
        }
    }

    private fun applyNewShape(shape: ShapeOption) {
        val newShapes = when (shape) {
            ShapeOption.DrawShape -> emptyList()
            ShapeOption.Box -> listOf(ShapeData.defaultBox)
            ShapeOption.Sphere -> listOf(ShapeData.defaultSphere)
            ShapeOption.Cylinder -> listOf(ShapeData.defaultCylinder)
            ShapeOption.Capsule -> listOf(ShapeData.defaultCapsule)
            ShapeOption.Heightmap -> listOf(ShapeData.defaultHeightmap)
            ShapeOption.Plane -> listOf(ShapeData.Plane)
        }
        val actions = components
            .filter { it.data.shapes != newShapes }
            .map { SetComponentDataAction(it, it.data, it.data.copy(shapes = newShapes)) }
        if (actions.isNotEmpty()) {
            FusedAction(actions).apply()
        }
    }

    private inline fun <reified T: ShapeData> getShapes(): List<T> {
        return components.map { (it.data.shapes[0] as T) }
    }

    private inline fun <reified T: ShapeData> List<ShapeData>.getShape(): T {
        return this[0] as T
    }

    private fun ColumnScope.boxShapeEditor() {
        actorVec3Editor(
            valueGetter = { it.shapes.getShape<ShapeData.Box>().size.toVec3d() },
            valueSetter = { oldData, newValue ->
                oldData.copy(shapes = listOf(oldData.shapes.getShape<ShapeData.Box>().copy(size = Vec3Data(newValue))))
            },
            label = "Size:",
            minValues = Vec3d(0.01, 0.01, 0.01)
        )
    }

    private fun ColumnScope.capsuleEditor() {
        actorDoubleEditor(
            valueGetter = { it.shapes.getShape<ShapeData.Capsule>().radius },
            valueSetter = { oldData, newValue ->
                oldData.copy(shapes = listOf(oldData.shapes.getShape<ShapeData.Capsule>().copy(radius = newValue)))
            },
            label = "Radius:",
            minValue = 0.01
        )
        actorDoubleEditor(
            valueGetter = { it.shapes.getShape<ShapeData.Capsule>().length },
            valueSetter = { oldData, newValue ->
                oldData.copy(shapes = listOf(oldData.shapes.getShape<ShapeData.Capsule>().copy(length = newValue)))
            },
            label = "Length:",
            minValue = 0.0
        )
    }

    private fun ColumnScope.cylinderEditor() {
        actorDoubleEditor(
            valueGetter = { it.shapes.getShape<ShapeData.Cylinder>().topRadius },
            valueSetter = { oldData, newValue ->
                oldData.copy(shapes = listOf(oldData.shapes.getShape<ShapeData.Cylinder>().copy(bottomRadius = newValue, topRadius = newValue)))
            },
            label = "Radius:",
            minValue = 0.01
        )
        actorDoubleEditor(
            valueGetter = { it.shapes.getShape<ShapeData.Cylinder>().length },
            valueSetter = { oldData, newValue ->
                oldData.copy(shapes = listOf(oldData.shapes.getShape<ShapeData.Cylinder>().copy(length = newValue)))
            },
            label = "Length:",
            minValue = 0.01
        )
    }

    private fun ColumnScope.sphereEditor() {
        actorDoubleEditor(
            valueGetter = { it.shapes.getShape<ShapeData.Sphere>().radius },
            valueSetter = { oldData, newValue ->
                oldData.copy(shapes = listOf(oldData.shapes.getShape<ShapeData.Sphere>().copy(radius = newValue)))
            },
            label = "Radius:",
            minValue = 0.01
        )
    }

    private fun ColumnScope.heightmapEditor() {
        val heightmaps = getShapes<ShapeData.Heightmap>()
        val mapPath = if (heightmaps.all { it.mapPath == heightmaps[0].mapPath }) heightmaps[0].mapPath else ""
        heightmapSelector(mapPath, true) {
            val editMaps = getShapes<ShapeData.Heightmap>()
            components.mapIndexed { i, component ->
                val bodyProps = component.data
                val applyShape = bodyProps.copy(shapes = listOf(editMaps[i].copy(mapPath = it?.path ?: "")))
                SetComponentDataAction(component, bodyProps, applyShape)
            }.fused().apply()
        }

        val loaded = heightmaps.map {
            it.toAssetRef()?.let { ref -> KoolEditor.instance.cachedAppAssets.getHeightmapMutableState(ref).use() }
        }
        val sizeX = condenseDouble(heightmaps.mapIndexed { i, heightmap ->
            val numCols = loaded[i]?.columns ?: MeshComponent.DEFAULT_HEIGHTMAP_COLS
            (numCols - 1) * heightmap.colScale
        })
        val sizeY = condenseDouble(heightmaps.mapIndexed { i, heightmap ->
            val numRows = loaded[i]?.rows ?: MeshComponent.DEFAULT_HEIGHTMAP_ROWS
            (numRows - 1) * heightmap.rowScale
        })

        labeledDoubleTextField(
            label = "Height scale:",
            value = condenseDouble(heightmaps.map { it.heightScale }),
            minValue = 0.0,
            editHandler = object: ValueEditHandler<Double> {
                override fun onEdit(value: Double) { }
                override fun onEditEnd(startValue: Double, endValue: Double) {
                    val editMaps = getShapes<ShapeData.Heightmap>()
                    components.mapIndexed { i, component ->
                        val bodyProps = component.data
                        val applyShape = bodyProps.copy(shapes = listOf(editMaps[i].copy(heightScale = endValue)))
                        SetComponentDataAction(component, bodyProps, applyShape)
                    }.fused().apply()
                }
            },
        )
        labeledXyRow(
            label = "Size:",
            xy = Vec2d(sizeX, sizeY),
            minValues = Vec2d.ZERO,
            editHandler = object: ValueEditHandler<Vec2d> {
                override fun onEdit(value: Vec2d) { }
                override fun onEditEnd(startValue: Vec2d, endValue: Vec2d) {
                    val editMaps = getShapes<ShapeData.Heightmap>()
                    components.mapIndexed { i, component ->
                        val numCols = loaded[i]?.columns ?: MeshComponent.DEFAULT_HEIGHTMAP_COLS
                        val numRows = loaded[i]?.rows ?: MeshComponent.DEFAULT_HEIGHTMAP_ROWS
                        val newScale = endValue / Vec2d(numCols -1.0, numRows - 1.0)
                        val bodyProps = component.data
                        val applyShape = bodyProps.copy(shapes = listOf(editMaps[i].copy(colScale = newScale.x, rowScale = newScale.y)))
                        SetComponentDataAction(component, bodyProps, applyShape)
                    }.fused().apply()
                }
            }
        )
    }

    private fun ColumnScope.actorDoubleEditor(
        valueGetter: (RigidActorComponentData) -> Double,
        valueSetter: (oldData: RigidActorComponentData, newValue: Double) -> RigidActorComponentData,
        label: String,
        precision: (Double) -> Int = { precisionForValue(it) },
        minValue: Double = Double.NEGATIVE_INFINITY,
        maxValue: Double = Double.POSITIVE_INFINITY,
    ) = doublePropertyEditor(
        dataGetter = { it.data },
        valueGetter = valueGetter,
        valueSetter = valueSetter,
        actionMapper = setActorActionMapper,
        label = label,
        precision = precision,
        minValue = minValue,
        maxValue = maxValue
    )

    private fun ColumnScope.actorVec3Editor(
        valueGetter: (RigidActorComponentData) -> Vec3d,
        valueSetter: (oldData: RigidActorComponentData, newValue: Vec3d) -> RigidActorComponentData,
        label: String,
        minValues: Vec3d? = null,
        maxValues: Vec3d? = null,
    ) = vec3dPropertyEditor(
        dataGetter = { it.data },
        valueGetter = valueGetter,
        valueSetter = valueSetter,
        actionMapper = setActorActionMapper,
        label = label,
        minValues = minValues,
        maxValues = maxValues
    )

    private val RigidActorComponentData.shapeOption: ShapeOption
        get() = ShapeOption.entries.first { it.matches(shapes.firstOrNull()) }

    private val RigidActorComponentData.typeOption: TypeOption
        get() = TypeOption.entries.first { it.type == actorType }

    private enum class TypeOption(val label: String, val type: RigidActorType) {
        Dynamic("Dynamic", RigidActorType.DYNAMIC),
        Kinematic("Kinematic", RigidActorType.KINEMATIC),
        Static("Static", RigidActorType.STATIC),
    }

    private enum class ShapeOption(val label: String, val matches: (ShapeData?) -> Boolean, val isDynamic: Boolean = true) {
        DrawShape("Draw shape", { it == null || it is ShapeData.Custom }),
        Box("Box", { it is ShapeData.Box }),
        Sphere("Sphere", { it is ShapeData.Sphere }),
        Cylinder("Cylinder", { it is ShapeData.Cylinder }),
        Capsule("Capsule", { it is ShapeData.Capsule }),
        Plane("Infinite plane", { it is ShapeData.Plane }, isDynamic = false),
        Heightmap("Heightmap", { it is ShapeData.Heightmap }, isDynamic = false)
    }

    private class MaterialItem(val itemText: String, val physicsMaterial: PhysicsMaterial?) {
        override fun toString(): String = itemText
    }

    companion object {
        private val shapeOptions = ComboBoxItems(ShapeOption.entries) { it.label }
        private val shapeOptionsDynamic = ComboBoxItems(ShapeOption.entries.filter { it.isDynamic }) { it.label }
        private val typeOptions = ComboBoxItems(TypeOption.entries) { it.label }
        private val charHitOptions = ComboBoxItems(listOf(HitActorBehavior.SLIDE, HitActorBehavior.RIDE)) {
            when (it) {
                HitActorBehavior.DEFAULT -> "Default"
                HitActorBehavior.SLIDE -> "Slide"
                HitActorBehavior.RIDE -> "Ride"
            }
        }

        private val setActorActionMapper: (RigidActorComponent, RigidActorComponentData, RigidActorComponentData) -> EditorAction = { component, undoData, applyData ->
            SetComponentDataAction(component, undoData, applyData)
        }
    }
}