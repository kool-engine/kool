package de.fabmax.kool.demo.physics.collision

import de.fabmax.kool.math.*
import de.fabmax.kool.physics.Material
import de.fabmax.kool.physics.Shape
import de.fabmax.kool.physics.geometry.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.scene.geometry.simpleShape
import de.fabmax.kool.util.PolyUtil
import kotlin.math.*
import kotlin.random.Random

enum class ShapeType {
    BOX {
        override val label = "Box"

        override fun MeshBuilder.generateShapeMesh() = bevelBox()

        override fun generatePhysicsShapes(meshGeometry: IndexedVertexList, material: Material, rand: Random): CollisionShapes {
            val sx = rand.randomF(2f, 3f)
            val sy = rand.randomF(2f, 3f)
            val sz = rand.randomF(2f, 3f)
            val mass = sx * sy * sz
            return CollisionShapes(mass, Shape(BoxGeometry(Vec3f(sx, sy, sz)), material))
        }
    },

    CAPSULE {
        override val label = "Capsule"

        override fun MeshBuilder.generateShapeMesh() = capsule()

        override fun generatePhysicsShapes(meshGeometry: IndexedVertexList, material: Material, rand: Random): CollisionShapes {
            val s = rand.randomF(0.75f, 1.5f)
            val mass = s.pow(3)
            return CollisionShapes(mass, Shape(CapsuleGeometry(2.5f * s, s), material))
        }
    },

    CONVEX_HULL {
        override val label = "Convex Hull"

        override fun MeshBuilder.generateShapeMesh() = flatIcoSphere()

        var convexMesh: ConvexMesh? = null

        override fun generatePhysicsShapes(meshGeometry: IndexedVertexList, material: Material, rand: Random): CollisionShapes {
            if (convexMesh == null) {
                val icoPoints = mutableListOf<Vec3f>()
                meshGeometry.forEach { icoPoints.add(Vec3f(it)) }
                convexMesh = ConvexMesh(icoPoints)
                convexMesh!!.releaseWithGeometry = false
            }
            val s = rand.randomF(1.25f, 2.5f)
            val mass = s.pow(3)
            val shapes = CollisionShapes(mass, Shape(ConvexMeshGeometry(convexMesh!!, Vec3f(s, s, s)), material))
            shapes.scale = s
            return shapes
        }
    },

    CYLINDER {
        override val label = "Cylinder"

        override fun MeshBuilder.generateShapeMesh() = cylinder()

        override fun generatePhysicsShapes(meshGeometry: IndexedVertexList, material: Material, rand: Random): CollisionShapes {
            val l = rand.randomF(2f, 4f)
            val r = rand.randomF(1f, 2f)
            val mass = r * r * l * 0.5f
            return CollisionShapes(mass, Shape(CylinderGeometry(l, r), material))
        }
    },

    MULTI_SHAPE {
        override val label = "Multi Shape"

        override fun MeshBuilder.generateShapeMesh() {
            cube {
                size.set(0.5f, 0.5f, 2f)
                origin.set(1f, 0f, 0f)
            }
            cube {
                size.set(0.5f, 0.5f, 2f)
                origin.set(-1f, 0f, 0f)
            }
            cube {
                size.set(2.5f, 0.5f, 0.5f)
                origin.set(0f, 0f, 1.25f)
            }
            cube {
                size.set(2.5f, 0.5f, 0.5f)
                origin.set(0f, 0f, -1.25f)
            }
        }

        override fun generatePhysicsShapes(meshGeometry: IndexedVertexList, material: Material, rand: Random): CollisionShapes {
            val s = rand.randomF(1f, 2f)
            val shape1 = Shape(
                BoxGeometry(MutableVec3f(0.5f, 0.5f, 2.0f).mul(s)),
                material,
                Mat4f.translation(1f * s, 0f, 0f)
            )
            val shape2 = Shape(
                BoxGeometry(MutableVec3f(0.5f, 0.5f, 2.0f).mul(s)),
                material,
                Mat4f.translation(-1f * s, 0f, 0f)
            )
            val shape3 = Shape(
                BoxGeometry(MutableVec3f(2.5f, 0.5f, 0.5f).mul(s)),
                material,
                Mat4f.translation(0f, 0f, 1.25f * s)
            )
            val shape4 = Shape(
                BoxGeometry(MutableVec3f(2.5f, 0.5f, 0.5f).mul(s)),
                material,
                Mat4f.translation(0f, 0f, -1.25f * s)
            )

            val mass = 8 * s.pow(3)
            return CollisionShapes(mass, shape1, shape2, shape3, shape4)
        }
    },

    SPHERE {
        override val label = "Sphere"

        override fun MeshBuilder.generateShapeMesh() {
            icoSphere { steps = 2 }
        }

        override fun generatePhysicsShapes(meshGeometry: IndexedVertexList, material: Material, rand: Random): CollisionShapes {
            val r = rand.randomF(1.25f, 2.5f)
            return CollisionShapes(r, Shape(SphereGeometry(r), material))
        }
    },

    MIXED {
        override val label = "Mixed"

        override fun MeshBuilder.generateShapeMesh() { }

        override fun generatePhysicsShapes(meshGeometry: IndexedVertexList, material: Material, rand: Random): CollisionShapes {
            throw IllegalStateException()
        }
    };

    abstract val label: String

    abstract fun MeshBuilder.generateShapeMesh()
    abstract fun generatePhysicsShapes(meshGeometry: IndexedVertexList, material: Material, rand: Random): CollisionShapes

    override fun toString(): String {
        return label
    }

