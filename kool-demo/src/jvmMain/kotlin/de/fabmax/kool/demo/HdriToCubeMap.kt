package de.fabmax.kool.demo

import de.fabmax.kool.OffscreenPassImpl
import de.fabmax.kool.ViewDirection
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.CubeMapTexture
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.pipeline.pipelineConfig
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.CullMethod
import de.fabmax.kool.scene.PerspectiveCamera
import de.fabmax.kool.scene.mesh
import de.fabmax.kool.scene.scene
import de.fabmax.kool.util.Color
import kotlin.math.PI

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
            ${outColor.declare()} = vec4(${input.ref3f()} * pow(2.0, ${input.ref4f()}.w * 255.0 - 127.0), 1.0);

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

fun hdriToCubeMapPass(): OffscreenPassImpl {
    //assets.loadImageData("skybox/hdri/driving_school.rgbe.png")
    //assets.loadImageData("skybox/hdri/newport_loft-js.rgbe.png")
    //assets.loadImageData("skybox/hdri/newport_loft.rgbe.png")
    //assets.loadImageData("skybox/hdri/lakeside_2k.rgbe.png")
    //assets.loadImageData("skybox/hdri/spruit_sunrise_2k.rgbe.png")

    return hdriToCubeMapPass(Texture { it.loadImageData("skybox/hdri/newport_loft.rgbe.png") })
}

