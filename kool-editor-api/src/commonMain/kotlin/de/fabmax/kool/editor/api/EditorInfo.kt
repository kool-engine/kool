package de.fabmax.kool.editor.api

@Target(AnnotationTarget.PROPERTY)
annotation class EditorInfo(
    val label: String = "",
    val min: Float = Float.NEGATIVE_INFINITY,
    val max: Float = Float.POSITIVE_INFINITY,
    val hideInEditor: Boolean = false
)