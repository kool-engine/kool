package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.BehaviorProperty
import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.actions.SetBehaviorPropertyAction
import de.fabmax.kool.editor.components.BehaviorComponent
import de.fabmax.kool.editor.data.PropertyValue
import de.fabmax.kool.editor.data.Vec2Data
import de.fabmax.kool.editor.data.Vec3Data
import de.fabmax.kool.editor.data.Vec4Data
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logW

class BehaviorEditor(component: BehaviorComponent) : ComponentEditor<BehaviorComponent>(component) {

    private val behaviorName: String get() = camelCaseToWords(component.behaviorClassNameState.value)

    override fun UiScope.compose() = componentPanel(
        title = behaviorName,
        imageIcon = IconMap.small.code,
        onRemove = ::removeComponent,

        headerContent = {
            iconButton(IconMap.small.edit, "Edit source code") {
                KoolEditor.instance.editBehaviorSource(component.componentData.behaviorClassName)
            }
            Box(width = sizes.smallGap) {  }
        }
    ) {

        Column(width = Grow.Std) {
            modifier
                .padding(horizontal = sizes.gap)
                .padding(top = sizes.gap)
                .margin(bottom = sizes.gap)

            labeledSwitch("Run in edit mode", component.runInEditMode.use()) {
                component.runInEditMode.set(it)
            }

            val behavior = component.behaviorInstance.use() ?: return@Column
            val behaviorProperties = KoolEditor.instance.loadedApp.use()?.behaviorClasses?.get(behavior::class)?.properties
            if (behaviorProperties == null) {
                logE { "Unable to get KoolBehavior class for behavior ${component.behaviorInstance.value}" }
            }

            if (!behaviorProperties.isNullOrEmpty()) {
                menuDivider()
            }

            behaviorProperties?.forEach { prop ->
                when (prop.type) {
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

                    else -> {
                        logW { "Type is not editable: ${prop.type} (in behavior: ${component.behaviorInstance})" }
                    }
                }
            }
        }
    }

    private fun UiScope.doubleEditor(prop: BehaviorProperty) {
        val propValue = prop.get(component) as Double
        val editHandler = ActionValueEditHandler<Double> { undoValue, applyValue ->
            val newValue = PropertyValue(d1 = applyValue)
            val oldValue = PropertyValue(d1 = undoValue)
            SetBehaviorPropertyAction(component, prop.name, oldValue, newValue) {
                surface.triggerUpdate()
                prop.set(component, it.d1!!)
                component.componentData.propertyValues[prop.name] = it
            }
        }

        labeledDoubleTextField(
            prop.label,
            propValue,
            prop.getPrecision(propValue),
            dragChangeSpeed = prop.dragChangeSpeed,
            minValue = prop.min,
            maxValue = prop.max,
            editHandler = editHandler
        )
    }

    private fun UiScope.vec2dEditor(prop: BehaviorProperty) {
        val propValue = prop.get(component) as Vec2d
        val editHandler = ActionValueEditHandler<Vec2d> { undoValue, applyValue ->
            val newValue = PropertyValue(d2 = Vec2Data(applyValue))
            val oldValue = PropertyValue(d2 = Vec2Data(undoValue))
            SetBehaviorPropertyAction(component, prop.name, oldValue, newValue) {
                surface.triggerUpdate()
                prop.set(component, it.d2!!.toVec2d())
                component.componentData.propertyValues[prop.name] = it
            }
        }

        labeledXyRow(
            prop.label,
            propValue,
            dragChangeSpeed = Vec2d(prop.dragChangeSpeed),
            minValues = Vec2d(prop.min),
            maxValues = Vec2d(prop.max),
            editHandler = editHandler
        )
    }

    private fun UiScope.vec3dEditor(prop: BehaviorProperty) {
        val propValue = prop.get(component) as Vec3d
        val editHandler = ActionValueEditHandler<Vec3d> { undoValue, applyValue ->
            val newValue = PropertyValue(d3 = Vec3Data(applyValue))
            val oldValue = PropertyValue(d3 = Vec3Data(undoValue))
            SetBehaviorPropertyAction(component, prop.name, oldValue, newValue) {
                surface.triggerUpdate()
                prop.set(component, it.d3!!.toVec3d())
                component.componentData.propertyValues[prop.name] = it
            }
        }

        labeledXyzRow(
            prop.label,
            propValue,
            dragChangeSpeed = Vec3d(prop.dragChangeSpeed),
            minValues = Vec3d(prop.min),
            maxValues = Vec3d(prop.max),
            editHandler = editHandler
        )
    }

