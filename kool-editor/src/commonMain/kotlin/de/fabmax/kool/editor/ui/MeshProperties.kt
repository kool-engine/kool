package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.actions.EditorActions
import de.fabmax.kool.editor.actions.SetShapeAction
import de.fabmax.kool.editor.data.MeshShapeData
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.editor.model.ecs.MeshComponent
import de.fabmax.kool.math.clamp
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.MdColor
import kotlin.math.abs
import kotlin.reflect.KClass

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
        ShapeOption("Empty", MeshShapeData.Empty::class) { MeshShapeData.Empty },
    )

    fun indexOfShape(shape: MeshShapeData): Int {
        return when (shape) {
            is MeshShapeData.Box -> 0
            is MeshShapeData.Rect -> 1
            is MeshShapeData.IcoSphere -> 2
            is MeshShapeData.UvSphere -> 3
            is MeshShapeData.Cylinder -> 4
            MeshShapeData.Empty -> 5
        }
    }
}

fun UiScope.meshTypeProperties(nodeModel: SceneNodeModel, meshComponent: MeshComponent) = collapsapsablePanel(
    title = "Mesh Type",
    scopeName = "${nodeModel.nodeData.nodeId}"
) {
    // todo: support multiple primitives per mesh
    val shape = meshComponent.shapesState.use().getOrNull(0) ?: return@collapsapsablePanel Unit

    Column(width = Grow.Std) {
        modifier
            .padding(horizontal = sizes.gap)
            .margin(bottom = sizes.smallGap)

        Row(width = Grow.Std, height = sizes.lineHeight) {
            modifier.margin(top = sizes.smallGap)
            Text("Shape:") {
                modifier
                    .width(sizes.baseSize * 3f)
                    .font(sizes.boldText)
                    .alignY(AlignmentY.Center)
            }

            ComboBox {
                modifier
                    .size(Grow.Std, sizes.lineHeight)
                    .items(ShapeOptions.items)
                    .selectedIndex(ShapeOptions.indexOfShape(shape))
                    .onItemSelected {
                        val shapeType = ShapeOptions.items[it]
                        if (!shapeType.type.isInstance(shape)) {
                            EditorActions.applyAction(
                                SetShapeAction(nodeModel, meshComponent, shape, shapeType.factory())
                            )
                        }
                    }
            }
        }

        when (val shapeType = shape) {
            is MeshShapeData.Box -> boxProperties(nodeModel, meshComponent, shapeType)
            is MeshShapeData.Rect -> TODO()
            is MeshShapeData.IcoSphere -> icoSphereProperties(nodeModel, meshComponent, shapeType)
            is MeshShapeData.UvSphere -> uvSphereProperties(nodeModel, meshComponent, shapeType)
            is MeshShapeData.Cylinder -> TODO()
            MeshShapeData.Empty -> {}
        }
    }
}

private fun UiScope.boxProperties(nodeModel: SceneNodeModel, meshComponent: MeshComponent, box: MeshShapeData.Box) = Column(
    width = Grow.Std,
    scopeName = "boxProperties"
) {
    Row(height = sizes.lineHeight) {
        modifier.margin(top = sizes.smallGap)
        Text("Size:") {
            modifier
                .font(sizes.boldText)
                .alignY(AlignmentY.Center)
        }
    }
    Row(width = Grow.Std, height = sizes.lineHeight) {
        modifier.margin(start = sizes.gap)

        Text("X") {
            modifier
                .alignY(AlignmentY.Center)
                .font(sizes.boldText)
                .textColor(MdColor.RED tone 300)
        }
        doubleTextField(box.size.x, 3, width = Grow.Std) {
            val x = if (!it.isFinite()) 1.0 else abs(it)
            EditorActions.applyAction(
                SetShapeAction(nodeModel, meshComponent, box, MeshShapeData.Box(box.size.copy(x = x)))
            )
        }

        Text("Y") {
            modifier
                .margin(start = sizes.gap)
                .alignY(AlignmentY.Center)
                .font(sizes.boldText)
                .textColor(MdColor.GREEN tone 300)
        }
        doubleTextField(box.size.y, 3, width = Grow.Std) {
            val y = if (!it.isFinite()) 1.0 else abs(it)
            EditorActions.applyAction(
                SetShapeAction(nodeModel, meshComponent, box, MeshShapeData.Box(box.size.copy(y = y)))
            )
        }

        Text("Z") {
            modifier
                .margin(start = sizes.gap)
                .alignY(AlignmentY.Center)
                .font(sizes.boldText)
                .textColor(MdColor.BLUE tone 300)
        }
        doubleTextField(box.size.z, 3, width = Grow.Std) {
            val z = if (!it.isFinite()) 1.0 else abs(it)
            EditorActions.applyAction(
                SetShapeAction(nodeModel, meshComponent, box, MeshShapeData.Box(box.size.copy(z = z)))
            )
        }
    }
}

