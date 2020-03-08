package de.fabmax.kool.util

import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.GlslType
import kotlin.math.max
import kotlin.math.round

class MeshInstanceList(val instanceAttributes: List<Attribute>) {

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
     * Number of instances. Equal to [dataF.position] / [instanceSizeF].
     */
    var numInstances = 0

    var dataF: Float32Buffer

    init {
        var strideF = 0
        val offsets = mutableMapOf<Attribute, Int>()
        for (attrib in instanceAttributes) {
            if (attrib.type.isInt) {
                throw IllegalArgumentException("For now only float attributes are supported")
            } else {
                offsets[attrib] = strideF
                strideF += attrib.type.size
            }
        }

        attributeOffsets = offsets
        instanceSizeF = strideF / 4
        strideBytesF = strideF
        dataF = createFloat32Buffer(strideF * INITIAL_SIZE)
    }

    private fun increaseDataSizeF(newSize: Int) {
        val newData = createFloat32Buffer(newSize)
        dataF.flip()
        newData.put(dataF)
        dataF = newData
    }

    fun checkBufferSize(reqSpace: Int = 1) {
        if (dataF.remaining < instanceSizeF * reqSpace) {
            increaseDataSizeF(max(round(dataF.capacity * GROW_FACTOR).toInt(), (numInstances + reqSpace) * instanceSizeF))
        }
    }

    inline fun addInstance(block: Float32Buffer.() -> Unit) {
        checkBufferSize()
        val szBefore = dataF.position
        dataF.block()
        val growSz = dataF.position - szBefore
        if (growSz != instanceSizeF) {
            throw IllegalStateException("Expected data to grow by $instanceSizeF elements, instead it grew by $growSz")
        }
        numInstances++
    }

    companion object {
        private const val INITIAL_SIZE = 1000
        private const val GROW_FACTOR = 2.0f

        val MODEL_MAT = Attribute("attrib_model_mat", GlslType.MAT_4F)
    }
}