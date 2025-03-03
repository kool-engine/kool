package de.fabmax.kool.scene.geometry

import de.fabmax.kool.math.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.GpuType
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MutableColor

class VertexView(val data: IndexedVertexList, index: Int) : MutableVec3f() {
    private var offsetF = index * data.vertexSizeF
    private var offsetI = index * data.vertexSizeI

    /**
     * Vertex index in the underlying vertex list. Can be set to navigate within the vertex list
     */
    var index = index
        set(value) {
            field = value
            offsetF = value * data.vertexSizeF
            offsetI = value * data.vertexSizeI
        }

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

    init {
        val attribViews = mutableMapOf<Attribute, Any>()
        attributeViews = attribViews

        for (offset in data.attributeByteOffsets) {
            when (offset.key.type) {
                GpuType.Float1 -> attribViews[offset.key] = FloatView(offset.value / 4)
                GpuType.Float2 -> attribViews[offset.key] = Vec2fView(offset.value / 4)
                GpuType.Float3 -> attribViews[offset.key] = Vec3fView(offset.value / 4)
                GpuType.Float4 -> attribViews[offset.key] = Vec4fView(offset.value / 4)
                GpuType.Int1 -> attribViews[offset.key] = IntView(offset.value / 4)
                GpuType.Int2 -> attribViews[offset.key] = Vec2iView(offset.value / 4)
                GpuType.Int3 -> attribViews[offset.key] = Vec3iView(offset.value / 4)
                GpuType.Int4 -> attribViews[offset.key] = Vec4iView(offset.value / 4)
                else -> throw IllegalArgumentException("${offset.key.type} is not a valid vertex attribute")
            }
        }

        position = getVec3fAttribute(Attribute.POSITIONS) ?: Vec3fView(-1)
        normal = getVec3fAttribute(Attribute.NORMALS) ?: Vec3fView(-1)
        tangent = getVec4fAttribute(Attribute.TANGENTS) ?: Vec4fView(-1)
        texCoord = getVec2fAttribute(Attribute.TEXTURE_COORDS) ?: Vec2fView(-1)
        color = getColorAttribute(Attribute.COLORS) ?: ColorWrapView(Vec4fView(-1))
        joints = getVec4iAttribute(Attribute.JOINTS) ?: Vec4iView(-1)
        weights = getVec4fAttribute(Attribute.WEIGHTS) ?: Vec4fView(-1)
        emissiveColor = getVec3fAttribute(Attribute.EMISSIVE_COLOR) ?: Vec3fView(-1)
        metallicRoughness = getVec2fAttribute(Attribute.METAL_ROUGH) ?: Vec2fView(-1)
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

    fun set(other: VertexView) {
        for (attrib in attributeViews.keys) {
            val view = other.attributeViews[attrib]
            if (view != null) {
                when (view) {
                    is FloatView -> (attributeViews[attrib] as FloatView).f = view.f
                    is Vec2fView -> (attributeViews[attrib] as Vec2fView).set(view)
                    is Vec3fView -> (attributeViews[attrib] as Vec3fView).set(view)
                    is Vec4fView -> (attributeViews[attrib] as Vec4fView).set(view)
                    is IntView -> (attributeViews[attrib] as IntView).i = view.i
                    is Vec2iView -> (attributeViews[attrib] as Vec2iView).set(view)
                    is Vec3iView -> (attributeViews[attrib] as Vec3iView).set(view)
                    is Vec4iView -> (attributeViews[attrib] as Vec4iView).set(view)
                }
            }
        }
    }

    fun getFloatAttribute(attribute: Attribute): FloatView? = attributeViews[attribute] as FloatView?
    fun getVec2fAttribute(attribute: Attribute): MutableVec2f? = attributeViews[attribute] as MutableVec2f?
    fun getVec3fAttribute(attribute: Attribute): MutableVec3f? = attributeViews[attribute] as MutableVec3f?
    fun getVec4fAttribute(attribute: Attribute): MutableVec4f? = attributeViews[attribute] as MutableVec4f?
    fun getColorAttribute(attribute: Attribute): MutableColor? = attributeViews[attribute]?.let { ColorWrapView(it as Vec4fView) }
    fun getIntAttribute(attribute: Attribute): IntView? = attributeViews[attribute] as IntView?
    fun getVec2iAttribute(attribute: Attribute): Vec2iView? = attributeViews[attribute] as Vec2iView?
    fun getVec3iAttribute(attribute: Attribute): Vec3iView? = attributeViews[attribute] as Vec3iView?
    fun getVec4iAttribute(attribute: Attribute): Vec4iView? = attributeViews[attribute] as Vec4iView?

    inner class FloatView(private val attribOffset: Int) {
        var f: Float
            get() = if (attribOffset < 0) { 0f } else { data.dataF[offsetF + attribOffset] }
            set(value) {
                if (attribOffset >= 0) { data.dataF[offsetF + attribOffset] = value }
            }
    }

    private inner class Vec2fView(private val attribOffset: Int) : MutableVec2f() {
        override var x
            get() = getComponent(0)
            set(value) = setComponent(0, value)
        override var y
            get() = getComponent(1)
            set(value) = setComponent(1, value)

        private fun getComponent(i: Int): Float {
            return if (attribOffset >= 0) {
                data.dataF[offsetF + attribOffset + i]
            } else {
                0f
            }
        }
        private fun setComponent(i: Int, v: Float) {
            if (attribOffset >= 0) {
                data.dataF[offsetF + attribOffset + i] = v
            }
        }
    }

    private inner class Vec3fView(val attribOffset: Int) : MutableVec3f() {
        override var x
            get() = getComponent(0)
            set(value) = setComponent(0, value)
        override var y
            get() = getComponent(1)
            set(value) = setComponent(1, value)
        override var z
            get() = getComponent(2)
            set(value) = setComponent(2, value)

        private fun getComponent(i: Int): Float {
            return if (attribOffset >= 0) {
                data.dataF[offsetF + attribOffset + i]
            } else {
                0f
            }
        }
        private fun setComponent(i: Int, v: Float) {
            if (attribOffset >= 0) {
                data.dataF[offsetF + attribOffset + i] = v
            }
        }
    }

    private inner class Vec4fView(val attribOffset: Int) : MutableVec4f() {
        override var x
            get() = getComponent(0)
            set(value) = setComponent(0, value)
        override var y
            get() = getComponent(1)
            set(value) = setComponent(1, value)
        override var z
            get() = getComponent(2)
            set(value) = setComponent(2, value)
        override var w
            get() = getComponent(3)
            set(value) = setComponent(3, value)

        private fun getComponent(i: Int): Float {
            return if (attribOffset >= 0) {
                data.dataF[offsetF + attribOffset + i]
            } else {
                0f
            }
        }
        private fun setComponent(i: Int, v: Float) {
            if (attribOffset >= 0) {
                data.dataF[offsetF + attribOffset + i] = v
            }
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
    }

    inner class IntView(private val attribOffset: Int) {
        var i: Int
            get() = if (attribOffset < 0) { 0 } else { data.dataI[offsetI + attribOffset] }
            set(value) {
                if (attribOffset >= 0) { data.dataI[offsetI + attribOffset] = value }
            }
    }

    inner class Vec2iView(private val attribOffset: Int) : MutableVec2i() {
        override var x
            get() = getComponent(0)
            set(value) = setComponent(0, value)
        override var y
            get() = getComponent(1)
            set(value) = setComponent(1, value)

        private fun getComponent(i: Int): Int {
            return if (attribOffset >= 0) {
                data.dataI[offsetI + attribOffset + i]
            } else {
                0
            }
        }
        private fun setComponent(i: Int, v: Int) {
            if (attribOffset >= 0) {
                data.dataI[offsetI + attribOffset + i] = v
            }
        }
    }

    inner class Vec3iView(private val attribOffset: Int) : MutableVec3i() {
        override var x
            get() = getComponent(0)
            set(value) = setComponent(0, value)
        override var y
            get() = getComponent(1)
            set(value) = setComponent(1, value)
        override var z
            get() = getComponent(2)
            set(value) = setComponent(2, value)

        private fun getComponent(i: Int): Int {
            return if (attribOffset >= 0) {
                data.dataI[offsetI + attribOffset + i]
            } else {
                0
            }
        }
        private fun setComponent(i: Int, v: Int) {
            if (attribOffset >= 0) {
                data.dataI[offsetI + attribOffset + i] = v
            }
        }
    }

    inner class Vec4iView(private val attribOffset: Int) : MutableVec4i() {
        override var x
            get() = getComponent(0)
            set(value) = setComponent(0, value)
        override var y
            get() = getComponent(1)
            set(value) = setComponent(1, value)
        override var z
            get() = getComponent(2)
            set(value) = setComponent(2, value)
        override var w
            get() = getComponent(3)
            set(value) = setComponent(3, value)

        private fun getComponent(i: Int): Int {
            return if (attribOffset >= 0) {
                data.dataI[offsetI + attribOffset + i]
            } else {
                0
            }
        }
        private fun setComponent(i: Int, v: Int) {
            if (attribOffset >= 0) {
                data.dataI[offsetI + attribOffset + i] = v
            }
        }
    }
}