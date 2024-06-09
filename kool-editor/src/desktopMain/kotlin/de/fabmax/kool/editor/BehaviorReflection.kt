package de.fabmax.kool.editor

import de.fabmax.kool.editor.api.EditorInfo
import de.fabmax.kool.editor.api.EditorRange
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.components.GameEntityComponent
import de.fabmax.kool.editor.ui.BehaviorEditor
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
            .sortedBy { (it.annotations.find { anno -> anno is EditorInfo } as EditorInfo?)?.order }
            .map { p ->
                val propertyKType = p.setter.parameters[1].type
                val info = p.annotations.filterIsInstance<EditorInfo>().firstOrNull()
                val label = if (info != null && info.label.isNotBlank()) info.label else BehaviorEditor.camelCaseToWords(p.name, allUppercase = false)
                val rng = p.annotations.filterIsInstance<EditorRange>().firstOrNull()
                val min = rng?.let { Vec4d(it.minX, it.minY, it.minZ, it.minW) } ?: Vec4d(Double.NEGATIVE_INFINITY)
                val max = rng?.let { Vec4d(it.maxX, it.maxY, it.maxZ, it.maxW) } ?: Vec4d(Double.POSITIVE_INFINITY)
                val type = when {
                    gameEntityType.isSupertypeOf(propertyKType) -> BehaviorPropertyType.NODE_MODEL
                    modelComponentType.isSupertypeOf(propertyKType) -> BehaviorPropertyType.COMPONENT
                    else -> BehaviorPropertyType.STD
                }

                BehaviorProperty(p.name, type, propertyKType, label, min, max)
            }
    }

    private val gameEntityType = typeOf<GameEntity?>()
    private val modelComponentType = typeOf<GameEntityComponent?>()

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

        typeOf<Boolean?>(),
        typeOf<Color?>(),
        typeOf<String?>(),

        gameEntityType,
        modelComponentType,
    )
}