package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.modules.ksl.lang.*

/*
 The following noise functions are based on:

 https://www.shadertoy.com/view/4djSRW#

 // Hash without Sine
 // MIT License...
 // Copyright (c)2014 David Hoskins.

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */

/**
 * Returns 1d noise for 1d input.
 */
fun KslScopeBuilder.noise11(p: KslExprFloat1): KslExprFloat1 {
    val func = parentStage.getOrCreateFunction(Noise11.FUNC_NAME) { Noise11(this) }
    return func(p)
}

/**
 * Returns 1d noise for 2d input.
 */
fun KslScopeBuilder.noise12(p: KslExprFloat2): KslExprFloat1 {
    val func = parentStage.getOrCreateFunction(Noise12.FUNC_NAME) { Noise12(this) }
    return func(p)
}

/**
 * Returns 1d noise for 3d input.
 */
fun KslScopeBuilder.noise13(p: KslExprFloat3): KslExprFloat1 {
    val func = parentStage.getOrCreateFunction(Noise13.FUNC_NAME) { Noise13(this) }
    return func(p)
}

/**
 * Returns 1d noise for 4d input.
 */
fun KslScopeBuilder.noise14(p: KslExprFloat4): KslExprFloat1 {
    val func = parentStage.getOrCreateFunction(Noise14.FUNC_NAME) { Noise14(this) }
    return func(p)
}

/**
 * Returns 2d noise for 1d input.
 */
fun KslScopeBuilder.noise21(p: KslExprFloat1): KslExprFloat2 {
    val func = parentStage.getOrCreateFunction(Noise21.FUNC_NAME) { Noise21(this) }
    return func(p)
}

/**
 * Returns 2d noise for 2d input.
 */
fun KslScopeBuilder.noise22(p: KslExprFloat2): KslExprFloat2 {
    val func = parentStage.getOrCreateFunction(Noise22.FUNC_NAME) { Noise22(this) }
    return func(p)
}

/**
 * Returns 3d noise for 2d input.
 */
fun KslScopeBuilder.noise23(p: KslExprFloat3): KslExprFloat2 {
    val func = parentStage.getOrCreateFunction(Noise23.FUNC_NAME) { Noise23(this) }
    return func(p)
}

/**
 * Returns 1d noise for 3d input.
 */
fun KslScopeBuilder.noise31(p: KslExprFloat1): KslExprFloat3 {
    val func = parentStage.getOrCreateFunction(Noise31.FUNC_NAME) { Noise31(this) }
    return func(p)
}

/**
 * Returns 2d noise for 3d input.
 */
fun KslScopeBuilder.noise32(p: KslExprFloat2): KslExprFloat3 {
    val func = parentStage.getOrCreateFunction(Noise32.FUNC_NAME) { Noise32(this) }
    return func(p)
}

/**
 * Returns 3d noise for 3d input.
 */
fun KslScopeBuilder.noise33(p: KslExprFloat3): KslExprFloat3 {
    val func = parentStage.getOrCreateFunction(Noise33.FUNC_NAME) { Noise33(this) }
    return func(p)
}

/**
 * Returns 4d noise for 1d input.
 */
fun KslScopeBuilder.noise41(p: KslExprFloat1): KslExprFloat4 {
    val func = parentStage.getOrCreateFunction(Noise41.FUNC_NAME) { Noise41(this) }
    return func(p)
}

/**
 * Returns 4d noise for 2d input.
 */
fun KslScopeBuilder.noise42(p: KslExprFloat2): KslExprFloat4 {
    val func = parentStage.getOrCreateFunction(Noise42.FUNC_NAME) { Noise42(this) }
    return func(p)
}

/**
 * Returns 4d noise for 3d input.
 */
fun KslScopeBuilder.noise43(p: KslExprFloat3): KslExprFloat4 {
    val func = parentStage.getOrCreateFunction(Noise43.FUNC_NAME) { Noise43(this) }
    return func(p)
}

/**
 * Returns 4d noise for 4d input.
 */
