package de.fabmax.kool.editor.api

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object BehaviorLoader {
    actual fun newInstance(behaviorClassName: String): KoolBehavior {
        TODO("Not yet implemented")
    }

    actual fun getProperty(behavior: KoolBehavior, propertyName: String): Any? {
        TODO("Not yet implemented")
    }

    actual fun setProperty(
        behavior: KoolBehavior,
        propertyName: String,
        value: Any?
    ) {
    }
}