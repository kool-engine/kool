package de.fabmax.kool.platform.gl

import de.fabmax.kool.drawqueue.DrawQueue
import de.fabmax.kool.pipeline.CullMethod
import de.fabmax.kool.pipeline.DepthCompareOp
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.platform.Lwjgl3Context
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL31.glDrawElementsInstanced

class QueueRendererGl(val backend: GlRenderBackend, val ctx: Lwjgl3Context) {

    private val glAttribs = GlAttribs()
    private val shaderMgr = ShaderManager(backend, ctx)

    fun disposePipelines(pipelines: List<Pipeline>) {
        pipelines.forEach {
            shaderMgr.deleteShader(it)
        }
    }

    fun renderQueue(queue: DrawQueue) {
        queue.renderPass.apply {
            glViewport(viewport.x, viewport.y, viewport.width, viewport.height)
            clearColor?.let { glClearColor(it.r, it.g, it.b, it.a) }
            val clearMask = clearMask()
            if (clearMask != 0) {
                glClear(clearMask)
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
                                glDrawElements(it.primitiveType, it.numIndices, it.indexType, 0)
                                ctx.engineStats.addPrimitiveCount(cmd.mesh.geometry.numPrimitives)
                            } else {
                                glDrawElementsInstanced(it.primitiveType, it.numIndices, it.indexType, 0, insts.numInstances)
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
        var actDepthTest: DepthCompareOp? = null
        var actCullMethod: CullMethod? = null
        var lineWidth = 0f

        fun setupPipelineAttribs(pipeline: Pipeline) {
            setDepthTest(pipeline.depthCompareOp)
            setCullMethod(pipeline.cullMethod)
            if (lineWidth != pipeline.lineWidth) {
                lineWidth = pipeline.lineWidth
                glLineWidth(pipeline.lineWidth)
            }
        }

        private fun setCullMethod(cullMethod: CullMethod) {
            if (this.actCullMethod != cullMethod) {
                this.actCullMethod = cullMethod
                when (cullMethod) {
                    CullMethod.DEFAULT -> {
                        glEnable(GL_CULL_FACE)
                        glCullFace(GL_BACK)
                    }
                    CullMethod.CULL_BACK_FACES -> {
                        glEnable(GL_CULL_FACE)
                        glCullFace(GL_BACK)
                    }
                    CullMethod.CULL_FRONT_FACES -> {
                        glEnable(GL_CULL_FACE)
                        glCullFace(GL_FRONT)
                    }
                    CullMethod.NO_CULLING -> glDisable(GL_CULL_FACE)
                }
            }
        }

        private fun setDepthTest(depthCompareOp: DepthCompareOp) {
            if (actDepthTest != depthCompareOp) {
                actDepthTest = depthCompareOp
                when (depthCompareOp) {
                    DepthCompareOp.DISABLED -> glDisable(GL_DEPTH_TEST)
                    DepthCompareOp.ALWAYS -> {
                        glEnable(GL_DEPTH_TEST)
                        glDepthFunc(GL_ALWAYS)
                    }
                    DepthCompareOp.LESS -> {
                        glEnable(GL_DEPTH_TEST)
                        glDepthFunc(GL_LESS)
                    }
                    DepthCompareOp.LESS_EQUAL -> {
                        glEnable(GL_DEPTH_TEST)
                        glDepthFunc(GL_LEQUAL)
                    }
                    DepthCompareOp.GREATER -> {
                        glEnable(GL_DEPTH_TEST)
                        glDepthFunc(GL_GREATER)
                    }
                    DepthCompareOp.GREATER_EQUAL -> {
                        glEnable(GL_DEPTH_TEST)
                        glDepthFunc(GL_GEQUAL)
                    }
                }
            }
        }
    }

    private fun RenderPass.clearMask(): Int {
        var mask = 0
        if (clearDepth) {
            mask = GL_DEPTH_BUFFER_BIT
        }
        if (clearColor != null) {
            mask = mask or GL_COLOR_BUFFER_BIT
        }
        return mask
    }
}