package de.fabmax.kool.editor.api

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object BehaviorLoader {
    fun newInstance(behaviorClassName: String): KoolBehavior
    fun getProperty(behavior: KoolBehavior, propertyName: String): Any?
    fun setProperty(behavior: KoolBehavior, propertyName: String, value: Any?)
}