private fun UiScope.icoSphereProperties(nodeModel: SceneNodeModel, meshComponent: MeshComponent, icoSphere: MeshShapeData.IcoSphere) = Column(
    width = Grow.Std,
    scopeName = "icoSphereProperties"
) {
    Row(width = Grow.Std, height = sizes.lineHeight) {
        modifier.margin(top = sizes.smallGap)

        Text("Radius:") {
            modifier
                .width(Grow.Std)
                .alignY(AlignmentY.Center)
                .font(sizes.boldText)
        }
        doubleTextField(icoSphere.radius, 3, width = sizes.baseSize * 2f) {
            val r = if (!it.isFinite()) 1.0 else abs(it)
            EditorActions.applyAction(
                SetShapeAction(nodeModel, meshComponent, icoSphere, icoSphere.copy(radius = r))
            )
        }
    }
    Row(width = Grow.Std, height = sizes.lineHeight) {
        modifier.margin(top = sizes.smallGap)

        Text("Sub-divisions:") {
            modifier
                .width(Grow.Std)
                .alignY(AlignmentY.Center)
                .font(sizes.boldText)
        }
        intTextField(icoSphere.subDivisions, width = sizes.baseSize * 2f) {
            val s = it.clamp(0, 7)
            EditorActions.applyAction(
                SetShapeAction(nodeModel, meshComponent, icoSphere, icoSphere.copy(subDivisions = s))
            )
        }
    }
}

private fun UiScope.uvSphereProperties(nodeModel: SceneNodeModel, meshComponent: MeshComponent, uvSphere: MeshShapeData.UvSphere) = Column(
    width = Grow.Std,
    scopeName = "uvSphereProperties"
) {
    Row(width = Grow.Std, height = sizes.lineHeight) {
        modifier.margin(top = sizes.smallGap)

        Text("Radius:") {
            modifier
                .width(Grow.Std)
                .alignY(AlignmentY.Center)
                .font(sizes.boldText)
        }
        doubleTextField(uvSphere.radius, 3, width = sizes.baseSize * 2f) {
            val r = if (!it.isFinite()) 1.0 else abs(it)
            EditorActions.applyAction(
                SetShapeAction(nodeModel, meshComponent, uvSphere, uvSphere.copy(radius = r))
            )
        }
    }
    Row(width = Grow.Std, height = sizes.lineHeight) {
        modifier.margin(top = sizes.smallGap)

        Text("Steps:") {
            modifier
                .width(Grow.Std)
                .alignY(AlignmentY.Center)
                .font(sizes.boldText)
        }
        intTextField(uvSphere.steps, width = sizes.baseSize * 2f) {
            val s = it.clamp(3, 100)
            EditorActions.applyAction(
                SetShapeAction(nodeModel, meshComponent, uvSphere, uvSphere.copy(steps = s))
            )
        }
    }
}
