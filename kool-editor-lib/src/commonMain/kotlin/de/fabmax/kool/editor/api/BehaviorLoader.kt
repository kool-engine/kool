package de.fabmax.kool.editor.api

expect object BehaviorLoader {
    fun newInstance(behaviorClassName: String): KoolBehavior
    fun getProperty(behavior: KoolBehavior, propertyName: String): Any?
    fun setProperty(behavior: KoolBehavior, propertyName: String, value: Any?)
}