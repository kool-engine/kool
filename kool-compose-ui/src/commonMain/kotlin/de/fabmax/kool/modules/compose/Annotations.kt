package de.fabmax.kool.modules.compose


@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message =
        "This is internal API for Kool's compose ui implementation that may change without warning."
)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
)
@Retention(AnnotationRetention.BINARY)
annotation class InternalKoolComposeAPI

@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "The Kool Compose module is currently highly experimental and acts as a wrapper around a small set of UI nodes." +
            "This API will change drastically as more nodes are ported with compose state in mind."
)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
)
@Retention(AnnotationRetention.BINARY)
annotation class ExperimentalKoolComposeAPI
