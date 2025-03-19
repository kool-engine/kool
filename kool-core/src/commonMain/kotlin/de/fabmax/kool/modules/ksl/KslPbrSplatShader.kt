package de.fabmax.kool.modules.ksl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ksl.KslLitShader.AmbientLight
import de.fabmax.kool.modules.ksl.blocks.*
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.ibl.EnvironmentMap
import de.fabmax.kool.pipeline.shading.AlphaMode
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logW

inline fun KslPbrSplatShader(block: KslPbrSplatShader.Config.Builder.() -> Unit): KslPbrSplatShader {
    val cfg = KslPbrSplatShader.Config.Builder().apply(block).build()
    return KslPbrSplatShader(cfg)
}

class KslPbrSplatShader(val cfg: Config) : KslShader("KslPbrSplatShader") {

    var splatMap by colorTexture(cfg.splatMapCfg)
    val materials = cfg.materials.map { MaterialBinding(it) }

    var ssaoMap: Texture2d? by texture2d("tSsaoMap", cfg.lightingCfg.defaultSsaoMap)
    var ambientFactor: Color by uniformColor("uAmbientColor")
    var ambientMapOrientation: Mat3f by uniformMat3f("uAmbientTextureOri")
    // if ambient color is image based
    var ambientMap: TextureCube? by textureCube("tAmbientTexture")
    // if ambient color is dual image based
    val ambientMaps = List(2) { textureCube("tAmbientTexture_$it") }
    var ambientMapWeights by uniform2f("tAmbientWeights", Vec2f.X_AXIS)

    val reflectionMaps = List(2) { textureCube("tReflectionMap_$it") }
    var reflectionMapWeights: Vec2f by uniform2f("uReflectionWeights")
    var reflectionStrength: Vec4f by uniform4f("uReflectionStrength", Vec4f(cfg.reflectionStrength, 0f))
    var reflectionMap: TextureCube?
        get() = reflectionMaps[0].get()
        set(value) {
            reflectionMaps[0].set(value)
            reflectionMaps[1].set(value)
            reflectionMapWeights = Vec2f.X_AXIS
        }

    var brdfLut: Texture2d? by texture2d("tBrdfLut")

    var parallaxStrength by uniform1f("uParallaxStrength", 0.5f)

    var debugMode by uniform1i("uDbgMode", 0)
    var dbgWeightColor0 by uniformColor("uColorW0", Color.GREEN.mulRgb(0.75f))
    var dbgWeightColor1 by uniformColor("uColorW1", Color.BLUE.mulRgb(0.75f))
    var dbgWeightColor2 by uniformColor("uColorW2", Color.MAGENTA.mulRgb(0.75f))
    var dbgWeightColor3 by uniformColor("uColorW3", Color.RED.mulRgb(0.75f))
    var dbgWeightColor4 by uniformColor("uColorW4", Color.YELLOW.mulRgb(0.75f))

    val ambientCfg: AmbientLight get() = cfg.lightingCfg.ambientLight
    val isSsao: Boolean get() = cfg.lightingCfg.isSsao
    val displacementCfg: PropertyBlockConfig get() = cfg.vertexCfg.displacementCfg
    val isParallaxMapped: Boolean get() = cfg.isParallax

    /**
     * Read-only list of shadow maps used by this shader. To modify the shadow maps, the shader has to be re-created.
     */
    val shadowMaps = cfg.lightingCfg.shadowMaps.map { it.shadowMap }

    init {
        check(cfg.numSplatMaterials in 2..5)
        pipelineConfig = cfg.pipelineCfg

        textureArrays[DISPLACEMENTS_TEX_NAME] = texture2dArray(DISPLACEMENTS_TEX_NAME, cfg.displacements)
        registerArrayTextures(cfg.vertexCfg.displacementCfg)

        when (val ac = cfg.lightingCfg.ambientLight) {
            is AmbientLight.Uniform -> ambientFactor = ac.ambientFactor
            is AmbientLight.ImageBased -> {
                ambientMap = ac.ambientMap
                ambientFactor = ac.ambientFactor
            }
            is AmbientLight.DualImageBased -> {
                ambientFactor = ac.ambientFactor
            }
        }
        reflectionMap = cfg.reflectionMap

        program.makeProgram()
    }

