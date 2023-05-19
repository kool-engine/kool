package de.fabmax.kool.editor.model

interface Creatable<T: Any> {
    val isCreated: Boolean
        get() = getOrNull() != null

    fun getOrNull(): T?
    suspend fun getOrCreate(createContext: CreateContext): T
    suspend fun create(createContext: CreateContext): T
}

class CreateContext(val project: MProject, val scene: MScene)
