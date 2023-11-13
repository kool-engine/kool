package de.fabmax.kool.pipeline.backend.stats

import de.fabmax.kool.util.UniqueId

abstract class ResourceInfo(val name: String) {

    val id = UniqueId.nextId()

    abstract fun deleted()

}