package de.fabmax.kool.demo

import de.fabmax.kool.AssetManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Mat3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.spatial.BoundingBox
import de.fabmax.kool.modules.ksl.blinnPhongShader
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.physics.*
import de.fabmax.kool.physics.geometry.BoxGeometry
import de.fabmax.kool.physics.geometry.HeightField
import de.fabmax.kool.physics.geometry.HeightFieldGeometry
import de.fabmax.kool.physics.geometry.PlaneGeometry
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.CullMethod
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.ibl.EnvironmentHelper
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.CascadedShadowMap
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.HeightMap
import de.fabmax.kool.util.MdColor

class HeightMapTest : DemoScene("Height Map Test") {

    private lateinit var heightMap: HeightMap
    private lateinit var colorTex: Texture2d
    private lateinit var normalTex: Texture2d
    private lateinit var ibl: EnvironmentMaps

    private lateinit var heightFieldBody: RigidStatic
    private val dynBodies = mutableListOf<RigidDynamic>()

    private val bodySize = 0.3f

    override suspend fun AssetManager.loadResources(ctx: KoolContext) {
        heightMap = HeightMap.fromRawData(loadAsset("${Demo.heightMapPath}/terrain.raw")!!, 20f)
        // more or less the same, but falls back to 8-bit height-resolution in javascript
        //heightMap = HeightMap.fromTextureData2d(loadTextureData2d("${Demo.heightMapPath}/terrain.png", TexFormat.R_F16), 20f)

        colorTex = loadAndPrepareTexture("${Demo.pbrBasePath}/tile_flat/tiles_flat_fine.png")
        normalTex = loadAndPrepareTexture("${Demo.pbrBasePath}/tile_flat/tiles_flat_fine_normal.png")

        ibl = EnvironmentHelper.hdriEnvironment(mainScene, "${Demo.envMapBasePath}/blaubeuren_outskirts_1k.rgbe.png", this)

        Physics.awaitLoaded()
        val world = PhysicsWorld(isContinuousCollisionDetection = true)
        world.registerHandlers(mainScene)
        world.gravity = Vec3f(0f, -1f, 0f)

        val heightField = HeightField(heightMap, 0.1f, 0.1f)
        val hfGeom = HeightFieldGeometry(heightField)
        val hfBounds = hfGeom.getBounds(BoundingBox())
        heightFieldBody = RigidStatic()
        heightFieldBody.attachShape(Shape(hfGeom, Physics.defaultMaterial))
        heightFieldBody.position = Vec3f(hfBounds.size.x * -0.5f, 0f, hfBounds.size.z * -0.5f)
        world.addActor(heightFieldBody)

        val groundPlane = RigidStatic()
        groundPlane.attachShape(Shape(PlaneGeometry(), Physics.defaultMaterial))
        groundPlane.position = Vec3f(0f, -5f, 0f)
        groundPlane.setRotation(Mat3f().rotate(90f, Vec3f.Z_AXIS))
        world.addActor(groundPlane)

        for (x in -15..15) {
            for (z in -15..15) {
                val shape = BoxGeometry(Vec3f(bodySize))
                val body = RigidDynamic(100f)
                body.attachShape(Shape(shape, Physics.defaultMaterial))
                body.position = Vec3f(x * 0.7f, 10f, z * 0.7f)
                world.addActor(body)
                dynBodies += body
            }
        }
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        defaultCamTransform().apply { maxZoom = 50.0 }

        lighting.apply {
            singleLight {
                setDirectional(Vec3f(-1f, -1f, -1f))
                setColor(Color.WHITE, 1f)
            }
        }
        val shadowMap = CascadedShadowMap(this@setupMainScene, 0).apply {
            setMapRanges(0.05f, 0.25f, 1f)
        }

        // height map / ground mesh
        +textureMesh(isNormalMapped = true) {
            generate {
                vertexModFun = {
                    texCoord.set(texCoord.x * 25f, texCoord.y * 25f)
                }
                val shape = heightFieldBody.shapes[0]
                withTransform {
                    transform.set(heightFieldBody.transform).mul(shape.localPose)
                    shape.geometry.generateMesh(this)
                }
            }

            shader = blinnPhongShader {
                color {
                    addTextureColor(colorTex)
                }
                normalMapping {
                    setNormalMap(normalTex)
                }
                shadow {
                    addShadowMap(shadowMap)
                }
                pipeline {
                    cullMethod = CullMethod.NO_CULLING
                }
                imageBasedAmbientColor(ibl.irradianceMap, Color.GRAY)
                specularStrength = 0.5f
                colorSpaceConversion = ColorSpaceConversion.LINEAR_TO_sRGB_HDR
            }
        }

        // dynamic bodies
        +colorMesh {
            generate {
                color = MdColor.LIGHT_BLUE.toLinear()
                cube {
                    size.set(Vec3f(bodySize))
                    centered()
                }
            }
            shader = blinnPhongShader {
                color {
                    addVertexColor()
                }
                shadow {
                    addShadowMap(shadowMap)
                }
                imageBasedAmbientColor(ibl.irradianceMap, Color.GRAY)
                isInstanced = true
                specularStrength = 0.5f
                colorSpaceConversion = ColorSpaceConversion.LINEAR_TO_sRGB_HDR
            }

            instances = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT))
            onUpdate += {
                instances!!.apply {
                    clear()
                    addInstances(dynBodies.size) { buf ->
                        dynBodies.forEach { body ->
                            buf.put(body.transform.matrix)
                        }
                    }
                }
            }
        }
    }
}