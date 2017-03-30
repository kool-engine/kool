package de.fabmax.kool.demo

import de.fabmax.kool.platform.Math
import de.fabmax.kool.platform.Platform
import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.*

/**
 * @author fabmax
 */

fun pointDemo(ctx: RenderContext) {
    ctx.scenes += pointScene()
    ctx.scenes += debugOverlay(ctx)

    // Set background color
    ctx.clearColor = color("00323F")
    // Finally run the whole thing
    ctx.run()
}

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

                val vert = data.data[0]
                for (point in trav.result) {
                    for (i in 0..ptVertCnt-1) {
                        vert.index = point.index + i
                        vert.color.set(Color.DARK_GRAY)
                    }
                }

                trav.center.set((Math.random() - 0.5).toFloat() * 2f,
                        (Math.random() - 0.5).toFloat() * 2f, (Math.random() - 0.5).toFloat() * 2f)
                val t = Platform.currentTimeMillis()
                tree.traverse(trav)
                println("In-radius search in ${Platform.currentTimeMillis() - t} ms, got ${trav.result.size} points")

                val color = Color.fromHsv(Math.random().toFloat() * 360f, 1f, 1f, 1f)
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
            setRotation(0f, -30f)
        }

        +transformGroup {
            onRender += {
                rotate(it.deltaT * 45, Vec3f.Y_AXIS)
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
            val x = (Math.random().toFloat() - 0.5f) * 5
            val z = (Math.random().toFloat() - 0.5f) * 5
            val y = (Math.random().toFloat() - 0.5f) * 5

            val idx = addPoint {
                position.set(x, y, z)
                color.set(Color.DARK_GRAY)
            }
            points.add(MeshPoint(x, y, z, idx))
        }
    }
    val t = Platform.currentTimeMillis()
    val tree = pointTree(points)
    println("Constructed k-d-Tree with ${points.size} points in ${Platform.currentTimeMillis() - t} ms")
    return Pair(mesh, tree)
}

fun makeBillboardPointMesh(): Pair<BillboardMesh, KdTree<MeshPoint>> {
    val mesh = BillboardMesh()
    mesh.billboardSize = 3f

    val points: MutableList<MeshPoint> = mutableListOf()
    for (i in 1..100_000) {
        val x = (Math.random().toFloat() - 0.5f) * 5
        val z = (Math.random().toFloat() - 0.5f) * 5
        val y = (Math.random().toFloat() - 0.5f) * 5

        mesh.addQuad(Vec3f(x, y, z), Color.DARK_GRAY)
        points.add(MeshPoint(x, y, z, (i-1)*4))
    }

    val t = Platform.currentTimeMillis()
    val tree = pointTree(points)
    println("Constructed k-d-Tree with ${points.size} points in ${Platform.currentTimeMillis() - t} ms")
    return Pair(mesh, tree)
}

class MeshPoint(x: Float, y: Float, z: Float, val index: Int): Vec3f(x, y, z)
