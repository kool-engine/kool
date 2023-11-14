package de.fabmax.kool

import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.decodeBase64
import de.fabmax.kool.util.encodeBase64
import de.fabmax.kool.util.logE
import kotlinx.browser.localStorage
import org.w3c.dom.get
import org.w3c.dom.set

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object KeyValueStore {
    actual fun storageKeys(): Set<String> {
        val keys = mutableSetOf<String>()
        for (i in 0 until localStorage.length) {
            localStorage.key(i)?.let { keys += it }
        }
        return keys
    }

    actual fun store(key: String, data: Uint8Buffer): Boolean {
        return try {
            localStorage[key] = data.encodeBase64()
            true
        } catch (e: Exception) {
            logE { "Failed storing data '$key' to localStorage: $e" }
            false
        }
    }

    actual fun storeString(key: String, data: String): Boolean {
        return try {
            localStorage[key] = data
            true
        } catch (e: Exception) {
            logE { "Failed storing string '$key' to localStorage: $e" }
            false
        }
    }

    actual fun load(key: String): Uint8Buffer? {
        return localStorage[key]?.decodeBase64()
    }

    actual fun loadString(key: String): String? {
        return localStorage[key]
    }

    actual fun delete(key: String) {
        localStorage.removeItem(key)
    }

    actual fun getBoolean(key: String, defaultVal: Boolean): Boolean {
        return loadString(key)?.toBooleanStrictOrNull() ?: defaultVal
    }

    actual fun setBoolean(key: String, value: Boolean) {
        storeString(key, "$value")
    }

    actual fun getInt(key: String, defaultVal: Int): Int {
        return loadString(key)?.toIntOrNull() ?: defaultVal
    }

    actual fun setInt(key: String, value: Int) {
        storeString(key, "$value")
    }

    actual fun getFloat(key: String, defaultVal: Float): Float {
        return loadString(key)?.toFloatOrNull() ?: defaultVal
    }

    actual fun setFloat(key: String, value: Float) {
        storeString(key, "$value")
    }

    actual fun getDouble(key: String, defaultVal: Double): Double {
        return loadString(key)?.toDoubleOrNull() ?: defaultVal
    }

    actual fun setDouble(key: String, value: Double) {
        storeString(key, "$value")
    }
}