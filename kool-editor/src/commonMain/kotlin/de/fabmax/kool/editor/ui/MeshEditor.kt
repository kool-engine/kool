package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.CachedAppAssets
import de.fabmax.kool.editor.actions.FusedAction
import de.fabmax.kool.editor.actions.SetMeshShapeAction
import de.fabmax.kool.editor.api.AppAssets
import de.fabmax.kool.editor.components.MeshComponent
import de.fabmax.kool.editor.components.toAssetReference
import de.fabmax.kool.editor.data.ShapeData
import de.fabmax.kool.editor.data.Vec2Data
import de.fabmax.kool.editor.data.Vec3Data
import de.fabmax.kool.math.Vec2d
import de.fabmax.kool.modules.ui2.*
import kotlin.math.roundToInt

class MeshEditor : ComponentEditor<MeshComponent>() {

    override fun UiScope.compose() {
        val componentShapes = components.map { it.shapesState.use().first().shapeOption }
        val isAllTheSameShape = componentShapes.all { it == componentShapes[0] }
        val (shapeItems, shapeIdx) = shapeOptions.getOptionsAndIndex(componentShapes)

        componentPanel(
            title = "Mesh",
            imageIcon = IconMap.small.cube,
            onRemove = ::removeComponent,
            titleWidth = sizes.baseSize * 2.3f,

            headerContent = {
                ComboBox {
                    defaultComboBoxStyle()
                    modifier
                        .margin(horizontal = sizes.gap)
                        .width(Grow.Std)
                        .alignY(AlignmentY.Center)
                        .items(shapeItems)
                        .selectedIndex(shapeIdx)
                        .onItemSelected { index ->
                            shapeItems[index].item?.let { applyNewShape(it) }
                        }
                }
            }
        ) {
            if (isAllTheSameShape) {
                editorBody()
            }
        }
    }

    private fun applyNewShape(shape: ShapeOption) {
        val newShapes = when (shape) {
            ShapeOption.Box -> listOf(ShapeData.defaultBox)
            ShapeOption.Rect -> listOf(ShapeData.defaultRect)
            ShapeOption.Sphere -> listOf(ShapeData.defaultSphere)
            ShapeOption.Cylinder -> listOf(ShapeData.defaultCylinder)
            ShapeOption.Capsule -> listOf(ShapeData.defaultCylinder)
            ShapeOption.Heightmap -> listOf(ShapeData.defaultHeightmap)
            ShapeOption.Empty -> listOf(ShapeData.defaultEmpty)
        }
        val actions = components
            .filter { it.shapesState != newShapes }
            .map { SetMeshShapeAction(it, it.shapesState.first(), newShapes[0]) }
        if (actions.isNotEmpty()) {
            FusedAction(actions).apply()
        }
    }

    private fun ColumnScope.editorBody() {
        Column(width = Grow.Std) {
            modifier
                .padding(horizontal = sizes.gap)
                .margin(bottom = sizes.gap)

            val shape = components[0].shapesState.use()[0]
            when (shape) {
                is ShapeData.Box -> boxProperties()
                is ShapeData.Rect -> rectProperties()
                is ShapeData.Sphere -> sphereProperties()
                is ShapeData.Cylinder -> cylinderProperties()
                is ShapeData.Capsule -> capsuleProperties()
                is ShapeData.Heightmap -> heightmapProperties()
                is ShapeData.Empty -> { }
            }

            if (shape.hasUvs) {
                labeledXyRow(
                    label = "Texture scale:",
                    xy = condenseVec2(components.map { it.shapesState[0].common.uvScale.toVec2d() }),
                    dragChangeSpeed = DragChangeRates.SIZE_VEC2,
                    editHandler = ActionValueEditHandler { undo, apply ->
                        val shapes = components.map { it.shapesState[0] }
                        val actions = components.mapIndexed { i, component ->
                            val uv = shapes[i].common.uvScale.toVec2d()
                            val mergedUndo = shapes[i].copyShape(shapes[i].common.copy(uvScale = Vec2Data(mergeVec2(undo, uv))))
                            val mergedApply = shapes[i].copyShape(shapes[i].common.copy(uvScale = Vec2Data(mergeVec2(apply, uv))))
                            SetMeshShapeAction(component, mergedUndo, mergedApply)
                        }
                        FusedAction(actions)
                    }
                )
            }
        }
    }

    private inline fun <reified T: ShapeData> getShapes(): List<T> {
        return components.map { (it.shapesState[0] as T) }
    }

