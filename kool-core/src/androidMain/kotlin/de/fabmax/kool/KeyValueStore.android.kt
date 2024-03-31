package de.fabmax.kool

import android.content.Context
import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.toBuffer
import java.io.File
import java.io.IOException

internal actual fun PlatformKeyValueStore(): PlatformKeyValueStore = AndroidKeyValueStore

object AndroidKeyValueStore : PlatformKeyValueStore {

    private val blobDir by lazy {
        val dir = File(KoolSystem.configAndroid.appContext.getExternalFilesDir(null), "keyValBlobs")
        dir.mkdir()
        dir
    }

    private val prefs by lazy {
        KoolSystem.configAndroid.appContext.getSharedPreferences("kool-key-value-store", Context.MODE_PRIVATE)
    }

    override fun storageKeys(): Set<String> {
        val keys = mutableSetOf<String>()
        keys += prefs.all.keys
        blobDir.list()?.let { names ->
            names.forEach { keys += it }
        }
        return keys
    }

    override fun loadBlob(key: String): Uint8Buffer? {
        return try {
            val blobFile = File(blobDir, key)
            return if (blobFile.exists()) {
                blobFile.readBytes().toBuffer()
            } else {
                null
            }
        } catch (e: IOException) {
            logE { "Failed reading blob [key = $key]. Key must be a valid file name!" }
            e.printStackTrace()
            null
        }
    }

    override fun storeBlob(key: String, data: Uint8Buffer): Boolean {
        return try {
            val blobFile = File(blobDir, key)
            blobFile.writeBytes(data.toArray())
            true
        } catch (e: IOException) {
            logE { "Failed writing blob [key = $key]. Key must be a valid file name!" }
            e.printStackTrace()
            false
        }
    }

    override fun storeString(key: String, data: String): Boolean {
        prefs.edit().putString(key, data).apply()
        return true
    }

    override fun loadString(key: String): String? {
        return prefs.getString(key, null)
    }

    override fun delete(key: String) {
        prefs.edit().remove(key).apply()
    }
}