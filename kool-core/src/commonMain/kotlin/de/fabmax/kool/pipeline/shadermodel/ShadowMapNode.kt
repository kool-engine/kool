package de.fabmax.kool.pipeline.shadermodel

import de.fabmax.kool.KoolException
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.pipeline.ShaderStage
import de.fabmax.kool.pipeline.UniformMat4f
import de.fabmax.kool.pipeline.UniformMat4fv
import de.fabmax.kool.util.CascadedShadowMap
import de.fabmax.kool.util.SimpleShadowMap

abstract class ShadowMapNode {
    var lightIndex = 0
    open var depthMap: Texture2dNode? = null

    abstract val outShadowFac: ShaderNodeIoVar
}

class SimpleShadowMapNode(shadowMap: SimpleShadowMap, vertexGraph: ShaderGraph, fragmentGraph: ShaderGraph) : ShadowMapNode() {
    private val ifPosLightSpace = StageInterfaceNode("posLightSpace_${vertexGraph.nextNodeId}", vertexGraph, fragmentGraph)
    private val ifNrmZLightSpace = StageInterfaceNode("nrmZLightSpace_${vertexGraph.nextNodeId}", vertexGraph, fragmentGraph)

    val vertexNode = SimpleShadowMapTransformNode(shadowMap, vertexGraph)
    val fragmentNode = SimpleShadowMapFragmentNode(shadowMap, fragmentGraph)

    var inWorldPos: ShaderNodeIoVar
        get() = vertexNode.inWorldPos
        set(value) { vertexNode.inWorldPos = value }

    var inWorldNrm: ShaderNodeIoVar
        get() = vertexNode.inWorldNrm
        set(value) { vertexNode.inWorldNrm = value }

    var inDepthOffset: ShaderNodeIoVar
        get() = fragmentNode.inDepthOffset
        set(value) { fragmentNode.inDepthOffset = value }

    override var depthMap: Texture2dNode?
        get() = fragmentNode.depthMap
        set(value) { fragmentNode.depthMap = value }

    override val outShadowFac: ShaderNodeIoVar
        get() = fragmentNode.outShadowFac

    init {
        this.lightIndex = shadowMap.lightIndex
        vertexGraph.addNode(ifPosLightSpace.vertexNode)
        vertexGraph.addNode(ifNrmZLightSpace.vertexNode)
        fragmentGraph.addNode(ifPosLightSpace.fragmentNode)
        fragmentGraph.addNode(ifNrmZLightSpace.fragmentNode)

        ifPosLightSpace.input = vertexNode.outPosLightSpace
        fragmentNode.inPosLightSpace = ifPosLightSpace.output
        ifNrmZLightSpace.input = vertexNode.outNrmZLightSpace
        fragmentNode.inNrmZLightSpace = ifNrmZLightSpace.output
    }
}

class SimpleShadowMapTransformNode(val shadowMap: SimpleShadowMap, graph: ShaderGraph) : ShaderNode("lightSpaceTf_${graph.nextNodeId}", graph) {
    val uShadowMapVP = UniformMat4f("${name}_shadowMapVP")

    var inWorldPos: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
    var inWorldNrm: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
    val outPosLightSpace = ShaderNodeIoVar(ModelVar4f("${name}_posLightSpace"), this)
    val outNrmZLightSpace = ShaderNodeIoVar(ModelVar1f("${name}_nrmZLightSpace"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inWorldPos, inWorldNrm)

        shaderGraph.descriptorSet.apply {
            uniformBuffer(name, shaderGraph.stage) {
                +{ uShadowMapVP }
                onUpdate = { _, _ ->
                    uShadowMapVP.value.set(shadowMap.lightViewProjMat)
                }
            }
        }
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("""
            ${outPosLightSpace.declare()} = ${uShadowMapVP.name} * vec4(${inWorldPos.ref3f()}, 1.0);
            ${outNrmZLightSpace.declare()} = (${uShadowMapVP.name} * vec4(${inWorldNrm.ref3f()}, 0.0)).z;
        """)
    }
}

