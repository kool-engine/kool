package de.fabmax.kool.modules.globe.elevation

import de.fabmax.kool.AssetManager

class LoadedMapCache(private val maxMaps: Int) {

    private val loadedMaps = mutableMapOf<String, LoadedMap>()
    private var useCnt = 0L

    fun getOrLoad(baseDir: String, meta: ElevationMapMeta, assetMgr: AssetManager): ElevationMap {
        val loaded = loadedMaps.getOrPut(meta.name) { LoadedMap(0L, meta.name, loadElevationMap(baseDir, meta, assetMgr)) }
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

    private data class LoadedMap(var lastUsed: Long, val key: String, val map: ElevationMap)
}