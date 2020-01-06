package de.fabmax.kool.demo

import de.fabmax.kool.OffscreenPassImpl
import de.fabmax.kool.ViewDirection
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.pipeline.pipelineConfig
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.CullMethod
import de.fabmax.kool.scene.PerspectiveCamera
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
            ${outColor.declare()} = vec4(${input.ref3f()} * pow(2.0, ${input.ref4f()}.w * 255.0 - 127.0), 1.0);

            //$outColor.rgb = 0.5 * pow($outColor.rgb, vec3(0.9));
            $outColor.rgb = $outColor.rgb / ($outColor.rgb + vec3(1.0));
            
            $outColor.rgb = pow($outColor.rgb, vec3(1.0 / 2.2));
        """)
    }
}

fun hdriToCubeMapPass(): OffscreenPassImpl {
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
                                val rgbeDec = addNode(DecodeRgbeNode(stage)).apply {
                                    input = sampler.outColor
                                }
                                colorOutput = rgbeDec.outColor
                            }
                        }
                        ModeledShader.TextureColor(model, texName).setup(mesh, buildCtx, ctx)
                    }

                    onPipelineCreated += {
                        (it.shader as ModeledShader.TextureColor).textureSampler.texture = Texture { assets ->
                            //assets.loadImageData("skybox/hdri/driving_school.rgbe.png")
                            //assets.loadImageData("skybox/hdri/newport_loft.rgbe.png")
                            assets.loadImageData("skybox/hdri/lakeside_2k.rgbe.png")
                            //assets.loadImageData("skybox/hdri/spruit_sunrise_2k.rgbe.png")
                        }
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
