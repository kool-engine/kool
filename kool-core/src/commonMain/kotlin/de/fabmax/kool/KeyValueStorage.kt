package de.fabmax.kool

import de.fabmax.kool.util.Uint8Buffer

interface KeyValueStorage {

    fun storageKeys(): Set<String>

    fun store(key: String, data: Uint8Buffer): Boolean

    fun storeString(key: String, data: String): Boolean

    fun load(key: String): Uint8Buffer?

    fun loadString(key: String): String?

    fun delete(key: String)

}