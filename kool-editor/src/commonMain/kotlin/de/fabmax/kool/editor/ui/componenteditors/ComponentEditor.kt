package de.fabmax.kool.editor.ui.componenteditors

import de.fabmax.kool.editor.actions.EditorAction
import de.fabmax.kool.editor.actions.FusedAction
import de.fabmax.kool.editor.actions.RemoveComponentAction
import de.fabmax.kool.editor.actions.fused
import de.fabmax.kool.editor.api.EditorScene
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.components.GameEntityDataComponent
import de.fabmax.kool.editor.data.EntityId
import de.fabmax.kool.editor.ui.*
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.toColor

abstract class ComponentEditor<T: GameEntityDataComponent<*>> : Composable {
    var components: List<T> = emptyList()
    var entityEditor: GameEntityEditor? = null
    val component: T get() = components[0]

    val entityId: EntityId get() = component.gameEntity.id
    val gameEntity: GameEntity get() = component.gameEntity
    val scene: EditorScene get() = gameEntity.scene

    protected fun removeComponent() {
        components.map { RemoveComponentAction(it.gameEntity.id, it) }.fused().apply()
    }

    fun getPanelState(default: Boolean = true, panelKey: String = component.componentType): Boolean {
        return entityEditor?.let { editor ->
            editor.panelCollapseStates
                .getOrElse(entityId) { emptyMap() }
                .getOrElse(panelKey) { default }
        } ?: default
    }

    fun setPanelState(state: Boolean, panelKey: String = component.componentType) {
        entityEditor?.let { editor ->
            editor.panelCollapseStates.getOrPut(entityId) { mutableMapOf() }[panelKey] = state
        }
    }

    fun UiScope.componentPanel(
        title: String,
        imageIcon: IconProvider? = null,
        onRemove: (() -> Unit)? = null,
        titleWidth: Dimension = Grow.Std,
        headerContent: (RowScope.() -> Unit)? = null,
        startExpanded: Boolean = getPanelState(true),
        scopeName: String = title,
        block: ColumnScope.() -> Any?
    ) = entityEditorPanel(
        title = title,
        imageIcon = imageIcon,
        onRemove = onRemove,
        titleWidth = titleWidth,
        headerContent = headerContent,
        startExpanded = startExpanded,
        scopeName = scopeName,
        onCollapseChanged = { setPanelState(it) },
        block = block,
    )

    protected fun condenseDouble(doubles: List<Double>, eps: Double = FUZZY_EQ_D): Double {
        return if (doubles.all { isFuzzyEqual(it, doubles[0], eps) }) doubles[0] else Double.NaN
    }

    protected fun condenseVec2(vecs: List<Vec2d>, eps: Double = FUZZY_EQ_D): Vec2d {
        val x = if (vecs.all { isFuzzyEqual(it.x, vecs[0].x, eps) }) vecs[0].x else Double.NaN
        val y = if (vecs.all { isFuzzyEqual(it.y, vecs[0].y, eps) }) vecs[0].y else Double.NaN
        return Vec2d(x, y)
    }

    protected fun condenseVec3(vecs: List<Vec3d>, eps: Double = FUZZY_EQ_D): Vec3d {
        val x = if (vecs.all { isFuzzyEqual(it.x, vecs[0].x, eps) }) vecs[0].x else Double.NaN
        val y = if (vecs.all { isFuzzyEqual(it.y, vecs[0].y, eps) }) vecs[0].y else Double.NaN
        val z = if (vecs.all { isFuzzyEqual(it.z, vecs[0].z, eps) }) vecs[0].z else Double.NaN
        return Vec3d(x, y, z)
    }

    protected fun condenseVec4(vecs: List<Vec4d>, eps: Double = FUZZY_EQ_D): Vec4d {
        val x = if (vecs.all { isFuzzyEqual(it.x, vecs[0].x, eps) }) vecs[0].x else Double.NaN
        val y = if (vecs.all { isFuzzyEqual(it.y, vecs[0].y, eps) }) vecs[0].y else Double.NaN
        val z = if (vecs.all { isFuzzyEqual(it.z, vecs[0].z, eps) }) vecs[0].z else Double.NaN
        val w = if (vecs.all { isFuzzyEqual(it.w, vecs[0].w, eps) }) vecs[0].w else Double.NaN
        return Vec4d(x, y, z, w)
    }

