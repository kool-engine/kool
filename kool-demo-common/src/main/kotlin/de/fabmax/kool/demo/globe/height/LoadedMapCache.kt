package de.fabmax.kool.demo.globe.height

class LoadedMapCache(val maxMaps: Int) {

    private val loadedMaps = mutableMapOf<String, LoadedMap>()
    private var useCnt = 0L

    fun getOrLoad(baseDir: String, meta: HeightMapMeta): HeightMap {
        val loaded = loadedMaps.getOrPut(meta.path) { LoadedMap(0L, meta.path, loadHeightMap(baseDir, meta)) }
        loaded.lastUsed = ++useCnt

        if (loadedMaps.size > maxMaps) {
            if (loadedMaps.size > maxMaps) {
                val remCnt = loadedMaps.size - maxMaps
                val sorted = loadedMaps.values.sortedBy { it.lastUsed }
                for (i in 0..remCnt) {
                    loadedMaps -= sorted[i].key
                }
            }
        }
        return loaded.map
    }

    private data class LoadedMap(var lastUsed: Long, val key: String, val map: HeightMap)
}