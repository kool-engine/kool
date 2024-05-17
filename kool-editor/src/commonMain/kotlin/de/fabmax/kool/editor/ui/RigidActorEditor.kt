package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.CachedAppAssets
import de.fabmax.kool.editor.actions.FusedAction
import de.fabmax.kool.editor.actions.SetRigidBodyPropertiesAction
import de.fabmax.kool.editor.api.AppAssets
import de.fabmax.kool.editor.components.MeshComponent
import de.fabmax.kool.editor.components.RigidActorComponent
import de.fabmax.kool.editor.components.toAssetReference
import de.fabmax.kool.editor.data.RigidActorProperties
import de.fabmax.kool.editor.data.RigidActorType
import de.fabmax.kool.editor.data.ShapeData
import de.fabmax.kool.editor.data.Vec3Data
import de.fabmax.kool.math.Vec2d
import de.fabmax.kool.modules.ui2.*

class RigidActorEditor : ComponentEditor<RigidActorComponent>() {

    override fun UiScope.compose() = componentPanel(
        title = "Rigid Actor",
        imageIcon = IconMap.small.physics,
        onRemove = ::removeComponent,
    ) {
        Column(width = Grow.Std) {
            modifier
                .padding(horizontal = sizes.gap)
                .margin(bottom = sizes.smallGap)

            val (typeItems, typeIdx) = typeOptions.getOptionsAndIndex(components.map { it.actorState.use().typeOption })
            labeledCombobox(
                label = "Type:",
                items = typeItems,
                selectedIndex = typeIdx
            ) { selected ->
                selected.item?.type?.let { actorType ->
                    val actions = components.map {
                        val bodyProps = it.actorState.value
                        SetRigidBodyPropertiesAction(it.nodeModel.nodeId, bodyProps, bodyProps.copy(type = actorType))
                    }
                    FusedAction(actions).apply()
                }
            }

            if (components.all { it.actorState.value.type == RigidActorType.DYNAMIC }) {
                labeledDoubleTextField(
                    label = "Mass:",
                    value = condenseDouble(components.map { it.actorState.value.mass }),
                    minValue = 0.001,
                    dragChangeSpeed = DragChangeRates.SIZE,
                    editHandler = ActionValueEditHandler { undo, apply ->
                        val actions = components.map {
                            val bodyProps = it.actorState.value
                            val mergedUndo = bodyProps.copy(mass = mergeDouble(undo, bodyProps.mass))
                            val mergedApply = bodyProps.copy(mass = mergeDouble(apply, bodyProps.mass))
                            SetRigidBodyPropertiesAction( it.nodeModel.nodeId, mergedUndo, mergedApply)
                        }
                        FusedAction(actions)
                    }
                )
            }

            shapeEditor()
        }
    }

    private fun ColumnScope.shapeEditor() {
        menuDivider()

        val (shapeItems, shapeIdx) = shapeOptions.getOptionsAndIndex(components.map { it.actorState.use().shapeOption })
        labeledCombobox(
            label = "Shape:",
            items = shapeItems,
            selectedIndex = shapeIdx
        ) { selected -> selected.item?.let { applyNewShape(it) } }

        if (components.all { it.actorState.value.shapes == components[0].actorState.value.shapes }) {
            when (components[0].actorState.value.shapes.firstOrNull()) {
                is ShapeData.Box -> boxShapeEditor()
                is ShapeData.Capsule -> capsuleEditor()
                is ShapeData.Cylinder -> cylinderEditor()
                is ShapeData.Sphere -> sphereEditor()
                is ShapeData.Heightmap -> heightmapEditor()
                else -> { }
            }
        }
    }

    private fun applyNewShape(shape: ShapeOption) {
        val newShapes = when (shape) {
            ShapeOption.UseMesh -> emptyList()
            ShapeOption.Box -> listOf(ShapeData.defaultBox)
            ShapeOption.Sphere -> listOf(ShapeData.defaultSphere)
            ShapeOption.Cylinder -> listOf(ShapeData.defaultCylinder)
            ShapeOption.Capsule -> listOf(ShapeData.defaultCapsule)
            ShapeOption.Heightmap -> listOf(ShapeData.defaultHeightmap)
        }
        val actions = components
            .filter { it.actorState.value.shapes != newShapes }
            .map {
                val props = it.actorState.value
                SetRigidBodyPropertiesAction(it.nodeModel.nodeId, props, props.copy(shapes = newShapes))
            }
        if (actions.isNotEmpty()) {
            FusedAction(actions).apply()
        }
    }

    private inline fun <reified T: ShapeData> getShapes(): List<T> {
        return components.map { (it.actorState.value.shapes[0] as T) }
    }

