package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.Color


open class DepthMapPass(drawNode: Node, width: Int, height: Int = width, setup: Setup = defaultSetup()) :
        OffscreenRenderPass2d(drawNode, width, height, setup) {
    private val shadowPipelines = mutableMapOf<Long, Pipeline?>()

    /**
     * Cull method to use for depth map rendering. If null (the default) the original cull method of meshes is used.
     */
    var cullMethod: CullMethod? = null

    init {
        type = Type.DEPTH
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
            vertexStage {
                var mvpMat = premultipliedMvpNode().outMvpMat
                if (mesh.instances != null) {
                    mvpMat = multiplyNode(mvpMat, instanceAttrModelMat().output).output
                }
                if (mesh.skin != null) {
                    val skinNd = skinTransformNode(attrJoints().output, attrWeights().output)
                    mvpMat = multiplyNode(mvpMat, skinNd.outJointMat).output
                }
                var localPos = attrPositions().output
                mesh.morphWeights?.let { weights ->
                    val morphAttribs = mesh.geometry.getMorphAttributes()
                    if (morphAttribs.isNotEmpty()) {
                        val morphWeights = morphWeightsNode(weights.size)
                        morphAttribs.filter { it.name.startsWith(Attribute.POSITIONS.name) }.forEach { attrib ->
                            val weight = getMorphWeightNode(morphAttribs.indexOf(attrib), morphWeights)
                            val posDisplacement = multiplyNode(attributeNode(attrib).output, weight.outWeight)
                            localPos = addNode(localPos, posDisplacement.output).output
                        }
                    }
                }
                positionOutput = vec4TransformNode(localPos, mvpMat).outVec4
            }
            fragmentStage { colorOutput(ShaderNodeIoVar(ModelVar4fConst(Vec4f(1f)))) }
        })
        shadowShader.onPipelineSetup += { builder, _, _ ->
            builder.blendMode = BlendMode.DISABLED
            builder.cullMethod = culling
        }
        return shadowShader.createPipeline(mesh, ctx)
    }

    override fun dispose(ctx: KoolContext) {
        super.dispose(ctx)
        shadowPipelines.values.filterNotNull().forEach { ctx.disposePipeline(it) }
    }

    companion object {
        fun defaultSetup(): Setup {
            return Setup().apply {
                colorFormat = TexFormat.R
                colorRenderTarget = RENDER_TARGET_RENDERBUFFER
                depthRenderTarget = RENDER_TARGET_TEXTURE
            }
        }
    }
}

class LinearDepthMapPass(drawNode: Node, width: Int, height: Int = width) : DepthMapPass(drawNode, width, height, linearDepthSetup()) {

    init {
        onAfterCollectDrawCommands += {
            clearColor = Color(1f, 0f, 0f, 1f)
        }
    }

    override fun createPipeline(mesh: Mesh, culling: CullMethod, ctx: KoolContext): Pipeline? {
        val shadowShader = ModeledShader(ShaderModel("shadow shader").apply {
            vertexStage {
                var mvpMat = premultipliedMvpNode().outMvpMat
                if (mesh.instances != null) {
                    mvpMat = multiplyNode(mvpMat, instanceAttrModelMat().output).output
                }
                if (mesh.skin != null) {
                    val skinNd = skinTransformNode(attrJoints().output, attrWeights().output)
                    mvpMat = multiplyNode(mvpMat, skinNd.outJointMat).output
                }
                var localPos = attrPositions().output
                mesh.morphWeights?.let { weights ->
                    val morphAttribs = mesh.geometry.getMorphAttributes()
                    if (morphAttribs.isNotEmpty()) {
                        val morphWeights = morphWeightsNode(weights.size)
                        morphAttribs.filter { it.name.startsWith(Attribute.POSITIONS.name) }.forEach { attrib ->
                            val weight = getMorphWeightNode(morphAttribs.indexOf(attrib), morphWeights)
                            val posDisplacement = multiplyNode(attributeNode(attrib).output, weight.outWeight)
                            localPos = addNode(localPos, posDisplacement.output).output
                        }
                    }
                }
                positionOutput = vec4TransformNode(localPos, mvpMat).outVec4
            }
            fragmentStage {
                val linDepth = addNode(LinearDepthNode(stage))
                colorOutput(linDepth.outColor)
            }
        })
        shadowShader.onPipelineSetup += { builder, _, _ ->
            builder.blendMode = BlendMode.DISABLED
            builder.cullMethod = culling
        }
        return shadowShader.createPipeline(mesh, ctx)
    }