    private fun UiScope.vec4dEditor(prop: BehaviorProperty) {
        val propValue = prop.get(component) as Vec4d
        val editHandler = ActionValueEditHandler<Vec4d> { undoValue, applyValue ->
            val newValue = PropertyValue(d4 = Vec4Data(applyValue))
            val oldValue = PropertyValue(d4 = Vec4Data(undoValue))
            SetBehaviorPropertyAction(component, prop.name, oldValue, newValue) {
                surface.triggerUpdate()
                prop.set(component, it.d4!!.toVec4d())
                component.componentData.propertyValues[prop.name] = it
            }
        }

        labeledXyzwRow(
            prop.label,
            propValue,
            dragChangeSpeed = Vec4d(prop.dragChangeSpeed),
            minValues = Vec4d(prop.min),
            maxValues = Vec4d(prop.max),
            editHandler = editHandler
        )
    }

    private fun UiScope.floatEditor(prop: BehaviorProperty) {
        val propValue = prop.get(component) as Float
        val editHandler = ActionValueEditHandler<Double> { undoValue, applyValue ->
            val newValue = PropertyValue(f1 = applyValue.toFloat())
            val oldValue = PropertyValue(f1 = undoValue.toFloat())
            SetBehaviorPropertyAction(component, prop.name, oldValue, newValue) {
                surface.triggerUpdate()
                prop.set(component, it.f1!!)
                component.componentData.propertyValues[prop.name] = it
            }
        }

        labeledDoubleTextField(
            prop.label,
            propValue.toDouble(),
            prop.getPrecision(propValue.toDouble()),
            dragChangeSpeed = prop.dragChangeSpeed,
            minValue = prop.min,
            maxValue = prop.max,
            editHandler = editHandler
        )
    }

    private fun UiScope.vec2fEditor(prop: BehaviorProperty) {
        val propValue = prop.get(component) as Vec2f
        val editHandler = ActionValueEditHandler<Vec2d> { undoValue, applyValue ->
            val newValue = PropertyValue(f2 = Vec2Data(applyValue))
            val oldValue = PropertyValue(f2 = Vec2Data(undoValue))
            SetBehaviorPropertyAction(component, prop.name, oldValue, newValue) {
                surface.triggerUpdate()
                prop.set(component, it.f2!!.toVec2f())
                component.componentData.propertyValues[prop.name] = it
            }
        }

        labeledXyRow(
            prop.label,
            propValue.toVec2d(),
            dragChangeSpeed = Vec2d(prop.dragChangeSpeed),
            minValues = Vec2d(prop.min),
            maxValues = Vec2d(prop.max),
            editHandler = editHandler
        )
    }

    private fun UiScope.vec3fEditor(prop: BehaviorProperty) {
        val propValue = prop.get(component) as Vec3f
        val editHandler = ActionValueEditHandler<Vec3d> { undoValue, applyValue ->
            val newValue = PropertyValue(f3 = Vec3Data(applyValue))
            val oldValue = PropertyValue(f3 = Vec3Data(undoValue))
            SetBehaviorPropertyAction(component, prop.name, oldValue, newValue) {
                surface.triggerUpdate()
                prop.set(component, it.f3!!.toVec3f())
                component.componentData.propertyValues[prop.name] = it
            }
        }

        labeledXyzRow(
            prop.label,
            propValue.toVec3d(),
            dragChangeSpeed = Vec3d(prop.dragChangeSpeed),
            minValues = Vec3d(prop.min),
            maxValues = Vec3d(prop.max),
            editHandler = editHandler
        )
    }

    private fun UiScope.vec4fEditor(prop: BehaviorProperty) {
        val propValue = prop.get(component) as Vec4f
        val editHandler = ActionValueEditHandler<Vec4d> { undoValue, applyValue ->
            val newValue = PropertyValue(f4 = Vec4Data(applyValue))
            val oldValue = PropertyValue(f4 = Vec4Data(undoValue))
            SetBehaviorPropertyAction(component, prop.name, oldValue, newValue) {
                surface.triggerUpdate()
                prop.set(component, it.f4!!.toVec4f())
                component.componentData.propertyValues[prop.name] = it
            }
        }

        labeledXyzwRow(
            prop.label,
            propValue.toVec4d(),
            dragChangeSpeed = Vec4d(prop.dragChangeSpeed),
            minValues = Vec4d(prop.min),
            maxValues = Vec4d(prop.max),
            editHandler = editHandler
        )
    }

