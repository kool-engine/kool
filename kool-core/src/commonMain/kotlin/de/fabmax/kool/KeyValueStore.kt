package de.fabmax.kool

import de.fabmax.kool.util.Uint8Buffer

/**
 * Key-value store for storing primitive values (e.g. application settings) as well as arbitrary small to medium
 * byte data (e.g. save games).
 */
expect object KeyValueStore {

    fun storageKeys(): Set<String>

    fun store(key: String, data: Uint8Buffer): Boolean

    fun storeString(key: String, data: String): Boolean

    fun load(key: String): Uint8Buffer?

    fun loadString(key: String): String?

    fun delete(key: String)

    fun getBoolean(key: String, defaultVal: Boolean = false): Boolean

    fun setBoolean(key: String, value: Boolean)

    fun getInt(key: String, defaultVal: Int = 0): Int

    fun setInt(key: String, value: Int)

    fun getFloat(key: String, defaultVal: Float = 0f): Float

    fun setFloat(key: String, value: Float)

    fun getDouble(key: String, defaultVal: Double = 0.0): Double

    fun setDouble(key: String, value: Double)

}