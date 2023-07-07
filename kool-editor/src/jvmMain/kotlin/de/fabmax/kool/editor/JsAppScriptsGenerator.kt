package de.fabmax.kool.editor

import java.io.File

object JsAppScriptsGenerator {

    fun generateScriptBindings(appScripts: List<AppScript>, filePath: String) {
        val source = StringBuilder()
        source.appendImports(appScripts)
        source.appendLine("""
            // GENERATED FILE! Do not edit manually ////////////////////////////
            
            object ScriptBindings : ScriptLoader.AppScriptLoader {
                override fun newScriptInstance(scriptClassName: String): KoolScript {
                    return when (scriptClassName) {${makeConstructorMappings(appScripts)}
                        else -> throw IllegalArgumentException("${"\$"}scriptClassName not mapped.")
                    }
                }

                override fun getScriptProperty(script: KoolScript, propertyName: String): Any {
                    return when (script) {${makePropertyGetterMappings(appScripts)}
                        else -> throw IllegalArgumentException("Unknown script class: ${"\$"}{script::class}")
                    }
                }

                override fun setScriptProperty(script: KoolScript, propertyName: String, value: Any?) {
                    when (script) {${makePropertySetterMappings(appScripts)}
                        else -> throw IllegalArgumentException("Unknown script class: ${"\$"}{script::class}")
                    }
                }
        """.trimIndent())

        source.appendPropertyGetters(appScripts)
        source.appendPropertySetters(appScripts)
        source.appendLine("}")

        val file = File(filePath)
        if (!file.exists() || file.readText() != source.toString()) {
            file.writeText(source.toString())
        }
    }

    private fun StringBuilder.appendImports(appScripts: List<AppScript>) {
        val imports = defaultImports + appScripts.map { it.qualifiedName }
        imports.forEach {
            appendLine("import $it")
        }
        appendLine()
    }

    private fun makeConstructorMappings(appScripts: List<AppScript>): String {
        return appScripts
            .joinToString("") { "\n                        \"${it.qualifiedName}\" -> ${it.simpleName}()" }
    }

    private fun makePropertyGetterMappings(appScripts: List<AppScript>): String {
        return appScripts
            .joinToString("") { "\n                        is ${it.simpleName} -> get${it.simpleName}Property(script, propertyName)" }
    }

    private fun makePropertySetterMappings(appScripts: List<AppScript>): String {
        return appScripts
            .joinToString("") { "\n                        is ${it.simpleName} -> set${it.simpleName}Property(script, propertyName, value)" }
    }

    private fun StringBuilder.appendPropertyGetters(appScripts: List<AppScript>) {
        appScripts.forEach { script ->
            val properties = script.properties
                .joinToString("") { "\n                        \"${it.name}\" -> script.${it.name}" }
            appendLine()
            appendLine("""
                private fun get${script.simpleName}Property(script: ${script.simpleName}, propertyName: String): Any {
                    return when (propertyName) {$properties
                        else -> throw IllegalArgumentException("Unknown parameter ${"\$"}propertyName for script class ${"\$"}{script::class}")
                    }
                }
            """.trimIndent().prependIndent("    "))
        }
    }

    private fun StringBuilder.appendPropertySetters(appScripts: List<AppScript>) {
        appScripts.forEach { script ->
            val properties = script.properties
                .joinToString("") { "\n                        \"${it.name}\" -> script.${it.name} = value as ${it.type.toString().removePrefix("kotlin.")}" }
            appendLine()
            appendLine("""
                private fun set${script.simpleName}Property(script: ${script.simpleName}, propertyName: String, value: Any?) {
                    when (propertyName) {$properties
                        else -> throw IllegalArgumentException("Unknown parameter ${"\$"}propertyName for script class ${"\$"}{script::class}")
                    }
                }
            """.trimIndent().prependIndent("    "))
        }
    }

    private val defaultImports = listOf(
        "de.fabmax.kool.editor.api.KoolScript",
        "de.fabmax.kool.editor.api.ScriptLoader"
    )
}