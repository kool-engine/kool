package de.fabmax.kool.scene.geometry

import de.fabmax.kool.math.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.asAttribute
import de.fabmax.kool.util.*

class VertexView<T: Struct>(val data: IndexedVertexList<T>, var index: Int) : MutableVec3f() {
    // standard attributes for easy access
    val position: MutableVec3f
    val normal: MutableVec3f
    val tangent: MutableVec4f
    val color: MutableColor
    val texCoord: MutableVec2f
    val joints: MutableVec4i
    val weights: MutableVec4f
    val emissiveColor: MutableVec3f
    val metallicRoughness: MutableVec2f

    private val attributeViews: Map<Attribute, Any>
    private val colorViews: Map<Attribute, ColorWrapView>

    init {
        val colViews = mutableMapOf<Attribute, ColorWrapView>()
        val attribViews = mutableMapOf<Attribute, Any>()
        attributeViews = attribViews
        colorViews = colViews

        for (member in data.layout.members) {
            @Suppress("UNCHECKED_CAST")
            when (member) {
                is Float1Member<*> -> attribViews[member.asAttribute()] = FloatView(member as Float1Member<T>)
                is Float2Member<*> -> attribViews[member.asAttribute()] = Vec2fView(member as Float2Member<T>)
                is Float3Member<*> -> attribViews[member.asAttribute()] = Vec3fView(member as Float3Member<T>)
                is Float4Member<*> -> {
                    val view = Vec4fView(member as Float4Member<T>)
                    attribViews[member.asAttribute()] = view
                    colViews[member.asAttribute()] = ColorWrapView(view)
                }
                is Int1Member<*> -> attribViews[member.asAttribute()] = IntView(member as Int1Member<T>)
                is Int2Member<*> -> attribViews[member.asAttribute()] = Vec2iView(member as Int2Member<T>)
                is Int3Member<*> -> attribViews[member.asAttribute()] = Vec3iView(member as Int3Member<T>)
                is Int4Member<*> -> attribViews[member.asAttribute()] = Vec4iView(member as Int4Member<T>)
                else -> { }
            }
        }

        position = getVec3fAttribute(Attribute.POSITIONS) ?: Vec3fView(null)
        normal = getVec3fAttribute(Attribute.NORMALS) ?: Vec3fView(null)
        tangent = getVec4fAttribute(Attribute.TANGENTS) ?: Vec4fView(null)
        texCoord = getVec2fAttribute(Attribute.TEXTURE_COORDS) ?: Vec2fView(null)
        color = getColorAttribute(Attribute.COLORS) ?: ColorWrapView(Vec4fView(null))
        joints = getVec4iAttribute(Attribute.JOINTS) ?: Vec4iView(null)
        weights = getVec4fAttribute(Attribute.WEIGHTS) ?: Vec4fView(null)
        emissiveColor = getVec3fAttribute(Attribute.EMISSIVE_COLOR) ?: Vec3fView(null)
        metallicRoughness = getVec2fAttribute(Attribute.METAL_ROUGH) ?: Vec2fView(null)
    }

    override var x: Float
        get() = position.x
        set(value) { position.x = value }
    override var y: Float
        get() = position.y
        set(value) { position.y = value }
    override var z: Float
        get() = position.z
        set(value) { position.z = value }

    fun setEmissiveColor(emissiveColor: Color) {
        this.emissiveColor.set(emissiveColor.r, emissiveColor.g, emissiveColor.b)
    }

    fun setMetallic(metallicFactor: Float) {
        metallicRoughness.x = metallicFactor
    }

    fun setRoughness(roughnessFactor: Float) {
        metallicRoughness.y = roughnessFactor
    }