    override fun createPipeline(mesh: Mesh, instances: MeshInstanceList?, ctx: KoolContext): DrawPipeline {
        return super.createPipeline(mesh, instances, ctx).also {
            if (brdfLut == null) {
                brdfLut = ctx.defaultPbrBrdfLut
            }
        }
    }

    private fun KslProgram.makeProgram() {
        val camData = cameraData()
        val positionWorldSpace = interStageFloat3()
        val normalWorldSpace = interStageFloat3()
        val tangentWorldSpace = interStageFloat4()
        val projPosition = interStageFloat4()

        val texCoordBlock: TexCoordAttributeBlock
        val shadowMapVertexStage: ShadowBlockVertexStage?

        vertexStage {
            main {
                val vertexBlock = vertexTransformBlock(cfg.vertexCfg) {
                    inLocalPos(vertexAttribFloat3(Attribute.POSITIONS.name))
                    inLocalNormal(vertexAttribFloat3(Attribute.NORMALS.name))
                    inLocalTangent(vertexAttribFloat4(Attribute.TANGENTS.name))
                }

                // world position and normal are made available via ports for custom models to modify them
                val worldPos = float3Port("worldPos", vertexBlock.outWorldPos)
                val worldNormal = float3Port("worldNormal", vertexBlock.outWorldNormal)

                positionWorldSpace.input set worldPos
                normalWorldSpace.input set worldNormal
                tangentWorldSpace.input set vertexBlock.outWorldTangent
                projPosition.input set (camData.viewProjMat * float4Value(worldPos, 1f))
                outPosition set projPosition.input

                // texCoordBlock is used by various other blocks to access texture coordinate vertex
                // attributes (usually either none, or Attribute.TEXTURE_COORDS but there can be more)
                texCoordBlock = texCoordAttributeBlock()

                // project coordinates into shadow map / light space
                shadowMapVertexStage = if (cfg.lightingCfg.shadowMaps.isEmpty()) null else {
                    vertexShadowBlock(cfg.lightingCfg) {
                        inPositionWorldSpace(worldPos)
                        inNormalWorldSpace(worldNormal)
                    }
                }
            }
        }

        fragmentStage {
            val lightData = sceneLightData(cfg.lightingCfg.maxNumberOfLights)
            val fnGetStochasticUv = getStochasticUv()

            val fnSampleParallax = if (!cfg.isParallax) null else fnSampleParallax(
                camData = cameraData(),
                strength = uniformFloat1("uParallaxStrength"),
                fragWorldPos = positionWorldSpace.output,
                fragNormal = normalWorldSpace.output,
                projPos = projPosition.output,
                fnGetStochasticUv = fnGetStochasticUv
            )

            main {
                val fragWorldPos = positionWorldSpace.output
                val uv = texCoordBlock.getTextureCoords()
                val ddx = float2Var(dpdx(uv))
                val ddy = float2Var(dpdy(uv))

                val splatMapBlock = fragmentColorBlock(cfg.splatMapCfg, ddx, ddy, uv)
                val weights = decodeWeights(splatMapBlock.outColor)

                val matStates = cfg.materials.map { matCfg ->
                    if (cfg.isParallax) {
                        SplatMatState(matCfg, fnSampleParallax!!, uv, ddx, ddy, this)
                    } else {
                        SplatMatState(matCfg, fnGetStochasticUv, uv, ddx, ddy, this)
                    }
                }

                val maxHeight = float1Var(0f.const)
                val selectedMat = int1Var(0.const)
                matStates.forEach { matState ->
                    `if`(weights[matState.index] gt maxHeight) {
                        with(matState) { sampleHeight(weights[matState.index]) }
                        `if`(matState.weightedHeight gt maxHeight) {
                            maxHeight set matState.weightedHeight
                            selectedMat set matState.index.const
                        }
                    }
                }

                val baseColor = float4Var()
                val normal = float3Var(normalize(normalWorldSpace.output))
                val arm = float3Var(float3Value(1f, 0.5f, 0f))
                matStates.forEach { matState ->
                    with(matState) {
                        sampleMaterialIfSelected(selectedMat, tangentWorldSpace.output, baseColor, normal, arm)
                    }
                }

                // create an array with light strength values per light source (1.0 = full strength)
                val shadowFactors = float1Array(lightData.maxLightCount, 1f.const)
                // adjust light strength values by shadow maps
                if (shadowMapVertexStage != null) {
                    fragmentShadowBlock(shadowMapVertexStage, shadowFactors)
                }

                if (cfg.lightingCfg.isSsao) {
                    val aoMap = texture2d("tSsaoMap")
                    val aoUv = float2Var(projPosition.output.xy / projPosition.output.w * 0.5f.const + 0.5f.const)
                    arm.x *= sampleTexture(aoMap, aoUv).x
                }

                val irradiance = when (cfg.lightingCfg.ambientLight) {
                    is AmbientLight.Uniform -> uniformFloat4("uAmbientColor").rgb
                    is AmbientLight.ImageBased -> {
                        val ambientOri = uniformMat3("uAmbientTextureOri")
                        val ambientTex = textureCube("tAmbientTexture")
                        (sampleTexture(ambientTex, ambientOri * normal, 0f.const) * uniformFloat4("uAmbientColor")).rgb
                    }
                    is AmbientLight.DualImageBased -> {
                        val ambientOri = uniformMat3("uAmbientTextureOri")
                        val ambientTexs = List(2) { textureCube("tAmbientTexture_$it") }
                        val ambientWeights = uniformFloat2("tAmbientWeights")
                        val ambientColor = float4Var(sampleTexture(ambientTexs[0], ambientOri * normal, 0f.const) * ambientWeights.x)
                        `if`(ambientWeights.y gt 0f.const) {
                            ambientColor += float4Var(sampleTexture(ambientTexs[1], ambientOri * normal, 0f.const) * ambientWeights.y)
                        }
                        (ambientColor * uniformFloat4("uAmbientColor")).rgb
                    }
                }

                val ambientOri = uniformMat3("uAmbientTextureOri")
                val brdfLut = texture2d("tBrdfLut")
                val reflectionStrength = uniformFloat4("uReflectionStrength").rgb
                val reflectionMaps = if (cfg.isTextureReflection) {
                    List(2) { textureCube("tReflectionMap_$it") }
                } else {
                    null
                }

                val material = pbrMaterialBlock(cfg.lightingCfg.maxNumberOfLights, reflectionMaps, brdfLut) {
                    inCamPos(camData.position)
                    inNormal(normal)
                    inFragmentPos(fragWorldPos)
                    inBaseColor(baseColor)

                    inRoughness(arm.y)
                    inMetallic(arm.z)

                    inIrradiance(irradiance)
                    inAoFactor(arm.x)
                    inAmbientOrientation(ambientOri)

                    inReflectionMapWeights(uniformFloat2("uReflectionWeights"))
                    inReflectionStrength(reflectionStrength)

                    setLightData(lightData, shadowFactors, cfg.lightingCfg.lightStrength.const)
                }
                val materialColor = float4Var(float4Value(material.outColor, baseColor.a))

                val outRgb = float3Var(materialColor.rgb)
                if (cfg.pipelineCfg.blendMode == BlendMode.BLEND_PREMULTIPLIED_ALPHA) {
                    outRgb set outRgb * materialColor.a
                }
                outRgb set convertColorSpace(outRgb, cfg.colorSpaceConversion)

                if (cfg.isWithDebugOptions) {
                    val dbg = uniformInt1("uDbgMode")
                    `if`(dbg eq DEBUG_MODE_WEIGHTS.const) {
                        materialColor.a set 1f.const
                        outRgb set Vec3f.ZERO.const
                        for (i in 0 until cfg.numSplatMaterials) {
                            outRgb += uniformFloat4("uColorW$i").rgb * weights[i]
                        }

                    }.elseIf(dbg eq DEBUG_MODE_NORMALS.const) {
                        materialColor.a set 1f.const
                        outRgb set normal * 0.5f.const + 0.5f.const

                    }.elseIf(dbg eq DEBUG_MODE_DISPLACEMENT.const) {
                        materialColor.a set 1f.const
                        outRgb set float3Value(maxHeight, maxHeight, maxHeight)
                    }
                }

                when (cfg.alphaMode) {
                    is AlphaMode.Blend -> colorOutput(outRgb, materialColor.a)
                    is AlphaMode.Mask -> colorOutput(outRgb, 1f.const)
                    is AlphaMode.Opaque -> colorOutput(outRgb, 1f.const)
                }
            }
        }
    }

