package de.fabmax.kool.demo

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.scene.colorMesh
import de.fabmax.kool.scene.group
import de.fabmax.kool.shading.ColorModel
import de.fabmax.kool.shading.LightModel
import de.fabmax.kool.shading.basicShader
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ShadowMap
import de.fabmax.kool.util.lineMesh

fun makeGroundGrid(cells: Int, shadows: ShadowMap? = null, y: Float = 0f) = group {
    val groundExt = cells / 2

    +colorMesh {
        isCastingShadow = false
        generator = {
            withTransform {
                rotate(-90f, Vec3f.X_AXIS)
                color = Color.LIGHT_GRAY.withAlpha(0.2f)
                rect {
                    origin.set(-groundExt.toFloat(), -groundExt.toFloat(), y)
                    width = groundExt * 2f
                    height = groundExt * 2f
                }
            }
        }
        shader = basicShader {
            lightModel = LightModel.PHONG_LIGHTING
            colorModel = ColorModel.VERTEX_COLOR
            shadowMap = shadows
        }
    }
    +lineMesh {
        isCastingShadow = false
        isXray = true
        for (i in -groundExt..groundExt) {
            val color = Color.LIGHT_GRAY.withAlpha(0.5f)
            addLine(Vec3f(i.toFloat(), y, -groundExt.toFloat()), color,
                    Vec3f(i.toFloat(), y, groundExt.toFloat()), color)
            addLine(Vec3f(-groundExt.toFloat(), y, i.toFloat()), color,
                    Vec3f(groundExt.toFloat(), y, i.toFloat()), color)
        }
        shader = basicShader {
            lightModel = LightModel.NO_LIGHTING
            colorModel = ColorModel.VERTEX_COLOR
            shadowMap = shadows
        }
    }
}