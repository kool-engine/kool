package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.actions.SetMeshShapeAction
import de.fabmax.kool.editor.components.MeshComponent
import de.fabmax.kool.editor.data.ShapeData
import de.fabmax.kool.editor.data.Vec2Data
import de.fabmax.kool.editor.data.Vec3Data
import de.fabmax.kool.modules.ui2.*
import kotlin.reflect.KClass

class MeshEditor(component: MeshComponent) : ComponentEditor<MeshComponent>(component) {

    override fun UiScope.compose() {
        // todo: support multiple primitives per mesh
        val shape = component.shapesState.use().getOrNull(0) ?: return

        componentPanel(
            title = "Mesh",
            imageIcon = IconMap.small.cube,
            onRemove = ::removeComponent,
            titleWidth = sizes.baseSize * 2.3f,

            headerContent = {
                var selectedIndex by remember(0)
                selectedIndex = ShapeOptions.indexOfShape(shape)

                ComboBox {
                    defaultComboBoxStyle()
                    modifier
                        .margin(horizontal = sizes.gap)
                        .width(Grow.Std)
                        .alignY(AlignmentY.Center)
                        .items(ShapeOptions.items)
                        .selectedIndex(selectedIndex)
                        .onItemSelected {
                            SetMeshShapeAction(component, shape, ShapeOptions.items[it].factory()).apply()
                        }
                }
            }
        ) {
            Column(width = Grow.Std) {
                modifier
                    .padding(horizontal = sizes.gap)
                    .margin(bottom = sizes.gap)

                when (shape) {
                    is ShapeData.Box -> boxProperties(shape)
                    is ShapeData.Rect -> rectProperties(shape)
                    is ShapeData.Sphere -> icoSphereProperties(shape)
                    is ShapeData.Cylinder -> cylinderProperties(shape)
                    is ShapeData.Capsule -> capsuleProperties(shape)
                    is ShapeData.Heightmap -> TODO()
                    is ShapeData.Empty -> { }
                }

                if (shape.hasUvs) {
                    val shapeI = component.shapesState.indexOf(shape)
                    labeledXyRow(
                        label = "Texture scale:",
                        xy = shape.common.uvScale.toVec2d(),
                        dragChangeSpeed = DragChangeRates.SIZE_VEC2,
                        editHandler = ActionValueEditHandler { undoValue, applyValue ->
                            // make sure up-to-date shape is used as base for SetShapeAction
                            val baseShape = component.shapesState[shapeI]
                            val undoShape = baseShape.copyShape(shape.common.copy(uvScale = Vec2Data(undoValue)))
                            val applyShape = baseShape.copyShape(shape.common.copy(uvScale = Vec2Data(applyValue)))
                            SetMeshShapeAction(component, undoShape, applyShape, shapeI)
                        }
                    )
                }
            }
        }
    }

    private fun UiScope.boxProperties(box: ShapeData.Box) = Column(
        width = Grow.Std,
        scopeName = "boxProperties"
    ) {
        val shapeI = component.shapesState.indexOf(box)
        labeledXyzRow(
            label = "Size:",
            xyz = box.size.toVec3d(),
            dragChangeSpeed = DragChangeRates.SIZE_VEC3,
            editHandler = ActionValueEditHandler { undo, apply ->
                SetMeshShapeAction(component, box.copy(size = Vec3Data(undo)), box.copy(size = Vec3Data(apply)), shapeI)
            }
        )
    }

    private fun UiScope.rectProperties(rect: ShapeData.Rect) = Column(
        width = Grow.Std,
        scopeName = "rectProperties"
    ) {
        val shapeI = component.shapesState.indexOf(rect)
        labeledXyRow(
            label = "Size:",
            xy = rect.size.toVec2d(),
            dragChangeSpeed = DragChangeRates.SIZE_VEC2,
            editHandler = ActionValueEditHandler { undo, apply ->
                SetMeshShapeAction(component, rect.copy(size = Vec2Data(undo)), rect.copy(size = Vec2Data(apply)), shapeI)
            }
        )
    }

    private fun UiScope.icoSphereProperties(sphere: ShapeData.Sphere) = Column(
        width = Grow.Std,
        scopeName = "icoSphereProperties"
    ) {
        val shapeI = component.shapesState.indexOf(sphere)
        labeledDoubleTextField(
            label = "Radius:",
            value = sphere.radius,
            dragChangeSpeed = DragChangeRates.SIZE,
            editHandler = ActionValueEditHandler { undo, apply ->
                SetMeshShapeAction(component, sphere.copy(radius = undo), sphere.copy(radius = apply), shapeI)
            }
        )
        val isIco = sphere.sphereType == "ico"
        labeledCheckbox("Generate as ico-sphere", isIco) { setIco ->
            val newType = if (setIco) "ico" else "uv"
            val newSteps = if (setIco) 2 else 20
            SetMeshShapeAction(component, sphere, sphere.copy(steps = newSteps, sphereType = newType), shapeI).apply()
        }
        if (isIco) {
            labeledIntTextField(
                label = "Sub-divisions:",
                value = sphere.steps,
                dragChangeSpeed = 0.02,
                minValue = 0,
                maxValue = 7,
                editHandler = ActionValueEditHandler { undo, apply ->
                    SetMeshShapeAction(component, sphere.copy(steps = undo), sphere.copy(steps = apply), shapeI)
                }
            )
        } else {
            labeledIntTextField(
                label = "Steps:",
                value = sphere.steps,
                dragChangeSpeed = 0.1,
                minValue = 3,
                maxValue = 100,
                editHandler = ActionValueEditHandler { undo, apply ->
                    SetMeshShapeAction(component, sphere.copy(steps = undo), sphere.copy(steps = apply), shapeI)
                }
            )
        }
    }

