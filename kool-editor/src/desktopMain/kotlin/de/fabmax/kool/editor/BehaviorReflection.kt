package de.fabmax.kool.editor

import de.fabmax.kool.editor.api.EditorInfo
import de.fabmax.kool.editor.api.EditorOrder
import de.fabmax.kool.editor.components.EditorModelComponent
import de.fabmax.kool.editor.model.NodeModel
import de.fabmax.kool.math.*
import de.fabmax.kool.util.Color
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KVisibility
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.isSupertypeOf
import kotlin.reflect.typeOf

object BehaviorReflection {

    fun getEditableProperties(behaviorClass: KClass<*>): List<BehaviorProperty> {
        return behaviorClass.declaredMemberProperties
            .filterIsInstance<KMutableProperty<*>>()
            .filter {
                it.visibility == KVisibility.PUBLIC
                    && it.annotations.none { anno -> anno is EditorInfo && anno.hideInEditor }
                    && editableTypes.any { type -> type.isSupertypeOf(it.setter.parameters[1].type) }
            }
            .sortedBy { (it.annotations.find { anno -> anno is EditorOrder } as EditorOrder?)?.order }
            .map {
                val propertyKType = it.setter.parameters[1].type
                val info = it.annotations.filterIsInstance<EditorInfo>().firstOrNull()
                val label = if (info != null && info.label.isNotBlank()) info.label else it.name
                val min = info?.min ?: Double.NEGATIVE_INFINITY
                val max = info?.max ?: Double.POSITIVE_INFINITY
                val type = when {
                    nodeModelType.isSupertypeOf(propertyKType) -> BehaviorPropertyType.NODE_MODEL
                    modelComponentType.isSupertypeOf(propertyKType) -> BehaviorPropertyType.COMPONENT
                    else -> BehaviorPropertyType.STD
                }

                BehaviorProperty(it.name, type, propertyKType, label, min, max)
            }
    }

    private val nodeModelType = typeOf<NodeModel?>()
    private val modelComponentType = typeOf<EditorModelComponent?>()

    private val editableTypes = listOf(
        typeOf<Int?>(),
        typeOf<Vec2i?>(),
        typeOf<Vec3i?>(),
        typeOf<Vec4i?>(),

        typeOf<Float?>(),
        typeOf<Vec2f?>(),
        typeOf<Vec3f?>(),
        typeOf<Vec4f?>(),

        typeOf<Double?>(),
        typeOf<Vec2d?>(),
        typeOf<Vec3d?>(),
        typeOf<Vec4d?>(),

        typeOf<Color?>(),
        typeOf<String?>(),

        nodeModelType,
        modelComponentType,
    )
}