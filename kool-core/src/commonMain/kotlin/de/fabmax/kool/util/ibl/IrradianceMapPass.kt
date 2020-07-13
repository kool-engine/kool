package de.fabmax.kool.util.ibl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.mesh
import kotlin.math.PI

class IrradianceMapPass(private val parentScene: Scene, hdriTexture: Texture) : OffscreenRenderPassCube(Group(), 32, 32, 1, TexFormat.RGBA_F16) {
    var hdriTexture = hdriTexture
        set(value) {
            irrMapShader?.texture = value
            field = value
        }

    private var irrMapShader: ModeledShader.TextureColor? = null

    init {
        clearColor = null

        (drawNode as Group).apply {
            +mesh(listOf(Attribute.POSITIONS)) {
                generate {
                    cube { centered() }
                }

                val texName = "colorTex"
                val model = ShaderModel("Irradiance Convolution Sampler").apply {
                    val ifLocalPos: StageInterfaceNode
                    vertexStage {
                        ifLocalPos = stageInterfaceNode("ifLocalPos", attrPositions().output)
                        positionOutput = simpleVertexPositionNode().outVec4
                    }
                    fragmentStage {
                        val tex = textureNode(texName)
                        val convNd = addNode(ConvoluteIrradianceNode(tex, stage)).apply {
                            inLocalPos = ifLocalPos.output
                        }
                        colorOutput(convNd.outColor)
                    }
                }
                irrMapShader = ModeledShader.TextureColor(hdriTexture, texName, model).apply {
                    onPipelineSetup += { builder, _, _ -> builder.cullMethod = CullMethod.CULL_FRONT_FACES }
                }
                shader = irrMapShader
            }
        }

        update()

        // this pass only needs to be rendered once, remove it immediately after first render
        onAfterCollectDrawCommands += {
            parentScene.removeOffscreenPass(this)
        }

        parentScene.onDispose += { ctx ->
            this@IrradianceMapPass.dispose(ctx)
        }
    }

    fun update() {
        parentScene.addOffscreenPass(this)
    }

    override fun dispose(ctx: KoolContext) {
        drawNode.dispose(ctx)
        super.dispose(ctx)
    }

    private class ConvoluteIrradianceNode(val texture: TextureNode, graph: ShaderGraph) : ShaderNode("convIrradiance", graph) {
        var inLocalPos = ShaderNodeIoVar(ModelVar3fConst(Vec3f.X_AXIS))
        var maxLightIntensity = ShaderNodeIoVar(ModelVar1fConst(5000f))
        val outColor = ShaderNodeIoVar(ModelVar4f("convIrradiance_outColor"), this)

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(inLocalPos)
            dependsOn(texture)
        }

        override fun generateCode(generator: CodeGenerator) {
            super.generateCode(generator)

            generator.appendFunction("sampleEquiRect", """
                const vec2 invAtan = vec2(0.1591, 0.3183);
                vec3 sampleEquiRect(vec3 texCoord) {
                    vec3 equiRect_in = normalize(texCoord);
                    vec2 uv = vec2(atan(equiRect_in.z, equiRect_in.x), -asin(equiRect_in.y));
                    uv *= invAtan;
                    uv += 0.5;
                    
                    // decode rgbe
                    vec4 rgbe = ${generator.sampleTexture2d(texture.name, "uv", "0.0")};
                    vec3 fRgb = rgbe.rgb;
                    float fExp = rgbe.a * 255.0 - 128.0;
                    return min(fRgb * pow(2.0, fExp), vec3(${maxLightIntensity.ref1f()}));
                }
            """)

            val phiMax = 2.0 * PI
            val thetaMax = 0.5 * PI
            generator.appendMain("""
                vec3 normal = normalize(${inLocalPos.ref3f()});
                vec3 up = vec3(0.0, 1.0, 0.0);
                vec3 right = normalize(cross(up, normal));
                up = cross(normal, right);
    
                float sampleDelta = 0.00737;
                vec3 irradiance = vec3(0.0);
                int nrSamples = 0; 
                
                for (float theta = 0.0; theta < $thetaMax; theta += sampleDelta) {
                    float deltaPhi = sampleDelta / sin(theta);
                    for (float phi = 0.0; phi < $phiMax; phi += deltaPhi) {
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
}