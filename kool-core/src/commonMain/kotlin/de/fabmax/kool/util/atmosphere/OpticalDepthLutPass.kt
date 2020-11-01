package de.fabmax.kool.util.atmosphere

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.OrthographicCamera
import de.fabmax.kool.scene.mesh

class OpticalDepthLutPass :
        OffscreenRenderPass2d(Group(), renderPassConfig {
            name = "OpticalDepthLutPass"
            setSize(LUT_SIZE_X, LUT_SIZE_Y)
            addColorTexture(TexFormat.RG_F16)
            clearDepthTexture()
        }) {

    private var lutNode: OpticalDepthLutNode? = null

    var atmosphereRadius = 65f
        set(value) {
            field = value
            lutNode?.uAtmosphereRadius?.value = value
            update()
        }
    var surfaceRadius = 60f
        set(value) {
            field = value
            lutNode?.uSurfaceRadius?.value = value
            update()
        }
    var densityFalloff = 9f
        set(value) {
            field = value
            lutNode?.uDensityFalloff?.value = value
            update()
        }

    init {
        clearColor = null

        camera = OrthographicCamera().apply {
            projCorrectionMode = Camera.ProjCorrectionMode.OFFSCREEN
            isKeepAspectRatio = false
            left = 0f
            right = 1f
            top = 1f
            bottom = 0f
        }

        (drawNode as Group).apply {
            +mesh(listOf(Attribute.POSITIONS, Attribute.TEXTURE_COORDS)) {
                generate {
                    rect {
                        size.set(1f, 1f)
                        mirrorTexCoordsY()
                    }
                }

                val model = ShaderModel("Optical Depth LUT").apply {
                    val ifTexCoords: StageInterfaceNode
                    vertexStage {
                        ifTexCoords = stageInterfaceNode("ifTexCoords", attrTexCoords().output)
                        positionOutput = simpleVertexPositionNode().outVec4
                    }
                    fragmentStage {
                        val lutNd = addNode(OpticalDepthLutNode(stage)).apply {
                            inTexCoords = ifTexCoords.output
                        }
                        colorOutput(lutNd.outColor)
                    }
                }
                shader = ModeledShader(model).apply {
                    onPipelineCreated += { _, _, _ ->
                        lutNode = model.findNodeByType()
                        lutNode?.apply {
                            uAtmosphereRadius.value = atmosphereRadius
                            uSurfaceRadius.value = surfaceRadius
                            uDensityFalloff.value = densityFalloff
                        }
                    }
                }
            }
        }

        // this pass only needs to be rendered once, remove it immediately after first render
        onAfterDraw += {
            isEnabled = false
        }
    }

    fun update() {
        isEnabled = true
    }

    override fun dispose(ctx: KoolContext) {
        drawNode.dispose(ctx)
        super.dispose(ctx)
    }

    companion object {
        const val LUT_SIZE_X = 512
        const val LUT_SIZE_Y = 512
    }

    private class OpticalDepthLutNode(graph: ShaderGraph) : ShaderNode("opticalDepthLut", graph) {
        var inTexCoords = ShaderNodeIoVar(ModelVar2fConst(Vec2f.ZERO))
        val outColor = ShaderNodeIoVar(ModelVar4f("brdfLut_outColor"), this)

        val uAtmosphereRadius = Uniform1f("uAtmosphereRadius")
        val uSurfaceRadius = Uniform1f("uSurfaceRadius")
        val uDensityFalloff = Uniform1f("uDensityFalloff")

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(inTexCoords)

            shaderGraph.descriptorSet.apply {
                uniformBuffer(name, shaderGraph.stage) {
                    +{ uAtmosphereRadius }
                    +{ uSurfaceRadius }
                    +{ uDensityFalloff }
                }
            }
        }

        override fun generateCode(generator: CodeGenerator) {
            super.generateCode(generator)

            val numDepthSamples = 100

            generator.appendFunction("rayLength", """
                float rayLength(vec3 rayOrigin, vec3 rayDir) {
                    float a = dot(rayDir, rayDir);
                    float b = 2.0 * dot(rayDir, rayOrigin);
                    float c = dot(rayOrigin, rayOrigin) - $uAtmosphereRadius * $uAtmosphereRadius;
                    
                    float discriminant = b * b - 4.0 * a * c;
                    if (discriminant < 0.0) {
                        return -1.0;
                    } else {
                        float q = -0.5 * (b + sign(b) * sqrt(discriminant));
                        float t1 = q / a;
                        float t2 = c / q;
                        return max(t1, t2);
                    }
                }
            """)

            generator.appendFunction("densityAtAltitude", """
                float densityAtAltitude(float altitude) {
                    float nH = clamp(altitude / ($uAtmosphereRadius - $uSurfaceRadius), 0.0, 1.0);

                    // exponential density
                    //float h = nH;

                    // exponential density with cut-off
                    //float cutOff = 0.2;
                    //float h = nH * (1.0 - cutOff) + cutOff;
                    
                    // reduced density at low altitudes
                    float x = nH * 10.0;
                    float f = 0.3 * (2.0 + 3.0 * x * exp(-x)) * (10.0 - x) / 10.0;
                    float h = 1.0 - f;
                    
                    return exp(-clamp(h, 0.0, 1.0) * $uDensityFalloff) * (1.0 - smoothstep(0.0, 1.0, nH));
                }
            """)

            generator.appendFunction("opticalDepth", """
                float opticalDepth(vec3 origin, vec3 dir, float rayLength) {
                    vec3 samplePt = origin;
                    float stepSize = rayLength / float(${numDepthSamples + 1});
                    float opticalDepth = 0.0;
                    for (int i = 0; i < $numDepthSamples; i++) {
                        samplePt += dir * stepSize;
                        opticalDepth += densityAtAltitude(length(samplePt) - $uSurfaceRadius) * stepSize;
                    }
                    return opticalDepth;
                }
            """)

            generator.appendMain("""
                float cosAngle = ${inTexCoords.ref2f()}.x * 2.0 - 1.0;
                float altitude = ${inTexCoords.ref2f()}.y * ($uAtmosphereRadius - $uSurfaceRadius);
                
                vec3 origin = vec3($uSurfaceRadius + altitude, 0.0, 0.0);
                vec3 direction = vec3(cosAngle, sin(acos(cosAngle)), 0.0);
                
                float shift = $uAtmosphereRadius * 2.0;
                float rayLen = rayLength(origin - direction * shift, direction) - shift;
                float opticalDepth = opticalDepth(origin, direction, rayLen);
                
                ${outColor.declare()} = vec4(opticalDepth, densityAtAltitude(altitude), 0.0, 1.0);
            """)
        }
    }
}