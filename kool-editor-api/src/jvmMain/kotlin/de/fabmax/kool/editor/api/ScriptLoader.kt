package de.fabmax.kool.editor.api

actual object ScriptLoader {

    var appScriptLoader: AppScriptLoader = ReflectionAppScriptLoader(javaClass.classLoader)

    actual fun newScriptInstance(scriptClassName: String): KoolScript {
        return appScriptLoader.newScriptInstance(scriptClassName)
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