package de.fabmax.kool.util

import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.GlslType
import kotlin.math.max

class MeshInstanceList(val instanceAttributes: List<Attribute>, initialSize: Int = 100) {

    /**
     * Vertex attribute offsets in bytes.
     */
    val attributeOffsets: Map<Attribute, Int>

    /**
     * Number of floats per instance. E.g. an instance containing only a model matrix consists of 16 floats.
     */
    val instanceSizeF: Int

    /**
     * Number of bytes per instance. E.g. an instance containing only a model matrix consists of 16 * 4 = 64 bytes.
     */
    val strideBytesF: Int

    /**
     * Expected usage of data in this instance list: STATIC if attributes are expected to change very infrequently /
     * never, DYNAMIC if they will be updated often.
     */
    var usage = Usage.DYNAMIC

    /**
     * Number of instances.
     */
    var numInstances = 0

    var dataF: Float32Buffer
    private val strideFloats: Int

    internal var maxInstances = initialSize
        private set

    var hasChanged = true

    constructor(initialSize: Int, vararg instanceAttributes: Attribute) : this(listOf(*instanceAttributes), initialSize)

    init {
        var strideF = 0
        val offsets = mutableMapOf<Attribute, Int>()
        for (attrib in instanceAttributes) {
            if (attrib.type.isInt) {
                throw IllegalArgumentException("For now only float attributes are supported")
            } else {
                offsets[attrib] = strideF
                strideF += attrib.type.byteSize
            }
        }
        strideFloats = strideF

        attributeOffsets = offsets
        instanceSizeF = strideFloats / 4
        strideBytesF = strideFloats
        dataF = createFloat32Buffer(strideFloats * maxInstances)
    }

    fun checkBufferSize(reqSpace: Int = 1) {
        if (numInstances + reqSpace > maxInstances) {
            maxInstances = max(maxInstances * 2, numInstances + reqSpace)
            val newBuf = createFloat32Buffer(strideFloats * maxInstances)
            dataF.flip()
            newBuf.put(dataF)
            dataF = newBuf

        }
    }

    inline fun addInstance(block: Float32Buffer.() -> Unit) = addInstances(1) { it.block() }

    inline fun addInstances(n: Int, block: (Float32Buffer) -> Unit) {
        checkBufferSize(n)
        val szBefore = dataF.position
        block(dataF)
        val growSz = dataF.position - szBefore
        if (growSz != instanceSizeF * n) {
            throw IllegalStateException("Expected data to grow by ${instanceSizeF * n} elements, instead it grew by $growSz")
        }
        numInstances += n
        hasChanged = true
    }

    fun clear() {
        numInstances = 0
        dataF.position = 0
        dataF.limit = dataF.capacity
        hasChanged = true
    }

    companion object {
        val MODEL_MAT = Attribute("attrib_model_mat", GlslType.MAT_4F)
    }
}