package de.fabmax.kool.util.gltf

import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.MutableVec4f
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
data class Accessor(
        val bufferView: Int = -1,
        val byteOffset: Int = 0,
        val componentType: Int,
        val normalized: Boolean = false,
        val count: Int,
        val type: String,
        val max: List<Float>? = null,
        val min: List<Float>? = null,
        //val sparse: Sparse? = null,
        val name: String? = null
) {
    @Transient
    lateinit var bufferViewRef: BufferView

    companion object {
        const val TYPE_SCALAR = "SCALAR"
        const val TYPE_VEC2 = "VEC2"
        const val TYPE_VEC3 = "VEC3"
        const val TYPE_VEC4 = "VEC4"

        const val COMP_TYPE_BYTE = 5120
        const val COMP_TYPE_UNSIGNED_BYTE = 5121
        const val COMP_TYPE_SHORT = 5122
        const val COMP_TYPE_UNSIGNED_SHORT = 5123
        const val COMP_TYPE_INT = 5124
        const val COMP_TYPE_UNSIGNED_INT = 5125
        const val COMP_TYPE_FLOAT = 5126
    }
}

/**
 * Utility class to retrieve scalar integer values from an accessor. The provided accessor must have a non floating
 * point component type (byte, short or int either signed or unsigned) and must be of type SCALAR.
 *
 * @param accessor [Accessor] to use.
 */
class IntAccessor(val accessor: Accessor) {
    private val stream = DataStream(accessor.bufferViewRef.bufferRef.data, accessor.byteOffset + accessor.bufferViewRef.byteOffset)

    private val elemSize: Int = when (accessor.componentType) {
        Accessor.COMP_TYPE_BYTE -> 1
        Accessor.COMP_TYPE_UNSIGNED_BYTE -> 1
        Accessor.COMP_TYPE_SHORT -> 2
        Accessor.COMP_TYPE_UNSIGNED_SHORT -> 2
        Accessor.COMP_TYPE_INT -> 4
        Accessor.COMP_TYPE_UNSIGNED_INT -> 4
        else -> throw IllegalArgumentException("Provided accessor does not have integer component type")
    }

    var index: Int
        get() = stream.index / elemSize
        set(value) {
            stream.index = value * elemSize
        }

    init {
        if (accessor.type != Accessor.TYPE_SCALAR) {
            throw IllegalArgumentException("IntAccessor requires accessor type ${Accessor.TYPE_SCALAR}, provided was ${accessor.type}")
        }
    }

    fun next(): Int {
        if (index < accessor.count) {
            return when (accessor.componentType) {
                Accessor.COMP_TYPE_BYTE -> stream.readByte()
                Accessor.COMP_TYPE_UNSIGNED_BYTE -> stream.readUByte()
                Accessor.COMP_TYPE_SHORT -> stream.readShort()
                Accessor.COMP_TYPE_UNSIGNED_SHORT -> stream.readUShort()
                Accessor.COMP_TYPE_INT -> stream.readInt()
                Accessor.COMP_TYPE_UNSIGNED_INT -> stream.readUInt()
                else -> 0
            }
        } else {
            throw IndexOutOfBoundsException("Accessor overflow")
        }
    }
}

/**
 * Utility class to retrieve scalar float values from an accessor. The provided accessor must have a float component
 * type and must be of type SCALAR.
 *
 * @param accessor [Accessor] to use.
 */
class FloatAccessor(val accessor: Accessor) {
    private val stream = DataStream(accessor.bufferViewRef.bufferRef.data, accessor.byteOffset + accessor.bufferViewRef.byteOffset)

    private val elemSize: Int = when (accessor.componentType) {
        Accessor.COMP_TYPE_FLOAT -> 4
        else -> throw IllegalArgumentException("Provided accessor does not have float component type")
    }

    var index: Int
        get() = stream.index / elemSize
        set(value) {
            stream.index = value * elemSize
        }

    init {
        if (accessor.type != Accessor.TYPE_SCALAR) {
            throw IllegalArgumentException("Vec2fAccessor requires accessor type ${Accessor.TYPE_SCALAR}, provided was ${accessor.type}")
        }
    }

