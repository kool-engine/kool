package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.TextureProps
import de.fabmax.kool.assetTexture
import de.fabmax.kool.gl.GL_LINEAR
import de.fabmax.kool.gl.GL_REPEAT
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.scene.group
import de.fabmax.kool.scene.textureMesh
import de.fabmax.kool.shading.ColorModel
import de.fabmax.kool.shading.LightModel
import de.fabmax.kool.shading.basicShader
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ShadowMap

fun makeGroundGrid(cells: Int, ctx: KoolContext, shadows: ShadowMap? = null, y: Float = 0f) = group {
    val groundExt = cells / 2

    +textureMesh(isNormalMapped = true) {
        isCastingShadow = false
        generator = {
            withTransform {
                rotate(-90f, Vec3f.X_AXIS)
                color = Color.LIGHT_GRAY.withAlpha(0.2f)
                rect {
                    origin.set(-groundExt.toFloat(), -groundExt.toFloat(), y)
                    width = groundExt * 2f
                    height = groundExt * 2f

                    val uv = groundExt.toFloat() / 2
                    texCoordUpperLeft.set(-uv, -uv)
                    texCoordUpperRight.set(uv, -uv)
                    texCoordLowerLeft.set(-uv, uv)
                    texCoordLowerRight.set(uv, uv)
                }
            }
            meshData.generateTangents()
        }
        shader = basicShader {
            lightModel = LightModel.PHONG_LIGHTING
            colorModel = ColorModel.TEXTURE_COLOR
            shadowMap = shadows
            isNormalMapped = true

            specularIntensity = 0.25f

            val props = TextureProps("ground_nrm.png", GL_LINEAR, GL_REPEAT)
            normalMap = assetTexture(props)
            val colorProps = TextureProps("ground_color.png", GL_LINEAR, GL_REPEAT)
            texture = assetTexture(colorProps)
        }
    }
}