package de.fabmax.kool.demo

import de.fabmax.kool.platform.Math
import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.scene.group
import de.fabmax.kool.scene.sphericalInputTransform
import de.fabmax.kool.util.*

/**
 * @author fabmax
 */

fun pointDemo(ctx: RenderContext) {

    val pointMesh = BillboardMesh()
    val data = pointMesh.meshData
    pointMesh.billboardSize = 3f

    // create 5 point spheres, 10000 points each
    for (ox in -20..20 step 10) {
        for (p in 1..100) {
            for (t in 1..100) {
                val x = ox + Math.cos(Math.PI * 2 * p / 100) * Math.sin(Math.PI * t / 100) * 4
                val z = Math.sin(Math.PI * 2 * p / 100) * Math.sin(Math.PI * t / 100) * 4
                val y = Math.cos(Math.PI * t / 100) * 4

                pointMesh.addPoint(Vec3f(x.toFloat(), y.toFloat(), z.toFloat()), Color.RED)
            }
        }
    }

    var cnt = 30
    var highlight = false

    // Create scene contents
    ctx.scene.root = group {
        onRender += {
            // change color of middle sphere every 30 frames
            if (--cnt == 0) {
                cnt = 30
                highlight = !highlight
                val color = if (highlight) Color.WHITE else Color.RED

                val vert = data.data[80000]
                for (i in 1..40000) {
                    vert.color.set(color)
                    vert.index++
                }
                data.isSyncRequired = true
            }
        }

        // Add a mouse-controlled camera manipulator (actually a specialized TransformGroup)
        +sphericalInputTransform { +ctx.scene.camera }
        +pointMesh
    }

    // Set background color
    ctx.clearColor = color("00323F")
    // Finally run the whole thing
    ctx.run()
}