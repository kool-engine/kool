package de.fabmax.kool.editor.ui

import de.fabmax.kool.editor.*
import de.fabmax.kool.editor.actions.SetBehaviorPropertyAction
import de.fabmax.kool.editor.actions.fused
import de.fabmax.kool.editor.components.BehaviorComponent
import de.fabmax.kool.editor.data.*
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.editor.util.nodeModel
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ui2.Box
import de.fabmax.kool.modules.ui2.UiScope
import de.fabmax.kool.modules.ui2.remember
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logW
import kotlin.math.roundToInt
import kotlin.reflect.KClass

class BehaviorEditor : ComponentEditor<BehaviorComponent>() {

    private val behaviorName: String get() = camelCaseToWords(components[0].behaviorClassNameState.value)

    override fun UiScope.compose() = componentPanel(
        title = behaviorName,
        imageIcon = IconMap.small.code,
        onRemove = ::removeComponent,

        headerContent = {
            iconButton(IconMap.small.edit, "Edit source code") {
                KoolEditor.instance.editBehaviorSource(components[0].componentData.behaviorClassName)
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

                SceneModel::class -> { }
                SceneNodeModel::class -> { }

                else -> {
                    if (prop.type == BehaviorPropertyType.COMPONENT) {
                        componentEditor(prop)
                    } else {
                        val behaviorName = components[0].behaviorInstance.value?.let { it::class.simpleName } ?: "null"
                        logW { "Type is not editable: ${prop.kType} (in behavior: $behaviorName)" }
                    }
                }
            }
        }
    }

    private fun UiScope.componentEditor(prop: BehaviorProperty) {
        val klass = prop.kType.classifier as KClass<*>
        val items = remember {
            val selComponents = listOf(null) + sceneModel.nodeModels.values
                .flatMap { it.components }
                .filter { klass.isInstance(it) }

            ComboBoxItems(selComponents) { it?.nodeModel?.name ?: "None" }
        }

        val selected = components.map { prop.getComponent(it) }
        val (options, index) = items.getOptionsAndIndex(selected)
        labeledCombobox(prop.label, options, index) { item ->
            components.map { edit ->
                val undo = PropertyValue(componentRef = ComponentRef(prop.getComponent(edit)))
                val apply = PropertyValue(componentRef = ComponentRef(item.item))
                SetBehaviorPropertyAction(edit.nodeModel.nodeId, prop.name, undo, apply) { comp, value ->
                    val nodeId = value.componentRef!!.nodeId
                    val setComponent = if (nodeId.id == -1L) null else {
                        nodeId.nodeModel?.components?.find { klass.isInstance(it) }
                    }

                    surface.triggerUpdate()
                    prop.set(comp, setComponent)
                    comp.componentData.propertyValues[prop.name] = value
                }
            }.fused().apply()
        }
    }

