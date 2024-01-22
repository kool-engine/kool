package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.modules.ksl.generator.KslGenerator
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.modules.ksl.model.KslState
import de.fabmax.kool.pipeline.ComputePipeline
import de.fabmax.kool.pipeline.DrawPipeline
import de.fabmax.kool.pipeline.PipelineBase
import de.fabmax.kool.pipeline.UniformBufferLayout
import de.fabmax.kool.util.logW

class WgslGenerator : KslGenerator() {

    var blockIndent = "  "

    override fun generateProgram(program: KslProgram, pipeline: DrawPipeline): WgslGeneratorOutput {
        val vertexStage = checkNotNull(program.vertexStage) {
            "KslProgram vertexStage is missing (a valid KslShader needs at least a vertexStage and fragmentStage)"
        }
        val fragmentStage = checkNotNull(program.fragmentStage) {
            "KslProgram fragmentStage is missing (a valid KslShader needs at least a vertexStage and fragmentStage)"
        }
        return WgslGeneratorOutput.shaderOutput(
            generateVertexSrc(vertexStage, pipeline),
            generateFragmentSrc(fragmentStage, pipeline)
        )
    }

    override fun generateComputeProgram(program: KslProgram, pipeline: ComputePipeline): WgslGeneratorOutput {
        val computeStage = checkNotNull(program.computeStage) {
            "KslProgram computeStage is missing"
        }
        return WgslGeneratorOutput.computeOutput(generateComputeSrc(computeStage, pipeline))
    }

    private fun generateVertexSrc(vertexStage: KslVertexStage, pipeline: PipelineBase): String {
        val src = StringBuilder()
        src.appendLine("""
            /*
             * ${vertexStage.program.name} - generated WGSL vertex shader
             */
        """.trimIndent())
        src.appendLine()

        val vertexInput = VertexInputStructs(vertexStage)
        val vertexOutput = VertexOutputStruct(vertexStage)
        val ubos = UboStructs(vertexStage, pipeline)

        ubos.generateStructs(src)
        vertexInput.generateStructs(src)
        vertexOutput.generateStruct(src)
//        src.generateUniformSamplers(vertexStage, pipeline)
//        src.generateUniformStorage(vertexStage, pipeline)
//        src.generateFunctions(vertexStage)

        src.appendLine("@vertex")
        src.appendLine("fn vertexMain(vertexInput: VertexInput) -> VertexOutput {")

        src.appendLine("  var vertexOutput: VertexOutput;")
        vertexOutput.generateExplodedMembers(src)
        vertexInput.generateExplodedMembers(src)
        ubos.generateExplodedMembers(src)

        src.appendLine(generateScope(vertexStage.main, blockIndent))

        vertexOutput.generateSetMembers(src)
        src.appendLine("  return vertexOutput;")
        src.appendLine("}")
        return src.toString()
    }

    private fun generateFragmentSrc(fragmentStage: KslFragmentStage, pipeline: PipelineBase): String {
        val src = StringBuilder()
        src.appendLine("""
            /*
             * ${fragmentStage.program.name} - generated WGSL fragment shader
             */
        """.trimIndent())
        src.appendLine()

        val fragmentInput = FragmentInputStruct(fragmentStage)
        val fragmentOutput = FragmentOutputStruct(fragmentStage)
        val ubos = UboStructs(fragmentStage, pipeline)

        ubos.generateStructs(src)
        fragmentInput.generateStructs(src)
        fragmentOutput.generateStruct(src)
//        src.generateUniformSamplers(fragmentStage, pipeline)
//        src.generateUniformStorage(fragmentStage, pipeline)
//        src.generateFunctions(fragmentStage)

        src.appendLine("@fragment")
        src.appendLine("fn fragmentMain(fragmentInput: FragmentInput) -> FragmentOutput {")

        src.appendLine("  var fragmentOutput: FragmentOutput;")
        fragmentOutput.generateExplodedMembers(src)
        fragmentInput.generateExplodedMembers(src)
        ubos.generateExplodedMembers(src)

        src.appendLine(generateScope(fragmentStage.main, blockIndent))

        fragmentOutput.generateSetMembers(src)
        src.appendLine("  return fragmentOutput;")
        src.appendLine("}")
        return src.toString()
    }

