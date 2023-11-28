package de.fabmax.kool.modules.ksl

import de.fabmax.kool.modules.ksl.lang.KslProgram
import de.fabmax.kool.pipeline.ComputePipeline
import de.fabmax.kool.pipeline.ComputeShader
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.util.logW

class KslComputeShader(name: String) : ComputeShader() {

    val program = KslProgram(name)

    override fun onPipelineSetup(builder: ComputePipeline.Builder, updateEvent: RenderPass.UpdateEvent) {
        checkNotNull(program.computeStage) {
            "KslProgram computeStage is missing (a valid KslComputeShader needs a computeStage)"
        }
        if (program.vertexStage != null || program.fragmentStage != null) {
            logW { "KslProgram has a vertex or fragment stage defined, although it is used as a compute shader. Vertex and fragment stages are ignored." }
        }

        // prepare shader model for generating source code, also updates program dependencies (e.g. which
        // uniform is used by which shader stage)
        program.prepareGenerate()

        builder.name = program.name
        builder.bindGroupLayouts += program.setupBindGroupLayout(this)
        builder.shaderCodeGenerator = { updateEvent.ctx.backend.generateKslComputeShader(this, it) }
    }

}
