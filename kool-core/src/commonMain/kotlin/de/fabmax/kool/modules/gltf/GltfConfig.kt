package de.fabmax.kool.modules.gltf

import de.fabmax.kool.AssetLoader
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ksl.ModelMatrixComposition
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.ibl.EnvironmentMap
import de.fabmax.kool.util.ShadowMap

data class GltfLoadConfig(
    val generateNormals: Boolean = false,
    val applyMaterials: Boolean = true,
    val materialConfig: GltfMaterialConfig = GltfMaterialConfig(),
    val setVertexAttribsFromMaterial: Boolean = false,
    val loadAnimations: Boolean = true,
    val applySkins: Boolean = true,
    val applyMorphTargets: Boolean = true,
    val applyTransforms: Boolean = false,
    val removeEmptyNodes: Boolean = true,
    val mergeMeshesByMaterial: Boolean = false,
    val sortNodesByAlpha: Boolean = true,
    val addInstanceAttributes: List<Attribute> = emptyList(),
    val assetLoader: AssetLoader? = null,
    val pbrBlock: (KslPbrShader.Config.Builder.(GltfMesh.Primitive) -> Unit)? = null
)

data class GltfMaterialConfig(
    val shadowMaps: List<ShadowMap> = emptyList(),
    val scrSpcAmbientOcclusionMap: Texture2d? = null,
    val environmentMap: EnvironmentMap? = null,
    val isDeferredShading: Boolean = false,
    val maxNumberOfLights: Int = 4,
    val fixedNumberOfJoints: Int = 0,
    val modelMatrixComposition: List<ModelMatrixComposition> = emptyList()
)
