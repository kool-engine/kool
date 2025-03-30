package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.modules.ksl.lang.*
import kotlin.jvm.JvmName

/*
 * A couple of noise / hash functions, which provide pseudo random output based on arbitrary input values.
 *
 * The hash functions are based on pcg-hash:
 *   https://www.reedbeta.com/blog/hash-functions-for-gpu-rendering/
 *
 * The noise functions use the hash function to generate pseudo random bits which are then converted to floats
 * in the range 0..1 for all permutations of input channels 1..4 to output channels 1..4
 */

fun KslScopeBuilder.randomSeed(seed: KslExprUint1) {
    parentStage.globalUint1("rand_seed") set seed
}
fun KslScopeBuilder.random(): KslExprUint1 = parentStage.getOrCreateFunction("rand_1") { Random(this) }.invoke()
fun KslScopeBuilder.randomF(): KslExprFloat1 = noise11(random())

fun KslScopeBuilder.hash1(p: KslExprUint1): KslExprUint1 = parentStage.getOrCreateFunction("pcg_hash_1") { Hash1(this) }.invoke(p)
fun KslScopeBuilder.hash2(p: KslExprUint2): KslExprUint2 = parentStage.getOrCreateFunction("pcg_hash_2") { Hash2(this) }.invoke(p)
fun KslScopeBuilder.hash3(p: KslExprUint3): KslExprUint3 = parentStage.getOrCreateFunction("pcg_hash_3") { Hash3(this) }.invoke(p)
fun KslScopeBuilder.hash4(p: KslExprUint4): KslExprUint4 = parentStage.getOrCreateFunction("pcg_hash_4") { Hash4(this) }.invoke(p)

fun KslScopeBuilder.noise11(p: KslExprUint1): KslExprFloat1 = parentStage.getOrCreateFunction("noise11") { Noise11(this) }.invoke(p)
fun KslScopeBuilder.noise12(p: KslExprUint1): KslExprFloat2 = parentStage.getOrCreateFunction("noise12") { Noise12(this) }.invoke(p)
fun KslScopeBuilder.noise13(p: KslExprUint1): KslExprFloat3 = parentStage.getOrCreateFunction("noise13") { Noise13(this) }.invoke(p)
fun KslScopeBuilder.noise14(p: KslExprUint1): KslExprFloat4 = parentStage.getOrCreateFunction("noise14") { Noise14(this) }.invoke(p)

fun KslScopeBuilder.noise21(p: KslExprUint2): KslExprFloat1 = parentStage.getOrCreateFunction("noise21") { Noise21(this) }.invoke(p)
fun KslScopeBuilder.noise22(p: KslExprUint2): KslExprFloat2 = parentStage.getOrCreateFunction("noise22") { Noise22(this) }.invoke(p)
fun KslScopeBuilder.noise23(p: KslExprUint2): KslExprFloat3 = parentStage.getOrCreateFunction("noise23") { Noise23(this) }.invoke(p)
fun KslScopeBuilder.noise24(p: KslExprUint2): KslExprFloat4 = parentStage.getOrCreateFunction("noise24") { Noise24(this) }.invoke(p)

fun KslScopeBuilder.noise31(p: KslExprUint3): KslExprFloat1 = parentStage.getOrCreateFunction("noise31") { Noise31(this) }.invoke(p)
fun KslScopeBuilder.noise32(p: KslExprUint3): KslExprFloat2 = parentStage.getOrCreateFunction("noise32") { Noise32(this) }.invoke(p)
fun KslScopeBuilder.noise33(p: KslExprUint3): KslExprFloat3 = parentStage.getOrCreateFunction("noise33") { Noise33(this) }.invoke(p)
fun KslScopeBuilder.noise34(p: KslExprUint3): KslExprFloat4 = parentStage.getOrCreateFunction("noise34") { Noise34(this) }.invoke(p)

fun KslScopeBuilder.noise41(p: KslExprUint4): KslExprFloat1 = parentStage.getOrCreateFunction("noise41") { Noise41(this) }.invoke(p)
fun KslScopeBuilder.noise42(p: KslExprUint4): KslExprFloat2 = parentStage.getOrCreateFunction("noise42") { Noise42(this) }.invoke(p)
fun KslScopeBuilder.noise43(p: KslExprUint4): KslExprFloat3 = parentStage.getOrCreateFunction("noise43") { Noise43(this) }.invoke(p)
fun KslScopeBuilder.noise44(p: KslExprUint4): KslExprFloat4 = parentStage.getOrCreateFunction("noise44") { Noise44(this) }.invoke(p)

