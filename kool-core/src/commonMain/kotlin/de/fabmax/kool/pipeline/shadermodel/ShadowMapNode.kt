package de.fabmax.kool.pipeline.shadermodel

import de.fabmax.kool.KoolException
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.CascadedShadowMap
import de.fabmax.kool.util.ShadowMapPass

abstract class ShadowMapNode {
    var lightIndex = 0
    var depthMap: TextureNode? = null

    abstract val outShadowFac: ShaderNodeIoVar
}

class SimpleShadowMapNode(shadowMap: ShadowMapPass, vertexGraph: ShaderGraph, fragmentGraph: ShaderGraph) : ShadowMapNode() {
    private val ifPosLightSpace = StageInterfaceNode("posLightSpace_${vertexGraph.nextNodeId}", vertexGraph, fragmentGraph)

    val uShadowMapVP = UniformMat4f("shadowMapVP_${vertexGraph.nextNodeId}")

    var inDepthOffset: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar1fConst(-0.01f))
    var inPosition: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
    var inModelMat: ShaderNodeIoVar? = null

    init {
        this.lightIndex = shadowMap.lightIndex
        vertexGraph.addNode(ifPosLightSpace.vertexNode)
        fragmentGraph.addNode(ifPosLightSpace.fragmentNode)
    }

    val vertexNode = object : ShaderNode("shadowMap_${vertexGraph.nextNodeId}", vertexGraph, ShaderStage.VERTEX_SHADER.mask) {
        val outPosLightSpace = ShaderNodeIoVar(ModelVar4f("shadowMap_${vertexGraph.nextNodeId}_posLightSpace"), this)

        init {
            ifPosLightSpace.input = outPosLightSpace
        }

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            val inModel = inModelMat ?: throw KoolException("Model matrix input not set")
            dependsOn(inPosition, inModel)

            shaderGraph.descriptorSet.apply {
                uniformBuffer(name, shaderGraph.stage) {
                    +{ uShadowMapVP }
                    onUpdate = { _, cmd ->
                        uShadowMapVP.value.set(shadowMap.lightViewProjMat)
                    }
                }
            }
        }

        override fun generateCode(generator: CodeGenerator) {
            val modelMat = inModelMat?.variable ?: throw KoolException("Model matrix input not set")
            generator.appendMain("${outPosLightSpace.declare()} = ${uShadowMapVP.name} * (${modelMat.refAsType(GlslType.MAT_4F)} * vec4(${inPosition.ref3f()}, 1.0));")
        }
    }

    val fragmentNode: ShaderNode = object : ShaderNode("shadowMap_${fragmentGraph.nextNodeId}", fragmentGraph, ShaderStage.FRAGMENT_SHADER.mask) {
        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            val depthTex = depthMap ?: throw KoolException("Depth map input not set")
            dependsOn(depthTex)
        }

        override fun generateCode(generator: CodeGenerator) {
            val depthTex = depthMap ?: throw KoolException("Depth map input not set")

            generator.appendMain("""
                float ${name}_size = float(textureSize(${depthTex.name}, 0).x);
                float ${name}_scale = 1.0 / float(${name}_size);
                vec4 ${name}_pos = ${ifPosLightSpace.output.ref4f()};
                ${name}_pos.z += ${inDepthOffset.ref1f()};
                vec2 ${name}_offset = vec2(float(fract(${name}_pos.x * ${name}_size * 5.0) > 0.5),
                                           float(fract(${name}_pos.y * ${name}_size * 5.0) > 0.5));
                ${outShadowFac.declare()} = 0.0;
            """)

            // dithered pcf shadow map sampling
            // https://developer.nvidia.com/gpugems/gpugems/part-ii-lighting-and-shadows/chapter-11-shadow-map-antialiasing
            var nSamples = 0
            val samplePattern = listOf(
                    Vec2f(-1.5f, 0.5f),
                    Vec2f(0.5f, 0.5f),
                    Vec2f(-1.5f, -1.5f),
                    Vec2f(0.5f, -1.5f))

            samplePattern.forEach { off ->
                val projCoord = "vec4(${name}_pos.xy + (${name}_offset + vec2(${off.x}, ${off.y})) * ${name}_scale * ${name}_pos.w, ${name}_pos.z, ${name}_pos.w)"
                generator.appendMain("${outShadowFac.name} += ${generator.sampleTexture2dDepth(depthTex.name, projCoord)};")
                nSamples++
            }
            generator.appendMain("${outShadowFac.name} *= ${1f / nSamples};")
        }
    }

    override val outShadowFac = ShaderNodeIoVar(ModelVar1f("${fragmentNode.name}_shadowFac"), fragmentNode)
}

class CascadedShadowMapNode(shadowMap: CascadedShadowMap, vertexGraph: ShaderGraph, fragmentGraph: ShaderGraph) : ShadowMapNode() {
    val uShadowMapVP = UniformMat4fv("shadowMapVP_${vertexGraph.nextNodeId}", shadowMap.numCascades)
    val uClipSpaceRanges = Uniform1fv("shadowMapClipRng_${fragmentGraph.nextNodeId}", shadowMap.numCascades)

