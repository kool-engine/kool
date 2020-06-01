package de.fabmax.kool.util.deferred

import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.pipeline.Uniform4f
import de.fabmax.kool.pipeline.UniformMat4f
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.scene.Camera


class DiscardClearNode(stage: ShaderGraph) : ShaderNode("discardClear", stage) {
    var inViewPos = ShaderNodeIoVar(ModelVar4fConst(Vec4f.ZERO))

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inViewPos)
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("if (${inViewPos.name}.z > 1.0) { discard; }")
    }
}

class DeferredCameraNode(stage: ShaderGraph) : ShaderNode("deferredCam", stage) {
    private val uInvViewMat = UniformMat4f("uInvViewMat")
    private val uCamPos = Uniform4f("uCamPos")
    private val uViewport = Uniform4f("uViewport")

    val outInvViewMat = ShaderNodeIoVar(ModelVarMat4f(uInvViewMat.name), this)
    val outCamPos = ShaderNodeIoVar(ModelVar4f(uCamPos.name), this)
    val outViewport = ShaderNodeIoVar(ModelVar4f(uViewport.name), this)

    var sceneCam: Camera? = null

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        shaderGraph.descriptorSet.apply {
            uniformBuffer(name, shaderGraph.stage) {
                +{ uInvViewMat }
                +{ uCamPos }
                +{ uViewport }
                onUpdate = { _, cmd ->
                    val cam = sceneCam
                    if (cam != null) {
                        uInvViewMat.value.set(cam.invView)
                        uCamPos.value.set(cam.globalPos, 1f)
                    }
                    cmd.renderPass.viewport.let {
                        uViewport.value.set(it.x.toFloat(), it.y.toFloat(), it.width.toFloat(), it.height.toFloat())
                    }
                }
            }
        }
    }

}
