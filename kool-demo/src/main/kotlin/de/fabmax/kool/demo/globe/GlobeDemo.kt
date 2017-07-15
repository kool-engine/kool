package de.fabmax.kool.demo.globe

import de.fabmax.kool.assetTexture
import de.fabmax.kool.platform.Math
import de.fabmax.kool.scene.*
import de.fabmax.kool.shading.BasicShader
import de.fabmax.kool.shading.ColorModel
import de.fabmax.kool.shading.LightModel
import de.fabmax.kool.shading.basicShader
import de.fabmax.kool.util.MutableVec3f
import de.fabmax.kool.util.Vec2f
import de.fabmax.kool.util.Vec3f

/**
 * Globe demo: Show an OSM map on a sphere.
 */

fun globeScene(): Scene = scene {
    +sphericalInputTransform {
        setMouseRotation(20f, -30f)
        panMethod = CameraOrthogonalPan()
        +camera
    }

    val z = 4
    val n = (1 shl z) - 1
    for (x in 0..n) {
        for (y in 0..n) {
            +makeTileMesh(x, y, z)
        }
    }
}

fun makeTileMesh(tx: Int, ty: Int, tz: Int): Mesh {
    return textureMesh("$tz/$tx/$ty") {
        generator = {
            val lonW = tx / (1 shl tz).toFloat() * 360f - 180f
            val lonE = (tx + 1f) / (1 shl tz).toFloat() * 360f - 180f
            val latN = 90f - Math.toDeg(Math.atan(Math.sinh(
                    Math.PI.toFloat() - ty / (1 shl tz).toFloat() * 2f * Math.PI.toFloat())))
            val latS = 90f - Math.toDeg(Math.atan(Math.sinh(
                    Math.PI.toFloat() - (ty + 1f) / (1 shl tz).toFloat() * 2f * Math.PI.toFloat())))

            val steps = 10
            var prevIndices = IntArray(steps + 1)
            var rowIndices = IntArray(steps + 1)
            for (row in 0..steps) {
                val tmp = prevIndices
                prevIndices = rowIndices
                rowIndices = tmp

                val theta = Math.toRad(latS + (latN - latS) * row / steps)
                val r = Math.sin(theta)
                val y = Math.cos(theta)
                for (i in 0..steps) {
                    val phi = Math.toRad(lonW + (lonE - lonW) * i / steps)
                    val x = Math.cos(-phi) * r
                    val z = Math.sin(-phi) * r
                    val uv = Vec2f(i.toFloat() / steps, 1f - row.toFloat() / steps)

                    rowIndices[i] = vertex(Vec3f(x, y, z), MutableVec3f(x, y, z).norm(), uv)

                    if (i > 0 && row > 0) {
                        meshData.addTriIndices(prevIndices[i - 1], rowIndices[i], rowIndices[i - 1])
                        meshData.addTriIndices(prevIndices[i - 1], prevIndices[i], rowIndices[i])
                    }
                }
            }
        }
        shader = basicShader {
            colorModel = ColorModel.TEXTURE_COLOR
            lightModel = LightModel.NO_LIGHTING
        }
        (shader as BasicShader).texture = assetTexture("tiles/${tz}_${tx}_$ty.png")
    }
}
