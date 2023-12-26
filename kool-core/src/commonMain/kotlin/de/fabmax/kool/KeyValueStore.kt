package de.fabmax.kool

import de.fabmax.kool.util.Uint8Buffer

/**
 * Key-value store for storing primitive values (e.g. application settings) as well as arbitrary small to medium
 * byte data (e.g. save games).
 */
object KeyValueStore {
    private val platformImpl = PlatformKeyValueStore()

    fun storageKeys(): Set<String> = platformImpl.storageKeys()

    fun store(key: String, data: Uint8Buffer): Boolean = platformImpl.storeBlob(key, data)

    fun storeString(key: String, data: String): Boolean = platformImpl.storeString(key, data)

    fun load(key: String): Uint8Buffer? = platformImpl.loadBlob(key)

    fun loadString(key: String): String? = platformImpl.loadString(key)

    fun delete(key: String) = platformImpl.delete(key)

    fun getBoolean(key: String, defaultVal: Boolean = false): Boolean = platformImpl.loadString(key)?.toBooleanStrictOrNull() ?: defaultVal

    fun setBoolean(key: String, value: Boolean) = platformImpl.storeString(key, "$value")

    fun getInt(key: String, defaultVal: Int = 0): Int = platformImpl.loadString(key)?.toIntOrNull() ?: defaultVal

    fun setInt(key: String, value: Int) = platformImpl.storeString(key, "$value")

    fun getFloat(key: String, defaultVal: Float = 0f): Float = platformImpl.loadString(key)?.toFloatOrNull() ?: defaultVal

    fun setFloat(key: String, value: Float) = platformImpl.storeString(key, "$value")

    fun getDouble(key: String, defaultVal: Double = 0.0): Double = platformImpl.loadString(key)?.toDoubleOrNull() ?: defaultVal

    fun setDouble(key: String, value: Double) = platformImpl.storeString(key, "$value")

}

interface PlatformKeyValueStore {
    fun storageKeys(): Set<String>

    fun loadBlob(key: String): Uint8Buffer?
    fun storeBlob(key: String, data: Uint8Buffer): Boolean

    fun storeString(key: String, data: String): Boolean
    fun loadString(key: String): String?

    fun delete(key: String)
}

internal expect fun PlatformKeyValueStore(): PlatformKeyValueStore
