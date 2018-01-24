package de.fabmax.kool.demo

import de.fabmax.kool.currentTimeMillis
import de.fabmax.kool.math.random
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.*

/**
 * @author fabmax
 */

fun pointScene(): Scene {
    val (pointMesh, tree) = makePointMesh()
    //val (pointMesh, tree) = makeBillboardPointMesh()

    val trav = InRadiusTraverser<MeshPoint>(Vec3f.ZERO, 1f)
    val data = pointMesh.meshData
    val ptVertCnt = if (pointMesh is BillboardMesh) 4 else 1

    var frameCnt = 30

    // Create scene contents
    val scene = scene {
        onRender += {
            // change color of a few points every 30 frames
            if (--frameCnt == 0) {
                frameCnt = 30

                val vert = data[0]
                for (point in trav.result) {
                    for (i in 0 until ptVertCnt) {
                        vert.index = point.index + i
                        vert.color.set(Color.DARK_GRAY)
                    }
                }

                trav.center.set((random() - 0.5).toFloat() * 2f,
                        (random() - 0.5).toFloat() * 2f, (random() - 0.5).toFloat() * 2f)
                val t = currentTimeMillis()
                tree.traverse(trav)
                println("In-radius search in ${currentTimeMillis() - t} ms, got ${trav.result.size} points")

                val color = Color.fromHsv(random().toFloat() * 360f, 1f, 1f, 1f)
                for (point in trav.result) {
                    for (i in 0..ptVertCnt-1) {
                        vert.index = point.index + i
                        vert.color.set(color)
                    }
                }

                data.isSyncRequired = true
            }
        }

        // Add a mouse-controlled camera manipulator (actually a specialized TransformGroup)
        +sphericalInputTransform {
            +camera
            setMouseRotation(0f, -30f)
            minZoom = 5f
            maxZoom = 25f
            // panning / camera translation is limited to a certain area
            translationBounds = BoundingBox(Vec3f(-10f, -10f, -10f), Vec3f(10f, 10f, 10f))
        }

        +transformGroup {
            onRender += {
                rotate(it.deltaT.toFloat() * 45, Vec3f.Y_AXIS)
            }
            +pointMesh
        }
    }
    return scene
}

fun makePointMesh(): Pair<Mesh, KdTree<MeshPoint>> {
    val points: MutableList<MeshPoint> = mutableListOf()
    val mesh = pointMesh {
        pointSize = 3f

        for (i in 1..100_000) {
            val x = (random().toFloat() - 0.5f) * 5
            val z = (random().toFloat() - 0.5f) * 5
            val y = (random().toFloat() - 0.5f) * 5

            val idx = addPoint {
                position.set(x, y, z)
                color.set(Color.DARK_GRAY)
            }
            points.add(MeshPoint(x, y, z, idx))
        }
    }
    val t = currentTimeMillis()
    val tree = pointTree(points)
    println("Constructed k-d-Tree with ${points.size} points in ${currentTimeMillis() - t} ms")
    return Pair(mesh, tree)
}

fun makeBillboardPointMesh(): Pair<BillboardMesh, KdTree<MeshPoint>> {
    val mesh = BillboardMesh()
    mesh.billboardSize = 3f

    val points: MutableList<MeshPoint> = mutableListOf()
    for (i in 1..100_000) {
        val x = (random().toFloat() - 0.5f) * 5
        val z = (random().toFloat() - 0.5f) * 5
        val y = (random().toFloat() - 0.5f) * 5

        mesh.addQuad(Vec3f(x, y, z), Color.DARK_GRAY)
        points.add(MeshPoint(x, y, z, (i-1)*4))
    }

    val t = currentTimeMillis()
    val tree = pointTree(points)
    println("Constructed k-d-Tree with ${points.size} points in ${currentTimeMillis() - t} ms")
    return Pair(mesh, tree)
}

class MeshPoint(x: Float, y: Float, z: Float, val index: Int): Vec3f(x, y, z)