fun hdriToCubeMapPass(hdri: Texture): OffscreenPassImpl {
    return OffscreenPassImpl(512, 512, true).apply {
        clearColor = Color.GRAY

        val cube = scene {
            (camera as PerspectiveCamera).let {
                it.position.set(Vec3f.ZERO)
                it.fovy = 90f
                it.clipNear = 0.1f
                it.clipFar = 10f
            }

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
        scene = cube

        val pos = 1f
        val camPositions = mutableMapOf(
                ViewDirection.FRONT to Vec3f(0f, 0f, pos),
                ViewDirection.BACK to Vec3f(0f, 0f, -pos),
                ViewDirection.LEFT to Vec3f(-pos, 0f, 0f),
                ViewDirection.RIGHT to Vec3f(pos, 0f, 0f),
                ViewDirection.UP to Vec3f(0f, -pos, 0f),
                ViewDirection.DOWN to Vec3f(0f, pos, 0f)
        )
        onRender = { viewDir, _ ->
            cube.camera.lookAt.set(camPositions[viewDir]!!)
            when (viewDir) {
                ViewDirection.UP -> cube.camera.up.set(Vec3f.NEG_Z_AXIS)
                ViewDirection.DOWN -> cube.camera.up.set(Vec3f.Z_AXIS)
                else -> cube.camera.up.set(Vec3f.NEG_Y_AXIS)
            }
        }
    }
}

class ConvoluteIrradianceNode(val texture: TextureNode, graph: ShaderGraph) : ShaderNode("convIrradiance", graph) {
    var inLocalPos = ShaderNodeIoVar(ModelVar3fConst(Vec3f.X_AXIS))
//    var inNormal = ShaderNodeIoVar(ModelVar3fConst(Vec3f.X_AXIS))
//    var inTangent = ShaderNodeIoVar(ModelVar3fConst(Vec3f.Y_AXIS))
    val outColor = ShaderNodeIoVar(ModelVar4f("convIrradiance_outColor"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inLocalPos)
//        dependsOn(inNormal)
//        dependsOn(inTangent)
        dependsOn(texture)
    }

    override fun generateCode(generator: CodeGenerator) {
        super.generateCode(generator)

        generator.appendFunction("sampleEquiRect", """
            const vec2 invAtan = vec2(0.1591, 0.3183);
            vec3 sampleEquiRect(vec3 texCoord) {
                vec3 equiRect_in = normalize(texCoord);
                vec2 uv = vec2(atan(equiRect_in.z, equiRect_in.x), asin(equiRect_in.y));
                uv *= invAtan;
                uv += 0.5;
                
                vec4 rgbe = ${generator.sampleTexture2d(texture.name, "uv")};
                
                // decode rgbe
                return rgbe.rgb * pow(2.0, rgbe.w * 255.0 - 127.0);
            }
        """)

        val phiMin = 0.0 * PI
        val phiMax = 2.0 * PI
        val thetaMin = 0.0 * PI
        val thetaMax = 0.5 * PI
        generator.appendMain("""
            //vec3 normal = normalize({inNormal.ref3f()});
            //vec3 up = normalize({inTangent.ref3f()});
            vec3 normal = normalize(${inLocalPos.ref3f()});
            vec3 up = vec3(0.0, 1.0, 0.0);
            vec3 right = normalize(cross(up, normal));
            up = cross(normal, right);

            float sampleDelta = 0.00937;
            vec3 irradiance = vec3(0.0);
            int nrSamples = 0; 
            
            for (float theta = $thetaMin; theta < $thetaMax; theta += sampleDelta) {
                float deltaPhi = sampleDelta / sin(theta);
                for (float phi = $phiMin; phi < $phiMax; phi += deltaPhi) {
                    vec3 tempVec = cos(phi) * right + sin(phi) * up;
                    vec3 sampleVector = cos(theta) * normal + sin(theta) * tempVec;
                    irradiance += sampleEquiRect(sampleVector).rgb * cos(theta) * 0.6;
                    nrSamples++;
                }
            }
            irradiance = irradiance * $PI / float(nrSamples);
            ${outColor.declare()} = vec4(irradiance, 1.0);
        """)
    }
}

class IrradianceMapPass(hdriTexture: Texture) {

    val offscreenPass: OffscreenPassImpl
    val irradianceMap: CubeMapTexture
        get() = offscreenPass.textureCube
    var hdriTexture = hdriTexture
        set(value) {
            irrMapShader?.textureSampler?.texture = value
            field = value
        }

    private var irrMapShader: ModeledShader.TextureColor? = null

    init {
        offscreenPass = OffscreenPassImpl(32, 32, true).apply {
            isSingleShot = true
            val cube = scene {
                (camera as PerspectiveCamera).let {
                    it.position.set(Vec3f.ZERO)
                    it.fovy = 90f
                    it.clipNear = 0.1f
                    it.clipFar = 10f
                }

                +mesh(setOf(Attribute.POSITIONS)) {
                    generator = {
                        cube { centerOrigin() }
//                        sphere {
//                            steps = 100
//                            radius = 1f
//                        }
                        meshData.generateTangents()
                    }

                    pipelineConfig {
                        // cube is viewed from inside
                        cullMethod = CullMethod.CULL_FRONT_FACES

                        shaderLoader = { mesh, buildCtx, ctx ->
                            val texName = "colorTex"
                            val model = ShaderModel("Irradiance Convolution Sampler").apply {
                                val ifLocalPos: StageInterfaceNode
                                //val ifNormal: StageInterfaceNode
                                //val ifTangent: StageInterfaceNode
                                vertexStage {
                                    ifLocalPos = stageInterfaceNode("ifLocalPos", attrPositions().output)
                                    //ifNormal = stageInterfaceNode("ifNormals", attrNormals().output)
                                    //ifTangent = stageInterfaceNode("ifTangents", attrTangents().output)
                                    positionOutput = simpleVertexPositionNode().outPosition
                                }
                                fragmentStage {
                                    val tex = textureNode(texName)
                                    val convNd = addNode(ConvoluteIrradianceNode(tex, stage)).apply {
                                        inLocalPos = ifLocalPos.output
                                        //inNormal = ifNormal.output
                                        //inTangent = ifTangent.output
                                    }
                                    colorOutput = convNd.outColor
                                }
                            }
                            ModeledShader.TextureColor(model, texName).setup(mesh, buildCtx, ctx)
                        }

                        onPipelineCreated += {
                            irrMapShader = (it.shader as ModeledShader.TextureColor)
                            irrMapShader!!.textureSampler.texture = hdriTexture
                        }
                    }
                }
            }
            scene = cube

            val pos = 1f
            val camPositions = mutableMapOf(
                    ViewDirection.FRONT to Vec3f(0f, 0f, pos),
                    ViewDirection.BACK to Vec3f(0f, 0f, -pos),
                    ViewDirection.LEFT to Vec3f(-pos, 0f, 0f),
                    ViewDirection.RIGHT to Vec3f(pos, 0f, 0f),
                    ViewDirection.UP to Vec3f(0f, -pos, 0f),
                    ViewDirection.DOWN to Vec3f(0f, pos, 0f)
            )
            onRender = { viewDir, _ ->
                cube.camera.lookAt.set(camPositions[viewDir]!!)
                when (viewDir) {
                    ViewDirection.UP -> cube.camera.up.set(Vec3f.NEG_Z_AXIS)
                    ViewDirection.DOWN -> cube.camera.up.set(Vec3f.Z_AXIS)
                    else -> cube.camera.up.set(Vec3f.NEG_Y_AXIS)
                }
            }
        }
    }

}

fun hdriToIrradianceMapPass(hdri: Texture): IrradianceMapPass {
    return IrradianceMapPass(hdri)
}
