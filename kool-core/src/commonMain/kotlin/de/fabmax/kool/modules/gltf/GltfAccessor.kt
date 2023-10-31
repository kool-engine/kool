package de.fabmax.kool.modules.gltf

import de.fabmax.kool.math.*
import de.fabmax.kool.util.DataStream
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * A typed view into a bufferView. A bufferView contains raw binary data. An accessor provides a typed view into a
 * bufferView or a subset of a bufferView similar to how WebGL's vertexAttribPointer() defines an attribute in a
 * buffer.
 *
 * @param bufferView    The index of the bufferView.
 * @param byteOffset    The offset relative to the start of the bufferView in bytes.
 * @param componentType The datatype of components in the attribute.
 * @param normalized    Specifies whether integer data values should be normalized.
 * @param count         The number of attributes referenced by this accessor.
 * @param type          Specifies if the attribute is a scalar, vector, or matrix.
 * @param max           Maximum value of each component in this attribute.
 * @param min           Minimum value of each component in this attribute.
 */
@Serializable
data class GltfAccessor(
    val bufferView: Int = -1,
    val byteOffset: Int = 0,
    val componentType: Int,
    val normalized: Boolean = false,
    val count: Int,
    val type: String,
    val max: List<Float>? = null,
    val min: List<Float>? = null,
    val sparse: Sparse? = null,
    val name: String? = null
) {
    @Transient
    var bufferViewRef: GltfBufferView? = null

    companion object {
        const val TYPE_SCALAR = "SCALAR"
        const val TYPE_VEC2 = "VEC2"
        const val TYPE_VEC3 = "VEC3"
        const val TYPE_VEC4 = "VEC4"
        const val TYPE_MAT2 = "MAT2"
        const val TYPE_MAT3 = "MAT3"
        const val TYPE_MAT4 = "MAT4"

        const val COMP_TYPE_BYTE = 5120
        const val COMP_TYPE_UNSIGNED_BYTE = 5121
        const val COMP_TYPE_SHORT = 5122
        const val COMP_TYPE_UNSIGNED_SHORT = 5123
        const val COMP_TYPE_INT = 5124
        const val COMP_TYPE_UNSIGNED_INT = 5125
        const val COMP_TYPE_FLOAT = 5126

        val COMP_INT_TYPES = setOf(
                COMP_TYPE_BYTE, COMP_TYPE_UNSIGNED_BYTE,
                COMP_TYPE_SHORT, COMP_TYPE_UNSIGNED_SHORT,
                COMP_TYPE_INT, COMP_TYPE_UNSIGNED_INT)
    }

    @Serializable
    data class Sparse(
            val count: Int,
            val indices: SparseIndices,
            val values: SparseValues
    )

    @Serializable
    data class SparseIndices(
            val bufferView: Int,
            val byteOffset: Int = 0,
            val componentType: Int
    ) {
        @Transient
        lateinit var bufferViewRef: GltfBufferView
    }

    @Serializable
    data class SparseValues(
            val bufferView: Int,
            val byteOffset: Int = 0
    ) {
        @Transient
        lateinit var bufferViewRef: GltfBufferView
    }
}

abstract class DataStreamAccessor(val accessor: GltfAccessor) {
    private val elemByteSize: Int
    private val byteStride: Int

    private val buffer: GltfBufferView? = accessor.bufferViewRef
    private val stream: DataStream?

    private val sparseIndexStream: DataStream?
    private val sparseValueStream: DataStream?
    private val sparseIndexType: Int
    private var nextSparseIndex: Int

    var index: Int = 0
        set(value) {
            field = value
            stream?.index = value * byteStride
        }

