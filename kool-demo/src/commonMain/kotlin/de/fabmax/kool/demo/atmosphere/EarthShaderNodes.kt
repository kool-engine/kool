package de.fabmax.kool.demo.atmosphere

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.Uniform4f
import de.fabmax.kool.pipeline.UniformColor
import de.fabmax.kool.pipeline.UniformMat4f
import de.fabmax.kool.pipeline.shadermodel.*
import kotlin.math.PI


class HeightMapNode(val heightMap: TextureNode, graph: ShaderGraph) : ShaderNode("heightMap", graph) {
    var inEdgeFlag = ShaderNodeIoVar(ModelVar1iConst(0))
    var inEdgeMask = ShaderNodeIoVar(ModelVar1fConst(0f))
    var inSampleStep = ShaderNodeIoVar(ModelVar1fConst(1f / 40f))
    var inTileName = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
    var inRawTexCoord = ShaderNodeIoVar(ModelVar2fConst(Vec2f.NEG_X_AXIS))
    var inModelMat = ShaderNodeIoVar(ModelVarMat4f("_"))

    var inTexCoord = ShaderNodeIoVar(ModelVar2fConst(Vec2f.NEG_X_AXIS))
    var inNormal = ShaderNodeIoVar(ModelVar3fConst(Vec3f.Y_AXIS))
    var inTangent = ShaderNodeIoVar(ModelVar3fConst(Vec3f.X_AXIS))
    var inPosition = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
    var inStrength = ShaderNodeIoVar(ModelVar1fConst(0.01f))
    var inNrmStrength = ShaderNodeIoVar(ModelVar1fConst(5f))

    val outPosition = ShaderNodeIoVar(ModelVar3f("${name}_outPos"), this)
    val outNormal = ShaderNodeIoVar(ModelVar3f("${name}_outNrm"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inEdgeFlag, inEdgeMask, inSampleStep, inTileName, inRawTexCoord, inModelMat)
        dependsOn(inTexCoord, inNormal, inTangent, inPosition, inStrength)
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendFunction("calcBumpedNormal", """
            vec3 calcBumpedNormal(vec3 n, vec4 t, vec3 bumpN, float strength) {
                vec3 normal = normalize(n);
                vec3 tangent = normalize(t.xyz);
                tangent = normalize(tangent - dot(tangent, normal) * normal);
                vec3 bitangent = cross(normal, tangent) * t.w;
                mat3 tbn = mat3(tangent, bitangent, normal);
                return normalize(mix(normal, tbn * bumpN, strength));
            }
        """)

        generator.appendFunction("sampleEdgeHeight", """
            float sampleEdgeHeight(mat4 modelMat, vec3 tileName, vec2 baseUv, vec2 sampleDir) {
                vec3 p1 = uv2localPos(tileName, baseUv - sampleDir);
                vec3 p2 = uv2localPos(tileName, baseUv + sampleDir);

                vec4 worldPos1 = modelMat * vec4(p1, 1.0);
                vec4 worldPos2 = modelMat * vec4(p2, 1.0);

                vec2 worldUv1 = vec2(atan(worldPos1.x, worldPos1.z) / $PI * 0.5 + 0.5, acos(worldPos1.y) / $PI);
                vec2 worldUv2 = vec2(atan(worldPos2.x, worldPos2.z) / $PI * 0.5 + 0.5, acos(worldPos2.y) / $PI);

                float h1 = ${generator.sampleTexture2d(heightMap.name, "worldUv1")}.x;
                float h2 = ${generator.sampleTexture2d(heightMap.name, "worldUv2")}.x;
                return (h1 + h2) * 0.5;
            }
        """)

        generator.appendMain("""
            float ${name}_hCt = ${generator.sampleTexture2d(heightMap.name, inTexCoord.ref2f())}.x;
            float ${name}_hLt = ${generator.sampleTexture2d(heightMap.name, "${inTexCoord.ref2f()} - vec2(1.0 / 8192.0, 0.0)")}.x;
            float ${name}_hRt = ${generator.sampleTexture2d(heightMap.name, "${inTexCoord.ref2f()} + vec2(1.0 / 8192.0, 0.0)")}.x;
            float ${name}_hUp = ${generator.sampleTexture2d(heightMap.name, "${inTexCoord.ref2f()} - vec2(0.0, 1.0 / 8192.0)")}.x;
            float ${name}_hDn = ${generator.sampleTexture2d(heightMap.name, "${inTexCoord.ref2f()} + vec2(0.0, 1.0 / 8192.0)")}.x;
            
            float ${name}_dx = (${name}_hCt - ${name}_hRt) + (${name}_hLt - ${name}_hCt);
            float ${name}_dy = (${name}_hUp - ${name}_hCt) + (${name}_hCt - ${name}_hDn);
            
            vec3 ${name}_bumpNrm = normalize(vec3(${name}_dx * ${inNrmStrength.ref1f()}, ${name}_dy * ${inNrmStrength.ref1f()}, 1.0));
            vec3 ${name}_bumpTan = ${inTangent.ref3f()};
            vec3 ${name}_localNrm = calcBumpedNormal(${inNormal.ref3f()}, vec4(${name}_bumpTan, 1.0), ${name}_bumpNrm, 1.0);
            ${outNormal.declare()} = ${name}_localNrm;
            
            float ${name}_disp = 0.0;
            int ${name}_edge = ${inEdgeFlag.ref1i()} & int(${inEdgeMask.ref1f()});
            if (${name}_edge == 0) {
                ${name}_disp = ${name}_hCt * ${inStrength.ref1f()};
            } else if (${name}_edge >= ${SphereGridSystem.EDGE_TOP}) {
                // upper / lower edge
                ${name}_disp = sampleEdgeHeight($inModelMat, ${inTileName.ref3f()}, ${inRawTexCoord.ref2f()}, vec2(${inSampleStep.ref1f()}, 0.0)) * ${inStrength.ref1f()};
            } else if (${name}_edge >= 1) {
                // left / right edge
                ${name}_disp = sampleEdgeHeight($inModelMat, ${inTileName.ref3f()}, ${inRawTexCoord.ref2f()}, vec2(0.0, ${inSampleStep.ref1f()})) * ${inStrength.ref1f()};
            }
            
            ${outPosition.declare()} = ${inPosition.ref3f()} * (1.0 + ${name}_disp);
        """)
    }
}

