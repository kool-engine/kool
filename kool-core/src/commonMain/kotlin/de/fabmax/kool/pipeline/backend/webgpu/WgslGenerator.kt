package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.modules.ksl.generator.KslGenerator
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.modules.ksl.model.KslState
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.logW

class WgslGenerator : KslGenerator() {

    var blockIndent = "  "

    private var generatorState = GeneratorState(BindGroupLayouts(BindGroupLayout.EMPTY_VIEW, BindGroupLayout.EMPTY_PIPELINE, BindGroupLayout.EMPTY_MESH))

    override fun generateProgram(program: KslProgram, pipeline: DrawPipeline): WgslGeneratorOutput {
        val vertexStage = checkNotNull(program.vertexStage) {
            "KslProgram vertexStage is missing (a valid KslShader needs at least a vertexStage and fragmentStage)"
        }
        val fragmentStage = checkNotNull(program.fragmentStage) {
            "KslProgram fragmentStage is missing (a valid KslShader needs at least a vertexStage and fragmentStage)"
        }

        generatorState = GeneratorState(pipeline.bindGroupLayouts)
        return WgslGeneratorOutput.shaderOutput(
            generateVertexSrc(vertexStage, pipeline),
            generateFragmentSrc(fragmentStage, pipeline)
        )
    }

