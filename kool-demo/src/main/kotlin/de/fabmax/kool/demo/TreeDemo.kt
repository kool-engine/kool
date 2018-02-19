package de.fabmax.kool.demo

import de.fabmax.kool.TextureProps
import de.fabmax.kool.assetTexture
import de.fabmax.kool.currentTimeMillis
import de.fabmax.kool.gl.GL_LINEAR
import de.fabmax.kool.gl.GL_REPEAT
import de.fabmax.kool.scene.*
import de.fabmax.kool.shading.ColorModel
import de.fabmax.kool.shading.LightModel
import de.fabmax.kool.shading.basicShader
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.CascadedShadowMap
import de.fabmax.kool.util.Vec3f


/**
 * @author fabmax
 */

fun treeScene(): Scene = scene {
    defaultShadowMap = CascadedShadowMap.defaultCascadedShadowMap3()

    +makeGroundGrid(40, defaultShadowMap)

    // generate tree structure
    val treeGen = TreeGenerator()
    treeGen.generate()

    // generate tree trunk mesh
    +textureMesh(isNormalMapped = true) {
        generator = {
            val t = currentTimeMillis()
            treeGen.buildTrunkMesh(this)
            println("Generated ${meshData.numIndices/3} trunk triangles, took ${currentTimeMillis() - t} ms")
        }
        shader = basicShader {
            colorModel = ColorModel.TEXTURE_COLOR
            lightModel = LightModel.PHONG_LIGHTING
            shadowMap = defaultShadowMap
            isNormalMapped = true
            specularIntensity = 0.25f

            val textureProps = TextureProps("tree_bark.png", GL_LINEAR, GL_REPEAT, 16)
            val nrmMapProps = TextureProps("tree_bark_nrm.png", GL_LINEAR, GL_REPEAT, 16)
            texture = assetTexture(textureProps)
            normalMap = assetTexture(nrmMapProps)
        }
    }

    // generate tree leaf mesh
    +textureMesh {
        generator = {
            val t = currentTimeMillis()
            treeGen.buildLeafMesh(this)
            println("Generated ${meshData.numIndices/3} leaf triangles, took ${currentTimeMillis() - t} ms")
        }
        shader = basicShader {
            colorModel = ColorModel.TEXTURE_COLOR
            lightModel = LightModel.PHONG_LIGHTING
            shadowMap = defaultShadowMap
            specularIntensity = 0.1f
            isDiscardTranslucent = true
            texture = assetTexture("leaf.png")
        }
    }

    // Add a mouse-controlled camera manipulator (actually a specialized TransformGroup)
    +sphericalInputTransform {
        +camera
        setMouseRotation(0f, -30f)
        minZoom = 1f
        maxZoom = 25f
        // panning / camera translation is limited to a certain area
        translationBounds = BoundingBox(Vec3f(-10f, -10f, -10f), Vec3f(10f, 10f, 10f))

        zoomMethod = SphericalInputTransform.ZoomMethod.ZOOM_CENTER
        rightDragMethod = SphericalInputTransform.DragMethod.NONE

        translate(0f, 2.5f, 0f)
    }
}