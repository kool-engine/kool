package de.fabmax.kool.platform

import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logW
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URI
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import kotlin.concurrent.thread

object HttpCache {

    private const val MAX_CACHE_SIZE = 1024L * 1024L * 1024L

    private val credentialsMap = mutableMapOf<String, BasicAuthCredentials>()

    private var isInitialized = false
    private var cacheDir: File = File(".httpCache")
    private var cacheSize = 0L

    private val index: MutableMap<File, CacheEntry> by lazy {
        val index = mutableMapOf<File, CacheEntry>()
        try {
            GZIPInputStream(FileInputStream(File(cacheDir, ".cacheIndex.json.gz"))).use { inStream ->
                val txt = String(inStream.readBytes(), StandardCharsets.UTF_8)
                val serCache = Json.decodeFromString<SerCache>(txt)
                serCache.items.forEach {
                    val f = File(it.file)
                    if (f.canRead()) {
                        index.addEntry(CacheEntry(f, it.size, it.access))
                    }
                }
            }
        } catch (e: Exception) {
            logD { "Failed loading cache index: $e. Rebuilding index..." }
            index.clear()
            index += rebuildIndex()
        }
        Runtime.getRuntime().addShutdownHook(thread(false) { saveIndex() })
        index
    }

    fun addCredentials(credentials: BasicAuthCredentials) {
        credentialsMap[credentials.forHost] = credentials
    }

    fun initCache(cacheDir: File) {
        check(!isInitialized) { "HttpCache must not be initialized multiple times" }
        isInitialized = true
        this.cacheDir = cacheDir
    }

    private fun rebuildIndex(): Map<File, CacheEntry> {
        val newIndex = mutableMapOf<File, CacheEntry>()
        fun File.walk(recv: (File) -> Unit) {
            listFiles()?.forEach {
                if (it.isDirectory) {
                    it.walk(recv)
                } else {
                    recv(it)
                }
            }
        }
        cacheDir.walk {
            if (it.name != ".cacheIndex") {
                newIndex.addEntry(CacheEntry(it))
            }
        }
        return newIndex
    }

    private fun MutableMap<File, CacheEntry>.addEntry(entry: CacheEntry) {
        if (entry.file.canRead()) {
            cacheSize -= this[entry.file]?.size ?: 0
            cacheSize += entry.size
            this[entry.file] = entry
        } else {
            logW { "Cache entry not readable: ${entry.file}" }
        }
        checkCacheSize()
    }

    private fun MutableMap<File, CacheEntry>.checkCacheSize() {
        if (cacheSize > MAX_CACHE_SIZE) {
            val removeQueue = PriorityQueue<CacheEntry>()
            removeQueue.addAll(values)

            var rmCnt = 0
            while (!removeQueue.isEmpty() && cacheSize > MAX_CACHE_SIZE * 0.8) {
                val rmEntry = removeQueue.poll()!!
                rmEntry.file.delete()
                logD { "Deleted from cache: ${rmEntry.file}" }
                remove(rmEntry.file)
                cacheSize -= rmEntry.size
                rmCnt++
            }
        }
    }

    private fun saveIndex() {
        check(cacheDir.exists() || cacheDir.mkdirs()) { "Failed to create cache directory" }

        val entries = synchronized(index) {
            val items = mutableListOf<SerCacheItem>()
            index.values.forEach { items += SerCacheItem(it.file.path, it.size, it.lastAccess) }
            SerCache(items)
        }
        try {
            GZIPOutputStream(FileOutputStream(File(cacheDir, ".cacheIndex.json.gz"))).use {
                it.write(Json.encodeToString(entries).toByteArray(StandardCharsets.UTF_8))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadHttpResource(url: String): File? {
        check(isInitialized) { "HttpCache is not initialized" }

        val req = URI(url).toURL()

        // use host-name as cache directory name, subdomain components are dropped
        // e.g. a.tile.openstreetmap.org and b.tile.openstreetmap.org should share the same cache dir
        var host = req.host
        while (host.count { it == '.' } > 1) {
            host = host.substring(host.indexOf('.') + 1)
        }

        val file = if (req.query != null) {
            File(cacheDir, "/$host/${req.path}_${req.query}")
        } else {
            File(cacheDir, "/$host/${req.path}")
        }

        if (!file.canRead()) {
            // download file and add to cache
            try {
                val con = req.openConnection() as HttpURLConnection
                if (req.host in credentialsMap.keys) {
                    con.addRequestProperty("Authorization", credentialsMap[req.host]!!.encoded)
                }
                if (con.responseCode == 200) {
                    con.inputStream.copyTo(file)
                    synchronized(index) {
                        index.addEntry(CacheEntry(file))
                    }
                    return file
                } else {
                    logW { "Unexpected response on downloading $url: ${con.responseCode} - ${con.responseMessage}" }
                }
            } catch (e: Exception) {
                logW { "Exception during download of $url: $e" }
            }
        }

        return if (file.canRead()) {
            synchronized(index) {
                index[file]?.lastAccess = System.currentTimeMillis()
            }
            file
        } else {
            logW { "Failed downloading $url" }
            null
        }
    }

    private fun InputStream.copyTo(file: File): Long {
        file.parentFile?.mkdirs()
        return use { inStream ->
            FileOutputStream(file).use { outStream ->
                inStream.copyTo(outStream, 4096)
            }
        }
    }

    class BasicAuthCredentials(val forHost: String, user: String, password: String) {
        val encoded = "Basic " + Base64.getEncoder().encodeToString("$user:$password".toByteArray())
    }

    private class CacheEntry(val file: File, var size: Long, lastAccess: Long) : Comparable<CacheEntry> {
        var lastAccess = lastAccess
            set(value) {
                field = value
                file.setLastModified(value)
            }

        constructor(file: File) : this(file, file.length(), file.lastModified())

        override fun compareTo(other: CacheEntry): Int {
            return when {
                lastAccess < other.lastAccess -> -1
                lastAccess > other.lastAccess -> 1
                else -> 0
            }
        }
    }
}

@Serializable
data class SerCache(val items: List<SerCacheItem>)

@Serializable
data class SerCacheItem(val file: String, val size: Long, val access: Long)