    private fun UiScope.boxProperties() = Column(
        width = Grow.Std,
        scopeName = "boxProperties"
    ) {
        labeledXyzRow(
            label = "Size:",
            xyz = condenseVec3(getShapes<ShapeData.Box>().map { it.size.toVec3d() }),
            dragChangeSpeed = DragChangeRates.SIZE_VEC3,
            editHandler = ActionValueEditHandler { undo, apply ->
                val boxes = getShapes<ShapeData.Box>()
                val actions = components.mapIndexed { i, component ->
                    val size = boxes[i].size.toVec3d()
                    val mergedUndo = Vec3Data(mergeVec3(undo, size))
                    val mergedApply = Vec3Data(mergeVec3(apply, size))
                    SetMeshShapeAction(component, boxes[i].copy(size = mergedUndo), boxes[i].copy(size = mergedApply))
                }
                FusedAction(actions)
            }
        )
    }

    private fun UiScope.rectProperties() = Column(
        width = Grow.Std,
        scopeName = "rectProperties"
    ) {
        labeledXyRow(
            label = "Size:",
            xy = condenseVec2(getShapes<ShapeData.Rect>().map { it.size.toVec2d() }),
            dragChangeSpeed = DragChangeRates.SIZE_VEC2,
            editHandler = ActionValueEditHandler { undo, apply ->
                val rects = getShapes<ShapeData.Rect>()
                val actions = components.mapIndexed { i, component ->
                    val size = rects[i].size.toVec2d()
                    val mergedUndo = Vec2Data(mergeVec2(undo, size))
                    val mergedApply = Vec2Data(mergeVec2(apply, size))
                    SetMeshShapeAction(component, rects[i].copy(size = mergedUndo), rects[i].copy(size = mergedApply))
                }
                FusedAction(actions)
            }
        )
    }

    private fun UiScope.sphereProperties() = Column(
        width = Grow.Std,
        scopeName = "sphereProperties"
    ) {
        val spheres = getShapes<ShapeData.Sphere>()
        labeledDoubleTextField(
            label = "Radius:",
            value = condenseDouble(spheres.map { it.radius }),
            dragChangeSpeed = DragChangeRates.SIZE,
            editHandler = ActionValueEditHandler { undo, apply ->
                val editSpheres = getShapes<ShapeData.Sphere>()
                val actions = components.mapIndexed { i, component ->
                    val mergedUndo = mergeDouble(undo, editSpheres[i].radius)
                    val mergedApply = mergeDouble(apply, editSpheres[i].radius)
                    SetMeshShapeAction(component, editSpheres[i].copy(radius = mergedUndo), editSpheres[i].copy(radius = mergedApply))
                }
                FusedAction(actions)
            }
        )
        val isIco = spheres.all { it.sphereType == "ico" }
        labeledCheckbox("Generate as ico-sphere", isIco) { setIco ->
            val newType = if (setIco) "ico" else "uv"
            val newSteps = if (setIco) 2 else 20
            val editSpheres = getShapes<ShapeData.Sphere>()
            val actions = components.mapIndexed { i, component ->
                SetMeshShapeAction(component, editSpheres[i], editSpheres[i].copy(steps = newSteps, sphereType = newType))
            }
            FusedAction(actions).apply()
        }
        if (isIco) {
            labeledDoubleTextField(
                label = "Sub-divisions:",
                value = condenseDouble(spheres.map { it.steps.toDouble() }),
                precision = 0,
                dragChangeSpeed = 0.02,
                minValue = 0.0,
                maxValue = 7.0,
                editHandler = ActionValueEditHandler { undo, apply ->
                    val editSpheres = getShapes<ShapeData.Sphere>()
                    val actions = components.mapIndexed { i, component ->
                        val mergedUndo = mergeDouble(undo, editSpheres[i].steps.toDouble()).roundToInt()
                        val mergedApply = mergeDouble(apply, editSpheres[i].steps.toDouble()).roundToInt()
                        SetMeshShapeAction(component, editSpheres[i].copy(steps = mergedUndo), editSpheres[i].copy(steps = mergedApply))
                    }
                    FusedAction(actions)
                }
            )
        } else {
            labeledDoubleTextField(
                label = "Steps:",
                value = condenseDouble(spheres.map { it.steps.toDouble() }),
                precision = 0,
                dragChangeSpeed = 0.1,
                minValue = 3.0,
                maxValue = 100.0,
                editHandler = ActionValueEditHandler { undo, apply ->
                    val editSpheres = getShapes<ShapeData.Sphere>()
                    val actions = components.mapIndexed { i, component ->
                        val mergedUndo = mergeDouble(undo, editSpheres[i].steps.toDouble()).roundToInt()
                        val mergedApply = mergeDouble(apply, editSpheres[i].steps.toDouble()).roundToInt()
                        SetMeshShapeAction(component, editSpheres[i].copy(steps = mergedUndo), editSpheres[i].copy(steps = mergedApply))
                    }
                    FusedAction(actions)
                }
            )
        }
    }