    private fun UiScope.cylinderProperties(cylinder: ShapeData.Cylinder) = Column(
        width = Grow.Std,
        scopeName = "cylinderProperties"
    ) {
        val shapeI = component.shapesState.indexOf(cylinder)

        var isUniRadius by remember(cylinder.topRadius == cylinder.bottomRadius)
        labeledCheckbox("Uniform radius:", isUniRadius) {
            isUniRadius = it
            if (isUniRadius && cylinder.topRadius != cylinder.bottomRadius) {
                SetMeshShapeAction(component, cylinder, cylinder.copy(topRadius = cylinder.bottomRadius)).apply()
            }
        }
        if (isUniRadius) {
            labeledDoubleTextField(
                label = "Radius:",
                value = cylinder.bottomRadius,
                dragChangeSpeed = DragChangeRates.SIZE,
                editHandler = ActionValueEditHandler { undo, apply ->
                    SetMeshShapeAction(component, cylinder.copy(bottomRadius = undo, topRadius = undo), cylinder.copy(bottomRadius = apply, topRadius = apply), shapeI)
                }
            )
        } else {
            labeledDoubleTextField(
                label = "Top-radius:",
                value = cylinder.topRadius,
                dragChangeSpeed = DragChangeRates.SIZE,
                editHandler = ActionValueEditHandler { undo, apply ->
                    SetMeshShapeAction(component, cylinder.copy(topRadius = undo), cylinder.copy(topRadius = apply), shapeI)
                }
            )
            labeledDoubleTextField(
                label = "Bottom-radius:",
                value = cylinder.bottomRadius,
                dragChangeSpeed = DragChangeRates.SIZE,
                editHandler = ActionValueEditHandler { undo, apply ->
                    SetMeshShapeAction(component, cylinder.copy(bottomRadius = undo), cylinder.copy(bottomRadius = apply), shapeI)
                }
            )
        }
        labeledDoubleTextField(
            label = "Length:",
            value = cylinder.length,
            dragChangeSpeed = DragChangeRates.SIZE,
            editHandler = ActionValueEditHandler { undo, apply ->
                SetMeshShapeAction(component, cylinder.copy(length = undo), cylinder.copy(length = apply), shapeI)
            }
        )
        labeledIntTextField(
            label = "Steps:",
            value = cylinder.steps,
            dragChangeSpeed = 0.1,
            minValue = 3,
            maxValue = 100,
            editHandler = ActionValueEditHandler { undo, apply ->
                SetMeshShapeAction(component, cylinder.copy(steps = undo), cylinder.copy(steps = apply), shapeI)
            }
        )
    }

    private fun UiScope.capsuleProperties(capsule: ShapeData.Capsule) = Column(
        width = Grow.Std,
        scopeName = "capsuleProperties"
    ) {
        val shapeI = component.shapesState.indexOf(capsule)
        labeledDoubleTextField(
            label = "Radius:",
            value = capsule.radius,
            dragChangeSpeed = DragChangeRates.SIZE,
            editHandler = ActionValueEditHandler { undo, apply ->
                SetMeshShapeAction(component, capsule.copy(radius = undo), capsule.copy(radius = apply), shapeI)
            }
        )
        labeledDoubleTextField(
            label = "Length:",
            value = capsule.length,
            dragChangeSpeed = DragChangeRates.SIZE,
            editHandler = ActionValueEditHandler { undo, apply ->
                SetMeshShapeAction(component, capsule.copy(length = undo), capsule.copy(length = apply), shapeI)
            }
        )
        labeledIntTextField(
            label = "Steps:",
            value = capsule.steps,
            dragChangeSpeed = 0.1,
            minValue = 3,
            maxValue = 100,
            editHandler = ActionValueEditHandler { undo, apply ->
                SetMeshShapeAction(component, capsule.copy(steps = undo), capsule.copy(steps = apply), shapeI)
            }
        )
    }

    private data class ShapeOption<T: ShapeData>(val name: String, val type: KClass<T>, val factory: () -> T) {
        override fun toString() = name
    }

    private object ShapeOptions {
        val items = listOf(
            ShapeOption("Box", ShapeData.Box::class) { ShapeData.defaultBox },
            ShapeOption("Rect", ShapeData.Rect::class) { ShapeData.defaultRect },
            ShapeOption("Sphere", ShapeData.Sphere::class) { ShapeData.defaultSphere },
            ShapeOption("Cylinder", ShapeData.Cylinder::class) { ShapeData.defaultCylinder },
            ShapeOption("Capsule", ShapeData.Capsule::class) { ShapeData.defaultCapsule },
            ShapeOption("Empty", ShapeData.Empty::class) { ShapeData.Empty() },
        )

        fun indexOfShape(shape: ShapeData): Int {
            return when (shape) {
                is ShapeData.Box -> 0
                is ShapeData.Rect -> 1
                is ShapeData.Sphere -> 2
                is ShapeData.Cylinder -> 3
                is ShapeData.Capsule -> 4
                is ShapeData.Heightmap -> 5
                is ShapeData.Empty -> 6
            }
        }
    }

}