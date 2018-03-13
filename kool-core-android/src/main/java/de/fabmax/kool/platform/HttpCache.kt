package de.fabmax.kool.platform

import de.fabmax.kool.KoolException
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.CompletableDeferred
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import java.io.*
import java.net.URL
import java.util.*
import kotlin.coroutines.experimental.coroutineContext

class HttpCache private constructor(val cacheDir: File) {

    private val cache = mutableMapOf<File, CacheEntry>()
    private var cacheSize = 0L

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
            cache.values.forEach{
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
            println("Deleted $rmCnt cache entries")
        }
    }

    suspend fun loadHttpResource(url: String): File? {
        val req = URL(url)
        val file = if (req.query != null) {
            File(cacheDir, "/${req.host}/${req.path}_${req.query}")
        } else {
            File(cacheDir, "/${req.host}/${req.path}")
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
//            Log.i("HttpCache", "Loading $url")
            entry.fileDeferred = async(coroutineContext) {
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
//        else {
//            Log.i("HttpCache", "Got from cache: $url")
//        }

        return try {
            entry.fileDeferred?.await().also { entry.lastAccess = System.currentTimeMillis() }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    companion object {
        private const val MAX_CACHE_SIZE = 100L * 1024L * 1024L
        private var instance: HttpCache? = null

        var assetLoadingCtx = CommonPool

        fun initCache(cacheDir: File) {
            if (instance == null) {
                instance = HttpCache(cacheDir)
            }
        }

        suspend fun loadHttpResource(url: String): File {
            val inst = instance ?: throw KoolException("Default cache used before initCache() was called")
            return inst.loadHttpResource(url) ?: throw KoolException("Failed loading http resource")
        }
    }

    private class CacheEntry(val file: File, var size: Long, lastAccess: Long) :
            Serializable, Comparable<CacheEntry> {

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
                return fd != null && ((fd.isCompleted && !fd.getCompleted().canRead()) || fd.isCompletedExceptionally)
            }

        override fun compareTo(other: CacheEntry): Int {
            return when {
                lastAccess < other.lastAccess -> -1
                lastAccess > other.lastAccess -> 1
                else -> 0
            }
        }
    }

}