    private fun KslScopeBuilder.decodeWeights(encodedWeights: KslExprFloat4): KslArrayScalar<KslFloat1> {
        val weights = float1Array(cfg.numSplatMaterials, 0f.const)

        val r = encodedWeights.r
        val g = encodedWeights.g
        val b = encodedWeights.b
        val a = encodedWeights.a

        weights[0] set when (cfg.numSplatMaterials) {
            2 -> 1f.const - r
            3 -> 1f.const - min(1f.const, r + g)
            4 -> 1f.const - min(1f.const, r + g + b)
            5 -> 1f.const - min(1f.const, r + g + b + a)
            else -> error("invalid number of splat materials: ${cfg.numSplatMaterials}")
        }
        weights[1] set r
        if (cfg.numSplatMaterials >= 3) weights[2] set g
        if (cfg.numSplatMaterials >= 4) weights[3] set b
        if (cfg.numSplatMaterials == 5) weights[4] set a
        return weights
    }

    private fun KslShaderStage.getStochasticUv() = functionFloat4("getStochasticUv") {
        val inputUv = paramFloat2()
        val materialIndex = paramInt1()
        val ddx = paramFloat2()
        val ddy = paramFloat2()
        val scaleRot = paramFloat3()

        body {
            val inputRot = rotationMat(scaleRot.x)
            val rotInputUv = float2Var(inputRot * inputUv)
            val uv = float2Var(rotInputUv * 3.464f.const * scaleRot.y)
            val dispTex = program.texture2dArray(DISPLACEMENTS_TEX_NAME)

            // skew input space into simplex triangle grid
            val gridToSkewedGrid = mat2Value(float2Value(1f, -0.57735026f), float2Value(0f, 1.1547005f))
            val skewedCoord = float2Var(gridToSkewedGrid * uv)

            val vertex1 = int2Var()
            val vertex2 = int2Var()
            val vertex3 = int2Var()
            val w = float3Var()

            // compute local triangle vertex IDs and local barycentric coordinates
            val baseId = int2Var(floor(skewedCoord).toInt2())
            val temp = float3Var(float3Value(fract(skewedCoord), 0f.const))
            temp.z set 1f.const - temp.x - temp.y
            `if`(temp.z gt 0f.const) {
                w.set(float3Value(temp.z, temp.y, temp.x))
                vertex1 set baseId
                vertex2 set baseId + int2Value(0, 1)
                vertex3 set baseId + int2Value(1, 0)
            }.`else` {
                w.set(float3Value(-temp.z, 1f.const - temp.y, 1f.const - temp.x))
                vertex1 set baseId + int2Value(1, 1)
                vertex2 set baseId + int2Value(1, 0)
                vertex3 set baseId + int2Value(0, 1)
            }

            // compute shifted (and rotated) uvs from hashed vertex ids
            val noise = float3Array(3, Vec3f.ZERO.const)
            noise[0] set noise23(vertex1.toFloat2())
            noise[1] set noise23(vertex2.toFloat2())
            noise[2] set noise23(vertex3.toFloat2())

            noise[0].z set (noise[0].z - 0.5f.const) * scaleRot.z
            noise[1].z set (noise[1].z - 0.5f.const) * scaleRot.z
            noise[2].z set (noise[2].z - 0.5f.const) * scaleRot.z

            val r1 = rotationMat(noise[0].z)
            val r2 = rotationMat(noise[1].z)
            val r3 = rotationMat(noise[2].z)

            val shiftedUvs = float2Array(3, Vec2f.ZERO.const)
            shiftedUvs[0] set r1 * (rotInputUv + noise[0].xy)
            shiftedUvs[1] set r2 * (rotInputUv + noise[1].xy)
            shiftedUvs[2] set r3 * (rotInputUv + noise[2].xy)

            // select shifted uv with the highest displacement value
            if (cfg.isContinuousHeight) {
                // sample all vertices, select highest one but use blended height
                val selected = int1Var(0.const)
                val h1 = float1Var(sampleTextureArrayGrad(dispTex, materialIndex, shiftedUvs[0], ddx, ddy).x * w.x)
                val h2 = float1Var(sampleTextureArrayGrad(dispTex, materialIndex, shiftedUvs[1], ddx, ddy).x * w.y)
                val h3 = float1Var(sampleTextureArrayGrad(dispTex, materialIndex, shiftedUvs[2], ddx, ddy).x * w.z)
                val h = float1Var(h1 + h2 + h3)
                `if`(h1 gt max(h2, h3)) {
                    selected set 0.const
                }.elseIf(h2 gt max(h1, h3)) {
                    selected set 1.const
                }.`else` {
                    selected set 2.const
                }
                float4Value(shiftedUvs[selected], h, noise[selected].z)

            } else {
                // try to reduce sample count by skipping vertices with weight less than current max height
                val selected = int1Var(0.const)
                val h = float1Var(sampleTextureArrayGrad(dispTex, materialIndex, shiftedUvs[0], ddx, ddy).x)
                val hw = float1Var(h * w.x)
                `if`(hw lt w.y) {
                    val ht = float1Var(sampleTextureArrayGrad(dispTex, materialIndex, shiftedUvs[1], ddx, ddy).x)
                    `if`(ht * w.y gt hw) {
                        h set ht
                        hw set ht * w.y
                        selected set 1.const
                    }
                }
                `if`(hw lt w.z) {
                    val ht = float1Var(sampleTextureArrayGrad(dispTex, materialIndex, shiftedUvs[2], ddx, ddy).x)
                    `if`(ht * w.z gt hw) {
                        h set ht
                        selected set 2.const
                    }
                }
                float4Value(shiftedUvs[selected], h, noise[selected].z)
            }
        }
    }

