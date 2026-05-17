package de.fabmax.kool.demo.deferred2

import de.fabmax.kool.math.MutableMat4f
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.ksl.BasicVertexConfig
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.LightingConfig
import de.fabmax.kool.modules.ksl.blocks.*
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shading.AlphaMode
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.VertexLayouts
import de.fabmax.kool.scene.vertexAttrib
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.StructBuffer
import de.fabmax.kool.util.asStorageBuffer

class GbufferPass(
    content: Node,
    camera: Camera,
    initialSize: Vec2i,
    name: String,
    val pipeline: Deferred2Pipeline,
) : OffscreenPass2d(
    drawNode = content,
    attachmentConfig = AttachmentConfig {
        // albedo, a * 255 = emission strength
        addColor(TexFormat.RGBA, filterMethod = FilterMethod.NEAREST)
        // metal, roughness, ao, [empty: a, flags maybe?]
        addColor(TexFormat.RGBA, filterMethod = FilterMethod.NEAREST)
        // encoded normals (view space)
        addColor(TexFormat.R_I32, filterMethod = FilterMethod.NEAREST, clearColor = ClearColorFill(Color.ZERO))
        // object-ids, meta
        addColor(TexFormat.R_I32, filterMethod = FilterMethod.NEAREST)
        defaultDepth()
    },
    initialSize = initialSize,
    name = name
) {
    val albedoEmission get() = colorTextures[0]
    val metalRoughnessAo get() = colorTextures[1]
    val normals get() = colorTextures[2]
    val objectIds get() = colorTextures[3]

    val depth get() = depthTexture!!

    val objModelMats = StructBuffer(ObjModelMatLayout, 1024)
    val objModelMatsGpu = objModelMats.asStorageBuffer()

    init {
        this.camera = camera
        val inverseBuf = MutableMat4f()
        val reprojectBuf = MutableMat4f()
        onAfterCollectDrawCommands += { viewData ->
            viewData.drawQueue.forEach { cmd ->
                (cmd.mesh.shader as? GbufferShader)?.let { gbufferShader ->
                    val id = gbufferShader.objectId
                    objModelMats.set(id) {
                        val prevViewProj = pipeline.prevViewProjMats[id]
                        inverseBuf.set(cmd.modelMatF).invert()
                        reprojectBuf.set(prevViewProj).mul(inverseBuf)
                        set(it.reprojectMat, reprojectBuf)
                        prevViewProj.set(viewData.drawQueue.viewProjMatF).mul(cmd.modelMatF)
                    }
                }
            }
        }

        onRelease { objModelMatsGpu.release() }
    }
}

class GbufferShader(val config: GbufferShaderConfig) : KslShader("deferred2-gbuffer-shader") {
    var objectId: Int by bindUniformInt1("uObjectId", config.objectId)

    init {
        pipelineConfig = PipelineConfig(blendMode = BlendMode.DISABLED, cullMethod = config.cullMethod)
        program.program()
        config.modelCustomizer?.invoke(program)
    }

    private fun KslProgram.program() {
        val camData = cameraData()
        val objectId = interStageInt1("objectId")
        val normalViewSpace = interStageFloat3("normalWorldSpace")
        var tangentViewSpace: KslInterStageVector<KslFloat4, KslFloat1>? = null

        val texCoordBlock: TexCoordAttributeBlock

        vertexStage {
            main {
                val uObjectId = uniformInt1("uObjectId")
                objectId.input set uObjectId + (inInstanceIndex.toInt1() shl 10.const)

                val vertexBlock = vertexTransformBlock(config.vertexCfg) {
                    inLocalPos(vertexAttrib(VertexLayouts.Position.position))
                    inLocalNormal(vertexAttrib(VertexLayouts.Normal.normal))

                    if (config.normalMapCfg.isNormalMapped) {
                        // if normal mapping is enabled, the input vertex data is expected to have a tangent attribute
                        inLocalTangent(vertexAttrib(VertexLayouts.Tangent.tangent))
                    }
                }

                // world position and normal are made available via ports for custom models to modify them
                val worldPos = float3Port("worldPos", vertexBlock.outWorldPos)
                val worldNormal = float3Port("worldNormal", vertexBlock.outWorldNormal)

                val viewPos by camData.viewMat * float4Value(worldPos, 1f)
                outPosition set camData.projMat * viewPos

                normalViewSpace.input set (camData.viewMat * float4Value(worldNormal, 0f)).xyz

                if (config.normalMapCfg.isNormalMapped) {
                    tangentViewSpace = interStageFloat4().apply {
                        input.xyz set (camData.viewMat * float4Value(vertexBlock.outWorldTangent.xyz, 0f)).xyz
                        input.w set vertexBlock.outWorldTangent.w
                    }
                }
                texCoordBlock = texCoordAttributeBlock()
            }
        }
        fragmentStage {
            main {
                // determine main color (albedo)
                val colorBlock = fragmentColorBlock(config.colorCfg)
                val baseColor = float4Port("baseColor", colorBlock.outColor)
                (config.alphaMode as? AlphaMode.Mask)?.let {
                    `if`(baseColor.a lt it.cutOff.const) {
                        discard()
                    }
                }
                val emissionBlock = fragmentPropertyBlock(config.emissionCfg)
                val emissionStrength = float1Port("emissionStrength", emissionBlock.outProperty)

                val vertexNormal = float3Var(normalize(normalViewSpace.output))
                if (config.cullMethod.isBackVisible && config.vertexCfg.isFlipBacksideNormals) {
                    `if`(!inIsFrontFacing) {
                        vertexNormal *= (-1f).const3
                    }
                }

                // do normal map computations (if enabled) and adjust material block input normal accordingly
                val bumpedNormal = if (config.normalMapCfg.isNormalMapped) {
                    val normalMapStrength = fragmentPropertyBlock(config.normalMapCfg.strengthCfg).outProperty
                    normalMapBlock(config.normalMapCfg) {
                        inTangentWorldSpace(tangentViewSpace!!.output)
                        inNormalWorldSpace(vertexNormal)
                        inStrength(normalMapStrength)
                        inTexCoords(texCoordBlock.getTextureCoords())
                    }.outBumpNormal
                } else {
                    vertexNormal
                }

                val normal = float3Port("normal", bumpedNormal)
                val roughness = float1Port("roughness", fragmentPropertyBlock(config.roughnessCfg).outProperty)
                val metallic = float1Port("metallic", fragmentPropertyBlock(config.metallicCfg).outProperty)
                val aoFactor = float1Port("aoFactor", fragmentPropertyBlock(config.aoCfg).outProperty)

                colorOutput(float4Value(baseColor.rgb, emissionStrength), location = 0)
                colorOutput(float4Value(metallic, roughness, aoFactor, 0f.const), location = 1)
                intOutput(int4Value(encodeNormalInt(normal), 0.const, 0.const, 0.const), location = 2)
                intOutput(int4Value(objectId.output, 0.const, 0.const, 0.const), location = 3)
            }
        }
    }
}

