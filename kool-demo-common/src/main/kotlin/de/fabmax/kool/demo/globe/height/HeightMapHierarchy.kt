package de.fabmax.kool.demo.globe.height

class HeightMapHierarchy(private val baseDir: String, metaHierarchy: HeightMapMetaHierarchy) : HeightMapProvider {

    private val sets = mutableListOf<ResolutionSet>()
    val loadedMaps = LoadedMapCache(64)

    init {
        metaHierarchy.maps.forEach { (resolution, metas) ->
            sets += ResolutionSet(resolution, HeightMapSet(metas))
        }
        sets.sortBy { it.resolution }
    }

    override fun getHeightMapAt(lat: Double, lon: Double, resolution: Double): HeightMap? {
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
                loadedMaps.getOrLoad(baseDir, meta)
            }
        }
        return null
    }

//    override fun getHeightAt(lat: Double, lon: Double, resolution: Double): Double {
//        return getMapAt(lat, lon, resolution)?.getHeightAt(lat, lon, resolution) ?: 0.0
//    }
//
//    override fun getNormalAt(lat: Double, lon: Double, resolution: Double, result: MutableVec3f): MutableVec3f {
//        return getMapAt(lat, lon, resolution)?.getNormalAt(lat, lon, resolution, result) ?: result.set(Vec3f.Z_AXIS)
//    }

    private data class ResolutionSet(val resolution: Double, val set: HeightMapSet)
}