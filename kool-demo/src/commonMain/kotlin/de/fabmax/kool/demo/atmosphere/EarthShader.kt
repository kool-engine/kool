package de.fabmax.kool.demo.atmosphere

import de.fabmax.kool.pipeline.Uniform3f
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.PbrMaterialConfig
import de.fabmax.kool.util.deferred.DeferredPbrShader

class EarthShader : DeferredPbrShader(cfg, model()) {
    var uDirToSun: Uniform3f? = null

    init {
        onPipelineCreated += { _, _, _ ->
            uDirToSun = model.findNode<PushConstantNode3f>("uDirToSun")?.uniform
        }
    }

    companion object {
        val cfg = PbrMaterialConfig().apply {
            useAlbedoMap("world_day.jpg")
            useDisplacementMap("world_height.png")
            useEmissiveMap("world_night.jpg")
            useNormalMap("world_nrm.jpg")

            displacementStrength = 1f
        }

        fun model() = defaultMrtPbrModel(cfg).apply {
            val ifSunDirViewSpace: StageInterfaceNode
            vertexStage {
                val mvp = findNodeByType<UniformBufferMvp>()!!
                val modelViewMat = multiplyNode(mvp.outViewMat, mvp.outModelMat).output
                val dirToSun = pushConstantNode3f("uDirToSun").output
                val viewSunDir = vec3TransformNode(dirToSun, modelViewMat, 0f).outVec3
                ifSunDirViewSpace = stageInterfaceNode("ifSunDirViewSpace", viewSunDir)
            }
            fragmentStage {
                val mrtOutput = findNodeByType<MrtMultiplexNode>()!!
                val dayAlbedo = mrtOutput.inAlbedo

                val roughnessNd = addNode(EarthRoughnessNode(dayAlbedo, stage))
                mrtOutput.inRoughness = roughnessNd.outRoughness

                addNode(EarthDayNightMixNode(stage)).apply {
                    inNormal = normalizeNode(mrtOutput.inViewNormal).output
                    inSunDir = normalizeNode(ifSunDirViewSpace.output).output
                    inAlbedo = dayAlbedo
                    inEmissive = mrtOutput.inEmissive
                    mrtOutput.inAlbedo = outAlbedo
                    mrtOutput.inEmissive = outEmissive
                }
            }
        }
    }

    private class EarthDayNightMixNode(graph: ShaderGraph) : ShaderNode("earthDayNightMix", graph) {
        lateinit var inNormal: ShaderNodeIoVar
        lateinit var inSunDir: ShaderNodeIoVar
        lateinit var inAlbedo: ShaderNodeIoVar
        lateinit var inEmissive: ShaderNodeIoVar

        val outAlbedo = ShaderNodeIoVar(ModelVar4f("${name}_outAlbedo"), this)
        val outEmissive = ShaderNodeIoVar(ModelVar4f("${name}_outEmissive"), this)

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(inNormal, inSunDir, inAlbedo, inEmissive)
        }

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("""
            float ${name}_mix = (clamp(dot($inNormal, $inSunDir), -0.05, 0.05) + 0.05) * 10.0;
            ${outAlbedo.declare()} = $inAlbedo * ${name}_mix;
            ${outEmissive.declare()} = $inEmissive * (1.0 - ${name}_mix) * 0.6;
        """)
        }
    }

    private class EarthRoughnessNode(val inAlbedo: ShaderNodeIoVar, graph: ShaderGraph) : ShaderNode("earthRoughness", graph) {
        val outRoughness = ShaderNodeIoVar(ModelVar1f("eartRoughness_out"), this)

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(inAlbedo)
        }

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("""
            float ${name}_relBlue = min($inAlbedo.b - $inAlbedo.r, $inAlbedo.b - $inAlbedo.g) / $inAlbedo.b;
            float ${name}_brightness = dot($inAlbedo.rgb, vec3(0.333));
            ${outRoughness.declare()};
            if (${name}_relBlue > 0.2 || ${name}_brightness < 0.002) {
                $outRoughness = 0.22;
            } else {
                $outRoughness = 1.0 - (0.3 + ${name}_brightness * 0.4);
            }
        """)
        }
    }
}