    private fun generateComputeSrc(computeStage: KslComputeStage, pipeline: PipelineBase): String {
        val src = StringBuilder()
        src.appendLine("""
            /*
             * ${computeStage.program.name} - generated WGSL compute shader
             */
        """.trimIndent())
        src.appendLine()

        val ubos = UboStructs(computeStage, pipeline)
        ubos.generateStructs(src)
//        src.generateUniformSamplers(computeStage, pipeline)
//        src.generateUniformStorage(computeStage, pipeline)
//        src.generateFunctions(computeStage)

        src.appendLine("@compute")
        src.appendLine("@workgroup_size(${computeStage.workGroupSize.x}, ${computeStage.workGroupSize.y}, ${computeStage.workGroupSize.z})")
        src.appendLine("fn computeMain(input: VertexOutput) {")
        ubos.generateExplodedMembers(src)
        src.appendLine(generateScope(computeStage.main, blockIndent))
        src.appendLine("}")
        return src.toString()
    }

    private inner class UboStructs(stage: KslShaderStage, pipeline: PipelineBase) : WgslStructHelper {

        val structs: List<UboStruct> = buildList {
            pipeline.bindGroupLayouts.asList.forEach { layout ->
                layout.bindings
                    .filterIsInstance<UniformBufferLayout>().filter { layoutUbo ->
                        stage.getUsedUbos().any { usedUbo -> usedUbo.name == layoutUbo.name }
                    }
                    .map { ubo ->
                        val kslUbo = stage.getUsedUbos().first { it.name == ubo.name }
                        val uboTypeName = ubo.name.mapIndexed { i, c -> if (i == 0) c.uppercase() else c }.joinToString("")
                        val uboVarName = ubo.name.mapIndexed { i, c -> if (i == 0) c.lowercase() else c }.joinToString("")
                        val members = kslUbo.uniforms.values
                            .filter { it.expressionType !is KslArrayType<*> || it.arraySize > 0 }
                            .map { WgslStructMember(uboVarName, it.value.name(), it.expressionType.wgslTypeName) }

                        add(UboStruct(uboVarName, uboTypeName, members, layout.group, ubo.bindingIndex))
                    }
            }
        }

        fun generateStructs(builder: StringBuilder) = builder.apply {
            structs.forEach { ubo -> generateStruct(ubo.typeName, ubo.members) }
            structs.forEach { ubo ->
                appendLine("@group(${ubo.group}) @binding(${ubo.binding}) var<uniform> ${ubo.name}: ${ubo.typeName};")
            }
            appendLine()
        }

        fun generateExplodedMembers(builder: StringBuilder) = builder.apply {
            structs.forEach { ubo -> createAndAssignExplodedMembers(ubo.members) }
        }
    }

    private data class UboStruct(val name: String, val typeName: String, val members: List<WgslStructMember>, val group: Int, val binding: Int)

    private class VertexInputStructs(stage: KslVertexStage) : WgslStructHelper {
        val vertexInputs = stage.attributes.values
            .filter { it.inputRate == KslInputRate.Vertex }
            .mapIndexed { i, attr ->
                WgslStructMember("vertexInput", attr.name, attr.expressionType.wgslTypeName, "@location($i) ")
            }
        val instanceInputs = stage.attributes.values
            .filter { it.inputRate == KslInputRate.Instance }
            .mapIndexed { i, attr ->
                WgslStructMember("instanceInput", attr.name, attr.expressionType.wgslTypeName, "@location($i) ")
            }
        val vertexIndex = if (stage.isUsingVertexIndex) {
            WgslStructMember("vertexInput", "vertexIndex", "u32", "@builtin(vertex_index) ")
        } else null

        val instanceIndex = if (stage.isUsingInstanceIndex) {
            WgslStructMember("vertexInput", "instanceIndex", "u32", "@builtin(instance_index) ")
        } else null

        fun generateStructs(builder: StringBuilder) = builder.apply {
            generateStruct("VertexInput", vertexInputs, vertexIndex, instanceIndex)
            generateStruct("InstanceInput", instanceInputs)
        }

        fun generateExplodedMembers(builder: StringBuilder) = builder.apply {
            createAndAssignExplodedMembers(vertexInputs + instanceInputs, vertexIndex, instanceIndex)
        }
    }

