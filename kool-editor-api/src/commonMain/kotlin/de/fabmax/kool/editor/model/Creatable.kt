package de.fabmax.kool.editor.model

interface Creatable<T: Any> {

    var created: T?

    val isCreated: Boolean get() = created != null

    fun create(): T

}