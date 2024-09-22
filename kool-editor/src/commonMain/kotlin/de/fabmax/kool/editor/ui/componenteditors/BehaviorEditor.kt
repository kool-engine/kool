package de.fabmax.kool.editor.ui.componenteditors

import de.fabmax.kool.editor.*
import de.fabmax.kool.editor.actions.SetBehaviorPropertyAction
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.api.KoolBehavior
import de.fabmax.kool.editor.components.BehaviorComponent
import de.fabmax.kool.editor.components.GameEntityComponent
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.editor.ui.ComboBoxItems
import de.fabmax.kool.editor.ui.Icons
import de.fabmax.kool.editor.ui.iconButton
import de.fabmax.kool.editor.ui.precisionForValue
import de.fabmax.kool.editor.util.gameEntity
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ui2.Box
import de.fabmax.kool.modules.ui2.ColumnScope
import de.fabmax.kool.modules.ui2.UiScope
import de.fabmax.kool.modules.ui2.remember
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logW
import kotlin.math.roundToInt
import kotlin.reflect.KClass

class BehaviorEditor : ComponentEditor<BehaviorComponent>() {

    private val behaviorName: String get() = camelCaseToWords(components[0].data.behaviorClassName)

    override fun UiScope.compose() = componentPanel(
        title = behaviorName,
        imageIcon = Icons.small.code,
        onRemove = ::removeComponent,

        headerContent = {
            iconButton(Icons.small.edit, "Edit source code") {
                KoolEditor.instance.editBehaviorSource(components[0].data.behaviorClassName)
            }
            Box(width = sizes.smallGap) {  }
        }
    ) {
        val behavior = components[0].behaviorInstance.use() ?: return@componentPanel Unit
        val behaviorProperties = KoolEditor.instance.loadedApp.use()?.behaviorClasses?.get(behavior::class)?.properties
        if (behaviorProperties == null) {
            logE { "Unable to get KoolBehavior class for behavior ${components[0].behaviorInstance.value}" }
        }

        behaviorProperties?.forEach { prop ->
            when (prop.kType.classifier) {
                Double::class -> doubleEditor(prop)
                Vec2d::class -> vec2dEditor(prop)
                Vec3d::class -> vec3dEditor(prop)
                Vec4d::class -> vec4dEditor(prop)

                Float::class -> floatEditor(prop)
                Vec2f::class -> vec2fEditor(prop)
                Vec3f::class -> vec3fEditor(prop)
                Vec4f::class -> vec4fEditor(prop)

                Int::class -> intEditor(prop)
                Vec2i::class -> vec2iEditor(prop)
                Vec3i::class -> vec3iEditor(prop)
                Vec4i::class -> vec4iEditor(prop)

                Boolean::class -> boolEditor(prop)
                Color::class -> colorEditor(prop)
                String::class -> { stringEditor(prop) }
                GameEntity::class -> gameEntityEditor(prop)

                else -> {
                    when (prop.type) {
                        BehaviorPropertyType.COMPONENT -> componentEditor(prop)
                        BehaviorPropertyType.BEHAVIOR -> behaviorComponentEditor(prop)
                        else -> {
                            val behaviorName = components[0].behaviorInstance.value?.let { it::class.simpleName } ?: "null"
                            logW { "Type is not editable: ${prop.kType} (in behavior: $behaviorName)" }
                        }
                    }
                }
            }
        }
    }

    private fun ColumnScope.gameEntityEditor(prop: BehaviorProperty) {
        val choices = remember {
            ComboBoxItems(listOf(null) + scene.sceneEntities.values.map { GameEntityChoice(it) })
        }
        choicePropertyEditor(
            choices = choices,
            dataGetter = { PropertyValue(gameEntityRef = prop.getGameEntity(it)?.id ?: EntityId.NULL) },
            valueGetter = { GameEntityChoice(it.gameEntityRef?.gameEntity) },
            valueSetter = { _, newValue -> PropertyValue(gameEntityRef = newValue?.gameEntity?.id ?: EntityId.NULL) },
            actionMapper = SetBehaviorPropertyAction(prop),
            label = prop.label
        )
    }

