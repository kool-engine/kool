package de.fabmax.kool.platform.webgl

import de.fabmax.kool.drawqueue.DrawQueue
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.JsContext
import de.fabmax.kool.platform.WebGL2RenderingContext
import de.fabmax.kool.platform.WebGL2RenderingContext.Companion.COLOR
import org.khronos.webgl.Float32Array
import org.khronos.webgl.WebGLRenderingContext.Companion.ALWAYS
import org.khronos.webgl.WebGLRenderingContext.Companion.BACK
import org.khronos.webgl.WebGLRenderingContext.Companion.BLEND
import org.khronos.webgl.WebGLRenderingContext.Companion.COLOR_BUFFER_BIT
import org.khronos.webgl.WebGLRenderingContext.Companion.CULL_FACE
import org.khronos.webgl.WebGLRenderingContext.Companion.DEPTH_BUFFER_BIT
import org.khronos.webgl.WebGLRenderingContext.Companion.DEPTH_TEST
import org.khronos.webgl.WebGLRenderingContext.Companion.FRONT
import org.khronos.webgl.WebGLRenderingContext.Companion.GEQUAL
import org.khronos.webgl.WebGLRenderingContext.Companion.GREATER
import org.khronos.webgl.WebGLRenderingContext.Companion.LEQUAL
import org.khronos.webgl.WebGLRenderingContext.Companion.LESS
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
        queue.renderPass.apply {
            ctx.gl.viewport(viewport.x, viewport.y, viewport.width, viewport.height)

            if (this is OffscreenRenderPass2dMrt) {
                for (i in 0 until nAttachments) {
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
        }

        for (cmd in queue.commands) {
            cmd.pipeline?.let { pipeline ->
                glAttribs.setupPipelineAttribs(pipeline)

                if (cmd.mesh.geometry.numIndices > 0) {
                    shaderMgr.setupShader(cmd)?.let {
                        if (it.primitiveType != 0 && it.indexType != 0) {
                            val insts = cmd.mesh.instances
                            if (insts == null) {
                                gl.drawElements(it.primitiveType, it.numIndices, it.indexType, 0)
                                ctx.engineStats.addPrimitiveCount(cmd.mesh.geometry.numPrimitives)
                            } else {
                                gl.drawElementsInstanced(it.primitiveType, it.numIndices, it.indexType, 0, insts.numInstances)
                                ctx.engineStats.addPrimitiveCount(cmd.mesh.geometry.numPrimitives * insts.numInstances)
                            }
                            ctx.engineStats.addDrawCommandCount(1)
                        }
                    }
                }
            }
        }
    }

    private inner class GlAttribs {
        var depthTest: DepthCompareOp? = null
        var cullMethod: CullMethod? = null
        var lineWidth = 0f

        fun setupPipelineAttribs(pipeline: Pipeline) {
            setBlendMode(pipeline.blendMode)
            setDepthTest(pipeline.depthCompareOp)
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
                    CullMethod.DEFAULT -> {
                        gl.enable(CULL_FACE)
                        gl.cullFace(BACK)
                    }
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

        private fun setDepthTest(depthCompareOp: DepthCompareOp) {
            if (depthTest != depthCompareOp) {
                depthTest = depthCompareOp
                when (depthCompareOp) {
                    DepthCompareOp.DISABLED -> gl.disable(DEPTH_TEST)
                    DepthCompareOp.ALWAYS -> {
                        gl.enable(DEPTH_TEST)
                        gl.depthFunc(ALWAYS)
                    }
                    DepthCompareOp.LESS -> {
                        gl.enable(DEPTH_TEST)
                        gl.depthFunc(LESS)
                    }
                    DepthCompareOp.LESS_EQUAL -> {
                        gl.enable(DEPTH_TEST)
                        gl.depthFunc(LEQUAL)
                    }
                    DepthCompareOp.GREATER -> {
                        gl.enable(DEPTH_TEST)
                        gl.depthFunc(GREATER)
                    }
                    DepthCompareOp.GREATER_EQUAL -> {
                        gl.enable(DEPTH_TEST)
                        gl.depthFunc(GEQUAL)
                    }
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
                else -> TODO("Unimplemented blend mode: $blendMode")
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