    private fun ColumnScope.boxShapeEditor() {
        labeledXyzRow(
            label = "Size:",
            xyz = condenseVec3(getShapes<ShapeData.Box>().map { it.size.toVec3d() }),
            dragChangeSpeed = DragChangeRates.SIZE_VEC3,
            editHandler = ActionValueEditHandler { undo, apply ->
                val props = components.map { it.actorState.value }
                val boxes = getShapes<ShapeData.Box>()
                val actions = components.mapIndexed { i, component ->
                    val size = boxes[i].size.toVec3d()
                    val mergedUndo = Vec3Data(mergeVec3(undo, size))
                    val mergedApply = Vec3Data(mergeVec3(apply, size))
                    val undoProps = props[i].copy(shapes = listOf(boxes[i].copy(size = mergedUndo)))
                    val applyProps = props[i].copy(shapes = listOf(boxes[i].copy(size = mergedApply)))
                    SetRigidBodyPropertiesAction(component.nodeModel.nodeId, undoProps, applyProps)
                }
                FusedAction(actions)
            }
        )
    }

    private fun ColumnScope.capsuleEditor() {
        labeledDoubleTextField(
            label = "Radius:",
            value = condenseDouble(getShapes<ShapeData.Capsule>().map { it.radius }),
            dragChangeSpeed = DragChangeRates.SIZE,
            editHandler = ActionValueEditHandler { undo, apply ->
                val props = components.map { it.actorState.value }
                val caps = getShapes<ShapeData.Capsule>()
                val actions = components.mapIndexed { i, component ->
                    val mergedUndo = mergeDouble(undo, caps[i].radius)
                    val mergedApply = mergeDouble(apply, caps[i].radius)
                    val undoProps = props[i].copy(shapes = listOf(caps[i].copy(radius = mergedUndo)))
                    val applyProps = props[i].copy(shapes = listOf(caps[i].copy(radius = mergedApply)))
                    SetRigidBodyPropertiesAction(component.nodeModel.nodeId, undoProps, applyProps)
                }
                FusedAction(actions)
            }
        )
        labeledDoubleTextField(
            label = "Length:",
            value = condenseDouble(getShapes<ShapeData.Capsule>().map { it.length }),
            dragChangeSpeed = DragChangeRates.SIZE,
            editHandler = ActionValueEditHandler { undo, apply ->
                val props = components.map { it.actorState.value }
                val caps = getShapes<ShapeData.Capsule>()
                val actions = components.mapIndexed { i, component ->
                    val mergedUndo = mergeDouble(undo, caps[i].length)
                    val mergedApply = mergeDouble(apply, caps[i].length)
                    val undoProps = props[i].copy(shapes = listOf(caps[i].copy(length = mergedUndo)))
                    val applyProps = props[i].copy(shapes = listOf(caps[i].copy(length = mergedApply)))
                    SetRigidBodyPropertiesAction(component.nodeModel.nodeId, undoProps, applyProps)
                }
                FusedAction(actions)
            }
        )
    }

    private fun ColumnScope.cylinderEditor() {
        labeledDoubleTextField(
            label = "Radius:",
            value = condenseDouble(getShapes<ShapeData.Cylinder>().map { it.topRadius }),
            dragChangeSpeed = DragChangeRates.SIZE,
            editHandler = ActionValueEditHandler { undo, apply ->
                val props = components.map { it.actorState.value }
                val caps = getShapes<ShapeData.Cylinder>()
                val actions = components.mapIndexed { i, component ->
                    val mergedUndo = mergeDouble(undo, caps[i].topRadius)
                    val mergedApply = mergeDouble(apply, caps[i].topRadius)
                    val undoProps = props[i].copy(shapes = listOf(caps[i].copy(topRadius = mergedUndo)))
                    val applyProps = props[i].copy(shapes = listOf(caps[i].copy(topRadius = mergedApply)))
                    SetRigidBodyPropertiesAction(component.nodeModel.nodeId, undoProps, applyProps)
                }
                FusedAction(actions)
            }
        )
        labeledDoubleTextField(
            label = "Length:",
            value = condenseDouble(getShapes<ShapeData.Cylinder>().map { it.length }),
            dragChangeSpeed = DragChangeRates.SIZE,
            editHandler = ActionValueEditHandler { undo, apply ->
                val props = components.map { it.actorState.value }
                val caps = getShapes<ShapeData.Cylinder>()
                val actions = components.mapIndexed { i, component ->
                    val mergedUndo = mergeDouble(undo, caps[i].length)
                    val mergedApply = mergeDouble(apply, caps[i].length)
                    val undoProps = props[i].copy(shapes = listOf(caps[i].copy(length = mergedUndo)))
                    val applyProps = props[i].copy(shapes = listOf(caps[i].copy(length = mergedApply)))
                    SetRigidBodyPropertiesAction(component.nodeModel.nodeId, undoProps, applyProps)
                }
                FusedAction(actions)
            }
        )
    }

