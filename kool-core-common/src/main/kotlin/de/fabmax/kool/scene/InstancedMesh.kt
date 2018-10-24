package de.fabmax.kool.scene

import de.fabmax.kool.KoolContext
import de.fabmax.kool.gl.*
import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.shading.Attribute
import de.fabmax.kool.shading.AttributeType
import de.fabmax.kool.shading.VboBinder
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.createFloat32Buffer
import kotlin.math.min

class InstancedMesh(meshData: MeshData, val maxInstances: Int, name: String? = null) : Mesh(meshData, name) {

    val instances = mutableListOf<Mat4f>()

    private val instanceBounds = BoundingBox()

    private val instanceMvps = createFloat32Buffer(maxInstances * 16)
    private var numInstances = 0

    private var instanceBuffer: BufferResource? = null
    private val instanceBinders = Array<VboBinder?>(4) { null }

    private val tmpInstanceCenterLocal = MutableVec3f()
    private val tmpInstanceCenterGlobal = MutableVec3f()

    /**
     * Estimated [BoundingBox] containing all instances. Only valid if [isFrustumChecked] is true and individual
     * instance model matrices don't apply scaling.
     */
    override val bounds: BoundingBox
        get() = instanceBounds

    override fun preRender(ctx: KoolContext) {
        instanceBounds.set(meshData.bounds)
        instanceMvps.clear()
        numInstances = 0

        super.preRender(ctx)

        val cam = scene?.camera
        if (isFrustumChecked && cam != null) {
            for (i in instances.indices) {
                val mat = instances[i]
                tmpInstanceCenterLocal.set(Vec3f.ZERO)
                mat.transform(tmpInstanceCenterLocal)
                tmpInstanceCenterGlobal.set(tmpInstanceCenterLocal)
                ctx.mvpState.modelMatrix.transform(tmpInstanceCenterGlobal)

                instanceBounds.add(tmpInstanceCenterLocal)

                // this assumes individual instances have all the same size (i.e. no scaling by the individual
                // instance matrices)
                if (cam.isInFrustum(tmpInstanceCenterGlobal, globalRadius)) {
                    instanceMvps.put(mat.matrix)
                    numInstances++
                }

                if (numInstances == maxInstances) {
                    break
                }
            }

            if (!instanceBounds.isEmpty) {
                tmpInstanceCenterGlobal.set(meshData.bounds.size).scale(0.5f)
                instanceBounds.expand(tmpInstanceCenterGlobal)
            }
        } else {
            for (i in 0 until min(instances.size, maxInstances)) {
                instanceMvps.put(instances[i].matrix)
                numInstances++
            }
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
                instanceBinders[i] = VboBinder(instanceBuffer!!, 4, 4 * 16, i * 4, GL_FLOAT)
            }
        }
        instanceBuffer!!.setData(instanceMvps, GL_DYNAMIC_DRAW, ctx)

        // disable frustum check flag before calling render, standard frustum check doesn't work with InstancedMesh
        val wasFrustumChecked = isFrustumChecked
        isFrustumChecked = false
        super.render(ctx)
        isFrustumChecked = wasFrustumChecked
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
        val MODEL_INSTANCES_0 = Attribute("attrib_model_insts_0", AttributeType.VEC_4F).apply {
            glslSrcName = "attrib_model_insts"
            locationOffset = 0
            divisor = 1
        }
        val MODEL_INSTANCES_1 = Attribute("attrib_model_insts_1", AttributeType.VEC_4F).apply {
            glslSrcName = "attrib_model_insts"
            locationOffset = 1
            divisor = 1
        }
        val MODEL_INSTANCES_2 = Attribute("attrib_model_insts_2", AttributeType.VEC_4F).apply {
            glslSrcName = "attrib_model_insts"
            locationOffset = 2
            divisor = 1
        }
        val MODEL_INSTANCES_3 = Attribute("attrib_model_insts_3", AttributeType.VEC_4F).apply {
            glslSrcName = "attrib_model_insts"
            locationOffset = 3
            divisor = 1
        }
    }
}