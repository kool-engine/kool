package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.*
import de.fabmax.kool.pipeline.FilterMethod
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.pipeline.TextureProps
import de.fabmax.kool.pipeline.shading.Albedo
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MeshBuilder
import de.fabmax.kool.util.SimpleShadowMap
import de.fabmax.kool.util.ao.AoPipeline
import de.fabmax.kool.util.gltf.loadGltfModel
import de.fabmax.kool.util.ibl.BrdfLutPass
import de.fabmax.kool.util.ibl.IrradianceMapPass
import de.fabmax.kool.util.ibl.ReflectionMapPass
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun gltfTest(ctx: KoolContext) = scene {
    +orbitInputTransform {
        // Set some initial rotation so that we look down on the scene
        setMouseRotation(0f, -30f)
        // Add camera to the transform group
        +camera
        zoom = 5.0


        translation.set(0.0, 0.5, 0.0)
        onUpdate += { _, _ ->
            verticalRotation -= ctx.deltaT * 3f
        }
    }

    lighting.lights.clear()
    lighting.lights.add(Light().apply {
        val pos = Vec3f(7f, 8f, 8f)
        val lookAt = Vec3f.ZERO
        setSpot(pos, lookAt.subtract(pos, MutableVec3f()).norm(), 25f)
        setColor(Color.WHITE.mix(Color.MD_AMBER, 0.3f).toLinear(), 500f)
    })
    lighting.lights.add(Light().apply {
        val pos = Vec3f(-7f, 8f, 8f)
        val lookAt = Vec3f.ZERO
        setSpot(pos, lookAt.subtract(pos, MutableVec3f()).norm(), 25f)
        setColor(Color.WHITE.mix(Color.MD_AMBER, 0.3f).toLinear(), 500f)
    })

    val hdriTexProps = TextureProps(minFilter = FilterMethod.NEAREST, magFilter = FilterMethod.NEAREST, mipMapping = true)
    ctx.assetMgr.loadAndPrepareTexture("${Demo.envMapBasePath}/shanghai_bund_1k.rgbe.png", hdriTexProps) { tex ->
        makeScene(tex, ctx)
    }
}

private fun Scene.makeScene(hdri: Texture, ctx: KoolContext) {
    val irrMapPass = IrradianceMapPass(this, hdri)
    val reflMapPass = ReflectionMapPass(this, hdri)
    val brdfLutPass = BrdfLutPass(this)

    val shadows = listOf(
            SimpleShadowMap(this, 0, 2048),
            SimpleShadowMap(this, 1, 2048))
    val aoPipeline = AoPipeline.createForward(this)

    onDispose += {
        hdri.dispose()
    }

    +transformGroup {
        +textureMesh(isNormalMapped = true) {
            generate {
                roundCylinder(4.1f, 0.2f)
            }
            pipelineLoader = pbrShader {
                shadowMaps += shadows
                isScrSpcAmbientOcclusion = true
                scrSpcAmbientOcclusionMap = aoPipeline.aoMap
                albedoSource = Albedo.TEXTURE_ALBEDO

                isAmbientOcclusionMapped = true
                isNormalMapped = true
                isRoughnessMapped = true

                albedoMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/Fabric030/Fabric030_1K_Color2.jpg") }
                normalMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/Fabric030/Fabric030_1K_Normal.jpg") }
                roughnessMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/Fabric030/Fabric030_1K_Roughness.jpg") }
                ambientOcclusionMap = Texture { it.loadTextureData("${Demo.pbrBasePath}/Fabric030/Fabric030_1K_AmbientOcclusion.jpg") }

                isImageBasedLighting = true
                irradianceMap = irrMapPass.colorTextureCube
                reflectionMap = reflMapPass.colorTextureCube
                brdfLut = brdfLutPass.colorTexture

                onDispose += {
                    albedoMap?.dispose()
                    normalMap?.dispose()
                    roughnessMap?.dispose()
                    ambientOcclusionMap?.dispose()
                }
            }
        }

        ctx.assetMgr.loadGltfModel("${Demo.modelBasePath}/camera.glb") { gltf ->
            gltf?.let {
                val model = it.makeModel(generateNormals = true) {
                    shadowMaps += shadows
                    scrSpcAmbientOcclusionMap = aoPipeline.aoMap
                    isScrSpcAmbientOcclusion = true

                    isImageBasedLighting = true
                    irradianceMap = irrMapPass.colorTextureCube
                    reflectionMap = reflMapPass.colorTextureCube
                    brdfLut = brdfLutPass.colorTexture
                }
                model.scale(20f, 20f, 20f)
                +model
            }
        }

        onUpdate += { _, ctx ->
            setIdentity()
            rotate(ctx.time * 3 - 45, Vec3d.Y_AXIS)
        }
    }

    this += Skybox(reflMapPass.colorTextureCube, 1f)
}

private fun MeshBuilder.roundCylinder(radius: Float, height: Float) {
    val nCorner = 20
    val cornerR = height / 2
    val cornerPts = mutableListOf<Vec3f>()
    for (i in 0..nCorner) {
        val a = (PI / nCorner * i).toFloat()
        val x = sin(a) * cornerR + radius
        val y = cos(a) * cornerR - cornerR
        cornerPts += Vec3f(x, y, 0f)
    }

    val uvScale = 0.3f
    val nCyl = 100
    var firstI = 0
    for (i in 0 .. nCyl) {
        val a = (PI / nCyl * i * 2).toFloat()
        cornerPts.forEachIndexed { ci, cpt ->
            val uv = MutableVec2f(radius + ci.toFloat() / cornerPts.size * PI.toFloat() * cornerR, 0f)
            uv.scale(uvScale)
            uv.rotate(a.toDeg())
            val pt = cpt.rotate(a.toDeg(), Vec3f.Y_AXIS, MutableVec3f())
            val iv = vertex(pt, Vec3f.ZERO, uv)
            if (i > 0 && ci > 0) {
                geometry.addTriIndices(iv - 1, iv - cornerPts.size - 1, iv - cornerPts.size)
                geometry.addTriIndices(iv, iv - 1, iv - cornerPts.size)
            }
            if (i == 0 && ci == 0) {
                firstI = iv
            }
        }
    }
    val firstIBot = firstI + cornerPts.size - 1
    for (i in 2 .. nCyl) {
        geometry.addTriIndices(firstI, firstI + ((i - 1) * cornerPts.size), firstI + (i * cornerPts.size))
        geometry.addTriIndices(firstIBot, firstIBot + (i * cornerPts.size), firstIBot + ((i - 1) * cornerPts.size))
    }
    geometry.generateNormals()

}