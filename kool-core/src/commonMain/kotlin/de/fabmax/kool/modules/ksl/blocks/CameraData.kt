package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.modules.ksl.KslShaderListener
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.MemoryLayout
import de.fabmax.kool.util.Struct
import de.fabmax.kool.util.Time

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
    override val name = "CameraData"

    private val camUniform = program.uniformStruct("uCameraData", CamDataStruct, BindGroupScope.VIEW)

    val viewProjMat: KslExprMat4 get() = camUniform[CamDataStruct.viewProj]
    val viewMat: KslExprMat4 get() = camUniform[CamDataStruct.view]
    val projMat: KslExprMat4 get() = camUniform[CamDataStruct.proj]

    val viewport: KslExprFloat4 get() = camUniform[CamDataStruct.viewport]
    val viewParams: KslExprFloat4 get() = camUniform[CamDataStruct.viewParams]
    val position: KslExprFloat3 get() = camUniform[CamDataStruct.position]
    val direction: KslExprFloat3 get() = camUniform[CamDataStruct.direction]
    val clip: KslExprFloat2 get() = camUniform[CamDataStruct.clip]

    val frameIndex: KslExprInt1 get() = camUniform[CamDataStruct.frameIndex]

    val clipNear: KslExprFloat1
        get() = clip.x
    val clipFar: KslExprFloat1
        get() = clip.y
    val viewWidth: KslExprFloat1
        get() = viewport.z
    val viewHeight: KslExprFloat1
        get() = viewport.w

    private var structLayout: UniformBufferLayout<CamDataStruct>? = null
    private val viewportVec = MutableVec4f()

    init {
        program.shaderListeners += this
        program.dataBlocks += this
    }

    override fun onShaderCreated(shader: ShaderBase<*>) {
        val (_, binding) = shader.createdPipeline!!.getUniformBufferLayout<CamDataStruct>()
        structLayout = binding
    }

    override fun onUpdateDrawData(cmd: DrawCommand) {
        val layout = structLayout ?: return
        cmd.queue.view.viewPipelineData.updatePipelineData(cmd.pipeline, layout.bindingIndex) { viewData ->
            val binding = viewData.uniformStructBindingData(layout)
            val q = cmd.queue
            val vp = q.view.viewport
            val cam = q.view.camera
            binding.set {
                set(it.viewProj, q.viewProjMatF)
                set(it.view, q.viewMatF)
                set(it.proj, q.projMat)
                set(it.viewport, viewportVec.set(vp.x.toFloat(), vp.y.toFloat(), vp.width.toFloat(), vp.height.toFloat()))
                set(it.viewParams, cam.viewParams)
                set(it.position, cam.globalPos)
                set(it.direction, cam.globalLookDir)
                set(it.clip, cam.clip)
                set(it.frameIndex, Time.frameCount)
            }
        }
    }

    object CamDataStruct : Struct("CameraData", MemoryLayout.Std140) {
        val viewProj = mat4("viewProjMat")
        val view = mat4("viewMat")
        val proj = mat4("projMat")
        val viewport = float4("viewport")
        val viewParams = float4("viewParams")
        val position = float3("position")
        val direction = float3("direction")
        val clip = float2("clip")
        val frameIndex = int1("frameIndex")
    }
}