    init {
        stream = if (buffer != null) {
            DataStream(buffer.bufferRef.data, accessor.byteOffset + buffer.byteOffset)
        } else {
            null
        }

        if (accessor.sparse != null) {
            sparseIndexStream = DataStream(accessor.sparse.indices.bufferViewRef.bufferRef.data, accessor.sparse.indices.bufferViewRef.byteOffset)
            sparseValueStream = DataStream(accessor.sparse.values.bufferViewRef.bufferRef.data, accessor.sparse.values.bufferViewRef.byteOffset)
            sparseIndexType = accessor.sparse.indices.componentType
            nextSparseIndex = sparseIndexStream.nextIntComponent(sparseIndexType)
        } else {
            sparseIndexStream = null
            sparseValueStream = null
            sparseIndexType = 0
            nextSparseIndex = -1
        }

        val compByteSize = when (accessor.componentType) {
            GltfAccessor.COMP_TYPE_BYTE -> 1
            GltfAccessor.COMP_TYPE_UNSIGNED_BYTE -> 1
            GltfAccessor.COMP_TYPE_SHORT -> 2
            GltfAccessor.COMP_TYPE_UNSIGNED_SHORT -> 2
            GltfAccessor.COMP_TYPE_INT -> 4
            GltfAccessor.COMP_TYPE_UNSIGNED_INT -> 4
            GltfAccessor.COMP_TYPE_FLOAT -> 4
            else -> throw IllegalArgumentException("Unknown accessor component type: ${accessor.componentType}")
        }
        val numComponents = when(accessor.type) {
            GltfAccessor.TYPE_SCALAR -> 1
            GltfAccessor.TYPE_VEC2 -> 2
            GltfAccessor.TYPE_VEC3 -> 3
            GltfAccessor.TYPE_VEC4 -> 4
            GltfAccessor.TYPE_MAT2 -> 4
            GltfAccessor.TYPE_MAT3 -> 9
            GltfAccessor.TYPE_MAT4 -> 16
            else -> throw IllegalArgumentException("Unsupported accessor type: ${accessor.type}")
        }
        // fixme: some mat types require padding (also depending on component type) which is currently not considered
        elemByteSize = compByteSize * numComponents
        byteStride = if (buffer != null && buffer.byteStride > 0) {
            buffer.byteStride
        } else {
            elemByteSize
        }
    }

    private fun selectDataStream() = if (index != nextSparseIndex) stream else sparseValueStream

    protected fun nextInt(): Int {
        if (index < accessor.count) {
            return selectDataStream()?.nextIntComponent(accessor.componentType) ?: 0
        } else {
            throw IndexOutOfBoundsException("Accessor overflow")
        }
    }

    protected fun nextFloat(): Float {
        if (accessor.componentType == GltfAccessor.COMP_TYPE_FLOAT) {
            if (index < accessor.count) {
                return selectDataStream()?.readFloat() ?: 0f
            } else {
                throw IndexOutOfBoundsException("Accessor overflow")
            }
        } else {
            // implicitly convert int type to normalized float
            return nextInt() / when (accessor.componentType) {
                GltfAccessor.COMP_TYPE_BYTE -> 128f
                GltfAccessor.COMP_TYPE_UNSIGNED_BYTE -> 255f
                GltfAccessor.COMP_TYPE_SHORT -> 32767f
                GltfAccessor.COMP_TYPE_UNSIGNED_SHORT -> 65535f
                GltfAccessor.COMP_TYPE_INT -> 2147483647f
                GltfAccessor.COMP_TYPE_UNSIGNED_INT -> 4294967296f
                else -> throw IllegalStateException("Unknown component type: ${accessor.componentType}")
            }
        }
    }

    protected fun nextDouble() = nextFloat().toDouble()

    private fun DataStream.nextIntComponent(componentType: Int): Int {
        return when (componentType) {
            GltfAccessor.COMP_TYPE_BYTE -> readByte()
            GltfAccessor.COMP_TYPE_UNSIGNED_BYTE -> readUByte()
            GltfAccessor.COMP_TYPE_SHORT -> readShort()
            GltfAccessor.COMP_TYPE_UNSIGNED_SHORT -> readUShort()
            GltfAccessor.COMP_TYPE_INT -> readInt()
            GltfAccessor.COMP_TYPE_UNSIGNED_INT -> readUInt()
            else -> throw IllegalArgumentException("Invalid component type: $componentType")
        }
    }

    protected fun advance() {
        if (index == nextSparseIndex && sparseIndexStream?.hasRemaining() == true) {
            nextSparseIndex = sparseIndexStream.nextIntComponent(sparseIndexType)
        }
        index++
    }
}

