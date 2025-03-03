package de.fabmax.kool.editor.overlays

import de.fabmax.kool.KoolContext
import de.fabmax.kool.editor.KoolEditor
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.components.MeshComponent
import de.fabmax.kool.editor.components.localToViewF
import de.fabmax.kool.editor.ui.UiColors
import de.fabmax.kool.input.KeyboardInput
import de.fabmax.kool.math.RayTest
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.modules.ksl.blocks.VertexTransformBlock
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.FullscreenShaderUtil.fullscreenQuadVertexStage
import de.fabmax.kool.pipeline.FullscreenShaderUtil.generateFullscreenQuad
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.launchDelayed
import de.fabmax.kool.util.logT
import kotlin.math.max

class SelectionOverlay(val overlay: OverlayScene) : Node("Selection overlay"), EditorOverlay {
    private val editor: KoolEditor get() = overlay.editor

    var selection: Set<GameEntity> = emptySet()
        private set(value) {
            field = value
            selectionState.set(value)
            onSelectionChanged.forEach { it(value) }
            updateOverlay = true
        }

    val selectionState = mutableStateOf(selection)
    val onSelectionChanged = mutableListOf<(Set<GameEntity>) -> Unit>()

    private val selectedMeshes = mutableMapOf<NodeId, SelectedMeshes>()
    private val selectionInstances = mutableMapOf<NodeId, MeshInstanceList>()
    private val expectedNodeIds = mutableSetOf<NodeId>()
    private var updateOverlay = false

    val selectionPass = SelectionPass()
    private val overlayMesh = Mesh(Attribute.POSITIONS, Attribute.TEXTURE_COORDS)
    private val outlineShader = SelectionOutlineShader(selectionPass.colorTexture)

    var selectionColor by outlineShader::outlineColorPrimary
    var selectionColorChildren by outlineShader::outlineColorChild

    init {
        overlayMesh.generateFullscreenQuad()
        overlayMesh.shader = outlineShader
        overlayMesh.isVisible = false
        addNode(overlayMesh)

        onSelectionChanged += { editor.editMode.updateGizmo() }

        onUpdate {
            selectionColor = editor.ui.uiColors.value.primary
            selectionColorChildren = UiColors.selectionChild

            if (selectionPass.isEnabled) {
                val vp = overlay.mainRenderPass.viewport
                val sceneWidth = vp.width
                val sceneHeight = vp.height
                selectionPass.setSize(sceneWidth, sceneHeight)
            }

            if (updateOverlay) {
                updateOverlay = false
                selectedMeshes.clear()
                getSelectedSceneEntities().forEach { collectMeshes(it) }
                launchDelayed(1) {
                    // delay disable by 1 frame, so that selectionPass clears its output
                    selectionPass.isEnabled = selectedMeshes.isNotEmpty()
                    overlayMesh.isVisible = selectedMeshes.isNotEmpty()
                }
            }
        }
    }

    override fun pick(rayTest: RayTest): GameEntity? {
        return overlay.currentScene?.hitTest?.hitTest(rayTest)
    }

