package de.fabmax.kool.demo.atmosphere

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.util.Color
import kotlin.math.pow

class AtmosphericScatteringShader : ModeledShader(atmosphereModel()) {

    private var sceneColorNode: TextureNode? = null
    private var scenePosNode: TextureNode? = null
    private var atmosphereNode: AtmosphereNode? = null

    var sceneColor: Texture? = null
        set(value) {
            field = value
            sceneColorNode?.sampler?.texture = value
        }
    var scenePos: Texture? = null
        set(value) {
            field = value
            scenePosNode?.sampler?.texture = value
        }

    var dirToSun = Vec3f(0f, 0f, 1f)
        set(value) {
            field = value
            atmosphereNode?.uDirToSun?.value?.set(value)
        }
    var sunIntensity = Vec3f(1f)
        set(value) {
            field = value
            atmosphereNode?.uSunIntensity?.value?.set(value)
        }

    var scatteringCoeffPow = 4.5f
        set(value) {
            field = value
            updateScatteringCoeffs()
        }
    var scatteringCoeffStrength = 1.5f
        set(value) {
            field = value
            updateScatteringCoeffs()
        }
    var scatteringCoeffs = Vec3f(0.5f, 0.8f, 1.0f)
        set(value) {
            field = value
            updateScatteringCoeffs()
        }

    var rayleighCoeffs = Vec3f(0.7f, 0.6f, 1.0f)
        set(value) {
            field = value
            atmosphereNode?.uRayleighCoeffs?.value?.set(value, rayleighStrength)
        }
    var rayleighStrength = 1f
        set(value) {
            field = value
            atmosphereNode?.uRayleighCoeffs?.value?.apply { w = value }
        }

    var mieG = 0.99f
        set(value) {
            field = value
            atmosphereNode?.uMieCoeffs?.value?.apply { x = value }
        }
    var mieStrength = 0.3f
        set(value) {
            field = value
            atmosphereNode?.uMieCoeffs?.value?.apply { y = value }
        }

    var planetCenter = Vec3f.ZERO
        set(value) {
            field = value
            atmosphereNode?.uPlanetCenter?.value?.set(value)
        }
    var planetRadius = 600f
        set(value) {
            field = value
            atmosphereNode?.uPlanetRadius?.value = value
        }
    var densityFalloff = 9.0f
        set(value) {
            field = value
            atmosphereNode?.uDensityFalloff?.value = value
        }
    var atmosphereRadius = 609f
        set(value) {
            field = value
            atmosphereNode?.uAtmosphereRadius?.value = value
        }

    init {
        onPipelineSetup += { builder, _, _ ->
            builder.cullMethod = CullMethod.CULL_FRONT_FACES
            builder.depthTest = DepthCompareOp.DISABLED
            builder.blendMode = BlendMode.BLEND_MULTIPLY_ALPHA
        }
        onPipelineCreated += { _, _, _ ->
            sceneColorNode = model.findNode("tSceneColor")
            sceneColorNode?.sampler?.texture = sceneColor
            scenePosNode = model.findNode("tScenePos")
            scenePosNode?.sampler?.texture = scenePos

            atmosphereNode = model.findNodeByType()
            atmosphereNode?.apply {
                uDirToSun.value.set(dirToSun)
                uPlanetCenter.value.set(Vec3f.ZERO)
                uPlanetRadius.value = planetRadius
                uAtmosphereRadius.value = atmosphereRadius
                uDensityFalloff.value = densityFalloff
                uRayleighCoeffs.value.set(rayleighCoeffs, rayleighStrength)
                uMieCoeffs.value.set(mieG, mieStrength)
                uSunIntensity.value.set(sunIntensity)
                updateScatteringCoeffs()
            }
        }
    }

    private fun updateScatteringCoeffs() {
        atmosphereNode?.uScatteringCoeffs?.value?.let {
            it.x = scatteringCoeffs.x.pow(scatteringCoeffPow) * scatteringCoeffStrength
            it.y = scatteringCoeffs.y.pow(scatteringCoeffPow) * scatteringCoeffStrength
            it.z = scatteringCoeffs.z.pow(scatteringCoeffPow) * scatteringCoeffStrength
        }
    }

