package de.fabmax.kool.pipeline.backend.wgsl

interface WgslStructHelper {

    fun isNotEmpty(members: List<WgslStructMember>, vararg builtins: WgslStructMember?): Boolean {
        return members.isNotEmpty() || builtins.count { it != null } > 0
    }

    fun StringBuilder.generateStruct(name: String, members: List<WgslStructMember>, vararg builtins: WgslStructMember?) {
        if (isNotEmpty(members, *builtins)) {
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
