package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.util.Struct

class KslStruct(val provider: () -> Struct<*>) : KslType(provider().name) {
    private val struct = provider()

    val name: String get() = struct.name
}