    private fun UiScope.cylinderProperties() = Column(
        width = Grow.Std,
        scopeName = "cylinderProperties"
    ) {
        val cylinders = getShapes<ShapeData.Cylinder>()
        var isUniRadius by remember(cylinders.all { it.topRadius == it.bottomRadius })
        labeledCheckbox("Uniform radius:", isUniRadius) {
            isUniRadius = it
            if (isUniRadius) {
                val editCyls = getShapes<ShapeData.Cylinder>()
                val actions = components.mapIndexed { i, component ->
                    SetMeshShapeAction(component, editCyls[i], editCyls[i].copy(topRadius = editCyls[i].bottomRadius))
                }
                FusedAction(actions).apply()
            }
        }
        if (isUniRadius) {
            labeledDoubleTextField(
                label = "Radius:",
                value = condenseDouble(cylinders.map { it.bottomRadius }),
                dragChangeSpeed = DragChangeRates.SIZE,
                editHandler = ActionValueEditHandler { undo, apply ->
                    val editCyls = getShapes<ShapeData.Cylinder>()
                    val actions = components.mapIndexed { i, component ->
                        val mergedUndo = mergeDouble(undo, editCyls[i].bottomRadius)
                        val mergedApply = mergeDouble(apply, editCyls[i].bottomRadius)
                        SetMeshShapeAction(
                            component,
                            editCyls[i].copy(bottomRadius = mergedUndo, topRadius = mergedUndo),
                            editCyls[i].copy(bottomRadius = mergedApply, topRadius = mergedApply)
                        )
                    }
                    FusedAction(actions)
                }
            )
        } else {
            labeledDoubleTextField(
                label = "Top-radius:",
                value = condenseDouble(cylinders.map { it.topRadius }),
                dragChangeSpeed = DragChangeRates.SIZE,
                editHandler = ActionValueEditHandler { undo, apply ->
                    val editCyls = getShapes<ShapeData.Cylinder>()
                    val actions = components.mapIndexed { i, component ->
                        val mergedUndo = mergeDouble(undo, editCyls[i].topRadius)
                        val mergedApply = mergeDouble(apply, editCyls[i].topRadius)
                        SetMeshShapeAction(component, editCyls[i].copy(topRadius = mergedUndo), editCyls[i].copy(topRadius = mergedApply))
                    }
                    FusedAction(actions)
                }
            )
            labeledDoubleTextField(
                label = "Bottom-radius:",
                value = condenseDouble(cylinders.map { it.bottomRadius }),
                dragChangeSpeed = DragChangeRates.SIZE,
                editHandler = ActionValueEditHandler { undo, apply ->
                    val editCyls = getShapes<ShapeData.Cylinder>()
                    val actions = components.mapIndexed { i, component ->
                        val mergedUndo = mergeDouble(undo, editCyls[i].bottomRadius)
                        val mergedApply = mergeDouble(apply, editCyls[i].bottomRadius)
                        SetMeshShapeAction(component, editCyls[i].copy(bottomRadius = mergedUndo), editCyls[i].copy(bottomRadius = mergedApply))
                    }
                    FusedAction(actions)
                }
            )
        }
        labeledDoubleTextField(
            label = "Length:",
            value = condenseDouble(cylinders.map { it.length }),
            dragChangeSpeed = DragChangeRates.SIZE,
            editHandler = ActionValueEditHandler { undo, apply ->
                val editCyls = getShapes<ShapeData.Cylinder>()
                val actions = components.mapIndexed { i, component ->
                    val mergedUndo = mergeDouble(undo, editCyls[i].length)
                    val mergedApply = mergeDouble(apply, editCyls[i].length)
                    SetMeshShapeAction(component, editCyls[i].copy(length = mergedUndo), editCyls[i].copy(length = mergedApply))
                }
                FusedAction(actions)
            }
        )
        labeledDoubleTextField(
            label = "Steps:",
            value = condenseDouble(cylinders.map { it.steps.toDouble() }),
            precision = 0,
            dragChangeSpeed = 0.1,
            minValue = 3.0,
            maxValue = 100.0,
            editHandler = ActionValueEditHandler { undo, apply ->
                val editCyls = getShapes<ShapeData.Cylinder>()
                val actions = components.mapIndexed { i, component ->
                    val mergedUndo = mergeDouble(undo, editCyls[i].steps.toDouble()).roundToInt()
                    val mergedApply = mergeDouble(apply, editCyls[i].steps.toDouble()).roundToInt()
                    SetMeshShapeAction(component, editCyls[i].copy(steps = mergedUndo), editCyls[i].copy(steps = mergedApply))
                }
                FusedAction(actions)
            }
        )
    }