    private inner class VertexOutputStruct(stage: KslVertexStage) : WgslStructHelper {
        val vertexOutputs = stage.interStageVars
            .mapIndexed { i, output ->
                val outVal = output.output
                val interp = if (output.interpolation == KslInterStageInterpolation.Flat) " @interpolate(flat)" else ""
                WgslStructMember("vertexOutput", outVal.name(), outVal.expressionType.wgslTypeName, "@location($i)$interp ")
            }
        val position = WgslStructMember("vertexOutput", "position", "vec4<f32>", "@builtin(position) ")
        val pointSize = if (stage.isSettingPointSize) {
            WgslStructMember("vertexOutput", "pointSize", "f32", "@location(${stage.interStageVars.size}) ")
        } else null

        init {
            if (pointSize != null) {
                logW { "Ignoring vertex shader point size output: Not supported by WGSL" }
            }
        }

        fun generateStruct(builder: StringBuilder) = builder.apply {
            generateStruct("VertexOutput", vertexOutputs, position, pointSize)
        }

        fun generateExplodedMembers(builder: StringBuilder) = builder.apply {
            createExplodedMembers(vertexOutputs, position, pointSize)
        }

        fun generateSetMembers(builder: StringBuilder) = builder.apply {
            assignStructMembers(vertexOutputs, position, pointSize)
        }
    }

    private inner class FragmentInputStruct(stage: KslFragmentStage) : WgslStructHelper {
        val fragmentInputs = stage.interStageVars
            .mapIndexed { i, input ->
                val inVal = input.input
                val interp = if (input.interpolation == KslInterStageInterpolation.Flat) " @interpolate(flat)" else ""
                WgslStructMember("fragmentInput", inVal.name(), inVal.expressionType.wgslTypeName, "@location($i)$interp ")
            }
        val fragPosition = if (stage.isUsingFragPosition) {
            WgslStructMember("fragmentInput", "position", " vec4<f32>", "@builtin(position) ")
        } else null

        val isFrontFacing = if (stage.isUsingIsFrontFacing) {
            WgslStructMember("fragmentInput", "isFrontFacing", "bool", "@builtin(front_facing) ")
        } else null

        fun generateStructs(builder: StringBuilder) = builder.apply {
            generateStruct("FragmentInput", fragmentInputs, fragPosition, isFrontFacing)
        }

        fun generateExplodedMembers(builder: StringBuilder) = builder.apply {
            createAndAssignExplodedMembers(fragmentInputs, fragPosition, isFrontFacing)
        }
    }

    private inner class FragmentOutputStruct(stage: KslFragmentStage) : WgslStructHelper {
        val outColors = stage.outColors.mapIndexed { i, outColor ->
            WgslStructMember("fragmentOutput", outColor.value.name(), outColor.value.expressionType.wgslTypeName, "@location($i) ")
        }
        val fragDepth = if (stage.isSettingFragDepth) {
            WgslStructMember("fragmentOutput", "fragDepth", "f32", "@builtin(frag_depth) ")
        } else null

        fun generateStruct(builder: StringBuilder) = builder.apply {
            generateStruct("FragmentOutput", outColors, fragDepth)
        }

        fun generateExplodedMembers(builder: StringBuilder) = builder.apply {
            createExplodedMembers(outColors, fragDepth)
        }

        fun generateSetMembers(builder: StringBuilder) = builder.apply {
            assignStructMembers(outColors, fragDepth)
        }
    }

    override fun constFloatVecExpression(vararg values: KslExpression<KslFloat1>) =
        constVecExpression("f32", values.toList())

    override fun constIntVecExpression(vararg values: KslExpression<KslInt1>) =
        constVecExpression("i32", values.toList())

    override fun constUintVecExpression(vararg values: KslExpression<KslUint1>) =
        constVecExpression("u32", values.toList())

    override fun constBoolVecExpression(vararg values: KslExpression<KslBool1>) =
        constVecExpression("bool", values.toList())

    private fun constVecExpression(type: String, values: List<KslExpression<*>>): String {
        check (values.size in 2..4) { "invalid vec dimension: ${values.size} (must be between 2 and 4)" }
        return "vec${values.size}<$type>(${values.joinToString { it.generateExpression(this) }})"
    }

