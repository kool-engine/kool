package de.fabmax.kool.modules.ksl.model

class KslHierarchy(val globalScope: KslScope) {
    fun printHierarchy() {
        println(globalScope.toPseudoCode())
    }
}