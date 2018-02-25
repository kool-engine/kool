package de.fabmax.kool.demo

import de.fabmax.kool.currentTimeMillis
import de.fabmax.kool.math.*
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.BillboardMesh
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.pointMesh

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

                trav.center.set(randomF(-1f, 1f), randomF(-1f, 1f), randomF(-1f, 1f))
                val t = currentTimeMillis()
                tree.traverse(trav)
                println("In-radius search in ${currentTimeMillis() - t} ms, got ${trav.result.size} points")

                val color = Color.fromHsv(randomF(0f, 360f), 1f, 1f, 1f)
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

        val dist = CubicPointDistribution(5f)
        for (i in 1..100_000) {
            val pt = dist.nextPoint()
            val idx = addPoint {
                position.set(pt)
                color.set(Color.DARK_GRAY)
            }
            points.add(MeshPoint(pt.x, pt.y, pt.z, idx))
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
        val x = randomF(-2.5f, 2.5f)
        val z = randomF(-2.5f, 2.5f)
        val y = randomF(-2.5f, 2.5f)

        mesh.addQuad(Vec3f(x, y, z), Color.DARK_GRAY)
        points.add(MeshPoint(x, y, z, (i-1)*4))
    }

    val t = currentTimeMillis()
    val tree = pointTree(points)
    println("Constructed k-d-Tree with ${points.size} points in ${currentTimeMillis() - t} ms")
    return Pair(mesh, tree)
}

class MeshPoint(x: Float, y: Float, z: Float, val index: Int): Vec3f(x, y, z)