    private fun UiScope.capsuleProperties() = Column(
        width = Grow.Std,
        scopeName = "capsuleProperties"
    ) {
        val capsules = getShapes<ShapeData.Capsule>()
        labeledDoubleTextField(
            label = "Radius:",
            value = condenseDouble(capsules.map { it.radius }),
            dragChangeSpeed = DragChangeRates.SIZE,
            editHandler = ActionValueEditHandler { undo, apply ->
                val editCaps = getShapes<ShapeData.Capsule>()
                val actions = components.mapIndexed { i, component ->
                    val mergedUndo = mergeDouble(undo, editCaps[i].radius)
                    val mergedApply = mergeDouble(apply, editCaps[i].radius)
                    SetMeshShapeAction(component, editCaps[i].copy(radius = mergedUndo), editCaps[i].copy(radius = mergedApply))
                }
                FusedAction(actions)
            }
        )
        labeledDoubleTextField(
            label = "Length:",
            value = condenseDouble(capsules.map { it.length }),
            dragChangeSpeed = DragChangeRates.SIZE,
            editHandler = ActionValueEditHandler { undo, apply ->
                val editCaps = getShapes<ShapeData.Capsule>()
                val actions = components.mapIndexed { i, component ->
                    val mergedUndo = mergeDouble(undo, editCaps[i].length)
                    val mergedApply = mergeDouble(apply, editCaps[i].length)
                    SetMeshShapeAction(component, editCaps[i].copy(length = mergedUndo), editCaps[i].copy(length = mergedApply))
                }
                FusedAction(actions)
            }
        )
        labeledDoubleTextField(
            label = "Steps:",
            value = condenseDouble(capsules.map { it.steps.toDouble() }),
            precision = 0,
            dragChangeSpeed = 0.1,
            minValue = 3.0,
            maxValue = 100.0,
            editHandler = ActionValueEditHandler { undo, apply ->
                val editCaps = getShapes<ShapeData.Capsule>()
                val actions = components.mapIndexed { i, component ->
                    val mergedUndo = mergeDouble(undo, editCaps[i].steps.toDouble()).roundToInt()
                    val mergedApply = mergeDouble(apply, editCaps[i].steps.toDouble()).roundToInt()
                    SetMeshShapeAction(component, editCaps[i].copy(steps = mergedUndo), editCaps[i].copy(steps = mergedApply))
                }
                FusedAction(actions)
            }
        )
    }

    private fun UiScope.heightmapProperties() = Column(
        width = Grow.Std,
        scopeName = "heightmapProperties"
    ) {
        val heightmaps = getShapes<ShapeData.Heightmap>()
        val mapPath = if (heightmaps.all { it.mapPath == heightmaps[0].mapPath }) heightmaps[0].mapPath else ""
        heightmapSelector(mapPath, true) {
            val editMaps = getShapes<ShapeData.Heightmap>()
            val actions = components.mapIndexed { i, component ->
                SetMeshShapeAction(component, editMaps[i], editMaps[i].copy(mapPath = it?.path ?: ""))
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
                        SetMeshShapeAction(component, editMaps[i], editMaps[i].copy(heightScale = endValue))
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
                        SetMeshShapeAction(component, editMaps[i], editMaps[i].copy(colScale = newScale.x, rowScale = newScale.y))
                    }
                    FusedAction(actions).apply()
                }
            }
        )
    }

    private val ShapeData.shapeOption: ShapeOption get() = ShapeOption.entries.first { it.matches(this) }

    private enum class ShapeOption(val label: String, val matches: (ShapeData?) -> Boolean) {
        Box("Box", { it is ShapeData.Box }),
        Rect("Rect", { it is ShapeData.Rect }),
        Sphere("Sphere", { it is ShapeData.Sphere }),
        Cylinder("Cylinder", { it is ShapeData.Cylinder }),
        Capsule("Capsule", { it is ShapeData.Capsule }),
        Heightmap("Heightmap", { it is ShapeData.Heightmap }),
        Empty("Empty", { it is ShapeData.Empty })
    }

    companion object {
        private val shapeOptions = ComboBoxItems(ShapeOption.entries) { it.label }
    }
}