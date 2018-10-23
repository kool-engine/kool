package de.fabmax.kool.scene

import de.fabmax.kool.KoolContext
import de.fabmax.kool.gl.*
import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.shading.Attribute
import de.fabmax.kool.shading.AttributeType
import de.fabmax.kool.shading.VboBinder
import de.fabmax.kool.util.createFloat32Buffer

class InstancedMesh(meshData: MeshData, maxInstances: Int, name: String? = null) : Mesh(meshData, name) {

    private val instanceMvps = createFloat32Buffer(maxInstances * 16)
    private var numInstances = 0

    private var instanceBuffer: BufferResource? = null
    private val instanceBinders = Array<VboBinder?>(4) { null }

    init {
        numInstances = 2

        val mat = Mat4f()
        for (i in -1..1 step 2) {
            mat.setIdentity()
            mat.translate(0f, i.toFloat(), 0f)
            instanceMvps.put(mat.matrix)
        }
    }

    override fun getAttributeBinder(attrib: Attribute): VboBinder? {
        return when (attrib) {
            MODEL_INSTANCES_0 -> instanceBinders[0]
            MODEL_INSTANCES_1 -> instanceBinders[1]
            MODEL_INSTANCES_2 -> instanceBinders[2]
            MODEL_INSTANCES_3 -> instanceBinders[3]
            else -> super.getAttributeBinder(attrib)
        }
    }

    override fun render(ctx: KoolContext) {
        if (instanceBuffer == null) {
            instanceBuffer = BufferResource.create(GL_ARRAY_BUFFER, ctx)
            for (i in 0..3) {
                instanceBinders[i] = VboBinder(instanceBuffer!!, 4, 4 * 16, i * 4, GL_FLOAT, 1)
            }
        }

        instanceBuffer!!.setData(instanceMvps, GL_DYNAMIC_DRAW, ctx)
        super.render(ctx)
        instanceBuffer!!.unbind(ctx)
    }

    override fun drawElements(ctx: KoolContext) {
        // todo: possible optimization: use glDrawElementsInstancedBaseVertex
        glDrawElementsInstanced(meshData.primitiveType, meshData.numIndices, meshData.indexType, 0, numInstances)
    }

    override fun dispose(ctx: KoolContext) {
        super.dispose(ctx)
        instanceBuffer?.delete(ctx)
        instanceBuffer = null
        for (i in 0..3) {
            instanceBinders[i] = null
        }
    }

    companion object {
        val MODEL_INSTANCES_0 = Attribute("attrib_model_insts_0", AttributeType.VEC_4F)
                .apply { glslSrcName = "attrib_model_insts"; locationOffset = 0 }
        val MODEL_INSTANCES_1 = Attribute("attrib_model_insts_1", AttributeType.VEC_4F)
                .apply { glslSrcName = "attrib_model_insts"; locationOffset = 1 }
        val MODEL_INSTANCES_2 = Attribute("attrib_model_insts_2", AttributeType.VEC_4F)
                .apply { glslSrcName = "attrib_model_insts"; locationOffset = 2 }
        val MODEL_INSTANCES_3 = Attribute("attrib_model_insts_3", AttributeType.VEC_4F)
                .apply { glslSrcName = "attrib_model_insts"; locationOffset = 3 }
    }
}