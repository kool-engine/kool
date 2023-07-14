package de.fabmax.kool.scene

import de.fabmax.kool.math.RayTest
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.spatial.*
import de.fabmax.kool.scene.geometry.PrimitiveType
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
                val distSqr = mesh.geometry.bounds.hitDistanceSqr(test.ray)
                if (distSqr < Float.MAX_VALUE && distSqr <= test.hitDistanceSqr) {
                    test.setHit(mesh, sqrt(distSqr))
                }
            }
        }

        fun geometryTest(mesh: Mesh): MeshRayTest {
            return when (mesh.geometry.primitiveType) {
                PrimitiveType.TRIANGLES -> TriangleGeometry(mesh)
                PrimitiveType.LINES -> LineGeometry(mesh)
                else -> throw IllegalArgumentException("Mesh primitive type must be either GL_TRIANGLES or GL_LINES")
            }
        }
    }

    class TriangleGeometry(val mesh: Mesh) : MeshRayTest {
        var triangleTree: KdTree<Triangle>? = null
            private set
        private val rayTraverser = TriangleHitTraverser<Triangle>()

        override fun onMeshDataChanged(mesh: Mesh) {
            triangleTree = if (mesh.geometry.primitiveType == PrimitiveType.TRIANGLES) {
                triangleKdTree(Triangle.getTriangles(mesh.geometry))
            } else {
                null
            }
        }

        override fun rayTest(test: RayTest) {
            rayTraverser.setup(test.ray)
            triangleTree?.let { rayTraverser.traverse(it) }
            if (rayTraverser.distanceSqr < test.hitDistanceSqr) {
                test.setHit(mesh, rayTraverser.distance)
            }
        }
    }

    class LineGeometry(val mesh: Mesh) : MeshRayTest {
        var edgeTree: KdTree<Edge<Vec3f>>? = null
            private set
        private val rayTraverser = NearestEdgeToRayTraverser<Edge<Vec3f>>()

        override fun onMeshDataChanged(mesh: Mesh) {
            edgeTree = if (mesh.geometry.primitiveType == PrimitiveType.LINES) {
                edgeKdTree(Edge.getEdges(mesh.geometry))
            } else {
                null
            }
        }

        override fun rayTest(test: RayTest) {
            rayTraverser.setup(test.ray)
            edgeTree?.let { rayTraverser.traverse(it) }
            if (rayTraverser.distanceSqr < test.hitDistanceSqr) {
                test.setHit(mesh, rayTraverser.distance)
            }
        }
    }
}