    override fun constMatExpression(vararg columns: KslVectorExpression<*, KslFloat1>): String {
        val d = columns.size
        check (d in 2..4) { "invalid mat dimension: ${d}x$d (must be between 2 and 4)" }
        return "mat${d}x${d}<f32>(${columns.joinToString { it.generateExpression(this) }})"
    }

    override fun castExpression(castExpr: KslExpressionCast<*>): String {
        return "${castExpr.expressionType.wgslTypeName}(${castExpr.value.generateExpression(this)})"
    }

    override fun <B: KslBoolType> compareExpression(expression: KslExpressionCompare<B>): String {
        val lt = expression.left.generateExpression(this)
        val rt = expression.right.generateExpression(this)
        return "($lt ${expression.operator.opString} $rt)"
    }

    override fun sampleColorTexture(sampleTexture: KslSampleColorTexture<*>): String {
        // fixme: in contrast to GLSL, WGSL distinguishes between sampler and texture (which makes sense)
        //  for now we generate the texture from the sampler expression by simply appending _tex, this is
        //  only a hack!
        val samplerName = sampleTexture.sampler.generateExpression(this)
        return "textureSample(${samplerName}_tex, $samplerName, ${sampleTexture.coord.generateExpression(this)})"
    }

    override fun sampleDepthTexture(sampleTexture: KslSampleDepthTexture<*>): String {
        TODO()
    }

    override fun textureSize(textureSize: KslTextureSize<*, *>): String {
        TODO()
    }

    override fun texelFetch(expression: KslTexelFetch<*>): String {
        TODO()
    }

    override fun storageSize(storageSize: KslStorageSize<*, *>): String {
        TODO()
    }

    override fun storageRead(storageRead: KslStorageRead<*, *, *>): String {
        TODO()
    }

    override fun opStorageWrite(op: KslStorageWrite<*, *, *>): String {
        TODO()
    }

    override fun storageAtomicOp(atomicOp: KslStorageAtomicOp<*, *, *>): String {
        TODO()
    }

    override fun storageAtomicCompareSwap(atomicCompSwap: KslStorageAtomicCompareSwap<*, *, *>): String {
        TODO()
    }

//    protected open fun StringBuilder.generateUniformSamplers(stage: KslShaderStage, pipeline: PipelineBase) {
//        val samplers = stage.getUsedSamplers()
//        if (samplers.isNotEmpty()) {
//            appendLine("// texture samplers")
//            for (u in samplers) {
//                appendLine("uniform ${wgslTypeName(u.expressionType)} ${u.value.name()};")
//            }
//            appendLine()
//        }
//    }
//
//    protected open fun StringBuilder.generateUniformStorage(stage: KslShaderStage, pipeline: PipelineBase) {
//        val storage = stage.getUsedStorage()
//        if (storage.isNotEmpty()) {
//            appendLine("// image storage")
//            storage.forEachIndexed { i, it ->
//                appendLine("layout(${it.storageType.formatQualifier}, binding=$i) uniform ${wgslTypeName(it.expressionType)} ${it.name};")
//            }
//            appendLine()
//        }
//    }

//    protected open fun StringBuilder.generateInterStageInputs(fragmentStage: KslFragmentStage) {
//        if (fragmentStage.interStageVars.isNotEmpty()) {
//            appendLine("// custom fragment stage inputs")
//            fragmentStage.interStageVars.forEach { interStage ->
//                val value = interStage.output
//                appendLine("${interStage.interpolation.glsl()} in ${wgslTypeName(value.expressionType)} ${value.name()};")
//            }
//            appendLine()
//        }
//    }
//
//    protected open fun StringBuilder.generateOutputs(outputs: List<KslStageOutput<*>>) {
//        if (outputs.isNotEmpty()) {
//            appendLine("// stage outputs")
//            outputs.forEach { output ->
//                val loc = if (output.location >= 0) "layout(location=${output.location}) " else ""
//                appendLine("${loc}out ${wgslTypeName(output.expressionType)} ${output.value.name()};")
//            }
//            appendLine()
//        }
//    }
//
//    private fun StringBuilder.generateFunctions(stage: KslShaderStage) {
//        if (stage.functions.isNotEmpty()) {
//            val funcList = stage.functions.values.toMutableList()
//            sortFunctions(funcList)
//            funcList.forEach { func ->
//                appendLine("${wgslTypeName(func.returnType)} ${func.name}(${func.parameters.joinToString { p -> "${wgslTypeName(p.expressionType)} ${p.stateName}" }}) {")
//                appendLine(generateScope(func.body, blockIndent))
//                appendLine("}")
//                appendLine()
//            }
//        }
//    }

