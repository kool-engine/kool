package de.fabmax.kool.math

abstract class PointDistribution {

    abstract fun nextPoint(): Vec3f

    fun nextPoints(n: Int): List<Vec3f> {
        val points = mutableListOf<Vec3f>()
        for (i in 1..n) {
            points += nextPoint()
        }
        return points
    }
}

class CubicPointDistribution(val size: Float = 1f, val center: Vec3f = Vec3f.ZERO,
                             val random: Random = defaultRandomInstance) : PointDistribution() {

    private val s = size * 0.5f

    override fun nextPoint(): Vec3f {
        return Vec3f(center.x + random.randomF(-s, s), center.y + random.randomF(-s, s),
                center.z + random.randomF(-s, s))
    }
}

class SphericalPointDistribution(val radius: Float = 1f, val center: Vec3f = Vec3f.ZERO,
                                 val random: Random = defaultRandomInstance) : PointDistribution() {

    private val rSqr = radius * radius

    override fun nextPoint(): Vec3f {
        while (true) {
            val x = random.randomF(-radius, radius)
            val y = random.randomF(-radius, radius)
            val z = random.randomF(-radius, radius)
            if (x*x + y*y + z*z < rSqr) {
                return Vec3f(center.x + x, center.y + y, center.z + z)
            }
        }
    }
}
