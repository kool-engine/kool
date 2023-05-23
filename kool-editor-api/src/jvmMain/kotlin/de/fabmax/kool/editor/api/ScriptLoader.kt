package de.fabmax.kool.editor.api

import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.declaredMemberProperties

actual object ScriptLoader {

    var appScriptLoader: AppScriptLoader = ReflectionAppScriptLoader(javaClass.classLoader)

    actual fun newScriptInstance(scriptClassName: String): KoolScript {
        return appScriptLoader.newScriptInstance(scriptClassName)
    }

    actual fun getScriptProperty(script: KoolScript, propertyName: String): Any? {
        val prop = script::class.declaredMemberProperties.first { it.name == propertyName }
        return prop.getter.call(script)
    }

    actual fun setScriptProperty(script: KoolScript, propertyName: String, value: Any?) {
        val prop = script::class.declaredMemberProperties.first { it.name == propertyName } as KMutableProperty<*>
        prop.setter.call(script, value)
    }

    interface AppScriptLoader {
        fun newScriptInstance(scriptClassName: String): KoolScript
    }

    class ReflectionAppScriptLoader(val classLoader: ClassLoader) : AppScriptLoader {
        override fun newScriptInstance(scriptClassName: String): KoolScript {
            val clazz = classLoader.loadClass(scriptClassName)
            val script = clazz.getDeclaredConstructor().newInstance() as KoolScript
            return script
        }
    }
}