fun KslScopeBuilder.noise44(p: KslExprFloat4): KslExprFloat4 {
    val func = parentStage.getOrCreateFunction(Noise44.FUNC_NAME) { Noise44(this) }
    return func(p)
}

class Noise11(parentScope: KslScopeBuilder) :
    KslFunction<KslFloat1>(FUNC_NAME, KslFloat1, parentScope.parentStage) {
    init {
        val p = paramFloat1("p")
        body {
            // float hash11(float p) {
            //     p = fract(p * .1031);
            //     p *= p + 33.33;
            //     p *= p + p;
            //     return fract(p);
            // }
            val x = float1Var(fract(p * 0.1031f.const))
            x *= x + 33.33f.const
            x *= x + x
            return@body fract(x)
        }
    }

    companion object {
        const val FUNC_NAME = "noise11"
    }
}

class Noise12(parentScope: KslScopeBuilder) :
    KslFunction<KslFloat1>(FUNC_NAME, KslFloat1, parentScope.parentStage) {
    init {
        val p = paramFloat2("p")
        body {
            // float hash12(vec2 p) {
            //     vec3 p3  = fract(vec3(p.xyx) * .1031);
            //     p3 += dot(p3, p3.yzx + 33.33);
            //     return fract((p3.x + p3.y) * p3.z);
            // }
            val x = float3Var(fract(p.float3("xyx") * 0.1031f.const))
            x set x + dot(x, x.float3("yzx") + 33.33f.const)
            return@body fract((x.x + x.y) * x.z)
        }
    }

    companion object {
        const val FUNC_NAME = "noise12"
    }
}

class Noise13(parentScope: KslScopeBuilder) :
    KslFunction<KslFloat1>(FUNC_NAME, KslFloat1, parentScope.parentStage) {
    init {
        val p = paramFloat3("p")
        body {
            // float hash13(vec3 p3) {
            //     p3  = fract(p3 * .1031);
            //     p3 += dot(p3, p3.zyx + 31.32);
            //     return fract((p3.x + p3.y) * p3.z);
            // }
            val x = float3Var(fract(p * 0.1031f.const))
            x set x + dot(x, x.float3("yzx") + 31.32f.const)
            return@body fract((x.x + x.y) * x.z)
        }
    }

    companion object {
        const val FUNC_NAME = "noise13"
    }
}

class Noise14(parentScope: KslScopeBuilder) :
    KslFunction<KslFloat1>(FUNC_NAME, KslFloat1, parentScope.parentStage) {
    init {
        val p = paramFloat4("p")
        body {
            // float hash14(vec4 p4) {
            //     p4 = fract(p4  * vec4(.1031, .1030, .0973, .1099));
            //     p4 += dot(p4, p4.wzxy+33.33);
            //     return fract((p4.x + p4.y) * (p4.z + p4.w));
            // }
            val x = float4Var(fract(p * float4Value(0.1031f, 0.1030f, 0.0973f, 0.1099f)))
            x set x + dot(x, x.float4("wzxy") + 33.33f.const)
            return@body fract((x.x + x.y) * (x.z + x.w))
        }
    }

    companion object {
        const val FUNC_NAME = "noise14"
    }
}

class Noise21(parentScope: KslScopeBuilder) :
    KslFunction<KslFloat2>(FUNC_NAME, KslFloat2, parentScope.parentStage) {
    init {
        val p = paramFloat1("p")
        body {
            // vec2 hash21(float p) {
            //	   vec3 p3 = fract(vec3(p) * vec3(.1031, .1030, .0973));
            //	   p3 += dot(p3, p3.yzx + 33.33);
            //     return fract((p3.xx+p3.yz)*p3.zy);
            // }
            val x = float3Var(fract(float3Value(p, p, p) * float3Value(0.1031f, 0.1030f, 0.0973f)))
            x set x + dot(x, x.float3("yzx") + 33.33f.const)
            return@body fract((x.float2("xx") + x.yz) * x.float2("zy"))
        }
    }

    companion object {
        const val FUNC_NAME = "noise21"
    }
}