    protected fun mergeDouble(masked: Double, fromComponent: Double): Double {
        return if (masked.isFinite()) masked else fromComponent
    }

    protected fun mergeVec2(masked: Vec2d, fromComponent: Vec2d): Vec2d {
        return Vec2d(
            if (masked.x.isFinite()) masked.x else fromComponent.x,
            if (masked.y.isFinite()) masked.y else fromComponent.y,
        )
    }

    protected fun mergeVec3(masked: Vec3d, fromComponent: Vec3d): Vec3d {
        return Vec3d(
            if (masked.x.isFinite()) masked.x else fromComponent.x,
            if (masked.y.isFinite()) masked.y else fromComponent.y,
            if (masked.z.isFinite()) masked.z else fromComponent.z,
        )
    }

    protected fun mergeVec4(masked: Vec4d, fromComponent: Vec4d): Vec4d {
        return Vec4d(
            if (masked.x.isFinite()) masked.x else fromComponent.x,
            if (masked.y.isFinite()) masked.y else fromComponent.y,
            if (masked.z.isFinite()) masked.z else fromComponent.z,
            if (masked.w.isFinite()) masked.w else fromComponent.w,
        )
    }

    protected fun <D> ColumnScope.doublePropertyEditor(
        dataGetter: (T) -> D,
        valueGetter: (D) -> Double,
        valueSetter: (oldData: D, newValue: Double) -> D,
        actionMapper: (component: T, undoData: D, applyData: D) -> EditorAction,

        label: String,
        precision: (Double) -> Int = { precisionForValue(it) },
        labelWidth: Dimension = sizes.editorLabelWidthLarge,
        valueWidth: Dimension = Grow.Std,
        dragChangeSpeed: Double = DragChangeRates.SIZE,
        minValue: Double = Double.NEGATIVE_INFINITY,
        maxValue: Double = Double.POSITIVE_INFINITY,
    ) {
        val value = condenseDouble(components.map { valueGetter(dataGetter(it)) })
        labeledDoubleTextField(
            label = label,
            value = value,
            precision = precision(value),
            labelWidth = labelWidth,
            valueWidth = valueWidth,
            dragChangeSpeed = dragChangeSpeed,
            minValue = minValue,
            maxValue = maxValue,
            editHandler = ActionValueEditHandler { undo, apply ->
                components.map { component ->
                    val componentData = dataGetter(component)
                    val mergedUndo = mergeDouble(undo, valueGetter(componentData))
                    val mergedApply = mergeDouble(apply, valueGetter(componentData))
                    val undoData = valueSetter(componentData, mergedUndo)
                    val applyData = valueSetter(componentData, mergedApply)
                    actionMapper(component, undoData, applyData)
                }.fused()
            }
        )
    }

    protected fun <D> ColumnScope.vec2dPropertyEditor(
        dataGetter: (T) -> D,
        valueGetter: (D) -> Vec2d,
        valueSetter: (oldData: D, newValue: Vec2d) -> D,
        actionMapper: (component: T, undoData: D, applyData: D) -> EditorAction,

        label: String,
        precision: (Vec2d) -> Vec2i = { Vec2i(precisionForValue(it.x), precisionForValue(it.y)) },
        minValues: Vec2d? = null,
        maxValues: Vec2d? = null,
        dragChangeSpeed: Vec2d = DragChangeRates.SIZE_VEC2,
    ) {
        val value = condenseVec2(components.map { valueGetter(dataGetter(it)) })
        labeledXyRow(
            label = label,
            xy = value,
            precision = precision(value),
            minValues = minValues,
            maxValues = maxValues,
            dragChangeSpeed = dragChangeSpeed,
            editHandler = ActionValueEditHandler { undo, apply ->
                components.map { component ->
                    val componentData = dataGetter(component)
                    val mergedUndo = mergeVec2(undo, valueGetter(componentData))
                    val mergedApply = mergeVec2(apply, valueGetter(componentData))
                    val undoData = valueSetter(componentData, mergedUndo)
                    val applyData = valueSetter(componentData, mergedApply)
                    actionMapper(component, undoData, applyData)
                }.fused()
            }
        )
    }

