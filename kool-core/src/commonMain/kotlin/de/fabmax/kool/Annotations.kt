package de.fabmax.kool

@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "This is internal Kool API that may lead to unexpected behavior or change without warning."
)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
)
@Retention(AnnotationRetention.BINARY)
annotation class InternalKoolAPI
