package de.fabmax.kool.platform.gl

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.util.Float32BufferImpl
import de.fabmax.kool.util.Profiling
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL31.glClearBufferfv
import org.lwjgl.opengl.GL31.glDrawElementsInstanced

class QueueRendererGl(backend: GlRenderBackend, val ctx: Lwjgl3Context) {

    private val glAttribs = GlAttribs()
    private val shaderMgr = ShaderManager(backend, ctx)

    private val colorBufferClearVal = Float32BufferImpl(4)

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
        if (ctx.isProfileRenderPasses) {
            Profiling.enter(view.renderPass.profileTag("render"))
        }

        view.apply {
            glViewport(viewport.x, viewport.y, viewport.width, viewport.height)

            val rp = renderPass
            if (rp is OffscreenRenderPass) {
                for (i in rp.colorAttachments.indices) {
                    clearColors[i]?.let { color ->
                        colorBufferClearVal.clear()
                        color.putTo(colorBufferClearVal)
                        colorBufferClearVal.useRaw {
                            glClearBufferfv(GL_COLOR, i, it)
                        }
                    }
                }
                if (clearDepth) {
                    glClear(GL_DEPTH_BUFFER_BIT)
                }

            } else {
                clearColor?.let { glClearColor(it.r, it.g, it.b, it.a) }
                val clearMask = clearMask()
                if (clearMask != 0) {
                    glClear(clearMask)
                }
            }
        }

        var numPrimitives = 0
        for (cmd in view.drawQueue.commands) {
            cmd.pipeline?.let { pipeline ->
                val t = System.nanoTime()
                glAttribs.setupPipelineAttribs(pipeline)

                if (cmd.geometry.numIndices > 0) {
                    val shaderInst = shaderMgr.setupShader(cmd)
                    if (shaderInst != null && shaderInst.indexType != 0) {
                        val insts = cmd.mesh.instances
                        if (insts == null) {
                            glDrawElements(shaderInst.primitiveType, shaderInst.numIndices, shaderInst.indexType, 0)
                            numPrimitives += cmd.geometry.numPrimitives
                        } else if (insts.numInstances > 0) {
                            glDrawElementsInstanced(shaderInst.primitiveType, shaderInst.numIndices, shaderInst.indexType, 0, insts.numInstances)
                            numPrimitives += cmd.geometry.numPrimitives * insts.numInstances
                        }
                        ctx.engineStats.addDrawCommandCount(1)
                    }
                }
                cmd.mesh.drawTime = (System.nanoTime() - t) / 1e6
            }
        }
        ctx.engineStats.addPrimitiveCount(numPrimitives)

        if (ctx.isProfileRenderPasses) {
            Profiling.exit(view.renderPass.profileTag("render"))
        }
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
                glLineWidth(pipeline.lineWidth)
            }
        }

        private fun setCullMethod(cullMethod: CullMethod) {
            if (this.actCullMethod != cullMethod) {
                this.actCullMethod = cullMethod
                when (cullMethod) {
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

        private fun setWriteDepth(enabled: Boolean) {
            if (actIsWriteDepth != enabled) {
                actIsWriteDepth = enabled
                glDepthMask(enabled)
            }
        }

        private fun setDepthTest(depthCompareOp: DepthCompareOp) {
            if (actDepthTest != depthCompareOp) {
                actDepthTest = depthCompareOp
                if (depthCompareOp == DepthCompareOp.DISABLED) {
                    glDisable(GL_DEPTH_TEST)
                } else {
                    glEnable(GL_DEPTH_TEST)
                    glDepthFunc(depthCompareOp.glOp)
                }
            }
        }

        private fun setBlendMode(blendMode: BlendMode) {
            when (blendMode) {
                BlendMode.DISABLED -> glDisable(GL_BLEND)
                BlendMode.BLEND_ADDITIVE -> {
                    glBlendFunc(GL_ONE, GL_ONE)
                    glEnable(GL_BLEND)
                }
                BlendMode.BLEND_MULTIPLY_ALPHA -> {
                    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
                    glEnable(GL_BLEND)
                }
                BlendMode.BLEND_PREMULTIPLIED_ALPHA -> {
                    glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA)
                    glEnable(GL_BLEND)
                }
            }
        }
    }

    private fun RenderPass.View.clearMask(): Int {
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