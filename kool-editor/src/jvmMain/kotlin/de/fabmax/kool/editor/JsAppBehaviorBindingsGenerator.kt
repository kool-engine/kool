package de.fabmax.kool.editor

import java.io.File

object JsAppBehaviorBindingsGenerator {

    fun generateBehaviorBindings(appBehaviors: List<AppBehavior>, filePath: String) {
        val source = StringBuilder()
        source.appendImports(appBehaviors)
        source.appendLine("""
            // GENERATED FILE! Do not edit manually ////////////////////////////
            
            object BehaviorBindings : BehaviorLoader.AppBehaviorLoader {
                override fun newInstance(behaviorClassName: String): KoolBehavior {
                    return when (behaviorClassName) {${makeConstructorMappings(appBehaviors)}
                        else -> throw IllegalArgumentException("${"\$"}behaviorClassName not mapped.")
                    }
                }

                override fun getProperty(behavior: KoolBehavior, propertyName: String): Any {
                    return when (behavior) {${makePropertyGetterMappings(appBehaviors)}
                        else -> throw IllegalArgumentException("Unknown behavior class: ${"\$"}{behavior::class}")
                    }
                }

                override fun setProperty(behavior: KoolBehavior, propertyName: String, value: Any?) {
                    when (behavior) {${makePropertySetterMappings(appBehaviors)}
                        else -> throw IllegalArgumentException("Unknown behavior class: ${"\$"}{behavior::class}")
                    }
                }
        """.trimIndent())

        source.appendPropertyGetters(appBehaviors)
        source.appendPropertySetters(appBehaviors)
        source.appendLine("}")

        val file = File(filePath)
        if (!file.exists() || file.readText() != source.toString()) {
            file.writeText(source.toString())
        }
    }

    private fun StringBuilder.appendImports(appBehaviors: List<AppBehavior>) {
        val imports = defaultImports + appBehaviors.map { it.qualifiedName }
        imports.forEach {
            appendLine("import $it")
        }
        appendLine()
    }

    private fun makeConstructorMappings(appBehaviors: List<AppBehavior>): String {
        return appBehaviors
            .joinToString("") { "\n                        \"${it.qualifiedName}\" -> ${it.simpleName}()" }
    }

    private fun makePropertyGetterMappings(appBehaviors: List<AppBehavior>): String {
        return appBehaviors
            .joinToString("") { "\n                        is ${it.simpleName} -> get${it.simpleName}Property(behavior, propertyName)" }
    }

    private fun makePropertySetterMappings(appBehaviors: List<AppBehavior>): String {
        return appBehaviors
            .joinToString("") { "\n                        is ${it.simpleName} -> set${it.simpleName}Property(behavior, propertyName, value)" }
    }

    private fun StringBuilder.appendPropertyGetters(appBehaviors: List<AppBehavior>) {
        appBehaviors.forEach { behavior ->
            val properties = behavior.properties
                .joinToString("") { "\n                        \"${it.name}\" -> behavior.${it.name}" }
            appendLine()
            appendLine("""
                private fun get${behavior.simpleName}Property(behavior: ${behavior.simpleName}, propertyName: String): Any {
                    return when (propertyName) {$properties
                        else -> throw IllegalArgumentException("Unknown parameter ${"\$"}propertyName for behavior class ${"\$"}{behavior::class}")
                    }
                }
            """.trimIndent().prependIndent("    "))
        }
    }

    private fun StringBuilder.appendPropertySetters(appBehaviors: List<AppBehavior>) {
        appBehaviors.forEach { behavior ->
            val properties = behavior.properties
                .joinToString("") { "\n                        \"${it.name}\" -> behavior.${it.name} = value as ${it.type.toString().removePrefix("kotlin.")}" }
            appendLine()
            appendLine("""
                private fun set${behavior.simpleName}Property(behavior: ${behavior.simpleName}, propertyName: String, value: Any?) {
                    when (propertyName) {$properties
                        else -> throw IllegalArgumentException("Unknown parameter ${"\$"}propertyName for behavior class ${"\$"}{behavior::class}")
                    }
                }
            """.trimIndent().prependIndent("    "))
        }
    }

    private val defaultImports = listOf(
        "de.fabmax.kool.editor.api.KoolBehavior",
        "de.fabmax.kool.editor.api.BehaviorLoader"
    )
}