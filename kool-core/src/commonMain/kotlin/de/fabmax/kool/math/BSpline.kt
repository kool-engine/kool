package de.fabmax.kool.math

import kotlin.math.PI

open class BSpline<T>(var degree: Int, private val factory: () -> T, private val copy: (src: T, dst: T) -> Unit,
                 private val mix: (w0: Float, p0: T, w1: Float, p1: T, result: T) -> Unit) {

    val ctrlPoints = mutableListOf<T>()

    private val d = mutableListOf<T>()

    fun addInterpolationEndpoints() {
        for (i in 0 until (degree - 1)) {
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

                // fixme: use better b-spline algorithm
                //  this spline implementation does not provide equidistant samples for equidistant input values
                //  so we use this gross linearization function to improve this behavior a bit
                val xLin = linearize(x, degree - 2)

                val xx = degree + xLin * (ctrlPoints.size - degree)
                deBoor(xx.toInt(), xx, result)
            }
        }
        return result
    }

    private fun linearize(x: Float, deg: Int): Float {
        var xx = x
        for (i in 0 until deg) {
            xx = linearize(xx)
        }
        return xx
    }

    private fun linearize(x: Float) = 1f - stableAcos((x - 0.5f) * 2f) / PI.toFloat()

    private fun deBoor(k: Int, t: Float, result: T) {
        val kk = k.clamp(degree, ctrlPoints.size - 1)

        for (j in 0..degree) {
            copy(ctrlPoints[j + kk - degree], d[j])
        }
        for (r in 1..degree) {
            for (j in degree downTo r) {
                val alpha = (t - (j + kk - degree).toFloat()) / ((j + 1 + kk - r).toFloat() - (j + kk - degree).toFloat())
                mix(1f - alpha, d[j-1], alpha, d[j], d[j])
            }
        }
        copy(d[degree], result)
    }

    private fun checkTemps() {
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

class SimpleSpline3f {
    val ctrlPoints = mutableListOf<CtrlPoint>()

    private val splinePieces = mutableListOf<BSplineVec3f>()

    fun evaluate(x: Float, result: MutableVec3f): MutableVec3f {
        checkSplinePieces()
        val splineI = x.toInt().clamp(0, splinePieces.lastIndex)
        val splineF = (x - splineI).clamp(0f, 1f)
        return splinePieces[splineI].evaluate(splineF, result)
    }

    private fun checkSplinePieces() {
        if (splinePieces.size != ctrlPoints.size - 1) {
            update()
        }
    }

    fun update() {
        splinePieces.clear()
        for (i in 1 until ctrlPoints.size) {
            val ctrl0 = ctrlPoints[i-1]
            val ctrl1 = ctrlPoints[i]
            splinePieces += BSplineVec3f(3).apply {
                ctrlPoints += ctrl0
                ctrlPoints += MutableVec3f(ctrl0).add(ctrl0.direction)
                ctrlPoints += MutableVec3f(ctrl1).subtract(ctrl1.direction)
                ctrlPoints += ctrl1
                addInterpolationEndpoints()
            }
        }
    }

    class CtrlPoint(x: Float, y: Float, z: Float) : MutableVec3f(x, y, z) {
        val direction = MutableVec3f(1f, 0f, 0f)

        constructor() : this(0f, 0f, 0f)
        constructor(pt: Vec3f) : this(pt.x, pt.y, pt.z)
        constructor(pt: Vec3f, direction: Vec3f) : this(pt.x, pt.y, pt.z) {
            this.direction.set(direction)
        }
    }
}