    companion object {
        private fun atmosphereModel() = ShaderModel().apply {
            val mvp: UniformBufferMvp
            val ifClipPos: StageInterfaceNode
            val ifWorldPos: StageInterfaceNode

            vertexStage {
                mvp = mvpNode()
                val localPos = attrPositions().output
                val worldPos = vec3TransformNode(localPos, mvp.outModelMat).outVec3
                ifWorldPos = stageInterfaceNode("ifWorldPos", worldPos)
                val clipPos = splitNode(vec4TransformNode(localPos, mvp.outMvpMat).outVec4, "xyww").output
                ifClipPos = stageInterfaceNode("ifClipPos", clipPos)
                positionOutput = clipPos
            }
            fragmentStage {
                val fragMvp = mvp.addToStage(stage)
                val clip2uv = addNode(Clip2UvNode(stage)).apply { inClipPos = ifClipPos.output }
                val sceneColor = textureSamplerNode(textureNode("tSceneColor"), clip2uv.outUv).outColor
                val viewPos = textureSamplerNode(textureNode("tScenePos"), clip2uv.outUv).outColor
                val view2world = addNode(ViewToWorldPosNode(stage)).apply {
                    inViewPos = viewPos
                }

                addNode(RaySphereIntersectionNode(stage))
                val atmoNd = addNode(AtmosphereNode(stage)).apply {
                    inSceneColor = sceneColor
                    inScenePos = view2world.outWorldPos
                    inViewDepth = splitNode(viewPos, "z").output
                    inCamPos = fragMvp.outCamPos
                    inLookDir = viewDirNode(fragMvp.outCamPos, ifWorldPos.output).output
                }

                colorOutput(hdrToLdrNode(atmoNd.outColor).outColor)
            }
        }
    }

