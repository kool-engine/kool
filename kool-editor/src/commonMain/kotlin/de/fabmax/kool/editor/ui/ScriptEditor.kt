package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.EditorState
import de.fabmax.kool.editor.ScriptProperty
import de.fabmax.kool.editor.actions.EditorActions
import de.fabmax.kool.editor.actions.SetScriptPropertyAction
import de.fabmax.kool.editor.data.PropertyValue
import de.fabmax.kool.editor.data.Vec2Data
import de.fabmax.kool.editor.data.Vec3Data
import de.fabmax.kool.editor.data.Vec4Data
import de.fabmax.kool.editor.model.ScriptComponent
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ui2.*
import de.fabmax.kool.util.logW
import kotlin.math.roundToInt

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
            divider(colors.secondaryVariantAlpha(0.5f), marginTop = sizes.gap, marginBottom = sizes.smallGap)
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
        val propValue = remember((prop.get(scriptComponent) as Double).toFloat())

        if (prop.isRanged) {
            labeledSlider(prop.label, propValue, prop.min, prop.max, prop.precision) { value ->
                val newValue = PropertyValue(d1 = value.toDouble())
                EditorActions.applyAction(
                    SetScriptPropertyAction(scriptComponent, prop.name, newValue) {
                        propValue.set(it.d1!!.toFloat())
                        prop.set(scriptComponent, propValue.value)
                        scriptComponent.componentData.propertyValues[prop.name] = it
                    }
                )
            }
        } else {
            menuRow {
                Text(prop.label) {
                    modifier
                        .width(Grow.Std)
                        .alignY(AlignmentY.Center)
                }
                doubleTextField(propValue.use().toDouble(), width = sizes.baseSize * 2) { value ->
                    val newValue = PropertyValue(d1 = value)
                    EditorActions.applyAction(
                        SetScriptPropertyAction(scriptComponent, prop.name, newValue) {
                            propValue.set(it.d1!!.toFloat())
                            prop.set(scriptComponent, propValue.value)
                            scriptComponent.componentData.propertyValues[prop.name] = it
                        }
                    )
                }
            }
        }
    }

    private fun UiScope.vec2dEditor(prop: ScriptProperty) {
        val propValue by remember(MutableVec2d(prop.get(scriptComponent) as Vec2d))
        xyRow(prop.label, propValue.x, propValue.y) { x, y ->
            val newValue = PropertyValue(d2 = Vec2Data(x, y))
            EditorActions.applyAction(
                SetScriptPropertyAction(scriptComponent, prop.name, newValue) {
                    it.d2!!.toVec2d(propValue)
                    prop.set(scriptComponent, propValue)
                    scriptComponent.componentData.propertyValues[prop.name] = it
                }
            )
        }
    }

    private fun UiScope.vec3dEditor(prop: ScriptProperty) {
        val propValue by remember(MutableVec3d(prop.get(scriptComponent) as Vec3d))
        xyzRow(prop.label, propValue.x, propValue.y, propValue.z) { x, y, z ->
            val newValue = PropertyValue(d3 = Vec3Data(x, y, z))
            EditorActions.applyAction(
                SetScriptPropertyAction(scriptComponent, prop.name, newValue) {
                    it.d3!!.toVec3d(propValue)
                    prop.set(scriptComponent, propValue)
                    scriptComponent.componentData.propertyValues[prop.name] = it
                }
            )
        }
    }

    private fun UiScope.vec4dEditor(prop: ScriptProperty) {
        val propValue by remember(MutableVec4d(prop.get(scriptComponent) as Vec4d))
        xyzwRow(prop.label, propValue.x, propValue.y, propValue.z, propValue.w) { x, y, z, w ->
            val newValue = PropertyValue(d4 = Vec4Data(x, y, z, w))
            EditorActions.applyAction(
                SetScriptPropertyAction(scriptComponent, prop.name, newValue) {
                    it.d4!!.toVec4d(propValue)
                    prop.set(scriptComponent, propValue)
                    scriptComponent.componentData.propertyValues[prop.name] = it
                }
            )
        }
    }

    private fun UiScope.floatEditor(prop: ScriptProperty) {
        val propValue = remember(prop.get(scriptComponent) as Float)

        if (prop.isRanged) {
            labeledSlider(prop.label, propValue, prop.min, prop.max, prop.precision) { value ->
                val newValue = PropertyValue(f1 = value)
                EditorActions.applyAction(
                    SetScriptPropertyAction(scriptComponent, prop.name, newValue) {
                        propValue.set(it.f1!!)
                        prop.set(scriptComponent, propValue.value)
                        scriptComponent.componentData.propertyValues[prop.name] = it
                    }
                )
            }
        } else {
            menuRow {
                Text(prop.label) {
                    modifier
                        .width(Grow.Std)
                        .alignY(AlignmentY.Center)
                }
                doubleTextField(propValue.use().toDouble(), width = sizes.baseSize * 2) { value ->
                    val newValue = PropertyValue(f1 = value.toFloat())
                    EditorActions.applyAction(
                        SetScriptPropertyAction(scriptComponent, prop.name, newValue) {
                            propValue.set(it.f1!!)
                            prop.set(scriptComponent, propValue.value)
                            scriptComponent.componentData.propertyValues[prop.name] = it
                        }
                    )
                }
            }
        }
    }

    private fun UiScope.vec2fEditor(prop: ScriptProperty) {
        val propValue by remember(MutableVec2f(prop.get(scriptComponent) as Vec2f))
        xyRow(prop.label, propValue.x.toDouble(), propValue.y.toDouble()) { x, y ->
            val newValue = PropertyValue(f2 = Vec2Data(x, y))
            EditorActions.applyAction(
                SetScriptPropertyAction(scriptComponent, prop.name, newValue) {
                    it.f2!!.toVec2f(propValue)
                    prop.set(scriptComponent, propValue)
                    scriptComponent.componentData.propertyValues[prop.name] = it
                }
            )
        }
    }

    private fun UiScope.vec3fEditor(prop: ScriptProperty) {
        val propValue by remember(MutableVec3f(prop.get(scriptComponent) as Vec3f))
        xyzRow(prop.label, propValue.x.toDouble(), propValue.y.toDouble(), propValue.z.toDouble()) { x, y, z ->
            val newValue = PropertyValue(f3 = Vec3Data(x, y, z))
            EditorActions.applyAction(
                SetScriptPropertyAction(scriptComponent, prop.name, newValue) {
                    it.f3!!.toVec3f(propValue)
                    prop.set(scriptComponent, propValue)
                    scriptComponent.componentData.propertyValues[prop.name] = it
                }
            )
        }
    }

    private fun UiScope.vec4fEditor(prop: ScriptProperty) {
        val propValue by remember(MutableVec4f(prop.get(scriptComponent) as Vec4f))
        xyzwRow(prop.label, propValue.x.toDouble(), propValue.y.toDouble(), propValue.z.toDouble(), propValue.w.toDouble()) { x, y, z, w ->
            val newValue = PropertyValue(f4 = Vec4Data(x, y, z, w))
            EditorActions.applyAction(
                SetScriptPropertyAction(scriptComponent, prop.name, newValue) {
                    it.f4!!.toVec4f(propValue)
                    prop.set(scriptComponent, propValue)
                    scriptComponent.componentData.propertyValues[prop.name] = it
                }
            )
        }
    }

    private fun UiScope.intEditor(prop: ScriptProperty) {
        if (prop.isRanged) {
            val propValue = remember((prop.get(scriptComponent) as Int).toFloat())
            labeledSlider(prop.label, propValue, prop.min, prop.max, prop.precision) { value ->
                val newValue = PropertyValue(i1 = value.roundToInt())
                EditorActions.applyAction(
                    SetScriptPropertyAction(scriptComponent, prop.name, newValue) {
                        propValue.set(it.i1!!.toFloat())
                        prop.set(scriptComponent, propValue.value)
                        scriptComponent.componentData.propertyValues[prop.name] = it
                    }
                )
            }
        } else {
            val propValue = remember(prop.get(scriptComponent) as Int)
            menuRow {
                Text(prop.label) {
                    modifier
                        .width(Grow.Std)
                        .alignY(AlignmentY.Center)
                }
                doubleTextField(propValue.use().toDouble(), precision = 0, width = sizes.baseSize * 2) { value ->
                    val newValue = PropertyValue(i1 = value.toInt())
                    EditorActions.applyAction(
                        SetScriptPropertyAction(scriptComponent, prop.name, newValue) {
                            propValue.set(it.i1!!)
                            prop.set(scriptComponent, propValue.value)
                            scriptComponent.componentData.propertyValues[prop.name] = it
                        }
                    )
                }
            }
        }
    }

    private fun UiScope.vec2iEditor(prop: ScriptProperty) {
        val propValue by remember(MutableVec2i(prop.get(scriptComponent) as Vec2i))
        xyRow(prop.label, propValue.x.toDouble(), propValue.y.toDouble(), xPrecision = 0, yPrecision = 0) { x, y ->
            val newValue = PropertyValue(i2 = Vec2Data(x, y))
            EditorActions.applyAction(
                SetScriptPropertyAction(scriptComponent, prop.name, newValue) {
                    it.f2!!.toVec2i(propValue)
                    prop.set(scriptComponent, propValue)
                    scriptComponent.componentData.propertyValues[prop.name] = it
                }
            )
        }
    }

    private fun UiScope.vec3iEditor(prop: ScriptProperty) {
        val propValue by remember(MutableVec3i(prop.get(scriptComponent) as Vec3i))
        xyzRow(
            prop.label,
            propValue.x.toDouble(), propValue.y.toDouble(), propValue.z.toDouble(),
            xPrecision = 0, yPrecision = 0, zPrecision = 0
        ) { x, y, z ->
            val newValue = PropertyValue(i3 = Vec3Data(x, y, z))
            EditorActions.applyAction(
                SetScriptPropertyAction(scriptComponent, prop.name, newValue) {
                    it.f3!!.toVec3i(propValue)
                    prop.set(scriptComponent, propValue)
                    scriptComponent.componentData.propertyValues[prop.name] = it
                }
            )
        }
    }

    private fun UiScope.vec4iEditor(prop: ScriptProperty) {
        val propValue by remember(MutableVec4i(prop.get(scriptComponent) as Vec4i))
        xyzwRow(
            prop.label,
            propValue.x.toDouble(), propValue.y.toDouble(), propValue.z.toDouble(), propValue.w.toDouble(),
            xPrecision = 0, yPrecision = 0, zPrecision = 0, wPrecision = 0
        ) { x, y, z, w ->
            val newValue = PropertyValue(i4 = Vec4Data(x, y, z, w))
            EditorActions.applyAction(
                SetScriptPropertyAction(scriptComponent, prop.name, newValue) {
                    it.i4!!.toVec4i(propValue)
                    prop.set(scriptComponent, propValue)
                    scriptComponent.componentData.propertyValues[prop.name] = it
                }
            )
        }
    }

    private val ScriptProperty.precision: Int
        get() = precisionForValue(max - min)

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