class GbufferShaderConfig(builder: Builder) {
    val vertexCfg: BasicVertexConfig = builder.vertexCfg.build()
    val colorCfg: ColorBlockConfig = builder.colorCfg.build()
    val emissionCfg: PropertyBlockConfig = builder.emissionCfg.build()
    val normalMapCfg: NormalMapConfig = builder.normalMapCfg.build()
    val metallicCfg: PropertyBlockConfig = builder.metallicCfg.build()
    val roughnessCfg: PropertyBlockConfig = builder.roughnessCfg.build()
    val aoCfg: PropertyBlockConfig = builder.aoCfg.build()
    // todo val parallaxCfg: ParallaxMapConfig = builder.parallaxCfg.build()
    val lightingCfg: LightingConfig = builder.lightingCfg.build()

    val alphaMode: AlphaMode = builder.alphaMode
    val cullMethod: CullMethod = builder.cullMethod
    val objectId: Int = builder.objectId

    val modelCustomizer: (KslProgram.() -> Unit)? = builder.modelCustomizer

    open class Builder {
        val vertexCfg = BasicVertexConfig.Builder()
        val colorCfg = ColorBlockConfig.Builder("baseColor").constColor(Color.GRAY)
        val emissionCfg = PropertyBlockConfig.Builder("emissionStrength").apply { constProperty(0f) }
        val normalMapCfg = NormalMapConfig.Builder()
        val metallicCfg = PropertyBlockConfig.Builder("metallic").apply { constProperty(0f) }
        val roughnessCfg = PropertyBlockConfig.Builder("roughness").apply { constProperty(0.5f) }
        val aoCfg = PropertyBlockConfig.Builder("ao").apply { constProperty(1f) }
        // todo val parallaxCfg = ParallaxMapConfig.Builder()
        val lightingCfg = LightingConfig.Builder()

        var alphaMode: AlphaMode = AlphaMode.Blend
        var cullMethod: CullMethod = CullMethod.CULL_BACK_FACES

        var objectId = 0

        var modelCustomizer: (KslProgram.() -> Unit)? = null

        fun enableSsao(ssaoMap: Texture2d? = null): Builder {
            lightingCfg.enableSsao(ssaoMap)
            return this
        }

        inline fun metallic(block: PropertyBlockConfig.Builder.() -> Unit) {
            metallicCfg.block()
        }

        inline fun roughness(block: PropertyBlockConfig.Builder.() -> Unit) {
            roughnessCfg.block()
        }

        inline fun ao(block: PropertyBlockConfig.Builder.() -> Unit) {
            aoCfg.block()
        }

        inline fun color(block: ColorBlockConfig.Builder.() -> Unit) {
            colorCfg.colorSources.clear()
            colorCfg.block()
        }

        inline fun emission(block: PropertyBlockConfig.Builder.() -> Unit) {
            emissionCfg.block()
        }

        inline fun lighting(block: LightingConfig.Builder.() -> Unit) {
            lightingCfg.block()
        }

        inline fun normalMapping(block: NormalMapConfig.Builder.() -> Unit) {
            normalMapCfg.block()
        }

//        inline fun parallaxMapping(block: ParallaxMapConfig.Builder.() -> Unit) {
//            parallaxCfg.block()
//        }

        inline fun vertices(block: BasicVertexConfig.Builder.() -> Unit) {
            vertexCfg.block()
        }

        open fun build() = GbufferShaderConfig(this)
    }
}

fun gbufferShader(objectId: Int, block: GbufferShaderConfig.Builder.() -> Unit): GbufferShader {
    val cfg = GbufferShaderConfig.Builder().apply{
        this.objectId = objectId
        block()
    }.build()
    return GbufferShader(cfg)
}