    override fun generateComputeProgram(program: KslProgram, pipeline: ComputePipeline): WgslGeneratorOutput {
        val computeStage = checkNotNull(program.computeStage) {
            "KslProgram computeStage is missing"
        }

        generatorState = GeneratorState(pipeline.bindGroupLayouts)
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
        src.generateTextureSamplers(vertexStage, pipeline)
        src.generateFunctions(vertexStage)

        src.appendLine("@vertex")
        src.appendLine("fn vertexMain(vertexInput: VertexInput) -> VertexOutput {")
        src.appendLine("  var vertexOutput: VertexOutput;")
        src.appendLine(generateScope(vertexStage.main, blockIndent))
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
        fragmentInput.generateStruct(src)
        fragmentOutput.generateStruct(src)
        src.generateTextureSamplers(fragmentStage, pipeline)
        src.generateFunctions(fragmentStage)

        src.appendLine("@fragment")
        src.appendLine("fn fragmentMain(fragmentInput: FragmentInput) -> FragmentOutput {")
        src.appendLine("  var fragmentOutput: FragmentOutput;")
        src.appendLine(generateScope(fragmentStage.main, blockIndent))
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
        src.generateTextureSamplers(computeStage, pipeline)
//        src.generateUniformStorage(computeStage, pipeline)
//        src.generateFunctions(computeStage)

        /*
           todo: map compute inputs
            KslComputeStage.NAME_IN_GLOBAL_INVOCATION_ID -> "computeInput.globalInvId"  // global_invocation_id
            KslComputeStage.NAME_IN_LOCAL_INVOCATION_ID -> "computeInput.localInvId"    // local_invocation_id
            KslComputeStage.NAME_IN_WORK_GROUP_ID -> "computeInput.workgroupId"         // workgroup_id
            KslComputeStage.NAME_IN_NUM_WORK_GROUPS -> "computeInput.numWorkgroups"     // num_workgroups
            KslComputeStage.NAME_IN_WORK_GROUP_SIZE -> "workgroupSize"                  // <emulated>
         */

        src.appendLine("@compute")
        src.appendLine("@workgroup_size(${computeStage.workGroupSize.x}, ${computeStage.workGroupSize.y}, ${computeStage.workGroupSize.z})")
        src.appendLine("fn computeMain(input: VertexOutput) {")
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
                            .map { WgslStructMember(uboVarName, it.value.stateName, it.expressionType.wgslTypeName()) }

                        add(UboStruct(uboVarName, uboTypeName, members, ubo))
                    }
            }
        }

        fun generateStructs(builder: StringBuilder) = builder.apply {
            structs.forEach { ubo -> generateStruct(ubo.typeName, ubo.members) }
            structs.forEach { ubo ->
                val location = generatorState.locations[ubo.binding]
                appendLine("@group(${location.group}) @binding(${location.binding}) var<uniform> ${ubo.name}: ${ubo.typeName};")
            }
            appendLine()
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
        return "${castExpr.expressionType.wgslTypeName()}(${castExpr.value.generateExpression(this)})"
    }

    override fun <B: KslBoolType> compareExpression(expression: KslExpressionCompare<B>): String {
        val lt = expression.left.generateExpression(this)
        val rt = expression.right.generateExpression(this)
        return "($lt ${expression.operator.opString} $rt)"
    }

    override fun sampleColorTexture(sampleTexture: KslSampleColorTexture<*>): String {
        val samplerName = sampleTexture.sampler.generateExpression(this)
        return "textureSample(${samplerName}, ${samplerName}_sampler, ${sampleTexture.coord.generateExpression(this)})"
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

    private fun StringBuilder.generateFunctions(stage: KslShaderStage) {
        if (stage.functions.isNotEmpty()) {
            val funcList = stage.functions.values.toMutableList()
            sortFunctions(funcList)
            funcList.forEach { func ->
                val returnType = if (func.returnType == KslTypeVoid) "" else " -> ${func.returnType.wgslTypeName()}"
                appendLine("fn ${func.name}(${func.parameters.joinToString { p -> "${p.name()}: ${p.expressionType.wgslTypeName()}" }})$returnType {")
                appendLine(generateScope(func.body, blockIndent))
                appendLine("}")
                appendLine()
            }
        }
    }

    override fun opDeclareVar(op: KslDeclareVar): String {
        val initExpr = op.initExpression?.generateExpression(this) ?: ""
        val state = op.declareVar
        return "var ${state.name()} = ${state.expressionType.wgslTypeName()}(${initExpr});"
    }

    override fun opDeclareArray(op: KslDeclareArray): String {
        val array = op.declareVar
        val typeName = array.expressionType.wgslTypeName()

        return if (op.elements.size == 1 && op.elements[0].expressionType == array.expressionType) {
            "var ${array.name()} = ${op.elements[0].generateExpression(this)};"
        } else {
            val initExpr = op.elements.joinToString { it.generateExpression(this) }
            "var ${array.name()} = ${typeName}(${initExpr});"
        }
    }

    override fun opAssign(op: KslAssign<*>): String {
        return if (op.assignTarget is KslVectorAccessor<*> && op.assignTarget.components.length > 1) {
            // wgsl currently does not permit assignment of swizzled values
            // https://github.com/gpuweb/gpuweb/issues/737

            val vec = op.assignTarget.vector as KslVectorExpression<*,*>
            val assignType = vec.expressionType.wgslTypeName()
            val targetComps = op.assignTarget.components
            val assignDimens = (vec.expressionType as KslVector<*>).dimens

            val compsXyzw = listOf('x', 'y', 'z', 'w')
            val compsRgba = listOf('r', 'g', 'b', 'a')

            val target = op.assignTarget.vector.generateExpression(this)
            val tmpVarName = generatorState.nextTempVar()
            val ctorArgs = buildString {
                for (i in 0 until assignDimens) {
                    val c1 = targetComps.indexOf(compsXyzw[i])
                    val c2 = targetComps.indexOf(compsRgba[i])
                    val src = when {
                        c1 >= 0 -> "${tmpVarName}.${compsXyzw[c1]}"
                        c2 >= 0 -> "${tmpVarName}.${compsXyzw[c2]}"
                        else -> "${target}.${compsXyzw[i]}"
                    }
                    append(src)
                    if (i < assignDimens-1) {
                        append(", ")
                    }
                }
            }
            """
                let $tmpVarName = ${op.assignExpression.generateExpression(this)};
                $target = $assignType($ctorArgs);
            """.trimIndent()
        } else {
            "${op.assignTarget.generateAssignable(this)} = ${op.assignExpression.generateExpression(this)};"
        }
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
        return StringBuilder("for (; ")
            .append(op.whileExpression.generateExpression(this)).append("; ")
            .append(op.loopVar.generateAssignable(this)).append(" += ").append(op.incExpr.generateExpression(this))
            .appendLine(") {")
            .appendLine(generateScope(op.body, blockIndent))
            .append("}")
            .toString()
    }

    override fun opWhile(op: KslLoopWhile): String {
        return StringBuilder("while (${op.whileExpression.generateExpression(this)}) {\n")
            .appendLine(generateScope(op.body, blockIndent))
            .append("}")
            .toString()
    }

    override fun opDoWhile(op: KslLoopDoWhile): String {
        return StringBuilder("loop {\n")
            .appendLine(generateScope(op.body, blockIndent))
            .appendLine("${blockIndent}break if !(${op.whileExpression.generateExpression(this)})")
            .append("}")
            .toString()
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

    override fun KslState.name(): String = generatorState.getVarName(stateName)

    companion object {
        fun KslType.wgslTypeName(): String {
            return when (this) {
                KslTypeVoid -> TODO()
                KslBool1 -> "bool"
                KslBool2 -> "vec2b"
                KslBool3 -> "vec3b"
                KslBool4 -> "vec4b"
                KslFloat1 -> "f32"
                KslFloat2 -> "vec2f"
                KslFloat3 -> "vec3f"
                KslFloat4 -> "vec4f"
                KslInt1 -> "i32"
                KslInt2 -> "vec2i"
                KslInt3 -> "vec3i"
                KslInt4 -> "vec4i"
                KslUint1 -> "u32"
                KslUint2 -> "vec2u"
                KslUint3 -> "vec3u"
                KslUint4 -> "vec4u"
                KslMat2 -> "mat2x2f"
                KslMat3 -> "mat3x3f"
                KslMat4 -> "mat4x4f"

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

                is KslArrayType<*> -> "array<${elemType.wgslTypeName()},${arraySize}>"

                is KslStorage1dType<*> -> TODO()
                is KslStorage2dType<*> -> TODO()
                is KslStorage3dType<*> -> TODO()
            }
        }
    }

    private class GeneratorState(groupLayouts: BindGroupLayouts) {
        val locations = WgslLocations(groupLayouts)
        var nextTempI = 0

        val nameMap = mutableMapOf<String, String>()

        init {
            groupLayouts.asList.forEach { layout ->
                layout.bindings
                    .filterIsInstance<UniformBufferLayout>()
                    .forEach { ubo ->
                        val uboVarName = ubo.name.mapIndexed { i, c -> if (i == 0) c.lowercase() else c }.joinToString("")
                        ubo.uniforms.forEach {  nameMap[it.name] = "${uboVarName}.${it.name}" }
                    }
            }
        }

        fun mapStructMemberNames(members: List<WgslStructMember>) {
            members.forEach {  nameMap[it.name] = "${it.structName}.${it.name}" }
        }

        fun nextTempVar(): String = "generatorTempVar_${nextTempI++}"

        fun getVarName(kslName: String): String {
            return nameMap.getOrElse(kslName) { kslName }
        }
    }

    private data class UboStruct(val name: String, val typeName: String, val members: List<WgslStructMember>, val binding: UniformBufferLayout)

    private inner class VertexInputStructs(stage: KslVertexStage) : WgslStructHelper {
        val vertexInputs = stage.attributes.values
            .filter { it.inputRate == KslInputRate.Vertex }
            .mapIndexed { i, attr ->
                WgslStructMember("vertexInput", attr.name, attr.expressionType.wgslTypeName(), "@location($i) ")
            }
        val instanceInputs = stage.attributes.values
            .filter { it.inputRate == KslInputRate.Instance }
            .mapIndexed { i, attr ->
                WgslStructMember("instanceInput", attr.name, attr.expressionType.wgslTypeName(), "@location($i) ")
            }
        val vertexIndex = if (stage.isUsingVertexIndex) {
            WgslStructMember("vertexInput", KslVertexStage.NAME_IN_VERTEX_INDEX, "u32", "@builtin(vertex_index) ")
        } else null

        val instanceIndex = if (stage.isUsingInstanceIndex) {
            WgslStructMember("vertexInput", KslVertexStage.NAME_IN_INSTANCE_INDEX, "u32", "@builtin(instance_index) ")
        } else null

        init {
            generatorState.mapStructMemberNames(vertexInputs)
            generatorState.mapStructMemberNames(instanceInputs)
            vertexIndex?.let { generatorState.mapStructMemberNames(listOf(it)) }
            instanceIndex?.let { generatorState.mapStructMemberNames(listOf(it)) }
        }

        fun generateStructs(builder: StringBuilder) = builder.apply {
            generateStruct("VertexInput", vertexInputs, vertexIndex, instanceIndex)
            generateStruct("InstanceInput", instanceInputs)
        }
    }

    private inner class VertexOutputStruct(stage: KslVertexStage) : WgslStructHelper {
        val vertexOutputs = stage.interStageVars
            .mapIndexed { i, output ->
                val outVal = output.output
                val interp = if (output.interpolation == KslInterStageInterpolation.Flat) " @interpolate(flat)" else ""
                WgslStructMember("vertexOutput", outVal.stateName, outVal.expressionType.wgslTypeName(), "@location($i)$interp ")
            }
        val position = WgslStructMember("vertexOutput", KslVertexStage.NAME_OUT_POSITION, "vec4<f32>", "@builtin(position) ")
        val pointSize = if (stage.isSettingPointSize) {
            WgslStructMember("vertexOutput", KslVertexStage.NAME_OUT_POINT_SIZE, "f32", "@location(${stage.interStageVars.size}) ")
        } else null

        init {
            if (pointSize != null) {
                logW { "Ignoring vertex shader point size output: Not supported by WGSL" }
            }
            generatorState.mapStructMemberNames(vertexOutputs)
            generatorState.mapStructMemberNames(listOf(position))
            pointSize?.let { generatorState.mapStructMemberNames(listOf(it)) }
        }

        fun generateStruct(builder: StringBuilder) = builder.apply {
            generateStruct("VertexOutput", vertexOutputs, position, pointSize)
        }
    }

    private inner class FragmentInputStruct(stage: KslFragmentStage) : WgslStructHelper {
        val fragmentInputs = stage.interStageVars
            .mapIndexed { i, input ->
                val inVal = input.input
                val interp = if (input.interpolation == KslInterStageInterpolation.Flat) " @interpolate(flat)" else ""
                WgslStructMember("fragmentInput", inVal.stateName, inVal.expressionType.wgslTypeName(), "@location($i)$interp ")
            }
        val fragPosition = if (stage.isUsingFragPosition) {
            WgslStructMember("fragmentInput", KslFragmentStage.NAME_IN_FRAG_POSITION, " vec4<f32>", "@builtin(position) ")
        } else null

        val isFrontFacing = if (stage.isUsingIsFrontFacing) {
            WgslStructMember("fragmentInput", KslFragmentStage.NAME_IN_IS_FRONT_FACING, "bool", "@builtin(front_facing) ")
        } else null

        // not-implemented: input sample_index
        // not-implemented: input sample_mask

        init {
            generatorState.mapStructMemberNames(fragmentInputs)
            fragPosition?.let { generatorState.mapStructMemberNames(listOf(it)) }
            isFrontFacing?.let { generatorState.mapStructMemberNames(listOf(it)) }
        }

        fun generateStruct(builder: StringBuilder) = builder.apply {
            generateStruct("FragmentInput", fragmentInputs, fragPosition, isFrontFacing)
        }
    }

    private inner class FragmentOutputStruct(stage: KslFragmentStage) : WgslStructHelper {
        val outColors = stage.outColors.mapIndexed { i, outColor ->
            WgslStructMember("fragmentOutput", outColor.value.stateName, outColor.value.expressionType.wgslTypeName(), "@location($i) ")
        }
        val fragDepth = if (stage.isSettingFragDepth) {
            WgslStructMember("fragmentOutput", KslFragmentStage.NAME_OUT_DEPTH, "f32", "@builtin(frag_depth) ")
        } else null

        init {
            generatorState.mapStructMemberNames(outColors)
            fragDepth?.let { generatorState.mapStructMemberNames(listOf(it)) }
        }

        fun generateStruct(builder: StringBuilder) = builder.apply {
            generateStruct("FragmentOutput", outColors, fragDepth)
        }
    }

    private fun StringBuilder.generateTextureSamplers(stage: KslShaderStage, pipeline: PipelineBase) {
        pipeline.bindGroupLayouts.asList.forEach { layout ->
            layout.bindings
                .filterIsInstance<TextureLayout>().filter { texLayout ->
                    stage.getUsedSamplers().any { usedTex -> usedTex.name == texLayout.name }
                }
                .map { tex ->
                    val location = generatorState.locations[tex]
                    val texType = when (tex.type) {
                        BindingType.TEXTURE_1D -> "texture_1d<f32>"
                        BindingType.TEXTURE_2D -> "texture_2d<f32>"
                        BindingType.TEXTURE_3D -> "texture_3d<f32>"
                        BindingType.TEXTURE_CUBE -> "texture_cube<f32>"
                        else -> error("invalid texture/sampler type: ${tex.type}")
                    }
                    appendLine("@group(${location.group}) @binding(${location.binding}) var ${tex.name}_sampler: sampler;")
                    appendLine("@group(${location.group}) @binding(${location.binding+1}) var ${tex.name}: $texType;")
                }
        }
        appendLine()
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