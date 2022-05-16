package de.fabmax.kool.demo.physics.terrain

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.spatial.InViewFrustumTraverser
import de.fabmax.kool.math.spatial.KdTree
import de.fabmax.kool.math.spatial.pointKdTree
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.scene.geometry.IndexedVertexList

class Ocean(val camera: Camera, val wind: Wind) {

    private val oceanInstances = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT))
    private val tileCenters: KdTree<Vec3f>
    private val visibleTileTraverser = InViewFrustumTraverser<Vec3f>()

    var oceanShader: WindAffectedShader? = null
        set(value) {
            field = value
            oceanMesh.shader = value?.shader
        }

    val oceanMesh = Mesh(IndexedVertexList(Attribute.POSITIONS, Attribute.NORMALS)).apply {
        isFrustumChecked = false
        isCastingShadow = false
        instances = oceanInstances
        generate {
            grid {
                sizeX = TILE_SIZE
                sizeY = TILE_SIZE
                stepsX = 32
                stepsY = 32
            }
        }

        onUpdate += {
            oceanShader?.let {
                val offStr = MutableVec4f(wind.offsetStrength).scale(0.4f)
                offStr.w = wind.offsetStrength.w * 6f

                it.windScale = 1f / (wind.scale * 1.5f)
                it.windOffsetStrength = offStr
            }
        }
    }

    init {
        val sz = 20
        val centerPoints = mutableListOf<Vec3f>()
        for (y in -sz .. sz) {
            for (x in -sz .. sz) {
                centerPoints += Vec3f(x * TILE_SIZE, 0f, y * TILE_SIZE)
            }
        }
        tileCenters = pointKdTree(centerPoints)

        val instTransform = Mat4f()
        camera.onCameraUpdated += {
            visibleTileTraverser.setup(camera, TILE_RADIUS).traverse(tileCenters)
            oceanInstances.clear()
            oceanInstances.addInstances(visibleTileTraverser.result.size) { buf ->
                for (i in visibleTileTraverser.result.indices) {
                    instTransform.setIdentity().translate(visibleTileTraverser.result[i])
                    buf.put(instTransform.matrix)
                }
            }
        }
    }

    companion object {
        const val TILE_SIZE = 64f
        const val TILE_RADIUS = TILE_SIZE * 0.708f
    }
}