    private fun ColumnScope.componentEditor(prop: BehaviorProperty) {
        val choices = remember {
            val klass = prop.kType.classifier as KClass<*>
            val selComponents = listOf(null) + scene.sceneEntities.values
                .flatMap { it.components }
                .filter { klass.isInstance(it) }
            ComboBoxItems(selComponents.map { ComponentChoice(it) })
        }
        choicePropertyEditor(
            choices = choices,
            dataGetter = { PropertyValue(componentRef = ComponentRef(prop.getComponent(it))) },
            valueGetter = { ComponentChoice(it.componentRef!!.entityId.gameEntity?.getComponent(it.componentRef!!)) },
            valueSetter = { _, newValue -> PropertyValue(componentRef = ComponentRef(newValue.component)) },
            actionMapper = SetBehaviorPropertyAction(prop),
            label = prop.label
        )
    }

    private fun ColumnScope.behaviorComponentEditor(prop: BehaviorProperty) {
        val choices = remember {
            val className = (prop.kType.classifier as KClass<*>).simpleName
            val behaviorComponents = listOf(null) + scene.getAllComponents<BehaviorComponent>().filter {
                it.data.behaviorClassName.substringAfterLast('.') == className
            }
            ComboBoxItems(behaviorComponents.map { BehaviorChoice(it?.behaviorInstance?.value) })
        }
        choicePropertyEditor(
            choices = choices,
            dataGetter = { PropertyValue(behaviorRef = BehaviorRef(prop.getBehavior(it))) },
            valueGetter = { BehaviorChoice(it.behaviorRef!!.entityId.gameEntity?.getBehavior(it.behaviorRef!!)) },
            valueSetter = { _, newValue -> PropertyValue(behaviorRef = BehaviorRef(newValue.behavior)) },
            actionMapper = SetBehaviorPropertyAction(prop),
            label = prop.label
        )
    }

    private fun ColumnScope.boolEditor(prop: BehaviorProperty) = booleanPropertyEditor(
        dataGetter = { PropertyValue(bool = prop.getBoolean(it)) },
        valueGetter = { it.bool!! },
        valueSetter = { _, newValue -> PropertyValue(bool = newValue) },
        actionMapper = SetBehaviorPropertyAction(prop),
        label = prop.label
    )

    private fun ColumnScope.colorEditor(prop: BehaviorProperty) = colorPropertyEditor(
        dataGetter = { PropertyValue(color = ColorData(prop.getColor(it) ?: Color.BLACK, isLinear = false)) },
        valueGetter = { it.color!!.toColorSrgb() },
        valueSetter = { _, newValue -> PropertyValue(color = ColorData(newValue, false)) },
        actionMapper = SetBehaviorPropertyAction(prop),
        label = prop.label
    )

    private fun ColumnScope.stringEditor(prop: BehaviorProperty) = stringPropertyEditor(
        dataGetter = { PropertyValue(str = prop.getString(it)) },
        valueGetter = { it.str!! },
        valueSetter = { _, newValue -> PropertyValue(str = newValue) },
        actionMapper = SetBehaviorPropertyAction(prop),
        label = prop.label
    )

    private fun ColumnScope.doubleEditor(prop: BehaviorProperty) = doublePropertyEditor(
        dataGetter = { PropertyValue(d1 = prop.getDouble(it)) },
        valueGetter = { it.d1!! },
        valueSetter = { _, newValue -> PropertyValue(d1 = newValue) },
        actionMapper = SetBehaviorPropertyAction(prop),
        label = prop.label,
        dragChangeSpeed = prop.dragChangeSpeed,
        minValue = prop.min.x,
        maxValue = prop.max.x,
        precision = { if (prop.precision > 0) prop.precision else precisionForValue(it) }
    )

    private fun ColumnScope.vec2dEditor(prop: BehaviorProperty) = vec2dPropertyEditor(
        dataGetter = { PropertyValue(d2 = Vec2Data(prop.getVec2d(it))) },
        valueGetter = { it.d2!!.toVec2d() },
        valueSetter = { _, newValue -> PropertyValue(d2 = Vec2Data(newValue)) },
        actionMapper = SetBehaviorPropertyAction(prop),
        label = prop.label,
        dragChangeSpeed = Vec2d(prop.dragChangeSpeed),
        minValues = prop.min.xy,
        maxValues = prop.max.xy,
        precision = { if (prop.precision > 0) Vec2i(prop.precision) else Vec2i(precisionForValue(it.x), precisionForValue(it.y)) }
    )

