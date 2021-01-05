package de.fabmax.kool.demo.physics

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.GlslType
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.shadermodel.PbrMaterialNode
import de.fabmax.kool.pipeline.shadermodel.StageInterfaceNode
import de.fabmax.kool.pipeline.shadermodel.fragmentStage
import de.fabmax.kool.pipeline.shadermodel.vertexStage
import de.fabmax.kool.pipeline.shading.PbrMaterialConfig
import de.fabmax.kool.pipeline.shading.PbrShader
import de.fabmax.kool.scene.mesh
import de.fabmax.kool.util.*
import de.fabmax.kool.util.ibl.EnvironmentMaps

object GearChainMeshGen {

    private val attribRoughMetallic = Attribute("aRoughMetal", GlslType.VEC_2F)
    private val meshAttribs = listOf(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, attribRoughMetallic)

    fun makeNiceGearMesh(ibl: EnvironmentMaps, aoMap: Texture2d, shadows: List<ShadowMap>) = mesh(meshAttribs) {
        isFrustumChecked = false
        instances = MeshInstanceList(listOf(MeshInstanceList.MODEL_MAT))
        generate {
            var roughness = 0.3f
            var metal = 1f
            vertexModFun = {
                val roughMetal = getVec2fAttribute(attribRoughMetallic)!!
                roughMetal.x = roughness
                roughMetal.y = metal
            }

            // tooth
            profile {
                simpleShape(true) {
                    xzArc(0.7f, -0.2f, Vec2f(0.6f, -0.2f), -90f, 3)
                    xzArc(-0.6f, -0.3f, Vec2f(-0.6f, -0.2f), -90f, 3)
                    xzArc(-0.7f, 0.2f, Vec2f(-0.6f, 0.2f), -90f, 3)
                    xzArc(0.6f, 0.3f, Vec2f(0.6f, 0.2f), -90f, 3)
                }
                withTransform {
                    for (i in 0..11) {
                        withTransform {
                            rotate(i * 30f, Vec3f.Z_AXIS)
                            color = Color.MD_BLUE_GREY_400.toLinear()
                            translate(0f, 8f, 0f)
                            scale(0.7f)
                            sampleAndFillTop()
                            scale(1.2f)
                            sample(inverseOrientation = true)
                            scale(1.2f)
                            translate(0f, -0.1f, 0f)
                            sample(inverseOrientation = true)
                            translate(0f, -0.85f, 0f)
                            scale(1.4f)
                            sample(inverseOrientation = true)
                            translate(0f, -0.15f, 0f)
                            scale(1.3f)
                            color = Color.MD_BLUE_GREY_200.toLinear()
                            sample(inverseOrientation = true)
                        }
                    }
                }
            }

            // generate normals here, everyting after this point generates accurate normals
            geometry.generateNormals()

            // bearing
            color = Color.MD_BLUE_GREY_200.toLinear()
            withTransform {
                profile {
                    simpleShape(true) {
                        roundRectXz(0.8f, 0f, 0.4f, 1.5f, 0.1f, 3)
                    }
                    withTransform {
                        sample()
                        for (i in 0..35) {
                            rotate(0f, 0f, 10f)
                            sample()
                        }
                    }
                }
            }

            // outer ring
            color = Color.MD_BLUE_GREY_200.toLinear()
            profile {
                simpleShape(true) {
                    roundRectXz(6.7f, 0f, 0.6f, 2.4f, 0.2f, 3)
                }
                withTransform {
                    sample()
                    for (i in 0..71) {
                        rotate(0f, 0f, 5f)
                        sample()
                    }
                }
            }

            // dielectric mesh components
            metal = 0f
            roughness = 0f

            // center
            color = Color.MD_RED.toLinear()
            withTransform {
                profile {
                    simpleShape(true) {
                        roundRectXz(1.25f, 0f, 0.5f, 1.6f, 0.1f, 3)
                    }
                    withTransform {
                        sample()
                        for (i in 0..35) {
                            rotate(0f, 0f, 10f)
                            sample()
                        }
                    }
                }
            }

            // spokes
            color = Color.MD_RED.toLinear()
            profile {
                simpleShape(true) {
                    xzArc(0.18f, 0f, Vec2f(0f, 0f), -315f, 8, true)
                }
                for (d in -1..1 step 2) {
                    for (i in 1..10) {
                        withTransform {
                            rotate((360f / 10) * i, Vec3f.Z_AXIS)
                            translate(1.2f * d, 0f, 0f)
                            rotate(-20f * d, 0f, -45f * d)
                            sample(connect = false)
                            for (j in 0..11) {
                                rotate(2.5f * d, 0f, 8f * d)
                                translate(0f, 0.57f, 0f)
                                sample()
                            }
                        }
                    }
                }
            }

            // outer support ring
            color = Color.MD_RED.toLinear()
            profile {
                simpleShape(false) {
                    xz(0f, 0.85f)
                    normals += MutableVec3f(0.1f, 0f, 1f).norm()
                    xzArc(0.8f, 0.5f, Vec2f(0.75f, 0.3f), -70f, 4, true)
                    xzArc(1f, -0.3f, Vec2f(0.75f, -0.3f), -70f, 4, true)
                    xz(0f, -0.85f)
                    normals += MutableVec3f(0.1f, 0f, -1f).norm()
                }
                for (i in 0..72) {
                    withTransform {
                        rotate(0f, 0f, i * 5f)
                        translate(-6.7f, 0f, 0f)
                        sample(inverseOrientation = true)
                    }
                }
            }
        }
        shader = makeMeshShader(ibl, aoMap, shadows)
    }

