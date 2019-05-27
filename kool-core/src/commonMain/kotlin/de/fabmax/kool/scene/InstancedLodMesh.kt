package de.fabmax.kool.scene

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.shading.Attribute

open class InstancedLodMesh<T: LodInstance>(val lodDescs: List<LodDesc>, name: String? = null, attributes: List<Attribute> = InstancedMesh.MODEL_INSTANCES) : Group() {
    val instances = mutableListOf<T>()

    val instancedLodMeshes = mutableListOf<InstancedMesh>()
    private val lodInstances = mutableListOf<LodInstances<T>>()

    init {
        lodDescs.forEachIndexed { i, lod ->
            val n = if (name != null) { "$name-lod-$i" } else { null }
            val insts = LodInstances<T>(lod.maxInstances)
            val mesh = InstancedMesh(lod.meshData, n, insts, attributes)
            instancedLodMeshes += mesh
            lodInstances += insts
            +mesh
        }
    }

    override fun preRender(ctx: KoolContext) {
        if (isVisible) {
            updateInstances(ctx)
        }
        super.preRender(ctx)
    }

    private fun updateInstances(ctx: KoolContext) {
        val cam = scene?.camera
        if (cam != null) {
            val tmpVec = MutableVec3f()
            for (i in lodInstances.indices) { lodInstances[i].clearInstances() }

            for (i in instances.indices) {
                val inst = instances[i]
                inst.lod = -1

                // determine global instance center position
                ctx.mvpState.modelMatrix.transform(tmpVec.set(inst.localOrigin))

                // do frustum check and compute lod based on cam distance
                if (cam.isInFrustum(tmpVec, inst.radius)) {
                    val camDist = cam.globalPos.distance(tmpVec)
                    for (j in lodDescs.indices) {
                        if (lodDescs[j].inRange(camDist)) {
                            inst.lod = j
                            lodInstances[j].addInstance(inst)
                            break
                        }
                    }
                }
            }
        }
    }

    class LodDesc(val meshData: MeshData, val minDist: Float, val maxDist: Float, val isCastingShadows: Boolean, val maxInstances: Int) {
        // notice that minDist and maxDist are separate fields instead of using a single field of type FloatRange
        // for some reason in the latter case an object is created during range check which produces lots of heap
        // garbage if many instances are checked
        fun inRange(dist: Float) = dist >= minDist && dist <= maxDist
    }

    private class LodInstances<T: LodInstance>(maxInstances: Int) : InstancedMesh.Instances<T>(maxInstances) {
        override fun isIncludeInstance(instance: T, localPos: Vec3f, cam: Camera, ctx: KoolContext) = true
    }
}

open class LodInstance : InstancedMesh.Instance(Mat4f()) {
    val localOrigin = MutableVec3f()
    var lod = -1
}