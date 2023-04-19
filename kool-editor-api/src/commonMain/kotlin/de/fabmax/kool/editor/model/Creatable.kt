package de.fabmax.kool.editor.model

import de.fabmax.kool.editor.api.ClassFactory

interface Creatable<T: Any> {

    var created: T?

    val isCreated: Boolean get() = created != null

    fun create(classFactory: ClassFactory): T

}