    private fun ColumnScope.vec3dEditor(prop: BehaviorProperty) = vec3dPropertyEditor(
        dataGetter = { PropertyValue(d3 = Vec3Data(prop.getVec3d(it))) },
        valueGetter = { it.d3!!.toVec3d() },
        valueSetter = { _, newValue -> PropertyValue(d3 = Vec3Data(newValue)) },
        actionMapper = SetBehaviorPropertyAction(prop),
        label = prop.label,
        dragChangeSpeed = Vec3d(prop.dragChangeSpeed),
        minValues = prop.min.xyz,
        maxValues = prop.max.xyz,
        precision = { if (prop.precision > 0) Vec3i(prop.precision) else Vec3i(precisionForValue(it.x), precisionForValue(it.y), precisionForValue(it.z)) }
    )

    private fun ColumnScope.vec4dEditor(prop: BehaviorProperty) = vec4dPropertyEditor(
        dataGetter = { PropertyValue(d4 = Vec4Data(prop.getVec4d(it))) },
        valueGetter = { it.d4!!.toVec4d() },
        valueSetter = { _, newValue -> PropertyValue(d4 = Vec4Data(newValue)) },
        actionMapper = SetBehaviorPropertyAction(prop),
        label = prop.label,
        dragChangeSpeed = Vec4d(prop.dragChangeSpeed),
        minValues = prop.min,
        maxValues = prop.max,
        precision = { if (prop.precision > 0) Vec4i(prop.precision) else Vec4i(precisionForValue(it.x), precisionForValue(it.y), precisionForValue(it.z), precisionForValue(it.w)) }
    )

    private fun ColumnScope.floatEditor(prop: BehaviorProperty) = doublePropertyEditor(
        dataGetter = { PropertyValue(f1 = prop.getFloat(it)) },
        valueGetter = { it.f1!!.toDouble() },
        valueSetter = { _, newValue -> PropertyValue(f1 = newValue.toFloat()) },
        actionMapper = SetBehaviorPropertyAction(prop),
        label = prop.label,
        dragChangeSpeed = prop.dragChangeSpeed,
        minValue = prop.min.x,
        maxValue = prop.max.x,
        precision = { if (prop.precision > 0) prop.precision else precisionForValue(it) }
    )

    private fun ColumnScope.vec2fEditor(prop: BehaviorProperty) = vec2dPropertyEditor(
        dataGetter = { PropertyValue(f2 = Vec2Data(prop.getVec2f(it))) },
        valueGetter = { it.f2!!.toVec2d() },
        valueSetter = { _, newValue -> PropertyValue(f2 = Vec2Data(newValue)) },
        actionMapper = SetBehaviorPropertyAction(prop),
        label = prop.label,
        dragChangeSpeed = Vec2d(prop.dragChangeSpeed),
        minValues = prop.min.xy,
        maxValues = prop.max.xy,
        precision = { if (prop.precision > 0) Vec2i(prop.precision) else Vec2i(precisionForValue(it.x), precisionForValue(it.y)) }
    )

    private fun ColumnScope.vec3fEditor(prop: BehaviorProperty)  = vec3dPropertyEditor(
        dataGetter = { PropertyValue(f3 = Vec3Data(prop.getVec3f(it))) },
        valueGetter = { it.f3!!.toVec3d() },
        valueSetter = { _, newValue -> PropertyValue(f3 = Vec3Data(newValue)) },
        actionMapper = SetBehaviorPropertyAction(prop),
        label = prop.label,
        dragChangeSpeed = Vec3d(prop.dragChangeSpeed),
        minValues = prop.min.xyz,
        maxValues = prop.max.xyz,
        precision = { if (prop.precision > 0) Vec3i(prop.precision) else Vec3i(precisionForValue(it.x), precisionForValue(it.y), precisionForValue(it.z)) }
    )

