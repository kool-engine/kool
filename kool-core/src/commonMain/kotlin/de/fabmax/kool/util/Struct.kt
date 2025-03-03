package de.fabmax.kool.util

import de.fabmax.kool.math.*
import de.fabmax.kool.pipeline.GpuType

abstract class Struct<T: Struct<T>>(val layout: BufferLayout) {
    val members = mutableListOf<StructMember>()

    private var layoutPos = 0
    val structSize: Int get() = layout.structSize(layoutPos)

    private var _bufferAccess: StructBufferAccess? = null
    val bufferAccess: StructBufferAccess get() = checkNotNull(_bufferAccess) {
        "Buffer access only works if StructDefinition is used in an StructBuffer context"
    }
    private val buffer: MixedBuffer get() = bufferAccess.structBuffer.buffer
    private val bufferPosition: Int get() = bufferAccess.bufferPosition

    protected fun float1(name: String = "f1_${members.size}"): Float1Member {
        val (offset, size) = layout.offsetAndSizeOf(layoutPos, GpuType.Float1, 1)
        layoutPos = offset + size
        return Float1Member(name, offset).also { members.add(it) }
    }

    protected fun float2(name: String = "f2_${members.size}"): Float2Member {
        val (offset, size) = layout.offsetAndSizeOf(layoutPos, GpuType.Float2, 1)
        layoutPos = offset + size
        return Float2Member(name, offset).also { members.add(it) }
    }

    protected fun float3(name: String = "f3_${members.size}"): Float3Member {
        val (offset, size) = layout.offsetAndSizeOf(layoutPos, GpuType.Float3, 1)
        layoutPos = offset + size
        return Float3Member(name, offset).also { members.add(it) }
    }

    protected fun float4(name: String = "f4_${members.size}"): Float4Member {
        val (offset, size) = layout.offsetAndSizeOf(layoutPos, GpuType.Float4, 1)
        layoutPos = offset + size
        return Float4Member(name, offset).also { members.add(it) }
    }

    protected fun <S: Struct<S>> struct(name: String = "nestedStruct_${members.size}", structProvider: () -> S): NestedStructMember<S> {
        val nested = structProvider()
        val (offset, size) = layout.offsetAndSizeOf(layoutPos, GpuType.Struct(name, nested.structSize), 1)
        layoutPos = offset + size
        return NestedStructMember<S>(name, offset, structProvider).also { members.add(it) }
    }

    internal fun setupBufferAccess(access: StructBufferAccess) {
        _bufferAccess = access
    }

    fun getLayoutString(indent: String = "") = buildString {
        members.forEach {
            val name = "${it.name}:".padEnd(20)
            appendLine("$indent$name${it.type.toString().padEnd(12)}    0x${it.byteOffset.toString(16).padStart(4, '0')} (${it.type.byteSize})")
            if (it is NestedStructMember<*>) {
                append(it.getLayoutString("    $indent"))
            }
        }
    }

    interface StructMember {
        val name: String
        val type: GpuType
        val byteOffset: Int
    }

    inner class Float1Member(override val name: String, override val byteOffset: Int) : StructMember {
        override val type = GpuType.Float1
        var buf: Float
            get() = buffer.getFloat32(bufferPosition + byteOffset)
            set(value) { buffer.setFloat32(bufferPosition + byteOffset, value) }
    }

    inner class Float2Member(override val name: String, override val byteOffset: Int) : StructMember {
        override val type = GpuType.Float2
        var buf: Vec2f
            get() = getFromBuffer(MutableVec2f())
            set(value) {
                val offset = bufferPosition + byteOffset
                buffer.setFloat32(offset +  0, value.x)
                buffer.setFloat32(offset +  4, value.y)
            }

        fun getFromBuffer(result: MutableVec2f): MutableVec2f {
            val offset = bufferPosition + byteOffset
            return result.set(
                buffer.getFloat32(offset +  0),
                buffer.getFloat32(offset +  4)
            )
        }
    }

    inner class Float3Member(override val name: String, override val byteOffset: Int) : StructMember {
        override val type = GpuType.Float3
        var buf: Vec3f
            get() = getFromBuffer(MutableVec3f())
            set(value) {
                val offset = bufferPosition + byteOffset
                buffer.setFloat32(offset +  0, value.x)
                buffer.setFloat32(offset +  4, value.y)
                buffer.setFloat32(offset +  8, value.z)
            }

        fun getFromBuffer(result: MutableVec3f): MutableVec3f {
            val offset = bufferPosition + byteOffset
            return result.set(
                buffer.getFloat32(offset +  0),
                buffer.getFloat32(offset +  4),
                buffer.getFloat32(offset +  8)
            )
        }
    }

    inner class Float4Member(override val name: String, override val byteOffset: Int) : StructMember {
        override val type = GpuType.Float4
        var buf: Vec4f
            get() = getFromBuffer(MutableVec4f())
            set(value) {
                val offset = bufferPosition + byteOffset
                buffer.setFloat32(offset +  0, value.x)
                buffer.setFloat32(offset +  4, value.y)
                buffer.setFloat32(offset +  8, value.z)
                buffer.setFloat32(offset + 12, value.w)
            }

        fun getFromBuffer(result: MutableVec4f): MutableVec4f {
            val offset = bufferPosition + byteOffset
            return result.set(
                buffer.getFloat32(offset +  0),
                buffer.getFloat32(offset +  4),
                buffer.getFloat32(offset +  8),
                buffer.getFloat32(offset + 12),
            )
        }
    }

    inner class NestedStructMember<S: Struct<S>>(override val name: String, override val byteOffset: Int, structProvider: () -> S) : StructMember {
        internal val accessor = structProvider()
        private var nestedBufferAccess: StructBufferAccess? = null

        override val type = GpuType.Struct(name, accessor.structSize)

        val buf: S
            get() {
                if (nestedBufferAccess == null) {
                    val acc = StructBufferAccessNested(bufferAccess, byteOffset)
                    nestedBufferAccess = acc
                    accessor.setupBufferAccess(acc)
                }
                return accessor
            }

        init {
            require(accessor.layout == layout) {
                "Nested struct must have the same layout as the parent struct (but nested layout is ${accessor.layout} and parent layout is $layout)"
            }
        }

        fun getLayoutString(indent: String): String = accessor.getLayoutString(indent)
    }
}

interface StructBufferAccess {
    val structBuffer: StructBuffer<*>
    val bufferPosition: Int
}

class StructBufferAccessIndexed(override val structBuffer: StructBuffer<*>, var index: Int = 0) : StructBufferAccess {
    override val bufferPosition: Int get() = index * structBuffer.strideBytes
}

class StructBufferAccessNested(val parent: StructBufferAccess, val byteOffset: Int) : StructBufferAccess {
    override val structBuffer: StructBuffer<*> get() = parent.structBuffer
    override val bufferPosition: Int get() = parent.bufferPosition + byteOffset
}