    private fun UiScope.intEditor(prop: BehaviorProperty) {
        val propValue = prop.get(component) as Int
        val editHandler = ActionValueEditHandler<Int> { undoValue, applyValue ->
            val newValue = PropertyValue(i1 = applyValue)
            val oldValue = PropertyValue(i1 = undoValue)
            SetBehaviorPropertyAction(component, prop.name, oldValue, newValue) {
                surface.triggerUpdate()
                prop.set(component, it.i1!!)
                component.componentData.propertyValues[prop.name] = it
            }
        }

        labeledIntTextField(
            prop.label,
            propValue,
            dragChangeSpeed = prop.dragChangeSpeed,
            minValue = prop.min.toInt(),
            maxValue = prop.max.toInt(),
            editHandler = editHandler
        )
    }

    private fun UiScope.vec2iEditor(prop: BehaviorProperty) {
        val propValue = prop.get(component) as Vec2i
        val editHandler = ActionValueEditHandler<Vec2d> { undoValue, applyValue ->
            val newValue = PropertyValue(i2 = Vec2Data(applyValue))
            val oldValue = PropertyValue(i2 = Vec2Data(undoValue))
            SetBehaviorPropertyAction(component, prop.name, oldValue, newValue) {
                surface.triggerUpdate()
                prop.set(component, it.i2!!.toVec2i())
                component.componentData.propertyValues[prop.name] = it
            }
        }

        labeledXyRow(
            prop.label,
            propValue.toVec2d(),
            dragChangeSpeed = Vec2d(prop.dragChangeSpeed),
            minValues = Vec2d(prop.min),
            maxValues = Vec2d(prop.max),
            editHandler = editHandler
        )
    }

    private fun UiScope.vec3iEditor(prop: BehaviorProperty) {
        val propValue = prop.get(component) as Vec3i
        val editHandler = ActionValueEditHandler<Vec3d> { undoValue, applyValue ->
            val newValue = PropertyValue(i3 = Vec3Data(applyValue))
            val oldValue = PropertyValue(i3 = Vec3Data(undoValue))
            SetBehaviorPropertyAction(component, prop.name, oldValue, newValue) {
                surface.triggerUpdate()
                prop.set(component, it.i3!!.toVec3i())
                component.componentData.propertyValues[prop.name] = it
            }
        }

        labeledXyzRow(
            prop.label,
            propValue.toVec3d(),
            dragChangeSpeed = Vec3d(prop.dragChangeSpeed),
            minValues = Vec3d(prop.min),
            maxValues = Vec3d(prop.max),
            editHandler = editHandler
        )
    }

    private fun UiScope.vec4iEditor(prop: BehaviorProperty) {
        val propValue = prop.get(component) as Vec4i
        val editHandler = ActionValueEditHandler<Vec4d> { undoValue, applyValue ->
            val newValue = PropertyValue(i4 = Vec4Data(applyValue))
            val oldValue = PropertyValue(i4 = Vec4Data(undoValue))
            SetBehaviorPropertyAction(component, prop.name, oldValue, newValue) {
                surface.triggerUpdate()
                prop.set(component, it.i4!!.toVec4i())
                component.componentData.propertyValues[prop.name] = it
            }
        }

        labeledXyzwRow(
            prop.label,
            propValue.toVec4d(),
            dragChangeSpeed = Vec4d(prop.dragChangeSpeed),
            minValues = Vec4d(prop.min),
            maxValues = Vec4d(prop.max),
            editHandler = editHandler
        )
    }

    private fun Vec2i.toVec2d() = Vec2d(x.toDouble(), y.toDouble())
    private fun Vec3i.toVec3d() = Vec3d(x.toDouble(), y.toDouble(), z.toDouble())
    private fun Vec4i.toVec4d() = Vec4d(x.toDouble(), y.toDouble(), z.toDouble(), w.toDouble())

    private fun BehaviorProperty.getPrecision(value: Double): Int {
        return if (isRanged) {
            precisionForValue(max - min)
        } else {
            precisionForValue(value)
        }
    }

    private val BehaviorProperty.dragChangeSpeed: Double
        get() = if (isRanged) (max - min) / 1000.0 else 0.05

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