package de.fabmax.kool

import de.fabmax.kool.util.Uint8Buffer

interface KeyValueStorage {

    fun storageKeys(): Set<String>

    fun store(key: String, data: Uint8Buffer): Boolean

    fun storeString(key: String, data: String): Boolean

    fun load(key: String): Uint8Buffer?

    fun loadString(key: String): String?

    fun delete(key: String)

    fun getBoolean(key: String, defaultVal: Boolean = false): Boolean {
        return loadString(key)?.toBooleanStrictOrNull() ?: defaultVal
    }

    fun setBoolean(key: String, value: Boolean) {
        storeString(key, "$value")
    }

    fun getInt(key: String, defaultVal: Int = 0): Int {
        return loadString(key)?.toIntOrNull() ?: defaultVal
    }

    fun setInt(key: String, value: Int) {
        storeString(key, "$value")
    }

    fun getFloat(key: String, defaultVal: Float = 0f): Float {
        return loadString(key)?.toFloatOrNull() ?: defaultVal
    }

    fun setFloat(key: String, value: Float) {
        storeString(key, "$value")
    }

    fun getDouble(key: String, defaultVal: Double = 0.0): Double {
        return loadString(key)?.toDoubleOrNull() ?: defaultVal
    }

    fun setDouble(key: String, value: Double) {
        storeString(key, "$value")
    }
}