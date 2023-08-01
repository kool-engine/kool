package de.fabmax.kool.editor.overlays

import de.fabmax.kool.KoolContext
import de.fabmax.kool.editor.EditorState
import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.components.ContentComponent
import de.fabmax.kool.editor.model.NodeModel
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.FullscreenShaderUtil.fullscreenQuadVertexStage
import de.fabmax.kool.pipeline.FullscreenShaderUtil.generateFullscreenQuad
import de.fabmax.kool.pipeline.drawqueue.DrawCommand
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.launchDelayed
import kotlin.math.max
import kotlin.math.roundToInt

class SelectionOverlay(editor: KoolEditor) : Node("Selection overlay") {

    val selectionPass = SelectionPass(editor)
    private val overlayMesh = Mesh(Attribute.POSITIONS, Attribute.TEXTURE_COORDS)
    private val outlineShader = SelectionOutlineShader(selectionPass.colorTexture)

    private val prevSelection = mutableSetOf<NodeModel>()
    private val meshSelection = mutableSetOf<Mesh>()

    var selectionColor by outlineShader::outlineColor

    init {
        overlayMesh.generateFullscreenQuad()
        overlayMesh.shader = outlineShader
        overlayMesh.isVisible = false
        addNode(overlayMesh)

        onUpdate { evt ->
            selectionColor = editor.ui.uiColors.value.primary

            if (selectionPass.isEnabled) {
                val vp = editor.editorOverlay.mainRenderPass.viewport
                val sceneWidth = (vp.width * 0.75f).roundToInt()
                val sceneHeight = (vp.height * 0.75f).roundToInt()
                if (sceneWidth != selectionPass.width || sceneHeight != selectionPass.height) {
                    selectionPass.resize(sceneWidth, sceneHeight, evt.ctx)
                }
            }

            if (prevSelection.size != EditorState.selection.size || EditorState.selection.any { it !in prevSelection }) {
                prevSelection.clear()
                prevSelection += EditorState.selection
                meshSelection.clear()
                prevSelection.forEach { it.getComponent<ContentComponent>()?.contentNode?.selectChildMeshes() }

                launchDelayed(1) {
                    // delay disable by 1 frame, so that selectionPass clears its output
                    selectionPass.isEnabled = meshSelection.isNotEmpty()
                    overlayMesh.isVisible = meshSelection.isNotEmpty()
                }
            }
        }
    }

    private fun Node.selectChildMeshes() {
        if (this is Mesh) {
            meshSelection += this
        }
        children.forEach { it.selectChildMeshes() }
    }

    inner class SelectionPass(editor: KoolEditor) : OffscreenRenderPass2d(
        Node(),
        renderPassConfig {
            name = "SelectionPass"
            setDynamicSize()
            addColorTexture(TexFormat.R)
            addColorTexture {
                colorFormat = TexFormat.R
                magFilter = FilterMethod.NEAREST
                minFilter = FilterMethod.NEAREST
            }
        }
    ) {
        private val selectionPipelines = mutableMapOf<Long, ShaderAndPipeline?>()

        init {
            camera = editor.editorOverlay.camera
            clearColor = Color.BLACK
            isUpdateDrawNode = false
            isEnabled = true

            drawMeshFilter = { it in meshSelection }

            onAfterCollectDrawCommands += { ctx ->
                // replace regular object shaders by selection shader
                for (i in drawQueue.commands.indices) {
                    setupDrawCommand(i, drawQueue.commands[i], ctx)
                }
            }
        }

        private fun setupDrawCommand(i: Int, cmd: DrawCommand, ctx: KoolContext) {
            cmd.pipeline = null
            getPipeline(cmd.mesh, ctx)?.let { (shader, pipeline) ->
                shader.color = selectionColors[i % selectionColors.size]
                cmd.pipeline = pipeline
            }
        }

        private fun getPipeline(mesh: Mesh, ctx: KoolContext): ShaderAndPipeline? {
            if (!mesh.geometry.hasAttribute(Attribute.POSITIONS)) {
                return null
            }
            return selectionPipelines.getOrPut(mesh.id) {
                val shader = KslUnlitShader {
                    pipeline { cullMethod = CullMethod.NO_CULLING }
                    vertices {
                        isInstanced = mesh.instances != null
                        mesh.skin?.let {
                            enableArmature(max(defaultMaxNumberOfJoints, it.nodes.size))
                        }
                        morphAttributes += mesh.geometry.getMorphAttributes()
                    }
                    color { uniformColor(Color.WHITE) }
                }
                ShaderAndPipeline(shader, shader.createPipeline(mesh, ctx))
            }
        }

        fun disposePipelines(ctx: KoolContext) {
            selectionPipelines.values.forEach { it?.let { ctx.disposePipeline(it.pipeline) } }
            selectionPipelines.clear()
        }

        override fun dispose(ctx: KoolContext) {
            super.dispose(ctx)
            disposePipelines(ctx)
        }
    }

