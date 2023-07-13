package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.actions.SetShapeAction
import de.fabmax.kool.editor.components.MeshComponent
import de.fabmax.kool.editor.data.MeshShapeData
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
            onRemove = ::removeComponent,

            headerContent = {
                var selectedIndex by remember(0)
                selectedIndex = ShapeOptions.indexOfShape(shape)

                Box(Grow.Std) { }
                ComboBox {
                    defaultComboBoxStyle()
                    modifier
                        .margin(end = sizes.gap)
                        .size(sizes.baseSize * 4, sizes.lineHeight)
                        .alignY(AlignmentY.Center)
                        .items(ShapeOptions.items)
                        .selectedIndex(selectedIndex)
                        .onItemSelected {
                            SetShapeAction(component, shape, ShapeOptions.items[it].factory()).apply()
                        }
                }
            }
        ) {
            Column(width = Grow.Std) {
                modifier
                    .padding(horizontal = sizes.gap)
                    .margin(bottom = sizes.gap)

                when (val shapeType = shape) {
                    is MeshShapeData.Box -> boxProperties(component, shapeType)
                    is MeshShapeData.Rect -> rectProperties(component, shapeType)
                    is MeshShapeData.IcoSphere -> icoSphereProperties(component, shapeType)
                    is MeshShapeData.UvSphere -> uvSphereProperties(component, shapeType)
                    is MeshShapeData.Cylinder -> cylinderProperties(component, shapeType)
                    is MeshShapeData.Capsule -> capsuleProperties(component, shapeType)
                    is MeshShapeData.Empty -> { }
                }

                if (shape.hasUvs) {
                    val shapeI = component.shapesState.indexOf(shape)
                    xyRow(
                        label = "Texture scale:",
                        xy = shape.uvScale.toVec2d(),
                        dragChangeSpeed = DragChangeRates.SIZE_VEC2,
                        editHandler = ActionValueEditHandler { undoValue, applyValue ->
                            val undoShape = shape.copyShape(uvScale = Vec2Data(undoValue))
                            val applyShape = shape.copyShape(uvScale = Vec2Data(applyValue))
                            SetShapeAction(component, undoShape, applyShape, shapeI)
                        }
                    )
                }
            }
        }
    }

    private fun UiScope.boxProperties(meshComponent: MeshComponent, box: MeshShapeData.Box) = Column(
        width = Grow.Std,
        scopeName = "boxProperties"
    ) {
        val shapeI = meshComponent.shapesState.indexOf(box)
        xyzRow(
            label = "Size:",
            xyz = box.size.toVec3d(),
            dragChangeSpeed = DragChangeRates.SIZE_VEC3,
            editHandler = ActionValueEditHandler { undo, apply ->
                SetShapeAction(meshComponent, box.copy(size = Vec3Data(undo)), box.copy(size = Vec3Data(apply)), shapeI)
            }
        )
    }

    private fun UiScope.rectProperties(meshComponent: MeshComponent, rect: MeshShapeData.Rect) = Column(
        width = Grow.Std,
        scopeName = "rectProperties"
    ) {
        val shapeI = meshComponent.shapesState.indexOf(rect)
        xyRow(
            label = "Size:",
            xy = rect.size.toVec2d(),
            dragChangeSpeed = DragChangeRates.SIZE_VEC2,
            editHandler = ActionValueEditHandler { undo, apply ->
                SetShapeAction(meshComponent, rect.copy(size = Vec2Data(undo)), rect.copy(size = Vec2Data(apply)), shapeI)
            }
        )
    }

    private fun UiScope.icoSphereProperties(meshComponent: MeshComponent, icoSphere: MeshShapeData.IcoSphere) = Column(
        width = Grow.Std,
        scopeName = "icoSphereProperties"
    ) {
        val shapeI = meshComponent.shapesState.indexOf(icoSphere)
        labeledDoubleTextField(
            label = "Radius:",
            value = icoSphere.radius,
            dragChangeSpeed = DragChangeRates.SIZE,
            editHandler = ActionValueEditHandler { undo, apply ->
                SetShapeAction(meshComponent, icoSphere.copy(radius = undo), icoSphere.copy(radius = apply), shapeI)
            }
        )
        labeledIntTextField(
            label = "Sub-divisions:",
            value = icoSphere.subDivisions,
            dragChangeSpeed = 0.02,
            minValue = 0,
            maxValue = 7,
            editHandler = ActionValueEditHandler { undo, apply ->
                SetShapeAction(meshComponent, icoSphere.copy(subDivisions = undo), icoSphere.copy(subDivisions = apply), shapeI)
            }
        )
    }

    private fun UiScope.uvSphereProperties(meshComponent: MeshComponent, uvSphere: MeshShapeData.UvSphere) = Column(
        width = Grow.Std,
        scopeName = "uvSphereProperties"
    ) {
        val shapeI = meshComponent.shapesState.indexOf(uvSphere)
        labeledDoubleTextField(
            label = "Radius:",
            value = uvSphere.radius,
            dragChangeSpeed = DragChangeRates.SIZE,
            editHandler = ActionValueEditHandler { undo, apply ->
                SetShapeAction(meshComponent, uvSphere.copy(radius = undo), uvSphere.copy(radius = apply), shapeI)
            }
        )
        labeledIntTextField(
            label = "Steps:",
            value = uvSphere.steps,
            dragChangeSpeed = 0.1,
            minValue = 3,
            maxValue = 100,
            editHandler = ActionValueEditHandler { undo, apply ->
                SetShapeAction(meshComponent, uvSphere.copy(steps = undo), uvSphere.copy(steps = apply), shapeI)
            }
        )
    }

    private fun UiScope.cylinderProperties(meshComponent: MeshComponent, cylinder: MeshShapeData.Cylinder) = Column(
        width = Grow.Std,
        scopeName = "cylinderProperties"
    ) {
        val shapeI = meshComponent.shapesState.indexOf(cylinder)

        var isUniRadius by remember(cylinder.topRadius == cylinder.bottomRadius)
        labeledCheckbox("Uniform radius:", isUniRadius) {
            isUniRadius = it
            if (isUniRadius && cylinder.topRadius != cylinder.bottomRadius) {
                SetShapeAction(meshComponent, cylinder, cylinder.copy(topRadius = cylinder.bottomRadius)).apply()
            }
        }
        if (isUniRadius) {
            labeledDoubleTextField(
                label = "Radius:",
                value = cylinder.bottomRadius,
                dragChangeSpeed = DragChangeRates.SIZE,
                editHandler = ActionValueEditHandler { undo, apply ->
                    SetShapeAction(meshComponent, cylinder.copy(bottomRadius = undo, topRadius = undo), cylinder.copy(bottomRadius = apply, topRadius = apply), shapeI)
                }
            )
        } else {
            labeledDoubleTextField(
                label = "Top-radius:",
                value = cylinder.topRadius,
                dragChangeSpeed = DragChangeRates.SIZE,
                editHandler = ActionValueEditHandler { undo, apply ->
                    SetShapeAction(meshComponent, cylinder.copy(topRadius = undo), cylinder.copy(topRadius = apply), shapeI)
                }
            )
            labeledDoubleTextField(
                label = "Bottom-radius:",
                value = cylinder.bottomRadius,
                dragChangeSpeed = DragChangeRates.SIZE,
                editHandler = ActionValueEditHandler { undo, apply ->
                    SetShapeAction(meshComponent, cylinder.copy(bottomRadius = undo), cylinder.copy(bottomRadius = apply), shapeI)
                }
            )
        }
        labeledDoubleTextField(
            label = "Height:",
            value = cylinder.height,
            dragChangeSpeed = DragChangeRates.SIZE,
            editHandler = ActionValueEditHandler { undo, apply ->
                SetShapeAction(meshComponent, cylinder.copy(height = undo), cylinder.copy(height = apply), shapeI)
            }
        )
        labeledIntTextField(
            label = "Steps:",
            value = cylinder.steps,
            dragChangeSpeed = 0.1,
            minValue = 3,
            maxValue = 100,
            editHandler = ActionValueEditHandler { undo, apply ->
                SetShapeAction(meshComponent, cylinder.copy(steps = undo), cylinder.copy(steps = apply), shapeI)
            }
        )
    }

    private fun UiScope.capsuleProperties(meshComponent: MeshComponent, capsule: MeshShapeData.Capsule) = Column(
        width = Grow.Std,
        scopeName = "capsuleProperties"
    ) {
        val shapeI = meshComponent.shapesState.indexOf(capsule)
        labeledDoubleTextField(
            label = "Radius:",
            value = capsule.radius,
            dragChangeSpeed = DragChangeRates.SIZE,
            editHandler = ActionValueEditHandler { undo, apply ->
                SetShapeAction(meshComponent, capsule.copy(radius = undo), capsule.copy(radius = apply), shapeI)
            }
        )
        labeledDoubleTextField(
            label = "Length:",
            value = capsule.length,
            dragChangeSpeed = DragChangeRates.SIZE,
            editHandler = ActionValueEditHandler { undo, apply ->
                SetShapeAction(meshComponent, capsule.copy(length = undo), capsule.copy(length = apply), shapeI)
            }
        )
        labeledIntTextField(
            label = "Steps:",
            value = capsule.steps,
            dragChangeSpeed = 0.1,
            minValue = 3,
            maxValue = 100,
            editHandler = ActionValueEditHandler { undo, apply ->
                SetShapeAction(meshComponent, capsule.copy(steps = undo), capsule.copy(steps = apply), shapeI)
            }
        )
    }

    private data class ShapeOption<T: MeshShapeData>(val name: String, val type: KClass<T>, val factory: () -> T) {
        override fun toString() = name
    }

    private object ShapeOptions {
        val items = listOf(
            ShapeOption("Box", MeshShapeData.Box::class) { MeshShapeData.defaultBox },
            ShapeOption("Rect", MeshShapeData.Rect::class) { MeshShapeData.defaultRect },
            ShapeOption("Ico-Sphere", MeshShapeData.IcoSphere::class) { MeshShapeData.defaultIcoSphere },
            ShapeOption("UV-Sphere", MeshShapeData.UvSphere::class) { MeshShapeData.defaultUvSphere },
            ShapeOption("Cylinder", MeshShapeData.Cylinder::class) { MeshShapeData.defaultCylinder },
            ShapeOption("Capsule", MeshShapeData.Capsule::class) { MeshShapeData.defaultCapsule },
            ShapeOption("Empty", MeshShapeData.Empty::class) { MeshShapeData.Empty() },
        )

        fun indexOfShape(shape: MeshShapeData): Int {
            return when (shape) {
                is MeshShapeData.Box -> 0
                is MeshShapeData.Rect -> 1
                is MeshShapeData.IcoSphere -> 2
                is MeshShapeData.UvSphere -> 3
                is MeshShapeData.Cylinder -> 4
                is MeshShapeData.Capsule -> 5
                is MeshShapeData.Empty -> 6
            }
        }
    }

}