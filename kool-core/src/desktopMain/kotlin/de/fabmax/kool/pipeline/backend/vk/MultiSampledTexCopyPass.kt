package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.addMesh
import de.fabmax.kool.scene.geometry.PrimitiveType
import de.fabmax.kool.util.logE
import org.lwjgl.vulkan.VK10.*

class MultiSampledTexCopyPass(
    val backend: RenderBackendVk,
    texFormat: TexFormat,
    filterMethod: FilterMethod
) : OffscreenPass2d(
    drawNode = Node(),
    attachmentConfig = AttachmentConfig(
        colorAttachments = ColorAttachmentTextures(listOf(
            TextureAttachmentConfig(texFormat, SamplerSettings(minFilter = filterMethod, magFilter = filterMethod).clamped())
        )),
        depthAttachment = DepthAttachmentNone
    ),
    initialSize = Vec2i(128, 128),
    name = "MultiSampledTexCopyPass"
) {

    private val device: Device get() = backend.device

    private val copyPassVk = impl as OffscreenPass2dVk
    private val copyShader = CopyShader()
    private val drawCmd: DrawCommand

    private val copySource = Texture2d()

    init {
        drawNode.addMesh(
            Attribute.POSITIONS,
            primitiveType = PrimitiveType.TRIANGLE_STRIP
        ) {
            geometry.addVertex(Vec3f(-1f, 1f, 0f))
            geometry.addVertex(Vec3f( 1f, 1f, 0f))
            geometry.addVertex(Vec3f(-1f,-1f, 0f))
            geometry.addVertex(Vec3f( 1f,-1f, 0f))
            geometry.addIndices(0, 1, 2, 3)

            shader = copyShader
        }
        copyShader.texture2d("copySrc", copySource)

        update(KoolSystem.requireContext())
        drawCmd = mainView.drawQueue.orderedQueues.first().commands.first()
    }

    fun doCopyPass(source: ImageVk, passEncoderState: PassEncoderState) {
        passEncoderState.ensureRenderPassInactive()

        setSize(source.width, source.height, 1)
        if (copySource.gpuTexture != source) {
            copySource.loadingState = Texture.LoadingState.LOADED
            copySource.gpuTexture = backend.swapchain.depthImage
        }

        backend.pipelineManager.prepareDrawPipeline(drawCmd, passEncoderState)
        passEncoderState.beginRenderPass(this, copyPassVk, 0)
        if (!backend.pipelineManager.bindDrawPipeline(drawCmd, passEncoderState)) {
            logE { "Failed to bind copy bind pipeline" }
        }

        vkCmdDrawIndexed(passEncoderState.commandBuffer, 4, 1, 0, 0, 0)
        passEncoderState.ensureRenderPassInactive()
    }

    private class CopyShader : DrawShader("copy-shader") {
        override fun createPipeline(mesh: Mesh, instances: MeshInstanceList?, ctx: KoolContext): DrawPipeline {
            val pipelineConfig = PipelineConfig(
                blendMode = BlendMode.DISABLED,
                cullMethod = CullMethod.NO_CULLING,
                depthTest = DepthCompareOp.ALWAYS,
                isWriteDepth = false,
            )

            val attribs = listOf(VertexLayout.VertexAttribute(0, 0, Attribute.POSITIONS))
            val bindings = listOf(VertexLayout.Binding(0, InputRate.VERTEX, attribs))
            val vertexLayout = VertexLayout(bindings, PrimitiveType.TRIANGLE_STRIP)

            val pipelineLayout = BindGroupLayout.Builder(1, BindGroupScope.PIPELINE).apply {
                textures += Texture2dLayout("copySrc", setOf(ShaderStage.FRAGMENT_SHADER), TextureSampleType.UNFILTERABLE_FLOAT)
            }.create()
            val emptyView = BindGroupLayout.Builder(0, BindGroupScope.VIEW).create()
            val emptyMesh = BindGroupLayout.Builder(2, BindGroupScope.MESH).create()
            val layouts = BindGroupLayouts(emptyView, pipelineLayout, emptyMesh)

            return DrawPipeline(name, pipelineConfig, vertexLayout, layouts) {
                val vertexStage = ShaderStageVk.fromSource("copy-shader-vert", VERT_SRC, VK_SHADER_STAGE_VERTEX_BIT)
                val fragmentStage = ShaderStageVk.fromSource("copy-shader-frag", FRAG_SRC, VK_SHADER_STAGE_FRAGMENT_BIT)
                ShaderCodeVk(listOf(vertexStage, fragmentStage))
            }
        }
    }

    companion object {
        private val VERT_SRC = """
            #version 450
            
            layout(location=0) in vec3 attrib_positions;
            layout(location=0) smooth out vec2 uv;
            
            void main() {
                uv = (attrib_positions.xy + 1.0) * 0.5;
                uv.y = 1.0 - uv.y;      
                gl_Position = vec4(attrib_positions, 1.0);
            }
        """.trimIndent()

        private val FRAG_SRC = """
            #version 450
            precision highp float;
            
            layout(set=1, binding=0) uniform sampler2DMS copySrc;
 
            layout(location=0) smooth in vec2 uv;
            layout(location = 0) out vec4 outColor;

            void main() {
                ivec2 texCoord = ivec2(textureSize(copySrc) * uv);
                outColor = texelFetch(copySrc, texCoord, 0);
            }
        """.trimIndent()
    }
}