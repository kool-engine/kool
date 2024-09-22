package de.fabmax.kool.editor.api

import de.fabmax.kool.util.logE
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.declaredMemberProperties

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object BehaviorLoader {

    var appBehaviorLoader: AppBehaviorLoader = ReflectionAppBehaviorLoader(javaClass.classLoader)

    actual fun newInstance(behaviorClassName: String): KoolBehavior {
        return appBehaviorLoader.newInstance(behaviorClassName)
    }

    actual fun getProperty(behavior: KoolBehavior, propertyName: String): Any? {
        val prop = behavior::class.declaredMemberProperties.find { it.name == propertyName }
        return if (prop == null) {
            logE { "Property \"$propertyName\" not found in KoolBehavior \"${behavior::class.simpleName}\"" }
            null
        } else {
            prop.getter.call(behavior)
        }
    }

    actual fun setProperty(behavior: KoolBehavior, propertyName: String, value: Any?) {
        val prop = behavior::class.declaredMemberProperties.find { it.name == propertyName } as KMutableProperty<*>?
        if (prop == null) {
            logE { "Property \"$propertyName\" not found in KoolBehavior \"${behavior::class.simpleName}\"" }
        } else {
            prop.setter.call(behavior, value)
        }
    }

    interface AppBehaviorLoader {
        fun newInstance(scriptClassName: String): KoolBehavior
    }

    class ReflectionAppBehaviorLoader(val classLoader: ClassLoader) : AppBehaviorLoader {
        override fun newInstance(scriptClassName: String): KoolBehavior {
            val clazz = classLoader.loadClass(scriptClassName)
            return clazz.getDeclaredConstructor().newInstance() as KoolBehavior
        }
    }
}