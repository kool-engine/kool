package de.fabmax.kool.editor

import de.fabmax.kool.math.Vec4d
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
                        else -> error("${"\$"}behaviorClassName not mapped.")
                    }
                }

                override fun getProperty(behavior: KoolBehavior, propertyName: String): Any? {
                    return when (behavior) {${makePropertyGetterMappings(appBehaviors)}
                        else -> error("Unknown behavior class: ${"\$"}{behavior::class}")
                    }
                }

                override fun setProperty(behavior: KoolBehavior, propertyName: String, value: Any?) {
                    when (behavior) {${makePropertySetterMappings(appBehaviors)}
                        else -> error("Unknown behavior class: ${"\$"}{behavior::class}")
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
                |            else -> error("Unknown parameter ${"\$"}propertyName for behavior class ${"\$"}{behavior::class}")
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
                |            else -> error("Unknown parameter ${"\$"}propertyName for behavior class ${"\$"}{behavior::class}")
                |        }
                |    }
            """.trimMargin())
        }
    }

    private fun StringBuilder.appendBehaviorMap(appBehaviors: List<AppBehavior>) {
        appendLine("\n    val behaviorClasses = mapOf<KClass<*>, AppBehavior>(")
        appBehaviors.forEach { behavior ->
            val properties = behavior.properties.joinToString("") { p ->
                val min = p.min.toSrc()?.let { ", min = $it" } ?: ""
                val max = p.min.toSrc()?.let { ", max = $it" } ?: ""
                "\n                BehaviorProperty(\"${p.name}\", BehaviorPropertyType.${p.type}, typeOf<${p.kType.qualifiedName}>(), \"${p.label}\"$min$max, precision = ${p.precision}),"
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

    private fun Vec4d.toSrc(): String? {
        return if (x.isFinite() || y.isFinite() || z.isFinite() || w.isFinite()) {
            val x = if (x.isFinite()) "$x" else if (x > 0.0) "Double.POSITIVE_INFINITY" else "Double.NEGATIVE_INFINITY"
            val y = if (y.isFinite()) "$y" else if (y > 0.0) "Double.POSITIVE_INFINITY" else "Double.NEGATIVE_INFINITY"
            val z = if (z.isFinite()) "$z" else if (z > 0.0) "Double.POSITIVE_INFINITY" else "Double.NEGATIVE_INFINITY"
            val w = if (w.isFinite()) "$w" else if (w > 0.0) "Double.POSITIVE_INFINITY" else "Double.NEGATIVE_INFINITY"
            "Vec4d($x, $y, $z, $w)"
        } else {
            null
        }
    }

    private val defaultImports = listOf(
        "kotlin.reflect.KClass",
        "kotlin.reflect.typeOf",
        "de.fabmax.kool.editor.AppBehavior",
        "de.fabmax.kool.editor.BehaviorProperty",
        "de.fabmax.kool.editor.BehaviorPropertyType",
        "de.fabmax.kool.editor.api.KoolBehavior",
        "de.fabmax.kool.editor.api.BehaviorLoader",
        "de.fabmax.kool.math.Vec4d",
    )

    private val KType.qualifiedName: String
        get() = (classifier as KClass<*>).qualifiedName!!.removePrefix("kotlin.") + if (isMarkedNullable) "?" else ""
}