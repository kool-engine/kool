package de.fabmax.kool.editor

import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KType

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

                override fun getProperty(behavior: KoolBehavior, propertyName: String): Any? {
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
        source.appendBehaviorMap(appBehaviors)
        source.appendLine("}")

        val file = File(filePath)
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }
        if (!file.exists() || file.readText() != source.toString()) {
            file.writeText(source.toString())
        }
    }

    private fun StringBuilder.appendImports(appBehaviors: List<AppBehavior>) {
        val imports = defaultImports + appBehaviors.map { it.qualifiedName }
        imports.sorted().forEach {
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
            val nullable = if (behavior.properties.any { it.kType.isMarkedNullable }) "?" else ""
            val properties = behavior.properties.joinToString("") {
                "\n            \"${it.name}\" -> behavior.${it.name}"
            }
            appendLine()
            appendLine("""
                |    private fun get${behavior.simpleName}Property(behavior: ${behavior.simpleName}, propertyName: String): Any$nullable {
                |        return when (propertyName) {$properties
                |            else -> throw IllegalArgumentException("Unknown parameter ${"\$"}propertyName for behavior class ${"\$"}{behavior::class}")
                |        }
                |    }
            """.trimMargin())
        }
    }

    private fun StringBuilder.appendPropertySetters(appBehaviors: List<AppBehavior>) {
        appBehaviors.forEach { behavior ->
            val properties = behavior.properties.joinToString("") {
                "\n            \"${it.name}\" -> behavior.${it.name} = value as ${it.kType.qualifiedName}"
            }
            appendLine()
            appendLine("""
                |    private fun set${behavior.simpleName}Property(behavior: ${behavior.simpleName}, propertyName: String, value: Any?) {
                |        when (propertyName) {$properties
                |            else -> throw IllegalArgumentException("Unknown parameter ${"\$"}propertyName for behavior class ${"\$"}{behavior::class}")
                |        }
                |    }
            """.trimMargin())
        }
    }

    private fun StringBuilder.appendBehaviorMap(appBehaviors: List<AppBehavior>) {
        appendLine("\n    val behaviorClasses = mapOf<KClass<*>, AppBehavior>(")
        appBehaviors.forEach { behavior ->
            val properties = behavior.properties.joinToString("") {
                val min = if (it.min.isFinite()) ", min = ${it.min}" else ""
                val max = if (it.max.isFinite()) ", max = ${it.max}" else ""

                "\n                BehaviorProperty(\"${it.name}\", BehaviorPropertyType.${it.type}, typeOf<${it.kType.qualifiedName}>(), \"${it.label}\"$min$max),"
            }
            appendLine("""
                |        ${behavior.simpleName}::class to AppBehavior(
                |            simpleName = "${behavior.simpleName}",
                |            qualifiedName = "${behavior.qualifiedName}",
                |            properties = listOf($properties
                |            )
                |        ),
            """.trimMargin())
        }
        appendLine("    )")
    }

    private val defaultImports = listOf(
        "kotlin.reflect.KClass",
        "kotlin.reflect.typeOf",
        "de.fabmax.kool.editor.AppBehavior",
        "de.fabmax.kool.editor.BehaviorProperty",
        "de.fabmax.kool.editor.BehaviorPropertyType",
        "de.fabmax.kool.editor.api.KoolBehavior",
        "de.fabmax.kool.editor.api.BehaviorLoader"
    )

    private val KType.qualifiedName: String
        get() = (classifier as KClass<*>).qualifiedName!!.removePrefix("kotlin.") + if (isMarkedNullable) "?" else ""
}