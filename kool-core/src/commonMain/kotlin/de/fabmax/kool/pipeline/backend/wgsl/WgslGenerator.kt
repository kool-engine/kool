package de.fabmax.kool.pipeline.backend.wgsl

import de.fabmax.kool.modules.ksl.generator.KslGenerator
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.modules.ksl.model.KslState
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.StructArrayMember
import de.fabmax.kool.util.logW

class WgslGenerator private constructor(
    generatorExpressions: Map<KslExpression<*>, KslExpression<*>>
) : KslGenerator(generatorExpressions) {
    private var blockIndent = "    "

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
        ).also {
            if (program.dumpCode) {
                it.dump()
            }
        }
    }

    override fun generateComputeProgram(program: KslProgram, pipeline: ComputePipeline): WgslGeneratorOutput {
        val computeStage = checkNotNull(program.computeStage) {
            "KslProgram computeStage is missing"
        }

        generatorState = GeneratorState(pipeline.bindGroupLayouts, null)
        return WgslGeneratorOutput.computeOutput(generateComputeSrc(computeStage, pipeline)).also {
            if (program.dumpCode) {
                it.dump()
            }
        }
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

        src.generateGlobals(vertexStage, pipeline)
        val vertexInput = VertexInputStructs(vertexStage)
        val vertexOutput = VertexOutputStruct(vertexStage)
        vertexInput.generateStruct(src)
        vertexOutput.generateStruct(src)

        val main = vertexStage.globalScope.ops.find {
            it is KslFunction<*>.FunctionRoot && it.function.name == "main"
        } as KslFunction<*>.FunctionRoot
        src.appendLine(generateOps(vertexStage.globalScope.ops.filter { it != main }, ""))

        src.appendLine("@vertex")
        src.appendLine("fn vertexMain(vertexInput: VertexInput) -> VertexOutput {")
        src.appendLine(vertexInput.reassembleMatrices().prependIndent(blockIndent))
        src.appendLine("  var vertexOutput: VertexOutput;")
        src.appendLine(generateScope(main.childScopes.first(), blockIndent))
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

        src.generateGlobals(fragmentStage, pipeline)
        val fragmentInput = FragmentInputStruct(fragmentStage)
        val fragmentOutput = FragmentOutputStruct(fragmentStage)
        fragmentInput.generateStruct(src)
        fragmentOutput.generateStruct(src)

        val mainParam = if (fragmentInput.isNotEmpty()) "fragmentInput: FragmentInput" else ""
        val main = fragmentStage.globalScope.ops.find {
            it is KslFunction<*>.FunctionRoot && it.function.name == "main"
        } as KslFunction<*>.FunctionRoot
        src.appendLine(generateOps(fragmentStage.globalScope.ops.filter { it != main }, ""))

        src.appendLine("@fragment")
        src.appendLine("fn fragmentMain($mainParam) -> FragmentOutput {")
        src.appendLine("  var fragmentOutput: FragmentOutput;")
        src.appendLine(generateScope(main.childScopes.first(), blockIndent))
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

        src.generateGlobals(computeStage, pipeline)
        val computeInput = ComputeInputStructs(computeStage)
        computeInput.generateStruct(src)

        val main = computeStage.globalScope.ops.find {
            it is KslFunction<*>.FunctionRoot && it.function.name == "main"
        } as KslFunction<*>.FunctionRoot
        src.appendLine(generateOps(computeStage.globalScope.ops.filter { it != main }, ""))

        src.appendLine("@compute")
        src.appendLine("@workgroup_size(${computeStage.workGroupSize.x}, ${computeStage.workGroupSize.y}, ${computeStage.workGroupSize.z})")
        src.appendLine("fn computeMain(computeInput: ComputeInput) {")
        src.appendLine(computeInput.addWorkGroupSizeDef().prependIndent(blockIndent))
        src.appendLine(generateScope(main.childScopes.first(), blockIndent))
        src.appendLine("}")
        return src.toString()
    }

    private fun StringBuilder.generateGlobals(stage: KslShaderStage, pipeline: PipelineBase) {
        val ubos = UboStructs(stage, pipeline)
        generateStructs(stage, pipeline)
        generateStructUbos(stage, pipeline)
        ubos.generateStructs(this)
        generateTextureSamplers(stage, pipeline)
        generateStorageBuffers(stage, pipeline)
        generateStorageTextures(stage, pipeline)
        generateGlobalVars(stage)
    }

    private inner class UboStructs(stage: KslShaderStage, pipeline: PipelineBase) : WgslStructHelper {
        val structs: List<UboStruct> = buildList {
            pipeline.bindGroupLayouts.asList.forEach { layout ->
                layout.bindings
                    .filterIsInstance<UniformBufferLayout<*>>().filter { layoutUbo ->
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
        return "vec${values.size}<$type>(${values.joinToString { it.generateExpression() }})"
    }

    override fun constMatExpression(vararg columns: KslVectorExpression<*, KslFloat1>): String {
        val d = columns.size
        check (d in 2..4) { "invalid mat dimension: ${d}x$d (must be between 2 and 4)" }
        return "mat${d}x${d}<f32>(${columns.joinToString { it.generateExpression() }})"
    }

    override fun generateCastExpression(castExpr: KslExpressionCast<*>): String {
        return "${castExpr.expressionType.wgslTypeName()}(${castExpr.value.generateExpression()})"
    }

    override fun generateBitcastExpression(expr: KslExpressionBitcast<*>): String {
        val dstIsUint = "u" in expr.expressionType.wgslTypeName()
        val dstIsInt = "i" in expr.expressionType.wgslTypeName()
        val dstIsFloat = "f" in expr.expressionType.wgslTypeName()

        val type = expr.expressionType
        if (type is KslVector<*>) {
            val dims = type.dimens
            return when {
                dstIsInt -> "bitcast<vec$dims<i32>>(${expr.value.generateExpression()})"
                dstIsUint -> "bitcast<vec$dims<u32>>(${expr.value.generateExpression()})"
                dstIsFloat -> "bitcast<vec$dims<f32>>(${expr.value.generateExpression()})"
                else -> error("no bitcast conversion possible (${expr.value.expressionType} -> ${expr.expressionType})")
            }
        } else {
            return when {
                dstIsInt -> "bitcast<i32>(${expr.value.generateExpression()})"
                dstIsUint -> "bitcast<u32>(${expr.value.generateExpression()})"
                dstIsFloat -> "bitcast<f32>(${expr.value.generateExpression()})"
                else -> error("no bitcast conversion possible (${expr.value.expressionType} -> ${expr.expressionType})")
            }
        }
    }

    override fun <T: KslNumericType> generateBitExpression(expression: KslExpressionBit<T>): String {
        val isLeftVec = expression.left.expressionType is KslVector<*>
        val isRightVec = expression.right.expressionType is KslVector<*>

        var rightExpr = expression.right.generateExpression()
        if (isLeftVec && !isRightVec) {
            val dims = (expression.left.expressionType as KslVector<*>).dimens
            rightExpr = "vec$dims<${expression.right.expressionType.wgslTypeName()}>($rightExpr)"
        }
        return "(${expression.left.generateExpression()} ${expression.operator.opString} $rightExpr)"
    }

    override fun <B: KslBoolType> compareExpression(expression: KslExpressionCompare<B>): String {
        val lt = expression.left.generateExpression()
        val rt = expression.right.generateExpression()
        return "($lt ${expression.operator.opString} $rt)"
    }

    override fun sampleColorTexture(sampleTexture: KslSampleColorTexture<*>): String {
        val textureName = sampleTexture.sampler.generateExpression()
        val level = sampleTexture.lod?.generateExpression()
        val coord = sampleTexture.coord.generateExpression()

        return if (level != null) {
            "textureSampleLevel(${textureName(textureName)}, ${samplerName(textureName)}, $coord, $level)"
        } else {
            "textureSample(${textureName(textureName)}, ${samplerName(textureName)}, $coord)"
        }
    }

    override fun sampleColorTextureGrad(sampleTextureGrad: KslSampleColorTextureGrad<*>): String {
        val textureName = sampleTextureGrad.sampler.generateExpression()
        val coord = sampleTextureGrad.coord.generateExpression()
        val ddx = sampleTextureGrad.ddx.generateExpression()
        val ddy = sampleTextureGrad.ddy.generateExpression()
        return "textureSampleGrad(${textureName(textureName)}, ${samplerName(textureName)}, $coord, $ddx, $ddy)"
    }

    override fun sampleDepthTexture(sampleTexture: KslSampleDepthTexture<*>): String {
        val textureName = sampleTexture.sampler.generateExpression()
        val coord = sampleTexture.coord.generateExpression()
        val depthRef = sampleTexture.depthRef.generateExpression()
        // use "Level" variant of textureSampleCompare, as our depth maps don't have mip levels -> therefore
        // no derivatives need to be computed and sampling can be invoked from non-uniform control flow
        return "textureSampleCompareLevel(${textureName(textureName)}, ${samplerName(textureName)}, $coord, $depthRef)"
    }

    override fun sampleColorTextureArray(sampleTexture: KslSampleColorTextureArray<*>): String {
        val textureName = sampleTexture.sampler.generateExpression()
        val coord = sampleTexture.coord.generateExpression()
        val index = sampleTexture.arrayIndex.generateExpression()
        val level = sampleTexture.lod?.generateExpression()
        return if (level != null) {
            "textureSampleLevel(${textureName(textureName)}, ${samplerName(textureName)}, ${coord}, $index, $level)"
        } else {
            "textureSample(${textureName(textureName)}, ${samplerName(textureName)}, ${coord}, $index)"
        }
    }

    override fun sampleColorTextureArrayGrad(sampleTextureGrad: KslSampleColorTextureArrayGrad<*>): String {
        val textureName = sampleTextureGrad.sampler.generateExpression()
        val coord = sampleTextureGrad.coord.generateExpression()
        val index = sampleTextureGrad.arrayIndex.generateExpression()
        val ddx = sampleTextureGrad.ddx.generateExpression()
        val ddy = sampleTextureGrad.ddy.generateExpression()
        return "textureSampleGrad(${textureName(textureName)}, ${samplerName(textureName)}, $coord, $index, $ddx, $ddy)"
    }

    override fun sampleDepthTextureArray(sampleTexture: KslSampleDepthTextureArray<*>): String {
        val textureName = sampleTexture.sampler.generateExpression()
        val coord = sampleTexture.coord.generateExpression()
        val index = sampleTexture.arrayIndex.generateExpression()
        val depthRef = sampleTexture.depthRef.generateExpression()
        return "textureSampleCompareLevel(${textureName(textureName)}, ${samplerName(textureName)}, $coord, $index, $depthRef)"
    }

    override fun generateTextureSize(textureSize: KslTextureSize<*, *>): String {
        val textureName = textureSize.sampler.generateExpression()
        val level = textureSize.lod.generateExpression()
        return "vec2i(textureDimensions(${textureName}, $level))"
    }

    override fun generateTextureSize(textureSize: KslStorageTextureSize<*, *, *>): String {
        return "vec2i(textureDimensions(${textureSize.storageTex.generateExpression()}))"
    }

    override fun imageTextureRead(expression: KslImageTextureLoad<*>): String {
        val textureName = expression.sampler.generateExpression()
        val coord = expression.coord.generateExpression()
        val level = expression.lod?.generateExpression() ?: "0"
        return "textureLoad($textureName, $coord, $level)"
    }

    override fun generateStorageRead(storageRead: KslStorageRead<*, *>): String {
        val storage = storageRead.storage.generateExpression()
        val index = storageRead.index.generateExpression()
        return if (storageRead.storage.isAccessedAtomically) {
            "atomicLoad(&${storage}[${index}])"
        } else {
            "${storage}[${index}]"
        }
    }

    override fun opStorageWrite(op: KslStorageWrite<*, *>): String {
        val storage = op.storage.generateExpression()
        val expr = op.data.generateExpression()
        val index = op.index.generateExpression()
        return if (op.storage.isAccessedAtomically) {
            "atomicStore(&${storage}[${index}], $expr);"
        } else {
            "${storage}[${index}] = $expr;"
        }
    }

    override fun opStorageTextureWrite(op: KslStorageTextureStore<*, *, *>): String {
        val storage = op.storage.generateExpression()
        val expr = op.data.generateExpression()
        val coord = op.coord.generateExpression()
        return "textureStore(${storage}, $coord, $expr);"
    }

    override fun storageAtomicOp(atomicOp: KslStorageAtomicOp<*, *>): String {
        val storage = atomicOp.storage.generateExpression()
        val expr = atomicOp.data.generateExpression()
        val index = atomicOp.index.generateExpression()
        val func = when(atomicOp.op) {
            KslStorageAtomicOp.Op.Swap -> "atomicExchange"
            KslStorageAtomicOp.Op.Add -> "atomicAdd"
            KslStorageAtomicOp.Op.And -> "atomicAnd"
            KslStorageAtomicOp.Op.Or -> "atomicOr"
            KslStorageAtomicOp.Op.Xor -> "atomicXor"
            KslStorageAtomicOp.Op.Min -> "atomicMin"
            KslStorageAtomicOp.Op.Max -> "atomicMax"
        }
        return "$func(&${storage}[${index}], ${expr})"
    }

    override fun storageAtomicCompareSwap(atomicCompSwap: KslStorageAtomicCompareSwap<*, *>): String {
        val storage = atomicCompSwap.storage.generateExpression()
        val comp = atomicCompSwap.compare.generateExpression()
        val expr = atomicCompSwap.data.generateExpression()
        val index = atomicCompSwap.index.generateExpression()
        return "atomicCompareExchangeWeak(&$storage[${index}], ${comp}, ${expr}).old_value"
    }

    override fun storageTextureRead(storageTextureRead: KslStorageTextureLoad<*, *, *>): String {
        val storage = storageTextureRead.storage.generateExpression()
        val coord = storageTextureRead.coord.generateExpression()
        return "textureLoad($storage, $coord)"
    }

    override fun opDeclareVar(op: KslDeclareVar): String {
        val state = op.declareVar
        val initExpr = op.initExpression?.generateExpression() ?: "${state.expressionType.wgslTypeName()}()"
        return "var ${getStateName(state)} = ${initExpr};"
    }

    override fun opDeclareArray(op: KslDeclareArray): String {
        val array = op.declareVar
        val typeName = array.expressionType.wgslTypeName()

        return if (op.elements.size == 1 && op.elements[0].expressionType == array.expressionType) {
            "var ${getStateName(array)} = ${op.elements[0].generateExpression()};"
        } else {
            val initExpr = op.elements.joinToString { it.generateExpression() }
            "var ${getStateName(array)} = ${typeName}(${initExpr});"
        }
    }

    override fun opAssign(op: KslAssign<*>): String {
        return if (op.assignTarget is KslVectorAccessor<*> && op.assignTarget.components.length > 1) {
            assignSwizzled(op.assignTarget, op.assignExpression) { _, b -> b }
        } else {
            "${op.assignTarget.generateAssignable(this)} = ${op.assignExpression.generateExpression()};"
        }
    }

    override fun opAugmentedAssign(op: KslAugmentedAssign<*>): String {
        return if (op.assignTarget is KslVectorAccessor<*> && op.assignTarget.components.length > 1) {
            val opChar = op.augmentationMode.opChar
            assignSwizzled(op.assignTarget, op.assignExpression) { a, b -> "$a $opChar $b" }
        } else {
            "${op.assignTarget.generateAssignable(this)} ${op.augmentationMode.opChar}= ${op.assignExpression.generateExpression()};"
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

        val target = assignTarget.vector.generateExpression()
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
            let $tmpVarName = ${assignExpression.generateExpression()};
            $target = $assignType($ctorArgs);
        """.trimIndent()
    }

    override fun opFunctionBody(op: KslFunction<*>.FunctionRoot): String {
        val func = op.function
        return buildString {
            val returnType = if (func.returnType == KslTypeVoid) "" else " -> ${func.returnType.wgslTypeName()}"
            val params = func.parameters.joinToString { p ->
                if (p.expressionType is KslSamplerType<*>) {
                    val (samplerType, texType) = p.expressionType.wgslSamplerAndTextureTypeName()
                    "${samplerName(getStateName(p))}: $samplerType, ${textureName(getStateName(p))}: $texType"
                } else {
                    "${getStateName(p)}: ${p.expressionType.wgslTypeName()}"
                }
            }
            appendLine("fn ${func.name}($params)$returnType {")
            appendLine(generateScope(op.childScopes.first(), blockIndent))
            appendLine("}")
            appendLine()
        }
    }

    override fun opIf(op: KslIf): String {
        val txt = StringBuilder("if ${op.condition.generateExpression()} {\n")
        txt.appendLine(generateScope(op.body, blockIndent))
        txt.append("}")
        op.elseIfs.forEach { elseIf ->
            txt.appendLine(" else if ${elseIf.first.generateExpression()} {")
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
            .append(op.whileExpression.generateExpression()).append("; ")
            .append(op.loopVar.generateAssignable(this)).append(" += ").append(op.incExpr.generateExpression())
            .appendLine(") {")
            .appendLine(generateScope(op.body, blockIndent))
            .append("}")
            .toString()
    }

    override fun opWhile(op: KslLoopWhile): String {
        return StringBuilder("while (${op.whileExpression.generateExpression()}) {\n")
            .appendLine(generateScope(op.body, blockIndent))
            .append("}")
            .toString()
    }

    override fun opDoWhile(op: KslLoopDoWhile): String {
        return StringBuilder("loop {\n")
            .appendLine(generateScope(op.body, blockIndent))
            .appendLine("${blockIndent}break if !(${op.whileExpression.generateExpression()})")
            .append("}")
            .toString()
    }

    override fun opBreak(op: KslLoopBreak) = "break;"

    override fun opContinue(op: KslLoopContinue) = "continue;"

    override fun opDiscard(op: KslDiscard): String = "discard;"

    override fun opReturn(op: KslReturn): String {
        return if (op.returnValue.expressionType == KslTypeVoid) {
            "return;"
        } else {
            "return ${op.returnValue.generateExpression()};"
        }
    }

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
        return args.joinToString { it.generateExpression() }
    }

    override fun generateInvokeFunction(func: KslInvokeFunction<*>): String {
        val args = func.args.joinToString {
            val expr = it.generateExpression()
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
    override fun builtinDpdx(func: KslBuiltinDpdxScalar) = "dpdx(${generateArgs(func.args, 1)})"
    override fun builtinDpdx(func: KslBuiltinDpdxVector<*>) = "dpdx(${generateArgs(func.args, 1)})"
    override fun builtinDpdy(func: KslBuiltinDpdyScalar) = "dpdy(${generateArgs(func.args, 1)})"
    override fun builtinDpdy(func: KslBuiltinDpdyVector<*>) = "dpdy(${generateArgs(func.args, 1)})"
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
        return "(${func.args[0].generateExpression()} * 0.0 != 0.0)"
    }
    override fun builtinIsInf(func: KslBuiltinIsInfVector<*, *>): String {
        logW { "WGSL has no builtin isInfinity check, using fallback method, which might not work as expected: https://github.com/gpuweb/gpuweb/issues/2270" }
        // In floating-point arithmetic Infinity * 0.0 is NaN
        return "(${func.args[0].generateExpression()} * 0.0 != ${func.args[0].generateExpression()} * 0.0)"
    }
    override fun builtinIsNan(func: KslBuiltinIsNanScalar): String {
        logW { "WGSL has no builtin isNaN check, using fallback method, which might not work as expected: https://github.com/gpuweb/gpuweb/issues/2270" }
        // In floating-point arithmetic: NaN != NaN
        return "(${func.args[0].generateExpression()} != ${func.args[0].generateExpression()})"
    }
    override fun builtinIsNan(func: KslBuiltinIsNanVector<*, *>): String {
        logW { "WGSL has no builtin isNaN check, using fallback method, which might not work as expected: https://github.com/gpuweb/gpuweb/issues/2270" }
        // In floating-point arithmetic: NaN != NaN
        return "(${func.args[0].generateExpression()} != ${func.args[0].generateExpression()})"
    }

    override fun getStateName(state: KslState): String = generatorState.getVarName(state.stateName)

    companion object {
        fun generateProgram(program: KslProgram, pipeline: DrawPipeline): WgslGeneratorOutput {
            val vertexStage = checkNotNull(program.vertexStage) {
                "KslProgram vertexStage is missing (a valid KslShader needs at least a vertexStage and fragmentStage)"
            }
            val fragmentStage = checkNotNull(program.fragmentStage) {
                "KslProgram fragmentStage is missing (a valid KslShader needs at least a vertexStage and fragmentStage)"
            }
            val generatorExpressions = vertexStage.generatorExpressions + fragmentStage.generatorExpressions
            val generator = WgslGenerator(generatorExpressions)
            return generator.generateProgram(program, pipeline)
        }

        fun generateComputeProgram(program: KslProgram, pipeline: ComputePipeline): WgslGeneratorOutput {
            val computeStage = checkNotNull(program.computeStage) {
                "KslProgram computeStage is missing"
            }
            val generator = WgslGenerator(computeStage.generatorExpressions)
            return generator.generateComputeProgram(program, pipeline)
        }

        fun samplerName(samplerExpression: String): String {
            return "${samplerExpression}_sampler"
        }

        fun textureName(samplerExpression: String): String {
            return samplerExpression
        }

        fun KslType.wgslStorageTextureTypeName(): String {
            return when (this) {
                is KslStorageTexture1dType<*> -> "texture_storage_1d"
                is KslStorageTexture2dType<*> -> "texture_storage_2d"
                is KslStorageTexture3dType<*> -> "texture_storage_3d"
                else -> error("$this is not a storage texture type")
            }
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
                is KslStruct<*> -> proto.structName

                is KslArrayType<*> -> "array<${elemType.wgslTypeName()},${arraySize}>"

                else -> error("no direct type mapping for type $this")
            }
        }

        fun GpuType.wgslTypeName(): String {
            return when (this) {
                GpuType.Float1 -> "f32"
                GpuType.Float2 -> "vec2f"
                GpuType.Float3 -> "vec3f"
                GpuType.Float4 -> "vec4f"

                GpuType.Int1 -> "i32"
                GpuType.Int2 -> "vec2i"
                GpuType.Int3 -> "vec3i"
                GpuType.Int4 -> "vec4i"

                GpuType.Uint1 -> "u32"
                GpuType.Uint2 -> "vec2u"
                GpuType.Uint3 -> "vec3u"
                GpuType.Uint4 -> "vec4u"

                GpuType.Bool1 -> "bool"
                GpuType.Bool2 -> "vec2b"
                GpuType.Bool3 -> "vec3b"
                GpuType.Bool4 -> "vec4b"

                GpuType.Mat2 -> "mat2x2f"
                GpuType.Mat3 -> "mat3x3f"
                GpuType.Mat4 -> "mat4x4f"
                is GpuType.Struct -> struct.structName
            }
        }

        fun StorageAccessType.wgslStorageTextureAccessType(): String {
            return when (this) {
                StorageAccessType.READ_ONLY -> "read"
                StorageAccessType.WRITE_ONLY -> "write"
                StorageAccessType.READ_WRITE -> "read_write"
            }
        }

        fun StorageAccessType.wgslAccessType(): String {
            return when (this) {
                StorageAccessType.READ_ONLY -> "read"
                StorageAccessType.WRITE_ONLY -> "read_write"    // in wgsl, access mode 'write' is not valid for the 'storage' address space
                StorageAccessType.READ_WRITE -> "read_write"
            }
        }

        fun storageTextureFormatQualifier(texFormat: TexFormat): String {
            return when (texFormat) {
                TexFormat.RGBA -> "rgba8unorm"
                TexFormat.RGBA_F16 -> "rgba16float"
                TexFormat.R_F32 -> "r32float"
                TexFormat.RG_F32 -> "rg32float"
                TexFormat.RGBA_F32 -> "rgba32float"
                TexFormat.R_I32 -> "r32sint"
                TexFormat.RG_I32 -> "rg32sint"
                TexFormat.RGBA_I32 -> "rgba32sint"
                TexFormat.R_U32 -> "r32uint"
                TexFormat.RG_U32 -> "rg32uint"
                TexFormat.RGBA_U32 -> "rgba32uint"
                TexFormat.RG11B10_F -> "rgba16float"        // wgsl does not support rg11b10 as storage texture format -> use f16 as fallback
                else -> error("unsupported storage texture format: $texFormat")
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
                    .filterIsInstance<UniformBufferLayout<*>>()
                    .forEach { ubo ->
                        val uboVarName = ubo.name.mapIndexed { i, c -> if (i == 0) c.lowercase() else c }.joinToString("")
                        ubo.structProvider().members.forEach { nameMap[it.memberName] = "${uboVarName}.${it.memberName}" }
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

    private data class UboStruct(val name: String, val typeName: String, val members: List<WgslStructMember>, val binding: UniformBufferLayout<*>)

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

    private fun StringBuilder.generateStructs(stage: KslShaderStage, pipeline: PipelineBase) {
        val structs = stage.getUsedStructs()
        if (structs.isNotEmpty()) {
            appendLine("// structs")
            for (struct in structs) {
                appendLine("struct ${struct.structName} {")
                struct.members.forEach {
                    val type = if (it is StructArrayMember) {
                        "array<${it.type.wgslTypeName()},${it.arraySize}>"
                    } else {
                        it.type.wgslTypeName()
                    }
                    appendLine("    ${it.memberName}: $type,")
                }
                appendLine("};")
            }
            appendLine()
        }
    }

    private fun StringBuilder.generateStructUbos(stage: KslShaderStage, pipeline: PipelineBase) {
        val uboStructs = stage.getUsedUboStructs()
        if (uboStructs.isNotEmpty()) {
            appendLine("// uniform structs")
            uboStructs.forEach { ubo ->
                val (_, desc) = pipeline.getBindGroupItemByName(ubo.name)
                val location = generatorState.locations[desc]
                appendLine("@group(${location.group}) @binding(${location.binding}) var<uniform> ${ubo.name}: ${ubo.expressionType.wgslTypeName()};")
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

    private fun StringBuilder.generateStorageTextures(stage: KslShaderStage, pipeline: PipelineBase) {
        pipeline.bindGroupLayouts.asList.forEach { layout ->
            layout.bindings
                .filterIsInstance<StorageTextureLayout>().filter { storageLayout ->
                    stage.type.pipelineStageType in storageLayout.stages
                }
                .forEach { storageLayout ->
                    val location = generatorState.locations[storageLayout]
                    val kslStorage = stage.getUsedStorageTextures().first { it.name == storageLayout.name }
                    val format = storageTextureFormatQualifier(kslStorage.texFormat)
                    val accessType = storageLayout.accessType.wgslStorageTextureAccessType()
                    val typeName = kslStorage.expressionType.wgslStorageTextureTypeName()
                    appendLine("@group(${location.group}) @binding(${location.binding}) var ${storageLayout.name}: $typeName<$format, $accessType>;")
                }
        }
        appendLine()
    }

    private fun StringBuilder.generateGlobalVars(stage: KslShaderStage) {
        val globals = stage.globalVars.values
        if (globals.isNotEmpty()) {
            appendLine("// global variables")
            globals.forEachIndexed { i, it ->
                appendLine("var<private> ${it.stateName}: ${it.expressionType.wgslTypeName()};")
            }
            appendLine()
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