class SimpleShadowMapFragmentNode(shadowMap: SimpleShadowMap, graph: ShaderGraph) : ShaderNode("shadowMap_${graph.nextNodeId}", graph, ShaderStage.FRAGMENT_SHADER.mask) {
    var depthMap: Texture2dNode? = null
    var inDepthOffset: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar1fConst(shadowMap.shaderDepthOffset))
    var inPosLightSpace: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar4fConst(Vec4f.ZERO))
    var inNrmZLightSpace: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar4fConst(Vec4f.ZERO))

    val outShadowFac = ShaderNodeIoVar(ModelVar1f("${name}_shadowFac"), this)

    var useDithering = false

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inDepthOffset, inPosLightSpace, inNrmZLightSpace)
        dependsOn(depthMap ?: throw KoolException("Depth map input not set"))
    }

    override fun generateCode(generator: CodeGenerator) {
        val depthTex = depthMap ?: throw KoolException("Depth map input not set")

        val sampleBuilder = StringBuilder()
        var nSamples = 0
        if (useDithering) {
            // dithered pcf shadow map sampling
            // https://developer.nvidia.com/gpugems/gpugems/part-ii-lighting-and-shadows/chapter-11-shadow-map-antialiasing

            // sample only 4 positions with varying offset, depending on screen position
            sampleBuilder.append("vec2 ${name}_offset = vec2(float(fract(gl_FragCoord.x * 0.5) > 0.25), float(fract(gl_FragCoord.y * 0.5) > 0.25));")

            val samplePattern = listOf(Vec2f(-1.5f, 0.5f), Vec2f(0.5f, 0.5f), Vec2f(-1.5f, -1.5f), Vec2f(0.5f, -1.5f))
            samplePattern.forEach { off ->
                val projCoord = "vec4(${name}_pos.xy + (${name}_offset + vec2(${off.x}, ${off.y})) * ${name}_scale * ${name}_pos.w, ${name}_pos.z, ${name}_pos.w)"
                sampleBuilder.append("${outShadowFac.name} += ${generator.sampleTexture2dDepth(depthTex.name, projCoord)};\n")
                nSamples++
            }

        } else {
            // regular 16 sample smooth shadow map sampling
            val samplePattern = mutableListOf<Vec2f>()
            for (y in 0..3) {
                for (x in 0..3) {
                    samplePattern += Vec2f(-1.5f + x, -1.5f + y)
                }
            }
            samplePattern.forEach { off ->
                val projCoord = "vec4(${name}_pos.xy + (vec2(${off.x}, ${off.y})) * ${name}_scale * ${name}_pos.w, ${name}_pos.z, ${name}_pos.w)"
                sampleBuilder.append("${outShadowFac.name} += ${generator.sampleTexture2dDepth(depthTex.name, projCoord)};\n")
                nSamples++
            }
        }

        generator.appendMain("""
            vec4 ${name}_pos = ${inPosLightSpace.ref4f()};
            
            // check shadow map bounds
            ${outShadowFac.declare()} = 1.0;
            vec3 ${name}_posW = ${name}_pos.xyz / ${name}_pos.w;
            if (${name}_posW.x > 0.0 && ${name}_posW.x < 1.0
                && ${name}_posW.y > 0.0 && ${name}_posW.y < 1.0
                && ${name}_posW.z > -1.0 && ${name}_posW.z < 1.0) {
            
                if (${inNrmZLightSpace.ref1f()} > 0.05) {
                    ${outShadowFac.name} = 0.0;
                } else {
                    float ${name}_size = float(textureSize(${depthTex.name}, 0).x);
                    float ${name}_scale = 1.0 / float(${name}_size);
                    ${name}_pos.z += ${inDepthOffset.ref1f()};
                    ${outShadowFac.name} = 0.0;
                    $sampleBuilder
                    ${outShadowFac.name} *= ${1f / nSamples};
                    if (${inNrmZLightSpace.ref1f()} > 0.0) {
                        ${outShadowFac.name} *= 1.0 - smoothstep(0.0, 0.05, ${inNrmZLightSpace.ref1f()});
                    }
                }
            }
        """)
    }
}

