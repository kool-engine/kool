package de.fabmax.kool.editor.overlays

import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.api.scene
import de.fabmax.kool.editor.ui.UiColors
import de.fabmax.kool.input.KeyboardInput
import de.fabmax.kool.input.Pointer
import de.fabmax.kool.math.RayTest
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.FullscreenShaderUtil.fullscreenQuadVertexStage
import de.fabmax.kool.pipeline.FullscreenShaderUtil.generateFullscreenQuad
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.launchDelayed
import de.fabmax.kool.util.logT
import kotlin.math.max

class SelectionOverlay(val editor: KoolEditor) : Node("Selection overlay") {

    var selection: Set<GameEntity> = emptySet()
        private set
    val selectionState = mutableStateOf(selection)
    val onSelectionChanged = mutableListOf<(Set<GameEntity>) -> Unit>()

    val selectionPass = SelectionPass(editor)
    private val overlayMesh = Mesh(Attribute.POSITIONS, Attribute.TEXTURE_COORDS)
    private val outlineShader = SelectionOutlineShader(selectionPass.colorTexture)

    private var updateSelection = false

    private val currentSelection = mutableSetOf<GameEntity>()
    private val prevSelection = mutableSetOf<GameEntity>()
    private val meshSelection = mutableMapOf<Mesh, SelectedMesh>()

    var selectionColor by outlineShader::outlineColorPrimary
    var selectionColorChildren by outlineShader::outlineColorChild

    init {
        overlayMesh.generateFullscreenQuad()
        overlayMesh.shader = outlineShader
        overlayMesh.isVisible = false
        addNode(overlayMesh)

        onSelectionChanged += {
            updateSelection = true
            editor.editMode.updateGizmo()
        }

        onUpdate {
            selectionColor = editor.ui.uiColors.value.primary
            selectionColorChildren = UiColors.selectionChild

            if (selectionPass.isEnabled) {
                val vp = editor.editorOverlay.mainRenderPass.viewport
                val sceneWidth = vp.width
                val sceneHeight = vp.height
                selectionPass.setSize(sceneWidth, sceneHeight)
            }

            if (updateSelection) {
                updateSelection = false
                prevSelection.clear()
                prevSelection += currentSelection
                meshSelection.clear()
                prevSelection
                    .filter { it.isSceneChild }
                    .forEach { collectMeshes(it, it.drawNode) }

                launchDelayed(1) {
                    // delay disable by 1 frame, so that selectionPass clears its output
                    selectionPass.isEnabled = meshSelection.isNotEmpty()
                    overlayMesh.isVisible = meshSelection.isNotEmpty()
                }
            }
        }
    }

    fun clickSelect(ptr: Pointer) {
        val sceneModel = editor.activeScene.value ?: return
        val appScene = sceneModel.scene

        val rayTest = RayTest()
        if (appScene.computePickRay(ptr, rayTest.ray)) {
            rayTest.clear()
            var selectedNodeModel: GameEntity? = editor.sceneObjectsOverlay.pick(rayTest)
            val distOv = if (rayTest.isHit) rayTest.hitDistanceSqr else Float.POSITIVE_INFINITY

            rayTest.clear()
            appScene.rayTest(rayTest)
            if (rayTest.isHit && rayTest.hitDistanceSqr < distOv) {
                var hitModel: GameEntity? = null
                var it = rayTest.hitNode
                while (it != null) {
                    hitModel = sceneModel.nodesToEntities[it]
                    if (hitModel != null) {
                        break
                    }
                    it = it.parent
                }
                selectedNodeModel = hitModel ?: selectedNodeModel
            }

            selectSingle(selectedNodeModel)
        }
    }

    fun selectSingle(selectModel: GameEntity?, expandIfShiftIsDown: Boolean = true, toggleSelect: Boolean = true) {
        val selectList = selectModel?.let { listOf(it) } ?: emptyList()

        if (toggleSelect && selectModel in currentSelection) {
            if (expandIfShiftIsDown && (KeyboardInput.isShiftDown || KeyboardInput.isCtrlDown)) {
                reduceSelection(selectList)
            } else {
                clearSelection()
            }
        } else if (expandIfShiftIsDown && (KeyboardInput.isShiftDown || KeyboardInput.isCtrlDown)) {
            expandSelection(selectList)
        } else {
            setSelection(selectList)
        }
    }

    fun clearSelection() = setSelection(emptyList())

    fun expandSelection(addModels: List<GameEntity>) = setSelection(currentSelection + addModels.toSet())

