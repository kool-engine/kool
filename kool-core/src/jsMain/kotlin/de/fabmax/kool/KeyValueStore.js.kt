package de.fabmax.kool

import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.decodeBase64
import de.fabmax.kool.util.encodeBase64
import de.fabmax.kool.util.logE
import kotlinx.browser.localStorage
import org.w3c.dom.get
import org.w3c.dom.set

internal actual fun PlatformKeyValueStore(): PlatformKeyValueStore = JsKeyValueStore

object JsKeyValueStore : PlatformKeyValueStore {
    override fun storageKeys(): Set<String> {
        val keys = mutableSetOf<String>()
        for (i in 0 until localStorage.length) {
            localStorage.key(i)?.let { keys += it }
        }
        return keys
    }

    override fun storeBlob(key: String, data: Uint8Buffer): Boolean {
        return try {
            localStorage[key] = data.encodeBase64()
            true
        } catch (e: Exception) {
            logE { "Failed storing data '$key' to localStorage: $e" }
            false
        }
    }

    override fun storeString(key: String, data: String): Boolean {
        return try {
            localStorage[key] = data
            true
        } catch (e: Exception) {
            logE { "Failed storing string '$key' to localStorage: $e" }
            false
        }
    }

    override fun loadBlob(key: String): Uint8Buffer? {
        return localStorage[key]?.decodeBase64()
    }

    override fun loadString(key: String): String? {
        return localStorage[key]
    }

    override fun delete(key: String) {
        localStorage.removeItem(key)
    }
}