    companion object {
        private const val defaultMaxNumberOfJoints = 16
        private val selectionColors = (1..255).map { Vec4f(it/255f, 0f, 0f, 1f) }
    }

    private data class ShaderAndPipeline(val shader: KslUnlitShader, val pipeline: Pipeline)

    private class SelectionOutlineShader(selectionMask: Texture2d?) : KslShader(Model(), pipelineCfg) {
        var outlineColor by uniform4f("uOutlineColor", Color.WHITE)

        init {
            texture2d("tSelectionMask", selectionMask)
        }

        class Model : KslProgram("Selection outline shader") {
            init {
                val uv = interStageFloat2("uv")
                fullscreenQuadVertexStage(uv)

                fragmentStage {
                    main {
                        val mask = texture2d("tSelectionMask")
                        val texelSz = float2Var(Vec2f(1f, 1f).const / textureSize2d(mask).toFloat2())

                        val minMask = float1Var(2f.const)
                        val maxMask = float1Var((-1f).const)
                        val minMaskCount = float1Var(0f.const)
                        val maxMaskCount = float1Var(0f.const)

                        samplePattern.forEach {
                            val maskVal = float1Var(sampleTexture(mask, uv.output + it.const * texelSz).r)
//                            minMask set min(minMask, maskVal)
//                            maxMask set max(maxMask, maskVal)
                            `if`(maskVal lt minMask) {
                                minMask set maskVal
                                minMaskCount set 0f.const
                            }
                            `if`(maskVal gt maxMask) {
                                maxMask set maskVal
                                maxMaskCount set 0f.const
                            }
                            minMaskCount += (maskVal eq minMask).toFloat1()
                            maxMaskCount += (maskVal eq maxMask).toFloat1()
                        }

                        `if`(minMask ne maxMask) {
                            val color = float4Var(uniformFloat4("uOutlineColor"))
                            color.a set clamp(min(minMaskCount, maxMaskCount) / max(minMaskCount, maxMaskCount) * 4f.const, 0f.const, 1f.const)
                            colorOutput(color)
                        }.`else` {
                            discard()
                        }
                    }
                }
            }

            companion object {
                private const val rE = 1f
                private const val rC = 1f
                val samplePattern = listOf(
                    //Vec2f(0f, 0f),
                    Vec2f(-rC, -rC),
                    Vec2f(rC, -rC),
                    Vec2f(-rC, rC),
                    Vec2f(rC, rC),
                    Vec2f(0f, -rE),
                    Vec2f(-rE, 0f),
                    Vec2f(rE, 0f),
                    Vec2f(0f, rE)
                )
            }
        }

        companion object {
            val pipelineCfg = PipelineConfig().apply {
                cullMethod = CullMethod.NO_CULLING
                depthTest = DepthCompareOp.DISABLED
            }
        }
    }

}