    fun MeshBuilder.bevelBox(rBevel: Float = 0.02f) {
        withTransform {
            rotate(0f.deg, 90f.deg, 0f.deg)
            rect {
                isCenteredOrigin = false
                size.set(1f - 2 * rBevel, 1f - 2 * rBevel)
                origin.set(-0.5f + rBevel, -0.5f + rBevel, 0.5f)
            }
        }
        withTransform {
            rotate(0f.deg, (-90f).deg, 0f.deg)
            rect {
                isCenteredOrigin = false
                size.set(1f - 2 * rBevel, 1f - 2 * rBevel)
                origin.set(-0.5f + rBevel, -0.5f + rBevel, 0.5f)
            }
        }
        withTransform {
            profile {
                val s = 1f / sqrt(2f)
                val c = 1f - s
                simpleShape(false) {
                    xy(-0.5f, 0f)
                    normals += MutableVec3f(-1f, 0f, 0f)
                    xy(-0.5f + rBevel * c, rBevel * s)
                    normals += MutableVec3f(-1f, 1f, 0f).norm()
                    xy(-0.5f + rBevel, rBevel)
                    normals += MutableVec3f(0f, 1f, 0f)

                    xy(0.5f - rBevel, rBevel)
                    normals += MutableVec3f(0f, 1f, 0f)
                    xy(0.5f - rBevel * c, rBevel * s)
                    normals += MutableVec3f(1f, 1f, 0f).norm()
                    xy(0.5f, 0f)
                    normals += MutableVec3f(1f, 0f, 0f)
                }

                translate(0f, 0.5f - rBevel, -0.5f + rBevel)
                sample()
                for (i in 0..3) {
                    rotate((-45f).deg, 0f.deg, 0f.deg)
                    sample()
                    rotate((-45f).deg, 0f.deg, 0f.deg)
                    sample()
                    translate(0f, 0f, -1f + 2 * rBevel)
                    sample()
                }
            }
        }
    }

    fun MeshBuilder.capsule(halfHeight: Float = 1.25f, radius: Float = 1f) {
        profile {
            simpleShape(false) {
                xyArc(Vec2f(halfHeight + radius, 0f), Vec2f(halfHeight, 0f), 90f.deg, 10, true)
                xyArc(Vec2f(-halfHeight, radius), Vec2f(-halfHeight, 0f), 90f.deg, 10, true)
            }
            for (i in 0 .. 20) {
                sample()
                rotate(360f.deg / 20, 0f.deg, 0f.deg)
            }
        }
    }

    fun MeshBuilder.flatIcoSphere() {
        val icoMesh = MeshBuilder(IndexedVertexList(Attribute.POSITIONS)).apply { icoSphere { steps = 0 } }
        for (i in 0 until icoMesh.geometry.numIndices step 3) {
            val vIt = icoMesh.geometry.vertexIt
            vIt.index = icoMesh.geometry.indices[i]
            val i0 = geometry.addVertex(vIt)
            vIt.index = icoMesh.geometry.indices[i + 1]
            val i1 = geometry.addVertex(vIt)
            vIt.index = icoMesh.geometry.indices[i + 2]
            val i2 = geometry.addVertex(vIt)
            geometry.addTriIndices(i0, i1, i2)
        }
        geometry.generateNormals()
    }

    fun MeshBuilder.cylinder(height: Float = 1f, radius: Float = 1f) {
        // make a beveled cylinder which looks a bit nicer than a simple one with sharp edges
        profile {
            simpleShape(false) {
                val bevelSteps = 3
                val bevelR = 0.02f
                for (i in 0..bevelSteps) {
                    val a = (i / bevelSteps.toFloat() * PI / 2 + PI * 1.5).toFloat()
                    val x = cos(a)
                    val y = sin(a)
                    xy(radius - bevelR + x * bevelR, height * -0.5f + bevelR + y * bevelR)
                    normals += MutableVec3f(x, y, 0f)
                }
                for (i in 0..bevelSteps) {
                    val a = (i / bevelSteps.toFloat() * PI / 2).toFloat()
                    val x = cos(a)
                    val y = sin(a)
                    xy(radius - bevelR + x * bevelR, height * 0.5f - bevelR + y * bevelR)
                    normals += MutableVec3f(x, y, 0f)
                }
            }

            val topVertInds = mutableListOf<Int>()
            val bottomVertInds = mutableListOf<Int>()
            val topVerts = mutableListOf<Vec3f>()
            val bottomVerts = mutableListOf<Vec3f>()
            rotate(90f.deg, Vec3f.Z_AXIS)
            for (i in 0 .. 40) {
                rotate(0f.deg, (-360f / 40).deg, 0f.deg)
                sample()
                geometry.vertexIt.apply {
                    index = shapes[0].sampledVertIndices.first()
                    bottomVertInds += index
                    bottomVerts += Vec3f(position)
                }
                geometry.vertexIt.apply {
                    index = shapes[0].sampledVertIndices.last()
                    topVertInds += index
                    topVerts += Vec3f(position)
                }
            }

            val topPoly = PolyUtil.fillPolygon(topVerts)
            for (i in topPoly.indices.indices step 3) {
                geometry.addTriIndices(topVertInds[topPoly.indices[i]],
                    topVertInds[topPoly.indices[i + 2]], topVertInds[topPoly.indices[i + 1]])
            }

            val bottomPoly = PolyUtil.fillPolygon(bottomVerts)
            for (i in bottomPoly.indices.indices step 3) {
                geometry.addTriIndices(bottomVertInds[bottomPoly.indices[i]],
                    bottomVertInds[bottomPoly.indices[i + 1]], bottomVertInds[bottomPoly.indices[i + 2]])
            }
        }
    }

    class CollisionShapes(val mass: Float, vararg shapes: Shape) {
        val primitives = mutableListOf(*shapes)

        // only used for mesh shapes (convex / triangle)
        var scale = 1f
    }
}