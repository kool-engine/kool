package de.fabmax.kool.pipeline.backend.wgsl

import de.fabmax.kool.modules.ksl.generator.KslGenerator
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.modules.ksl.model.KslState
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.logW

class WgslGenerator : KslGenerator() {

    var blockIndent = "  "

    private var generatorState = GeneratorState(
        BindGroupLayouts(
            BindGroupLayout(-1, BindGroupScope.VIEW, emptyList()),
            BindGroupLayout(-1, BindGroupScope.PIPELINE, emptyList()),
            BindGroupLayout(-1, BindGroupScope.MESH, emptyList())
        ),
        null
    )

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

        generatorState = GeneratorState(pipeline.bindGroupLayouts, null)
        return WgslGeneratorOutput.computeOutput(generateComputeSrc(computeStage, pipeline))
    }

    private fun generateVertexSrc(vertexStage: KslVertexStage, pipeline: DrawPipeline): String {
        generatorState = GeneratorState(pipeline.bindGroupLayouts, pipeline.vertexLayout)

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
        vertexInput.generateStruct(src)
        vertexOutput.generateStruct(src)
        src.generateTextureSamplers(vertexStage, pipeline)
        src.generateStorageBuffers(vertexStage, pipeline)
        src.generateFunctions(vertexStage)

        src.appendLine("@vertex")
        src.appendLine("fn vertexMain(vertexInput: VertexInput) -> VertexOutput {")
        src.appendLine(vertexInput.reassembleMatrices().prependIndent(blockIndent))
        src.appendLine("  var vertexOutput: VertexOutput;")
        src.appendLine(generateScope(vertexStage.main, blockIndent))
        src.appendLine("  return vertexOutput;")
        src.appendLine("}")
        return src.toString()
    }

    private fun generateFragmentSrc(fragmentStage: KslFragmentStage, pipeline: DrawPipeline): String {
        generatorState = GeneratorState(pipeline.bindGroupLayouts, null)

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
        src.generateStorageBuffers(fragmentStage, pipeline)
        src.generateFunctions(fragmentStage)

        val mainParam = if (fragmentInput.isNotEmpty()) "fragmentInput: FragmentInput" else ""

        src.appendLine("@fragment")
        src.appendLine("fn fragmentMain($mainParam) -> FragmentOutput {")
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

        val computeInput = ComputeInputStructs(computeStage)
        val ubos = UboStructs(computeStage, pipeline)

        ubos.generateStructs(src)
        computeInput.generateStruct(src)
        src.generateTextureSamplers(computeStage, pipeline)
        src.generateStorageBuffers(computeStage, pipeline)
        src.generateFunctions(computeStage)

        src.appendLine("@compute")
        src.appendLine("@workgroup_size(${computeStage.workGroupSize.x}, ${computeStage.workGroupSize.y}, ${computeStage.workGroupSize.z})")
        src.appendLine("fn computeMain(computeInput: ComputeInput) {")
        src.appendLine(computeInput.addWorkGroupSizeDef().prependIndent(blockIndent))
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
        val textureName = sampleTexture.sampler.generateExpression(this)
        val level = sampleTexture.lod?.generateExpression(this)
        return if (level != null) {
            "textureSampleLevel(${textureName(textureName)}, ${samplerName(textureName)}, ${sampleTexture.coord.generateExpression(this)}, $level)"
        } else {
            "textureSample(${textureName(textureName)}, ${samplerName(textureName)}, ${sampleTexture.coord.generateExpression(this)})"
        }
    }

    override fun sampleDepthTexture(sampleTexture: KslSampleDepthTexture<*>): String {
        val textureName = sampleTexture.sampler.generateExpression(this)
        val coordExpr = sampleTexture.coord.generateExpression(this)
        val coordType = sampleTexture.coord.expressionType
        val coord: String
        val ref: String
        when (coordType) {
            is KslFloat3 -> {
                coord = "(${coordExpr}).xy"
                ref = "(${coordExpr}).z"
            }
            is KslFloat4 -> {
                coord = "(${coordExpr}).xyz"
                ref = "(${coordExpr}).w"
            }
            else -> error("Invalid depth sampler coordinate type: $coordType")
        }
        // use "Level" variant of textureSampleCompare, as out depth maps don't have mip levels -> therefore
        // no derivatives need to be computed and sampling can be invoked from non-uniform control flow
        return "textureSampleCompareLevel(${textureName(textureName)}, ${samplerName(textureName)}, $coord, $ref)"
    }

    override fun textureSize(textureSize: KslTextureSize<*, *>): String {
        val textureName = textureSize.sampler.generateExpression(this)
        val level = textureSize.lod.generateExpression(this)
        return "textureDimensions(${textureName}, $level)"
    }

    override fun texelFetch(expression: KslTexelFetch<*>): String {
        val textureName = expression.sampler.generateExpression(this)
        val coord = expression.coord.generateExpression(this)
        val level = expression.lod?.generateExpression(this) ?: "0"
        return "textureLoad($textureName, $coord, $level)"
    }

    private fun KslStorage<*,*>.getIndexString(coordExpr: String) = when (this) {
        // always use a 1d array and compute array index dynamically based on buffer size
        is KslStorage1d<*> -> "[${coordExpr}]"
        is KslStorage2d<*> -> "[${coordExpr}.y * $sizeX + ${coordExpr}.x]"
        is KslStorage3d<*> -> "[${coordExpr}.z * ${sizeY * sizeX} + ${coordExpr}.y * $sizeX + ${coordExpr}.x]"
    }

    override fun storageRead(storageRead: KslStorageRead<*, *, *>): String {
        val storage = storageRead.storage.generateExpression(this)
        val coord = storageRead.coord.generateExpression(this)
        val arrayIndex = storageRead.storage.getIndexString(coord)
        return if (storageRead.storage.isAccessedAtomically) {
            "atomicLoad(&${storage}${arrayIndex})"
        } else {
            "${storage}${arrayIndex}"
        }
    }

    override fun opStorageWrite(op: KslStorageWrite<*, *, *>): String {
        val storage = op.storage.generateExpression(this)
        val expr = op.data.generateExpression(this)
        val coord = op.coord.generateExpression(this)
        val arrayIndex = op.storage.getIndexString(coord)
        return if (op.storage.isAccessedAtomically) {
            "atomicStore(&${storage}${arrayIndex}, $expr)"
        } else {
            "${storage}${arrayIndex} = $expr;"
        }
    }

    override fun storageAtomicOp(atomicOp: KslStorageAtomicOp<*, *, *>): String {
        val storage = atomicOp.storage.generateExpression(this)
        val expr = atomicOp.data.generateExpression(this)
        val coord = atomicOp.coord.generateExpression(this)
        val arrayIndex = atomicOp.storage.getIndexString(coord)
        val func = when(atomicOp.op) {
            KslStorageAtomicOp.Op.Swap -> "atomicExchange"
            KslStorageAtomicOp.Op.Add -> "atomicAdd"
            KslStorageAtomicOp.Op.And -> "atomicAnd"
            KslStorageAtomicOp.Op.Or -> "atomicOr"
            KslStorageAtomicOp.Op.Xor -> "atomicXor"
            KslStorageAtomicOp.Op.Min -> "atomicMin"
            KslStorageAtomicOp.Op.Max -> "atomicMax"
        }
        return "$func(&${storage}${arrayIndex}, ${expr})"
    }

    override fun storageAtomicCompareSwap(atomicCompSwap: KslStorageAtomicCompareSwap<*, *, *>): String {
        val storage = atomicCompSwap.storage.generateExpression(this)
        val comp = atomicCompSwap.compare.generateExpression(this)
        val expr = atomicCompSwap.data.generateExpression(this)
        val coord = atomicCompSwap.coord.generateExpression(this)
        val arrayIndex = atomicCompSwap.storage.getIndexString(coord)
        return "atomicCompareExchangeWeak(&$storage${arrayIndex}, ${comp}, ${expr}).old_value"
    }

    private fun StringBuilder.generateFunctions(stage: KslShaderStage) {
        if (stage.functions.isNotEmpty()) {
            val funcList = stage.functions.values.toMutableList()
            sortFunctions(funcList)
            funcList.forEach { func ->
                val returnType = if (func.returnType == KslTypeVoid) "" else " -> ${func.returnType.wgslTypeName()}"
                val params = func.parameters.joinToString { p ->
                    if (p.expressionType is KslSamplerType<*>) {
                        val (samplerType, texType) = p.expressionType.wgslSamplerAndTextureTypeName()
                        "${samplerName(p.name())}: $samplerType, ${textureName(p.name())}: $texType"
                    } else {
                        "${p.name()}: ${p.expressionType.wgslTypeName()}"
                    }
                }
                appendLine("fn ${func.name}($params)$returnType {")
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
            assignSwizzled(op.assignTarget, op.assignExpression) { _, b -> b }
        } else {
            "${op.assignTarget.generateAssignable(this)} = ${op.assignExpression.generateExpression(this)};"
        }
    }

    override fun opAugmentedAssign(op: KslAugmentedAssign<*>): String {
        return if (op.assignTarget is KslVectorAccessor<*> && op.assignTarget.components.length > 1) {
            val opChar = op.augmentationMode.opChar
            assignSwizzled(op.assignTarget, op.assignExpression) { a, b -> "$a $opChar $b" }
        } else {
            "${op.assignTarget.generateAssignable(this)} ${op.augmentationMode.opChar}= ${op.assignExpression.generateExpression(this)};"
        }
    }

    private fun assignSwizzled(
        assignTarget: KslVectorAccessor<*>,
        assignExpression: KslExpression<*>,
        makeArg: (String, String) -> String
    ): String {
        // wgsl currently does not permit assignment of swizzled values
        // https://github.com/gpuweb/gpuweb/issues/737

        val vec = assignTarget.vector as KslVectorExpression<*,*>
        val assignType = vec.expressionType.wgslTypeName()
        val targetComps = assignTarget.components
        val assignDimens = (vec.expressionType as KslVector<*>).dimens

        val compsXyzw = listOf('x', 'y', 'z', 'w')
        val compsRgba = listOf('r', 'g', 'b', 'a')

        val target = assignTarget.vector.generateExpression(this)
        val tmpVarName = generatorState.nextTempVar()
        val ctorArgs = buildString {
            for (i in 0 until assignDimens) {
                val c1 = targetComps.indexOf(compsXyzw[i])
                val c2 = targetComps.indexOf(compsRgba[i])
                val src = when {
                    c1 >= 0 -> makeArg("${target}.${compsXyzw[i]}", "${tmpVarName}.${compsXyzw[c1]}")
                    c2 >= 0 -> makeArg("${target}.${compsXyzw[i]}", "${tmpVarName}.${compsXyzw[c2]}")
                    else -> "${target}.${compsXyzw[i]}"
                }
                append(src)
                if (i < assignDimens-1) {
                    append(", ")
                }
            }
        }
        return """
            let $tmpVarName = ${assignExpression.generateExpression(this)};
            $target = $assignType($ctorArgs);
        """.trimIndent()
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

    override fun invokeFunction(func: KslInvokeFunction<*>): String {
        val args = func.args.joinToString {
            val expr = it.generateExpression(this)
            if (it.expressionType is KslSamplerType<*>) {
                "${samplerName(expr)}, ${textureName(expr)}"
            } else {
                expr
            }
        }
        return "${func.function.name}($args)"
    }

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

    override fun builtinIsInf(func: KslBuiltinIsInfScalar): String {
        logW { "WGSL has no builtin isInfinity check, using fallback method, which might not work as expected: https://github.com/gpuweb/gpuweb/issues/2270" }
        // In floating-point arithmetic Infinity * 0.0 is NaN
        return "(${func.args[0].generateExpression(this)} * 0.0 != 0.0)"
    }
    override fun builtinIsInf(func: KslBuiltinIsInfVector<*, *>): String {
        logW { "WGSL has no builtin isInfinity check, using fallback method, which might not work as expected: https://github.com/gpuweb/gpuweb/issues/2270" }
        // In floating-point arithmetic Infinity * 0.0 is NaN
        return "(${func.args[0].generateExpression(this)} * 0.0 != ${func.args[0].generateExpression(this)} * 0.0)"
    }
    override fun builtinIsNan(func: KslBuiltinIsNanScalar): String {
        logW { "WGSL has no builtin isNaN check, using fallback method, which might not work as expected: https://github.com/gpuweb/gpuweb/issues/2270" }
        // In floating-point arithmetic: NaN != NaN
        return "(${func.args[0].generateExpression(this)} != ${func.args[0].generateExpression(this)})"
    }
    override fun builtinIsNan(func: KslBuiltinIsNanVector<*, *>): String {
        logW { "WGSL has no builtin isNaN check, using fallback method, which might not work as expected: https://github.com/gpuweb/gpuweb/issues/2270" }
        // In floating-point arithmetic: NaN != NaN
        return "(${func.args[0].generateExpression(this)} != ${func.args[0].generateExpression(this)})"
    }

    override fun KslState.name(): String = generatorState.getVarName(stateName)

    companion object {
        fun samplerName(samplerExpression: String): String {
            return "${samplerExpression}_sampler"
        }

        fun textureName(samplerExpression: String): String {
            return samplerExpression
        }

        fun KslType.wgslSamplerAndTextureTypeName(): Pair<String, String> {
            return when (this) {
                KslColorSampler1d -> "sampler" to "texture_1d<f32>"
                KslColorSampler2d -> "sampler" to "texture_2d<f32>"
                KslColorSampler3d -> "sampler" to "texture_3d<f32>"
                KslColorSamplerCube -> "sampler" to "texture_cube<f32>"
                KslColorSampler2dArray -> "sampler" to "texture_2d_array<f32>"
                KslColorSamplerCubeArray -> "sampler" to "texture_cube_array<f32>"

                KslDepthSampler2d -> "sampler_comparison" to "texture_depth_2d"
                KslDepthSamplerCube -> "sampler_comparison" to "texture_depth_cube"
                KslDepthSampler2dArray -> "sampler_comparison" to "texture_depth_2d_array"
                KslDepthSamplerCubeArray -> "sampler_comparison" to "texture_depth_cube_array"

                else -> error("$this is not a sampler type")
            }
        }

        fun KslType.wgslTypeName(): String {
            return when (this) {
                KslTypeVoid -> error("there is no explicit void type in WGSL")

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

                is KslArrayType<*> -> "array<${elemType.wgslTypeName()},${arraySize}>"

                else -> error("no direct type mapping for type $this")
            }
        }

        fun StorageAccessType.wgslAccessType(): String {
            return when (this) {
                StorageAccessType.READ_ONLY -> "read"
                StorageAccessType.WRITE_ONLY -> "read_write"        // wgsl storage access type must be either "read" or "read_write"
                StorageAccessType.READ_WRITE -> "read_write"
            }
        }
    }

    private class GeneratorState(groupLayouts: BindGroupLayouts, vertexLayout: VertexLayout?) {
        val locations = WgslLocations(groupLayouts, vertexLayout)
        var nextTempI = 0

        val nameMap = mutableMapOf<String, String>()

        init {
            groupLayouts.asList.forEach { layout ->
                layout.bindings
                    .filterIsInstance<UniformBufferLayout>()
                    .forEach { ubo ->
                        val uboVarName = ubo.name.mapIndexed { i, c -> if (i == 0) c.lowercase() else c }.joinToString("")
                        ubo.uniforms.forEach { nameMap[it.name] = "${uboVarName}.${it.name}" }
                    }
            }
        }

        fun mapStructMemberNames(members: List<WgslStructMember>) {
            members.forEach { nameMap[it.name] = "${it.structName}.${it.name}" }
        }

        fun nextTempVar(): String = "generatorTempVar_${nextTempI++}"

        fun getVarName(kslName: String): String {
            return nameMap.getOrElse(kslName) { kslName }
        }
    }

    private data class UboStruct(val name: String, val typeName: String, val members: List<WgslStructMember>, val binding: UniformBufferLayout)

    private inner class VertexInputStructs(val stage: KslVertexStage) : WgslStructHelper {
        val vertexInputs = buildList {
            stage.attributes.values.forEach { attr ->
                val locs = generatorState.locations[attr]
                if (locs.size == 1) {
                    add(WgslStructMember("vertexInput", attr.name, attr.expressionType.wgslTypeName(), "@location(${locs[0].location}) "))
                } else {
                    val colType = when (attr.expressionType) {
                        is KslMat2 -> KslFloat2.wgslTypeName()
                        is KslMat3 -> KslFloat3.wgslTypeName()
                        is KslMat4 -> KslFloat4.wgslTypeName()
                        else -> error(attr.expressionType)
                    }
                    locs.forEach {
                        add(WgslStructMember("vertexInput", it.name, colType, "@location(${it.location}) "))
                    }
                }
            }
        }.sortedBy { it.annotation }

        val vertexIndex = if (stage.isUsingVertexIndex) {
            WgslStructMember("vertexInput", KslVertexStage.NAME_IN_VERTEX_INDEX, "u32", "@builtin(vertex_index) ")
        } else null

        val instanceIndex = if (stage.isUsingInstanceIndex) {
            WgslStructMember("vertexInput", KslVertexStage.NAME_IN_INSTANCE_INDEX, "u32", "@builtin(instance_index) ")
        } else null

        init {
            generatorState.mapStructMemberNames(vertexInputs)
            vertexIndex?.let { generatorState.mapStructMemberNames(listOf(it)) }
            instanceIndex?.let { generatorState.mapStructMemberNames(listOf(it)) }
        }

        fun generateStruct(builder: StringBuilder) = builder.apply {
            generateStruct("VertexInput", vertexInputs, vertexIndex, instanceIndex)
        }

        fun reassembleMatrices(): String = buildString {
            stage.attributes.values
                .filter { generatorState.locations[it].size > 1 }
                .forEach { matrixAttr ->
                    val type = matrixAttr.expressionType.wgslTypeName()
                    val members = generatorState.locations[matrixAttr].joinToString { "vertexInput.${it.name}" }
                    appendLine("let ${matrixAttr.name} = $type($members);")
                }
        }
    }

    private inner class VertexOutputStruct(stage: KslVertexStage) : WgslStructHelper {
        val vertexOutputs = stage.interStageVars
            .mapIndexed { i, output ->
                val outVal = output.output
                val interp = if (output.interpolation == KslInterStageInterpolation.Flat) " @interpolate(flat)" else ""
                WgslStructMember("vertexOutput", outVal.stateName, outVal.expressionType.wgslTypeName(), "@location($i)$interp ")
            }
        val position = WgslStructMember("vertexOutput", KslVertexStage.NAME_OUT_POSITION, "vec4f", "@builtin(position) ")
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
            WgslStructMember("fragmentInput", KslFragmentStage.NAME_IN_FRAG_POSITION, " vec4f", "@builtin(position) ")
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

        fun isNotEmpty(): Boolean = isNotEmpty(fragmentInputs, fragPosition, isFrontFacing)

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

    private inner class ComputeInputStructs(val stage: KslComputeStage) : WgslStructHelper {
        val globalInvocationId = if (stage.isUsingGlobalInvocationId) {
            WgslStructMember("computeInput", KslComputeStage.NAME_IN_GLOBAL_INVOCATION_ID, "vec3u", "@builtin(global_invocation_id) ")
        } else null

        val localInvocationId = if (stage.isUsingLocalInvocationId) {
            WgslStructMember("computeInput", KslComputeStage.NAME_IN_LOCAL_INVOCATION_ID, "vec3u", "@builtin(local_invocation_id) ")
        } else null

        val workGroupId = if (stage.isUsingWorkGroupId) {
            WgslStructMember("computeInput", KslComputeStage.NAME_IN_WORK_GROUP_ID, "vec3u", "@builtin(workgroup_id) ")
        } else null

        val numWorkGroups = if (stage.isUsingNumWorkGroups) {
            WgslStructMember("computeInput", KslComputeStage.NAME_IN_NUM_WORK_GROUPS, "vec3u", "@builtin(num_workgroups) ")
        } else null

        init {
            globalInvocationId?.let { generatorState.mapStructMemberNames(listOf(it)) }
            localInvocationId?.let { generatorState.mapStructMemberNames(listOf(it)) }
            workGroupId?.let { generatorState.mapStructMemberNames(listOf(it)) }
            numWorkGroups?.let { generatorState.mapStructMemberNames(listOf(it)) }
        }

        fun generateStruct(builder: StringBuilder) = builder.apply {
            generateStruct("ComputeInput", emptyList(), globalInvocationId, localInvocationId, workGroupId, numWorkGroups)
        }

        fun addWorkGroupSizeDef(): String = buildString {
            if (stage.isUsingWorkGroupSize) {
                appendLine("let ${KslComputeStage.NAME_IN_WORK_GROUP_SIZE} = vec3u(${stage.workGroupSize.x}, ${stage.workGroupSize.y}, ${stage.workGroupSize.z});")
            }
        }
    }

    private fun StringBuilder.generateTextureSamplers(stage: KslShaderStage, pipeline: PipelineBase) {
        pipeline.bindGroupLayouts.asList.forEach { layout ->
            layout.bindings
                .filterIsInstance<TextureLayout>().filter { texLayout ->
                    stage.type.pipelineStageType in texLayout.stages
                }
                .map { tex ->
                    val location = generatorState.locations[tex]
                    val kslTex = stage.getUsedSamplers().first { it.name == tex.name }
                    val (samplerType, texType) = kslTex.expressionType.wgslSamplerAndTextureTypeName()
                    appendLine("@group(${location.group}) @binding(${location.binding}) var ${samplerName(tex.name)}: $samplerType;")
                    appendLine("@group(${location.group}) @binding(${location.binding+1}) var ${textureName(tex.name)}: $texType;")
                }
        }
        appendLine()
    }

    private fun StringBuilder.generateStorageBuffers(stage: KslShaderStage, pipeline: PipelineBase) {
        pipeline.bindGroupLayouts.asList.forEach { layout ->
            layout.bindings
                .filterIsInstance<StorageBufferLayout>().filter { storageLayout ->
                    stage.type.pipelineStageType in storageLayout.stages
                }
                .forEach { storageLayout ->
                    val location = generatorState.locations[storageLayout]
                    val kslStorage = stage.getUsedStorage().first { it.name == storageLayout.name }
                    val accessType = storageLayout.accessType.wgslAccessType()
                    val typeName = kslStorage.expressionType.elemType.wgslTypeName()
                    val elementType = if (kslStorage.isAccessedAtomically) "atomic<$typeName>" else typeName
                    appendLine("@group(${location.group}) @binding(${location.binding}) var <storage, ${accessType}> ${storageLayout.name}: array<$elementType>;")
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