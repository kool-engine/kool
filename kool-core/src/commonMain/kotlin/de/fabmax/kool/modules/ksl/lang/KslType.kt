package de.fabmax.kool.modules.ksl.lang

sealed class KslType(val typeName: String) {
    override fun toString(): String = typeName
}

object KslTypeVoid : KslType("void")

sealed class KslNumericType(typeName: String) : KslType(typeName)
sealed class KslFloatType(typeName: String) : KslNumericType(typeName)
sealed class KslIntType(typeName: String) : KslNumericType(typeName)
sealed class KslBoolType(typeName: String) : KslType(typeName)
sealed class KslTypeSampler<R: KslNumericType>(typeName: String) : KslType(typeName)

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

object KslTypeFloat1 : KslFloatType("float1"), KslScalar
object KslTypeFloat2 : KslFloatType("float2"), KslVector2<KslTypeFloat1>
object KslTypeFloat3 : KslFloatType("float3"), KslVector3<KslTypeFloat1>
object KslTypeFloat4 : KslFloatType("float4"), KslVector4<KslTypeFloat1>

object KslTypeInt1 : KslIntType("int1"), KslScalar
object KslTypeInt2 : KslIntType("int2"), KslVector2<KslTypeInt1>
object KslTypeInt3 : KslIntType("int3"), KslVector3<KslTypeInt1>
object KslTypeInt4 : KslIntType("int4"), KslVector4<KslTypeInt1>

object KslTypeUint1 : KslIntType("uint1"), KslScalar
object KslTypeUint2 : KslIntType("uint2"), KslVector2<KslTypeUint1>
object KslTypeUint3 : KslIntType("uint3"), KslVector3<KslTypeUint1>
object KslTypeUint4 : KslIntType("uint4"), KslVector4<KslTypeUint1>

object KslTypeBool1 : KslBoolType("bool1"), KslScalar
object KslTypeBool2 : KslBoolType("bool2"), KslVector2<KslTypeBool1>
object KslTypeBool3 : KslBoolType("bool3"), KslVector3<KslTypeBool1>
object KslTypeBool4 : KslBoolType("bool4"), KslVector4<KslTypeBool1>

object KslTypeMat2 : KslFloatType("mat2"), KslMatrix<KslTypeFloat2>
object KslTypeMat3 : KslFloatType("mat3"), KslMatrix<KslTypeFloat3>
object KslTypeMat4 : KslFloatType("mat4"), KslMatrix<KslTypeFloat4>

open class KslTypeArray<T: KslType>(val elemType: T, val arraySize: Int) : KslType("array<${elemType.typeName}>[$arraySize]") {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KslTypeArray<*>) return false
        return elemType == other.elemType && arraySize == other.arraySize
    }

    override fun hashCode(): Int {
        return 31 * elemType.hashCode() + arraySize
    }
}

class KslTypeFloat1Array(arraySize: Int) : KslTypeArray<KslTypeFloat1>(KslTypeFloat1, arraySize)
class KslTypeFloat2Array(arraySize: Int) : KslTypeArray<KslTypeFloat2>(KslTypeFloat2, arraySize)
class KslTypeFloat3Array(arraySize: Int) : KslTypeArray<KslTypeFloat3>(KslTypeFloat3, arraySize)
class KslTypeFloat4Array(arraySize: Int) : KslTypeArray<KslTypeFloat4>(KslTypeFloat4, arraySize)

class KslTypeInt1Array(arraySize: Int) : KslTypeArray<KslTypeInt1>(KslTypeInt1, arraySize)
class KslTypeInt2Array(arraySize: Int) : KslTypeArray<KslTypeInt2>(KslTypeInt2, arraySize)
class KslTypeInt3Array(arraySize: Int) : KslTypeArray<KslTypeInt3>(KslTypeInt3, arraySize)
class KslTypeInt4Array(arraySize: Int) : KslTypeArray<KslTypeInt4>(KslTypeInt4, arraySize)

class KslTypeUint1Array(arraySize: Int) : KslTypeArray<KslTypeUint1>(KslTypeUint1, arraySize)
class KslTypeUint2Array(arraySize: Int) : KslTypeArray<KslTypeUint2>(KslTypeUint2, arraySize)
class KslTypeUint3Array(arraySize: Int) : KslTypeArray<KslTypeUint3>(KslTypeUint3, arraySize)
class KslTypeUint4Array(arraySize: Int) : KslTypeArray<KslTypeUint4>(KslTypeUint4, arraySize)

class KslTypeBool1Array(arraySize: Int) : KslTypeArray<KslTypeBool1>(KslTypeBool1, arraySize)
class KslTypeBool2Array(arraySize: Int) : KslTypeArray<KslTypeBool2>(KslTypeBool2, arraySize)
class KslTypeBool3Array(arraySize: Int) : KslTypeArray<KslTypeBool3>(KslTypeBool3, arraySize)
class KslTypeBool4Array(arraySize: Int) : KslTypeArray<KslTypeBool4>(KslTypeBool4, arraySize)

class KslTypeMat2Array(arraySize: Int) : KslTypeArray<KslTypeMat2>(KslTypeMat2, arraySize)
class KslTypeMat3Array(arraySize: Int) : KslTypeArray<KslTypeMat3>(KslTypeMat3, arraySize)
class KslTypeMat4Array(arraySize: Int) : KslTypeArray<KslTypeMat4>(KslTypeMat4, arraySize)

sealed class KslTypeColorSampler<C: KslFloatType>(typeName: String) : KslTypeSampler<KslTypeFloat4>(typeName)
sealed class KslTypeDepthSampler<C: KslFloatType>(typeName: String) : KslTypeSampler<KslTypeFloat1>(typeName)

interface KslTypeSampler1d
interface KslTypeSampler2d
interface KslTypeSampler3d
interface KslTypeSamplerCube
interface KslTypeSampler2dArray
interface KslTypeSamplerCubeArray

object KslTypeColorSampler1d : KslTypeColorSampler<KslTypeFloat1>("sampler1d"), KslTypeSampler1d
object KslTypeColorSampler2d : KslTypeColorSampler<KslTypeFloat2>("sampler2d"), KslTypeSampler2d
object KslTypeColorSampler3d : KslTypeColorSampler<KslTypeFloat3>("sampler3d"), KslTypeSampler3d
object KslTypeColorSamplerCube : KslTypeColorSampler<KslTypeFloat3>("samplerCube"), KslTypeSamplerCube
object KslTypeColorSampler2dArray : KslTypeColorSampler<KslTypeFloat3>("sampler2dArray"), KslTypeSampler2dArray
object KslTypeColorSamplerCubeArray : KslTypeColorSampler<KslTypeFloat4>("samplerCubeArray"), KslTypeSamplerCubeArray

object KslTypeDepthSampler2d : KslTypeDepthSampler<KslTypeFloat3>("depthSampler2d"), KslTypeSampler2d
object KslTypeDepthSamplerCube : KslTypeDepthSampler<KslTypeFloat4>("depthSamplerCube"), KslTypeSamplerCube
object KslTypeDepthSampler2dArray : KslTypeDepthSampler<KslTypeFloat4>("depthSampler2dArray"), KslTypeSampler2dArray
object KslTypeDepthSamplerCubeArray : KslTypeDepthSampler<KslTypeFloat4>("depthSamplerCubeArray"), KslTypeSamplerCubeArray