    private fun KslShaderStage.fnSampleParallax(
        camData: CameraData,
        strength: KslExprFloat1,
        fragWorldPos: KslExprFloat3,
        fragNormal: KslExprFloat3,
        projPos: KslExprFloat4,
        fnGetStochasticUv: KslFunctionFloat4,
    ) = functionFloat4("sampleParallax") {
        val uv = paramFloat2()
        val materialIndex = paramInt1()
        val ddx = paramFloat2()
        val ddy = paramFloat2()
        val scaleRot = paramFloat3()

        body {
            val maxSteps = 16.const
            val step = (1f / 16f).const

            val viewDir = float3Var(normalize(fragWorldPos - camData.position))

            val proj = float2Var((projPos.xy / projPos.w + 1f.const) * 0.5f.const)
            if (KoolSystem.requireContext().backend.isInvertedNdcY) {
                proj.y set 1f.const - proj.y
            }
            val pixelPos = float2Var(proj * camData.viewport.zw)

            val sampleScale = float1Var(strength / abs(dot(fragNormal, viewDir)))
            val sampleDir = float3Var(viewDir - fragNormal * dot(viewDir, fragNormal))

            val sampleExt = float3Var(fragWorldPos + sampleDir * sampleScale)
            val sampleExtProj = float4Var(camData.viewProjMat * float4Value(sampleExt, 1f.const))
            proj set (sampleExtProj.xy / sampleExtProj.w + 1f.const) * 0.5f.const
            if (KoolSystem.requireContext().backend.isInvertedNdcY) {
                proj.y set 1f.const - proj.y
            }
            val sampleExtPixel = float2Var(proj * camData.viewport.zw - pixelPos)

            val sampleUv = float2Var(uv)
            val prevSampleUv = float2Var(uv)
            val prevH = float1Var(0f.const)
            val hStart = float1Var(noise21(pixelPos) * step)

            val outBlendInfo = float4Var()

            repeat(maxSteps) { i ->
                val hLimit = float1Var(hStart + i.toFloat1() * step)

                outBlendInfo set fnGetStochasticUv(sampleUv, materialIndex, ddx, ddy, scaleRot)
                val h = float1Var(1f.const - outBlendInfo.z)

                `if` (h lt hLimit) {
                    val afterDepth = float1Var(h - hLimit)
                    val beforeDepth = float1Var(prevH - hLimit + step)
                    val weight = float1Var(afterDepth / (afterDepth - beforeDepth))
                    sampleUv set prevSampleUv * weight + sampleUv * (1f.const - weight)
                    prevH set prevH * weight + h * (1f.const - weight)
                    `break`()

                }.`else` {
                    prevH set h
                    prevSampleUv set sampleUv
                    val sampleOffset = float2Var(sampleExtPixel * min(h, hLimit))
                    sampleUv set uv + ddx * sampleOffset.x + ddy * sampleOffset.y
                }
            }

            outBlendInfo
        }
    }

