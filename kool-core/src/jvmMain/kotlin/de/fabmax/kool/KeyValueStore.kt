package de.fabmax.kool

import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.Uint8BufferImpl
import de.fabmax.kool.util.logE
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.concurrent.thread

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object KeyValueStore {

    private const val KEY_VALUE_STORAGE_NAME = ".keyValueStorage.json"

    private val storageDir: File = File(KoolSystem.config.storageDir)
    private val keyValueStore = mutableMapOf<String, String>()

    init {
        if (!storageDir.exists() && !storageDir.mkdirs()) {
            logE { "Failed to create storage directory" }
        }

        val persistentKvStorage = File(storageDir, KEY_VALUE_STORAGE_NAME)
        if (persistentKvStorage.canRead()) {
            try {
                val kvStore = Json.decodeFromString<KeyValueStore>(persistentKvStorage.readText())
                kvStore.keyValues.forEach { (k, v) -> keyValueStore[k] = v }
            } catch (e: Exception) {
                logE { "Failed loading key value store: $e" }
                e.printStackTrace()
            }
        }

        Runtime.getRuntime().addShutdownHook(thread(false) {
            val kvStore = KeyValueStore(keyValueStore.map { (k, v) -> KeyValueEntry(k, v) })
            File(storageDir, KEY_VALUE_STORAGE_NAME).writeText(Json.encodeToString(kvStore))
        })
    }

    actual fun storageKeys(): Set<String> {
        val keys = mutableSetOf<String>()
        keys += keyValueStore.keys
        storageDir.list()?.let {
            keys += it
        }
        return keys
    }

    actual fun store(key: String, data: Uint8Buffer): Boolean {
        return try {
            FileOutputStream(File(storageDir, key)).use { it.write(data.toArray()) }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    actual fun storeString(key: String, data: String): Boolean {
        keyValueStore[key] = data
        return true
    }

    actual fun load(key: String): Uint8Buffer? {
        val file = File(storageDir, key)
        if (!file.canRead()) {
            return null
        }
        return try {
            Uint8BufferImpl(file.readBytes())
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    actual fun loadString(key: String): String? {
        return keyValueStore[key]
    }

    actual fun delete(key: String) {
        keyValueStore.remove(key)
        val f = File(storageDir, key)
        if (f.exists()) {
            f.delete()
        }
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

    @Serializable
    private data class KeyValueEntry(val k: String, val v: String)

    @Serializable
    private data class KeyValueStore(val keyValues: List<KeyValueEntry>)
}