    private fun UiScope.doubleEditor(prop: BehaviorProperty) {
        val propValue = condenseDouble(components.map { prop.getDouble(it) })
        val editHandler = ActionValueEditHandler<Double> { undoValue, applyValue ->
            components.map { edit ->
                val newValue = PropertyValue(d1 = mergeDouble(applyValue, prop.getDouble(edit)))
                val oldValue = PropertyValue(d1 = mergeDouble(undoValue, prop.getDouble(edit)))
                SetBehaviorPropertyAction(edit.nodeModel.nodeId, prop.name, oldValue, newValue) { comp, value ->
                    surface.triggerUpdate()
                    prop.set(comp, value.d1!!)
                    comp.componentData.propertyValues[prop.name] = value
                }
            }.fused()
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
        val propValue = condenseVec2(components.map { prop.getVec2d(it) })
        val editHandler = ActionValueEditHandler<Vec2d> { undoValue, applyValue ->
            components.map { edit ->
                val newValue = PropertyValue(d2 = Vec2Data(mergeVec2(applyValue, prop.getVec2d(edit))))
                val oldValue = PropertyValue(d2 = Vec2Data(mergeVec2(undoValue, prop.getVec2d(edit))))
                SetBehaviorPropertyAction(edit.nodeModel.nodeId, prop.name, oldValue, newValue) { comp, value ->
                    surface.triggerUpdate()
                    prop.set(comp, value.d2!!.toVec2d())
                    comp.componentData.propertyValues[prop.name] = value
                }
            }.fused()
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
        val propValue = condenseVec3(components.map { prop.getVec3d(it) })
        val editHandler = ActionValueEditHandler<Vec3d> { undoValue, applyValue ->
            components.map { edit ->
                val newValue = PropertyValue(d3 = Vec3Data(mergeVec3(applyValue, prop.getVec3d(edit))))
                val oldValue = PropertyValue(d3 = Vec3Data(mergeVec3(undoValue, prop.getVec3d(edit))))
                SetBehaviorPropertyAction(edit.nodeModel.nodeId, prop.name, oldValue, newValue) { comp, value ->
                    surface.triggerUpdate()
                    prop.set(comp, value.d3!!.toVec3d())
                    comp.componentData.propertyValues[prop.name] = value
                }
            }.fused()
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
        val propValue = condenseVec4(components.map { prop.getVec4d(it) })
        val editHandler = ActionValueEditHandler<Vec4d> { undoValue, applyValue ->
            components.map { edit ->
                val newValue = PropertyValue(d4 = Vec4Data(mergeVec4(applyValue, prop.getVec4d(edit))))
                val oldValue = PropertyValue(d4 = Vec4Data(mergeVec4(undoValue, prop.getVec4d(edit))))
                SetBehaviorPropertyAction(edit.nodeModel.nodeId, prop.name, oldValue, newValue) { comp, value ->
                    surface.triggerUpdate()
                    prop.set(comp, value.d4!!.toVec4d())
                    comp.componentData.propertyValues[prop.name] = value
                }
            }.fused()
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
        val propValue = condenseDouble(components.map { prop.getFloat(it).toDouble() })
        val editHandler = ActionValueEditHandler<Double> { undoValue, applyValue ->
            components.map { edit ->
                val newValue = PropertyValue(f1 = mergeDouble(applyValue, prop.getFloat(edit).toDouble()).toFloat())
                val oldValue = PropertyValue(f1 = mergeDouble(undoValue, prop.getFloat(edit).toDouble()).toFloat())
                SetBehaviorPropertyAction(edit.nodeModel.nodeId, prop.name, oldValue, newValue) { comp, value ->
                    surface.triggerUpdate()
                    prop.set(comp, value.f1!!)
                    comp.componentData.propertyValues[prop.name] = value
                }
            }.fused()
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

    private fun UiScope.vec2fEditor(prop: BehaviorProperty) {
        val propValue = condenseVec2(components.map { prop.getVec2f(it).toVec2d() })
        val editHandler = ActionValueEditHandler<Vec2d> { undoValue, applyValue ->
            components.map { edit ->
                val newValue = PropertyValue(f2 = Vec2Data(mergeVec2(applyValue, prop.getVec2f(edit).toVec2d())))
                val oldValue = PropertyValue(f2 = Vec2Data(mergeVec2(undoValue, prop.getVec2f(edit).toVec2d())))
                SetBehaviorPropertyAction(edit.nodeModel.nodeId, prop.name, oldValue, newValue) { comp, value ->
                    surface.triggerUpdate()
                    prop.set(comp, value.f2!!.toVec2f())
                    comp.componentData.propertyValues[prop.name] = value
                }
            }.fused()
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

    private fun UiScope.vec3fEditor(prop: BehaviorProperty) {
        val propValue = condenseVec3(components.map { prop.getVec3f(it).toVec3d() })
        val editHandler = ActionValueEditHandler<Vec3d> { undoValue, applyValue ->
            components.map { edit ->
                val newValue = PropertyValue(f3 = Vec3Data(mergeVec3(applyValue, prop.getVec3f(edit).toVec3d())))
                val oldValue = PropertyValue(f3 = Vec3Data(mergeVec3(undoValue, prop.getVec3f(edit).toVec3d())))
                SetBehaviorPropertyAction(edit.nodeModel.nodeId, prop.name, oldValue, newValue) { comp, value ->
                    surface.triggerUpdate()
                    prop.set(comp, value.f3!!.toVec3f())
                    comp.componentData.propertyValues[prop.name] = value
                }
            }.fused()
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

    private fun UiScope.vec4fEditor(prop: BehaviorProperty) {
        val propValue = condenseVec4(components.map { prop.getVec4f(it).toVec4d() })
        val editHandler = ActionValueEditHandler<Vec4d> { undoValue, applyValue ->
            components.map { edit ->
                val newValue = PropertyValue(f4 = Vec4Data(mergeVec4(applyValue, prop.getVec4f(edit).toVec4d())))
                val oldValue = PropertyValue(f4 = Vec4Data(mergeVec4(undoValue, prop.getVec4f(edit).toVec4d())))
                SetBehaviorPropertyAction(edit.nodeModel.nodeId, prop.name, oldValue, newValue) { comp, value ->
                    surface.triggerUpdate()
                    prop.set(comp, value.f4!!.toVec4f())
                    comp.componentData.propertyValues[prop.name] = value
                }
            }.fused()
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

    private fun UiScope.intEditor(prop: BehaviorProperty) {
        val propValue = condenseDouble(components.map { prop.getInt(it).toDouble() })
        val editHandler = ActionValueEditHandler<Double> { undoValue, applyValue ->
            components.map { edit ->
                val newValue = PropertyValue(i1 = mergeDouble(applyValue, prop.getInt(edit).toDouble()).roundToInt())
                val oldValue = PropertyValue(i1 = mergeDouble(undoValue, prop.getInt(edit).toDouble()).roundToInt())
                SetBehaviorPropertyAction(edit.nodeModel.nodeId, prop.name, oldValue, newValue) { comp, value ->
                    surface.triggerUpdate()
                    prop.set(comp, value.i1!!)
                    comp.componentData.propertyValues[prop.name] = value
                }
            }.fused()
        }

        labeledDoubleTextField(
            prop.label,
            propValue,
            precision = 0,
            dragChangeSpeed = prop.dragChangeSpeed,
            minValue = prop.min,
            maxValue = prop.max,
            editHandler = editHandler
        )
    }

    private fun UiScope.vec2iEditor(prop: BehaviorProperty) {
        val propValue = condenseVec2(components.map { prop.getVec2i(it).toVec2d() })
        val editHandler = ActionValueEditHandler<Vec2d> { undoValue, applyValue ->
            components.map { edit ->
                val newValue = PropertyValue(i2 = Vec2Data(mergeVec2(applyValue, prop.getVec2i(edit).toVec2d())))
                val oldValue = PropertyValue(i2 = Vec2Data(mergeVec2(undoValue, prop.getVec2i(edit).toVec2d())))
                SetBehaviorPropertyAction(edit.nodeModel.nodeId, prop.name, oldValue, newValue) { comp, value ->
                    surface.triggerUpdate()
                    prop.set(comp, value.i2!!.toVec2i())
                    comp.componentData.propertyValues[prop.name] = value
                }
            }.fused()
        }

        labeledXyRow(
            prop.label,
            propValue,
            precision = Vec2i.ZERO,
            dragChangeSpeed = Vec2d(prop.dragChangeSpeed),
            minValues = Vec2d(prop.min),
            maxValues = Vec2d(prop.max),
            editHandler = editHandler
        )
    }

    private fun UiScope.vec3iEditor(prop: BehaviorProperty) {
        val propValue = condenseVec3(components.map { prop.getVec3i(it).toVec3d() })
        val editHandler = ActionValueEditHandler<Vec3d> { undoValue, applyValue ->
            components.map { edit ->
                val newValue = PropertyValue(i3 = Vec3Data(mergeVec3(applyValue, prop.getVec3i(edit).toVec3d())))
                val oldValue = PropertyValue(i3 = Vec3Data(mergeVec3(undoValue, prop.getVec3i(edit).toVec3d())))
                SetBehaviorPropertyAction(edit.nodeModel.nodeId, prop.name, oldValue, newValue) { comp, value ->
                    surface.triggerUpdate()
                    prop.set(comp, value.i3!!.toVec3i())
                    comp.componentData.propertyValues[prop.name] = value
                }
            }.fused()
        }

        labeledXyzRow(
            prop.label,
            propValue,
            precision = Vec3i.ZERO,
            dragChangeSpeed = Vec3d(prop.dragChangeSpeed),
            minValues = Vec3d(prop.min),
            maxValues = Vec3d(prop.max),
            editHandler = editHandler
        )
    }

    private fun UiScope.vec4iEditor(prop: BehaviorProperty) {
        val propValue = condenseVec4(components.map { prop.getVec4i(it).toVec4d() })
        val editHandler = ActionValueEditHandler<Vec4d> { undoValue, applyValue ->
            components.map { edit ->
                val newValue = PropertyValue(i4 = Vec4Data(mergeVec4(applyValue, prop.getVec4i(edit).toVec4d())))
                val oldValue = PropertyValue(i4 = Vec4Data(mergeVec4(undoValue, prop.getVec4i(edit).toVec4d())))
                SetBehaviorPropertyAction(edit.nodeModel.nodeId, prop.name, oldValue, newValue) { comp, value ->
                    surface.triggerUpdate()
                    prop.set(comp, value.i4!!.toVec4i())
                    comp.componentData.propertyValues[prop.name] = value
                }
            }.fused()
        }

        labeledXyzwRow(
            prop.label,
            propValue,
            precision = Vec4i.ZERO,
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
        } else if (value.isFinite()) {
            precisionForValue(value)
        } else {
            0
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