class Noise22(parentScope: KslScopeBuilder) :
    KslFunction<KslFloat2>(FUNC_NAME, KslFloat2, parentScope.parentStage) {
    init {
        val p = paramFloat2("p")
        body {
            // vec2 hash22(vec2 p) {
            //     vec3 p3 = fract(vec3(p.xyx) * vec3(.1031, .1030, .0973));
            //     p3 += dot(p3, p3.yzx+33.33);
            //     return fract((p3.xx+p3.yz)*p3.zy);
            // }
            val x = float3Var(fract(p.float3("xyx") * float3Value(0.1031f, 0.1030f, 0.0973f)))
            x set x + dot(x, x.float3("yzx") + 33.33f.const)
            return@body fract((x.float2("xx") + x.yz) * x.float2("zy"))
        }
    }

    companion object {
        const val FUNC_NAME = "noise22"
    }
}

class Noise23(parentScope: KslScopeBuilder) :
    KslFunction<KslFloat2>(FUNC_NAME, KslFloat2, parentScope.parentStage) {
    init {
        val p = paramFloat3("p")
        body {
            // vec2 hash23(vec3 p3) {
            //     p3 = fract(p3 * vec3(.1031, .1030, .0973));
            //     p3 += dot(p3, p3.yzx+33.33);
            //     return fract((p3.xx+p3.yz)*p3.zy);
            // }
            val x = float3Var(fract(p * float3Value(0.1031f, 0.1030f, 0.0973f)))
            x set x + dot(x, x.float3("yzx") + 33.33f.const)
            return@body fract((x.float2("xx") + x.yz) * x.float2("zy"))
        }
    }

    companion object {
        const val FUNC_NAME = "noise23"
    }
}

class Noise31(parentScope: KslScopeBuilder) :
    KslFunction<KslFloat3>(FUNC_NAME, KslFloat3, parentScope.parentStage) {
    init {
        val p = paramFloat1("p")
        body {
            // vec3 hash31(float p) {
            //     vec3 p3 = fract(vec3(p) * vec3(.1031, .1030, .0973));
            //     p3 += dot(p3, p3.yzx+33.33);
            //     return fract((p3.xxy+p3.yzz)*p3.zyx);
            // }
            val x = float3Var(fract(float3Value(p, p, p) * float3Value(0.1031f, 0.1030f, 0.0973f)))
            x set x + dot(x, x.float3("yzx") + 33.33f.const)
            return@body fract((x.float3("xxy") + x.float3("yzz")) * x.float3("zyx"))
        }
    }

    companion object {
        const val FUNC_NAME = "noise31"
    }
}

class Noise32(parentScope: KslScopeBuilder) :
    KslFunction<KslFloat3>(FUNC_NAME, KslFloat3, parentScope.parentStage) {
    init {
        val p = paramFloat2("p")
        body {
            // vec3 hash32(vec2 p) {
            //     vec3 p3 = fract(vec3(p.xyx) * vec3(.1031, .1030, .0973));
            //     p3 += dot(p3, p3.yxz+33.33);
            //     return fract((p3.xxy+p3.yzz)*p3.zyx);
            // }
            val x = float3Var(fract(p.float3("xyx") * float3Value(0.1031f, 0.1030f, 0.0973f)))
            x set x + dot(x, x.float3("yxz") + 33.33f.const)
            return@body fract((x.float3("xxy") + x.float3("yzz")) * x.float3("zyx"))
        }
    }

    companion object {
        const val FUNC_NAME = "noise32"
    }
}

class Noise33(parentScope: KslScopeBuilder) :
    KslFunction<KslFloat3>(FUNC_NAME, KslFloat3, parentScope.parentStage) {
    init {
        val p = paramFloat3("p")
        body {
            // vec3 hash33(vec3 p3) {
            //     p3 = fract(p3 * vec3(.1031, .1030, .0973));
            //     p3 += dot(p3, p3.yxz+33.33);
            //     return fract((p3.xxy + p3.yxx)*p3.zyx);
            // }
            val x = float3Var(fract(p * float3Value(0.1031f, 0.1030f, 0.0973f)))
            x set x + dot(x, x.float3("yxz") + 33.33f.const)
            return@body fract((x.float3("xxy") + x.float3("yxx")) * x.float3("zyx"))
        }
    }

    companion object {
        const val FUNC_NAME = "noise33"
    }
}

