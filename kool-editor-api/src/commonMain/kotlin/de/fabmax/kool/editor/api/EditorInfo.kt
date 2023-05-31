package de.fabmax.kool.editor.api

@Target(AnnotationTarget.PROPERTY)
annotation class EditorInfo(
    val label: String = "",
    val min: Double = Double.NEGATIVE_INFINITY,
    val max: Double = Double.POSITIVE_INFINITY,
    val hideInEditor: Boolean = false
)