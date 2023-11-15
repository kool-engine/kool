package de.fabmax.kool.util

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableMat4f
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Node

class InstancedLodController<T: InstancedLodController.Instance<T>>(name: String? = null) : Node(name) {

    val instances = mutableListOf<T>()

    private val lods = mutableListOf<Lod>()

    fun getInstanceCount(lod: Int): Int {
        if (lod < lods.size) {
            return lods[lod].instances.size
        }
        return 0
    }

    fun addLod(lodMesh: Mesh, maxDistance: Float, maxInstances: Int = Int.MAX_VALUE) {
        lods += Lod(lodMesh, maxDistance, maxInstances)
        lods.sortBy { it.maxDistance }
        this += lodMesh
    }

    override fun update(updateEvent: RenderPass.UpdateEvent) {
        // clear assigned lods
        for (i in lods.indices) {
            lods[i].instances.clear()
        }

        // assign lod to each instance
        val cam = updateEvent.camera
        for (i in instances.indices) {
            val inst = instances[i]
            inst.update(this, cam, updateEvent.ctx)

            if (inst.isInFrustum) {
                for (j in lods.lastIndex downTo 0) {
                    if (j == 0 || inst.camDistance > lods[j-1].maxDistance) {
                        lods[j].instances += inst
                        break
                    }
                }
            }
        }

        for (i in lods.indices) {
            val lod = lods[i]
            if (lod.instances.size > lod.maxInstances) {
                // too many instances assigned to lod, sort by distance and move farthest to next lod
                lod.instances.sortBy { it.camDistance }

                while (lod.instances.size > lod.maxInstances) {
                    val rmInst = lod.instances.removeAt(lod.instances.lastIndex)
                    if (i < lods.lastIndex) {
                        lods[i+1].instances += rmInst
                    }
                }
            }

            lod.updateInstances(i, updateEvent.ctx)
        }

        super.update(updateEvent)
    }

    private inner class Lod(val mesh: Mesh, val maxDistance: Float, val maxInstances: Int) {
        val instances = mutableListOf<T>()

        fun updateInstances(iLod: Int, ctx: KoolContext) {
            mesh.instances?.apply {
                clear()
                addInstances(instances.size) { buf ->
                    for (i in instances.indices) {
                        instances[i].addInstanceData(iLod, buf, ctx)
                    }
                }
            }
        }
    }

    open class Instance<T: Instance<T>> {
        var instanceModelMat = MutableMat4f()

        val center = MutableVec3f()
        var radius = 1f

        val globalCenter: Vec3f
            get() = globalCenterMut
        var globalRadius = 0f
            protected set
        var camDistance = 0f
            protected set
        var isInFrustum = false
            protected set

        private val globalCenterMut = MutableVec3f()
        private val globalExtentMut = MutableVec3f()

        open fun update(lodCtrl: InstancedLodController<T>, cam: Camera, ctx: KoolContext) {
            // update global center and radius
            globalCenterMut.set(center)
            globalExtentMut.set(center).x += radius

            instanceModelMat.transform(globalCenterMut)
            instanceModelMat.transform(globalExtentMut)
            lodCtrl.modelMatF.transform(globalCenterMut)
            lodCtrl.modelMatF.transform(globalExtentMut)
            globalRadius = globalCenterMut.distance(globalExtentMut)

            isInFrustum = cam.isInFrustum(globalCenterMut, globalRadius)
            camDistance = cam.globalPos.distance(globalCenterMut)
        }

        open fun addInstanceData(lod: Int, buffer: Float32Buffer, ctx: KoolContext) {
            instanceModelMat.putTo(buffer)
        }
    }
}