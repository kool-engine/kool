package de.fabmax.kool.pipeline

import de.fabmax.kool.modules.ksl.lang.*

/**
 * @author fabmax
 */

sealed interface GpuType {
    val byteSize: Int

    data object Float1 : GpuType { override val byteSize = 4 }
    data object Float2 : GpuType { override val byteSize = 8 }
    data object Float3 : GpuType { override val byteSize = 12 }
    data object Float4 : GpuType { override val byteSize = 16 }

    data object Int1 : GpuType { override val byteSize = 4 }
    data object Int2 : GpuType { override val byteSize = 8 }
    data object Int3 : GpuType { override val byteSize = 12 }
    data object Int4 : GpuType { override val byteSize = 16 }

    data object Uint1 : GpuType { override val byteSize = 4 }
    data object Uint2 : GpuType { override val byteSize = 8 }
    data object Uint3 : GpuType { override val byteSize = 12 }
    data object Uint4 : GpuType { override val byteSize = 16 }

    data object Bool1 : GpuType { override val byteSize = 4 }
    data object Bool2 : GpuType { override val byteSize = 8 }
    data object Bool3 : GpuType { override val byteSize = 12 }
    data object Bool4 : GpuType { override val byteSize = 16 }

    data object Mat2 : GpuType { override val byteSize = 32 }
    data object Mat3 : GpuType { override val byteSize = 48 }
    data object Mat4 : GpuType { override val byteSize = 64 }

    class Struct(val struct: de.fabmax.kool.util.Struct) : GpuType {
        override val byteSize: Int get() = struct.structSize
        override fun toString(): String = "Struct<${struct.structName}>"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Struct
            return struct == other.struct
        }

        override fun hashCode(): Int {
            return struct.hashCode()
        }
    }
}

val GpuType.isFloat: Boolean
    get() = this == GpuType.Float1 ||
        this == GpuType.Float2 ||
        this == GpuType.Float3 ||
        this == GpuType.Float4 ||
        this == GpuType.Mat2 ||
        this == GpuType.Mat3 ||
        this == GpuType.Mat4

val GpuType.isInt: Boolean
    get() = this == GpuType.Int1 ||
            this == GpuType.Int2 ||
            this == GpuType.Int3 ||
            this == GpuType.Int4

val GpuType.isUint: Boolean
    get() = this == GpuType.Uint1 ||
            this == GpuType.Uint2 ||
            this == GpuType.Uint3 ||
            this == GpuType.Uint4

val GpuType.isBool: Boolean
    get() = this == GpuType.Bool1 ||
            this == GpuType.Bool2 ||
            this == GpuType.Bool3 ||
            this == GpuType.Bool4

val KslType.gpuType: GpuType
    get() = when (this) {
        KslFloat1 -> GpuType.Float1
        KslFloat2 -> GpuType.Float2
        KslFloat3 -> GpuType.Float3
        KslFloat4 -> GpuType.Float4

        KslInt1 -> GpuType.Int1
        KslInt2 -> GpuType.Int2
        KslInt3 -> GpuType.Int3
        KslInt4 -> GpuType.Int4

        KslUint1 -> GpuType.Uint1
        KslUint2 -> GpuType.Uint2
        KslUint3 -> GpuType.Uint3
        KslUint4 -> GpuType.Uint4

        KslBool1 -> GpuType.Bool1
        KslBool2 -> GpuType.Bool2
        KslBool3 -> GpuType.Bool3
        KslBool4 -> GpuType.Bool4

        KslMat2 -> GpuType.Mat2
        KslMat3 -> GpuType.Mat3
        KslMat4 -> GpuType.Mat4

        is KslStruct<*> -> proto.type

        else -> throw IllegalArgumentException("KslType has no corresponding glsl type: $this")
    }