    fun set(other: VertexView<*>) {
        for (attrib in attributeViews.keys) {
            val dst = attributeViews[attrib]
            when (val src = other.attributeViews[attrib]) {
                is VertexView<*>.FloatView -> (dst as VertexView<*>.FloatView).f = src.f
                is VertexView<*>.Vec2fView -> (dst as VertexView<*>.Vec2fView).set(src)
                is VertexView<*>.Vec3fView -> (dst as VertexView<*>.Vec3fView).set(src)
                is VertexView<*>.Vec4fView -> (dst as VertexView<*>.Vec4fView).set(src)
                is VertexView<*>.IntView -> (dst as VertexView<*>.IntView).i = src.i
                is VertexView<*>.Vec2iView -> (dst as VertexView<*>.Vec2iView).set(src)
                is VertexView<*>.Vec3iView -> (dst as VertexView<*>.Vec3iView).set(src)
                is VertexView<*>.Vec4iView -> (dst as VertexView<*>.Vec4iView).set(src)
            }
        }
    }

    fun getFloatAttribute(attribute: Attribute): FloatView? = attributeViews[attribute] as VertexView<T>.FloatView?
    fun getVec2fAttribute(attribute: Attribute): MutableVec2f? = attributeViews[attribute] as MutableVec2f?
    fun getVec3fAttribute(attribute: Attribute): MutableVec3f? = attributeViews[attribute] as MutableVec3f?
    fun getVec4fAttribute(attribute: Attribute): MutableVec4f? = attributeViews[attribute] as MutableVec4f?
    fun getIntAttribute(attribute: Attribute): IntView? = attributeViews[attribute] as VertexView<T>.IntView?
    fun getVec2iAttribute(attribute: Attribute): MutableVec2i? = attributeViews[attribute] as MutableVec2i?
    fun getVec3iAttribute(attribute: Attribute): MutableVec3i? = attributeViews[attribute] as MutableVec3i?
    fun getVec4iAttribute(attribute: Attribute): MutableVec4i? = attributeViews[attribute] as MutableVec4i?

    fun getColorAttribute(attribute: Attribute): MutableColor? = colorViews[attribute]

    inner class FloatView(private val member: Float1Member<T>?) {
        var f: Float
            get() = member?.let { data.vertexData.get(index) { get(member) } } ?: 0f
            set(value) {
                member?.let { data.vertexData.set(index) { set(member, value) } }
            }
    }

    private inner class Vec2fView(val member: Float2Member<T>?) : MutableVec2f() {
        private val cache = MutableVec2f()
        override var x: Float
            get() = read().x
            set(value) { cache.x = value; member?.let { write(it) } }
        override var y: Float
            get() = read().y
            set(value) { cache.y = value; member?.let { write(it) } }

        override fun set(x: Float, y: Float): MutableVec2f {
            if (member != null) {
                cache.set(x, y)
                write(member)
            }
            return this
        }

        private fun read() = member?.let { data.vertexData.get(index) { get(member, cache) } } ?: ZERO
        private fun write(m: Float2Member<T>) {
            data.vertexData.set(index) { set(m, cache) }
        }
    }

    private inner class Vec3fView(val member: Float3Member<T>?) : MutableVec3f() {
        private val cache = MutableVec3f()
        override var x: Float
            get() = read().x
            set(value) { cache.x = value; member?.let { write(it) } }
        override var y: Float
            get() = read().y
            set(value) { cache.y = value; member?.let { write(it) } }
        override var z: Float
            get() = read().z
            set(value) { cache.z = value; member?.let { write(it) } }

        override fun set(x: Float, y: Float, z: Float): MutableVec3f {
            if (member != null) {
                cache.set(x, y, z)
                write(member)
            }
            return this
        }

        private fun read() = member?.let { data.vertexData.get(index) { get(member, cache) } } ?: ZERO
        private fun write(m: Float3Member<T>) {
            data.vertexData.set(index) { set(m, cache) }
        }
    }