    protected fun <D> ColumnScope.vec3dPropertyEditor(
        dataGetter: (T) -> D,
        valueGetter: (D) -> Vec3d,
        valueSetter: (oldData: D, newValue: Vec3d) -> D,
        actionMapper: (component: T, undoData: D, applyData: D) -> EditorAction,

        label: String,
        precision: (Vec3d) -> Vec3i = { Vec3i(precisionForValue(it.x), precisionForValue(it.y), precisionForValue(it.z)) },
        minValues: Vec3d? = null,
        maxValues: Vec3d? = null,
        dragChangeSpeed: Vec3d = DragChangeRates.SIZE_VEC3,
    ) {
        val value = condenseVec3(components.map { valueGetter(dataGetter(it)) })
        labeledXyzRow(
            label = label,
            xyz = value,
            precision = precision(value),
            minValues = minValues,
            maxValues = maxValues,
            dragChangeSpeed = dragChangeSpeed,
            editHandler = ActionValueEditHandler { undo, apply ->
                components.map { component ->
                    val componentData = dataGetter(component)
                    val mergedUndo = mergeVec3(undo, valueGetter(componentData))
                    val mergedApply = mergeVec3(apply, valueGetter(componentData))
                    val undoData = valueSetter(componentData, mergedUndo)
                    val applyData = valueSetter(componentData, mergedApply)
                    actionMapper(component, undoData, applyData)
                }.fused()
            }
        )
    }

    protected fun <D> ColumnScope.vec4dPropertyEditor(
        dataGetter: (T) -> D,
        valueGetter: (D) -> Vec4d,
        valueSetter: (oldData: D, newValue: Vec4d) -> D,
        actionMapper: (component: T, undoData: D, applyData: D) -> EditorAction,

        label: String,
        precision: (Vec4d) -> Vec4i = { Vec4i(precisionForValue(it.x), precisionForValue(it.y), precisionForValue(it.z), precisionForValue(it.w)) },
        minValues: Vec4d? = null,
        maxValues: Vec4d? = null,
        dragChangeSpeed: Vec4d = DragChangeRates.SIZE_VEC4,
    ) {
        val value = condenseVec4(components.map { valueGetter(dataGetter(it)) })
        labeledXyzwRow(
            label = label,
            xyzw = value,
            precision = precision(value),
            minValues = minValues,
            maxValues = maxValues,
            dragChangeSpeed = dragChangeSpeed,
            editHandler = ActionValueEditHandler { undo, apply ->
                components.map { component ->
                    val componentData = dataGetter(component)
                    val mergedUndo = mergeVec4(undo, valueGetter(componentData))
                    val mergedApply = mergeVec4(apply, valueGetter(componentData))
                    val undoData = valueSetter(componentData, mergedUndo)
                    val applyData = valueSetter(componentData, mergedApply)
                    actionMapper(component, undoData, applyData)
                }.fused()
            }
        )
    }

    protected fun <C, D> ColumnScope.choicePropertyEditor(
        choices: ComboBoxItems<C>,
        dataGetter: (T) -> D,
        valueGetter: (D) -> C,
        valueSetter: (oldData: D, newValue: C) -> D,
        actionMapper: (component: T, undoData: D, applyData: D) -> EditorAction,

        label: String,
        labelWidth: Dimension = sizes.editorLabelWidthSmall,
    ) {
        val (items, index) = choices.getOptionsAndIndex(components.map { valueGetter(dataGetter(it)) })
        labeledCombobox(
            label = label,
            labelWidth = labelWidth,
            items = items,
            selectedIndex = index
        ) { selected ->
            selected.item?.let { choice ->
                val actions = components
                    .filter { valueGetter(dataGetter(it)) != choice }
                    .map {
                        val oldData = dataGetter(it)
                        val newData = valueSetter(oldData, choice)
                        actionMapper(it, oldData, newData)
                    }
                if (actions.isNotEmpty()) {
                    FusedAction(actions).apply()
                }
            }
        }
    }

