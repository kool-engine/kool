package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.FilterMethod
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.pipeline.TextureProps
import de.fabmax.kool.pipeline.shading.Albedo
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.SimpleShadowMap
import de.fabmax.kool.util.ao.AoPipeline
import de.fabmax.kool.util.gltf.loadGltfModel
import de.fabmax.kool.util.ibl.BrdfLutPass
import de.fabmax.kool.util.ibl.IrradianceMapPass
import de.fabmax.kool.util.ibl.ReflectionMapPass

fun gltfTest(ctx: KoolContext) = scene {
    +orbitInputTransform {
        // Set some initial rotation so that we look down on the scene
        setMouseRotation(0f, -35f)
        // Add camera to the transform group
        +camera
        zoom = 8.0


        translation.set(0.0, 1.0, 0.0)
        onUpdate += { _, _ ->
            verticalRotation += ctx.deltaT * 3f
        }
    }

    lighting.singleLight {
        val pos = Vec3f(7f, 10f, 8f)
        val lookAt = Vec3f.ZERO
        setSpot(pos, lookAt.subtract(pos, MutableVec3f()).norm(), 60f)
        setColor(Color.WHITE.mix(Color.MD_AMBER, 0.3f).toLinear(), 500f)
    }

    val hdriTexProps = TextureProps(minFilter = FilterMethod.NEAREST, magFilter = FilterMethod.NEAREST, mipMapping = true)
    ctx.assetMgr.loadAndPrepareTexture("${Demo.envMapBasePath}/mossy_forest_1k.rgbe.png", hdriTexProps) { tex ->
        makeScene(tex, ctx)
    }
}

private fun Scene.makeScene(hdri: Texture, ctx: KoolContext) {
    val irrMapPass = IrradianceMapPass(this, hdri)
    val reflMapPass = ReflectionMapPass(this, hdri)
    val brdfLutPass = BrdfLutPass(this)

    val shadows = listOf(SimpleShadowMap(this, 0, 2048))
    val aoPipeline = AoPipeline.createForward(this)

    +colorMesh {
        generate {
            rotate(-90f, Vec3f.X_AXIS)
            color = Color.WHITE
            rect {
                size.set(20f, 20f)
                origin.set(size.x, size.y, 0f).scale(-0.5f)
            }
        }
        pipelineLoader = pbrShader {
            shadowMaps += shadows
            isScrSpcAmbientOcclusion = true
            scrSpcAmbientOcclusionMap = aoPipeline.aoMap
            albedoSource = Albedo.VERTEX_ALBEDO

            isImageBasedLighting = true
            irradianceMap = irrMapPass.colorTextureCube
            reflectionMap = reflMapPass.colorTextureCube
            brdfLut = brdfLutPass.colorTexture
        }
    }

    ctx.assetMgr.loadGltfModel("${Demo.modelBasePath}/camera.glb") { gltf ->
        gltf?.let {
            val model = it.makeModel {
                shadowMaps += shadows
                scrSpcAmbientOcclusionMap = aoPipeline.aoMap
                isScrSpcAmbientOcclusion = true

                isImageBasedLighting = true
                irradianceMap = irrMapPass.colorTextureCube
                reflectionMap = reflMapPass.colorTextureCube
                brdfLut = brdfLutPass.colorTexture
            }
            model.scale(40f, 40f, 40f)
            +model
        }
    }

    this += Skybox(reflMapPass.colorTextureCube, 1f)
}