    private inner class Vec4fView(val member: Float4Member<T>?) : MutableVec4f() {
        private val cache = MutableVec4f()
        override var x: Float
            get() = read().x
            set(value) { cache.x = value; member?.let { write(it) } }
        override var y: Float
            get() = read().y
            set(value) { cache.y = value; member?.let { write(it) } }
        override var z: Float
            get() = read().z
            set(value) { cache.z = value; member?.let { write(it) } }
        override var w: Float
            get() = read().w
            set(value) { cache.w = value; member?.let { write(it) } }

        override fun set(x: Float, y: Float, z: Float, w: Float): MutableVec4f {
            if (member != null) {
                cache.set(x, y, z, w)
                write(member)
            }
            return this
        }

        private fun read() = member?.let { data.vertexData.get(index) { get(member, cache) } } ?: ZERO
        private fun write(m: Float4Member<T>) {
            data.vertexData.set(index) { set(m, cache) }
        }
    }

    private inner class ColorWrapView(val vecView: Vec4fView) : MutableColor() {
        override var r
            get() = vecView.x
            set(value) { vecView.x = value }
        override var g
            get() = vecView.y
            set(value) { vecView.y = value }
        override var b
            get() = vecView.z
            set(value) { vecView.z = value }
        override var a
            get() = vecView.w
            set(value) { vecView.w = value }

        override fun set(r: Float, g: Float, b: Float, a: Float): MutableColor {
            vecView.set(r, g, b, a)
            return this
        }
    }

    inner class IntView(private val member: Int1Member<T>?) {
        var i: Int
            get() = member?.let { data.vertexData.get(index) { get(member) } } ?: 0
            set(value) {
                member?.let { data.vertexData.set(index) { set(member, value) } }
            }
    }

    private inner class Vec2iView(val member: Int2Member<T>?) : MutableVec2i() {
        private val cache = MutableVec2i()
        override var x: Int
            get() = read().x
            set(value) { cache.x = value; member?.let { write(it) } }
        override var y: Int
            get() = read().y
            set(value) { cache.y = value; member?.let { write(it) } }

        override fun set(x: Int, y: Int): MutableVec2i {
            if (member != null) {
                cache.set(x, y)
                write(member)
            }
            return this
        }

        private fun read() = member?.let { data.vertexData.get(index) { get(member, cache) } } ?: ZERO
        private fun write(m: Int2Member<T>) {
            data.vertexData.set(index) { set(m, cache) }
        }
    }

    private inner class Vec3iView(val member: Int3Member<T>?) : MutableVec3i() {
        private val cache = MutableVec3i()
        override var x: Int
            get() = read().x
            set(value) { cache.x = value; member?.let { write(it) } }
        override var y: Int
            get() = read().y
            set(value) { cache.y = value; member?.let { write(it) } }
        override var z: Int
            get() = read().z
            set(value) { cache.y = value; member?.let { write(it) } }

        override fun set(x: Int, y: Int, z: Int): MutableVec3i {
            if (member != null) {
                cache.set(x, y, z)
                write(member)
            }
            return this
        }

        private fun read() = member?.let { data.vertexData.get(index) { get(member, cache) } } ?: ZERO
        private fun write(m: Int3Member<T>) {
            data.vertexData.set(index) { set(m, cache) }
        }
    }

    private inner class Vec4iView(val member: Int4Member<T>?) : MutableVec4i() {
        private val cache = MutableVec4i()
        override var x: Int
            get() = read().x
            set(value) { cache.x = value; member?.let { write(it) } }
        override var y: Int
            get() = read().y
            set(value) { cache.y = value; member?.let { write(it) } }
        override var z: Int
            get() = read().z
            set(value) { cache.y = value; member?.let { write(it) } }
        override var w: Int
            get() = read().w
            set(value) { cache.w = value; member?.let { write(it) } }

        override fun set(x: Int, y: Int, z: Int, w: Int): MutableVec4i {
            if (member != null) {
                cache.set(x, y, z, w)
                write(member)
            }
            return this
        }

        private fun read() = member?.let { data.vertexData.get(index) { get(member, cache) } } ?: ZERO
        private fun write(m: Int4Member<T>) {
            data.vertexData.set(index) { set(m, cache) }
        }
    }
}