/**
 * Utility class to retrieve scalar integer values from an accessor. The provided accessor must have a non floating
 * point component type (byte, short or int either signed or unsigned) and must be of type SCALAR.
 *
 * @param accessor [GltfAccessor] to use.
 */
class IntAccessor(accessor: GltfAccessor) : DataStreamAccessor(accessor) {
    init {
        if (accessor.type != GltfAccessor.TYPE_SCALAR) {
            throw IllegalArgumentException("IntAccessor requires accessor type ${GltfAccessor.TYPE_SCALAR}, provided was ${accessor.type}")
        }
        if (accessor.componentType !in GltfAccessor.COMP_INT_TYPES) {
            throw IllegalArgumentException("IntAccessor requires a (byte / short / int) component type, provided was ${accessor.componentType}")
        }
    }

    fun next(): Int {
        if (index < accessor.count) {
            val i = nextInt()
            advance()
            return i
        } else {
            throw IndexOutOfBoundsException("Accessor overflow")
        }
    }
}

/**
 * Utility class to retrieve scalar float values from an accessor. The provided accessor must have a float component
 * type and must be of type SCALAR.
 *
 * @param accessor [GltfAccessor] to use.
 */
class FloatAccessor(accessor: GltfAccessor) : DataStreamAccessor(accessor) {
    init {
        if (accessor.type != GltfAccessor.TYPE_SCALAR) {
            throw IllegalArgumentException("Vec2fAccessor requires accessor type ${GltfAccessor.TYPE_SCALAR}, provided was ${accessor.type}")
        }
//        if (accessor.componentType != GltfAccessor.COMP_TYPE_FLOAT) {
//            throw IllegalArgumentException("FloatAccessor requires a float component type, provided was ${accessor.componentType}")
//        }
    }

    fun next(): Float {
        val f = nextFloat()
        advance()
        return f
    }

    fun nextD() = next().toDouble()
}

/**
 * Utility class to retrieve Vec2 float values from an accessor. The provided accessor must have a float component type
 * and must be of type VEC2.
 *
 * @param accessor [GltfAccessor] to use.
 */
class Vec2fAccessor(accessor: GltfAccessor) : DataStreamAccessor(accessor) {
    init {
        if (accessor.type != GltfAccessor.TYPE_VEC2) {
            throw IllegalArgumentException("Vec2fAccessor requires accessor type ${GltfAccessor.TYPE_VEC2}, provided was ${accessor.type}")
        }
//        if (accessor.componentType != GltfAccessor.COMP_TYPE_FLOAT) {
//            throw IllegalArgumentException("Vec2fAccessor requires a float component type, provided was ${accessor.componentType}")
//        }
    }

    fun next(): MutableVec2f = next(MutableVec2f())

    fun next(result: MutableVec2f): MutableVec2f {
        result.x = nextFloat()
        result.y = nextFloat()
        advance()
        return result
    }

    fun nextD(): MutableVec2d = nextD(MutableVec2d())

    fun nextD(result: MutableVec2d): MutableVec2d {
        result.x = nextDouble()
        result.y = nextDouble()
        advance()
        return result
    }
}

/**
 * Utility class to retrieve Vec3 float values from an accessor. The provided accessor must have a float component type
 * and must be of type VEC3.
 *
 * @param accessor [GltfAccessor] to use.
 */
class Vec3fAccessor(accessor: GltfAccessor) : DataStreamAccessor(accessor) {
    init {
        if (accessor.type != GltfAccessor.TYPE_VEC3) {
            throw IllegalArgumentException("Vec3fAccessor requires accessor type ${GltfAccessor.TYPE_VEC3}, provided was ${accessor.type}")
        }
//        if (accessor.componentType != GltfAccessor.COMP_TYPE_FLOAT) {
//            throw IllegalArgumentException("Vec3fAccessor requires a float component type, provided was ${accessor.componentType}")
//        }
    }

    fun next(): MutableVec3f = next(MutableVec3f())

