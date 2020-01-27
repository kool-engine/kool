package de.fabmax.kool.platform.webgl

import de.fabmax.kool.drawqueue.DrawQueue
import de.fabmax.kool.pipeline.DepthCompareOp
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.platform.JsContext
import de.fabmax.kool.scene.CullMethod
import org.khronos.webgl.WebGLRenderingContext
import org.khronos.webgl.WebGLRenderingContext.Companion.ALWAYS
import org.khronos.webgl.WebGLRenderingContext.Companion.BACK
import org.khronos.webgl.WebGLRenderingContext.Companion.CULL_FACE
import org.khronos.webgl.WebGLRenderingContext.Companion.DEPTH_TEST
import org.khronos.webgl.WebGLRenderingContext.Companion.FRONT
import org.khronos.webgl.WebGLRenderingContext.Companion.GEQUAL
import org.khronos.webgl.WebGLRenderingContext.Companion.GREATER
import org.khronos.webgl.WebGLRenderingContext.Companion.LEQUAL
import org.khronos.webgl.WebGLRenderingContext.Companion.LESS

class QueueRendererWebGl(val ctx: JsContext) {

    private val gl: WebGLRenderingContext
        get() = ctx.gl

    private val glAttribs = GlAttribs()
    private val shaderMgr = ShaderManager(ctx)

    fun renderQueue(queue: DrawQueue) {
        for (cmd in queue.commands) {
            cmd.pipeline?.let { pipeline ->
                glAttribs.setupPipelineAttribs(pipeline)

                val inst = shaderMgr.setupShader(cmd)
                gl.drawElements(inst.primitiveType, inst.numIndices, inst.indexType, 0)
            }
        }
    }

    private inner class GlAttribs {
        var depthTest: DepthCompareOp? = null
        var cullMethod: CullMethod? = null
        var lineWidth = 0f

        fun setupPipelineAttribs(pipeline: Pipeline) {
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
    }
}