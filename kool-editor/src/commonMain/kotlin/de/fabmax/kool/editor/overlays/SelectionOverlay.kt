package de.fabmax.kool.editor.overlays

import de.fabmax.kool.KoolContext
import de.fabmax.kool.editor.EditorState
import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.components.ContentComponent
import de.fabmax.kool.editor.model.EditorNodeModel
import de.fabmax.kool.editor.ui.EditorUi
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
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

    private var prevSelection: EditorNodeModel? = null
    private val meshSelection = mutableSetOf<Mesh>()

    var selectionColor by outlineShader::outlineColor

    init {
        overlayMesh.generateFullscreenQuad()
        overlayMesh.shader = outlineShader
        overlayMesh.isVisible = false
        addNode(overlayMesh)

        selectionColor = EditorUi.EDITOR_THEME_COLORS.primary

        onUpdate {
            if (selectionPass.isEnabled) {
                val vp = editor.editorOverlay.mainRenderPass.viewport
                val sceneWidth = (vp.width * 0.75f).roundToInt()
                val sceneHeight = (vp.height * 0.75f).roundToInt()
                if (sceneWidth != selectionPass.width || sceneHeight != selectionPass.height) {
                    selectionPass.resize(sceneWidth, sceneHeight, it.ctx)
                }
            }

            val selectedNode = EditorState.selectedNode.value
            if (selectedNode != prevSelection) {
                prevSelection = selectedNode
                meshSelection.clear()
                selectedNode?.getComponent<ContentComponent>()?.contentNode?.selectChildMeshes()

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
            //addColorTexture(TexFormat.RGBA)
            addColorTexture(TexFormat.R)
        }
    ) {
        private val selectionPipelines = mutableMapOf<Long, Pipeline?>()

        init {
            camera = editor.editorOverlay.camera
            clearColor = Color.BLACK
            isUpdateDrawNode = false
            isEnabled = true

            drawMeshFilter = { it in meshSelection }

            onAfterCollectDrawCommands += { ctx ->
                // replace regular object shaders by selection shader
                for (i in drawQueue.commands.indices) {
                    setupDrawCommand(drawQueue.commands[i], ctx)
                }
            }
        }

        fun disposePipelines(ctx: KoolContext) {
            selectionPipelines.values.forEach { it?.let { ctx.disposePipeline(it) } }
            selectionPipelines.clear()
        }

        private fun setupDrawCommand(cmd: DrawCommand, ctx: KoolContext) {
            cmd.pipeline = getPipeline(cmd.mesh, ctx)
        }

        private fun getPipeline(mesh: Mesh, ctx: KoolContext): Pipeline? {
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
                    color { constColor(Color.WHITE) }
                }
                shader.createPipeline(mesh, ctx)
            }
        }

        override fun dispose(ctx: KoolContext) {
            super.dispose(ctx)
            selectionPipelines.values.filterNotNull().forEach { ctx.disposePipeline(it) }
        }
    }

    companion object {
        private const val defaultMaxNumberOfJoints = 16
    }

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

                        val avgMask = float3Var(Vec3f.ZERO.const)
                        val maxMask = float3Var(Vec3f.ZERO.const)
                        samplePattern.forEach {
                            val maskVal = float3Var(sampleTexture(mask, uv.output + it.const * texelSz).rgb)
                            avgMask += maskVal
                            maxMask set max(maxMask, maskVal)
                        }
                        // needed in case maxMask should be used as outline color - would allow distinct outline colors
                        // per selected object
                        //maxSamples.forEach {
                        //    maxMask set max(maxMask, float3Var(sampleTexture(mask, uv.output + it.const * texelSz).rgb))
                        //}

                        avgMask set avgMask / samplePattern.size.toFloat().const
                        val dotMax = float1Var(dot(maxMask, maxMask))
                        val dif = float1Var(abs(dot(avgMask, avgMask) - dotMax) / dotMax)
                        `if`((dotMax gt 0.1f.const) and (dif gt 0.05f.const)) {
                            val a = smoothStep(0f.const, 0.25f.const, dif) * (1f.const - smoothStep(0.75f.const, 1f.const, dif))
                            val outlineColor = float4Var(uniformFloat4("uOutlineColor"))
                            outlineColor.a *= a
                            colorOutput(outlineColor)
                        }.`else` {
                            discard()
                        }
                    }
                }
            }

            companion object {
                val cfg = PipelineConfig()

                val rE = 1.5f
                val rC = 1.25f
                val samplePattern = listOf(
                    Vec2f(-rC, -rC),
                    Vec2f(0f, -rE),
                    Vec2f(rC, -rC),
                    Vec2f(-rE, 0f),
                    Vec2f(0f, 0f),
                    Vec2f(rE, 0f),
                    Vec2f(-rC, rC),
                    Vec2f(0f, rE),
                    Vec2f(rC, rC)
                )

                val maxSamples = listOf(
                    Vec2f(-2f, -2f),
                    Vec2f(-2f, 2f),
                    Vec2f(2f, -2f),
                    Vec2f(2f, 2f)
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