    override fun opDeclareVar(op: KslDeclareVar): String {
        val initExpr = op.initExpression?.generateExpression(this) ?: ""
        val state = op.declareVar
        return "var ${state.name()} = ${state.expressionType.wgslTypeName}(${initExpr});"
    }

    override fun opDeclareArray(op: KslDeclareArray): String {
        TODO()
//        val array = op.declareVar
//        val typeName = wgslTypeName(array.expressionType)
//
//        return if (op.elements.size == 1 && op.elements[0].expressionType == array.expressionType) {
//            "$typeName ${array.name()} = ${op.elements[0].generateExpression(this)};"
//        } else {
//            val initExpr = op.elements.joinToString { it.generateExpression(this) }
//            "$typeName ${array.name()} = ${typeName}(${initExpr});"
//        }
    }

    override fun opAssign(op: KslAssign<*>): String {
        return "${op.assignTarget.generateAssignable(this)} = ${op.assignExpression.generateExpression(this)};"
    }

    override fun opAugmentedAssign(op: KslAugmentedAssign<*>): String {
        return "${op.assignTarget.generateAssignable(this)} ${op.augmentationMode.opChar}= ${op.assignExpression.generateExpression(this)};"
    }

    override fun opIf(op: KslIf): String {
        val txt = StringBuilder("if ${op.condition.generateExpression(this)} {\n")
        txt.appendLine(generateScope(op.body, blockIndent))
        txt.append("}")
        op.elseIfs.forEach { elseIf ->
            txt.appendLine(" else if ${elseIf.first.generateExpression(this)} {")
            txt.appendLine(generateScope(elseIf.second, blockIndent))
            txt.append("}")
        }
        if (op.elseBody.isNotEmpty()) {
            txt.appendLine(" else {")
            txt.appendLine(generateScope(op.elseBody, blockIndent))
            txt.append("}")
        }
        return txt.toString()
    }

    override fun opFor(op: KslLoopFor<*>): String {
        TODO()
//        return StringBuilder("for (; ")
//            .append(op.whileExpression.generateExpression(this)).append("; ")
//            .append(op.loopVar.generateAssignable(this)).append(" += ").append(op.incExpr.generateExpression(this))
//            .appendLine(") {")
//            .appendLine(generateScope(op.body, blockIndent))
//            .append("}")
//            .toString()
    }

    override fun opWhile(op: KslLoopWhile): String {
        TODO()
//        return StringBuilder("while (${op.whileExpression.generateExpression(this)}) {\n")
//            .appendLine(generateScope(op.body, blockIndent))
//            .append("}")
//            .toString()
    }

    override fun opDoWhile(op: KslLoopDoWhile): String {
        TODO()
//        return StringBuilder("do {\n")
//            .appendLine(generateScope(op.body, blockIndent))
//            .append("} while (${op.whileExpression.generateExpression(this)});")
//            .toString()
    }

    override fun opBreak(op: KslLoopBreak) = "break;"

    override fun opContinue(op: KslLoopContinue) = "continue;"

    override fun opDiscard(op: KslDiscard): String = "discard;"

    override fun opReturn(op: KslReturn): String = "return ${op.returnValue.generateExpression(this)};"

    override fun opBlock(op: KslBlock): String {
        val txt = StringBuilder("{ // block: ${op.opName}\n")
        txt.appendLine(generateScope(op.body, blockIndent))
        txt.append("}")
        return txt.toString()
    }

    override fun opInlineCode(op: KslInlineCode): String {
        return op.code
    }

    private fun generateArgs(args: List<KslExpression<*>>, expectedArgs: Int): String {
        check(args.size == expectedArgs)
        return args.joinToString { it.generateExpression(this) }
    }