    fun reduceSelection(removeModel: GameEntity) = setSelection(currentSelection - removeModel)
    fun reduceSelection(removeModels: List<GameEntity>) = setSelection(currentSelection - removeModels.toSet())

    fun setSelection(selectModels: Collection<GameEntity>) {
        if (currentSelection != selectModels) {
            currentSelection.clear()
            currentSelection += selectModels

            selection = currentSelection.toSet().also { sel ->
                selectionState.set(sel)
                onSelectionChanged.forEach { it(sel) }
            }
        }
    }

    fun getSelectedNodes(filter: (GameEntity) -> Boolean = { true }): List<GameEntity> {
        return currentSelection.filter(filter)
    }

    fun getSelectedSceneNodes(filter: (GameEntity) -> Boolean = { true }): List<GameEntity> {
        return currentSelection.filter { it.isSceneChild && filter(it) }
    }

    fun isSelected(gameEntity: GameEntity): Boolean {
        return gameEntity in currentSelection
    }

    fun invalidateSelection() {
        prevSelection.clear()
        meshSelection.clear()
        selectionPass.disposePipelines()
    }

    private fun collectMeshes(model: GameEntity, node: Node) {
        if (node is Mesh && meshSelection[node]?.type != MeshSelectionType.PRIMARY) {
            var owner: GameEntity? = null
            var it: Node? = node
            while (owner == null && it != null) {
                owner = model.scene.nodesToEntities[it]
                if (owner == null) {
                    it = it.parent
                }
            }

            val selectionType = if (owner == model) MeshSelectionType.PRIMARY else MeshSelectionType.CHILD_NODE
            meshSelection[node] = SelectedMesh(node, selectionType)
        }
        node.children.forEach { collectMeshes(model, it) }
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
                q.forEach {
                    setupDrawCommand(it, ev)
                }
            }
        }

        private fun setupDrawCommand(cmd: DrawCommand, updateEvent: UpdateEvent) {
            cmd.isActive = false
            val selection = meshSelection[cmd.mesh]
            if (selection != null) {
                getPipeline(cmd.mesh, updateEvent)?.let { (shader, pipeline) ->
                    shader.color = selectionColors[selection.type] ?: COLOR_OTHER_SEL
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

        private val COLOR_PRIMARY_SEL = Color(255f/255f, 0f, 0f, 1f)
        private val COLOR_CHILD_SEL = Color(254f/255f, 0f, 0f, 1f)
        private val COLOR_OTHER_SEL = Color(1f/255f, 0f, 0f, 1f)
        private val selectionColors = mapOf(
            MeshSelectionType.PRIMARY to COLOR_PRIMARY_SEL,
            MeshSelectionType.CHILD_NODE to COLOR_CHILD_SEL
        )
    }

    private data class SelectedMesh(val mesh: Mesh, val type: MeshSelectionType)

    private enum class MeshSelectionType {
        PRIMARY,
        CHILD_NODE
    }

    private data class ShaderAndPipeline(val shader: KslUnlitShader, val pipeline: DrawPipeline)

    private class SelectionOutlineShader(selectionMask: Texture2d?) :
        KslShader(
            Model(),
            PipelineConfig(
                blendMode = BlendMode.BLEND_MULTIPLY_ALPHA,
                cullMethod = CullMethod.NO_CULLING,
                depthTest = DepthCompareOp.ALWAYS,
                isWriteDepth = false
            )
        )
    {
        var outlineColorPrimary by uniformColor("uOutlineColorPrim", Color.WHITE)
        var outlineColorChild by uniformColor("uOutlineColorChild", Color.WHITE)

        init {
            texture2d("tSelectionMask", selectionMask)
        }

        class Model : KslProgram("Selection outline shader") {
            init {
                val uv = interStageFloat2("uv")
                fullscreenQuadVertexStage(uv)

                val colorPrim = uniformFloat4("uOutlineColorPrim")
                val colorChild = uniformFloat4("uOutlineColorChild")

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
                            val color = float4Var(Color.MAGENTA.const)
                            `if` (maxMask eq COLOR_PRIMARY_SEL.r.const) {
                                color set colorPrim
                            }.elseIf(maxMask eq COLOR_CHILD_SEL.r.const) {
                                color set colorChild
                            }
                            color.a *= clamp(min(minMaskCount, maxMaskCount) / max(minMaskCount, maxMaskCount) * 2f.const, 0f.const, 1f.const)
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