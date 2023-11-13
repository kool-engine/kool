package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.stats.BackendStats
import de.fabmax.kool.util.Float32Buffer
import de.fabmax.kool.util.Time

class QueueRenderer(val backend: RenderBackendGl) {

    private val glAttribs = GlAttribs()
    private val shaderMgr = ShaderManager(backend)

    private val colorBufferClearVal = Float32Buffer(4)

    private val ctx: KoolContext = backend.ctx
    private val gl: GlApi = backend.gl

    fun disposePipelines(pipelines: List<Pipeline>) {
        pipelines.forEach {
            shaderMgr.deleteShader(it)
        }
    }

    fun renderViews(renderPass: RenderPass) {
        for (i in renderPass.views.indices) {
            renderView(renderPass.views[i])
        }
    }

    fun renderView(view: RenderPass.View) {
        view.apply {
            gl.viewport(viewport.x, viewport.y, viewport.width, viewport.height)

            val rp = renderPass
            if (rp is OffscreenRenderPass) {
                for (i in rp.colorAttachments.indices) {
                    clearColors[i]?.let { color ->
                        colorBufferClearVal.clear()
                        color.putTo(colorBufferClearVal)
                        gl.clearBufferfv(gl.COLOR, i, colorBufferClearVal)
                    }
                }
                if (clearDepth) {
                    gl.clear(gl.DEPTH_BUFFER_BIT)
                }

            } else {
                clearColor?.let { gl.clearColor(it.r, it.g, it.b, it.a) }
                val clearMask = clearMask()
                if (clearMask != 0) {
                    gl.clear(clearMask)
                }
            }
        }

        var numPrimitives = 0
        for (cmd in view.drawQueue.commands) {
            cmd.pipeline?.let { pipeline ->
                val t = Time.precisionTime
                glAttribs.setupPipelineAttribs(pipeline)

                if (cmd.geometry.numIndices > 0) {
                    val shaderInst = shaderMgr.setupShader(cmd)
                    if (shaderInst != null && shaderInst.indexType != 0) {
                        val insts = cmd.mesh.instances
                        if (insts == null) {
                            gl.drawElements(shaderInst.primitiveType, shaderInst.numIndices, shaderInst.indexType)
                            numPrimitives += cmd.geometry.numPrimitives
                        } else if (insts.numInstances > 0) {
                            gl.drawElementsInstanced(shaderInst.primitiveType, shaderInst.numIndices, shaderInst.indexType, insts.numInstances)
                            numPrimitives += cmd.geometry.numPrimitives * insts.numInstances
                        }
                        BackendStats.addDrawCommandCount(1)
                    }
                }
                cmd.mesh.drawTime = Time.precisionTime - t
            }
        }
        BackendStats.addPrimitiveCount(numPrimitives)
    }

    private inner class GlAttribs {
        var actIsWriteDepth = true
        var actDepthTest: DepthCompareOp? = null
        var actCullMethod: CullMethod? = null
        var lineWidth = 0f

        fun setupPipelineAttribs(pipeline: Pipeline) {
            setBlendMode(pipeline.blendMode)
            setDepthTest(pipeline.depthCompareOp)
            setWriteDepth(pipeline.isWriteDepth)
            setCullMethod(pipeline.cullMethod)
            if (lineWidth != pipeline.lineWidth) {
                lineWidth = pipeline.lineWidth
                gl.lineWidth(pipeline.lineWidth)
            }
        }

        private fun setCullMethod(cullMethod: CullMethod) {
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

        private fun setWriteDepth(enabled: Boolean) {
            if (actIsWriteDepth != enabled) {
                actIsWriteDepth = enabled
                gl.depthMask(enabled)
            }
        }

        private fun setDepthTest(depthCompareOp: DepthCompareOp) {
            if (actDepthTest != depthCompareOp) {
                actDepthTest = depthCompareOp
                if (depthCompareOp == DepthCompareOp.DISABLED) {
                    gl.disable(gl.DEPTH_TEST)
                } else {
                    gl.enable(gl.DEPTH_TEST)
                    gl.depthFunc(depthCompareOp.glOp)
                }
            }
        }

        private fun setBlendMode(blendMode: BlendMode) {
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

    private fun RenderPass.View.clearMask(): Int {
        var mask = 0
        if (clearDepth) {
            mask = gl.DEPTH_BUFFER_BIT
        }
        if (clearColor != null) {
            mask = mask or gl.COLOR_BUFFER_BIT
        }
        return mask
    }

    private val DepthCompareOp.glOp: Int
        get() = when(this) {
            DepthCompareOp.DISABLED -> 0
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