    private fun KslScopeBuilder.rotationMat(angle: KslExprFloat1): KslExprMat2 {
        val cos = float1Var(cos(angle))
        val sin = float1Var(sin(angle))
        return mat2Var(mat2Value(float2Value(cos, sin), float2Value(-sin, cos)))
    }

    companion object {
        const val DEBUG_MODE_OFF = 0
        const val DEBUG_MODE_WEIGHTS = 1
        const val DEBUG_MODE_NORMALS = 2
        const val DEBUG_MODE_DISPLACEMENT = 3

        const val DISPLACEMENTS_TEX_NAME = "disp_textures"
    }

    private inner class SplatMatState(
        val splatMatCfg: SplatMaterialConfig,
        val fnGetUv: KslFunctionFloat4,
        inputUv: KslExprFloat2,
        inputDdx: KslVarFloat2,
        inputDdy: KslVarFloat2,
        scope: KslScopeBuilder
    ) {
        val index: Int get() = splatMatCfg.materialIndex
        val matSettings = scope.parentStage.program.uniformFloat4("uMatSetting_$index")

        val uvScale: KslExprFloat1 get() = matSettings.x
        val uvRot: KslExprFloat1 get() = matSettings.y
        val tileSize: KslExprFloat1 get() = matSettings.z
        val tileRot: KslExprFloat1 get() = matSettings.w

        val scaledUv = scope.float2Var(inputUv * uvScale)
        val ddx = scope.float2Var(inputDdx * uvScale)
        val ddy = scope.float2Var(inputDdy * uvScale)
        val rotMat = scope.mat3Var()
        val blendInfo = scope.float4Var(KslValueFloat4(0f, 0f, 0f, 0f))

        val tiledUv: KslExprFloat2 get() = blendInfo.xy
        val height: KslExprFloat1 get() = blendInfo.z
        var weightedHeight: KslExprFloat1 = KslValueFloat1(0f)

        fun KslScopeBuilder.sampleHeight(weight: KslExprFloat1) {
            blendInfo set fnGetUv(scaledUv, index.const, ddx, ddy, float3Value(uvRot, tileSize, tileRot))
            weightedHeight = float1Var(height * weight)

            val cos = float1Var(cos(blendInfo.w))
            val sin = float1Var(sin(blendInfo.w))
            rotMat set mat3Value(
                float3Value(cos, -sin, 0f.const),
                float3Value(sin, cos, 0f.const),
                float3Value(0f, 0f, 1f),
            )
            ddx set (rotMat * float3Value(ddx, 0f.const)).xy
            ddy set (rotMat * float3Value(ddy, 0f.const)).xy
        }

        fun KslScopeBuilder.sampleMaterialIfSelected(
            selectedMat: KslExprInt1,
            inTangent: KslExprFloat4,
            outBaseColor: KslVarFloat4,
            inOutNormal: KslVarFloat3,
            outArm: KslVarFloat3
        ) {
            `if`(index.const eq selectedMat) {
                fragmentColorBlock(splatMatCfg.colorCfg, ddx, ddy, tiledUv).apply {
                    outBaseColor set outColor
                }
                if (splatMatCfg.normalMapCfg.isNormalMapped) {
                    val mapNormal = if (splatMatCfg.normalMapCfg.isArrayNormalMap) {
                        val tNormal = program.texture2dArray(splatMatCfg.normalMapCfg.textureName)
                        float3Var(sampleTextureArrayGrad(tNormal, splatMatCfg.normalMapCfg.normalMapArrayIndex.const, tiledUv, ddx, ddy).xyz * 2f.const - 1f.const)
                    } else {
                        val tNormal = program.texture2d(splatMatCfg.normalMapCfg.textureName)
                        float3Var(sampleTextureGrad(tNormal, tiledUv, ddx, ddy).xyz * 2f.const - 1f.const)
                    }
                    mapNormal set rotMat * normalize(mapNormal)
                    inOutNormal set calcBumpedNormal(inOutNormal, inTangent, mapNormal, 1f.const)
                }
                fragmentPropertyBlock(splatMatCfg.aoCfg, ddx, ddy, tiledUv).apply { outArm.x set outProperty }
                fragmentPropertyBlock(splatMatCfg.roughnessCfg, ddx, ddy, tiledUv).apply { outArm.y set outProperty }
                fragmentPropertyBlock(splatMatCfg.metallicCfg, ddx, ddy, tiledUv).apply { outArm.z set outProperty }
            }
        }
    }

