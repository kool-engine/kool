package de.fabmax.kool.modules.ksl.lang

sealed class KslType(val typeName: String) {
    override fun toString(): String = typeName
}

object KslTypeVoid : KslType("void")

sealed class KslNumericType(typeName: String) : KslType(typeName)
sealed class KslFloatType(typeName: String) : KslNumericType(typeName)
sealed class KslIntType(typeName: String) : KslNumericType(typeName)
sealed class KslBoolType(typeName: String) : KslType(typeName)
sealed class KslSamplerType<R: KslNumericType>(typeName: String) : KslType(typeName)
sealed class KslStorageType<R: KslNumericType, C: KslIntType>(typeName: String, val elemType: R, val coordType: C) : KslType(typeName)

interface KslScalar

interface KslVector<S: KslScalar> {
    val dimens: Int
}

interface KslVector2<S: KslScalar> : KslVector<S> {
    override val dimens: Int
        get() = 2
}

interface KslVector3<S: KslScalar> : KslVector<S> {
    override val dimens: Int
        get() = 3
}

interface KslVector4<S: KslScalar> : KslVector<S> {
    override val dimens: Int
        get() = 4
}

interface KslMatrix<V: KslVector<*>>

object KslFloat1 : KslFloatType("float1"), KslScalar
object KslFloat2 : KslFloatType("float2"), KslVector2<KslFloat1>
object KslFloat3 : KslFloatType("float3"), KslVector3<KslFloat1>
object KslFloat4 : KslFloatType("float4"), KslVector4<KslFloat1>

object KslInt1 : KslIntType("int1"), KslScalar
object KslInt2 : KslIntType("int2"), KslVector2<KslInt1>
object KslInt3 : KslIntType("int3"), KslVector3<KslInt1>
object KslInt4 : KslIntType("int4"), KslVector4<KslInt1>

object KslUint1 : KslIntType("uint1"), KslScalar
object KslUint2 : KslIntType("uint2"), KslVector2<KslUint1>
object KslUint3 : KslIntType("uint3"), KslVector3<KslUint1>
object KslUint4 : KslIntType("uint4"), KslVector4<KslUint1>

object KslBool1 : KslBoolType("bool1"), KslScalar
object KslBool2 : KslBoolType("bool2"), KslVector2<KslBool1>
object KslBool3 : KslBoolType("bool3"), KslVector3<KslBool1>
object KslBool4 : KslBoolType("bool4"), KslVector4<KslBool1>

object KslMat2 : KslFloatType("mat2"), KslMatrix<KslFloat2>
object KslMat3 : KslFloatType("mat3"), KslMatrix<KslFloat3>
object KslMat4 : KslFloatType("mat4"), KslMatrix<KslFloat4>

open class KslArrayType<T: KslType>(val elemType: T, val arraySize: Int) : KslType("array<${elemType.typeName}>[$arraySize]") {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KslArrayType<*>) return false
        return elemType == other.elemType && arraySize == other.arraySize
    }

    override fun hashCode(): Int {
        return 31 * elemType.hashCode() + arraySize
    }
}

class KslFloat1Array(arraySize: Int) : KslArrayType<KslFloat1>(KslFloat1, arraySize)
class KslFloat2Array(arraySize: Int) : KslArrayType<KslFloat2>(KslFloat2, arraySize)
class KslFloat3Array(arraySize: Int) : KslArrayType<KslFloat3>(KslFloat3, arraySize)
class KslFloat4Array(arraySize: Int) : KslArrayType<KslFloat4>(KslFloat4, arraySize)

class KslInt1Array(arraySize: Int) : KslArrayType<KslInt1>(KslInt1, arraySize)
class KslInt2Array(arraySize: Int) : KslArrayType<KslInt2>(KslInt2, arraySize)
class KslInt3Array(arraySize: Int) : KslArrayType<KslInt3>(KslInt3, arraySize)
class KslInt4Array(arraySize: Int) : KslArrayType<KslInt4>(KslInt4, arraySize)

class KslUint1Array(arraySize: Int) : KslArrayType<KslUint1>(KslUint1, arraySize)
class KslUint2Array(arraySize: Int) : KslArrayType<KslUint2>(KslUint2, arraySize)
class KslUint3Array(arraySize: Int) : KslArrayType<KslUint3>(KslUint3, arraySize)
class KslUint4Array(arraySize: Int) : KslArrayType<KslUint4>(KslUint4, arraySize)

