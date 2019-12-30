package de.fabmax.kool.pipeline.shadermodel

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolException
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.Color

open class ShaderNodeIoVar(val node: ShaderNode?, val variable: ModelVar) {
    val name: String get() = variable.name
}

abstract class ShaderNode(val name: String, val allowedStages: Int = ShaderStage.ALL.mask) {
    val dependencies = mutableSetOf<ShaderNode>()

    fun dependsOn(nd: ShaderNode?) {
        if (nd != null) {
            dependencies += nd
        }
    }

    fun dependsOn(ndVar: ShaderNodeIoVar) {
        dependsOn(ndVar.node)
    }

    fun dependsOn(vararg ndVars: ShaderNodeIoVar) {
        ndVars.forEach { dependsOn(it) }
    }

    open fun setup(shaderGraph: ShaderGraph) {
        check(shaderGraph.stage.mask and allowedStages != 0) {
            "Unallowed shader stage (${shaderGraph.stage} for node $name"
        }
    }

    open fun generateCode(generator: CodeGenerator, pipeline: Pipeline, ctx: KoolContext) { }

    companion object {
        private var nextId = 0
    }
}

abstract class UniformBufferNode(name: String) : ShaderNode(name) {
    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        createUniformBuffer(shaderGraph)
    }

    protected abstract fun createUniformBuffer(shaderGraph: ShaderGraph)
}

class UniformBufferPremultipliedMvp : UniformBufferNode("UboPremultipliedMvp") {
    val uMvp = UniformMat4f("modelViewProj")

    val outMvpMat = ShaderNodeIoVar(this, ModelVarMat4f(uMvp.name))

    override fun createUniformBuffer(shaderGraph: ShaderGraph) {
        shaderGraph.descriptorSet.apply {
            uniformBuffer(name, shaderGraph.stage) {
                +{ uMvp }
                onUpdate = { _, cmd -> uMvp.value.set(cmd.mvpMat) }
            }
        }
    }
}

class UniformBufferMvp : UniformBufferNode("UboMvp") {
    val uModelMat = UniformMat4f("modelMat")
    val uViewMat = UniformMat4f("viewMat")
    val uProjMat = UniformMat4f("projMat")

    val outModelMat = ShaderNodeIoVar(this, ModelVarMat4f(uModelMat.name))
    val outViewMat = ShaderNodeIoVar(this, ModelVarMat4f(uViewMat.name))
    val outProjMat = ShaderNodeIoVar(this, ModelVarMat4f(uProjMat.name))
    val outMvpMat = ShaderNodeIoVar(this, ModelVarMat4f("uMvp_outMvp"))

    override fun createUniformBuffer(shaderGraph: ShaderGraph) {
        shaderGraph.descriptorSet.apply {
            uniformBuffer(name, shaderGraph.stage) {
                +{ uModelMat }
                +{ uViewMat }
                +{ uProjMat }
                onUpdate = { _, cmd ->
                    uModelMat.value.set(cmd.modelMat)
                    uViewMat.value.set(cmd.viewMat)
                    uProjMat.value.set(cmd.projMat)
                }
            }
        }
    }

    override fun generateCode(generator: CodeGenerator, pipeline: Pipeline, ctx: KoolContext) {
        generator.appendMain("${outMvpMat.variable.declare()} = ${outProjMat.name} * ${outViewMat.name} * ${outModelMat.name};")
    }
}

class TextureNode(val texName: String) : ShaderNode("Texture $texName") {
    var samplerIdx = 1

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        shaderGraph.descriptorSet.apply {
            +TextureSampler.Builder().apply {
                name = texName
                stages += shaderGraph.stage
            }
        }
    }
}

class TextureSamplerNode(val texture: TextureNode) : ShaderNode("Sample Texture ${texture.texName}") {
    var inTexCoord = ShaderNodeIoVar(this, ModelVar2fConst(Vec2f.ZERO))
    val outColor: ShaderNodeIoVar = ShaderNodeIoVar(this, ModelVar4f("texSampler_${texture.texName}_${++texture.samplerIdx}_outColor"))

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(texture)
        dependsOn(inTexCoord)
    }

    override fun generateCode(generator: CodeGenerator, pipeline: Pipeline, ctx: KoolContext) {
        generator.appendMain("${outColor.variable.declare()} = " +
                "${generator.sampleTexture2d(texture.texName, inTexCoord.variable.ref2f())};")
    }
}

abstract class PushConstantNode<T: Uniform<*>>(name: String) : ShaderNode(name) {
    abstract val output: ShaderNodeIoVar
    lateinit var uniform: T

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        uniform = createUniform()
        shaderGraph.pushConstants += PushConstantRange.Builder().apply {
            stages += shaderGraph.stage
            +{ uniform }
        }
    }

    protected abstract fun createUniform(): T
}

