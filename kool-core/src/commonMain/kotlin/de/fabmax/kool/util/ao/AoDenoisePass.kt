package de.fabmax.kool.util.ao

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.mesh
import de.fabmax.kool.util.Color

class AoDenoisePass(aoPass: AmbientOcclusionPass, depthTexture: Texture, depthComponent: String) :
        OffscreenRenderPass2d(Group(), renderPassConfig {
            name = "AoDenoisePass"
            setSize(aoPass.config.width, aoPass.config.height)
            addColorTexture(TexFormat.R)
            clearDepthTexture()
        }) {

    private val uRadius = Uniform1f(1f, "uRadius")

    var radius: Float
        get() = uRadius.value
        set(value) { uRadius.value = value }

    var clearAndDisable = false
    private val denoiseMesh: Mesh
    private val clearMesh: Mesh

    init {
        clearColor = Color.BLACK

        denoiseMesh = mesh(listOf(Attribute.POSITIONS, Attribute.TEXTURE_COORDS)) {
            generate {
                rect {
                    size.set(1f, 1f)
                    mirrorTexCoordsY()
                }
            }

            val model = ShaderModel("AoDenoisePass").apply {
                val ifTexCoords: StageInterfaceNode
                vertexStage {
                    ifTexCoords = stageInterfaceNode("ifTexCoords", attrTexCoords().output)
                    positionOutput = fullScreenQuadPositionNode(attrTexCoords().output).outQuadPos
                }
                fragmentStage {
                    val noisyAo = textureNode("noisyAo")
                    val depth = textureNode("depth")
                    val radius = pushConstantNode1f(uRadius)
                    val blurNd = addNode(BlurNode(noisyAo, depth, depthComponent, stage))
                    blurNd.inScreenPos = ifTexCoords.output
                    blurNd.radius = radius.output
                    colorOutput(blurNd.outColor)
                }
            }
            shader = ModeledShader(model).apply {
                onPipelineSetup += { builder, _, _ ->
                    builder.blendMode = BlendMode.DISABLED
                    builder.depthTest = DepthCompareOp.DISABLED
                }
                onPipelineCreated += { _, _, _ ->
                    model.findNode<TextureNode>("noisyAo")!!.sampler.texture = aoPass.colorTexture
                    model.findNode<TextureNode>("depth")!!.sampler.texture = depthTexture
                }
            }
        }

        clearMesh = mesh(listOf(Attribute.POSITIONS, Attribute.TEXTURE_COORDS)) {
            isVisible = false
            generate {
                rect {
                    size.set(1f, 1f)
                    mirrorTexCoordsY()
                }
            }

            val model = ShaderModel("ClearAoDenoisePass").apply {
                vertexStage {
                    // fixme: currently Vulkan pipeline creation fails if a shader has no uniform inputs, add mvpNode as a dummy
                    mvpNode()
                    positionOutput = fullScreenQuadPositionNode(attrTexCoords().output).outQuadPos
                }
                fragmentStage {
                    colorOutput(constVec4f(Color.WHITE))
                }
            }
            shader = ModeledShader(model).apply {
                onPipelineSetup += { builder, _, _ ->
                    builder.blendMode = BlendMode.DISABLED
                    builder.depthTest = DepthCompareOp.DISABLED
                }
            }
        }

        (drawNode as Group).apply {
            +denoiseMesh
            +clearMesh
        }
    }

    override fun update(ctx: KoolContext) {
        if (clearAndDisable) {
            resize(1, 1, ctx)
            clearAndDisable = false
            denoiseMesh.isVisible = false
            clearMesh.isVisible = true

            ctx.runDelayed(5) {
                isEnabled = false
                denoiseMesh.isVisible = true
                clearMesh.isVisible = false
            }
        }

        super.update(ctx)
    }

    override fun dispose(ctx: KoolContext) {
        drawNode.dispose(ctx)
        super.dispose(ctx)
    }

    private class BlurNode(val noisyAo: TextureNode, val depth: TextureNode,
                                 val depthComponent: String, shaderGraph: ShaderGraph) :
            ShaderNode("blurNode", shaderGraph) {

        var inScreenPos = ShaderNodeIoVar(ModelVar2fConst(Vec2f.ZERO))
        var radius = ShaderNodeIoVar(ModelVar1fConst(1f))

        val outColor = ShaderNodeIoVar(ModelVar4f("colorOut"), this)

        override fun setup(shaderGraph: ShaderGraph) {
            dependsOn(noisyAo)
            dependsOn(depth)
            dependsOn(inScreenPos, radius)
            super.setup(shaderGraph)
        }

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("""
                int blurSize = 4;
                vec2 texelSize = 1.0 / vec2(textureSize(${noisyAo.name}, 0));
                float depthOri = ${generator.sampleTexture2d(depth.name, inScreenPos.ref2f())}.$depthComponent;
                float depthThreshold = ${radius.ref1f()} * 0.1;
                
                float result = 0.0;
                float weight = 0.0;
                vec2 hlim = vec2(float(-blurSize) * 0.5 + 0.5);
                for (int x = 0; x < blurSize; x++) {
                    for (int y = 0; y < blurSize; y++) {
                        vec2 uv = ${inScreenPos.ref2f()} + (hlim + vec2(float(x), float(y))) * texelSize;
                        float w = 1.0 - step(depthThreshold, abs(${generator.sampleTexture2d(depth.name, "uv")}.$depthComponent - depthOri)) * 0.99;
                        
                        result += ${generator.sampleTexture2d(noisyAo.name, "uv")}.r * w;
                        weight += w;
                    }
                }
                result /= weight;
                ${outColor.declare()} = vec4(result, result, result, 1.0);
            """)
        }
    }
}