    var inDepthOffset: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar1fConst(-0.001f))
    var inPosition: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
    var inClipPosition: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
    var inModelMat: ShaderNodeIoVar? = null

    private val posClipSpaceZ = ShaderNodeIoVar(ModelVar1f("ifPosClipSpaceZ_${vertexGraph.nextNodeId}"))
    private val posLightSpace = ShaderNodeIoVar(ModelVar4f("ifPosLightSpace_${vertexGraph.nextNodeId}[${shadowMap.numCascades}]"))
    private lateinit var ifPosClipSpaceZ: ShaderInterfaceIoVar
    private lateinit var ifPosLightSpace: ShaderInterfaceIoVar

    init {
        this.lightIndex = shadowMap.lightIndex
    }

    val vertexNode = object : ShaderNode("shadowMap_${vertexGraph.nextNodeId}", vertexGraph, ShaderStage.VERTEX_SHADER.mask) {
        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            val inModel = inModelMat ?: throw KoolException("Model matrix input not set")
            dependsOn(inPosition, inModel)

            ifPosClipSpaceZ = shaderGraph.addStageOutput(posClipSpaceZ.variable)
            ifPosLightSpace = shaderGraph.addStageOutput(posLightSpace.variable, shadowMap.numCascades)

            shaderGraph.descriptorSet.apply {
                uniformBuffer(name, shaderGraph.stage) {
                    +{ uShadowMapVP }
                    onUpdate = { _, _ ->
                        for (i in 0 until shadowMap.numCascades) {
                            uShadowMapVP.value[i].set(shadowMap.cascades[i].lightViewProjMat)
                        }
                    }
                }
            }
        }

        override fun generateCode(generator: CodeGenerator) {
            val modelMat = inModelMat?.variable ?: throw KoolException("Model matrix input not set")
            for (i in 0 until shadowMap.numCascades) {
                generator.appendMain("${posLightSpace.name.substringBefore('[')}[$i] = ${uShadowMapVP.name}[$i] * (${modelMat.refAsType(GlslType.MAT_4F)} * vec4(${inPosition.ref3f()}, 1.0));")
            }
            generator.appendMain("${posClipSpaceZ.name} = ${inClipPosition.name}.z / ${inClipPosition.name}.w;")
        }
    }

    val fragmentNode: ShaderNode = object : ShaderNode("shadowMap_${fragmentGraph.nextNodeId}", fragmentGraph, ShaderStage.FRAGMENT_SHADER.mask) {
        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            val depthTex = depthMap ?: throw KoolException("Depth map input not set")
            dependsOn(depthTex)

            shaderGraph.inputs += ifPosClipSpaceZ
            shaderGraph.inputs += ifPosLightSpace

            shaderGraph.descriptorSet.apply {
                uniformBuffer(name, shaderGraph.stage) {
                    +{ uClipSpaceRanges }
                    onUpdate = { _, _ ->
                        for (i in 0 until shadowMap.numCascades) {
                            uClipSpaceRanges.value[i] = shadowMap.clipSpaceRanges[i]
                        }
                    }
                }
            }
        }

        override fun generateCode(generator: CodeGenerator) {
            val depthTex = depthMap ?: throw KoolException("Depth map input not set")

            val shadowFacFun = StringBuilder()
            shadowFacFun.append("""
                float cascadedShadowFac(sampler2DShadow shadowTex, vec4 projPos, float depthOffset) {
                    float texSize = float(textureSize(shadowTex, 0).x);
                    float texScale = 1.0 / float(texSize);
                    projPos.z += depthOffset;
                    vec2 offset = vec2(float(fract(projPos.x * texSize * 5.0) > 0.5),
                                       float(fract(projPos.y * texSize * 5.0) > 0.5));
                    float shadowFac = 0.0;
                    if (projPos.z >= 1.0) {
                        shadowFac = 1.0;
                    } else {
                        shadowFac = ${generator.sampleTexture2dDepth("shadowTex", "projPos")};
            """)
            // dithered pcf shadow map sampling
            // https://developer.nvidia.com/gpugems/gpugems/part-ii-lighting-and-shadows/chapter-11-shadow-map-antialiasing
            var nSamples = 0
            val samplePattern = listOf(
                    Vec2f(-1.5f, 0.5f),
                    Vec2f(0.5f, 0.5f),
                    Vec2f(-1.5f, -1.5f),
                    Vec2f(0.5f, -1.5f))

            samplePattern.forEach { off ->
                val projCoord = "vec4(projPos.xy + (offset + vec2(${off.x}, ${off.y})) * texScale * projPos.w, projPos.z, projPos.w)"
                shadowFacFun.append("shadowFac += ${generator.sampleTexture2dDepth("shadowTex", projCoord)};")
                nSamples++
            }
            shadowFacFun.append("""
                        shadowFac *= ${1f / nSamples};
                    }
                    return shadowFac;
                }
            """)
            generator.appendFunction("cascadedShadowFac", shadowFacFun.toString())

            generator.appendMain("""
                ${outShadowFac.declare()} = 1.0;
            """)
            for (i in 0 until shadowMap.numCascades) {
                generator.appendMain("""
                    if (${posClipSpaceZ.name} <= ${uClipSpaceRanges.name}[$i]) {
                        ${outShadowFac.name} = cascadedShadowFac(${depthTex.name}[$i], ${posLightSpace.name.substringBefore('[')}[$i], ${inDepthOffset.ref1f()});
                    }
                """)
                if (i < shadowMap.numCascades - 1) {
                    generator.appendMain(" else ")
                }
            }
        }
    }

    override val outShadowFac = ShaderNodeIoVar(ModelVar1f("${fragmentNode.name}_shadowFac"), fragmentNode)
}
