package de.fabmax.kool.util.gltf

import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.MutableVec4f
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Accessor(
        val bufferView: Int,
        val componentType: Int,
        val count: Int,
        val type: String,
        val byteOffset: Int = 0,
        val min: List<Float>? = null,
        val max: List<Float>? = null
) {
    @Transient
    lateinit var bufferViewRef: BufferView
}

class IntAccessor(val accessor: Accessor) {
    private val stream = DataStream(accessor.bufferViewRef.bufferRef.data, accessor.byteOffset + accessor.bufferViewRef.byteOffset)

    private val elemSize: Int = when (accessor.componentType) {
        GltfFile.COMP_TYPE_BYTE -> 1
        GltfFile.COMP_TYPE_UNSIGNED_BYTE -> 1
        GltfFile.COMP_TYPE_SHORT -> 2
        GltfFile.COMP_TYPE_UNSIGNED_SHORT -> 2
        GltfFile.COMP_TYPE_INT -> 4
        GltfFile.COMP_TYPE_UNSIGNED_INT -> 4
        else -> throw IllegalArgumentException("Provided accessor does not have integer component type")
    }

    var index: Int
        get() = stream.index / elemSize
        set(value) {
            stream.index = value * elemSize
        }

    init {
        if (accessor.type != GltfFile.ACCESSOR_TYPE_SCALAR) {
            throw IllegalArgumentException("IntAccessor requires accessor type ${GltfFile.ACCESSOR_TYPE_SCALAR}, provided was ${accessor.type}")
        }
    }

    fun next(): Int {
        return if (index < accessor.count) {
            when (accessor.componentType) {
                GltfFile.COMP_TYPE_BYTE -> stream.readByte()
                GltfFile.COMP_TYPE_UNSIGNED_BYTE -> stream.readUByte()
                GltfFile.COMP_TYPE_SHORT -> stream.readShort()
                GltfFile.COMP_TYPE_UNSIGNED_SHORT -> stream.readUShort()
                GltfFile.COMP_TYPE_INT -> stream.readInt()
                GltfFile.COMP_TYPE_UNSIGNED_INT -> stream.readUInt()
                else -> 0
            }

        } else {
            throw IndexOutOfBoundsException("Accessor overflow")
        }
    }
}

class Vec2fAccessor(val accessor: Accessor) {
    private val stream = DataStream(accessor.bufferViewRef.bufferRef.data, accessor.byteOffset + accessor.bufferViewRef.byteOffset)

    private val elemSize: Int = when (accessor.componentType) {
        GltfFile.COMP_TYPE_FLOAT -> 4 * 2
        else -> throw IllegalArgumentException("Provided accessor does not have float component type")
    }

    var index: Int
        get() = stream.index / elemSize
        set(value) {
            stream.index = value * elemSize
        }

    init {
        if (accessor.type != GltfFile.ACCESSOR_TYPE_VEC2) {
            throw IllegalArgumentException("Vec2fAccessor requires accessor type ${GltfFile.ACCESSOR_TYPE_VEC2}, provided was ${accessor.type}")
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

class Vec3fAccessor(val accessor: Accessor) {
    private val stream = DataStream(accessor.bufferViewRef.bufferRef.data, accessor.byteOffset + accessor.bufferViewRef.byteOffset)

    private val elemSize: Int = when (accessor.componentType) {
        GltfFile.COMP_TYPE_FLOAT -> 4 * 3
        else -> throw IllegalArgumentException("Provided accessor does not have float component type")
    }

    var index: Int
        get() = stream.index / elemSize
        set(value) {
            stream.index = value * elemSize
        }

    init {
        if (accessor.type != GltfFile.ACCESSOR_TYPE_VEC3) {
            throw IllegalArgumentException("Vec3fAccessor requires accessor type ${GltfFile.ACCESSOR_TYPE_VEC3}, provided was ${accessor.type}")
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

class Vec4fAccessor(val accessor: Accessor) {
    private val stream = DataStream(accessor.bufferViewRef.bufferRef.data, accessor.byteOffset + accessor.bufferViewRef.byteOffset)

    private val elemSize: Int = when (accessor.componentType) {
        GltfFile.COMP_TYPE_FLOAT -> 4 * 4
        else -> throw IllegalArgumentException("Provided accessor does not have float component type")
    }

    var index: Int
        get() = stream.index / elemSize
        set(value) {
            stream.index = value * elemSize
        }

    init {
        if (accessor.type != GltfFile.ACCESSOR_TYPE_VEC4) {
            throw IllegalArgumentException("Vec3fAccessor requires accessor type ${GltfFile.ACCESSOR_TYPE_VEC4}, provided was ${accessor.type}")
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