class KslBool1Array(arraySize: Int) : KslArrayType<KslBool1>(KslBool1, arraySize)
class KslBool2Array(arraySize: Int) : KslArrayType<KslBool2>(KslBool2, arraySize)
class KslBool3Array(arraySize: Int) : KslArrayType<KslBool3>(KslBool3, arraySize)
class KslBool4Array(arraySize: Int) : KslArrayType<KslBool4>(KslBool4, arraySize)

class KslMat2Array(arraySize: Int) : KslArrayType<KslMat2>(KslMat2, arraySize)
class KslMat3Array(arraySize: Int) : KslArrayType<KslMat3>(KslMat3, arraySize)
class KslMat4Array(arraySize: Int) : KslArrayType<KslMat4>(KslMat4, arraySize)

sealed class KslColorSampler<C: KslFloatType>(typeName: String) : KslSamplerType<KslFloat4>(typeName)
sealed class KslDepthSampler<C: KslFloatType>(typeName: String) : KslSamplerType<KslFloat1>(typeName)

interface KslSampler1dType
interface KslSampler2dType
interface KslSampler3dType
interface KslSamplerCubeType
interface KslSamplerArrayType

object KslColorSampler1d : KslColorSampler<KslFloat1>("sampler1d"), KslSampler1dType
object KslColorSampler2d : KslColorSampler<KslFloat2>("sampler2d"), KslSampler2dType
object KslColorSampler3d : KslColorSampler<KslFloat3>("sampler3d"), KslSampler3dType
object KslColorSamplerCube : KslColorSampler<KslFloat3>("samplerCube"), KslSamplerCubeType
object KslColorSampler2dArray : KslColorSampler<KslFloat2>("sampler2dArray"), KslSampler2dType, KslSamplerArrayType
object KslColorSamplerCubeArray : KslColorSampler<KslFloat3>("samplerCubeArray"), KslSamplerCubeType, KslSamplerArrayType

object KslDepthSampler2d : KslDepthSampler<KslFloat2>("depthSampler2d"), KslSampler2dType
object KslDepthSamplerCube : KslDepthSampler<KslFloat3>("depthSamplerCube"), KslSamplerCubeType
object KslDepthSampler2dArray : KslDepthSampler<KslFloat2>("depthSampler2dArray"), KslSampler2dType, KslSamplerArrayType
object KslDepthSamplerCubeArray : KslDepthSampler<KslFloat3>("depthSamplerCubeArray"), KslSamplerCubeType, KslSamplerArrayType


class KslStorage1dType<R: KslNumericType>(elemType: R) : KslStorageType<R, KslInt1>("KslStorage1dType<${elemType.typeName}>", elemType, KslInt1) {
    override fun hashCode(): Int = this::class.hashCode() * 31 + elemType.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is KslStorage1dType<*>) return false
        return elemType == other.elemType
    }
}

class KslStorage2dType<R: KslNumericType>(elemType: R) : KslStorageType<R, KslInt2>("KslStorage2dType<${elemType.typeName}>", elemType, KslInt2) {
    override fun hashCode(): Int = this::class.hashCode() * 31 + elemType.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is KslStorage2dType<*>) return false
        return elemType == other.elemType
    }
}

class KslStorage3dType<R: KslNumericType>(elemType: R) : KslStorageType<R, KslInt3>("KslStorage3dType<${elemType.typeName}>", elemType, KslInt3) {
    override fun hashCode(): Int = this::class.hashCode() * 31 + elemType.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is KslStorage3dType<*>) return false
        return elemType == other.elemType
    }
}

inline fun <reified T: KslNumericType> numericTypeForT(): T {
    // cast is needed if K2 compiler is used
    @Suppress("USELESS_CAST")
    return when {
        KslFloat1 is T -> KslFloat1
        KslFloat2 is T -> KslFloat2
        KslFloat3 is T -> KslFloat3
        KslFloat4 is T -> KslFloat4

        KslInt1 is T   -> KslInt1
        KslInt2 is T   -> KslInt2
        KslInt3 is T   -> KslInt3
        KslInt4 is T   -> KslInt4

        KslUint1 is T  -> KslUint1
        KslUint2 is T  -> KslUint2
        KslUint3 is T  -> KslUint3
        KslUint4 is T  -> KslUint4
        else -> error("Unsupported storage type")
    } as T
}
