package de.fabmax.kool.platform.webgl

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.drawqueue.DrawQueue
import de.fabmax.kool.platform.JsContext
import de.fabmax.kool.platform.WebGL2RenderingContext
import de.fabmax.kool.platform.WebGL2RenderingContext.Companion.COLOR
import de.fabmax.kool.platform.glOp
import de.fabmax.kool.util.Profiling
import de.fabmax.kool.util.Time
import org.khronos.webgl.Float32Array
import org.khronos.webgl.WebGLRenderingContext.Companion.BACK
import org.khronos.webgl.WebGLRenderingContext.Companion.BLEND
import org.khronos.webgl.WebGLRenderingContext.Companion.COLOR_BUFFER_BIT
import org.khronos.webgl.WebGLRenderingContext.Companion.CULL_FACE
import org.khronos.webgl.WebGLRenderingContext.Companion.DEPTH_BUFFER_BIT
import org.khronos.webgl.WebGLRenderingContext.Companion.DEPTH_TEST
import org.khronos.webgl.WebGLRenderingContext.Companion.FRONT
import org.khronos.webgl.WebGLRenderingContext.Companion.ONE
import org.khronos.webgl.WebGLRenderingContext.Companion.ONE_MINUS_SRC_ALPHA
import org.khronos.webgl.WebGLRenderingContext.Companion.SRC_ALPHA
import org.khronos.webgl.set

class QueueRendererWebGl(val ctx: JsContext) {

    private val gl: WebGL2RenderingContext
        get() = ctx.gl

    private val glAttribs = GlAttribs()
    private val shaderMgr = ShaderManager(ctx)

    private val colorBuffer = Float32Array(4)

    fun disposePipelines(pipelines: List<Pipeline>) {
        pipelines.forEach {
            shaderMgr.deleteShader(it)
        }
    }

    fun renderQueue(queue: DrawQueue) {
        if (ctx.isProfileRenderPasses) {
            Profiling.enter(queue.renderPass.profileTag("render"))
        }

        queue.renderPass.apply {
            ctx.gl.viewport(viewport.x, viewport.y, viewport.width, viewport.height)

            if (this is OffscreenRenderPass2d) {
                for (i in 0 until config.nColorAttachments) {
                    (clearColors[i] ?: clearColor)?.let {
                        colorBuffer[0] = it.r
                        colorBuffer[1] = it.g
                        colorBuffer[2] = it.b
                        colorBuffer[3] = it.a
                        ctx.gl.clearBufferfv(COLOR, i, colorBuffer)
                    }
                }
                if (clearDepth) {
                    ctx.gl.clear(DEPTH_BUFFER_BIT)
                }

            } else {
                clearColor?.let { ctx.gl.clearColor(it.r, it.g, it.b, it.a) }
                val clearMask = clearMask()
                if (clearMask != 0) {
                    ctx.gl.clear(clearMask)
                }
            }
            onBeforeRenderQueue(ctx)
        }

        var numPrimitives = 0
        for (cmd in queue.commands) {
            cmd.pipeline?.let { pipeline ->
                val t = Time.precisionTime
                glAttribs.setupPipelineAttribs(pipeline)

                if (cmd.geometry.numIndices > 0) {
                    shaderMgr.setupShader(cmd)?.let {
                        if (it.indexType != 0) {
                            val insts = cmd.mesh.instances
                            if (insts == null) {
                                gl.drawElements(it.primitiveType, it.numIndices, it.indexType, 0)
                                numPrimitives += cmd.geometry.numPrimitives
                            } else if (insts.numInstances > 0) {
                                gl.drawElementsInstanced(it.primitiveType, it.numIndices, it.indexType, 0, insts.numInstances)
                                numPrimitives += cmd.geometry.numPrimitives * insts.numInstances
                            }
                            ctx.engineStats.addDrawCommandCount(1)
                        }
                    }
                }
                cmd.mesh.drawTime = (Time.precisionTime - t) * 1e3
            }
        }
        ctx.engineStats.addPrimitiveCount(numPrimitives)
        queue.renderPass.onAfterRenderQueue(ctx)

        if (ctx.isProfileRenderPasses) {
            Profiling.exit(queue.renderPass.profileTag("render"))
        }
    }

    private inner class GlAttribs {
        var actIsWriteDepth = true
        var depthTest: DepthCompareOp? = null
        var cullMethod: CullMethod? = null
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
            if (this.cullMethod != cullMethod) {
                this.cullMethod = cullMethod
                when (cullMethod) {
                    CullMethod.CULL_BACK_FACES -> {
                        gl.enable(CULL_FACE)
                        gl.cullFace(BACK)
                    }
                    CullMethod.CULL_FRONT_FACES -> {
                        gl.enable(CULL_FACE)
                        gl.cullFace(FRONT)
                    }
                    CullMethod.NO_CULLING -> gl.disable(CULL_FACE)
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
            if (depthTest != depthCompareOp) {
                depthTest = depthCompareOp
                if (depthCompareOp == DepthCompareOp.DISABLED) {
                    gl.disable(DEPTH_TEST)
                } else {
                    gl.enable(DEPTH_TEST)
                    gl.depthFunc(depthCompareOp.glOp)
                }
            }
        }

        private fun setBlendMode(blendMode: BlendMode) {
            when (blendMode) {
                BlendMode.DISABLED -> gl.disable(BLEND)
                BlendMode.BLEND_ADDITIVE -> {
                    gl.blendFunc(ONE, ONE)
                    gl.enable(BLEND)
                }
                BlendMode.BLEND_MULTIPLY_ALPHA -> {
                    gl.blendFunc(SRC_ALPHA, ONE_MINUS_SRC_ALPHA)
                    gl.enable(BLEND)
                }
                BlendMode.BLEND_PREMULTIPLIED_ALPHA -> {
                    gl.blendFunc(ONE, ONE_MINUS_SRC_ALPHA)
                    gl.enable(BLEND)
                }
            }
        }
    }

    private fun RenderPass.clearMask(): Int {
        var mask = 0
        if (clearDepth) {
            mask = DEPTH_BUFFER_BIT
        }
        if (clearColor != null) {
            mask = mask or COLOR_BUFFER_BIT
        }
        return mask
    }
}