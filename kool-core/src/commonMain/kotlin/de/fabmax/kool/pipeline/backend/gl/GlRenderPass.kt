package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.stats.BackendStats
import de.fabmax.kool.util.Float32Buffer
import de.fabmax.kool.util.Time

abstract class GlRenderPass(val backend: RenderBackendGl) {
    private val colorBufferClearVal = Float32Buffer(4)

    private val ctx: KoolContext = backend.ctx
    protected val gl: GlApi = backend.gl

    fun renderViews(renderPass: RenderPass) {
        val t = if (renderPass.isProfileTimes) Time.precisionTime else 0.0

        for (mipLevel in 0 until renderPass.numRenderMipLevels) {
            renderPass.setupMipLevel(mipLevel)

            when (renderPass.viewRenderMode) {
                RenderPass.ViewRenderMode.SINGLE_RENDER_PASS -> {
                    setupFramebuffer(0, mipLevel)
                    clear(renderPass)
                    for (viewIndex in renderPass.views.indices) {
                        renderPass.setupView(viewIndex)
                        renderView(renderPass.views[viewIndex], mipLevel)
                    }
                }

                RenderPass.ViewRenderMode.MULTI_RENDER_PASS -> {
                    for (viewIndex in renderPass.views.indices) {
                        setupFramebuffer(viewIndex, mipLevel)
                        clear(renderPass)
                        renderPass.setupView(viewIndex)
                        renderView(renderPass.views[viewIndex], mipLevel)
                    }
                }
            }
        }

        if (renderPass.isProfileTimes) {
            renderPass.tDraw = Time.precisionTime - t
        }
    }

    fun renderView(view: RenderPass.View, mipLevel: Int) {
        val viewport = view.viewport
        val x = viewport.x shr mipLevel
        val y = viewport.y shr mipLevel
        val w = viewport.width shr mipLevel
        val h = viewport.height shr mipLevel
        gl.viewport(x, y, w, h)

        view.drawQueue.forEach { cmd ->
            cmd.pipeline?.let { pipeline ->
                val drawInfo = backend.shaderMgr.bindDrawShader(cmd)
                val isValid = cmd.geometry.numIndices > 0  &&  drawInfo.isValid && drawInfo.numIndices > 0

                if (isValid) {
                    GlState.setupPipelineAttribs(pipeline, view.renderPass.isReverseDepth, gl)

                    val insts = cmd.mesh.instances
                    if (insts == null) {
                        gl.drawElements(drawInfo.primitiveType, drawInfo.numIndices, drawInfo.indexType)
                        BackendStats.addDrawCommands(1, cmd.geometry.numPrimitives)
                    } else if (insts.numInstances > 0) {
                        gl.drawElementsInstanced(drawInfo.primitiveType, drawInfo.numIndices, drawInfo.indexType, insts.numInstances)
                        BackendStats.addDrawCommands(1, cmd.geometry.numPrimitives * insts.numInstances)
                    }
                }
            }
        }
    }

    protected abstract fun setupFramebuffer(viewIndex: Int, mipLevel: Int)

    fun clear(renderPass: RenderPass) {
        for (i in renderPass.clearColors.indices) {
            renderPass.clearColors[i]?.let { color ->
                colorBufferClearVal.clear()
                color.putTo(colorBufferClearVal)
                gl.clearBufferfv(gl.COLOR, i, colorBufferClearVal)
            }
        }
        if (renderPass.clearDepth) {
            GlState.setWriteDepth(true, gl)
            gl.clearDepth(if (renderPass.isReverseDepth) 0f else 1f)
            gl.clear(gl.DEPTH_BUFFER_BIT)
        }
    }

    private object GlState {
        var actIsWriteDepth = true
        var actDepthTest: DepthCompareOp? = null
        var actCullMethod: CullMethod? = null
        var lineWidth = 0f

