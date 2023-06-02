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

class ScriptEditor(val scriptComponent: ScriptComponent) : Composable {

    override fun UiScope.compose() = Column(width = Grow.Std) {
        modifier
            .padding(horizontal = sizes.gap)
            .padding(top = sizes.gap)
            .margin(bottom = sizes.smallGap)

        labeledSwitch("Run in edit mode", scriptComponent.runInEditMode) {  }

        val scriptProperties = scriptComponent.scriptInstance.use()?.let {
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

                else -> { logW { "Type is not editable: ${prop.type} (in script: ${scriptComponent.scriptInstance})" } }
            }
        }
    }

    private fun UiScope.doubleEditor(prop: ScriptProperty) {
        val propValue = remember(prop.get(scriptComponent) as Double)
        val editHandler = ActionValueEditHandler<Double> { undoValue, applyValue ->
            val newValue = PropertyValue(d1 = applyValue)
            val oldValue = PropertyValue(d1 = undoValue)
            SetScriptPropertyAction(scriptComponent, prop.name, oldValue, newValue) {
                propValue.set(it.d1!!)
                prop.set(scriptComponent, propValue.value)
                scriptComponent.componentData.propertyValues[prop.name] = it
            }
        }

        labeledDoubleTextField(
            prop.label,
            propValue.use(),
            prop.getPrecision(propValue.value),
            dragChangeSpeed = prop.dragChangeSpeed,
            minValue = prop.min,
            maxValue = prop.max,
            editHandler = editHandler
        )
    }

    private fun UiScope.vec2dEditor(prop: ScriptProperty) {
        val propValue = remember(prop.get(scriptComponent) as Vec2d)
        val editHandler = ActionValueEditHandler<Vec2d> { undoValue, applyValue ->
            val newValue = PropertyValue(d2 = Vec2Data(applyValue))
            val oldValue = PropertyValue(d2 = Vec2Data(undoValue))
            SetScriptPropertyAction(scriptComponent, prop.name, oldValue, newValue) {
                propValue.set(it.d2!!.toVec2d())
                prop.set(scriptComponent, MutableVec2d(propValue.value))
                scriptComponent.componentData.propertyValues[prop.name] = it
            }
        }

        xyRow(
            prop.label,
            propValue.use(),
            dragChangeSpeed = Vec2d(prop.dragChangeSpeed),
            minValues = Vec2d(prop.min),
            maxValues = Vec2d(prop.max),
            editHandler = editHandler
        )
    }

    private fun UiScope.vec3dEditor(prop: ScriptProperty) {
        val propValue = remember(prop.get(scriptComponent) as Vec3d)
        val editHandler = ActionValueEditHandler<Vec3d> { undoValue, applyValue ->
            val newValue = PropertyValue(d3 = Vec3Data(applyValue))
            val oldValue = PropertyValue(d3 = Vec3Data(undoValue))
            SetScriptPropertyAction(scriptComponent, prop.name, oldValue, newValue) {
                propValue.set(it.d3!!.toVec3d())
                prop.set(scriptComponent, MutableVec3d(propValue.value))
                scriptComponent.componentData.propertyValues[prop.name] = it
            }
        }

        xyzRow(
            prop.label,
            propValue.use(),
            dragChangeSpeed = Vec3d(prop.dragChangeSpeed),
            minValues = Vec3d(prop.min),
            maxValues = Vec3d(prop.max),
            editHandler = editHandler
        )
    }

    private fun UiScope.vec4dEditor(prop: ScriptProperty) {
        val propValue = remember(prop.get(scriptComponent) as Vec4d)
        val editHandler = ActionValueEditHandler<Vec4d> { undoValue, applyValue ->
            val newValue = PropertyValue(d4 = Vec4Data(applyValue))
            val oldValue = PropertyValue(d4 = Vec4Data(undoValue))
            SetScriptPropertyAction(scriptComponent, prop.name, oldValue, newValue) {
                propValue.set(it.d4!!.toVec4d())
                prop.set(scriptComponent, MutableVec4d(propValue.value))
                scriptComponent.componentData.propertyValues[prop.name] = it
            }
        }

        xyzwRow(
            prop.label,
            propValue.use(),
            dragChangeSpeed = Vec4d(prop.dragChangeSpeed),
            minValues = Vec4d(prop.min),
            maxValues = Vec4d(prop.max),
            editHandler = editHandler
        )
    }

    private fun UiScope.floatEditor(prop: ScriptProperty) {
        val propValue = remember(prop.get(scriptComponent) as Float)
        val editHandler = ActionValueEditHandler<Double> { undoValue, applyValue ->
            val newValue = PropertyValue(f1 = applyValue.toFloat())
            val oldValue = PropertyValue(f1 = undoValue.toFloat())
            SetScriptPropertyAction(scriptComponent, prop.name, oldValue, newValue) {
                propValue.set(it.f1!!)
                prop.set(scriptComponent, propValue.value)
                scriptComponent.componentData.propertyValues[prop.name] = it
            }
        }

        labeledDoubleTextField(
            prop.label,
            propValue.use().toDouble(),
            prop.getPrecision(propValue.value.toDouble()),
            dragChangeSpeed = prop.dragChangeSpeed,
            minValue = prop.min,
            maxValue = prop.max,
            editHandler = editHandler
        )
    }

    private fun UiScope.vec2fEditor(prop: ScriptProperty) {
        val propValue = remember(prop.get(scriptComponent) as Vec2f)
        val editHandler = ActionValueEditHandler<Vec2d> { undoValue, applyValue ->
            val newValue = PropertyValue(f2 = Vec2Data(applyValue))
            val oldValue = PropertyValue(f2 = Vec2Data(undoValue))
            SetScriptPropertyAction(scriptComponent, prop.name, oldValue, newValue) {
                propValue.set(it.f2!!.toVec2f())
                prop.set(scriptComponent, MutableVec2f(propValue.value))
                scriptComponent.componentData.propertyValues[prop.name] = it
            }
        }

        xyRow(
            prop.label,
            propValue.use().toVec2d(),
            dragChangeSpeed = Vec2d(prop.dragChangeSpeed),
            minValues = Vec2d(prop.min),
            maxValues = Vec2d(prop.max),
            editHandler = editHandler
        )
    }

    private fun UiScope.vec3fEditor(prop: ScriptProperty) {
        val propValue = remember(prop.get(scriptComponent) as Vec3f)
        val editHandler = ActionValueEditHandler<Vec3d> { undoValue, applyValue ->
            val newValue = PropertyValue(f3 = Vec3Data(applyValue))
            val oldValue = PropertyValue(f3 = Vec3Data(undoValue))
            SetScriptPropertyAction(scriptComponent, prop.name, oldValue, newValue) {
                propValue.set(it.f3!!.toVec3f())
                prop.set(scriptComponent, MutableVec3f(propValue.value))
                scriptComponent.componentData.propertyValues[prop.name] = it
            }
        }

        xyzRow(
            prop.label,
            propValue.use().toVec3d(),
            dragChangeSpeed = Vec3d(prop.dragChangeSpeed),
            minValues = Vec3d(prop.min),
            maxValues = Vec3d(prop.max),
            editHandler = editHandler
        )
    }

    private fun UiScope.vec4fEditor(prop: ScriptProperty) {
        val propValue = remember(prop.get(scriptComponent) as Vec4f)
        val editHandler = ActionValueEditHandler<Vec4d> { undoValue, applyValue ->
            val newValue = PropertyValue(f4 = Vec4Data(applyValue))
            val oldValue = PropertyValue(f4 = Vec4Data(undoValue))
            SetScriptPropertyAction(scriptComponent, prop.name, oldValue, newValue) {
                propValue.set(it.f4!!.toVec4f())
                prop.set(scriptComponent, MutableVec4f(propValue.value))
                scriptComponent.componentData.propertyValues[prop.name] = it
            }
        }

        xyzwRow(
            prop.label,
            propValue.use().toVec4d(),
            dragChangeSpeed = Vec4d(prop.dragChangeSpeed),
            minValues = Vec4d(prop.min),
            maxValues = Vec4d(prop.max),
            editHandler = editHandler
        )
    }

    private fun UiScope.intEditor(prop: ScriptProperty) {
        val propValue = remember(prop.get(scriptComponent) as Int)
        val editHandler = ActionValueEditHandler<Int> { undoValue, applyValue ->
            val newValue = PropertyValue(i1 = applyValue)
            val oldValue = PropertyValue(i1 = undoValue)
            SetScriptPropertyAction(scriptComponent, prop.name, oldValue, newValue) {
                propValue.set(it.i1!!)
                prop.set(scriptComponent, propValue.value)
                scriptComponent.componentData.propertyValues[prop.name] = it
            }
        }

        labeledIntTextField(
            prop.label,
            propValue.use(),
            dragChangeSpeed = prop.dragChangeSpeed,
            minValue = prop.min.toInt(),
            maxValue = prop.max.toInt(),
            editHandler = editHandler
        )
    }

    private fun UiScope.vec2iEditor(prop: ScriptProperty) {
        val propValue = remember(prop.get(scriptComponent) as Vec2i)
        val editHandler = ActionValueEditHandler<Vec2d> { undoValue, applyValue ->
            val newValue = PropertyValue(i2 = Vec2Data(applyValue))
            val oldValue = PropertyValue(i2 = Vec2Data(undoValue))
            SetScriptPropertyAction(scriptComponent, prop.name, oldValue, newValue) {
                propValue.set(it.i2!!.toVec2i())
                prop.set(scriptComponent, MutableVec2i(propValue.value))
                scriptComponent.componentData.propertyValues[prop.name] = it
            }
        }

        xyRow(
            prop.label,
            propValue.use().toVec2d(),
            dragChangeSpeed = Vec2d(prop.dragChangeSpeed),
            minValues = Vec2d(prop.min),
            maxValues = Vec2d(prop.max),
            editHandler = editHandler
        )
    }

    private fun UiScope.vec3iEditor(prop: ScriptProperty) {
        val propValue = remember(prop.get(scriptComponent) as Vec3i)
        val editHandler = ActionValueEditHandler<Vec3d> { undoValue, applyValue ->
            val newValue = PropertyValue(i3 = Vec3Data(applyValue))
            val oldValue = PropertyValue(i3 = Vec3Data(undoValue))
            SetScriptPropertyAction(scriptComponent, prop.name, oldValue, newValue) {
                propValue.set(it.i3!!.toVec3i())
                prop.set(scriptComponent, MutableVec3i(propValue.value))
                scriptComponent.componentData.propertyValues[prop.name] = it
            }
        }

        xyzRow(
            prop.label,
            propValue.use().toVec3d(),
            dragChangeSpeed = Vec3d(prop.dragChangeSpeed),
            minValues = Vec3d(prop.min),
            maxValues = Vec3d(prop.max),
            editHandler = editHandler
        )
    }

    private fun UiScope.vec4iEditor(prop: ScriptProperty) {
        val propValue = remember(prop.get(scriptComponent) as Vec4i)
        val editHandler = ActionValueEditHandler<Vec4d> { undoValue, applyValue ->
            val newValue = PropertyValue(i4 = Vec4Data(applyValue))
            val oldValue = PropertyValue(i4 = Vec4Data(undoValue))
            SetScriptPropertyAction(scriptComponent, prop.name, oldValue, newValue) {
                propValue.set(it.i4!!.toVec4i())
                prop.set(scriptComponent, MutableVec4i(propValue.value))
                scriptComponent.componentData.propertyValues[prop.name] = it
            }
        }

        xyzwRow(
            prop.label,
            propValue.use().toVec4d(),
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