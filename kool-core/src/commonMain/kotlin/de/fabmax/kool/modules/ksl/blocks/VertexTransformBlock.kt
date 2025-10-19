package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.modules.ksl.BasicVertexConfig
import de.fabmax.kool.modules.ksl.ModelMatrixComposition
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.scene.VertexLayouts
import de.fabmax.kool.scene.vertexAttrib

fun KslScopeBuilder.vertexTransformBlock(cfg: BasicVertexConfig, block: VertexTransformBlock.() -> Unit): VertexTransformBlock {
    val vertexBlock = VertexTransformBlock(cfg, parentStage.program.nextName("vertexBlock"), this)
    vertexBlock.block()
    ops += vertexBlock
    return vertexBlock
}

class VertexTransformBlock(val cfg: BasicVertexConfig, name: String, parentScope: KslScopeBuilder) : KslBlock(name, parentScope) {
    val inLocalPos = inFloat3()
    val inLocalNormal = inFloat3(defaultValue = KslValueFloat3(0f, 0f, 0f))
    val inLocalTangent = inFloat4(defaultValue = KslValueFloat4(0f, 0f, 0f, 0f))

    val outModelMat = outMat4()
    val outWorldPos = outFloat3()
    val outWorldNormal = outFloat3()
    val outWorldTangent = outFloat4()

    init {
        body.apply {
            val stage = parentStage as? KslVertexStage ?: throw IllegalStateException("VertexTransformBlock is only allowed in vertex stage")

            val localPos = float3Var(inLocalPos)
            val localNormal = float3Var(inLocalNormal)
            val localTangent = float3Var(inLocalTangent.xyz)

            if (cfg.modelMatrixComposition.isEmpty()) {
                outModelMat set mat4Value(Vec4f.X_AXIS.const, Vec4f.Y_AXIS.const, Vec4f.Z_AXIS.const, Vec4f.W_AXIS.const)
            } else {
                cfg.modelMatrixComposition.forEachIndexed { i, mat ->
                    val srcMat = when (mat) {
                        ModelMatrixComposition.UniformModelMat -> parentStage.program.modelMatrix().matrix
                        is ModelMatrixComposition.InstanceModelMat -> stage.instanceAttribMat4(mat.attributeName)
                    }
                    if (i == 0) {
                        outModelMat set srcMat
                    } else {
                        outModelMat *= srcMat
                    }
                }
            }

            if (cfg.isArmature) {
                val armatureBlock = armatureBlock(cfg.maxNumberOfBones)
                armatureBlock.inBoneWeights(stage.vertexAttrib(VertexLayouts.Weight.weight))
                armatureBlock.inBoneIndices(stage.vertexAttrib(VertexLayouts.Joint.joint))
                outModelMat *= armatureBlock.outBoneTransform
            }

            if (cfg.isMorphing) {
                val morphData = stage.program.morphWeightData()
                cfg.morphAttributes.forEachIndexed { i, morphAttrib ->
                    val weight = getMorphWeightComponent(i, morphData)
                    when {
                        morphAttrib.name.startsWith(VertexLayouts.Position.name) -> {
                            localPos += stage.vertexAttribFloat3(morphAttrib.name) * weight
                        }
                        morphAttrib.name.startsWith(VertexLayouts.Normal.name) -> {
                            localNormal += stage.vertexAttribFloat3(morphAttrib.name) * weight
                        }
                        morphAttrib.name.startsWith(VertexLayouts.Tangent.name) -> {
                            localTangent += stage.vertexAttribFloat3(morphAttrib.name) * weight
                        }
                    }
                }
            }

            if (!cfg.displacementCfg.isEmptyOrConst(0f)) {
                val displacement = vertexDisplacementBlock(cfg.displacementCfg).outProperty
                localPos += normalize(localNormal) * displacement
            }

            val worldPos = float4Var(outModelMat * float4Value(localPos, 1f))
            val worldNrm = float4Var(outModelMat * float4Value(localNormal, 0f))
            val worldTan = float4Var(outModelMat * float4Value(localTangent, 0f))

            outWorldPos set worldPos.xyz
            outWorldNormal set normalize(worldNrm.xyz)
            outWorldTangent set float4Value(worldTan.xyz, inLocalTangent.w)
        }
    }

    private fun getMorphWeightComponent(iMorphAttrib: Int, morphData: MorphWeightData): KslExprFloat1 {
        val component = when (iMorphAttrib % 4) {
            0 -> "x"
            1 -> "y"
            2 -> "z"
            else -> "w"
        }
        val weights = if (iMorphAttrib < 4) morphData.weightsA else morphData.weightsB
        return weights.float1(component)
    }
}