class CascadedShadowMapNode(val shadowMap: CascadedShadowMap, val vertexGraph: ShaderGraph, val fragmentGraph: ShaderGraph) : ShadowMapNode() {
    var inViewPosition: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))

    var inWorldPos: ShaderNodeIoVar
        get() = vertexNode.inWorldPos
        set(value) { vertexNode.inWorldPos = value }

    var inWorldNrm: ShaderNodeIoVar
        get() = vertexNode.inWorldNrm
        set(value) { vertexNode.inWorldNrm = value }

    override var depthMap: Texture2dNode?
        get() = fragmentNode.depthMap
        set(value) { fragmentNode.depthMap = value }

    override val outShadowFac: ShaderNodeIoVar
        get() = fragmentNode.outShadowFac

    val vertexNode = CascadedShadowMapTransformNode(shadowMap, vertexGraph)
    val fragmentNode = CascadedShadowMapFragmentNode(shadowMap, fragmentGraph)

    private val helperNd = CascadedShadowHelperNd(vertexGraph)
    private val posLightSpace = ShaderNodeIoVar(ModelVar4f("ifPosLightSpace_${vertexGraph.nextNodeId}[${shadowMap.numCascades}]"))
    private val nrmZLightSpace = ShaderNodeIoVar(ModelVar1f("ifNrmZLightSpace_${vertexGraph.nextNodeId}[${shadowMap.numCascades}]"))
    private val ifViewZ = ShaderNodeIoVar(ModelVar1f("outViewZ_${helperNd.nodeId}"))

    init {
        this.lightIndex = shadowMap.lightIndex
        vertexGraph.addNode(helperNd)

        fragmentNode.inPosLightSpace = posLightSpace
        fragmentNode.inNrmZLightSpace = nrmZLightSpace
        fragmentNode.inViewZ = ifViewZ
    }

    private inner class CascadedShadowHelperNd(graph: ShaderGraph) : ShaderNode("cascadedShadowHelper_${graph.nextNodeId}", graph) {
        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(inViewPosition, vertexNode.outPosLightSpace)

            fragmentGraph.inputs += vertexGraph.addStageOutput(posLightSpace.variable, false, shadowMap.numCascades)
            fragmentGraph.inputs += vertexGraph.addStageOutput(nrmZLightSpace.variable, false, shadowMap.numCascades)
            fragmentGraph.inputs += vertexGraph.addStageOutput(ifViewZ.variable, false)
        }

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("${ifViewZ.name} = $inViewPosition.z;")
            for (i in 0 until shadowMap.numCascades) {
                generator.appendMain("${posLightSpace.name.substringBefore('[')}[$i] = ${vertexNode.outPosLightSpace.name.substringBefore('[')}[$i];")
                generator.appendMain("${nrmZLightSpace.name.substringBefore('[')}[$i] = ${vertexNode.outNrmZLightSpace.name.substringBefore('[')}[$i];")
            }
        }
    }
}

class CascadedShadowMapTransformNode(val shadowMap: CascadedShadowMap, graph: ShaderGraph) : ShaderNode("lightSpaceTf_${graph.nextNodeId}", graph) {
    val uShadowMapVP = UniformMat4fv("${name}_shadowMapVPs", shadowMap.numCascades)

    var inWorldPos: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
    var inWorldNrm: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
    val outPosLightSpace = ShaderNodeIoVar(ModelVar4f("${name}_posLightSpace[${shadowMap.numCascades}]"), this)
    val outNrmZLightSpace = ShaderNodeIoVar(ModelVar1f("${name}_nrmZLightSpace[${shadowMap.numCascades}]"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inWorldPos)

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
        generator.appendMain("""
            ${outPosLightSpace.declare()};
            ${outNrmZLightSpace.declare()};
            vec3 ${name}_nrmLightSpace;
        """)
        for (i in 0 until shadowMap.numCascades) {
            generator.appendMain("""
                ${outPosLightSpace.name.substringBefore('[')}[$i] = ${uShadowMapVP.name}[$i] * vec4(${inWorldPos.ref3f()}, 1.0);
                ${name}_nrmLightSpace = normalize((${uShadowMapVP.name}[$i] * vec4(${inWorldNrm.ref3f()}, 0.0)).xyz);
                ${outNrmZLightSpace.name.substringBefore('[')}[$i] = ${name}_nrmLightSpace.z;
            """)
        }
    }
}

