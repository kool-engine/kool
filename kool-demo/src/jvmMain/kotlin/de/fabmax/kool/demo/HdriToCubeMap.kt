package de.fabmax.kool.demo

import de.fabmax.kool.OffscreenPassCube
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.pipeline.pipelineConfig
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.CullMethod
import de.fabmax.kool.scene.mesh
import de.fabmax.kool.scene.scene
import de.fabmax.kool.util.Color

class EquiRectCoords(graph: ShaderGraph) : ShaderNode("equiRect", graph) {
    var input = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
    val outUv = ShaderNodeIoVar(ModelVar2f("equiRect_uv"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(input)
    }

    override fun generateCode(generator: CodeGenerator) {
        super.generateCode(generator)
        generator.appendFunction("invAtan", "const vec2 invAtan = vec2(0.1591, 0.3183);")
        generator.appendMain("""
            vec3 equiRect_in = normalize(${input.ref3f()});
            ${outUv.declare()} = vec2(atan(equiRect_in.z, equiRect_in.x), asin(equiRect_in.y));
            ${outUv.ref2f()} *= invAtan;
            ${outUv.ref2f()} += 0.5;
        """)
    }
}

class DecodeRgbeNode(graph: ShaderGraph) : ShaderNode("decodeRgbe", graph) {
    var input = ShaderNodeIoVar(ModelVar4fConst(Color.MAGENTA))
    val outColor = ShaderNodeIoVar(ModelVar4f("decodeRgbe_outColor"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(input)
    }

    override fun generateCode(generator: CodeGenerator) {
        super.generateCode(generator)

        generator.appendMain("""
            // decode rgbe
            ${outColor.declare()} = vec4(${input.ref3f()} * pow(2.0, ${input.ref4f()}.w * 255.0 - 127.0), 1.0);
        """)
    }
}

class DecodeAndToneMapRgbeNode(graph: ShaderGraph) : ShaderNode("decodeRgbe", graph) {
    var input = ShaderNodeIoVar(ModelVar4fConst(Color.MAGENTA))
    val outColor = ShaderNodeIoVar(ModelVar4f("decodeRgbe_outColor"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(input)
    }

    override fun generateCode(generator: CodeGenerator) {
        super.generateCode(generator)

        generator.appendFunction("uncharted2ToneMap", """
            vec3 uncharted2Tonemap_func(vec3 x) {
                float A = 0.15;
                float B = 0.50;
                float C = 0.10;
                float D = 0.20;
                float E = 0.02;
                float F = 0.30;
                
                return ((x*(A*x+C*B)+D*E)/(x*(A*x+B)+D*F))-E/F;
            }
            
            vec3 uncharted2Tonemap(vec3 rgbLinear) {
                float W = 11.2;
                
                float ExposureBias = 2.0;
                vec3 curr = uncharted2Tonemap_func(ExposureBias * rgbLinear);
                
                vec3 whiteScale = 1.0 / uncharted2Tonemap_func(vec3(W));
                vec3 color = curr * whiteScale;
                
                return pow(color, vec3(1.0 / 2.2));
            }
        """)

        generator.appendFunction("acesFilm", """
            vec3 ACESFilm(vec3 x) {
                float a = 2.51f;
                float b = 0.03f;
                float c = 2.43f;
                float d = 0.59f;
                float e = 0.14f;
                vec3 color = clamp((x*(a*x+b))/(x*(c*x+d)+e), 0.0, 1.0);
                return pow(color, vec3(1.0 / 2.2));
            }
        """)

        generator.appendMain("""
            // decode rgbe
            vec3 fRgb = ${input.ref4f()}.rgb;
            float fExp = ${input.ref4f()}.a * 255.0 - 128.0;
            
            ${outColor.declare()} = vec4(fRgb * pow(2.0, fExp), 1.0);

            //$outColor.rgb = 0.5 * pow($outColor.rgb, vec3(0.9));
            //$outColor.rgb = $outColor.rgb / ($outColor.rgb + vec3(1.0));
            
            //$outColor.rgb = pow($outColor.rgb, vec3(1.0 / 2.2));

            // from: http://filmicworlds.com/blog/filmic-tonemapping-operators/
            // Jim Hejl and Richard Burgess-Dawson
            // no need for pow(rgb, 1.0/2.2)!
            vec3 x = max(vec3(0), $outColor.rgb - 0.004);
            $outColor.rgb = (x * (6.2 * x + 0.5)) / (x * (6.2 * x + 1.7) + 0.06);
            
            // uncharted 2
            //$outColor.rgb = uncharted2Tonemap($outColor.rgb);
            
            // https://knarkowicz.wordpress.com/2016/01/06/aces-filmic-tone-mapping-curve/
            //$outColor.rgb = ACESFilm($outColor.rgb);
            
            // maybe also worth a try:
            //https://github.com/TheRealMJP/BakingLab/blob/master/BakingLab/ACES.hlsl
        """)
    }
}

fun hdriToCubeMapPass(hdri: Texture): OffscreenPassCube {
    return OffscreenPassCube(512, 512, 1).apply {
        clearColor = Color.GRAY

        scene = scene {
            +mesh(setOf(Attribute.POSITIONS, Attribute.TEXTURE_COORDS)) {
                generator = {
                    cube { centerOrigin() }
                }
                pipelineConfig {
                    // cube is viewed from inside
                    cullMethod = CullMethod.CULL_FRONT_FACES

                    shaderLoader = { mesh, buildCtx, ctx ->
                        val texName = "colorTex"
                        val model = ShaderModel("Equi Rect Sampler").apply {
                            val ifLocalPos: StageInterfaceNode
                            vertexStage {
                                ifLocalPos = stageInterfaceNode("ifLocalPos", attrPositions().output)
                                positionOutput = simpleVertexPositionNode().outPosition
                            }
                            fragmentStage {
                                val equiRect = addNode(EquiRectCoords(stage)).apply {
                                    input = ifLocalPos.output
                                }
                                val sampler = textureSamplerNode(textureNode(texName), equiRect.outUv, false)
                                val rgbeDec = addNode(DecodeAndToneMapRgbeNode(stage)).apply {
                                    input = sampler.outColor
                                }
                                colorOutput = rgbeDec.outColor
                            }
                        }
                        ModeledShader.TextureColor(model, texName).setup(mesh, buildCtx, ctx)
                    }

                    onPipelineCreated += {
                        (it.shader as ModeledShader.TextureColor).textureSampler.texture = hdri
                    }
                }
            }
        }
    }
}
