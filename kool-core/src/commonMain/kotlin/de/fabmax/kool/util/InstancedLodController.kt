package de.fabmax.kool.util

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene

class InstancedLodController<T: InstancedLodController.Instance>(name: String? = null) : Node(name) {

    val instances = mutableListOf<T>()

    override var isFrustumChecked: Boolean
        get() = false
        set(_) {}

    private val lods = mutableListOf<Lod>()

    fun getInstanceCount(lod: Int): Int {
        if (lod < lods.size) {
            return lods[lod].instances.size
        }
        return 0
    }

    fun addLod(lodMesh: Mesh, maxDistance: Float) {
        lods += Lod(lodMesh, maxDistance)
        lods.sortBy { it.maxDistance }
        lodMesh.parent = this
    }

    override fun onSceneChanged(oldScene: Scene?, newScene: Scene?) {
        super.onSceneChanged(oldScene, newScene)
        for (i in lods.indices) {
            lods[i].mesh.scene = newScene
        }
    }

    override fun preRender(ctx: KoolContext) {
        // clear assigned lods
        for (i in lods.indices) {
            lods[i].instances.clear()
        }

        // assign lod to each instance
        val cam = scene?.camera
        if (cam != null) {
            for (i in instances.indices) {
                val inst = instances[i]
                inst.preRender(cam, ctx)

                if (inst.isInFrustum) {
                    for (j in lods.lastIndex downTo 0) {
                        if (j == 0 || inst.camDistance > lods[j-1].maxDistance) {
                            lods[j].instances += inst
                            break
                        }
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

            lod.updateInstances(i, ctx)
            lod.mesh.preRender(ctx)
        }
    }

    override fun render(ctx: KoolContext) {
        for (i in lods.indices) {
            lods[i].mesh.render(ctx)
        }
    }

    override fun postRender(ctx: KoolContext) {
        for (i in lods.indices) {
            lods[i].mesh.postRender(ctx)
        }
    }

    override fun dispose(ctx: KoolContext) {
        super.dispose(ctx)
        for (i in lods.indices) {
            lods[i].mesh.dispose(ctx)
        }
    }

    private inner class Lod(val mesh: Mesh, val maxDistance: Float) {
        val instances = mutableListOf<T>()
        val maxInstances: Int
            get() = mesh.instances?.maxInstances ?: 0

        fun updateInstances(iLod: Int, ctx: KoolContext) {
            mesh.instances?.apply {
                clear()
                instances.forEach {
                    it.addInstanceData(iLod, this, ctx)
                }
            }
        }
    }

    open class Instance {
        var instanceModelMat = Mat4f()

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

        protected val globalCenterMut = MutableVec3f()
        protected val globalExtentMut = MutableVec3f()

        open fun preRender(cam: Camera, ctx: KoolContext) {
            // update global center and radius
            globalCenterMut.set(center)
            globalExtentMut.set(center).x += radius

            instanceModelMat.transform(globalCenterMut)
            instanceModelMat.transform(globalExtentMut)
            ctx.mvpState.modelMatrix.transform(globalCenterMut)
            ctx.mvpState.modelMatrix.transform(globalExtentMut)
            globalRadius = globalCenterMut.distance(globalExtentMut)

            isInFrustum = cam.isInFrustum(globalCenterMut, globalRadius)
            camDistance = cam.globalPos.distance(globalCenterMut)
        }

        open fun addInstanceData(lod: Int, instanceList: MeshInstanceList, ctx: KoolContext) {
            instanceList.addInstance {
                put(instanceModelMat.matrix)
            }
        }
    }
}