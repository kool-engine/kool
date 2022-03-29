package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.modules.ksl.lang.*

fun KslScopeBuilder.armatureBlock(maxNumberOfBones: Int): ArmatureBlock {
    val armatureBlock = ArmatureBlock(maxNumberOfBones, parentStage.program.nextName("armatureBlock"), this)
    ops += armatureBlock
    return armatureBlock
}

class ArmatureBlock(maxBones: Int, name: String, parentScope: KslScopeBuilder) : KslBlock(name, parentScope) {
    val armatureData: ArmatureData

    var inBoneIndices by inInt4("inBoneIndices")
    var inBoneWeights by inFloat4("inBoneWeights")

    val outBoneTransform = outMat4("outBoneTransform")

    init {
        body.apply {
            parentScope.parentStage.program.apply {
                armatureData = ArmatureData(maxBones, this)
                if (maxBones > 0) {
                    uniformBuffers += armatureData
                }
            }

            outBoneTransform set armatureData.boneTransforms[inBoneIndices.x] * inBoneWeights.x
            outBoneTransform += armatureData.boneTransforms[inBoneIndices.y] * inBoneWeights.y
            outBoneTransform += armatureData.boneTransforms[inBoneIndices.z] * inBoneWeights.z
            outBoneTransform += armatureData.boneTransforms[inBoneIndices.w] * inBoneWeights.w
        }
    }
}