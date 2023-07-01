package de.fabmax.kool.demo.menu

import de.fabmax.kool.demo.Demos
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.randomI
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.mvpMatrix
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.modules.ui2.Ui2Shader
import de.fabmax.kool.modules.ui2.UiNode
import de.fabmax.kool.modules.ui2.UiRenderer
import de.fabmax.kool.modules.ui2.UiSurface
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.util.Time
import de.fabmax.kool.util.createUint8Buffer
import kotlin.random.Random

class TitleBgRenderer(
    val titleBgMesh: BgMesh,
    val fromColor: Float,
    val toColor: Float,
    val topRadius: Float = 0f,
    val bottomRadius: Float = 0f
) : UiRenderer<UiNode> {

    override fun renderUi(node: UiNode) {
        val meshLayer = node.surface.getMeshLayer(node.modifier.zLayer + UiSurface.LAYER_BACKGROUND)
        val isFirstUsage = meshLayer.addCustomLayer("title-bg") { titleBgMesh }
        if (isFirstUsage) {
            titleBgMesh.bgInstances.clear()
        }

        titleBgMesh.bgInstances.addInstance {
            put(node.clipBoundsPx.array)
            put(node.leftPx)
            put(node.topPx)
            put(node.widthPx)
            put(node.heightPx)
            put(fromColor)
            put(toColor)
            put(topRadius)
            put(bottomRadius)
        }
    }

    class BgMesh : Mesh(IndexedVertexList(Ui2Shader.UI_MESH_ATTRIBS)) {
        val bgInstances = MeshInstanceList(listOf(
            Ui2Shader.ATTRIB_CLIP,
            CategoryShader.ATTRIB_DIMENS,
            CategoryShader.ATTRIB_GRADIENT_RANGE
        ))

        private val catShader = CategoryShader()

        init {
            shader = catShader
            instances = bgInstances
            generate {
                grid {
                    yDir.set(Vec3f.Y_AXIS)
                    center.set(0.5f, 0.5f, 0f)

                    stepsX = 6
                    stepsY = 4
                    sizeX = 1.2f
                    sizeY = 2f
                }
            }
            onUpdate += {
                catShader.noiseOffset += Time.deltaT * 0.01f
            }
        }
    }

    private class CategoryShader : KslShader(Model(), pipelineConfig) {
        val colorTex by texture1d("tGradient", GradientTexture(Demos.demoColors, size = 512))
        val noiseTex by texture2d("tNoise", generateNoiseTex())
        var noiseOffset by uniform1f("uNoiseOffset")

        private fun generateNoiseTex(): Texture2d {
            val width = 32
            val height = 32
            val data = createUint8Buffer(width * height * 4)

            val r = Random(13654164)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    data.put(r.randomI(0, 255).toByte())
                    data.put(r.randomI(0, 255).toByte())
                    data.put(r.randomI(0, 255).toByte())
                    data.put(r.randomI(0, 255).toByte())
                }
            }

            data.flip()
            val noiseProps = TextureProps(mipMapping = false, maxAnisotropy = 0)
            return Texture2d(noiseProps, loader = BufferedTextureLoader(TextureData2d(data, width, height, TexFormat.RGBA)))
        }

        private class Model : KslProgram("Demo category shader") {
            init {
                val screenPos = interStageFloat2()
                val texCoords = interStageFloat2(interpolation = KslInterStageInterpolation.Flat)
                val clipBounds = interStageFloat4(interpolation = KslInterStageInterpolation.Flat)
                val clipCornerRadius = interStageFloat2(interpolation = KslInterStageInterpolation.Flat)

                vertexStage {
                    main {
                        clipBounds.input set instanceAttribFloat4(Ui2Shader.ATTRIB_CLIP.name)
                        val uvRange = float4Var(instanceAttribFloat4(ATTRIB_GRADIENT_RANGE.name))
                        val meshUv = float2Var(vertexAttribFloat2(Attribute.TEXTURE_COORDS.name))
                        clipCornerRadius.input set uvRange.zw
                        meshUv.x += uniformFloat1("uNoiseOffset")
                        meshUv.y += uvRange.x

                        val noise = sampleTexture(texture2d("tNoise"), meshUv)
                        val pos = float3Var(vertexAttribFloat3(Attribute.POSITIONS.name))
                        pos.xy += (noise.xy * 2f.const - 1f.const) * Vec2f(0.1f, 0.5f).const

                        texCoords.input set pos.xy
                        texCoords.input.x set uvRange.x + clamp(pos.x + pos.y * 0.2f.const, (-0.1f).const, 1.1f.const) * (uvRange.y - uvRange.x)

                        val dimens = float4Var(instanceAttribFloat4(ATTRIB_DIMENS.name))
                        pos.x set dimens.x + pos.x * dimens.z
                        pos.y set dimens.y + pos.y * dimens.w

                        screenPos.input set pos.xy
                        outPosition set mvpMatrix().matrix * float4Value(pos, 1f.const)
                    }
                }
                fragmentStage {
                    main {
                        val gradientTex = texture1d("tGradient")

                        `if` (all(screenPos.output gt clipBounds.output.xy) and
                                all(screenPos.output lt clipBounds.output.zw)) {

                            // rounded upper corners
                            val p = screenPos.output
                            var r = clipCornerRadius.output.x
                            val lt = clipBounds.output.x
                            val up = clipBounds.output.y
                            val rt = clipBounds.output.z
                            val dn = clipBounds.output.w
                            val cLt = float2Var(float2Value(lt + r, up + r))
                            val cRt = float2Var(float2Value(rt - r, up + r))
                            `if` ((all(p lt cLt) and (length(cLt - p) gt r)) or
                                    ((p.x gt cRt.x) and (p.y lt cRt.y) and (length(cRt - p) gt r))) {
                                discard()
                            }

                            // rounded bootm corners
                            r = clipCornerRadius.output.y
                            cLt set float2Value(lt + r, dn - r)
                            cRt set float2Value(rt - r, dn - r)
                            `if` ((all(p gt cRt) and (length(cRt - p) gt r)) or
                                    ((p.x lt cLt.x) and (p.y gt cLt.y) and (length(cLt - p) gt r))) {
                                discard()
                            }

                            val color = sampleTexture(gradientTex, texCoords.output.x)
                            colorOutput(color)
                        }.`else` {
                            discard()
                        }
                    }
                }
            }
        }

        companion object {
            val ATTRIB_DIMENS = Attribute("aDimens", GlslType.VEC_4F)
            val ATTRIB_GRADIENT_RANGE = Attribute("aGradientRange", GlslType.VEC_4F)

            val pipelineConfig = PipelineConfig().apply {
                blendMode = BlendMode.BLEND_PREMULTIPLIED_ALPHA
                cullMethod = CullMethod.NO_CULLING
                depthTest = DepthCompareOp.DISABLED
            }
        }
    }
}