    override fun invokeFunction(func: KslInvokeFunction<*>) = "${func.function.name}(${generateArgs(func.args, func.args.size)})"

    override fun builtinAbs(func: KslBuiltinAbsScalar<*>) = "abs(${generateArgs(func.args, 1)})"
    override fun builtinAbs(func: KslBuiltinAbsVector<*, *>) = "abs(${generateArgs(func.args, 1)})"
    override fun builtinAtan2(func: KslBuiltinAtan2Scalar) = "atan2(${generateArgs(func.args, 2)})"
    override fun builtinAtan2(func: KslBuiltinAtan2Vector<*>) = "atan2(${generateArgs(func.args, 2)})"
    override fun builtinCeil(func: KslBuiltinCeilScalar) = "ceil(${generateArgs(func.args, 1)})"
    override fun builtinCeil(func: KslBuiltinCeilVector<*>) = "ceil(${generateArgs(func.args, 1)})"
    override fun builtinClamp(func: KslBuiltinClampScalar<*>) = "clamp(${generateArgs(func.args, 3)})"
    override fun builtinClamp(func: KslBuiltinClampVector<*, *>) = "clamp(${generateArgs(func.args, 3)})"
    override fun builtinCross(func: KslBuiltinCross) = "cross(${generateArgs(func.args, 2)})"
    override fun builtinDegrees(func: KslBuiltinDegreesScalar) = "degrees(${generateArgs(func.args, 1)})"
    override fun builtinDegrees(func: KslBuiltinDegreesVector<*>) = "degrees(${generateArgs(func.args, 1)})"
    override fun builtinDistance(func: KslBuiltinDistanceScalar<*>) = "distance(${generateArgs(func.args, 2)})"
    override fun builtinDot(func: KslBuiltinDot<*>) = "dot(${generateArgs(func.args, 2)})"
    override fun builtinExp(func: KslBuiltinExpScalar) = "exp(${generateArgs(func.args, 1)})"
    override fun builtinExp(func: KslBuiltinExpVector<*>) = "exp(${generateArgs(func.args, 1)})"
    override fun builtinExp2(func: KslBuiltinExp2Scalar) = "exp2(${generateArgs(func.args, 1)})"
    override fun builtinExp2(func: KslBuiltinExp2Vector<*>) = "exp2(${generateArgs(func.args, 1)})"
    override fun builtinFaceForward(func: KslBuiltinFaceForward<*>) = "faceForward(${generateArgs(func.args, 3)})"
    override fun builtinFloor(func: KslBuiltinFloorScalar) = "floor(${generateArgs(func.args, 1)})"
    override fun builtinFloor(func: KslBuiltinFloorVector<*>) = "floor(${generateArgs(func.args, 1)})"
    override fun builtinFma(func: KslBuiltinFmaScalar) = "fma(${generateArgs(func.args, 3)})"
    override fun builtinFma(func: KslBuiltinFmaVector<*>) = "fma(${generateArgs(func.args, 3)})"
    override fun builtinFract(func: KslBuiltinFractScalar) = "fract(${generateArgs(func.args, 1)})"
    override fun builtinFract(func: KslBuiltinFractVector<*>) = "fract(${generateArgs(func.args, 1)})"
    override fun builtinInverseSqrt(func: KslBuiltinInverseSqrtScalar) = "inverseSqrt(${generateArgs(func.args, 1)})"
    override fun builtinInverseSqrt(func: KslBuiltinInverseSqrtVector<*>) = "inverseSqrt(${generateArgs(func.args, 1)})"
    override fun builtinIsInf(func: KslBuiltinIsInfScalar) = TODO() //"isinf(${generateArgs(func.args, 1)})"
    override fun builtinIsInf(func: KslBuiltinIsInfVector<*, *>) = TODO() //"isinf(${generateArgs(func.args, 1)})"
    override fun builtinIsNan(func: KslBuiltinIsNanScalar) = "(${func.args[0].generateExpression(this)} != ${func.args[0].generateExpression(this)})"
    override fun builtinIsNan(func: KslBuiltinIsNanVector<*, *>) = "(${func.args[0].generateExpression(this)} != ${func.args[0].generateExpression(this)})"
    override fun builtinLength(func: KslBuiltinLength<*>) = "length(${generateArgs(func.args, 1)})"
    override fun builtinLog(func: KslBuiltinLogScalar) = "log(${generateArgs(func.args, 1)})"
    override fun builtinLog(func: KslBuiltinLogVector<*>) = "log(${generateArgs(func.args, 1)})"
    override fun builtinLog2(func: KslBuiltinLog2Scalar) = "log2(${generateArgs(func.args, 1)})"
    override fun builtinLog2(func: KslBuiltinLog2Vector<*>) = "log2(${generateArgs(func.args, 1)})"
    override fun builtinMax(func: KslBuiltinMaxScalar<*>) = "max(${generateArgs(func.args, 2)})"
    override fun builtinMax(func: KslBuiltinMaxVector<*, *>) = "max(${generateArgs(func.args, 2)})"
    override fun builtinMin(func: KslBuiltinMinScalar<*>) = "min(${generateArgs(func.args, 2)})"
    override fun builtinMin(func: KslBuiltinMinVector<*, *>) = "min(${generateArgs(func.args, 2)})"
    override fun builtinMix(func: KslBuiltinMixScalar) = "mix(${generateArgs(func.args, 3)})"
    override fun builtinMix(func: KslBuiltinMixVector<*>) = "mix(${generateArgs(func.args, 3)})"
    override fun builtinNormalize(func: KslBuiltinNormalize<*>) = "normalize(${generateArgs(func.args, 1)})"
    override fun builtinReflect(func: KslBuiltinReflect<*>) = "reflect(${generateArgs(func.args, 2)})"
    override fun builtinRefract(func: KslBuiltinRefract<*>) = "refract(${generateArgs(func.args, 3)})"
    override fun builtinRound(func: KslBuiltinRoundScalar) = "round(${generateArgs(func.args, 1)})"
    override fun builtinRound(func: KslBuiltinRoundVector<*>) = "round(${generateArgs(func.args, 1)})"
    override fun builtinSign(func: KslBuiltinSignScalar<*>) = "sign(${generateArgs(func.args, 1)})"
    override fun builtinSign(func: KslBuiltinSignVector<*, *>) = "sign(${generateArgs(func.args, 1)})"
    override fun builtinPow(func: KslBuiltinPowScalar) = "pow(${generateArgs(func.args, 2)})"
    override fun builtinPow(func: KslBuiltinPowVector<*>) = "pow(${generateArgs(func.args, 2)})"
    override fun builtinRadians(func: KslBuiltinRadiansScalar) = "radians(${generateArgs(func.args, 1)})"
    override fun builtinRadians(func: KslBuiltinRadiansVector<*>) = "radians(${generateArgs(func.args, 1)})"
    override fun builtinSmoothStep(func: KslBuiltinSmoothStepScalar) = "smoothstep(${generateArgs(func.args, 3)})"
    override fun builtinSmoothStep(func: KslBuiltinSmoothStepVector<*>) = "smoothstep(${generateArgs(func.args, 3)})"
    override fun builtinSqrt(func: KslBuiltinSqrtScalar) = "sqrt(${generateArgs(func.args, 1)})"
    override fun builtinSqrt(func: KslBuiltinSqrtVector<*>) = "sqrt(${generateArgs(func.args, 1)})"
    override fun builtinStep(func: KslBuiltinStepScalar) = "step(${generateArgs(func.args, 2)})"
    override fun builtinStep(func: KslBuiltinStepVector<*>) = "step(${generateArgs(func.args, 2)})"
    override fun builtinTrigonometry(func: KslBuiltinTrigonometryScalar) = "${func.name}(${generateArgs(func.args, 1)})"
    override fun builtinTrigonometry(func: KslBuiltinTrigonometryVector<*>) = "${func.name}(${generateArgs(func.args, 1)})"
    override fun builtinTrunc(func: KslBuiltinTruncScalar) = "trunc(${generateArgs(func.args, 1)})"
    override fun builtinTrunc(func: KslBuiltinTruncVector<*>) = "trunc(${generateArgs(func.args, 1)})"