    inner class MaterialBinding(matCfg: SplatMaterialConfig) {
        var colorMap by colorTexture(matCfg.colorCfg)
        var normalMap by normalTexture(matCfg.normalMapCfg)
        var aoMap by propertyTexture(matCfg.aoCfg)
        var roughnessMap by propertyTexture(matCfg.roughnessCfg)
        var metallicMap by propertyTexture(matCfg.metallicCfg)
        var emissionMap: Texture2d? by colorTexture(matCfg.emissionCfg)

        var color: Color by colorUniform(matCfg.colorCfg)
        var roughness: Float by propertyUniform(matCfg.roughnessCfg)
        var metallic: Float by propertyUniform(matCfg.metallicCfg)
        var emission: Color by colorUniform(matCfg.emissionCfg)

        private var matSettings: Vec4f by uniform4f(
            uniformName = "uMatSetting_${matCfg.materialIndex}",
            defaultVal = Vec4f(matCfg.uvScale, matCfg.stochasticTileSize, matCfg.stochasticTileRotation.rad, 0f)
        )
        var textureScale: Float
            get() = matSettings.x
            set(value) { matSettings = Vec4f(value, matSettings.y, matSettings.z, matSettings.w) }
        var textureRotation: AngleF
            get() = matSettings.y.rad
            set(value) { matSettings = Vec4f(matSettings.x, value.rad, matSettings.z, matSettings.w) }
        var tileSize: Float
            get() = matSettings.z
            set(value) { matSettings = Vec4f(matSettings.x, matSettings.y, value, matSettings.w) }
        var tileRotation: AngleF
            get() = matSettings.w.rad
            set(value) { matSettings = Vec4f(matSettings.x, matSettings.y, matSettings.z, value.rad) }

        init {
            if (matCfg.normalMapCfg.isArrayNormalMap && matCfg.normalMapCfg.textureName !in textureArrays) {
                textureArrays[matCfg.normalMapCfg.textureName] = texture2dArray(
                    textureName = matCfg.normalMapCfg.textureName,
                    defaultVal = matCfg.normalMapCfg.defaultArrayNormalMap,
                )
            }
            registerArrayTextures(matCfg.colorCfg)
            registerArrayTextures(matCfg.emissionCfg)
            registerArrayTextures(matCfg.aoCfg)
            registerArrayTextures(matCfg.roughnessCfg)
            registerArrayTextures(matCfg.metallicCfg)
        }
    }

