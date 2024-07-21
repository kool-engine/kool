package de.fabmax.kool.demo.physics.joints

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.GpuType
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.scene.geometry.Profile
import de.fabmax.kool.scene.geometry.SimpleShape
import de.fabmax.kool.scene.geometry.simpleShape
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.ShadowMap

object GearChainMeshGen {

    private val attribRoughness = Attribute("aRoughness", GpuType.FLOAT1)
    private val attribMetallic = Attribute("aMetallic", GpuType.FLOAT1)
    private val meshAttribs = listOf(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, attribRoughness, attribMetallic)

    fun makeNiceGearMesh(ibl: EnvironmentMaps, aoMap: Texture2d, shadows: List<ShadowMap>) = Mesh(
        meshAttribs,
        MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT))
    ).apply {
        isFrustumChecked = false
        generate {
            var roughness = 0.3f
            var metal = 1f
            vertexModFun = {
                getFloatAttribute(attribRoughness)?.f = roughness
                getFloatAttribute(attribMetallic)?.f = metal
            }

            // tooth
            profile {
                simpleShape(true) {
                    xzArc(0.7f, -0.2f, Vec2f(0.6f, -0.2f), (-90f).deg, 3)
                    xzArc(-0.6f, -0.3f, Vec2f(-0.6f, -0.2f), (-90f).deg, 3)
                    xzArc(-0.7f, 0.2f, Vec2f(-0.6f, 0.2f), (-90f).deg, 3)
                    xzArc(0.6f, 0.3f, Vec2f(0.6f, 0.2f), (-90f).deg, 3)
                }
                withTransform {
                    for (i in 0..11) {
                        withTransform {
                            rotate((i * 30f).deg, Vec3f.Z_AXIS)
                            color = MdColor.BLUE_GREY toneLin 400
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
                            color = MdColor.BLUE_GREY toneLin 200
                            sample(inverseOrientation = true)
                        }
                    }
                }
            }

            // generate normals here, everyting after this point generates accurate normals
            geometry.generateNormals()

            // bearing
            color = MdColor.BLUE_GREY toneLin 200
            withTransform {
                profile {
                    simpleShape(true) {
                        roundRectXz(0.8f, 0f, 0.4f, 1.5f, 0.1f, 3)
                    }
                    withTransform {
                        sample()
                        for (i in 0..35) {
                            rotate(0f.deg, 0f.deg, 10f.deg)
                            sample()
                        }
                    }
                }
            }

            // outer ring
            color = MdColor.BLUE_GREY toneLin 200
            profile {
                simpleShape(true) {
                    roundRectXz(6.65f, 0f, 0.6f, 2.4f, 0.2f, 3)
                }
                withTransform {
                    sample()
                    for (i in 0..71) {
                        rotate(0f.deg, 0f.deg, 5f.deg)
                        sample()
                    }
                }
            }

            // dielectric mesh components
            metal = 0f
            roughness = 0f

            // center
            color = MdColor.RED.toLinear()
            withTransform {
                profile {
                    simpleShape(true) {
                        roundRectXz(1.25f, 0f, 0.5f, 1.6f, 0.1f, 3)
                    }
                    withTransform {
                        sample()
                        for (i in 0..35) {
                            rotate(0f.deg, 0f.deg, 10f.deg)
                            sample()
                        }
                    }
                }
            }

            // spokes
            color = MdColor.RED.toLinear()
            profile {
                simpleShape(true) {
                    xzArc(0.18f, 0f, Vec2f(0f, 0f), (-315f).deg, 8, true)
                }
                for (d in -1..1 step 2) {
                    for (i in 1..10) {
                        withTransform {
                            rotate((360f.deg / 10) * i, Vec3f.Z_AXIS)
                            translate(1.2f * d, 0f, 0f)
                            rotate((-20f).deg * d, 0f.deg, (-45f).deg * d)
                            sample(connect = false)
                            for (j in 0..22) {
                                rotate(1.25f.deg * d, 0f.deg, 4f.deg * d)
                                translate(0f, 0.285f, 0f)
                                sample()
                            }
                        }
                    }
                }
            }

            // outer support ring
            color = MdColor.RED.toLinear()
            profile {
                simpleShape(false) {
                    xz(0f, 0.85f)
                    normals += MutableVec3f(0.1f, 0f, 1f).norm()
                    xzArc(0.8f, 0.5f, Vec2f(0.75f, 0.3f), (-70f).deg, 4, true)
                    xzArc(1f, -0.3f, Vec2f(0.75f, -0.3f), (-70f).deg, 4, true)
                    xz(0f, -0.85f)
                    normals += MutableVec3f(0.1f, 0f, -1f).norm()
                }
                for (i in 0..72) {
                    withTransform {
                        rotate(0f.deg, 0f.deg, (i * 5f).deg)
                        translate(-6.7f, 0f, 0f)
                        sample(inverseOrientation = true)
                    }
                }
            }
        }
        shader = makeMeshShader(ibl, aoMap, shadows)
    }

    fun makeNiceAxleMesh(ibl: EnvironmentMaps, aoMap: Texture2d, shadows: List<ShadowMap>) = Mesh(
        meshAttribs,
        MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT))
    ).apply {
        isFrustumChecked = false
        generate {
            var roughness = 0.3f
            var metal = 1f
            vertexModFun = {
                getFloatAttribute(attribRoughness)?.f = roughness
                getFloatAttribute(attribMetallic)?.f = metal
            }

            // dielectric mesh components
            metal = 0f
            roughness = 0f

            rotate((-90f).deg, Vec3f.Z_AXIS)
            rotate(90f.deg, Vec3f.Y_AXIS)

            color = MdColor.RED.toLinear()
            profile {
                simpleShape(true) {
                    xy(-2f, 0f)
                    xy(-2f, 0f)
                    xyArc(Vec2f(-2f, 0.3f), Vec2f(-1.8f, 0.3f), (-90f).deg, 6)
                    xyArc(Vec2f(-1f, 0.5f), Vec2f(-1f, 0.7f), 90f.deg, 6)
                    xyArc(Vec2f(-0.8f, 1f), Vec2f(0f, 1f), (-180f).deg, 30)
                    xyArc(Vec2f(0.8f, 0.7f), Vec2f(1f, 0.7f), 90f.deg, 6)
                    xyArc(Vec2f(1.8f, 0.5f), Vec2f(1.8f, 0.3f), (-90f).deg, 6)
                    xy(2f, 0f)
                    xy(2f, 0f)
                }

                withTransform {
                    rotate((-90f).deg, Vec3f.X_AXIS)
                    rotate((-90f).deg, Vec3f.Z_AXIS)
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

            color = MdColor.BLUE_GREY.toLinear()
            cylinder {
                radius = 0.6f
                height = 7f
                origin.set(0f, -2.5f, 0f)
            }
            cube {
                size.set(0.3f, 4f, 4.5f)
                origin.set(-1.15f, -4f, 0f)
            }
            withTransform {
                rotate(90f.deg, Vec3f.Z_AXIS)
                val positions = listOf(Vec2f(-2.8f, 1.4f), Vec2f(-5f, 1.4f), Vec2f(-2.8f, -1.4f), Vec2f(-5f, -1.4f))
                positions.forEach {
                    cylinder {
                        radius = 0.2f
                        height = 0.5f
                        origin.set(it.x, 0.65f, it.y)
                    }
                }
                color = MdColor.BLUE_GREY toneLin 200
                cylinder {
                    radius = 1f
                    height = 18.7f
                    origin.set(-4f, 10.65f, 0f)
                }
            }
        }
        shader = makeMeshShader(ibl, aoMap, shadows)
    }

    fun makeNiceInnerLinkMesh(ibl: EnvironmentMaps, aoMap: Texture2d, shadows: List<ShadowMap>) = Mesh(
        meshAttribs,
        MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT))
    ).apply {
        isFrustumChecked = false
        generate {
            var roughness = 0.3f
            var metal = 1f
            vertexModFun = {
                getFloatAttribute(attribRoughness)?.f = roughness
                getFloatAttribute(attribMetallic)?.f = metal
            }

            color = Color.GRAY
            roughness = 0.2f
            metal = 1f
            profile {
                val firstInds = mutableListOf<Int>()
                val lastInds = mutableListOf<Int>()

                fun Profile.sampleWithInds() {
                    sample()
                    firstInds += shapes[0].sampledVertIndices.first()
                    lastInds.add(0, shapes[0].sampledVertIndices.last())
                }

                simpleShape(false) {
                    xyArc(Vec2f(-0.45f, -0.5f), Vec2f(-0.45f, -0.35f), (-90f).deg, 4, true)
                    xyArc(Vec2f(-0.6f, 0.35f), Vec2f(-0.45f, 0.35f), (-90f).deg, 4, true)
                }

                withTransform {
                    rotate(90f.deg, 0f.deg, 90f.deg)
                    sampleWithInds()
                    translate(0f, 0f, -0.5f)
                    sampleWithInds()
                    for (i in 1..10) {
                        rotate((-18f).deg, Vec3f.Y_AXIS)
                        sampleWithInds()
                    }
                    translate(0f, 0f, -1f)
                    sampleWithInds()
                    for (i in 1..10) {
                        rotate((-18f).deg, Vec3f.Y_AXIS)
                        sampleWithInds()
                    }
                    translate(0f, 0f, -0.5f)
                    sampleWithInds()
                }

                fillPolygon(firstInds)
                fillPolygon(lastInds)
            }
        }
        shader = makeMeshShader(ibl, aoMap, shadows)
    }

    fun makeNiceOuterLinkMesh(ibl: EnvironmentMaps, aoMap: Texture2d, shadows: List<ShadowMap>) = Mesh(
        meshAttribs,
        MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT))
    ).apply {
        isFrustumChecked = false
        generate {
            var roughness = 0.3f
            var metal = 1f
            vertexModFun = {
                getFloatAttribute(attribRoughness)?.f = roughness
                getFloatAttribute(attribMetallic)?.f = metal
            }

            color = MdColor.BLUE_GREY toneLin 400
            roughness = 0.4f
            metal = 1f
            profile {
                val firstInds = mutableListOf<Int>()
                val lastInds = mutableListOf<Int>()

                fun Profile.sampleWithInds(connect: Boolean = true) {
                    sample(connect)
                    firstInds += shapes[0].sampledVertIndices.first()
                    lastInds.add(0, shapes[0].sampledVertIndices.last())
                }

                simpleShape(false) {
                    xyArc(Vec2f(-0.35f, -0.1f), Vec2f(-0.35f, -0.05f), (-90f).deg, 4, true)
                    xyArc(Vec2f(-0.4f, 0.05f), Vec2f(-0.35f, 0.05f), (-90f).deg, 4, true)
                }

                for (s in -1..1 step 2) {
                    firstInds.clear()
                    lastInds.clear()
                    withTransform {
                        translate(0f, 0f, 0.7f * s)
                        rotate(90f.deg, 0f.deg, 90f.deg)
                        sampleWithInds(false)
                        translate(0f, 0f, -1.5f)
                        sampleWithInds()
                        for (i in 1..10) {
                            rotate((-18f).deg, Vec3f.Y_AXIS)
                            sampleWithInds()
                        }
                        translate(0f, 0f, -3f)
                        sampleWithInds()
                        for (i in 1..10) {
                            rotate((-18f).deg, Vec3f.Y_AXIS)
                            sampleWithInds()
                        }
                        translate(0f, 0f, -1.5f)
                        sampleWithInds()
                    }
                    fillPolygon(firstInds)
                    fillPolygon(lastInds)
                }
            }
            rotate(90f.deg, Vec3f.X_AXIS)

            color = MdColor.BLUE_GREY toneLin 700
            roughness = 0.3f
            metal = 1f
            for (s in -1..1 step 2) {
                cylinder {
                    origin.set(1.5f * s, 0f, 0f)
                    steps = 16
                    radius = 0.18f
                    height = 1.8f
                }
            }
            //geometry.generateNormals()
        }
        shader = makeMeshShader(ibl, aoMap, shadows)
    }

    private fun makeMeshShader(ibl: EnvironmentMaps, aoMap: Texture2d, shadows: List<ShadowMap>) = KslPbrShader {
        color { vertexColor() }
        vertices { isInstanced = true }
        roughness { vertexProperty(attribRoughness) }
        metallic { vertexProperty(attribMetallic) }
        enableSsao(aoMap)
        lighting {
            addShadowMaps(shadows)
            imageBasedAmbientLight(ibl.irradianceMap)
        }
        reflectionMap = ibl.reflectionMap
    }

    private fun SimpleShape.roundRectXz(centerX: Float, centerZ: Float, sizeX: Float, sizeZ: Float, r: Float, steps: Int) {
        val sx = sizeX / 2
        val sz = sizeZ / 2
        xzArc(centerX + sx, centerZ - sz + r, Vec2f(centerX + sx - r, centerZ - sz + r), (-90f).deg, steps, generateNormals = true)
        xzArc(centerX - sx + r, centerZ - sz, Vec2f(centerX - sx + r, centerZ - sz + r), (-90f).deg, steps, generateNormals = true)
        xzArc(centerX - sx, centerZ + sz - r, Vec2f(centerX - sx + r, centerZ + sz - r), (-90f).deg, steps, generateNormals = true)
        xzArc(centerX + sx - r, centerZ + sz, Vec2f(centerX + sx - r, centerZ + sz - r), (-90f).deg, steps, generateNormals = true)
    }
}