    override fun builtinDeterminant(func: KslBuiltinDeterminant<*, *>) = "determinant(${generateArgs(func.args, 1)})"
    override fun builtinTranspose(func: KslBuiltinTranspose<*, *>) = "transpose(${generateArgs(func.args, 1)})"

    override fun KslState.name(): String {
        return when (stateName) {
            KslVertexStage.NAME_IN_VERTEX_INDEX -> "vertexInput.vertexIndex"            // vertex_index
            KslVertexStage.NAME_IN_INSTANCE_INDEX -> "vertexInput.instanceIndex"        // instance_index
            KslVertexStage.NAME_OUT_POSITION -> "position"                              // position
            KslVertexStage.NAME_OUT_POINT_SIZE -> "pointSize"                           // <unsupported>

            KslFragmentStage.NAME_IN_FRAG_POSITION -> "fragmentInput.position"          // position
            KslFragmentStage.NAME_IN_IS_FRONT_FACING -> "fragmentInput.isFrontFacing"   // front_facing
            KslFragmentStage.NAME_OUT_DEPTH -> "fragDepth"                              // frag_depth
                                                                                        // not-implemented: sample_index
                                                                                        // not-implemented: sample_mask

            KslComputeStage.NAME_IN_GLOBAL_INVOCATION_ID -> "computeInput.globalInvId"  // global_invocation_id
            KslComputeStage.NAME_IN_LOCAL_INVOCATION_ID -> "computeInput.localInvId"    // local_invocation_id
            KslComputeStage.NAME_IN_WORK_GROUP_ID -> "computeInput.workgroupId"         // workgroup_id
            KslComputeStage.NAME_IN_NUM_WORK_GROUPS -> "computeInput.numWorkgroups"     // num_workgroups
            KslComputeStage.NAME_IN_WORK_GROUP_SIZE -> "workgroupSize"                  // <emulated>

            else -> stateName
        }
    }