    fun makeNiceAxleMesh(ibl: EnvironmentMaps, aoMap: Texture2d, shadows: List<ShadowMap>) = mesh(meshAttribs) {
        isFrustumChecked = false
        instances = MeshInstanceList(listOf(MeshInstanceList.MODEL_MAT))
        generate {
            var roughness = 0.3f
            var metal = 1f
            vertexModFun = {
                val roughMetal = getVec2fAttribute(attribRoughMetallic)!!
                roughMetal.x = roughness
                roughMetal.y = metal
            }

            // dielectric mesh components
            metal = 0f
            roughness = 0f

            color = Color.MD_RED.toLinear()
            profile {
                simpleShape(true) {
                    xy(-2f, 0f)
                    xy(-2f, 0f)
                    xyArc(-2f, 0.3f, Vec2f(-1.8f, 0.3f), -90f, 6)
                    xyArc(-1f, 0.5f, Vec2f(-1f, 0.7f), 90f, 6)
                    xyArc(-0.8f, 1f, Vec2f(0f, 1f), -180f, 30)
                    xyArc(0.8f, 0.7f, Vec2f(1f, 0.7f), 90f, 6)
                    xyArc(1.8f, 0.5f, Vec2f(1.8f, 0.3f), -90f, 6)
                    xy(2f, 0f)
                    xy(2f, 0f)
                }

                withTransform {
                    rotate(-90f, Vec3f.X_AXIS)
                    rotate(-90f, Vec3f.Z_AXIS)
                    translate(0f, -1f, -2.2f)

                    scale(0.95f)
                    sampleAndFillBottom()
                    scale(1.015f)
                    translate(0f, 0f, -0.01f)
                    sample()
                    scale(1.05f)
                    translate(0f, 0f, -0.1f)
                    sample()
                    translate(0f, 0f, -0.1f)
                    sample()

                    translate(0f, 0f, -3f)
                    sample()

                    translate(0f, 0f, -0.1f)
                    sample()
                    scale(1 / 1.05f)
                    translate(0f, 0f, -0.1f)
                    sample()
                    scale(1 / 1.015f)
                    translate(0f, 0f, -0.01f)
                    sample()
                    scale(0.95f)
                    sampleAndFillTop(connect = true)

                }
                geometry.generateNormals()
            }

            // metallic mesh components
            metal = 1f
            roughness = 0.3f

            color = Color.MD_BLUE_GREY.toLinear()
            cylinder {
                radius = 0.6f
                height = 7f
                origin.set(0f, -6f, 0f)
            }
            cube {
                size.set(0.3f, 4f, 4.5f)
                origin.set(-1.3f, -6f, -size.z / 2f)
            }
            withTransform {
                rotate(90f, Vec3f.Z_AXIS)
                val positions = listOf(Vec2f(-2.8f, 1.4f), Vec2f(-5f, 1.4f), Vec2f(-2.8f, -1.4f), Vec2f(-5f, -1.4f))
                positions.forEach {
                    cylinder {
                        radius = 0.2f
                        height = 0.5f
                        origin.set(it.x, 0.4f, it.y)
                    }
                }
                color = Color.MD_BLUE_GREY_200.toLinear()
                cylinder {
                    radius = 1f
                    height = 18.7f
                    origin.set(-4f, 1.3f, 0f)
                }
            }
        }
        shader = makeMeshShader(ibl, aoMap, shadows)
    }