class CascadedShadowMapFragmentNode(val shadowMap: CascadedShadowMap, graph: ShaderGraph) : ShaderNode("cascadedShadowMap_${graph.nextNodeId}", graph, ShaderStage.FRAGMENT_SHADER.mask) {
    var depthMap: Texture2dNode? = null
    var inPosLightSpace: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar4fConst(Vec4f.ZERO))
    var inNrmZLightSpace: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar1fConst(0f))
    var inViewZ: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar1fConst(0f))

    val outShadowFac = ShaderNodeIoVar(ModelVar1f("${name}_shadowFac"), this)

    var useDithering = false

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(depthMap ?: throw KoolException("Depth map input not set"))
        dependsOn(inPosLightSpace, inNrmZLightSpace, inViewZ)
    }

    override fun generateCode(generator: CodeGenerator) {
        val depthTex = depthMap ?: throw KoolException("Depth map input not set")

        val sampleBuilder = StringBuilder()
        var nSamples = 0
        if (useDithering) {
            // dithered pcf shadow map sampling
            // https://developer.nvidia.com/gpugems/gpugems/part-ii-lighting-and-shadows/chapter-11-shadow-map-antialiasing

            // sample only 4 positions with varying offset, depending on screen position
            sampleBuilder.append("vec2 offset = vec2(float(fract(gl_FragCoord.x * 0.5) > 0.25), float(fract(gl_FragCoord.y * 0.5) > 0.25));")

            val samplePattern = listOf(Vec2f(-1.5f, 0.5f), Vec2f(0.5f, 0.5f), Vec2f(-1.5f, -1.5f), Vec2f(0.5f, -1.5f))
            samplePattern.forEach { off ->
                val projCoord = "vec4(projPos.xy + (offset + vec2(${off.x}, ${off.y})) * texScale * projPos.w, projPos.z, projPos.w)"
                sampleBuilder.append("shadowFac += ${generator.sampleTexture2dDepth("shadowTex", projCoord)};\n")
                nSamples++
            }

        } else {
            // regular 16 sample smooth shadow map sampling
            val samplePattern = mutableListOf<Vec2f>()
            for (y in 0..3) {
                for (x in 0..3) {
                    samplePattern += Vec2f(-1.5f + x, -1.5f + y)
                }
            }
            samplePattern.forEach { off ->
                val projCoord = "vec4(projPos.xy + (vec2(${off.x}, ${off.y})) * texScale * projPos.w, projPos.z, projPos.w)"
                sampleBuilder.append("shadowFac += ${generator.sampleTexture2dDepth("shadowTex", projCoord)};\n")
                nSamples++
            }
        }

        generator.appendFunction("cascadedShadowFac", """
            float cascadedShadowFac(sampler2DShadow shadowTex, vec4 projPos, float depthOffset) {
                float texSize = float(textureSize(shadowTex, 0).x);
                float texScale = 1.0 / float(texSize);
                projPos.z += depthOffset;
                float shadowFac = 0.0;
                if (projPos.z >= 1.0) {
                    shadowFac = 1.0;
                } else {
                    $sampleBuilder
                    shadowFac *= ${1f / nSamples};
                }
                return shadowFac;
            }
        """)

        generator.appendMain("""
                ${outShadowFac.declare()} = 1.0;
                bool ${name}_hasSampled = false;
            """)
        for (i in 0 until shadowMap.numCascades) {
            generator.appendMain("""
                if (!${name}_hasSampled) {
                    vec3 ${name}_samplePos = ${inPosLightSpace.name.substringBefore('[')}[$i].xyz / ${inPosLightSpace.name.substringBefore('[')}[$i].w;
                    if (${name}_samplePos.x > 0.0 && ${name}_samplePos.x < 1.0
                        && ${name}_samplePos.y > 0.0 && ${name}_samplePos.y < 1.0
                        && ${name}_samplePos.z > -1.0 && ${name}_samplePos.z < 1.0) {
                        
                        if (${inNrmZLightSpace.name.substringBefore('[')}[$i] > 0.05) {
                            ${outShadowFac.name} = 0.0;
                        } else {
                            ${outShadowFac.name} = cascadedShadowFac(${depthTex.name}[$i], ${inPosLightSpace.name.substringBefore('[')}[$i], float(${shadowMap.cascades[i].shaderDepthOffset}));
                            if (${inNrmZLightSpace.name.substringBefore('[')}[$i] > 0.0) {
                                ${outShadowFac.name} *= 1.0 - smoothstep(0.0, 0.05, ${inNrmZLightSpace.name.substringBefore('[')}[$i]);
                            }
                        }
                        ${name}_hasSampled = true;
                    }
                }
            """)
        }
    }
}