        fun setupPipelineAttribs(pipeline: DrawPipeline, isReversedDepth: Boolean, gl: GlApi) {
            setBlendMode(pipeline.blendMode, gl)
            setDepthTest(pipeline, isReversedDepth, gl)
            setWriteDepth(pipeline.isWriteDepth, gl)
            setCullMethod(pipeline.cullMethod, gl)
            if (lineWidth != pipeline.lineWidth) {
                lineWidth = pipeline.lineWidth
                gl.lineWidth(pipeline.lineWidth)
            }
        }

        private fun setCullMethod(cullMethod: CullMethod, gl: GlApi) {
            if (this.actCullMethod != cullMethod) {
                this.actCullMethod = cullMethod
                when (cullMethod) {
                    CullMethod.CULL_BACK_FACES -> {
                        gl.enable(gl.CULL_FACE)
                        gl.cullFace(gl.BACK)
                    }
                    CullMethod.CULL_FRONT_FACES -> {
                        gl.enable(gl.CULL_FACE)
                        gl.cullFace(gl.FRONT)
                    }
                    CullMethod.NO_CULLING -> gl.disable(gl.CULL_FACE)
                }
            }
        }

        fun setWriteDepth(enabled: Boolean, gl: GlApi) {
            if (actIsWriteDepth != enabled) {
                actIsWriteDepth = enabled
                gl.depthMask(enabled)
            }
        }

        private fun setDepthTest(pipeline: DrawPipeline, isReversedDepth: Boolean, gl: GlApi) {
            val depthCompareOp = if (isReversedDepth && pipeline.autoReverseDepthFunc) {
                when (pipeline.depthCompareOp) {
                    DepthCompareOp.LESS -> DepthCompareOp.GREATER
                    DepthCompareOp.LESS_EQUAL -> DepthCompareOp.GREATER_EQUAL
                    DepthCompareOp.GREATER -> DepthCompareOp.LESS
                    DepthCompareOp.GREATER_EQUAL -> DepthCompareOp.LESS_EQUAL
                    else -> pipeline.depthCompareOp
                }
            } else {
                pipeline.depthCompareOp
            }

            if (actDepthTest != depthCompareOp) {
                actDepthTest = depthCompareOp
                if (depthCompareOp == DepthCompareOp.ALWAYS && !pipeline.isWriteDepth) {
                    gl.disable(gl.DEPTH_TEST)
                } else {
                    gl.enable(gl.DEPTH_TEST)
                    gl.depthFunc(depthCompareOp.glOp(gl))
                }
            }
        }

        private fun setBlendMode(blendMode: BlendMode, gl: GlApi) {
            when (blendMode) {
                BlendMode.DISABLED -> gl.disable(gl.BLEND)
                BlendMode.BLEND_ADDITIVE -> {
                    gl.blendFunc(gl.ONE, gl.ONE)
                    gl.enable(gl.BLEND)
                }
                BlendMode.BLEND_MULTIPLY_ALPHA -> {
                    gl.blendFunc(gl.SRC_ALPHA, gl.ONE_MINUS_SRC_ALPHA)
                    gl.enable(gl.BLEND)
                }
                BlendMode.BLEND_PREMULTIPLIED_ALPHA -> {
                    gl.blendFunc(gl.ONE, gl.ONE_MINUS_SRC_ALPHA)
                    gl.enable(gl.BLEND)
                }
            }
        }
    }

    private val DepthCompareOp.glOp: Int
        get() = when(this) {
            DepthCompareOp.ALWAYS -> gl.ALWAYS
            DepthCompareOp.NEVER -> gl.NEVER
            DepthCompareOp.LESS -> gl.LESS
            DepthCompareOp.LESS_EQUAL -> gl.LEQUAL
            DepthCompareOp.GREATER -> gl.GREATER
            DepthCompareOp.GREATER_EQUAL -> gl.GEQUAL
            DepthCompareOp.EQUAL -> gl.EQUAL
            DepthCompareOp.NOT_EQUAL -> gl.NOTEQUAL
        }
}