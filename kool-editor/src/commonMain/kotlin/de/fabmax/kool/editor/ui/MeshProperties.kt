package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.actions.EditorActions
import de.fabmax.kool.editor.actions.SetShapeAction
import de.fabmax.kool.editor.model.MMesh
import de.fabmax.kool.editor.model.MMeshShape
import de.fabmax.kool.math.clamp
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.MdColor
import kotlin.math.abs
import kotlin.reflect.KClass

private data class ShapeOption<T: MMeshShape>(val name: String, val type: KClass<T>, val factory: () -> T) {
    override fun toString() = name
}

private object ShapeOptions {
    val items = listOf(
        ShapeOption("Box", MMeshShape.Box::class) { MMeshShape.defaultBox },
        ShapeOption("Rect", MMeshShape.Rect::class) { MMeshShape.defaultRect },
        ShapeOption("Ico-Sphere", MMeshShape.IcoSphere::class) { MMeshShape.defaultIcoSphere },
        ShapeOption("UV-Sphere", MMeshShape.UvSphere::class) { MMeshShape.defaultUvSphere },
        ShapeOption("Cylinder", MMeshShape.Cylinder::class) { MMeshShape.defaultCylinder },
        ShapeOption("Empty", MMeshShape.Empty::class) { MMeshShape.Empty },
    )

    fun indexOfShape(shape: MMeshShape): Int {
        return when (shape) {
            is MMeshShape.Box -> 0
            is MMeshShape.Rect -> 1
            is MMeshShape.IcoSphere -> 2
            is MMeshShape.UvSphere -> 3
            is MMeshShape.Cylinder -> 4
            MMeshShape.Empty -> 5
        }
    }
}

fun UiScope.meshTypeProperties(nodeModel: MMesh) = collapsapsablePanel("Mesh Type") {
    Column(width = Grow.Std) {
        modifier
            .padding(horizontal = sizes.gap)
            .margin(bottom = sizes.smallGap)

        Row(width = Grow.Std, height = sizes.lineHeight) {
            modifier.margin(top = sizes.smallGap)
            Text("Shape:") {
                modifier
                    .width(sizes.largeGap * 6f)
                    .font(sizes.boldText)
                    .alignY(AlignmentY.Center)
            }

            ComboBox {
                modifier
                    .width(Grow.Std)
                    .items(ShapeOptions.items)
                    .selectedIndex(ShapeOptions.indexOfShape(nodeModel.shape))
                    .onItemSelected {
                        val shapeType = ShapeOptions.items[it]
                        if (!shapeType.type.isInstance(nodeModel.shape)) {
                            EditorActions.applyAction(SetShapeAction(nodeModel, nodeModel.shape, shapeType.factory()))
                        }
                    }
            }
        }

        when (val type = nodeModel.shapeMutableState.use()) {
            is MMeshShape.Box -> boxProperties(nodeModel, type)
            is MMeshShape.Rect -> TODO()
            is MMeshShape.IcoSphere -> icoSphereProperties(nodeModel, type)
            is MMeshShape.UvSphere -> uvSphereProperties(nodeModel, type)
            is MMeshShape.Cylinder -> TODO()
            MMeshShape.Empty -> {}
        }
    }
}

private fun UiScope.boxProperties(nodeModel: MMesh, box: MMeshShape.Box) = Column(
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
                SetShapeAction(nodeModel, nodeModel.shape, MMeshShape.Box(box.size.copy(x = x)))
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
                SetShapeAction(nodeModel, nodeModel.shape, MMeshShape.Box(box.size.copy(y = y)))
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
                SetShapeAction(nodeModel, nodeModel.shape, MMeshShape.Box(box.size.copy(z = z)))
            )
        }
    }
}

private fun UiScope.icoSphereProperties(nodeModel: MMesh, icoSphere: MMeshShape.IcoSphere) = Column(
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
        doubleTextField(icoSphere.radius, 3, width = sizes.largeGap * 4f) {
            val r = if (!it.isFinite()) 1.0 else abs(it)
            EditorActions.applyAction(
                SetShapeAction(nodeModel, nodeModel.shape, icoSphere.copy(radius = r))
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
        intTextField(icoSphere.subDivisions, width = sizes.largeGap * 4f) {
            val s = it.clamp(0, 7)
            EditorActions.applyAction(
                SetShapeAction(nodeModel, nodeModel.shape, icoSphere.copy(subDivisions = s))
            )
        }
    }
}

private fun UiScope.uvSphereProperties(nodeModel: MMesh, uvSphere: MMeshShape.UvSphere) = Column(
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
        doubleTextField(uvSphere.radius, 3, width = sizes.largeGap * 4f) {
            val r = if (!it.isFinite()) 1.0 else abs(it)
            EditorActions.applyAction(
                SetShapeAction(nodeModel, nodeModel.shape, uvSphere.copy(radius = r))
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
        intTextField(uvSphere.steps, width = sizes.largeGap * 4f) {
            val s = it.clamp(3, 100)
            EditorActions.applyAction(
                SetShapeAction(nodeModel, nodeModel.shape, uvSphere.copy(steps = s))
            )
        }
    }
}