    fun next(result: MutableVec3f): MutableVec3f {
        result.x = nextFloat()
        result.y = nextFloat()
        result.z = nextFloat()
        advance()
        return result
    }

    fun nextD(): MutableVec3d = nextD(MutableVec3d())

    fun nextD(result: MutableVec3d): MutableVec3d {
        result.x = nextDouble()
        result.y = nextDouble()
        result.z = nextDouble()
        advance()
        return result
    }
}

/**
 * Utility class to retrieve Vec4 float values from an accessor. The provided accessor must have a float component type
 * and must be of type VEC4.
 *
 * @param accessor [GltfAccessor] to use.
 */
class Vec4fAccessor(accessor: GltfAccessor) : DataStreamAccessor(accessor) {
    init {
        if (accessor.type != GltfAccessor.TYPE_VEC4) {
            throw IllegalArgumentException("Vec4fAccessor requires accessor type ${GltfAccessor.TYPE_VEC4}, provided was ${accessor.type}")
        }
//        if (accessor.componentType != GltfAccessor.COMP_TYPE_FLOAT) {
//            throw IllegalArgumentException("Vec4fAccessor requires a float component type, provided was ${accessor.componentType}")
//        }
    }

    fun next(): MutableVec4f = next(MutableVec4f())

    fun next(result: MutableVec4f): MutableVec4f {
        result.x = nextFloat()
        result.y = nextFloat()
        result.z = nextFloat()
        result.w = nextFloat()
        advance()
        return result
    }

    fun nextD(): MutableVec4d = nextD(MutableVec4d())

    fun nextD(result: MutableVec4d): MutableVec4d {
        result.x = nextDouble()
        result.y = nextDouble()
        result.z = nextDouble()
        result.w = nextDouble()
        advance()
        return result
    }
}

/**
 * Utility class to retrieve Vec4 int values from an accessor. The provided accessor must have a non floating
 * point component type (byte, short or int either signed or unsigned) and must be of type VEC4.
 *
 * @param accessor [GltfAccessor] to use.
 */
class Vec4iAccessor(accessor: GltfAccessor) : DataStreamAccessor(accessor) {
    init {
        if (accessor.type != GltfAccessor.TYPE_VEC4) {
            throw IllegalArgumentException("Vec4iAccessor requires accessor type ${GltfAccessor.TYPE_VEC4}, provided was ${accessor.type}")
        }
        if (accessor.componentType !in GltfAccessor.COMP_INT_TYPES) {
            throw IllegalArgumentException("Vec4fAccessor requires a (byte / short / int) component type, provided was ${accessor.componentType}")
        }
    }

    fun next(): MutableVec4i = next(MutableVec4i())

    fun next(result: MutableVec4i): MutableVec4i {
        result.x = nextInt()
        result.y = nextInt()
        result.z = nextInt()
        result.w = nextInt()
        advance()
        return result
    }
}


/**
 * Utility class to retrieve Mat4 float values from an accessor. The provided accessor must have a float component type
 * and must be of type MAT4.
 *
 * @param accessor [GltfAccessor] to use.
 */
class Mat4fAccessor(accessor: GltfAccessor) : DataStreamAccessor(accessor) {
    init {
        if (accessor.type != GltfAccessor.TYPE_MAT4) {
            throw IllegalArgumentException("Mat4fAccessor requires accessor type ${GltfAccessor.TYPE_MAT4}, provided was ${accessor.type}")
        }
        if (accessor.componentType != GltfAccessor.COMP_TYPE_FLOAT) {
            throw IllegalArgumentException("Mat4fAccessor requires a float component type, provided was ${accessor.componentType}")
        }
    }

    fun next(): Mat4f = next(MutableMat4f())

    fun next(result: MutableMat4f): Mat4f {
        for (col in 0..3) {
            for (row in 0..3) {
                result[row, col] = nextFloat()
            }
        }
        advance()
        return result
    }

    fun nextD(): Mat4d = nextD(MutableMat4d())

    fun nextD(result: MutableMat4d): Mat4d {
        for (col in 0..3) {
            for (row in 0..3) {
                result[row, col] = nextDouble()
            }
        }
        advance()
        return result
    }
}