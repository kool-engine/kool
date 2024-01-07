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

    fun StringBuilder.createExplodedMembers(members: List<WgslStructMember>, vararg builtins: WgslStructMember?) {
        (members + builtins).forEach {
            it?.createExplodedMember(this)
        }
        appendLine()
    }

    fun StringBuilder.createAndAssignExplodedMembers(members: List<WgslStructMember>, vararg builtins: WgslStructMember?) {
        (members + builtins).forEach {
            it?.createAndAssignExplodedMember(this)
        }
        appendLine()
    }

    fun StringBuilder.assignStructMembers(members: List<WgslStructMember>, vararg builtins: WgslStructMember?) {
        appendLine()
        (members + builtins).forEach {
            it?.setMemberFromExploded(this)
        }
    }
}

data class WgslStructMember(val structName: String, val name: String, val type: String, val annotation: String = "") {
    fun generateStructMember(builder: StringBuilder) {
        builder.appendLine("  ${annotation}${name}: ${type},")
    }

    fun createExplodedMember(builder: StringBuilder) {
        builder.appendLine("  var $name = $type();")
    }

    fun createAndAssignExplodedMember(builder: StringBuilder) {
        builder.appendLine("  var $name = $structName.$name;")
    }

    fun setMemberFromExploded(builder: StringBuilder) {
        builder.appendLine("  $structName.$name = $name;")
    }
}