    private fun ColumnScope.sphereEditor() {
        labeledDoubleTextField(
            label = "Radius:",
            value = condenseDouble(getShapes<ShapeData.Sphere>().map { it.radius }),
            dragChangeSpeed = DragChangeRates.SIZE,
            editHandler = ActionValueEditHandler { undo, apply ->
                val props = components.map { it.actorState.value }
                val caps = getShapes<ShapeData.Sphere>()
                val actions = components.mapIndexed { i, component ->
                    val mergedUndo = mergeDouble(undo, caps[i].radius)
                    val mergedApply = mergeDouble(apply, caps[i].radius)
                    val undoProps = props[i].copy(shapes = listOf(caps[i].copy(radius = mergedUndo)))
                    val applyProps = props[i].copy(shapes = listOf(caps[i].copy(radius = mergedApply)))
                    SetRigidBodyPropertiesAction(component.nodeModel.nodeId, undoProps, applyProps)
                }
                FusedAction(actions)
            }
        )
    }

    private fun ColumnScope.heightmapEditor() {
        val heightmaps = getShapes<ShapeData.Heightmap>()
        val mapPath = if (heightmaps.all { it.mapPath == heightmaps[0].mapPath }) heightmaps[0].mapPath else ""
        heightmapSelector(mapPath, true) {
            val editMaps = getShapes<ShapeData.Heightmap>()
            val actions = components.mapIndexed { i, component ->
                val bodyProps = component.actorState.value
                val applyShape = bodyProps.copy(shapes = listOf(editMaps[i].copy(mapPath = it?.path ?: "")))
                SetRigidBodyPropertiesAction(component.nodeModel.nodeId, bodyProps, applyShape)
            }
            FusedAction(actions).apply()
        }

        val loaded = heightmaps.map {
            (AppAssets.impl as CachedAppAssets).getHeightmapMutableState(it.toAssetReference()).use()
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
            editHandler = object: ValueEditHandler<Double> {
                override fun onEdit(value: Double) { }
                override fun onEditEnd(startValue: Double, endValue: Double) {
                    val editMaps = getShapes<ShapeData.Heightmap>()
                    val actions = components.mapIndexed { i, component ->
                        val bodyProps = component.actorState.value
                        val applyShape = bodyProps.copy(shapes = listOf(editMaps[i].copy(heightScale = endValue)))
                        SetRigidBodyPropertiesAction(component.nodeModel.nodeId, bodyProps, applyShape)
                    }
                    FusedAction(actions).apply()
                }
            }
        )
        labeledXyRow(
            label = "Size:",
            xy = Vec2d(sizeX, sizeY),
            editHandler = object: ValueEditHandler<Vec2d> {
                override fun onEdit(value: Vec2d) { }
                override fun onEditEnd(startValue: Vec2d, endValue: Vec2d) {
                    val editMaps = getShapes<ShapeData.Heightmap>()
                    val actions = components.mapIndexed { i, component ->
                        val numCols = loaded[i]?.columns ?: MeshComponent.DEFAULT_HEIGHTMAP_COLS
                        val numRows = loaded[i]?.rows ?: MeshComponent.DEFAULT_HEIGHTMAP_ROWS
                        val newScale = endValue / Vec2d(numCols -1.0, numRows - 1.0)
                        val bodyProps = component.actorState.value
                        val applyShape = bodyProps.copy(shapes = listOf(editMaps[i].copy(colScale = newScale.x, rowScale = newScale.y)))
                        SetRigidBodyPropertiesAction(component.nodeModel.nodeId, bodyProps, applyShape)
                    }
                    FusedAction(actions).apply()
                }
            }
        )
    }

    private val RigidActorProperties.shapeOption: ShapeOption get() =
        ShapeOption.entries.first { it.matches(shapes.firstOrNull()) }

    private val RigidActorProperties.typeOption: TypeOption get() =
        TypeOption.entries.first { it.type == type }

    private enum class TypeOption(val label: String, val type: RigidActorType) {
        Dynamic("Dynamic", RigidActorType.DYNAMIC),
        Kinematic("Kinematic", RigidActorType.KINEMATIC),
        Static("Static", RigidActorType.STATIC),
    }

    private enum class ShapeOption(val label: String, val matches: (ShapeData?) -> Boolean) {
        UseMesh("Use mesh", { it == null }),
        Box("Box", { it is ShapeData.Box }),
        Sphere("Sphere", { it is ShapeData.Sphere }),
        Cylinder("Cylinder", { it is ShapeData.Cylinder }),
        Capsule("Capsule", { it is ShapeData.Capsule }),
        Heightmap("Heightmap", { it is ShapeData.Heightmap })
    }

    companion object {
        private val shapeOptions = ComboBoxItems(ShapeOption.entries) { it.label }
        private val typeOptions = ComboBoxItems(TypeOption.entries) { it.label }
    }
}