    private fun ColumnScope.vec4fEditor(prop: BehaviorProperty) = vec4dPropertyEditor(
        dataGetter = { PropertyValue(f4 = Vec4Data(prop.getVec4f(it))) },
        valueGetter = { it.f4!!.toVec4d() },
        valueSetter = { _, newValue -> PropertyValue(f4 = Vec4Data(newValue)) },
        actionMapper = SetBehaviorPropertyAction(prop),
        label = prop.label,
        dragChangeSpeed = Vec4d(prop.dragChangeSpeed),
        minValues = prop.min,
        maxValues = prop.max,
        precision = { if (prop.precision > 0) Vec4i(prop.precision) else Vec4i(precisionForValue(it.x), precisionForValue(it.y), precisionForValue(it.z), precisionForValue(it.w)) }
    )

    private fun ColumnScope.intEditor(prop: BehaviorProperty) = doublePropertyEditor(
        dataGetter = { PropertyValue(i1 = prop.getInt(it)) },
        valueGetter = { it.i1!!.toDouble() },
        valueSetter = { _, newValue -> PropertyValue(i1 = newValue.roundToInt()) },
        actionMapper = SetBehaviorPropertyAction(prop),
        label = prop.label,
        precision = { 0 },
        dragChangeSpeed = prop.dragChangeSpeed,
        minValue = prop.min.x,
        maxValue = prop.max.x,
    )

    private fun ColumnScope.vec2iEditor(prop: BehaviorProperty) = vec2dPropertyEditor(
        dataGetter = { PropertyValue(i2 = Vec2Data(prop.getVec2i(it))) },
        valueGetter = { it.i2!!.toVec2d() },
        valueSetter = { _, newValue -> PropertyValue(i2 = Vec2Data(newValue)) },
        actionMapper = SetBehaviorPropertyAction(prop),
        label = prop.label,
        precision = { Vec2i.ZERO },
        dragChangeSpeed = Vec2d(prop.dragChangeSpeed),
        minValues = prop.min.xy,
        maxValues = prop.max.xy,
    )

    private fun ColumnScope.vec3iEditor(prop: BehaviorProperty) = vec3dPropertyEditor(
        dataGetter = { PropertyValue(i3 = Vec3Data(prop.getVec3i(it))) },
        valueGetter = { it.i3!!.toVec3d() },
        valueSetter = { _, newValue -> PropertyValue(i3 = Vec3Data(newValue)) },
        actionMapper = SetBehaviorPropertyAction(prop),
        label = prop.label,
        precision = { Vec3i.ZERO },
        dragChangeSpeed = Vec3d(prop.dragChangeSpeed),
        minValues = prop.min.xyz,
        maxValues = prop.max.xyz,
    )

    private fun ColumnScope.vec4iEditor(prop: BehaviorProperty) = vec4dPropertyEditor(
        dataGetter = { PropertyValue(i4 = Vec4Data(prop.getVec4i(it))) },
        valueGetter = { it.i4!!.toVec4d() },
        valueSetter = { _, newValue -> PropertyValue(i4 = Vec4Data(newValue)) },
        actionMapper = SetBehaviorPropertyAction(prop),
        label = prop.label,
        precision = { Vec4i.ZERO },
        dragChangeSpeed = Vec4d(prop.dragChangeSpeed),
        minValues = prop.min,
        maxValues = prop.max,
    )

    private fun Vec2i.toVec2d() = Vec2d(x.toDouble(), y.toDouble())
    private fun Vec3i.toVec3d() = Vec3d(x.toDouble(), y.toDouble(), z.toDouble())
    private fun Vec4i.toVec4d() = Vec4d(x.toDouble(), y.toDouble(), z.toDouble(), w.toDouble())

    private val BehaviorProperty.dragChangeSpeed: Double
        get() = if (isRanged) (max.x - min.x) / 1000.0 else 0.05

    private fun BehaviorProperty.getPrecision(value: Double): Int {
        return if (isRanged) {
            precisionForValue(max.x - min.x)
        } else if (value.isFinite()) {
            precisionForValue(value)
        } else {
            0
        }
    }