    fun selectSingle(selectModel: GameEntity?, expandIfShiftIsDown: Boolean = true, toggleSelect: Boolean = true) {
        val selectList = selectModel?.let { listOf(it) } ?: emptyList()
        if (toggleSelect && selectModel in selection) {
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

    fun expandSelection(addModels: List<GameEntity>) = setSelection(selection + addModels.toSet())

    fun reduceSelection(removeModels: List<GameEntity>) = setSelection(selection - removeModels.toSet())

    fun setSelection(selectModels: Collection<GameEntity>) {
        if (selection != selectModels) {
            selection = selectModels.toSet()
        }
    }

    fun getSelectedEntities(filter: (GameEntity) -> Boolean = { true }): List<GameEntity> {
        return selection.filter(filter)
    }

    fun getSelectedSceneEntities(): List<GameEntity> = getSelectedEntities { it.isSceneChild }

    fun isSelected(gameEntity: GameEntity): Boolean {
        return gameEntity in selection
    }

    fun refreshSelection() {
        updateOverlay = true
    }

    fun invalidateSelection() {
        selectedMeshes.clear()
        selectionPass.disposePipelines()
        selectionInstances.values.forEach { it.release() }
        selectionInstances.clear()
        updateOverlay = true
    }

    private fun collectMeshes(entity: GameEntity) {
        entity.getComponent<MeshComponent>()?.let { meshComponent ->
            meshComponent.sceneNode?.let { sceneNode ->
                val meshSelection = when (sceneNode) {
                    is Mesh -> listOf(selectedMeshes.getOrPut(sceneNode.id) { SelectedMeshes(sceneNode.id) })
                    is Model -> sceneNode.meshes.values.map { selectedMeshes.getOrPut(it.id) { SelectedMeshes(sceneNode.id) } }
                    else -> emptyList()
                }
                val selectionType = if (entity in selection) MeshSelectionType.PRIMARY else MeshSelectionType.CHILD
                meshSelection.forEach {
                    it.selectedInstances += SelectedInstance(meshComponent, selectionType, entity.id.value.toInt())
                }
            }
        }

        // recursively append *non-selected* child nodes to selection (selected ones are handled by top-level
        // selection handler)
        for (i in entity.children.indices) {
            val child = entity.children[i]
            if (child !in selection) {
                collectMeshes(child)
            }
        }
    }

    inner class SelectionPass : OffscreenPass2d(
        drawNode = Node(),  // drawNode will be replaced by content scene, once it is loaded
        attachmentConfig = AttachmentConfig.singleColorDefaultDepth(TexFormat.RG),
        Vec2i(128),
        name = "selection-overlay"
    ) {
        private val selectionPipelines = mutableMapOf<NodeId, DrawPipeline?>()

        init {
            camera = editor.editorCam
            isUpdateDrawNode = false
            isEnabled = true
            defaultView.drawFilter = { it !is Mesh || it.id in selectedMeshes }

            onAfterCollectDrawCommands += { ev ->
                expectedNodeIds.clear()
                expectedNodeIds += selectedMeshes.keys

                // replace regular object shaders by selection shader
                val q = ev.view.drawQueue
                q.forEach {
                    expectedNodeIds -= it.mesh.id
                    setupDrawCommand(it, ev.ctx)
                }

                if (expectedNodeIds.isNotEmpty()) {
                    refreshSelection()
                }
            }
        }

        private fun setupDrawCommand(cmd: DrawCommand, ctx: KoolContext) {
            cmd.isActive = false
            val selection = selectedMeshes[cmd.mesh.id]
            if (selection != null) {
                getPipeline(cmd.mesh, ctx)?.let { pipeline ->
                    cmd.pipeline = pipeline
                    cmd.isActive = true
                    cmd.instances = selection.updateInstances()
                }
            }
        }

        private fun getPipeline(mesh: Mesh, ctx: KoolContext): DrawPipeline? {
            val meshSelection = selectedMeshes[mesh.id]
            if (!mesh.geometry.hasAttribute(Attribute.POSITIONS) || meshSelection == null) {
                return null
            }
            return selectionPipelines.getOrPut(mesh.id) {
                logT { "Creating selection shader for mesh ${mesh.id}" }
                val shader = KslUnlitShader {
                    pipeline {
                        cullMethod = CullMethod.NO_CULLING
                        blendMode = BlendMode.DISABLED
                    }
                    vertices {
                        isInstanced = true
                        mesh.skin?.let {
                            enableArmature(max(DEFAULT_NUM_JOINTS, it.nodes.size))
                        }
                        morphAttributes += mesh.geometry.getMorphAttributes()

                        (mesh.shader as? KslShader)?.let { ksl ->
                            ksl.program.vertexStage?.findBlock<VertexTransformBlock>()?.cfg?.modelMatrixComposition
                        }?.let { modelMatrixComposition = it }
                    }

                    modelCustomizer = {
                        val typeAndId = interStageInt2()
                        vertexStage {
                            main {
                                val type = int1Var(instanceAttribFloat4(meshId).x.toInt1())
                                val id = int1Var(instanceAttribFloat4(meshId).y.toInt1())
                                typeAndId.input set int2Value(type, id)
                            }
                        }
                        fragmentStage {
                            main {
                                val r = int1Var((typeAndId.output.x shl 4.const) or ((typeAndId.output.y and 0xf00.const) shr 8.const))
                                val g = int1Var(typeAndId.output.y and 0xff.const)
                                colorOutput(float4Value(r.toFloat1() / 255f.const, g.toFloat1() / 255f.const, 0f.const, 0f.const))
                            }
                        }
                    }
                }
                shader.getOrCreatePipeline(mesh, ctx, meshSelection.instanceList)
            }
        }

        fun disposePipelines() {
            selectionPipelines.values.forEach { it?.release() }
            selectionPipelines.clear()
        }

        override fun release() {
            super.release()
            disposePipelines()
        }
    }

    companion object {
        private const val DEFAULT_NUM_JOINTS = 64
        private val meshId = Attribute("attrib_meshid", GpuType.Float4)
    }

    private inner class SelectedMeshes(val nodeId: NodeId) {
        val selectedInstances = mutableListOf<SelectedInstance>()
        val instanceList = selectionInstances.getOrPut(nodeId) {
            MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT, meshId))
        }

        fun updateInstances(): MeshInstanceList {
            instanceList.clear()
            instanceList.addInstancesUpTo(selectedInstances.size) { buf ->
                var addCount = 0
                for (i in selectedInstances.indices) {
                    val inst = selectedInstances[i]
                    if (inst.component.sceneNode?.id == nodeId) {
                        inst.component.gameEntity.localToViewF.putTo(buf)
                        buf.put(inst.type.mask.toFloat())
                        buf.put((inst.id and 0x7fffff).toFloat())
                        buf.put(0f)
                        buf.put(0f)
                        addCount++
                    } else {
                        refreshSelection()
                    }
                }
                addCount
            }
            return instanceList
        }
    }

    private data class SelectedInstance(val component: MeshComponent, val type: MeshSelectionType, val id: Int)

    private enum class MeshSelectionType(val mask: Int) {
        PRIMARY(1),
        CHILD(2)
    }

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

                        val minMask = int1Var(Int.MAX_VALUE.const)
                        val maxMask = int1Var(Int.MIN_VALUE.const)
                        val minMaskCount = float1Var(0f.const)
                        val maxMaskCount = float1Var(0f.const)

                        samplePattern.forEach {
                            val tex = float2Var(sampleTexture(mask, uv.output + it.const * texelSz).rg)
                            val maskVal = int1Var(round(tex.x * 65280f.const).toInt1() + round(tex.y * 255f.const).toInt1())
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
                            val type = int1Var(maxMask shr 12.const)
                            `if` (type eq MeshSelectionType.PRIMARY.mask.const) {
                                color set colorPrim
                            }.elseIf(type eq MeshSelectionType.CHILD.mask.const) {
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