    private class LinearDepthNode(graph: ShaderGraph) : ShaderNode("linearDepth", graph) {
        val outColor = ShaderNodeIoVar(ModelVar4f("linearDepth"), this)

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("""
                float d = gl_FragCoord.z / gl_FragCoord.w;
                ${outColor.declare()} = vec4(-d, 0.0, 0.0, 1.0);
            """)
        }
    }

    companion object {
        private fun linearDepthSetup(): Setup {
            return Setup().apply {
                colorFormat = TexFormat.R_F16
                colorRenderTarget = RENDER_TARGET_TEXTURE
                depthRenderTarget = RENDER_TARGET_RENDERBUFFER
            }
        }
    }
}

class NormalLinearDepthMapPass(drawNode: Node, width: Int, height: Int = width) : DepthMapPass(drawNode, width, height, normalLinearDepthSetup()) {

    init {
        name = "NormalLinearDepthMapPass"

        onAfterCollectDrawCommands += {
            clearColor = Color(0f, 1f, 0f, 1f)
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
                var modelViewMat = multiplyNode(mvpNode.outViewMat, mvpNode.outModelMat).output
                var mvpMat = mvpNode.outMvpMat

                if (mesh.instances != null) {
                    mvpMat = multiplyNode(mvpMat, instanceAttrModelMat().output).output
                    modelViewMat = multiplyNode(modelViewMat, instanceAttrModelMat().output).output
                }
                if (mesh.skin != null) {
                    val skinNd = skinTransformNode(attrJoints().output, attrWeights().output)
                    mvpMat = multiplyNode(mvpMat, skinNd.outJointMat).output
                    modelViewMat = multiplyNode(modelViewMat, skinNd.outJointMat).output
                }

                var localPos = attrPositions().output
                var localNrm = attrNormals().output
                mesh.morphWeights?.let { weights ->
                    val morphAttribs = mesh.geometry.getMorphAttributes()
                    if (morphAttribs.isNotEmpty()) {
                        val morphWeights = morphWeightsNode(weights.size)
                        morphAttribs.filter { it.name.startsWith(Attribute.POSITIONS.name) }.forEach { attrib ->
                            val weight = getMorphWeightNode(morphAttribs.indexOf(attrib), morphWeights)
                            val posDisplacement = multiplyNode(attributeNode(attrib).output, weight.outWeight)
                            localPos = addNode(localPos, posDisplacement.output).output
                        }
                        morphAttribs.filter { it.name.startsWith(Attribute.NORMALS.name) }.forEach { attrib ->
                            val weight = getMorphWeightNode(morphAttribs.indexOf(attrib), morphWeights)
                            val nrmDisplacement = multiplyNode(attributeNode(attrib).output, weight.outWeight)
                            localNrm = addNode(localNrm, nrmDisplacement.output).output
                        }
                    }
                }

                val nrm = vec3TransformNode(localNrm, modelViewMat, 0f)
                ifNormals = stageInterfaceNode("ifNormals", nrm.outVec3)

                positionOutput = vec4TransformNode(localPos, mvpMat).outVec4
            }
            fragmentStage {
                val normal = flipBacksideNormalNode(ifNormals.output).outNormal
                val linDepth = addNode(NormalLinearDepthNode(normal, stage))
                colorOutput(linDepth.outColor)
            }
        })
        shadowShader.onPipelineSetup += { builder, _, _ ->
            builder.blendMode = BlendMode.DISABLED
            builder.cullMethod = culling
        }
        return shadowShader.createPipeline(mesh, ctx)
    }

    private class NormalLinearDepthNode(val inNormals: ShaderNodeIoVar, graph: ShaderGraph) : ShaderNode("normalLinearDepth", graph) {
        val outColor = ShaderNodeIoVar(ModelVar4f("normaLinearDepth"), this)

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("""
                float d = gl_FragCoord.z / gl_FragCoord.w;
                ${outColor.declare()} = vec4(normalize(${inNormals.ref3f()}), -d);
            """)
        }
    }

    companion object {
        private fun normalLinearDepthSetup(): Setup {
            return Setup().apply {
                colorFormat = TexFormat.RGBA_F16
                colorRenderTarget = RENDER_TARGET_TEXTURE
                depthRenderTarget = RENDER_TARGET_RENDERBUFFER
            }
        }
    }
}
