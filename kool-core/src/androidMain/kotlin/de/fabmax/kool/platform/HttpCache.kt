package de.fabmax.kool.platform

import android.util.Base64
import de.fabmax.kool.KoolException
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logW
import kotlinx.coroutines.*
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.concurrent.Executors

class HttpCache private constructor(private val cacheDir: File) {

    private val cache = mutableMapOf<File, CacheEntry>()
    private var cacheSize = 0L

    init {
        try {
            ObjectInputStream(FileInputStream(File(cacheDir, ".cacheIndex"))).use { inStream ->
                @Suppress("UNCHECKED_CAST")
                val entries = inStream.readObject() as List<CacheEntry>
                entries.filter { it.file.canRead() }.forEach { entry ->
                    addCacheEntry(entry)
                }
                checkCacheSize()
            }
        } catch (e: Exception) {
            logD { "Rebuilding http cache index, $e" }
            rebuildIndex()
        }

        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                close()
            }
        })
    }

    private fun close() {
        saveIndex()
    }

    private fun rebuildIndex() {
        synchronized(cache) {
            cache.clear()
            if (!cacheDir.exists() && !cacheDir.mkdirs()) {
                throw KoolException("Failed to create cache directory")
            }
        }

        fun File.walk(recv: (File) -> Unit) {
            listFiles().forEach {
                if (it.isDirectory) {
                    it.walk(recv)
                } else {
                    recv(it)
                }
            }
        }
        cacheDir.walk {
            if (it.name != ".cacheIndex") {
                addCacheEntry(CacheEntry(it))
            }
        }
        saveIndex()
    }

    private fun addCacheEntry(entry: CacheEntry) {
        synchronized(cache) {
            if (entry.file.canRead()) {
                cacheSize -= cache[entry.file]?.size ?: 0
                cacheSize += entry.size
                cache[entry.file] = entry
            } else {
                logW { "Cache entry not readable: ${entry.file}" }
            }
        }
        checkCacheSize()
    }

    private fun saveIndex() {
        val entries = mutableListOf<CacheEntry>()
        synchronized(cache) {
            entries.addAll(cache.values)
        }
        try {
            ObjectOutputStream(FileOutputStream(File(cacheDir, ".cacheIndex"))).use {
                it.writeObject(entries)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkCacheSize() {
        if (cacheSize > MAX_CACHE_SIZE) {
            val removeQueue = PriorityQueue<CacheEntry>()
            synchronized(cache) {
                removeQueue.addAll(cache.values)
            }

            var rmCnt = 0
            while (!removeQueue.isEmpty() && cacheSize > MAX_CACHE_SIZE * 0.8) {
                val rmEntry = removeQueue.poll()
                rmEntry.file.delete()
                logD { "Deleted from cache: ${rmEntry.file}" }
                synchronized(cache) {
                    cache.remove(rmEntry.file)
                    cacheSize -= rmEntry.size
                }
                rmCnt++
            }
        }
    }

    fun loadHttpResource(url: String): File? {
        val req = URL(url)

        // use host-name as cache directory name, sub-domain components are dropped
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
                    addCacheEntry(CacheEntry(file))
                    return file
                } else {
                    logW { "Unexpected response on downloading $url: ${con.responseCode} - ${con.responseMessage}" }
                }
            } catch (e: Exception) {
                logW { "Exception during download of $url: $e" }
            }
        }

        return if (file.canRead()) {
            synchronized(cache) {
                cache[file]?.lastAccess = System.currentTimeMillis()
            }
            file
        } else {
            logW { "Failed downloading $url" }
            null
        }
    }

    private fun InputStream.copyTo(file: File): Long {
        file.parentFile.mkdirs()
        return use { inStream ->
            FileOutputStream(file).use { outStream ->
                inStream.copyTo(outStream, 4096)
            }
        }
    }

    companion object {
        private const val MAX_CACHE_SIZE = 1024L * 1024L * 1024L
        private var instance: HttpCache? = null

        private val credentialsMap = mutableMapOf<String, BasicAuthCredentials>()

        fun addCredentials(credentials: BasicAuthCredentials) {
            credentialsMap[credentials.forHost] = credentials
        }

        fun initCache(cacheDir: File) {
            if (instance == null) {
                instance = HttpCache(cacheDir)
            }
        }

        fun loadHttpResource(url: String): File? {
            val inst = instance ?: throw KoolException("Default cache used before initCache() was called")
            return inst.loadHttpResource(url)
        }
    }

    class BasicAuthCredentials(val forHost: String, user: String, password: String) {
        val encoded = "Basic " + Base64.encodeToString("$user:$password".toByteArray(), Base64.NO_WRAP)
    }

    private class CacheEntry(val file: File, var size: Long, lastAccess: Long) : Serializable, Comparable<CacheEntry> {
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
/*class HttpCache private constructor(val cacheDir: File) {

    private val cache = mutableMapOf<File, CacheEntry>()
    private var cacheSize = 0L

    private val httpDispatcher = Executors.newFixedThreadPool(MAX_PARALLEL_REQUESTS) { r ->
        Executors.defaultThreadFactory().newThread(r).apply { isDaemon = true }
    }.asCoroutineDispatcher()

    init {
        try {
            ObjectInputStream(FileInputStream(cacheDir)).use {
                @Suppress("UNCHECKED_CAST")
                val entries = it.readObject() as List<CacheEntry>
                entries.filter { it.file.canRead() }.forEach { entry ->
                    entry.fileDeferred = CompletableDeferred(entry.file)
                    addCacheEntry(entry)
                }
                checkCacheSize()
            }
        } catch (e: Exception) {
            rebuildIndex()
        }

        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                close()
            }
        })
    }

    private fun close() {
        synchronized(cache) {
            cache.values.forEach {
                it.fileDeferred?.cancel()
            }
        }
        saveIndex()
    }

    private fun rebuildIndex() {
        synchronized(cache) {
            cache.clear()
            if (!cacheDir.exists() && !cacheDir.mkdirs()) {
                throw KoolException("Failed to create cache directory")
            }
        }

        fun File.walk(recv: (File) -> Unit) {
            listFiles().forEach {
                if (it.isDirectory) {
                    it.walk(recv)
                } else {
                    recv(it)
                }
            }
        }
        cacheDir.walk {
            if (it.name != ".cacheIndex") {
                addCacheEntry(CacheEntry(it, it.length(), System.currentTimeMillis()).apply {
                    fileDeferred = CompletableDeferred(it)
                })
            }
        }
        saveIndex()
    }

    private fun addCacheEntry(entry: CacheEntry) {
        synchronized(cache) {
            cacheSize -= cache[entry.file]?.size ?: 0
            cacheSize += entry.size
            cache[entry.file] = entry
        }
        checkCacheSize()
    }

    private fun saveIndex() {
        val entries = mutableListOf<CacheEntry>()
        synchronized(cache) {
            entries.addAll(cache.values)
        }
        try {
            ObjectOutputStream(FileOutputStream("$cacheDir/.cacheIndex")).use {
                it.writeObject(entries)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkCacheSize() {
        if (cacheSize > MAX_CACHE_SIZE) {
            val removeQueue = PriorityQueue<CacheEntry>()
            synchronized(cache) {
                removeQueue.addAll(cache.values)
            }

            var rmCnt = 0
            while (!removeQueue.isEmpty() && cacheSize > MAX_CACHE_SIZE * 0.8) {
                val rmEntry = removeQueue.poll()
                rmEntry.file.delete()
                synchronized(cache) {
                    cache.remove(rmEntry.file)
                    cacheSize -= rmEntry.size
                }
                rmCnt++
            }
        }
    }

    suspend fun loadHttpResource(url: String): File? {
        val req = URL(url)

        // use host-name as cache directory name, sub-domain components are dropped
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

        var load = false
        val entry: CacheEntry = synchronized(cache) {
            val cached = cache[file]
            if (cached != null && !cached.isReloadNeeded) {
                cached.lastAccess = System.currentTimeMillis()
                cached
            } else {
                val loading = CacheEntry(file, 0, System.currentTimeMillis())
                addCacheEntry(loading)
                load = true
                loading
            }
        }

        if (load) {
            entry.fileDeferred = GlobalScope.async(httpDispatcher) {
                file.parentFile.mkdirs()
                entry.size = 0L

                req.openStream().use { httpStream ->
                    FileOutputStream(file).use { outStream ->
                        val buf = ByteArray(4096)
                        var len = 1
                        while (len > 0) {
                            len = httpStream.read(buf)
                            if (len > 0) {
                                outStream.write(buf, 0, len)
                                entry.size += len
                            }
                        }
                    }
                }
                synchronized(cache) {
                    cacheSize += entry.size
                }
                entry.file
            }
            checkCacheSize()
        }

        return try {
            entry.fileDeferred?.await().also { entry.lastAccess = System.currentTimeMillis() }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    companion object {
        private const val MAX_CACHE_SIZE = 100L * 1024L * 1024L
        private const val MAX_PARALLEL_REQUESTS = 4
        private var instance: HttpCache? = null

        fun initCache(cacheDir: File) {
            if (instance == null) {
                instance = HttpCache(cacheDir)
            }
        }

        suspend fun loadHttpResource(url: String): File {
            val inst = instance ?: throw KoolException("Default cache used before initCache() was called")
            return inst.loadHttpResource(url) ?: throw KoolException("Failed loading http resource $url")
        }
    }

    private class CacheEntry(val file: File, var size: Long, lastAccess: Long) : Serializable, Comparable<CacheEntry> {

        var lastAccess = lastAccess
            set(value) {
                field = value
                file.setLastModified(value)
            }

        @Transient
        var fileDeferred: Deferred<File>? = null

        val isReloadNeeded: Boolean
            get() {
                val fd = fileDeferred
                return fd != null && ((fd.isCompleted && !fd.getCompleted().canRead()))
            }

        override fun compareTo(other: CacheEntry): Int {
            return when {
                lastAccess < other.lastAccess -> -1
                lastAccess > other.lastAccess -> 1
                else -> 0
            }
        }
    }
}*/