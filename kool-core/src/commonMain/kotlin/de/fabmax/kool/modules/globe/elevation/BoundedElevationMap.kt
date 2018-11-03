package de.fabmax.kool.modules.globe.elevation

import de.fabmax.kool.math.FUZZY_EQ_F

interface BoundedElevationMap : ElevationMap {
    val west: Double
    val east: Double
    val south: Double
    val north: Double

    val centerLat: Double get() = (north + south) / 2.0
    val centerLon: Double get() = (east + west) / 2.0

    override fun contains(lat: Double, lon: Double) = lat in (south - FUZZY_EQ_F)..(north + FUZZY_EQ_F)
            && lon in (west - FUZZY_EQ_F)..(east + FUZZY_EQ_F)

}