    class Clip2UvNode(graph: ShaderGraph) : ShaderNode("clip2uv", graph) {
        lateinit var inClipPos: ShaderNodeIoVar
        val outUv = ShaderNodeIoVar(ModelVar2f("${name}_outUv"), this)

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(inClipPos)
        }

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("${outUv.declare()} = ($inClipPos.xy / $inClipPos.w) * 0.5 + 0.5;")
        }
    }

    class ViewToWorldPosNode(graph: ShaderGraph) : ShaderNode("view2worldPos", graph) {
        private val uInvViewMat = UniformMat4f("uInvViewMat")

        var inViewPos = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
        val outWorldPos = ShaderNodeIoVar(ModelVar4f("${name}_outWorldPos"), this)

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(inViewPos)
            shaderGraph.descriptorSet.apply {
                uniformBuffer(name, shaderGraph.stage) {
                    +{ uInvViewMat }
                    onUpdate = { _, cmd ->
                        uInvViewMat.value.set(cmd.renderPass.camera.invView)
                    }
                }
            }
        }

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("${outWorldPos.declare()} = $uInvViewMat * vec4(${inViewPos.ref3f()}, 1.0);")
        }
    }

    class RaySphereIntersectionNode(graph: ShaderGraph) : ShaderNode("raySphereIntersection", graph) {
        override fun generateCode(generator: CodeGenerator) {
            generator.appendFunction("raySphereIntersection", """
                bool raySphereIntersection(vec3 rayOrigin, vec3 rayDir, vec3 center, float radius, out float d1, out float d2) {
                    vec3 centerToOri = rayOrigin - center;
                    float a = dot(rayDir, rayDir);
                    float b = 2.0 * dot(rayDir, centerToOri);
                    float c = dot(centerToOri, centerToOri) - radius * radius;
                    
                    float discriminant = b * b - 4.0 * a * c;
                    if (discriminant < 0.0) {
                        return false;
                    } else {
                        float q = -0.5 * (b + sign(b) * sqrt(discriminant));
                        float t1 = q / a;
                        float t2 = c / q;
                        d1 = min(t1, t2);
                        d2 = max(t1, t2);
                        return d2 > 0.0;
                    }
                }
            """)


            generator.appendFunction("nearestPointToRay", """
                vec4 rayPointDistance(vec3 rayOrigin, vec3 rayDir, vec3 point) {
                    vec3 w = point - rayOrigin;
                    float c1 = dot(w, rayDir);
                    float c2 = dot(rayDir, rayDir);
                    vec3 pn = rayOrigin + rayDir * (c1 / c2);
                    float dist = length(pn - point) * sign(c1);
                    return vec4(pn, dist);
                }
            """)
        }
    }

    class AtmosphereNode(graph: ShaderGraph) : ShaderNode("atmosphereNode", graph) {
        var inSceneColor = ShaderNodeIoVar(ModelVar4fConst(Color.MAGENTA))
        var inScenePos = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
        var inViewDepth = ShaderNodeIoVar(ModelVar1fConst(-1f))
        var inCamPos = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
        var inLookDir = ShaderNodeIoVar(ModelVar3fConst(Vec3f.Z_AXIS))

        val uDirToSun = Uniform3f("uDirToSun")
        val uPlanetCenter = Uniform3f("uPlanetCenter")
        val uPlanetRadius = Uniform1f("uPlanetRadius")
        val uAtmosphereRadius = Uniform1f("uAtmosphereRadius")
        val uDensityFalloff = Uniform1f("uDensityFalloff")
        val uRayleighCoeffs = Uniform4f("uRayleighCoeffs")
        val uMieCoeffs = Uniform2f("uMieCoeffs")
        val uScatteringCoeffs = Uniform3f("uScatteringCoeffs")
        val uSunIntensity = Uniform3f("uSunIntensity")

        val outColor = ShaderNodeIoVar(ModelVar4f("outColor"), this)

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(inSceneColor, inCamPos, inLookDir)

            shaderGraph.descriptorSet.apply {
                uniformBuffer(name, shaderGraph.stage) {
                    +{ uDirToSun }
                    +{ uPlanetCenter }
                    +{ uPlanetRadius }
                    +{ uAtmosphereRadius }
                    +{ uDensityFalloff }
                    +{ uRayleighCoeffs }
                    +{ uMieCoeffs }
                    +{ uScatteringCoeffs }
                    +{ uSunIntensity }
                }
            }
        }

        override fun generateCode(generator: CodeGenerator) {
            val numDepthSamples = 12
            val numScatterSamples = 12

            generator.appendFunction("phaseFunRayleigh", """
                vec3 phaseFunRayleigh(float cosTheta) {
                    vec3 phase = vec3(1.0 + clamp(cosTheta, 0.0, 1.0));
                    return phase * $uRayleighCoeffs.xyz;
                }
            """)

            generator.appendFunction("phaseFunMie", """
                vec3 phaseFunMie(float cosTheta, float g) {
                    float g2 = g * g;
                    float f1 = (3.0 * (1.0 - g2)) / (2.0 * (2.0 + g2));
                    float f2 = (1.0 + cosTheta * cosTheta) / pow(1.0 + g2 - 2.0 * g * cosTheta, 1.5);
                    return vec3(f1 * f2);
                }
            """)

            generator.appendFunction("densityAtPoint", """
                float densityAtPoint(vec3 point) {
                    float height = length(point - $uPlanetCenter) - $uPlanetRadius;
                    float normHeight = clamp(height / ($uAtmosphereRadius - $uPlanetRadius) * 0.8 + 0.2, 0.0, 1.0);
                    return exp(-normHeight * $uDensityFalloff) * (1.0 - normHeight);
                }
            """)

            generator.appendFunction("opticalDepth", """
                float opticalDepth(vec3 origin, vec3 dir, float length) {
                    vec3 samplePt = origin;
                    float stepSz = length / float(${numDepthSamples + 1});
                    float opticalDepth = 0.0;
                    for (int i = 0; i < $numDepthSamples; i++) {
                        samplePt += dir * stepSz;
                        opticalDepth += densityAtPoint(samplePt) * stepSz;
                    }
                    return opticalDepth;
                }
            """)

            generator.appendFunction("calcLight", """
                vec3 calcLight(vec3 origin, vec3 dir, float length, vec3 dirToSun, vec3 scatteringCoeffs, vec3 sunColor) {
                    vec3 inScatterPt = origin;
                    float stepSz = length / float(${numScatterSamples + 1});
                    vec3 inScatteredLight = vec3(0.0);
                    for (int i = 0; i < $numScatterSamples; i++) {
                        inScatterPt += dir * stepSz;
                        
                        float _, dAtmoOut;
                        raySphereIntersection(inScatterPt, dirToSun, $uPlanetCenter, $uAtmosphereRadius, _, dAtmoOut);
                        float sunRayOpticalDepth = opticalDepth(inScatterPt, dirToSun, dAtmoOut);
                        float viewRayOpticalDepth = opticalDepth(inScatterPt, -dir, stepSz * float(i));
                        
                        vec3 transmittance = exp(-(sunRayOpticalDepth + viewRayOpticalDepth) * scatteringCoeffs);
                        float localDensity = densityAtPoint(inScatterPt);
                        
                        inScatteredLight += localDensity * transmittance * scatteringCoeffs * stepSz;
                    }
                    
                    vec3 rayleigh = phaseFunRayleigh(dot(dir, dirToSun)) * $uRayleighCoeffs.w;
                    vec3 mie = phaseFunMie(dot(dir, dirToSun), $uMieCoeffs.x) * $uMieCoeffs.y;
                    return (rayleigh + mie) * inScatteredLight * sunColor;
                }
            """)

            generator.appendMain("""
                float dAtmoIn, dAtmoOut;
                vec3 nrmDirToSun = normalize($uDirToSun);
                bool hitAtmo = raySphereIntersection(${inCamPos.ref3f()}, ${inLookDir.ref3f()}, $uPlanetCenter, $uAtmosphereRadius, dAtmoIn, dAtmoOut);
                
                ${outColor.declare()} = $inSceneColor;
                if (hitAtmo) {
                    // dAtmoIn is negative if camera is inside atmosphere
                    float dToAtmo = max(0.0, dAtmoIn);
                    float dThroughAtmo = dAtmoOut - dToAtmo;
                    if (${inViewDepth.ref1f()} < 0.0) {
                    float sceneDepth = length(${inCamPos.ref3f()} - ${inScenePos.ref3f()});
                        dThroughAtmo = min(dThroughAtmo, sceneDepth - dToAtmo);
                    }
                    
                    vec3 atmoHitPt = ${inCamPos.ref3f()} + ${inLookDir.ref3f()} * dToAtmo;
                    vec3 light = calcLight(atmoHitPt, ${inLookDir.ref3f()}, dThroughAtmo, nrmDirToSun, $uScatteringCoeffs, $uSunIntensity);
                    $outColor.rgb += light;
                    
                    vec4 rayToPoint = rayPointDistance(${inCamPos.ref3f()}, ${inLookDir.ref3f()}, $uPlanetCenter);
                    vec3 nearestPoint = rayToPoint.xyz;
                    float rayPointDist = rayToPoint.w;
                    
                    if (rayPointDist < 0.0) {
                        nearestPoint = ${inCamPos.ref3f()};
                        rayPointDist = length(${inCamPos.ref3f()} - $uPlanetCenter);
                    } else if (rayPointDist < $uPlanetRadius) {
                        float dPlanetIn, dPlanetOut;
                        raySphereIntersection(${inCamPos.ref3f()}, ${inLookDir.ref3f()}, $uPlanetCenter, $uAtmosphereRadius, dPlanetIn, dPlanetOut);
                        nearestPoint = ${inCamPos.ref3f()} + ${inLookDir.ref3f()} * dPlanetIn;
                        rayPointDist = $uPlanetRadius;
                    }
                    
                    float normalDotSun = dot(normalize(nearestPoint - $uPlanetCenter), $uDirToSun);
                    float skyAlpha = smoothstep(-0.3, -0.1, normalDotSun);
                    float heightAlpha = 1.0 - smoothstep(0.3, 1.0, (rayPointDist - $uPlanetRadius) / ($uAtmosphereRadius - $uPlanetRadius));
                    
                    $outColor.a = skyAlpha * heightAlpha;
                }
            """)
        }
    }
}