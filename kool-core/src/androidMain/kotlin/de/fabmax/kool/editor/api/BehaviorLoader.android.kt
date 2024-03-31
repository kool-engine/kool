package de.fabmax.kool.editor.api

import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.declaredMemberProperties

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object BehaviorLoader {

    var appBehaviorLoader: AppBehaviorLoader = ReflectionAppBehaviorLoader(checkNotNull(javaClass.classLoader))

    actual fun newInstance(behaviorClassName: String): KoolBehavior {
        return appBehaviorLoader.newInstance(behaviorClassName)
    }

    actual fun getProperty(behavior: KoolBehavior, propertyName: String): Any? {
        val prop = behavior::class.declaredMemberProperties.first { it.name == propertyName }
        return prop.getter.call(behavior)
    }

    actual fun setProperty(behavior: KoolBehavior, propertyName: String, value: Any?) {
        val prop = behavior::class.declaredMemberProperties.first { it.name == propertyName } as KMutableProperty<*>
        prop.setter.call(behavior, value)
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