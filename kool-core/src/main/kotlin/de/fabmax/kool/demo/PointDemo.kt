package de.fabmax.kool.demo

import de.fabmax.kool.platform.Math
import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.scene
import de.fabmax.kool.scene.sphericalInputTransform
import de.fabmax.kool.util.*

/**
 * @author fabmax
 */

fun pointDemo(ctx: RenderContext) {
    ctx.scenes += pointScene()
    // Set background color
    ctx.clearColor = color("00323F")
    // Finally run the whole thing
    ctx.run()
}

fun pointScene(): Scene {
    val pointMesh = makePointMesh()
    //val pointMesh = makeBillboardPointMesh()
    val data = pointMesh.meshData
    val ptVertCnt = if (pointMesh is BillboardMesh) 4 else 1

    var frameCnt = 30
    var highlight = false

    // Create scene contents
    val scene = scene {
        onRender += {
            // change color of middle sphere every 30 frames
            // iterating over all vertices is super slow, but anyway...
            if (--frameCnt == 0) {
                frameCnt = 30
                highlight = !highlight
                val color = if (highlight) Color.WHITE else Color.RED

                val vert = data.data[20000 * ptVertCnt]
                for (i in 1..(10000*ptVertCnt)) {
                    vert.color.set(color)
                    vert.index++
                }
                data.isSyncRequired = true
            }
        }

        // Add a mouse-controlled camera manipulator (actually a specialized TransformGroup)
        +sphericalInputTransform { +camera }
        +pointMesh
    }
    return scene
}

fun makePointMesh(): Mesh {
    return pointMesh {
        pointSize = 3f

        for (ox in -20..20 step 10) {
            for (p in 1..100) {
                for (t in 1..100) {
                    val x = ox + Math.cos(Math.PI * 2 * p / 100) * Math.sin(Math.PI * t / 100) * 4
                    val z = Math.sin(Math.PI * 2 * p / 100) * Math.sin(Math.PI * t / 100) * 4
                    val y = Math.cos(Math.PI * t / 100) * 4

                    addPoint {
                        position.set(x.toFloat(), y.toFloat(), z.toFloat())
                        color.set(Color.RED)
                    }
                }
            }
        }
    }
}

fun makeBillboardPointMesh(): BillboardMesh {
    val pointMesh = BillboardMesh()
    pointMesh.billboardSize = 3f

    // create 5 point spheres, 10000 points each
    for (ox in -20..20 step 10) {
        for (p in 1..100) {
            for (t in 1..100) {
                val x = ox + Math.cos(Math.PI * 2 * p / 100) * Math.sin(Math.PI * t / 100) * 4
                val z = Math.sin(Math.PI * 2 * p / 100) * Math.sin(Math.PI * t / 100) * 4
                val y = Math.cos(Math.PI * t / 100) * 4

                pointMesh.addQuad(Vec3f(x.toFloat(), y.toFloat(), z.toFloat()), Color.RED)
            }
        }
    }

    return pointMesh
}
