package de.fabmax.kool.scene

import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.backend.GpuInstances
import de.fabmax.kool.pipeline.isInt
import de.fabmax.kool.scene.geometry.Usage
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.Float32Buffer
import kotlin.math.max

class MeshInstanceList(val instanceAttributes: List<Attribute>, initialSize: Int = 100) : BaseReleasable() {

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
     * never, DYNAMIC (the default value) if they will be updated often.
     */
    var usage = Usage.DYNAMIC

    /**
     * Number of instances.
     */
    var numInstances = 0

    var dataF: Float32Buffer
        private set

    private val strideFloats: Int

    var maxInstances = initialSize
        private set

    var hasChanged = true

    var gpuInstances: GpuInstances? = null

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
        dataF = Float32Buffer(strideFloats * maxInstances, true)
    }

    fun checkBufferSize(reqSpace: Int = 1) {
        if (numInstances + reqSpace > maxInstances) {
            maxInstances = max(maxInstances * 2, numInstances + reqSpace).coerceAtMost(Int.MAX_VALUE / (strideFloats * 4))
            check(maxInstances >= numInstances + reqSpace) {
                "Unable to increase instance buffer size to required size of ${numInstances + reqSpace} instances. " +
                        "Maximum size is ${Int.MAX_VALUE / (strideFloats * 4)} instances / ${Int.MAX_VALUE} bytes."
            }
            val newBuf = Float32Buffer(strideFloats * maxInstances, true)
            newBuf.put(dataF)
            dataF = newBuf
        }
    }

    inline fun addInstance(block: Float32Buffer.() -> Unit) = addInstances(1) { it.block() }

    inline fun addInstances(n: Int, block: (Float32Buffer) -> Unit) {
        if (n == 0) {
            return
        }
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

    inline fun addInstancesUpTo(upperBoundN: Int, block: (Float32Buffer) -> Int) {
        if (upperBoundN == 0) {
            return
        }
        checkBufferSize(upperBoundN)
        val szBefore = dataF.position
        val actuallyAdded = block(dataF)
        val growSz = dataF.position - szBefore
        if (growSz != instanceSizeF * actuallyAdded) {
            throw IllegalStateException("Expected data to grow by ${instanceSizeF * upperBoundN} elements, instead it grew by $growSz")
        }
        numInstances += actuallyAdded
        if (actuallyAdded > 0) {
            hasChanged = true
        }
    }

    fun clear() {
        if (numInstances == 0) {
            return
        }
        numInstances = 0
        dataF.clear()
        hasChanged = true
    }

    override fun release() {
        gpuInstances?.release()
        gpuInstances = null
        super.release()
    }
}