    protected fun <D> ColumnScope.booleanPropertyEditor(
        dataGetter: (T) -> D,
        valueGetter: (D) -> Boolean,
        valueSetter: (oldData: D, newValue: Boolean) -> D,
        actionMapper: (component: T, undoData: D, applyData: D) -> EditorAction,

        label: String,
    ) {
        val flag = components.all { valueGetter(dataGetter(it)) }
        labeledCheckbox(label, flag) { newFlag ->
            val actions = components
                .filter { valueGetter(dataGetter(it)) != newFlag }
                .map {
                    val oldData = dataGetter(it)
                    val newData = valueSetter(oldData, newFlag)
                    actionMapper(it, oldData, newData)
                }
            if (actions.isNotEmpty()) {
                FusedAction(actions).apply()
            }
        }
    }

    protected fun <D> ColumnScope.colorPropertyEditor(
        dataGetter: (T) -> D,
        valueGetter: (D) -> Color,
        valueSetter: (oldData: D, newValue: Color) -> D,
        actionMapper: (component: T, undoData: D, applyData: D) -> EditorAction,

        label: String,
        isWithAlpha: Boolean = false
    ) {
        val colors = components.map { valueGetter(dataGetter(it)) }
        val color = if (colors.all { it == colors[0] }) colors[0] else Color.BLACK

        labeledColorPicker(
            label = label,
            pickerColor = color,
            isWithAlpha = isWithAlpha,
            editHandler = ActionValueEditHandler { undo, apply ->
                components.map { component ->
                    val componentData = dataGetter(component)
                    val mergedUndo = mergeVec4(undo.toVec4f().toVec4d(), valueGetter(componentData).toVec4f().toVec4d())
                    val mergedApply = mergeVec4(apply.toVec4f().toVec4d(), valueGetter(componentData).toVec4f().toVec4d())
                    val undoData = valueSetter(componentData, mergedUndo.toVec4f().toColor())
                    val applyData = valueSetter(componentData, mergedApply.toVec4f().toColor())
                    actionMapper(component, undoData, applyData)
                }.fused()
            }
        )
    }

    protected fun <D> ColumnScope.stringPropertyEditor(
        dataGetter: (T) -> D,
        valueGetter: (D) -> String,
        valueSetter: (oldData: D, newValue: String) -> D,
        actionMapper: (component: T, undoData: D, applyData: D) -> EditorAction,

        label: String,
        labelWidth: Dimension = sizes.editorLabelWidthSmall,
    ) {
        val texts = components.map { valueGetter(dataGetter(it)) }
        val text = if (texts.all { it == texts[0] }) texts[0] else ""
        labeledTextField(label, text, labelWidth = labelWidth) { newText ->
            val actions = components
                .filter { valueGetter(dataGetter(it)) != newText }
                .map {
                    val oldData = dataGetter(it)
                    val newData = valueSetter(oldData, newText)
                    actionMapper(it, oldData, newData)
                }
            if (actions.isNotEmpty()) {
                FusedAction(actions).apply()
            }
        }
    }
}

fun UiScope.entityEditorPanel(
    title: String,
    imageIcon: IconProvider? = null,
    onRemove: (() -> Unit)? = null,
    titleWidth: Dimension = Grow.Std,
    headerContent: (RowScope.() -> Unit)? = null,
    startExpanded: Boolean = true,
    onCollapseChanged: ((Boolean) -> Unit)? = null,
    scopeName: String = title,
    block: ColumnScope.() -> Any?
) = collapsablePanel(
    title,
    imageIcon,
    titleWidth = titleWidth,
    startExpanded = startExpanded,
    onCollapseChanged = onCollapseChanged,
    scopeName = scopeName,
    headerContent = {
        headerContent?.invoke(this)
        onRemove?.let { remove ->
            Box {
                var isHovered by remember(false)
                val fgColor = colors.onBackground
                val bgColor = if (isHovered) MdColor.RED else colors.componentBg

                modifier
                    .alignY(AlignmentY.Center)
                    .margin(end = sizes.gap * 0.75f)
                    .padding(sizes.smallGap * 0.5f)
                    .onEnter { isHovered = true }
                    .onExit { isHovered = false }
                    .onClick { remove() }
                    .background(CircularBackground(bgColor))

                Image {
                    modifier.iconImage(Icons.small.trash, fgColor)
                }
            }
        }
    },
) {
    block()
}
