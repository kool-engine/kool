package de.fabmax.kool.pipeline.backend.webgpu

interface WgslStructHelper {
    fun StringBuilder.generateStruct(name: String, members: List<WgslStructMember>, vararg builtins: WgslStructMember?) {
        if (members.isNotEmpty() || builtins.isNotEmpty()) {
            appendLine("struct $name {")
            builtins.forEach { it?.generateStructMember(this) }
            members.forEach { it.generateStructMember(this) }
            appendLine("};")
            appendLine()
        }
    }
}

data class WgslStructMember(val structName: String, val name: String, val type: String, val annotation: String = "") {
    fun generateStructMember(builder: StringBuilder) {
        builder.appendLine("  ${annotation}${name}: ${type},")
    }
}