    class Config(builder: Builder) {
        val pipelineCfg = builder.pipelineCfg.build()
        val vertexCfg = builder.vertexCfg.build()
        val lightingCfg = builder.lightingCfg.build()

        val splatMapCfg = builder.splatMapCfg.build()
        val displacements: Texture2dArray? = builder.displacements
        val materials = builder.materials.toList()
        val numSplatMaterials: Int get() = materials.size

        val isTextureReflection = builder.isTextureReflection
        val reflectionStrength = builder.reflectionStrength
        val reflectionMap = builder.reflectionMap

        val colorSpaceConversion = builder.colorSpaceConversion
        val alphaMode = builder.alphaMode

        val modelCustomizer = builder.modelCustomizer
        val isWithDebugOptions = builder.isWithDebugOptions

        val isContinuousHeight = builder.isContinuousHeight
        val isParallax = builder.isParallax

        open class Builder {
            val pipelineCfg = PipelineConfig.Builder()
            val vertexCfg = BasicVertexConfig.Builder()
            val lightingCfg = LightingConfig.Builder()

            val splatMapCfg = ColorBlockConfig.Builder("splatMap")
            var displacements: Texture2dArray? = null
            private val _materials = mutableListOf<SplatMaterialConfig>()
            val materials: List<SplatMaterialConfig> get() = _materials

            var isTextureReflection = false
            var reflectionStrength = Vec3f.ONES
            var reflectionMap: TextureCube? = null
                set(value) {
                    field = value
                    isTextureReflection = value != null
                }

            var colorSpaceConversion: ColorSpaceConversion = ColorSpaceConversion.LinearToSrgbHdr()
            var alphaMode: AlphaMode = AlphaMode.Blend

            var modelCustomizer: (KslProgram.() -> Unit)? = null
            var isWithDebugOptions = false

            // slightly slower than without continuous height but slightly better quality (should be enabled for
            // parallax, else leave it disabled)
            var isContinuousHeight = false

            // somewhat works but is slow, is problematic for materials with different height scales and
            // introduces a lot of artifacts, so keep it off for now
            var isParallax = false

            init {
                useSplatMap(null)
            }

            inline fun pipeline(block: PipelineConfig.Builder.() -> Unit) {
                pipelineCfg.block()
            }

