package de.fabmax.kool.util

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.distanceToEdge
import de.fabmax.kool.math.nearestPointOnEdge

class LineString<T: Vec3f>(private val points: MutableList<T> = mutableListOf()) : MutableList<T> by points {

    fun isSingular(): Boolean = size <= 1

    fun length(): Float {
        var len = 0f
        for (i in 0 until lastIndex) {
            len += get(i).distance(get(i+1))
        }
        return len
    }

    fun getLengthToIndex(index: Int): Float {
        var len = 0f
        for (i in 0 until index) {
            len += get(i).distance(get(i+1))
        }
        return len
    }

    fun getLowerIndex(forDistance: Float): Int {
        var d = forDistance
        for (i in 0 until lastIndex) {
            d -= get(i).distance(get(i+1))
            if (d < 0) {
                return i
            }
        }
        return lastIndex-1
    }

    fun getNearestElement(forDistance: Float): T {
        var d = forDistance
        for (i in 0 until lastIndex) {
            val l = get(i).distance(get(i+1))
            if (l > d) {
                return if (l > d*2) get(i) else get(i+1)
            }
            d -= l
        }
        return last()
    }

    fun getNearestPoint(forDistance: Float, result: MutableVec3f): MutableVec3f {
        if (forDistance < 0f) {
            return result.set(first())
        }

        var d = forDistance
        for (i in 0 until lastIndex) {
            val l = get(i).distance(get(i+1))
            if (l > d) {
                return result.set(get(i+1)).subtract(get(i)).scale(d / l).add(get(i))
            }
            d -= l
        }
        return result.set(last())
    }

    fun getLowerIndex(forPoint: Vec3f, startIndex: Int = 0): Int {
        var minDist = Float.MAX_VALUE
        var bestI = startIndex
        for (i in 0 until lastIndex) {
            val d = forPoint.distanceToEdge(get(i), get(i+1))
            if (d < minDist) {
                minDist = d
                bestI = i
            }
        }
        return bestI
    }

    fun getNearestElement(forPoint: Vec3f, startIndex: Int = 0): T {
        val i = getLowerIndex(forPoint, startIndex)
        return if (get(i).distance(forPoint) < get(i+1).distance(forPoint)) get(i) else get(i+1)
    }

    fun getNearestPoint(forPoint: Vec3f, result: MutableVec3f, startIndex: Int = 0): MutableVec3f {
        val lowerI = getLowerIndex(forPoint, startIndex)
        return forPoint.nearestPointOnEdge(get(lowerI), get(lowerI+1), result)
    }
}