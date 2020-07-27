package de.fabmax.kool.util.ibl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.mesh
import de.fabmax.kool.util.logD
import kotlin.math.PI

class IrradianceMapPass(parentScene: Scene, envMap: CubeMapTexture) :
        OffscreenRenderPassCube(Group(), renderPassConfig {
            name = "IrradianceMapPass"
            setSize(32, 32)
            addColorTexture(TexFormat.RGBA_F16)
            clearDepthTexture()
        }) {

    init {
        clearColor = null

        (drawNode as Group).apply {
            +mesh(listOf(Attribute.POSITIONS)) {
                generate {
                    cube { centered() }
                }

                val texName = "colorTex"
                val model = ShaderModel("IrradianceMapPass").apply {
                    val ifLocalPos: StageInterfaceNode
                    vertexStage {
                        ifLocalPos = stageInterfaceNode("ifLocalPos", attrPositions().output)
                        positionOutput = simpleVertexPositionNode().outVec4
                    }
                    fragmentStage {
                        val tex = cubeMapNode(texName)
                        val convNd = addNode(ConvoluteIrradianceNode(tex, stage)).apply {
                            inLocalPos = ifLocalPos.output
                        }
                        colorOutput(convNd.outColor)
                    }
                }
                shader = ModeledShader.CubeMapColor(envMap, texName, model).apply {
                    onPipelineSetup += { builder, _, _ -> builder.cullMethod = CullMethod.CULL_FRONT_FACES }
                }
            }
        }

        parentScene.addOffscreenPass(this)

        // this pass only needs to be rendered once, remove it immediately after first render
        onAfterDraw += { ctx ->
            logD { "Generated irradiance map from cube map: ${envMap.name}" }
            parentScene.removeOffscreenPass(this)
            ctx.runDelayed(1) { dispose(ctx) }
        }
    }

    override fun dispose(ctx: KoolContext) {
        drawNode.dispose(ctx)
        super.dispose(ctx)
    }

    private class ConvoluteIrradianceNode(val texture: CubeMapNode, graph: ShaderGraph) : ShaderNode("convIrradiance", graph) {
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
                        vec3 envColor = min(${generator.sampleTextureCube(texture.name, "sampleVector")}.rgb, vec3(${maxLightIntensity.ref1f()}));
                        irradiance += envColor * cos(theta) * 0.6;
                        nrSamples++;
                    }
                }
                irradiance = irradiance * $PI / float(nrSamples);
                ${outColor.declare()} = vec4(irradiance, 1.0);
            """)
        }
    }
}