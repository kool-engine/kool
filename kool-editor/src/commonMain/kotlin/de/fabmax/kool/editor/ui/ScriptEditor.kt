package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.EditorState
import de.fabmax.kool.editor.ScriptProperty
import de.fabmax.kool.editor.actions.SetScriptPropertyAction
import de.fabmax.kool.editor.components.ScriptComponent
import de.fabmax.kool.editor.data.PropertyValue
import de.fabmax.kool.editor.data.Vec2Data
import de.fabmax.kool.editor.data.Vec3Data
import de.fabmax.kool.editor.data.Vec4Data
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.logW

class ScriptEditor(component: ScriptComponent) : ComponentEditor<ScriptComponent>(component) {

    private val scriptName: String get() = component.scriptClassNameState.value
        .replaceBeforeLast('.', "")
        .removePrefix(".")

    override fun UiScope.compose() = componentPanel(scriptName, ::removeComponent) {
        Column(width = Grow.Std) {
            modifier
                .padding(horizontal = sizes.gap)
                .padding(top = sizes.gap)
                .margin(bottom = sizes.gap)

            labeledSwitch("Run in edit mode", component.runInEditMode.use()) {
                component.runInEditMode.set(it)
            }

            val scriptProperties = component.scriptInstance.use()?.let {
                EditorState.loadedApp.use()?.scriptClasses?.get(it::class)?.properties
            }

            if (!scriptProperties.isNullOrEmpty()) {
                menuDivider()
            }

            scriptProperties?.forEach { prop ->
                when (prop.type.classifier) {
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
                        logW { "Type is not editable: ${prop.type} (in script: ${component.scriptInstance})" }
                    }
                }
            }
        }
    }

    private fun UiScope.doubleEditor(prop: ScriptProperty) {
        val propValue = prop.get(component) as Double
        val editHandler = ActionValueEditHandler<Double> { undoValue, applyValue ->
            val newValue = PropertyValue(d1 = applyValue)
            val oldValue = PropertyValue(d1 = undoValue)
            SetScriptPropertyAction(component, prop.name, oldValue, newValue) {
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

    private fun UiScope.vec2dEditor(prop: ScriptProperty) {
        val propValue = prop.get(component) as Vec2d
        val editHandler = ActionValueEditHandler<Vec2d> { undoValue, applyValue ->
            val newValue = PropertyValue(d2 = Vec2Data(applyValue))
            val oldValue = PropertyValue(d2 = Vec2Data(undoValue))
            SetScriptPropertyAction(component, prop.name, oldValue, newValue) {
                surface.triggerUpdate()
                prop.set(component, it.d2!!.toVec2d())
                component.componentData.propertyValues[prop.name] = it
            }
        }

        xyRow(
            prop.label,
            propValue,
            dragChangeSpeed = Vec2d(prop.dragChangeSpeed),
            minValues = Vec2d(prop.min),
            maxValues = Vec2d(prop.max),
            editHandler = editHandler
        )
    }

    private fun UiScope.vec3dEditor(prop: ScriptProperty) {
        val propValue = prop.get(component) as Vec3d
        val editHandler = ActionValueEditHandler<Vec3d> { undoValue, applyValue ->
            val newValue = PropertyValue(d3 = Vec3Data(applyValue))
            val oldValue = PropertyValue(d3 = Vec3Data(undoValue))
            SetScriptPropertyAction(component, prop.name, oldValue, newValue) {
                surface.triggerUpdate()
                prop.set(component, it.d3!!.toVec3d())
                component.componentData.propertyValues[prop.name] = it
            }
        }

        xyzRow(
            prop.label,
            propValue,
            dragChangeSpeed = Vec3d(prop.dragChangeSpeed),
            minValues = Vec3d(prop.min),
            maxValues = Vec3d(prop.max),
            editHandler = editHandler
        )
    }

    private fun UiScope.vec4dEditor(prop: ScriptProperty) {
        val propValue = prop.get(component) as Vec4d
        val editHandler = ActionValueEditHandler<Vec4d> { undoValue, applyValue ->
            val newValue = PropertyValue(d4 = Vec4Data(applyValue))
            val oldValue = PropertyValue(d4 = Vec4Data(undoValue))
            SetScriptPropertyAction(component, prop.name, oldValue, newValue) {
                surface.triggerUpdate()
                prop.set(component, it.d4!!.toVec4d())
                component.componentData.propertyValues[prop.name] = it
            }
        }

        xyzwRow(
            prop.label,
            propValue,
            dragChangeSpeed = Vec4d(prop.dragChangeSpeed),
            minValues = Vec4d(prop.min),
            maxValues = Vec4d(prop.max),
            editHandler = editHandler
        )
    }

    private fun UiScope.floatEditor(prop: ScriptProperty) {
        val propValue = prop.get(component) as Float
        val editHandler = ActionValueEditHandler<Double> { undoValue, applyValue ->
            val newValue = PropertyValue(f1 = applyValue.toFloat())
            val oldValue = PropertyValue(f1 = undoValue.toFloat())
            SetScriptPropertyAction(component, prop.name, oldValue, newValue) {
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

    private fun UiScope.vec2fEditor(prop: ScriptProperty) {
        val propValue = prop.get(component) as Vec2f
        val editHandler = ActionValueEditHandler<Vec2d> { undoValue, applyValue ->
            val newValue = PropertyValue(f2 = Vec2Data(applyValue))
            val oldValue = PropertyValue(f2 = Vec2Data(undoValue))
            SetScriptPropertyAction(component, prop.name, oldValue, newValue) {
                surface.triggerUpdate()
                prop.set(component, it.f2!!.toVec2f())
                component.componentData.propertyValues[prop.name] = it
            }
        }

        xyRow(
            prop.label,
            propValue.toVec2d(),
            dragChangeSpeed = Vec2d(prop.dragChangeSpeed),
            minValues = Vec2d(prop.min),
            maxValues = Vec2d(prop.max),
            editHandler = editHandler
        )
    }

    private fun UiScope.vec3fEditor(prop: ScriptProperty) {
        val propValue = prop.get(component) as Vec3f
        val editHandler = ActionValueEditHandler<Vec3d> { undoValue, applyValue ->
            val newValue = PropertyValue(f3 = Vec3Data(applyValue))
            val oldValue = PropertyValue(f3 = Vec3Data(undoValue))
            SetScriptPropertyAction(component, prop.name, oldValue, newValue) {
                surface.triggerUpdate()
                prop.set(component, it.f3!!.toVec3f())
                component.componentData.propertyValues[prop.name] = it
            }
        }

        xyzRow(
            prop.label,
            propValue.toVec3d(),
            dragChangeSpeed = Vec3d(prop.dragChangeSpeed),
            minValues = Vec3d(prop.min),
            maxValues = Vec3d(prop.max),
            editHandler = editHandler
        )
    }

    private fun UiScope.vec4fEditor(prop: ScriptProperty) {
        val propValue = prop.get(component) as Vec4f
        val editHandler = ActionValueEditHandler<Vec4d> { undoValue, applyValue ->
            val newValue = PropertyValue(f4 = Vec4Data(applyValue))
            val oldValue = PropertyValue(f4 = Vec4Data(undoValue))
            SetScriptPropertyAction(component, prop.name, oldValue, newValue) {
                surface.triggerUpdate()
                prop.set(component, it.f4!!.toVec4f())
                component.componentData.propertyValues[prop.name] = it
            }
        }

        xyzwRow(
            prop.label,
            propValue.toVec4d(),
            dragChangeSpeed = Vec4d(prop.dragChangeSpeed),
            minValues = Vec4d(prop.min),
            maxValues = Vec4d(prop.max),
            editHandler = editHandler
        )
    }

    private fun UiScope.intEditor(prop: ScriptProperty) {
        val propValue = prop.get(component) as Int
        val editHandler = ActionValueEditHandler<Int> { undoValue, applyValue ->
            val newValue = PropertyValue(i1 = applyValue)
            val oldValue = PropertyValue(i1 = undoValue)
            SetScriptPropertyAction(component, prop.name, oldValue, newValue) {
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

    private fun UiScope.vec2iEditor(prop: ScriptProperty) {
        val propValue = prop.get(component) as Vec2i
        val editHandler = ActionValueEditHandler<Vec2d> { undoValue, applyValue ->
            val newValue = PropertyValue(i2 = Vec2Data(applyValue))
            val oldValue = PropertyValue(i2 = Vec2Data(undoValue))
            SetScriptPropertyAction(component, prop.name, oldValue, newValue) {
                surface.triggerUpdate()
                prop.set(component, it.i2!!.toVec2i())
                component.componentData.propertyValues[prop.name] = it
            }
        }

        xyRow(
            prop.label,
            propValue.toVec2d(),
            dragChangeSpeed = Vec2d(prop.dragChangeSpeed),
            minValues = Vec2d(prop.min),
            maxValues = Vec2d(prop.max),
            editHandler = editHandler
        )
    }

    private fun UiScope.vec3iEditor(prop: ScriptProperty) {
        val propValue = prop.get(component) as Vec3i
        val editHandler = ActionValueEditHandler<Vec3d> { undoValue, applyValue ->
            val newValue = PropertyValue(i3 = Vec3Data(applyValue))
            val oldValue = PropertyValue(i3 = Vec3Data(undoValue))
            SetScriptPropertyAction(component, prop.name, oldValue, newValue) {
                surface.triggerUpdate()
                prop.set(component, it.i3!!.toVec3i())
                component.componentData.propertyValues[prop.name] = it
            }
        }

        xyzRow(
            prop.label,
            propValue.toVec3d(),
            dragChangeSpeed = Vec3d(prop.dragChangeSpeed),
            minValues = Vec3d(prop.min),
            maxValues = Vec3d(prop.max),
            editHandler = editHandler
        )
    }

    private fun UiScope.vec4iEditor(prop: ScriptProperty) {
        val propValue = prop.get(component) as Vec4i
        val editHandler = ActionValueEditHandler<Vec4d> { undoValue, applyValue ->
            val newValue = PropertyValue(i4 = Vec4Data(applyValue))
            val oldValue = PropertyValue(i4 = Vec4Data(undoValue))
            SetScriptPropertyAction(component, prop.name, oldValue, newValue) {
                surface.triggerUpdate()
                prop.set(component, it.i4!!.toVec4i())
                component.componentData.propertyValues[prop.name] = it
            }
        }

        xyzwRow(
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

    private fun ScriptProperty.getPrecision(value: Double): Int {
        return if (isRanged) {
            precisionForValue(max - min)
        } else {
            precisionForValue(value)
        }
    }

    private val ScriptProperty.dragChangeSpeed: Double
        get() = if (isRanged) (max - min) / 1000.0 else 0.05

    companion object {
        fun camelCaseToWords(camelCase: String, allUppercase: Boolean = true): String {
            val words = mutableListOf<String>()
            var word = StringBuilder()
            camelCase.forEach {
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