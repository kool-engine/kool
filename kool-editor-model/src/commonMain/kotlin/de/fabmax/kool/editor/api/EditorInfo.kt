package de.fabmax.kool.editor.api

@Target(AnnotationTarget.PROPERTY)
annotation class EditorInfo(
    val label: String = "",
    val order: Int = 0,
    val precision: Int = 0
)

@Target(AnnotationTarget.PROPERTY)
annotation class EditorHidden

@Target(AnnotationTarget.PROPERTY)
annotation class EditorRange(
    val minX: Double = Double.NEGATIVE_INFINITY,
    val minY: Double = Double.NEGATIVE_INFINITY,
    val minZ: Double = Double.NEGATIVE_INFINITY,
    val minW: Double = Double.NEGATIVE_INFINITY,

    val maxX: Double = Double.POSITIVE_INFINITY,
    val maxY: Double = Double.POSITIVE_INFINITY,
    val maxZ: Double = Double.POSITIVE_INFINITY,
    val maxW: Double = Double.POSITIVE_INFINITY,
)