    fun makeNiceInnerLinkMesh(ibl: EnvironmentMaps, aoMap: Texture2d, shadows: List<ShadowMap>) = mesh(meshAttribs) {
        isFrustumChecked = false
        instances = MeshInstanceList(listOf(MeshInstanceList.MODEL_MAT))
        generate {
            var roughness = 0.3f
            var metal = 1f
            vertexModFun = {
                val roughMetal = getVec2fAttribute(attribRoughMetallic)!!
                roughMetal.x = roughness
                roughMetal.y = metal
            }

            color = Color.GRAY
            roughness = 0.2f
            metal = 1f
            profile {
                simpleShape(true) {
                    xyArc(0.6f, -0.3f, Vec2f(0.6f, 0f), 180f, 8)
                    xyArc(-0.6f, 0.3f, Vec2f(-0.6f, 0f), 180f, 8)
                }

                withTransform {
                    translate(0f, 0f, -0.5f)
                    sampleAndFillBottom()
                    sample(false)
                    translate(0f, 0f, 1f)
                    sample()
                    sampleAndFillTop()
                }
            }
            geometry.generateNormals()
        }
        shader = makeMeshShader(ibl, aoMap, shadows)
    }

    fun makeNiceOuterLinkMesh(ibl: EnvironmentMaps, aoMap: Texture2d, shadows: List<ShadowMap>) = mesh(meshAttribs) {
        isFrustumChecked = false
        instances = MeshInstanceList(listOf(MeshInstanceList.MODEL_MAT))
        generate {
            var roughness = 0.3f
            var metal = 1f
            vertexModFun = {
                val roughMetal = getVec2fAttribute(attribRoughMetallic)!!
                roughMetal.x = roughness
                roughMetal.y = metal
            }

            color = Color.MD_BLUE_GREY_400.toLinear()
            roughness = 0.4f
            metal = 1f
            profile {
                simpleShape(true) {
                    xyArc(1.5f, -0.4f, Vec2f(1.5f, 0f), 180f, 12)
                    xyArc(-1.5f, 0.4f, Vec2f(-1.5f, 0f), 180f, 12)
                }

                for (i in 0..1) {
                    withTransform {
                        translate(0f, 0f, -0.8f + 1.4f * i)
                        sampleAndFillBottom()
                        sample(false)
                        translate(0f, 0f, 0.2f)
                        sample()
                        sampleAndFillTop()
                    }
                }
            }
            rotate(90f, Vec3f.X_AXIS)

            color = Color.MD_BLUE_GREY_700.toLinear()
            roughness = 0.3f
            metal = 1f
            for (s in -1..1 step 2) {
                cylinder {
                    origin.set(1.5f * s, -0.9f, 0f)
                    steps = 16
                    radius = 0.18f
                    height = 1.8f
                }
            }
            geometry.generateNormals()
        }
        shader = makeMeshShader(ibl, aoMap, shadows)
    }

    private fun makeMeshShader(ibl: EnvironmentMaps, aoMap: Texture2d, shadows: List<ShadowMap>): PbrShader {
        val cfg = PbrMaterialConfig().apply {
            isInstanced = true
            shadowMaps += shadows
            useImageBasedLighting(ibl)
            useScreenSpaceAmbientOcclusion(aoMap)
        }
        val model = PbrShader.defaultPbrModel(cfg).apply {
            val ifRoughMetal: StageInterfaceNode
            vertexStage {
                ifRoughMetal = stageInterfaceNode("ifRoughMetal", attributeNode(attribRoughMetallic).output)
            }
            fragmentStage {
                findNodeByType<PbrMaterialNode>()!!.apply {
                    inRoughness = splitNode(ifRoughMetal.output, "x").output
                    inMetallic = splitNode(ifRoughMetal.output, "y").output
                }
            }
        }
        return PbrShader(cfg, model)
    }

    private fun SimpleShape.roundRectXz(centerX: Float, centerZ: Float, sizeX: Float, sizeZ: Float, r: Float, steps: Int) {
        val sx = sizeX / 2
        val sz = sizeZ / 2
        xzArc(centerX + sx, centerZ - sz + r, Vec2f(centerX + sx - r, centerZ - sz + r), -90f, steps, generateNormals = true)
        xzArc(centerX - sx + r, centerZ - sz, Vec2f(centerX - sx + r, centerZ - sz + r), -90f, steps, generateNormals = true)
        xzArc(centerX - sx, centerZ + sz - r, Vec2f(centerX - sx + r, centerZ + sz - r), -90f, steps, generateNormals = true)
        xzArc(centerX + sx - r, centerZ + sz, Vec2f(centerX + sx - r, centerZ + sz - r), -90f, steps, generateNormals = true)
    }
}