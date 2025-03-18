package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.modules.ksl.KslShaderListener
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.BindGroupScope
import de.fabmax.kool.pipeline.DrawCommand
import de.fabmax.kool.pipeline.ShaderBase
import de.fabmax.kool.pipeline.UniformBufferLayout
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

    private val camUniform = program.uniformStruct("uCameraData", BindGroupScope.VIEW) { CamDataStruct() }

    val viewProjMat: KslExprMat4 get() = camUniform.struct.viewProj.ksl
    val viewMat: KslExprMat4 get() = camUniform.struct.view.ksl
    val projMat: KslExprMat4 get() = camUniform.struct.proj.ksl

    val viewport: KslExprFloat4 get() = camUniform.struct.viewport.ksl
    val viewParams: KslExprFloat4 get() = camUniform.struct.viewParams.ksl
    val position: KslExprFloat3 get() = camUniform.struct.position.ksl
    val direction: KslExprFloat3 get() = camUniform.struct.direction.ksl
    val clip: KslExprFloat2 get() = camUniform.struct.clip.ksl

    val frameIndex: KslExprInt1 get() = camUniform.struct.frameIndex.ksl

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
        val (_, binding) = shader.createdPipeline!!.getBindGroupItem<UniformBufferLayout<CamDataStruct>> {
            it.isStructInstanceOf<CamDataStruct>()
        }
        structLayout = binding
    }

    override fun onUpdate(cmd: DrawCommand) {
        val layout = structLayout ?: return
        val viewData = cmd.queue.view.viewPipelineData.getPipelineDataUpdating(cmd.pipeline, layout.bindingIndex) ?: return
        val binding = viewData.uniformStructBindingData(layout)
        val q = cmd.queue
        val vp = q.view.viewport
        val cam = q.view.camera

        binding.set {
            viewProj.set(q.viewProjMatF)
            view.set(q.viewMatF)
            proj.set(q.projMat)
            viewport.set(viewportVec.set(vp.x.toFloat(), vp.y.toFloat(), vp.width.toFloat(), vp.height.toFloat()))
            viewParams.set(cam.viewParams)
            position.set(cam.globalPos)
            direction.set(cam.globalLookDir)
            clip.set(cam.clip)
            frameIndex.set(Time.frameCount)
        }
    }

    class CamDataStruct : Struct("CameraData", MemoryLayout.Std140) {
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