class Noise41(parentScope: KslScopeBuilder) :
    KslFunction<KslFloat4>(FUNC_NAME, KslFloat4, parentScope.parentStage) {
    init {
        val p = paramFloat1("p")
        body {
            // vec4 hash41(float p) {
            //     vec4 p4 = fract(vec4(p) * vec4(.1031, .1030, .0973, .1099));
            //     p4 += dot(p4, p4.wzxy+33.33);
            //     return fract((p4.xxyz+p4.yzzw)*p4.zywx);
            // }
            val x = float4Var(fract(float4Value(p, p, p, p) * float4Value(0.1031f, 0.1030f, 0.0973f, 0.1099f)))
            x set x + dot(x, x.float4("wzxy") + 33.33f.const)
            return@body fract((x.float4("xxyz") + x.float4("yzzw")) * x.float4("zywx"))
        }
    }

    companion object {
        const val FUNC_NAME = "noise41"
    }
}

class Noise42(parentScope: KslScopeBuilder) :
    KslFunction<KslFloat4>(FUNC_NAME, KslFloat4, parentScope.parentStage) {
    init {
        val p = paramFloat2("p")
        body {
            // vec4 hash41(float p) {
            //     vec4 p4 = fract(vec4(p) * vec4(.1031, .1030, .0973, .1099));
            //     p4 += dot(p4, p4.wzxy+33.33);
            //     return fract((p4.xxyz+p4.yzzw)*p4.zywx);
            // }
            val x = float4Var(fract(p.float4("xyxy") * float4Value(0.1031f, 0.1030f, 0.0973f, 0.1099f)))
            x set x + dot(x, x.float4("wzxy") + 33.33f.const)
            return@body fract((x.float4("xxyz") + x.float4("yzzw")) * x.float4("zywx"))
        }
    }

    companion object {
        const val FUNC_NAME = "noise42"
    }
}

class Noise43(parentScope: KslScopeBuilder) :
    KslFunction<KslFloat4>(FUNC_NAME, KslFloat4, parentScope.parentStage) {
    init {
        val p = paramFloat3("p")
        body {
            // vec4 hash43(vec3 p) {
            //     vec4 p4 = fract(vec4(p.xyzx)  * vec4(.1031, .1030, .0973, .1099));
            //     p4 += dot(p4, p4.wzxy+33.33);
            //     return fract((p4.xxyz+p4.yzzw)*p4.zywx);
            // }
            val x = float4Var(fract(p.float4("xyzx") * float4Value(0.1031f, 0.1030f, 0.0973f, 0.1099f)))
            x set x + dot(x, x.float4("wzxy") + 33.33f.const)
            return@body fract((x.float4("xxyz") + x.float4("yzzw")) * x.float4("zywx"))
        }
    }

    companion object {
        const val FUNC_NAME = "noise43"
    }
}

class Noise44(parentScope: KslScopeBuilder) :
    KslFunction<KslFloat4>(FUNC_NAME, KslFloat4, parentScope.parentStage) {
    init {
        val p = paramFloat4("p")
        body {
            // vec4 hash44(vec4 p4) {
            //     p4 = fract(p4  * vec4(.1031, .1030, .0973, .1099));
            //     p4 += dot(p4, p4.wzxy+33.33);
            //     return fract((p4.xxyz+p4.yzzw)*p4.zywx);
            // }
            val x = float4Var(fract(p * float4Value(0.1031f, 0.1030f, 0.0973f, 0.1099f)))
            x set x + dot(x, x.float4("wzxy") + 33.33f.const)
            return@body fract((x.float4("xxyz") + x.float4("yzzw")) * x.float4("zywx"))
        }
    }

    companion object {
        const val FUNC_NAME = "noise44"
    }
}
