package de.fabmax.kool.modules.gltf

import de.fabmax.kool.AssetLoader
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.ModelMatrixComposition
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.deferred.DeferredKslPbrShader
import de.fabmax.kool.pipeline.deferred2.gbufferShader
import de.fabmax.kool.pipeline.ibl.EnvironmentMap
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.ShadowMap
import de.fabmax.kool.util.Struct

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
    val instanceLayout: Struct? = null,
    val assetLoader: AssetLoader? = null,
    val pbrBlock: (KslPbrShader.Config.Builder.(GltfMesh.Primitive) -> Unit)? = null,
)

data class GltfMaterialConfig(
    val shadowMaps: List<ShadowMap> = emptyList(),
    val scrSpcAmbientOcclusionMap: Texture2d? = null,
    val environmentMap: EnvironmentMap? = null,
    val isDeferredShading: Boolean = false,
    val maxNumberOfLights: Int = 4,
    val fixedNumberOfJoints: Int = 0,
    val modelMatrixComposition: List<ModelMatrixComposition> = emptyList(),
    val shaderFactory: GltfShaderFactory? = null,
)

fun interface GltfShaderFactory {
    fun createShader(mesh: Mesh<*>, pbrConfig: DeferredKslPbrShader.Config.Builder): KslShader
}

object GltfDeferredShaderFactory : GltfShaderFactory {
    override fun createShader(mesh: Mesh<*>, pbrConfig: DeferredKslPbrShader.Config.Builder): KslShader {
        return if (mesh.isOpaque) {
            val cfg = pbrConfig.build()
            gbufferShader {
                vertexCfg.set(cfg.vertexCfg)
                colorCfg.colorSources.addAll(cfg.colorCfg.colorSources)
                normalMapCfg.set(cfg.normalMapCfg)
                roughnessCfg.propertySources.addAll(cfg.roughnessCfg.propertySources)
                metallicCfg.propertySources.addAll(cfg.metallicCfg.propertySources)
                aoCfg.propertySources.addAll(cfg.aoCfg.propertySources)
                alphaMode = cfg.alphaMode
            }
        } else {
            pbrConfig.colorSpaceConversion = ColorSpaceConversion.AsIs
            KslPbrShader(pbrConfig.build())
        }
    }
}