    fun next(): Float {
        if (index < accessor.count) {
            return stream.readFloat()
        } else {
            throw IndexOutOfBoundsException("Accessor overflow")
        }
    }
}

/**
 * Utility class to retrieve Vec2 float values from an accessor. The provided accessor must have a float component type
 * and must be of type VEC2.
 *
 * @param accessor [Accessor] to use.
 */
class Vec2fAccessor(val accessor: Accessor) {
    private val stream = DataStream(accessor.bufferViewRef.bufferRef.data, accessor.byteOffset + accessor.bufferViewRef.byteOffset)

    private val elemSize: Int = when (accessor.componentType) {
        Accessor.COMP_TYPE_FLOAT -> 4 * 2
        else -> throw IllegalArgumentException("Provided accessor does not have float component type")
    }

    var index: Int
        get() = stream.index / elemSize
        set(value) {
            stream.index = value * elemSize
        }

    init {
        if (accessor.type != Accessor.TYPE_VEC2) {
            throw IllegalArgumentException("Vec2fAccessor requires accessor type ${Accessor.TYPE_VEC2}, provided was ${accessor.type}")
        }
    }

    fun next(): MutableVec2f = next(MutableVec2f())

    fun next(result: MutableVec2f): MutableVec2f {
        if (index < accessor.count) {
            result.x = stream.readFloat()
            result.y = stream.readFloat()
        } else {
            throw IndexOutOfBoundsException("Accessor overflow")
        }
        return result
    }
}

/**
 * Utility class to retrieve Vec3 float values from an accessor. The provided accessor must have a float component type
 * and must be of type VEC3.
 *
 * @param accessor [Accessor] to use.
 */
class Vec3fAccessor(val accessor: Accessor) {
    private val stream = DataStream(accessor.bufferViewRef.bufferRef.data, accessor.byteOffset + accessor.bufferViewRef.byteOffset)

    private val elemSize: Int = when (accessor.componentType) {
        Accessor.COMP_TYPE_FLOAT -> 4 * 3
        else -> throw IllegalArgumentException("Provided accessor does not have float component type")
    }

    var index: Int
        get() = stream.index / elemSize
        set(value) {
            stream.index = value * elemSize
        }

    init {
        if (accessor.type != Accessor.TYPE_VEC3) {
            throw IllegalArgumentException("Vec3fAccessor requires accessor type ${Accessor.TYPE_VEC3}, provided was ${accessor.type}")
        }
    }

    fun next(): MutableVec3f = next(MutableVec3f())

    fun next(result: MutableVec3f): MutableVec3f {
        if (index < accessor.count) {
            result.x = stream.readFloat()
            result.y = stream.readFloat()
            result.z = stream.readFloat()
        } else {
            throw IndexOutOfBoundsException("Accessor overflow")
        }
        return result
    }
}

/**
 * Utility class to retrieve Vec4 float values from an accessor. The provided accessor must have a float component type
 * and must be of type VEC4.
 *
 * @param accessor [Accessor] to use.
 */
class Vec4fAccessor(val accessor: Accessor) {
    private val stream = DataStream(accessor.bufferViewRef.bufferRef.data, accessor.byteOffset + accessor.bufferViewRef.byteOffset)

    private val elemSize: Int = when (accessor.componentType) {
        Accessor.COMP_TYPE_FLOAT -> 4 * 4
        else -> throw IllegalArgumentException("Provided accessor does not have float component type")
    }

    var index: Int
        get() = stream.index / elemSize
        set(value) {
            stream.index = value * elemSize
        }

    init {
        if (accessor.type != Accessor.TYPE_VEC4) {
            throw IllegalArgumentException("Vec3fAccessor requires accessor type ${Accessor.TYPE_VEC4}, provided was ${accessor.type}")
        }
    }

    fun next(): MutableVec4f = next(MutableVec4f())

    fun next(result: MutableVec4f): MutableVec4f {
        if (index < accessor.count) {
            result.x = stream.readFloat()
            result.y = stream.readFloat()
            result.z = stream.readFloat()
            result.w = stream.readFloat()
        } else {
            throw IndexOutOfBoundsException("Accessor overflow")
        }
        return result
    }
}
