package de.fabmax.kool.platform

import de.fabmax.kool.KeyValueStorage
import de.fabmax.kool.util.BufferUtil
import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.Uint8BufferImpl
import de.fabmax.kool.util.logE
import kotlinx.browser.localStorage
import org.w3c.dom.get
import org.w3c.dom.set

class KeyValueStorageJs : KeyValueStorage {

    override fun storageKeys(): Set<String> {
        val keys = mutableSetOf<String>()
        for (i in 0 until localStorage.length) {
            localStorage.key(i)?.let { keys += it }
        }
        return keys
    }

    override fun store(key: String, data: Uint8Buffer): Boolean {
        return try {
            localStorage[key] = BufferUtil.binToBase64((data as Uint8BufferImpl).buffer)
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

    override fun load(key: String): Uint8Buffer? {
        return localStorage[key]?.let { Uint8BufferImpl(BufferUtil.base64ToBin(it)) }
    }

    override fun loadString(key: String): String? {
        return localStorage[key]
    }

    override fun delete(key: String) {
        localStorage.removeItem(key)
    }
}