            inline fun vertices(block: BasicVertexConfig.Builder.() -> Unit) {
                vertexCfg.block()
            }

            inline fun lighting(block: LightingConfig.Builder.() -> Unit) {
                lightingCfg.block()
            }

            inline fun splatMap(block: ColorBlockConfig.Builder.() -> Unit) {
                splatMapCfg.colorSources.clear()
                splatMapCfg.block()
            }

            fun useDisplacements(displacements: Texture2dArray?) {
                this.displacements = displacements
            }

            fun useSplatMap(texture2d: Texture2d?) = splatMap { textureData(texture2d) }

            fun enableImageBasedLighting(iblMaps: EnvironmentMap): Builder {
                lightingCfg.imageBasedAmbientLight(iblMaps.irradianceMap)
                reflectionMap = iblMaps.reflectionMap
                return this
            }

            fun addMaterial(block: SplatMaterialConfig.Builder.() -> Unit) {
                if (materials.size >= 5) {
                    logE { "Maximum number of splat materials reached (max: 5 materials)" }
                    return
                }
                val builder = SplatMaterialConfig.Builder(materials.size).apply(block)
                _materials += builder.build()
            }

            fun build(): Config {
                if (materials.size < 2) {
                    logW { "KslPbrSplatShader requires at least 2 splat materials" }
                    while (materials.size < 2) {
                        addMaterial {  }
                    }
                }
                return Config(this)
            }
        }
    }

    data class SplatMaterialConfig(
        val materialIndex: Int,
        val colorCfg: ColorBlockConfig,
        val normalMapCfg: NormalMapConfig,
        val aoCfg: PropertyBlockConfig,
        val roughnessCfg: PropertyBlockConfig,
        val metallicCfg: PropertyBlockConfig,
        val emissionCfg: ColorBlockConfig,

        val uvScale: Float,
        val stochasticTileSize: Float,
        val stochasticTileRotation: AngleF,
    ) {
        class Builder(val materialIndex: Int) {
            val colorCfg = ColorBlockConfig.Builder("color_$materialIndex")
            val normalMapCfg = NormalMapConfig.Builder("normalMap_$materialIndex")
            val aoCfg = PropertyBlockConfig.Builder("ao_$materialIndex").apply { constProperty(1f) }
            val roughnessCfg = PropertyBlockConfig.Builder("rough_$materialIndex")
            val metallicCfg = PropertyBlockConfig.Builder("metal_$materialIndex")
            val emissionCfg = ColorBlockConfig.Builder("emission_$materialIndex")

            var uvScale: Float = 10f
            var stochasticTileSize: Float = 0.5f
            var stochasticTileRotation: AngleF = 360f.deg

            inline fun ao(block: PropertyBlockConfig.Builder.() -> Unit) {
                aoCfg.propertySources.clear()
                aoCfg.block()
            }

            inline fun color(block: ColorBlockConfig.Builder.() -> Unit) {
                colorCfg.colorSources.clear()
                colorCfg.block()
            }

            inline fun emission(block: ColorBlockConfig.Builder.() -> Unit) {
                emissionCfg.colorSources.clear()
                emissionCfg.block()
            }

            inline fun normalMapping(block: NormalMapConfig.Builder.() -> Unit) {
                normalMapCfg.block()
            }

            inline fun metallic(block: PropertyBlockConfig.Builder.() -> Unit) {
                metallicCfg.propertySources.clear()
                metallicCfg.block()
            }

            fun metallic(value: Float): Builder {
                metallic { constProperty(value) }
                return this
            }

            inline fun roughness(block: PropertyBlockConfig.Builder.() -> Unit) {
                roughnessCfg.propertySources.clear()
                roughnessCfg.block()
            }

            fun roughness(value: Float): Builder {
                roughness { constProperty(value) }
                return this
            }


            fun build(): SplatMaterialConfig {
                return SplatMaterialConfig(
                    materialIndex = materialIndex,
                    colorCfg = colorCfg.build(),
                    normalMapCfg = normalMapCfg.build(),
                    aoCfg = aoCfg.build(),
                    roughnessCfg = roughnessCfg.build(),
                    metallicCfg = metallicCfg.build(),
                    emissionCfg = emissionCfg.build(),
                    uvScale = uvScale,
                    stochasticTileSize = stochasticTileSize,
                    stochasticTileRotation = stochasticTileRotation
                )
            }
        }
    }
}