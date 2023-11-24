package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.lang.*

class RaySphereIntersection(parentScope: KslScopeBuilder) :
    KslFunction<KslFloat3>(FUNC_NAME, KslFloat3, parentScope.parentStage) {

    init {
        val rayOrigin = paramFloat3("rayOrigin")
        val rayDir = paramFloat3("rayDir")
        val center = paramFloat3("center")
        val radius = paramFloat1("radius")

        body {
            val centerToOri = float3Var(rayOrigin - center)
            val a = float1Var(dot(rayDir, rayDir))
            val b = float1Var(2f.const * dot(rayDir, centerToOri))
            val c = float1Var(dot(centerToOri, centerToOri) - radius * radius)

            val discriminant = float1Var(b * b - 4f.const * a * c)
            val result = float3Var(Vec3f.ZERO.const)
            `if`(discriminant ge 0f.const) {
                val q = float1Var((-0.5f).const * (b + sign(b) * sqrt(discriminant)))
                val t1 = float1Var(q / a)
                val t2 = float1Var(c / q)
                result.x set min(t1, t2)
                result.y set max(t1, t2)
                result.z set step(0f.const, result.y)
            }
            result
        }
    }

    companion object {
        const val FUNC_NAME = "raySphereIntersection"
    }
}

/**
 * Computes ray-sphere intersection distances and returns them as a float3:
 * - x: distance from ray origin to inbound sphere hit
 * - y: distance from ray origin to outbound sphere hit
 * - z: 1.0 if sphere is hit, 0.0 if not
 */
fun KslScopeBuilder.raySphereIntersection(
    rayOrigin: KslExprFloat3,
    rayDir: KslExprFloat3,
    center: KslExprFloat3,
    radius: KslExprFloat1
): KslExprFloat3 {
    val func = parentStage.getOrCreateFunction(RaySphereIntersection.FUNC_NAME) { RaySphereIntersection(this) }
    return func(rayOrigin, rayDir, center, radius)
}

class RayPointDistance(parentScope: KslScopeBuilder) :
    KslFunction<KslFloat4>(FUNC_NAME, KslFloat4, parentScope.parentStage) {

    init {
        val rayOrigin = paramFloat3("rayOrigin")
        val rayDir = paramFloat3("rayDir")
        val point = paramFloat3("point")

        body {
            val w = float3Var(point - rayOrigin)
            val c1 = float1Var(dot(w, rayDir))
            val c2 = float1Var(dot(rayDir, rayDir))
            val pn = float3Var(rayOrigin + rayDir * (c1 / c2))
            float4Value(pn, length(pn - point) * sign(c1))
        }
    }

    companion object {
        const val FUNC_NAME = "rayPointDistance"
    }
}

/**
 * Computes the distance between a ray and an arbitrary point and returns the result as a float4:
 * - xyz: point on the ray nearest to query point
 * - w: distance between query point and nearest point on ray
 */
fun KslScopeBuilder.rayPointDistance(
    rayOrigin: KslExprFloat3,
    rayDir: KslExprFloat3,
    point: KslExprFloat3
): KslExprFloat4 {
    val func = parentStage.getOrCreateFunction(RayPointDistance.FUNC_NAME) { RayPointDistance(this) }
    return func(rayOrigin, rayDir, point)
}