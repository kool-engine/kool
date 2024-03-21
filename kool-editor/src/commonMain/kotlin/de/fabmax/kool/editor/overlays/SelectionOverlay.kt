package de.fabmax.kool.editor.overlays

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.model.NodeModel
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.input.KeyboardInput
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.modules.ui2.mutableStateListOf
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.FullscreenShaderUtil.fullscreenQuadVertexStage
import de.fabmax.kool.pipeline.FullscreenShaderUtil.generateFullscreenQuad
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.copy
import de.fabmax.kool.util.launchDelayed
import de.fabmax.kool.util.logT
import kotlin.math.max

class SelectionOverlay(editor: KoolEditor) : Node("Selection overlay") {

    val selection = mutableStateListOf<NodeModel>()
    val onSelectionChanged = mutableListOf<(List<NodeModel>) -> Unit>()

    val selectionPass = SelectionPass(editor)
    private val overlayMesh = Mesh(Attribute.POSITIONS, Attribute.TEXTURE_COORDS)
    private val outlineShader = SelectionOutlineShader(selectionPass.colorTexture)

    private var updateSelection = false
    private val prevSelection = mutableSetOf<NodeModel>()
    private val meshSelection = mutableSetOf<Mesh>()

    var selectionColor by outlineShader::outlineColor

    init {
        overlayMesh.generateFullscreenQuad()
        overlayMesh.shader = outlineShader
        overlayMesh.isVisible = false
        addNode(overlayMesh)

        onSelectionChanged += { updateSelection = true }

        onUpdate {
            selectionColor = editor.ui.uiColors.value.primary

            if (selectionPass.isEnabled) {
                val vp = editor.editorOverlay.mainRenderPass.viewport
                val sceneWidth = vp.width
                val sceneHeight = vp.height
                selectionPass.setSize(sceneWidth, sceneHeight)
            }

            if (updateSelection) {
                updateSelection = false
                prevSelection.clear()
                prevSelection += selection
                meshSelection.clear()
                prevSelection
                    .filter { it !is SceneModel }
                    .forEach { it.drawNode.selectChildMeshes() }

                launchDelayed(1) {
                    // delay disable by 1 frame, so that selectionPass clears its output
                    selectionPass.isEnabled = meshSelection.isNotEmpty()
                    overlayMesh.isVisible = meshSelection.isNotEmpty()
                }
            }
        }
    }

    fun selectSingle(selectModel: NodeModel?, expandIfShiftIsDown: Boolean = true, toggleSelect: Boolean = true) {
        val selectList = selectModel?.let { listOf(it) } ?: emptyList()

        if (toggleSelect && selectModel in selection) {
            if (expandIfShiftIsDown && KeyboardInput.isShiftDown) {
                reduceSelection(selectList)
            } else {
                clearSelection()
            }
        } else if (expandIfShiftIsDown && KeyboardInput.isShiftDown) {
            expandSelection(selectList)
        } else {
            setSelection(selectList)
        }
    }

    fun clearSelection() = setSelection(emptyList())

    fun expandSelection(addModels: List<NodeModel>) = setSelection(selection.toSet() + addModels.toSet())

    fun reduceSelection(removeModels: List<NodeModel>) = setSelection(selection.toSet() - removeModels.toSet())

    fun setSelection(selectModels: Collection<NodeModel>) {
        if (selection != selectModels) {
            selection.atomic {
                clear()
                addAll(selectModels)
            }
            onSelectionChanged.forEach { it(selection) }
        }
    }

    fun getSelectedNodes(filter: (NodeModel) -> Boolean = { true }): List<NodeModel> {
        return selection.copy().filter(filter)
    }

    fun getSelectedSceneNodes(filter: (SceneNodeModel) -> Boolean = { true }): List<SceneNodeModel> {
        return selection.copy().filterIsInstance<SceneNodeModel>().filter(filter)
    }

    fun invalidateSelection() {
        prevSelection.clear()
        meshSelection.clear()
        selectionPass.disposePipelines()
    }

    private fun Node.selectChildMeshes() {
        if (this is Mesh) {
            meshSelection += this
        }
        children.forEach { it.selectChildMeshes() }
    }

    inner class SelectionPass(editor: KoolEditor) : OffscreenRenderPass2d(
        // drawNode will be replaced by content scene, once it is loaded
        Node(),
        colorAttachmentDefaultDepth(TexFormat.R),
        Vec2i(128),
        name = "selection-overlay"
    ) {
        private val selectionPipelines = mutableMapOf<Int, ShaderAndPipeline?>()

        init {
            camera = editor.editorOverlay.camera
            clearColor = Color.BLACK
            isUpdateDrawNode = false
            isEnabled = true

            onAfterCollectDrawCommands += { ev ->
                // replace regular object shaders by selection shader
                val q = ev.view.drawQueue
                var i = 0
                q.forEach {
                    setupDrawCommand(i++, it, ev)
                }
            }
        }

        private fun setupDrawCommand(i: Int, cmd: DrawCommand, updateEvent: UpdateEvent) {
            cmd.isActive = false
            if (cmd.mesh in meshSelection) {
                getPipeline(cmd.mesh, updateEvent)?.let { (shader, pipeline) ->
                    shader.color = selectionColors[i % selectionColors.size]
                    cmd.pipeline = pipeline
                    cmd.isActive = true
                }
            }
        }

        private fun getPipeline(mesh: Mesh, updateEvent: UpdateEvent): ShaderAndPipeline? {
            if (!mesh.geometry.hasAttribute(Attribute.POSITIONS)) {
                return null
            }
            return selectionPipelines.getOrPut(mesh.id) {
                logT { "Creating selection shader for mesh ${mesh.id}" }
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
                ShaderAndPipeline(shader, shader.getOrCreatePipeline(mesh, updateEvent))
            }
        }

        fun disposePipelines() {
            selectionPipelines.values.forEach { it?.pipeline?.release() }
            selectionPipelines.clear()
        }

        override fun release() {
            super.release()
            disposePipelines()
        }
    }

    companion object {
        private const val defaultMaxNumberOfJoints = 16
        private val selectionColors = (1..255).map { Color(it/255f, 0f, 0f, 1f) }
    }

    private data class ShaderAndPipeline(val shader: KslUnlitShader, val pipeline: DrawPipeline)

    private class SelectionOutlineShader(selectionMask: Texture2d?) :
        KslShader(
            Model(),
            PipelineConfig(
                blendMode = BlendMode.BLEND_MULTIPLY_ALPHA,
                cullMethod = CullMethod.NO_CULLING,
                depthTest = DepthCompareOp.ALWAYS,
            )
        )
    {
        var outlineColor by uniformColor("uOutlineColor", Color.WHITE)

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
                            color.a set clamp(min(minMaskCount, maxMaskCount) / max(minMaskCount, maxMaskCount) * 2f.const, 0f.const, 1f.const)
                            colorOutput(color)
                        }.`else` {
                            discard()
                        }
                    }
                }
            }

            companion object {
                private const val rE = 2f
                private const val rC = 1f
                val samplePattern = listOf(
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
    }
}