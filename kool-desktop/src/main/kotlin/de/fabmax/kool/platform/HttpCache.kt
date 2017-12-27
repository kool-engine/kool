package de.fabmax.kool.platform

import de.fabmax.kool.KoolException
import java.io.*
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.CompletableFuture

class HttpCache private constructor(val cacheDir: String) {

    private val cache = mutableMapOf<File, CacheEntry>()
    private var cacheSize = 0L

    init {
        try {
            ObjectInputStream(FileInputStream(cacheDir + "/.cacheIndex")).use {
                @Suppress("UNCHECKED_CAST")
                val entries = it.readObject() as List<CacheEntry>
                for (entry in entries) {
                    entry.fileFuture = CompletableFuture.completedFuture(entry.file)
                    addCacheEntry(entry)
                }
                checkCacheSize()
            }
        } catch (e: Exception) {
            rebuildIndex()
        }

        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                saveIndex()
            }
        })
    }

    private fun rebuildIndex() {
        //println("Rebuilding cache index")
        val cacheDir = Paths.get(cacheDir)
        synchronized(cache) {
            cache.clear()
            val dir = cacheDir.toFile()
            if (!dir.exists() && !dir.mkdirs()) {
                throw KoolException("Failed to create cache directory")
            }
        }
        Files.walk(cacheDir).forEach {
            val f = it.toFile()
            if (!f.isDirectory && it.fileName.toString() != ".cacheIndex") {
                addCacheEntry(CacheEntry(f, f.length(), System.currentTimeMillis()).apply { fileFuture.complete(f) })
            }
        }
        saveIndex()
    }

    private fun addCacheEntry(entry: CacheEntry) {
        synchronized(cache) {
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
            ObjectOutputStream(FileOutputStream(cacheDir + "/.cacheIndex")).use {
                it.writeObject(entries)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkCacheSize() {
        if (cacheSize > MAX_CACHE_SIZE) {
            val removeQueue = PriorityQueue<CacheEntry>(Comparator.comparingLong { e -> e.lastAccess })
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

    private fun loadHttpResource(url: String): File {
        val req = URL(url)
        val cachePath = if (req.query != null) {
            cacheDir + '/' + req.host + '/' + req.path + '_' + req.query
        } else {
            cacheDir + '/' + req.host + '/' + req.path
        }
        val file = File(cachePath)

        var load = false
        val entry: CacheEntry = synchronized(cache) {
            val cached = cache[file]
            if (cached != null) {
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
            file.parentFile.mkdirs()

            var size = 0L
            var instream: InputStream? = null
            var outstream: OutputStream? = null
            try {
                instream = req.openStream()
                outstream = FileOutputStream(file)
                val buf = ByteArray(4096)
                var len = 1
                while (len > 0) {
                    len = instream.read(buf)
                    if (len > 0) {
                        outstream.write(buf, 0, len)
                        size += len
                    }
                }
            } finally {
                try {
                    instream?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                try {
                    outstream?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            entry.size = size
            entry.fileFuture.complete(file)
            synchronized(cache) {
                cacheSize += size
            }
            checkCacheSize()
        }

        return entry.fileFuture.get().also { entry.lastAccess = System.currentTimeMillis() }
    }


    companion object {
        private const val MAX_CACHE_SIZE = 500L * 1024L * 1024L
        private val instance = HttpCache("./.httpCache")

        fun loadHttpResource(url: String): File {
            return instance.loadHttpResource(url)
        }
    }

    private data class CacheEntry(val file: File, var size: Long, var lastAccess: Long) : Serializable {
        @Transient
        var fileFuture = CompletableFuture<File>()
    }

}