    private fun BehaviorProperty.setProperty(behaviorComponent: BehaviorComponent, value: PropertyValue): Boolean {
        return when {
            value.bool != null -> set(behaviorComponent, value.bool)

            value.d1 != null -> set(behaviorComponent, value.d1)
            value.d2 != null -> set(behaviorComponent, value.d2!!.toVec2d())
            value.d3 != null -> set(behaviorComponent, value.d3!!.toVec3d())
            value.d4 != null -> set(behaviorComponent, value.d4!!.toVec4d())

            value.f1 != null -> set(behaviorComponent, value.f1)
            value.f2 != null -> set(behaviorComponent, value.f2!!.toVec2f())
            value.f3 != null -> set(behaviorComponent, value.f3!!.toVec3f())
            value.f4 != null -> set(behaviorComponent, value.f4!!.toVec4f())

            value.i1 != null -> set(behaviorComponent, value.i1)
            value.i2 != null -> set(behaviorComponent, value.i2!!.toVec2i())
            value.i3 != null -> set(behaviorComponent, value.i3!!.toVec3i())
            value.i4 != null -> set(behaviorComponent, value.i4!!.toVec4i())

            value.color != null -> set(behaviorComponent, if (value.color!!.isLinear) value.color!!.toColorLinear() else value.color!!.toColorSrgb())
            value.transform != null -> set(behaviorComponent, value.transform!!.toMat4d())
            value.str != null -> set(behaviorComponent, value.str)
            value.gameEntityRef != null -> set(behaviorComponent, value.gameEntityRef!!.gameEntity)
            value.componentRef != null -> set(behaviorComponent, value.componentRef!!.entityId.gameEntity?.getComponent(value.componentRef!!))
            value.behaviorRef != null -> set(behaviorComponent, value.behaviorRef!!.entityId.gameEntity?.getBehavior(value.behaviorRef!!))

            else -> error("PropertyValue has no non-null value")
        }
    }

    private fun UiScope.SetBehaviorPropertyAction(prop: BehaviorProperty): (BehaviorComponent, PropertyValue, PropertyValue) -> SetBehaviorPropertyAction {
        return { component: BehaviorComponent, undoData: PropertyValue, applyData: PropertyValue ->
            SetBehaviorPropertyAction(component.gameEntity.id, prop.name, component.data.behaviorClassName, undoData, applyData) { comp, value ->
                prop.setProperty(comp, value)
                val props = comp.data.propertyValues.toMutableMap()
                props[prop.name] = value
                comp.setPersistent(comp.data.copy(propertyValues = props))
                surface.triggerUpdate()
            }
        }
    }

    private data class GameEntityChoice(val gameEntity: GameEntity?) {
        override fun toString(): String = gameEntity?.name ?: "None"
    }

    private data class ComponentChoice(val component: GameEntityComponent?) {
        override fun toString(): String = component?.gameEntity?.name ?: "None"
    }

    private data class BehaviorChoice(val behavior: KoolBehavior?) {
        override fun toString(): String = behavior?.gameEntity?.name ?: "None"
    }

    private val BehaviorProperty.isRanged: Boolean
        get() = min.x > Double.NEGATIVE_INFINITY && max.x < Double.POSITIVE_INFINITY &&
                min.y > Double.NEGATIVE_INFINITY && max.y < Double.POSITIVE_INFINITY &&
                min.z > Double.NEGATIVE_INFINITY && max.z < Double.POSITIVE_INFINITY &&
                min.w > Double.NEGATIVE_INFINITY && max.w < Double.POSITIVE_INFINITY

    companion object {
        fun camelCaseToWords(camelCase: String, allUppercase: Boolean = true): String {
            val simpleName = camelCase.substringAfterLast('.')

            val words = mutableListOf<String>()
            var word = StringBuilder()
            simpleName.forEach {
                if (word.isEmpty() || it.isLowerCase() || it.isDigit()) {
                    word.append(it)

                } else {
                    words += word.toString()
                    word = StringBuilder().append(it)
                }
            }
            if (word.isNotEmpty()) {
                words += word.toString()
            }
            return if (allUppercase) {
                words.joinToString(" ") { it.lowercase().replaceFirstChar { c -> c.uppercase() } }
            } else {
                words.joinToString(" ") { it.lowercase() }.replaceFirstChar { c -> c.uppercase() }
            }
        }
    }
}