class SpherePosNode(graph: ShaderGraph) : ShaderNode("spherePos", graph) {
    lateinit var inModelMat: ShaderNodeIoVar
    lateinit var inTileName: ShaderNodeIoVar
    lateinit var inXyPos: ShaderNodeIoVar

    val outPos = ShaderNodeIoVar(ModelVar3f("outSpherePos"), this)
    val outNrm = ShaderNodeIoVar(ModelVar3f("outSphereNrm"), this)
    val outTan = ShaderNodeIoVar(ModelVar4f("outSphereTangent"), this)
    val outTex = ShaderNodeIoVar(ModelVar2f("outSphereTexCoord"), this)

    val uUnprojMat = UniformMat4f("uUnprojMat").apply {
        value.setPerspective(90f, 1f, 1f, 10f).invert()
    }

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inModelMat, inTileName, inXyPos)
        shaderGraph.descriptorSet.apply {
            uniformBuffer(name, shaderGraph.stage) {
                +{ uUnprojMat }
            }
        }
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendFunction("uv2localPos", """
            vec3 uv2localPos(vec3 tileName, vec2 uv) {
                float tileScale = 1.0 / tileName.z;
                vec2 tilePos = tileName.xy;
                tilePos.x = tileName.z - 1.0 - tilePos.x;
                vec2 projCoord = tilePos * tileScale + uv * tileScale;
                projCoord = projCoord * 2.0 - 1.0;

                vec4 unproj = uUnprojMat * vec4(projCoord, 1.0, 1.0);
                return normalize(unproj.xyz / unproj.w * -1.0);
            }
        """)

        generator.appendMain("""
            ${outNrm.declare()} = uv2localPos(${inTileName.ref3f()}, ${inXyPos.ref2f()});
            ${outPos.declare()} = $outNrm * 60.0;
            
            vec3 ${name}_worldNrm = ($inModelMat * vec4($outNrm, 0.0)).xyz;
            vec3 ${name}_worldTan = normalize(cross(vec3(0.0, 1.0, 0.0), ${name}_worldNrm));
            ${outTan.declare()} = vec4(transpose(mat3($inModelMat)) * ${name}_worldTan, 1.0);
            ${outTex.declare()} = vec2(atan(${name}_worldNrm.x, ${name}_worldNrm.z) / $PI * 0.5 + 0.5, acos(${name}_worldNrm.y) / $PI);
        """)
    }
}