@JvmName("noise11f")
fun KslScopeBuilder.noise11(p: KslExprFloat1): KslExprFloat1 = noise11(p.toUintBits())
@JvmName("noise12f")
fun KslScopeBuilder.noise12(p: KslExprFloat1): KslExprFloat2 = noise12(p.toUintBits())
@JvmName("noise13f")
fun KslScopeBuilder.noise13(p: KslExprFloat1): KslExprFloat3 = noise13(p.toUintBits())
@JvmName("noise14f")
fun KslScopeBuilder.noise14(p: KslExprFloat1): KslExprFloat4 = noise14(p.toUintBits())

@JvmName("noise21f")
fun KslScopeBuilder.noise21(p: KslExprFloat2): KslExprFloat1 = noise21(p.toUintBits())
@JvmName("noise22f")
fun KslScopeBuilder.noise22(p: KslExprFloat2): KslExprFloat2 = noise22(p.toUintBits())
@JvmName("noise23f")
fun KslScopeBuilder.noise23(p: KslExprFloat2): KslExprFloat3 = noise23(p.toUintBits())
@JvmName("noise24f")
fun KslScopeBuilder.noise24(p: KslExprFloat2): KslExprFloat4 = noise24(p.toUintBits())

@JvmName("noise31f")
fun KslScopeBuilder.noise31(p: KslExprFloat3): KslExprFloat1 = noise31(p.toUintBits())
@JvmName("noise32f")
fun KslScopeBuilder.noise32(p: KslExprFloat3): KslExprFloat2 = noise32(p.toUintBits())
@JvmName("noise33f")
fun KslScopeBuilder.noise33(p: KslExprFloat3): KslExprFloat3 = noise33(p.toUintBits())
@JvmName("noise34f")
fun KslScopeBuilder.noise34(p: KslExprFloat3): KslExprFloat4 = noise34(p.toUintBits())

@JvmName("noise41f")
fun KslScopeBuilder.noise41(p: KslExprFloat4): KslExprFloat1 = noise41(p.toUintBits())
@JvmName("noise42f")
fun KslScopeBuilder.noise42(p: KslExprFloat4): KslExprFloat2 = noise42(p.toUintBits())
@JvmName("noise43f")
fun KslScopeBuilder.noise423(p: KslExprFloat4): KslExprFloat3 = noise43(p.toUintBits())
@JvmName("noise44f")
fun KslScopeBuilder.noise44(p: KslExprFloat4): KslExprFloat4 = noise44(p.toUintBits())

class Random(parentScope: KslScopeBuilder) : KslFunction<KslUint1>("rand_1", KslUint1, parentScope.parentStage) {
    init {
        body {
            val seed = parentStage.globalUint1("rand_seed")
            val state = uint1Var(seed * 747796405u.const + 2891336453u.const)
            val word = uint1Var(((state shr ((state shr 28u.const) + 4u.const)) xor state) * 277803737u.const)
            seed set ((word shr 22u.const) xor word)
            return@body seed
        }
    }
}

class Hash1(parentScope: KslScopeBuilder) : KslFunction<KslUint1>("pcg_hash_1", KslUint1, parentScope.parentStage) {
    init {
        val input = paramUint1()
        body {
            val state = uint1Var(input * 747796405u.const + 2891336453u.const)
            val word = uint1Var(((state shr ((state shr 28u.const) + 4u.const)) xor state) * 277803737u.const)
            return@body (word shr 22u.const) xor word
        }
    }
}

class Hash2(parentScope: KslScopeBuilder) : KslFunction<KslUint2>("pcg_hash_2", KslUint2, parentScope.parentStage) {
    init {
        val input = paramUint2()
        body {
            val state = uint2Var(input * 747796405u.const + 2891336453u.const)
            val word = uint2Var(((state shr ((state shr 28u.const) + 4u.const)) xor state) * 277803737u.const)
            return@body (word shr 22u.const) xor word
        }
    }
}

