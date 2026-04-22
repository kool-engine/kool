package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.modules.ksl.lang.*

class GetLinearDepth(parentScope: KslScopeBuilder) :
    KslFunction<KslFloat1>(FUNC_NAME, KslFloat1, parentScope.parentStage) {

    init {
        val depth = paramFloat1("depth")
        val camNear = paramFloat1("camNear")
        val camFar = paramFloat1("camFar")
        body {
            return@body camNear * camFar / (camFar - depth * (camFar - camNear))
        }
    }

    companion object {
        const val FUNC_NAME = "GetLinearDepth"
    }
}

fun KslScopeBuilder.getLinearDepth(
    depth: KslExprFloat1,
    camNear: KslExprFloat1,
    camFar: KslExprFloat1
): KslExprFloat1 {
    val func = parentStage.getOrCreateFunction(GetLinearDepth.FUNC_NAME) { GetLinearDepth(this) }
    return func(depth, camNear, camFar)
}

class GetLinearDepthReversed(parentScope: KslScopeBuilder) :
    KslFunction<KslFloat1>(FUNC_NAME, KslFloat1, parentScope.parentStage) {

    init {
        val depth = paramFloat1("depth")
        val camNear = paramFloat1("camNear")
        body {
            return@body camNear / max(1e-15f.const, depth)
        }
    }

    companion object {
        const val FUNC_NAME = "GetLinearDepthReversed"
    }
}

fun KslScopeBuilder.getLinearDepthReversed(
    depth: KslExprFloat1,
    camNear: KslExprFloat1,
): KslExprFloat1 {
    val func = parentStage.getOrCreateFunction(GetLinearDepthReversed.FUNC_NAME) { GetLinearDepthReversed(this) }
    return func(depth, camNear)
}
