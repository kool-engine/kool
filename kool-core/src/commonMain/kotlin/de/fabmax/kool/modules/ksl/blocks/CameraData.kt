package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.modules.ksl.KslShaderListener
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.Time
import de.fabmax.kool.util.positioned

fun KslProgram.cameraData(): CameraData {
    return (dataBlocks.find { it is CameraData } as? CameraData) ?: CameraData(this)
}

fun KslScopeBuilder.depthToViewSpacePos(linearDepth: KslExprFloat1, clipSpaceXy: KslExprFloat2, camData: CameraData): KslExprFloat3 {
    return depthToViewSpacePos(linearDepth, clipSpaceXy, camData.viewParams)
}

fun KslScopeBuilder.depthToViewSpacePos(linearDepth: KslExprFloat1, clipSpaceXy: KslExprFloat2, viewParams: KslExprFloat4): KslExprFloat3 {
    return float3Value(clipSpaceXy * linearDepth * viewParams.xy + clipSpaceXy * viewParams.zw, -linearDepth)
}

class CameraData(program: KslProgram) : KslDataBlock, KslShaderListener {

    override val name = NAME

    val position: KslUniformVector<KslFloat3, KslFloat1>
    val direction: KslUniformVector<KslFloat3, KslFloat1>
    val clip: KslUniformVector<KslFloat2, KslFloat1>
    val viewParams: KslUniformVector<KslFloat4, KslFloat1>

    val viewMat: KslUniformMatrix<KslMat4, KslFloat4>
    val projMat: KslUniformMatrix<KslMat4, KslFloat4>
    val viewProjMat: KslUniformMatrix<KslMat4, KslFloat4>
    val viewport: KslUniformVector<KslFloat4, KslFloat1>

    val clipNear: KslExprFloat1
        get() = clip.x
    val clipFar: KslExprFloat1
        get() = clip.y
    val viewWidth: KslExprFloat1
        get() = viewport.z
    val viewHeight: KslExprFloat1
        get() = viewport.w

    val frameIndex: KslExprInt1

    private val camUbo = KslUniformBuffer("CameraUniforms", program, BindGroupScope.VIEW).apply {
        viewMat = uniformMat4(UNIFORM_NAME_VIEW_MAT)
        projMat = uniformMat4(UNIFORM_NAME_PROJ_MAT)
        viewProjMat = uniformMat4(UNIFORM_NAME_VIEW_PROJ_MAT)
        viewport = uniformFloat4(UNIFORM_NAME_VIEWPORT)

        position = uniformFloat3(UNIFORM_NAME_CAM_POSITION)
        direction = uniformFloat3(UNIFORM_NAME_CAM_DIRECTION)
        clip = uniformFloat2(UNIFORM_NAME_CAM_CLIP)
        viewParams = uniformFloat4(UNIFORM_NAME_VIEW_PARAMS)

        frameIndex = uniformInt1(UNIFORM_NAME_FRAME_INDEX)
    }

    private var uboLayout: UniformBufferLayout? = null
    private var bufferPosPosition: BufferPosition? = null
    private var bufferPosDirection: BufferPosition? = null
    private var bufferPosClip: BufferPosition? = null
    private var bufferPosDepthToViewSpace: BufferPosition? = null
    private var bufferPosViewMat: BufferPosition? = null
    private var bufferPosProjMat: BufferPosition? = null
    private var bufferPosViewProjMat: BufferPosition? = null
    private var bufferPosViewport: BufferPosition? = null
    private var bufferPosFrameIndex: BufferPosition? = null

    init {
        program.shaderListeners += this
        program.dataBlocks += this
        program.uniformBuffers += camUbo
    }

    override fun onShaderCreated(shader: ShaderBase<*>) {
        val binding = shader.createdPipeline!!.findBindingLayout<UniformBufferLayout> { it.name == "CameraUniforms" }
        uboLayout = binding?.second
        uboLayout?.let {
            bufferPosPosition = it.layout.uniformPositions[UNIFORM_NAME_CAM_POSITION]
            bufferPosDirection = it.layout.uniformPositions[UNIFORM_NAME_CAM_DIRECTION]
            bufferPosClip = it.layout.uniformPositions[UNIFORM_NAME_CAM_CLIP]
            bufferPosDepthToViewSpace = it.layout.uniformPositions[UNIFORM_NAME_VIEW_PARAMS]
            bufferPosViewMat = it.layout.uniformPositions[UNIFORM_NAME_VIEW_MAT]
            bufferPosProjMat = it.layout.uniformPositions[UNIFORM_NAME_PROJ_MAT]
            bufferPosViewProjMat = it.layout.uniformPositions[UNIFORM_NAME_VIEW_PROJ_MAT]
            bufferPosViewport = it.layout.uniformPositions[UNIFORM_NAME_VIEWPORT]
            bufferPosFrameIndex = it.layout.uniformPositions[UNIFORM_NAME_FRAME_INDEX]
        }
    }

    override fun onUpdate(cmd: DrawCommand) {
        val bindingLayout = uboLayout ?: return
        val viewData = cmd.queue.view.viewPipelineData.getPipelineDataUpdating(cmd.pipeline, bindingLayout.bindingIndex) ?: return

        val q = cmd.queue
        val vp = q.view.viewport
        val cam = q.view.camera
        val ubo = viewData.uniformBufferBindingData(bindingLayout.bindingIndex)
        
        ubo.markDirty()
        ubo.buffer.positioned(bufferPosPosition!!.byteIndex) { cam.globalPos.putTo(it) }
        ubo.buffer.positioned(bufferPosDirection!!.byteIndex) { cam.globalLookDir.putTo(it) }
        ubo.buffer.positioned(bufferPosClip!!.byteIndex) { it.putFloat32(cam.clipNear); it.putFloat32(cam.clipFar) }
        ubo.buffer.positioned(bufferPosDepthToViewSpace!!.byteIndex) { cam.viewParams.putTo(it) }
        ubo.buffer.positioned(bufferPosFrameIndex!!.byteIndex) { it.putInt32(Time.frameCount) }
        ubo.buffer.positioned(bufferPosViewMat!!.byteIndex) { q.viewMatF.putTo(it) }
        ubo.buffer.positioned(bufferPosProjMat!!.byteIndex) { q.projMat.putTo(it) }
        ubo.buffer.positioned(bufferPosViewProjMat!!.byteIndex) { q.viewProjMatF.putTo(it) }
        ubo.buffer.positioned(bufferPosViewport!!.byteIndex) {
            it.putFloat32(vp.x.toFloat())
            it.putFloat32(vp.y.toFloat())
            it.putFloat32(vp.width.toFloat())
            it.putFloat32(vp.height.toFloat())
        }
    }

    companion object {
        const val NAME = "CameraData"

        const val UNIFORM_NAME_CAM_POSITION = "uCamPos"
        const val UNIFORM_NAME_CAM_DIRECTION = "uCamDir"
        const val UNIFORM_NAME_CAM_CLIP = "uCamClip"
        const val UNIFORM_NAME_VIEW_PARAMS = "uViewParams"
        const val UNIFORM_NAME_FRAME_INDEX = "uFrameIdx"

        const val UNIFORM_NAME_VIEW_MAT = "uViewMat"
        const val UNIFORM_NAME_PROJ_MAT = "uProjMat"
        const val UNIFORM_NAME_VIEW_PROJ_MAT = "uViewProjMat"

        const val UNIFORM_NAME_VIEWPORT = "uViewport"
    }
}