    companion object {
        val KslType.wgslTypeName: String get() {
            return when (this) {
                KslTypeVoid -> TODO()
                KslBool1 -> "bool"
                KslBool2 -> "vec2<bool>"
                KslBool3 -> "vec3<bool>"
                KslBool4 -> "vec4<bool>"
                KslFloat1 -> "f32"
                KslFloat2 -> "vec2<f32>"
                KslFloat3 -> "vec3<f32>"
                KslFloat4 -> "vec4<f32>"
                KslInt1 -> "i32"
                KslInt2 -> "vec2<i32>"
                KslInt3 -> "vec3<i32>"
                KslInt4 -> "vec4<i32>"
                KslUint1 -> "u32"
                KslUint2 -> "vec2<u32>"
                KslUint3 -> "vec3<u32>"
                KslUint4 -> "vec4<u32>"
                KslMat2 -> "mat2x2<f32>"
                KslMat3 -> "mat3x3<f32>"
                KslMat4 -> "mat4x4<f32>"

                KslColorSampler1d -> TODO()
                KslColorSampler2d -> TODO()
                KslColorSampler3d -> TODO()
                KslColorSamplerCube -> TODO()
                KslColorSampler2dArray -> TODO()
                KslColorSamplerCubeArray -> TODO()

                KslDepthSampler2d -> TODO()
                KslDepthSamplerCube -> TODO()
                KslDepthSampler2dArray -> TODO()
                KslDepthSamplerCubeArray -> TODO()

                is KslArrayType<*> -> TODO()

                is KslStorage1dType<*> -> TODO()
                is KslStorage2dType<*> -> TODO()
                is KslStorage3dType<*> -> TODO()
            }
        }
    }

    class WgslGeneratorOutput(
        val vertexEntryPoint: String = "vertexMain",
        val fragmentEntryPoint: String = "fragmentMain",
        val computeEntryPoint: String = "computeMain"
    ) : GeneratedSourceOutput() {
        companion object {
            fun shaderOutput(vertexSrc: String, fragmentSrc: String) = WgslGeneratorOutput().apply {
                stages[KslShaderStageType.VertexShader] = vertexSrc
                stages[KslShaderStageType.FragmentShader] = fragmentSrc
            }

            fun computeOutput(computeSrc: String) = WgslGeneratorOutput().apply {
                stages[KslShaderStageType.ComputeShader] = computeSrc
            }
        }
    }
}