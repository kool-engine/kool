package de.fabmax.kool.math

open class BSpline<T>(var degree: Int, private val factory: () -> T, private val copy: (src: T, dst: T) -> Unit,
                 private val mix: (w0: Float, p0: T, w1: Float, p1: T, result: T) -> Unit) {

    val ctrlPoints = mutableListOf<T>()

    private val knots = mutableListOf<Float>()
    private val d = mutableListOf<T>()

    fun addInterpolationEndpoints() {
        for (i in 0 until degree) {
            ctrlPoints.add(0, ctrlPoints.first())
            ctrlPoints += ctrlPoints.last()
        }
    }

    fun evaluate(x: Float, result: T): T {
        when {
            x <= 0f -> copy(ctrlPoints.first(), result)
            x >= 1f -> copy(ctrlPoints.last(), result)
            else -> {
                checkTemps()
                val xx = degree + x * (ctrlPoints.size - degree*2 + 1)
                deBoor(xx.toInt(), xx, result)
            }
        }
        return result
    }

    private fun deBoor(k: Int, t: Float, result: T) {
        val kk = k.clamp(degree, ctrlPoints.size - 1)

        for (j in 0..degree) {
            copy(ctrlPoints[j + kk - degree], d[j])
        }
        for (r in 1..degree) {
            for (j in degree downTo r) {
                val alpha = (t - knots[j + kk - degree]) / (knots[j + 1 + kk - r] - knots[j + kk - degree])
                mix(1f - alpha, d[j-1], alpha, d[j], d[j])
            }
        }
        copy(d[degree], result)
    }

    private fun checkTemps() {
        if (knots.size != ctrlPoints.size + degree) {
            knots.clear()
            for (i in 0 until ctrlPoints.size + degree) {
                knots += i.toFloat()
            }
        }
        if (d.size != degree+1) {
            d.clear()
            for (i in 0..degree) {
                d += factory()
            }
        }
    }
}

class BSplineVec2f(degree: Int) : BSpline<MutableVec2f>(degree, { MutableVec2f() }, { src, dst -> dst.set(src) },
        { w0, p0, w1, p1, result ->
            result.x = p0.x * w0 + p1.x * w1
            result.y = p0.y * w0 + p1.y * w1
        })

class BSplineVec3f(degree: Int) : BSpline<MutableVec3f>(degree, { MutableVec3f() }, { src, dst -> dst.set(src) },
        { w0, p0, w1, p1, result ->
            result.x = p0.x * w0 + p1.x * w1
            result.y = p0.y * w0 + p1.y * w1
            result.z = p0.z * w0 + p1.z * w1
        })
