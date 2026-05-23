package de.fabmax.kool.demo.deferred2

import de.fabmax.kool.modules.ksl.blocks.getLinearDepthReversed
import de.fabmax.kool.modules.ksl.lang.*

context(scope: KslScopeBuilder)
fun baseCoordToUv(baseCoord: KslExprInt2, viewSize: KslExprInt2): KslExprFloat2  {
    val uv = float2Var((baseCoord.toFloat2() + 0.5f.const2) / viewSize.toFloat2())
    uv.y set 1f.const - uv.y
    return uv
}

class UnprojectBaseCoord(
    parentScope: KslScopeBuilder
) : KslFunction<KslFloat4>("fnUnprojectBaseCoord", KslFloat4, parentScope.parentStage) {
    init {
        val depth = paramFloat1("depth")
        val baseCoord = paramInt2("baseCoord")
        val size = paramInt2("size")
        val camNear = paramFloat1("camNear")
        val invViewProj = paramMat4("invProj")

        body {
            val uv by baseCoordToUv(baseCoord, size)
            val viewDepth by getLinearDepthReversed(depth, camNear)
            val viewProjXy by (uv * 2f.const - 1f.const) * viewDepth
            val viewProjPos by float4Value(viewProjXy, camNear, viewDepth)
            val worldPos by invViewProj * viewProjPos
            worldPos
        }
    }
}

fun KslScopeBuilder.unprojectBaseCoord(
    depth: KslExprFloat1,
    baseCoord: KslExprInt2,
    size: KslExprInt2,
    camNear: KslExprFloat1,
    invViewProj: KslExprMat4,
): KslExprFloat4 {
    val func = parentStage.getOrCreateFunction("fnUnprojectBaseCoord") { UnprojectBaseCoord(this) }
    return func(depth, baseCoord, size, camNear, invViewProj)
}


class UnprojectUv(
    depth: KslUniform<KslColorSampler2d>,
    parentScope: KslScopeBuilder
) : KslFunction<KslFloat4>("fnUnprojectUv", KslFloat4, parentScope.parentStage) {
    init {
        val uv = paramFloat2("uv")
        val camNear = paramFloat1("camNear")
        val invProj = paramMat4("invProj")
        val invView = paramMat4("invView")

        body {
            // todo: need to invert uv.y?
            val viewDepth by getLinearDepthReversed(depth.sample(uv, lod = 0f.const).x, camNear)
            val viewProjXy by (uv * 2f.const - 1f.const) * viewDepth
            val viewProjPos by float4Value(viewProjXy, camNear, viewDepth)
            val viewPos by invProj * viewProjPos
            val worldPos by invView * float4Value(viewPos.xyz, 1f)
            worldPos
        }
    }
}
