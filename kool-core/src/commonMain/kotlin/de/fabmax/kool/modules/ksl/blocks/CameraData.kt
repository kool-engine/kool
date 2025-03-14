package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.modules.ksl.KslShaderListener
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.BindGroupScope
import de.fabmax.kool.pipeline.DrawCommand
import de.fabmax.kool.pipeline.ShaderBase
import de.fabmax.kool.pipeline.UniformBufferLayout
import de.fabmax.kool.util.*

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

    private var uboLayout: UniformBufferLayout<*>? = null
    private var positionIndex = -1
    private var directionIndex = -1
    private var clipIndex = -1
    private var viewParamsIndex = -1
    private var viewMatIndex = -1
    private var projMatIndex = -1
    private var viewProjMatIndex = -1
    private var viewportIndex = -1
    private var frameIndexIndex = -1
    private val viewportVec = MutableVec4f()

    init {
        program.shaderListeners += this
        program.dataBlocks += this
        program.uniformBuffers += camUbo
    }

    override fun onShaderCreated(shader: ShaderBase<*>) {
        val binding = shader.createdPipeline!!.findBindingLayout<UniformBufferLayout<*>> { it.name == "CameraUniforms" }
        uboLayout = binding?.second
        uboLayout?.let {
            positionIndex = it.indexOfMember(UNIFORM_NAME_CAM_POSITION)
            directionIndex = it.indexOfMember(UNIFORM_NAME_CAM_DIRECTION)
            clipIndex = it.indexOfMember(UNIFORM_NAME_CAM_CLIP)
            viewParamsIndex = it.indexOfMember(UNIFORM_NAME_VIEW_PARAMS)
            viewMatIndex = it.indexOfMember(UNIFORM_NAME_VIEW_MAT)
            projMatIndex = it.indexOfMember(UNIFORM_NAME_PROJ_MAT)
            viewProjMatIndex = it.indexOfMember(UNIFORM_NAME_VIEW_PROJ_MAT)
            viewportIndex = it.indexOfMember(UNIFORM_NAME_VIEWPORT)
            frameIndexIndex = it.indexOfMember(UNIFORM_NAME_FRAME_INDEX)
        }
    }

    override fun onUpdate(cmd: DrawCommand) {
        val bindingLayout = uboLayout ?: return
        val viewData = cmd.queue.view.viewPipelineData.getPipelineDataUpdating(cmd.pipeline, bindingLayout.bindingIndex) ?: return

        val q = cmd.queue
        val vp = q.view.viewport
        val cam = q.view.camera
        val ubo = viewData.uniformBufferBindingData(bindingLayout.bindingIndex)
        val struct = ubo.buffer.struct

        viewportVec.set(vp.x.toFloat(), vp.y.toFloat(), vp.width.toFloat(), vp.height.toFloat())

        ubo.markDirty()
        struct.setFloat3(positionIndex, cam.globalPos)
        struct.setFloat3(directionIndex, cam.globalLookDir)
        struct.setFloat2(clipIndex, cam.clip)
        struct.setFloat4(viewParamsIndex, cam.viewParams)
        struct.setMat4(viewMatIndex, q.viewMatF)
        struct.setMat4(projMatIndex, q.projMat)
        struct.setMat4(viewProjMatIndex, q.viewProjMatF)
        struct.setFloat4(viewportIndex, viewportVec)
        struct.setInt1(frameIndexIndex, Time.frameCount)
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