package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.CachedAppAssets
import de.fabmax.kool.editor.actions.EditorAction
import de.fabmax.kool.editor.actions.FusedAction
import de.fabmax.kool.editor.actions.SetMeshShapeAction
import de.fabmax.kool.editor.actions.fused
import de.fabmax.kool.editor.api.AppAssets
import de.fabmax.kool.editor.components.MeshComponent
import de.fabmax.kool.editor.components.toAssetReference
import de.fabmax.kool.editor.data.ShapeData
import de.fabmax.kool.editor.data.Vec2Data
import de.fabmax.kool.editor.data.Vec3Data
import de.fabmax.kool.math.Vec2d
import de.fabmax.kool.math.Vec3d
import de.fabmax.kool.modules.ui2.*
import kotlin.math.roundToInt

class MeshEditor : ComponentEditor<MeshComponent>() {

    override fun UiScope.compose() {
        val componentShapes = components.map { it.dataState.use().shapes.first().shapeOption }
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
                        .margin(end = sizes.gap)
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
            ShapeOption.Capsule -> listOf(ShapeData.defaultCapsule)
            ShapeOption.Heightmap -> listOf(ShapeData.defaultHeightmap)
            ShapeOption.Empty -> listOf(ShapeData.defaultCustom)
        }
        val actions = components
            .filter { it.data.shapes != newShapes }
            .map { SetMeshShapeAction(it, it.data.shapes[0], newShapes[0]) }
        if (actions.isNotEmpty()) {
            FusedAction(actions).apply()
        }
    }

    private fun ColumnScope.editorBody() {
        val shape = components[0].data.shapes[0]
        when (shape) {
            is ShapeData.Box -> boxProperties()
            is ShapeData.Rect -> rectProperties()
            is ShapeData.Sphere -> sphereProperties()
            is ShapeData.Cylinder -> cylinderProperties()
            is ShapeData.Capsule -> capsuleProperties()
            is ShapeData.Heightmap -> heightmapProperties()
            is ShapeData.Custom -> { }
            is ShapeData.Plane -> error("Plane shape is not supported as mesh shape")
        }

        if (shape.hasUvs) {
            shapeVec2Editor<ShapeData>(
                valueGetter = { it.common.uvScale.toVec2d() },
                valueSetter = { oldData, newValue -> oldData.copyShape(oldData.common.copy(uvScale = Vec2Data(newValue))) },
                label = "Texture scale:"
            )
        }
    }

    private fun UiScope.boxProperties() = Column(width = Grow.Std, scopeName = "boxProperties") {
        shapeVec3Editor<ShapeData.Box>(
            valueGetter = { it.size.toVec3d() },
            valueSetter = { oldData, newValue -> oldData.copy(size = Vec3Data(newValue)) },
            label = "Size:",
            minValues = Vec3d.ZERO
        )
    }

    private fun UiScope.rectProperties() = Column(width = Grow.Std, scopeName = "rectProperties") {
        shapeVec2Editor<ShapeData.Rect>(
            valueGetter = { it.size.toVec2d() },
            valueSetter = { oldData, newValue -> oldData.copy(size = Vec2Data(newValue)) },
            label = "Size:",
            minValues = Vec2d.ZERO
        )
    }

    private fun UiScope.sphereProperties() = Column(width = Grow.Std, scopeName = "sphereProperties") {
        shapeDoubleEditor<ShapeData.Sphere>(
            valueGetter = { it.radius },
            valueSetter = { oldData, newValue -> oldData.copy(radius = newValue) },
            label = "Radius:",
            minValue = 0.0
        )

        val isIco = components.all { (it.data.shapes[0] as ShapeData.Sphere).sphereType == "ico" }
        labeledCheckbox("Generate ico-sphere:", isIco) { setIco ->
            val newType = if (setIco) "ico" else "uv"
            val newSteps = if (setIco) 2 else 20
            components.map { component ->
                val sphere = component.data.shapes[0] as ShapeData.Sphere
                SetMeshShapeAction(component, sphere, sphere.copy(steps = newSteps, sphereType = newType))
            }.fused().apply()
        }

        if (isIco) {
            shapeDoubleEditor<ShapeData.Sphere>(
                valueGetter = { it.steps.toDouble() },
                valueSetter = { oldData, newValue -> oldData.copy(steps = newValue.roundToInt()) },
                label = "Sub-divisions:",
                precision = { 0 },
                minValue = 0.0,
                maxValue = 7.0
            )
        } else {
            shapeDoubleEditor<ShapeData.Sphere>(
                valueGetter = { it.steps.toDouble() },
                valueSetter = { oldData, newValue -> oldData.copy(steps = newValue.roundToInt()) },
                label = "Steps:",
                precision = { 0 },
                minValue = 3.0,
                maxValue = 100.0
            )
        }
    }

    private fun UiScope.cylinderProperties() = Column(
        width = Grow.Std,
        scopeName = "cylinderProperties"
    ) {
        var isUniRadius by remember(components.map { (it.data.shapes[0] as ShapeData.Cylinder) }.all { it.topRadius == it.bottomRadius })
        labeledCheckbox("Uniform radius:", isUniRadius) {
            isUniRadius = it
            if (isUniRadius) {
                components.map { component ->
                    val cyl = component.data.shapes[0] as ShapeData.Cylinder
                    SetMeshShapeAction(component, cyl, cyl.copy(topRadius = cyl.bottomRadius))
                }.fused().apply()
            }
        }
        if (isUniRadius) {
            shapeDoubleEditor<ShapeData.Cylinder>(
                valueGetter = { it.bottomRadius },
                valueSetter = { oldData, newValue -> oldData.copy(topRadius = newValue, bottomRadius = newValue) },
                label = "Radius:",
                minValue = 0.0
            )
        } else {
            shapeDoubleEditor<ShapeData.Cylinder>(
                valueGetter = { it.topRadius },
                valueSetter = { oldData, newValue -> oldData.copy(topRadius = newValue) },
                label = "Top-radius:",
                minValue = 0.0
            )
            shapeDoubleEditor<ShapeData.Cylinder>(
                valueGetter = { it.topRadius },
                valueSetter = { oldData, newValue -> oldData.copy(bottomRadius = newValue) },
                label = "Bottom-radius:",
                minValue = 0.0
            )
        }
        shapeDoubleEditor<ShapeData.Cylinder>(
            valueGetter = { it.length },
            valueSetter = { oldData, newValue -> oldData.copy(length = newValue) },
            label = "Length:",
            minValue = 0.0
        )
        shapeDoubleEditor<ShapeData.Cylinder>(
            valueGetter = { it.steps.toDouble() },
            valueSetter = { oldData, newValue -> oldData.copy(steps = newValue.roundToInt()) },
            label = "Steps:",
            precision = { 0 },
            minValue = 3.0,
            maxValue = 100.0
        )
    }

    private fun UiScope.capsuleProperties() = Column(
        width = Grow.Std,
        scopeName = "capsuleProperties"
    ) {
        shapeDoubleEditor<ShapeData.Capsule>(
            valueGetter = { it.radius },
            valueSetter = { oldData, newValue -> oldData.copy(radius = newValue) },
            label = "Radius:",
            minValue = 0.0
        )
        shapeDoubleEditor<ShapeData.Capsule>(
            valueGetter = { it.length },
            valueSetter = { oldData, newValue -> oldData.copy(length = newValue) },
            label = "Length:",
            minValue = 0.0
        )
        shapeDoubleEditor<ShapeData.Capsule>(
            valueGetter = { it.steps.toDouble() },
            valueSetter = { oldData, newValue -> oldData.copy(steps = newValue.roundToInt()) },
            label = "Steps:",
            precision = { 0 },
            minValue = 3.0,
            maxValue = 100.0
        )
    }

    private fun UiScope.heightmapProperties() = Column(
        width = Grow.Std,
        scopeName = "heightmapProperties"
    ) {
        val heightmaps = components.map { it.data.shapes[0] as ShapeData.Heightmap }
        val mapPath = if (heightmaps.all { it.mapPath == heightmaps[0].mapPath }) heightmaps[0].mapPath else ""
        heightmapSelector(mapPath, true) {
            components.map { component ->
                val hgt = component.data.shapes[0] as ShapeData.Heightmap
                SetMeshShapeAction(component, hgt, hgt.copy(mapPath = it?.path ?: ""))
            }.fused().apply()
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

        // todo: heightmap manipulation is currently much too slow for interactive editing
        labeledDoubleTextField(
            label = "Height scale:",
            value = condenseDouble(heightmaps.map { it.heightScale }),
            editHandler = object: ValueEditHandler<Double> {
                override fun onEdit(value: Double) { }
                override fun onEditEnd(startValue: Double, endValue: Double) {
                    components.map { component ->
                        val hgt = component.data.shapes[0] as ShapeData.Heightmap
                        SetMeshShapeAction(component, hgt, hgt.copy(heightScale = endValue))
                    }.fused().apply()
                }
            }
        )
        labeledXyRow(
            label = "Size:",
            xy = Vec2d(sizeX, sizeY),
            minValues = Vec2d.ZERO,
            editHandler = object: ValueEditHandler<Vec2d> {
                override fun onEdit(value: Vec2d) { }
                override fun onEditEnd(startValue: Vec2d, endValue: Vec2d) {
                    components.mapIndexed { i, component ->
                        val hgt = component.data.shapes[0] as ShapeData.Heightmap
                        val numCols = loaded[i]?.columns ?: MeshComponent.DEFAULT_HEIGHTMAP_COLS
                        val numRows = loaded[i]?.rows ?: MeshComponent.DEFAULT_HEIGHTMAP_ROWS
                        val newScale = endValue / Vec2d(numCols -1.0, numRows - 1.0)
                        SetMeshShapeAction(component, hgt, hgt.copy(colScale = newScale.x, rowScale = newScale.y))
                    }.fused().apply()
                }
            }
        )
    }

    private inline fun <reified T: ShapeData> UiScope.shapeDoubleEditor(
        noinline valueGetter: (T) -> Double,
        noinline valueSetter: (oldData: T, newValue: Double) -> T,
        label: String,
        noinline precision: (Double) -> Int = { precisionForValue(it) },
        minValue: Double = Double.NEGATIVE_INFINITY,
        maxValue: Double = Double.POSITIVE_INFINITY,
    ) = doublePropertyEditor(
        dataGetter = { it.data.shapes[0] as T },
        valueGetter = valueGetter,
        valueSetter = valueSetter,
        actionMapper = meshShapeActionMapper,
        label = label,
        precision = precision,
        minValue = minValue,
        maxValue = maxValue
    )

    private inline fun <reified T: ShapeData> UiScope.shapeVec2Editor(
        noinline valueGetter: (T) -> Vec2d,
        noinline valueSetter: (oldData: T, newValue: Vec2d) -> T,
        label: String,
        minValues: Vec2d? = null,
        maxValues: Vec2d? = null,
    ) = vec2dPropertyEditor(
        dataGetter = { it.data.shapes[0] as T },
        valueGetter = valueGetter,
        valueSetter = valueSetter,
        actionMapper = meshShapeActionMapper,
        label = label,
        minValues = minValues,
        maxValues = maxValues
    )

    private inline fun <reified T: ShapeData> UiScope.shapeVec3Editor(
        noinline valueGetter: (T) -> Vec3d,
        noinline valueSetter: (oldData: T, newValue: Vec3d) -> T,
        label: String,
        minValues: Vec3d? = null,
        maxValues: Vec3d? = null,
    ) = vec3dPropertyEditor(
        dataGetter = { it.data.shapes[0] as T },
        valueGetter = valueGetter,
        valueSetter = valueSetter,
        actionMapper = meshShapeActionMapper,
        label = label,
        minValues = minValues,
        maxValues = maxValues
    )

    private val ShapeData.shapeOption: ShapeOption get() = ShapeOption.entries.first { it.matches(this) }

    private enum class ShapeOption(val label: String, val matches: (ShapeData?) -> Boolean) {
        Box("Box", { it is ShapeData.Box }),
        Rect("Rect", { it is ShapeData.Rect }),
        Sphere("Sphere", { it is ShapeData.Sphere }),
        Cylinder("Cylinder", { it is ShapeData.Cylinder }),
        Capsule("Capsule", { it is ShapeData.Capsule }),
        Heightmap("Heightmap", { it is ShapeData.Heightmap }),
        Empty("Custom", { it is ShapeData.Custom })
    }

    companion object {
        private val shapeOptions = ComboBoxItems(ShapeOption.entries) { it.label }
        private val meshShapeActionMapper: (MeshComponent, ShapeData, ShapeData) -> EditorAction = { component, undoData, applyData ->
            SetMeshShapeAction(component, undoData, applyData)
        }
    }
}