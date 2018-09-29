package de.fabmax.kool.scene

import de.fabmax.kool.gl.GL_LINES
import de.fabmax.kool.gl.GL_TRIANGLES
import de.fabmax.kool.math.RayTest
import de.fabmax.kool.util.*
import kotlin.math.sqrt

interface MeshRayTest {

    fun rayTest(test: RayTest)
    fun onMeshDataChanged(mesh: Mesh) { }

    companion object {
        fun nopTest(): MeshRayTest = object : MeshRayTest {
            override fun rayTest(test: RayTest) { }
        }

        fun boundsTest(): MeshRayTest = object : MeshRayTest {
            var mesh: Mesh? = null

            override fun onMeshDataChanged(mesh: Mesh) {
                this.mesh = mesh
            }

            override fun rayTest(test: RayTest) {
                val mesh = this.mesh ?: return
                val distSqr = mesh.bounds.hitDistanceSqr(test.ray)
                if (distSqr < Float.MAX_VALUE && distSqr <= test.hitDistanceSqr) {
                    test.setHit(mesh, sqrt(distSqr))
                }
            }
        }

        fun geometryTest(mesh: Mesh): MeshRayTest {
            return when (mesh.meshData.primitiveType) {
                GL_TRIANGLES -> object : MeshRayTest {
                    var triangleTree: KdTree<Triangle>? = null
                    val rayTraverser = TriangleHitTraverser<Triangle>()

                    override fun onMeshDataChanged(mesh: Mesh) {
                        triangleTree = triangleKdTree(Triangle.getTriangles(mesh.meshData))
                    }

                    override fun rayTest(test: RayTest) {
                        rayTraverser.setup(test.ray)
                        triangleTree?.traverse(rayTraverser)
                        if (rayTraverser.distanceSqr < test.hitDistanceSqr) {
                            test.setHit(mesh, rayTraverser.distance)
                        }
                    }
                }
                GL_LINES -> object : MeshRayTest {
                    var edgeTree: KdTree<Edge>? = null
                    val rayTraverser = NearestEdgeToRayTraverser<Edge>()

                    override fun onMeshDataChanged(mesh: Mesh) {
                        edgeTree = edgeKdTree(Edge.getEdges(mesh.meshData))
                    }

                    override fun rayTest(test: RayTest) {
                        rayTraverser.setup(test.ray)
                        edgeTree?.traverse(rayTraverser)
                        if (rayTraverser.distanceSqr < test.hitDistanceSqr) {
                            test.setHit(mesh, rayTraverser.distance)
                        }
                    }
                }
                else -> throw IllegalArgumentException("Mesh primitive type must be either GL_TRIANGLES or GL_LINES")
            }
        }
    }
}
