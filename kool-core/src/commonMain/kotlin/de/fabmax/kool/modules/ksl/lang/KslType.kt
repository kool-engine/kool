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
interface KslSampler2dArrayType
interface KslSamplerCubeArrayType

object KslColorSampler1d : KslColorSampler<KslFloat1>("sampler1d"), KslSampler1dType
object KslColorSampler2d : KslColorSampler<KslFloat2>("sampler2d"), KslSampler2dType
object KslColorSampler3d : KslColorSampler<KslFloat3>("sampler3d"), KslSampler3dType
object KslColorSamplerCube : KslColorSampler<KslFloat3>("samplerCube"), KslSamplerCubeType
object KslColorSampler2dArray : KslColorSampler<KslFloat3>("sampler2dArray"), KslSampler2dArrayType
object KslColorSamplerCubeArray : KslColorSampler<KslFloat4>("samplerCubeArray"), KslSamplerCubeArrayType

object KslDepthSampler2D : KslDepthSampler<KslFloat3>("depthSampler2d"), KslSampler2dType
object KslDepthSamplerCube : KslDepthSampler<KslFloat4>("depthSamplerCube"), KslSamplerCubeType
object KslDepthSampler2DArray : KslDepthSampler<KslFloat4>("depthSampler2dArray"), KslSampler2dArrayType
object KslDepthSamplerCubeArray : KslDepthSampler<KslFloat4>("depthSamplerCubeArray"), KslSamplerCubeArrayType


sealed class KslStorage1dType<R: KslNumericType>(typeName: String, elemType: R) : KslStorageType<R, KslInt1>(typeName, elemType, KslInt1)
sealed class KslStorage2dType<R: KslNumericType>(typeName: String, elemType: R) : KslStorageType<R, KslInt2>(typeName, elemType, KslInt2)
sealed class KslStorage3dType<R: KslNumericType>(typeName: String, elemType: R) : KslStorageType<R, KslInt3>(typeName, elemType, KslInt3)

object KslStorage1dFloat1 : KslStorage1dType<KslFloat1>("float1", KslFloat1)
object KslStorage1dFloat2 : KslStorage1dType<KslFloat2>("float2", KslFloat2)
object KslStorage1dFloat3 : KslStorage1dType<KslFloat3>("float3", KslFloat3)
object KslStorage1dFloat4 : KslStorage1dType<KslFloat4>("float4", KslFloat4)

object KslStorage1dInt1 : KslStorage1dType<KslInt1>("int1", KslInt1)
object KslStorage1dInt2 : KslStorage1dType<KslInt2>("int2", KslInt2)
object KslStorage1dInt3 : KslStorage1dType<KslInt3>("int3", KslInt3)
object KslStorage1dInt4 : KslStorage1dType<KslInt4>("int4", KslInt4)

object KslStorage1dUint1 : KslStorage1dType<KslUint1>("uint1", KslUint1)
object KslStorage1dUint2 : KslStorage1dType<KslUint2>("uint2", KslUint2)
object KslStorage1dUint3 : KslStorage1dType<KslUint3>("uint3", KslUint3)
object KslStorage1dUint4 : KslStorage1dType<KslUint4>("uint4", KslUint4)