class Hash3(parentScope: KslScopeBuilder) : KslFunction<KslUint3>("pcg_hash_3", KslUint3, parentScope.parentStage) {
    init {
        val input = paramUint3()
        body {
            val state = uint3Var(input * 747796405u.const + 2891336453u.const)
            val word = uint3Var(((state shr ((state shr 28u.const) + 4u.const)) xor state) * 277803737u.const)
            return@body (word shr 22u.const) xor word
        }
    }
}

class Hash4(parentScope: KslScopeBuilder) : KslFunction<KslUint4>("pcg_hash_4", KslUint4, parentScope.parentStage) {
    init {
        val input = paramUint4()
        body {
            val state = uint4Var(input * 747796405u.const + 2891336453u.const)
            val word = uint4Var(((state shr ((state shr 28u.const) + 4u.const)) xor state) * 277803737u.const)
            return@body (word shr 22u.const) xor word
        }
    }
}

class Noise11(parentScope: KslScopeBuilder) : KslFunction<KslFloat1>("noise11", KslFloat1, parentScope.parentStage) {
    init {
        val p = paramUint1("p")
        body {
            val x = uint1Var(hash1(p))
            return@body x.toFloat1() / (UInt.MAX_VALUE.toFloat()).const
        }
    }
}

class Noise12(parentScope: KslScopeBuilder) : KslFunction<KslFloat2>("noise12", KslFloat2, parentScope.parentStage) {
    init {
        val p = paramUint1("p")
        body {
            val x = uint2Var(hash2(uint2Value(p, p * 358711u.const)))
            return@body x.toFloat2() / (UInt.MAX_VALUE.toFloat()).const
        }
    }
}

class Noise13(parentScope: KslScopeBuilder) : KslFunction<KslFloat3>("noise13", KslFloat3, parentScope.parentStage) {
    init {
        val p = paramUint1("p")
        body {
            val x = uint3Var(hash3(uint3Value(p, p * 358711u.const, p * 597367u.const)))
            return@body x.toFloat3() / (UInt.MAX_VALUE.toFloat()).const
        }
    }
}

class Noise14(parentScope: KslScopeBuilder) : KslFunction<KslFloat4>("noise14", KslFloat4, parentScope.parentStage) {
    init {
        val p = paramUint1("p")
        body {
            val x = uint4Var(hash4(uint4Value(p, p * 358711u.const, p * 597367u.const, p * 715109u.const)))
            return@body x.toFloat4() / (UInt.MAX_VALUE.toFloat()).const
        }
    }
}


class Noise21(parentScope: KslScopeBuilder) : KslFunction<KslFloat1>("noise21", KslFloat1, parentScope.parentStage) {
    init {
        val p = paramUint2("p")
        body {
            val s = uint1Var(p.x xor (p.y * 599213u.const))
            val x = uint1Var(hash1(s))
            return@body x.toFloat1() / (UInt.MAX_VALUE.toFloat()).const
        }
    }
}

class Noise22(parentScope: KslScopeBuilder) : KslFunction<KslFloat2>("noise22", KslFloat2, parentScope.parentStage) {
    init {
        val p = paramUint2("p")
        body {
            val s = uint1Var(p.x xor (p.y * 599213u.const))
            val x = uint2Var(hash2(uint2Value(s, s * 358711u.const)))
            return@body x.toFloat2() / (UInt.MAX_VALUE.toFloat()).const
        }
    }
}

class Noise23(parentScope: KslScopeBuilder) : KslFunction<KslFloat3>("noise23", KslFloat3, parentScope.parentStage) {
    init {
        val p = paramUint2("p")
        body {
            val s = uint1Var(p.x xor (p.y * 599213u.const))
            val x = uint3Var(hash3(uint3Value(s, s * 358711u.const, s * 597367u.const)))
            return@body x.toFloat3() / (UInt.MAX_VALUE.toFloat()).const
        }
    }
}

class Noise24(parentScope: KslScopeBuilder) : KslFunction<KslFloat4>("noise24", KslFloat4, parentScope.parentStage) {
    init {
        val p = paramUint2("p")
        body {
            val s = uint1Var(p.x xor (p.y * 599213u.const))
            val x = uint4Var(hash4(uint4Value(s, s * 358711u.const, s * 597367u.const, s * 715109u.const)))
            return@body x.toFloat4() / (UInt.MAX_VALUE.toFloat()).const
        }
    }
}


