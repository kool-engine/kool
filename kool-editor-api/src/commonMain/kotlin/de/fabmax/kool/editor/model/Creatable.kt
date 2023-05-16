package de.fabmax.kool.editor.model

interface Creatable<T: Any> {

    val isCreated: Boolean
        get() = getOrNull() != null

    fun getOrNull(): T?
    suspend fun getOrCreate(): T

}