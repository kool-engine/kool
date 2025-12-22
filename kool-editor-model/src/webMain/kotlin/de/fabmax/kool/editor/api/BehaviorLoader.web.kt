package de.fabmax.kool.editor.api

actual object BehaviorLoader {
    var appBehaviorLoader: AppBehaviorLoader? = null

    private val loader: AppBehaviorLoader
        get() = appBehaviorLoader ?: throw IllegalStateException("BehaviorLoader.appBehaviorLoader not initialized")

    actual fun newInstance(behaviorClassName: String): KoolBehavior {
        return loader.newInstance(behaviorClassName)
    }

    actual fun getProperty(behavior: KoolBehavior, propertyName: String): Any? {
        return loader.getProperty(behavior, propertyName)
    }

    actual fun setProperty(behavior: KoolBehavior, propertyName: String, value: Any?) {
        loader.setProperty(behavior, propertyName, value)
    }

    interface AppBehaviorLoader {
        fun newInstance(behaviorClassName: String): KoolBehavior
        fun getProperty(behavior: KoolBehavior, propertyName: String): Any?
        fun setProperty(behavior: KoolBehavior, propertyName: String, value: Any?)
    }
}