class Noise31(parentScope: KslScopeBuilder) : KslFunction<KslFloat1>("noise31", KslFloat1, parentScope.parentStage) {
    init {
        val p = paramUint3("p")
        body {
            val s = uint1Var(p.x xor (p.y * 599213u.const) xor (p.z * 987251u.const))
            val x = uint1Var(hash1(s))
            return@body x.toFloat1() / (UInt.MAX_VALUE.toFloat()).const
        }
    }
}

class Noise32(parentScope: KslScopeBuilder) : KslFunction<KslFloat2>("noise32", KslFloat2, parentScope.parentStage) {
    init {
        val p = paramUint3("p")
        body {
            val s = uint1Var(p.x xor (p.y * 599213u.const) xor (p.z * 987251u.const))
            val x = uint2Var(hash2(uint2Value(s, s * 358711u.const)))
            return@body x.toFloat2() / (UInt.MAX_VALUE.toFloat()).const
        }
    }
}

class Noise33(parentScope: KslScopeBuilder) : KslFunction<KslFloat3>("noise33", KslFloat3, parentScope.parentStage) {
    init {
        val p = paramUint3("p")
        body {
            val s = uint1Var(p.x xor (p.y * 599213u.const) xor (p.z * 987251u.const))
            val x = uint3Var(hash3(uint3Value(s, s * 358711u.const, s * 597367u.const)))
            return@body x.toFloat3() / (UInt.MAX_VALUE.toFloat()).const
        }
    }
}

class Noise34(parentScope: KslScopeBuilder) : KslFunction<KslFloat4>("noise34", KslFloat4, parentScope.parentStage) {
    init {
        val p = paramUint3("p")
        body {
            val s = uint1Var(p.x xor (p.y * 599213u.const) xor (p.z * 987251u.const))
            val x = uint4Var(hash4(uint4Value(s, s * 358711u.const, s * 597367u.const, s * 715109u.const)))
            return@body x.toFloat4() / (UInt.MAX_VALUE.toFloat()).const
        }
    }
}


class Noise41(parentScope: KslScopeBuilder) : KslFunction<KslFloat1>("noise41", KslFloat1, parentScope.parentStage) {
    init {
        val p = paramUint4("p")
        body {
            val s = uint1Var(p.x xor (p.y * 599213u.const) xor (p.z * 987251u.const) xor (p.w * 279137u.const))
            val x = uint1Var(hash1(s))
            return@body x.toFloat1() / (UInt.MAX_VALUE.toFloat()).const
        }
    }
}

class Noise42(parentScope: KslScopeBuilder) : KslFunction<KslFloat2>("noise42", KslFloat2, parentScope.parentStage) {
    init {
        val p = paramUint4("p")
        body {
            val s = uint1Var(p.x xor (p.y * 599213u.const) xor (p.z * 987251u.const) xor (p.w * 279137u.const))
            val x = uint2Var(hash2(uint2Value(s, s * 358711u.const)))
            return@body x.toFloat2() / (UInt.MAX_VALUE.toFloat()).const
        }
    }
}

class Noise43(parentScope: KslScopeBuilder) : KslFunction<KslFloat3>("noise43", KslFloat3, parentScope.parentStage) {
    init {
        val p = paramUint4("p")
        body {
            val s = uint1Var(p.x xor (p.y * 599213u.const) xor (p.z * 987251u.const) xor (p.w * 279137u.const))
            val x = uint3Var(hash3(uint3Value(s, s * 358711u.const, s * 597367u.const)))
            return@body x.toFloat3() / (UInt.MAX_VALUE.toFloat()).const
        }
    }
}

class Noise44(parentScope: KslScopeBuilder) : KslFunction<KslFloat4>("noise44", KslFloat4, parentScope.parentStage) {
    init {
        val p = paramUint4("p")
        body {
            val s = uint1Var(p.x xor (p.y * 599213u.const) xor (p.z * 987251u.const) xor (p.w * 279137u.const))
            val x = uint4Var(hash4(uint4Value(s, s * 358711u.const, s * 597367u.const, s * 715109u.const)))
            return@body x.toFloat4() / (UInt.MAX_VALUE.toFloat()).const
        }
    }
}
