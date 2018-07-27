package de.fabmax.kool.modules.globe.elevation

import de.fabmax.kool.AssetManager
import de.fabmax.kool.util.logD

class ElevationMapHierarchy(private val baseDir: String, metaHierarchy: ElevationMapMetaHierarchy, private val assetMgr: AssetManager) : ElevationMapProvider {

    private val sets = mutableListOf<ResolutionSet>()
    private val loadedMaps = LoadedMapCache(64)

    init {
        metaHierarchy.maps.forEach { (resolution, metas) ->
            sets += ResolutionSet(resolution, ElevationMapSet(metas))
        }
        sets.sortBy { it.resolution }
        logD { "taking elevation data from: $baseDir" }
    }

    override fun getElevationMapAt(lat: Double, lon: Double, resolution: Double): ElevationMap? {
        var bestSet = sets.first()
        for (i in sets.indices) {
            // select set with lowest resolution, that is still higher than requested resolution
            if (sets[i].resolution < resolution) {
                bestSet = sets[i]
            } else {
                break
            }
        }

        val meta = bestSet.set.getMetaAt(lat, lon)
        if (meta != null) {
            return synchronized(loadedMaps) {
                loadedMaps.getOrLoad(baseDir, meta, assetMgr)
            }
        }
        return null
    }

    private data class ResolutionSet(val resolution: Double, val set: ElevationMapSet)
}