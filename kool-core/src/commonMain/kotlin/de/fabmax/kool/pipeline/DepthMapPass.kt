package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.OrthographicCamera
import de.fabmax.kool.scene.PerspectiveCamera
import de.fabmax.kool.util.Color


open class DepthMapPass(drawNode: Node, width: Int, height: Int = width, colorFormat: TexFormat = TexFormat.R) : OffscreenRenderPass2D(drawNode, width, height, colorFormat = colorFormat) {
    private val shadowPipelines = mutableMapOf<Long, Pipeline?>()

    /**
     * Cull method to use for depth map rendering. If null (the default) the original cull method of meshes is used.
     */
    var cullMethod: CullMethod? = null

    init {
        type = Type.DEPTH
        colorBlend = false

        onAfterCollectDrawCommands += { ctx ->
            // replace regular object shaders by cheaper shadow versions
            drawQueue.commands.forEach {
                it.pipeline = getShadowPipeline(it.mesh, ctx)
            }
        }
    }

    protected open fun getShadowPipeline(mesh: Mesh, ctx: KoolContext): Pipeline? {
        val culling = this.cullMethod ?: mesh.getPipeline(ctx)?.cullMethod ?: CullMethod.CULL_BACK_FACES
        return shadowPipelines.getOrPut(mesh.id) { createPipeline(mesh, culling, ctx) }
    }

    protected open fun createPipeline(mesh: Mesh, culling: CullMethod, ctx: KoolContext): Pipeline? {
        // create a minimal dummy shader for each attribute set
        val shadowShader = ModeledShader(ShaderModel("shadow shader").apply {
            vertexStage { positionOutput = simpleVertexPositionNode().outPosition }
            fragmentStage { colorOutput = ShaderNodeIoVar(ModelVar4fConst(Vec4f(1f))) }
        })
        val pipelineBuilder = Pipeline.Builder().apply { cullMethod = culling }
        return shadowShader.createPipeline(mesh, pipelineBuilder, ctx)
    }

    override fun dispose(ctx: KoolContext) {
        super.dispose(ctx)
        shadowPipelines.values.filterNotNull().forEach { ctx.disposePipeline(it) }
    }
}

class LinearDepthMapPass(drawNode: Node, width: Int, height: Int = width) : DepthMapPass(drawNode, width, height, TexFormat.R_F16) {

    init {
        onAfterCollectDrawCommands += {
            camera.let {
                val far = when(it) {
                    is PerspectiveCamera -> it.clipFar
                    is OrthographicCamera -> it.far
                    else -> 1e9f
                }
                clearColor = Color(far, 0f, 0f, 1f)
            }
        }
    }

    override fun createPipeline(mesh: Mesh, culling: CullMethod, ctx: KoolContext): Pipeline? {
        val shadowShader = ModeledShader(ShaderModel("shadow shader").apply {
            vertexStage { positionOutput = simpleVertexPositionNode().outPosition }
            fragmentStage {
                val linDepth = addNode(LinearDepthNode(stage))
                colorOutput = linDepth.outColor
            }
        })
        val pipelineBuilder = Pipeline.Builder().apply { cullMethod = culling }
        return shadowShader.createPipeline(mesh, pipelineBuilder, ctx)
    }

    private class LinearDepthNode(graph: ShaderGraph) : ShaderNode("linearDepth", graph) {
        val outColor = ShaderNodeIoVar(ModelVar4f("linearDepth"), this)

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("""
                float d = gl_FragCoord.z / gl_FragCoord.w;
                ${outColor.declare()} = vec4(d, 0.0, 0.0, 1.0);
            """)
        }
    }
}

class NormalLinearDepthMapPass(drawNode: Node, width: Int, height: Int = width) : DepthMapPass(drawNode, width, height, TexFormat.RGBA_F16) {

    init {
        name = "NormalLinearDepthMapPass"

        onAfterCollectDrawCommands += {
            camera.let {
                val far = when(it) {
                    is PerspectiveCamera -> it.clipFar
                    is OrthographicCamera -> it.far
                    else -> 1e9f
                }
                clearColor = Color(0f, 1f, 0f, far)
            }
        }
    }

    override fun createPipeline(mesh: Mesh, culling: CullMethod, ctx: KoolContext): Pipeline? {
        if (!mesh.geometry.hasAttribute(Attribute.NORMALS)) {
            return null
        }

        val shadowShader = ModeledShader(ShaderModel("shadow shader").apply {
            val ifNormals: StageInterfaceNode
            vertexStage {
                val mvpNode = mvpNode()

                val modelViewMat = multiplyNode(mvpNode.outModelMat, mvpNode.outViewMat).output
                val nrm = transformNode(attrNormals().output, modelViewMat, 0f)
                ifNormals = stageInterfaceNode("ifNormals", nrm.output)

                positionOutput = vertexPositionNode(attrPositions().output, mvpNode.outMvpMat).outPosition
            }
            fragmentStage {
                val linDepth = addNode(NormalLinearDepthNode(ifNormals.output, stage))
                colorOutput = linDepth.outColor
            }
        })
        val pipelineBuilder = Pipeline.Builder().apply { cullMethod = culling }
        return shadowShader.createPipeline(mesh, pipelineBuilder, ctx)
    }

    private class NormalLinearDepthNode(val inNormals: ShaderNodeIoVar, graph: ShaderGraph) : ShaderNode("normalLinearDepth", graph) {
        val outColor = ShaderNodeIoVar(ModelVar4f("normaLinearDepth"), this)

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("""
                float d = gl_FragCoord.z / gl_FragCoord.w;
                ${outColor.declare()} = vec4(normalize(${inNormals.ref3f()}) * 0.5 + 0.5, d);
            """)
        }
    }
}