class EarthRoughnessNode(val inAlbedo: ShaderNodeIoVar, graph: ShaderGraph) : ShaderNode("earthRoughness", graph) {
    val outRoughness = ShaderNodeIoVar(ModelVar1f("earth_outRoughness"), this)
    val outIsOcean = ShaderNodeIoVar(ModelVar1i("earth_outIsOcean"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inAlbedo)
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("""
            float ${name}_relBlue = min($inAlbedo.b - $inAlbedo.r, $inAlbedo.b - $inAlbedo.g) / $inAlbedo.b;
            float ${name}_brightness = dot($inAlbedo.rgb, vec3(0.333));
            
            ${outRoughness.declare()} = 1.0 - (0.3 + ${name}_brightness * 0.4);
            bool $outIsOcean = ${name}_relBlue > 0.2 || ${name}_brightness < 0.002;
            if ($outIsOcean) {
                $outRoughness = 0.22;
            }
        """)
    }
}

class EarthOceanNode(graph: ShaderGraph) : ShaderNode("earthOcean", graph) {
    lateinit var inOceanTex1: TextureNode

    lateinit var inAlbedo: ShaderNodeIoVar
    lateinit var inIsOcean: ShaderNodeIoVar
    lateinit var inNormal: ShaderNodeIoVar
    lateinit var inBumpNormal: ShaderNodeIoVar
    lateinit var inTangent: ShaderNodeIoVar

    val outColor = ShaderNodeIoVar(ModelVar4f("ocean_outColor"), this)
    val outBumpNormal = ShaderNodeIoVar(ModelVar3f("ocean_outBump"), this)

    val uNormalShift = Uniform4f("uNormalShift")
    val uWaterColor = UniformColor("uWaterColor")

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inAlbedo, inIsOcean, inNormal, inBumpNormal, inTangent)

        shaderGraph.pushConstants.apply {
            +{ uNormalShift }
            +{ uWaterColor }
        }
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendFunction("cubeUv", """
                vec2 cubeUv(vec3 normal) {
                    vec3 absN = abs(normal);
                    float maxAxis = 1.0;
                    vec2 uvc = vec2(1.0, 1.0);
                    
                    if (absN.x > absN.y && absN.x > absN.z) {
                        maxAxis = absN.x;
                        uvc = vec2(-normal.z * sign(normal.x), -normal.y);
                    } else if (absN.y > absN.x && absN.y > absN.z) {
                        maxAxis = absN.y;
                        uvc = vec2(normal.x, normal.z * sign(normal.y));
                    } else {
                        maxAxis = absN.z;
                        uvc = vec2(normal.x * sign(normal.z), -normal.y);
                    }
                    return 0.5 * (uvc / maxAxis + 1.0);
                }
            """)

        generator.appendMain("""
                ${outColor.declare()} = ${inAlbedo.ref4f()};
                ${outBumpNormal.declare()} = ${inBumpNormal.ref3f()};
                if ($inIsOcean) {
                    vec2 oceanUvBase = cubeUv(normalize(${inNormal.ref3f()}));
                    float s = 800.0;
                    vec2 oceanUv1 = oceanUvBase * vec2(1.0 * s, 1.5 * s) + $uNormalShift.xy;
                    vec2 oceanUv2 = oceanUvBase * vec2(1.69751 * s, 1.27841 * s) + $uNormalShift.zw;
                    vec2 oceanUvStrength = oceanUvBase;
                    
                    vec3 oceanNrm1 = ${generator.sampleTexture2d(inOceanTex1.name, "oceanUv1")}.rgb * 2.0 - 1.0;
                    vec3 oceanNrm2 = ${generator.sampleTexture2d(inOceanTex1.name, "oceanUv2")}.rgb * 2.0 - 1.0;
                    vec3 oceanNrm = oceanNrm1 + oceanNrm2;
                    float nrmStrength = smoothstep(0.3, 0.6, ${generator.sampleTexture2d(inOceanTex1.name, "oceanUvStrength")}.r) * 0.7 + 0.3;
                    oceanNrm.xy *= nrmStrength;
                    
                    //$outColor = vec4(${generator.sampleTexture2d(inOceanTex1.name, "oceanUvBase")}.rgb, 1.0);
                    $outColor = $uWaterColor;
                    $outBumpNormal = calcBumpedNormal(${inBumpNormal.ref3f()}, ${inTangent.ref4f()}, normalize(oceanNrm), 0.4);
                }
            """)
    }
}