class PushConstantNode1f(name: String) : PushConstantNode<Uniform1f>(name) {
    override val output = ShaderNodeIoVar(this, ModelVar1f(name))
    override fun createUniform(): Uniform1f = Uniform1f(output.variable.name)
}

class PushConstantNode2f(name: String) : PushConstantNode<Uniform2f>(name) {
    override val output = ShaderNodeIoVar(this, ModelVar2f(name))
    override fun createUniform(): Uniform2f = Uniform2f(output.variable.name)
}

class PushConstantNode3f(name: String) : PushConstantNode<Uniform3f>(name) {
    override val output = ShaderNodeIoVar(this, ModelVar3f(name))
    override fun createUniform(): Uniform3f = Uniform3f(output.variable.name)
}

class PushConstantNode4f(name: String) : PushConstantNode<Uniform4f>(name) {
    override val output = ShaderNodeIoVar(this, ModelVar4f(name))
    override fun createUniform(): Uniform4f = Uniform4f(output.variable.name)
}

class AttributeNode(val attribute: Attribute) : ShaderNode("Vertex Attribute ${attribute.name}", ShaderStage.VERTEX_SHADER.mask) {
    val output = ShaderNodeIoVar(this, ModelVar(attribute.glslSrcName, attribute.type))

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        shaderGraph as VertexShaderGraph
        shaderGraph.requiredVertexAttributes += attribute
    }
}

// fixme: for now this assumes the only stages are vertex and fragment
class StageInterfaceNode(name: String) {
    // written in source stage (i.e. vertex shader)
    var input = ShaderNodeIoVar(null, ModelVar1f("_"))
        set(value) {
            field = value
            output = ShaderNodeIoVar(fragmentNode, ModelVar("stageIf_${value.name}", value.variable.type))
        }

    // accessible in target stage (i.e. fragment shader)
    lateinit var output: ShaderNodeIoVar

    private var layout = 0

    val vertexNode = object : ShaderNode("Stage Interface Src $name", ShaderStage.VERTEX_SHADER.mask) {
        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(input)
            layout = shaderGraph.outputs.size
            shaderGraph.outputs += ShaderInterfaceIoVar(layout, output.variable)
        }

        override fun generateCode(generator: CodeGenerator, pipeline: Pipeline, ctx: KoolContext) {
            generator.appendMain("${output.name} = ${input.variable.refAsType(output.variable.type)};")
        }
    }

    val fragmentNode = object : ShaderNode("Stage Interface Dst $name", ShaderStage.FRAGMENT_SHADER.mask) {
        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            shaderGraph.inputs += ShaderInterfaceIoVar(layout, output.variable)
        }
    }
}

class VertexPosTransformNode : ShaderNode("Vertex Pos Transform", ShaderStage.VERTEX_SHADER.mask) {
    var inMvp: ShaderNodeIoVar? = null
    var inPosition: ShaderNodeIoVar = ShaderNodeIoVar(null, ModelVar3fConst(Vec3f.ZERO))
    val outPosition = ShaderNodeIoVar(this, ModelVar4f("plainVertPos_outPosition"))

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inPosition, inMvp ?: throw KoolException("MVP matrix input not set"))

        shaderGraph as VertexShaderGraph
        shaderGraph.positionOutput = outPosition
    }

    override fun generateCode(generator: CodeGenerator, pipeline: Pipeline, ctx: KoolContext) {
        val mvp = inMvp?.variable ?: throw KoolException("MVP matrix input not set")
        generator.appendMain("${outPosition.variable.declare()} = " +
                "${mvp.refAsType(AttributeType.MAT_4F)} * vec4(${inPosition.variable.ref3f()}, 1.0);")
    }
}

class UnlitMaterialNode(var inAlbedo: ShaderNodeIoVar = ShaderNodeIoVar(null, ModelVar4fConst(Color.MAGENTA))) :
        ShaderNode("Unlit Material", ShaderStage.FRAGMENT_SHADER.mask) {

    val outColor = ShaderNodeIoVar(this, ModelVar4f("unlitMat_outColor"))

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inAlbedo)

        shaderGraph as FragmentShaderGraph
        shaderGraph.colorOutput = outColor
    }

    override fun generateCode(generator: CodeGenerator, pipeline: Pipeline, ctx: KoolContext) {
        generator.appendMain("${outColor.variable.declare()} = vec4(${inAlbedo.variable.ref3f()} * " +
                "${inAlbedo.variable.ref4f()}.a, ${inAlbedo.variable.ref4f()}.a);")
    }
}
