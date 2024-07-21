package de.fabmax.kool.demo.physics.manyvehicles

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.DemoLoader
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.physics.*
import de.fabmax.kool.physics.geometry.PlaneGeometry
import de.fabmax.kool.physics.vehicle.Vehicle
import de.fabmax.kool.physics.vehicle.VehicleProperties
import de.fabmax.kool.physics.vehicle.VehicleUtils
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.MutableColor
import kotlin.math.cos
import kotlin.math.sin

class ManyVehiclesDemo : DemoScene("Many Vehicles") {

    private val ibl by hdriImage("${DemoLoader.hdriPath}/syferfontein_0d_clear_1k.rgbe.png")
    private val groundAlbedo by texture2d("${DemoLoader.materialPath}/tile_flat/tiles_flat_fine.png")
    private val groundNormal by texture2d("${DemoLoader.materialPath}/tile_flat/tiles_flat_fine_normal.png")

    private val physicsWorld = PhysicsWorld(mainScene)

    private val groundSimFilterData = FilterData(VehicleUtils.COLLISION_FLAG_GROUND, VehicleUtils.COLLISION_FLAG_GROUND_AGAINST)
    private val groundQryFilterData = FilterData { VehicleUtils.setupDrivableSurface(this) }

    private val numVehicles = 2048
    private val chassisInstances = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT, Attribute.COLORS), numVehicles)
    private val wheelInstances = MeshInstanceList(listOf(Attribute.INSTANCE_MODEL_MAT), numVehicles * 4)
    private val vehicleInstances = mutableListOf<VehicleInstance>()

    private val vehicleProps = VehicleProperties().apply {
        chassisDims = Vec3f(2f, 1f, 5f)
        trackWidthFront = 1.8f
        trackWidthRear = 1.8f
        gearFinalRatio = 3f
        maxCompression = 0.1f
        maxDroop = 0.1f
        springStrength = 50000f
        springDamperRate = 6000f

        wheelRadiusFront = 0.4f
        wheelWidthFront = 0.3f
        wheelMassFront = 30f
        wheelPosFront = 1.7f

        wheelRadiusRear = 0.4f
        wheelWidthRear = 0.3f
        wheelMassRear = 30f
        wheelPosRear = -1.7f

        updateChassisMoiFromDimensionsAndMass()
        updateWheelMoiFromRadiusAndMass()
    }

    override fun Scene.setupMainScene(ctx: KoolContext) {
        (camera as PerspectiveCamera).apply {
            clipNear = 1f
            clipFar = 1000f
        }

        defaultOrbitCamera().apply {
            setZoom(100.0, max = 500.0)
        }
        makeGround()

        addMesh(Attribute.POSITIONS, Attribute.NORMALS, instances = chassisInstances) {
            isFrustumChecked = false
            generate {
                cube {
                    size.set(vehicleProps.chassisDims)
                    origin.subtract(vehicleProps.chassisCMOffset)
                }
            }
            shader = chassisShader()
        }

        addColorMesh(instances = wheelInstances) {
            isFrustumChecked = false
            generate {
                color = Color.DARK_GRAY.toLinear()
                rotate(90f.deg, Vec3f.Z_AXIS)
                cylinder {
                    radius = vehicleProps.wheelRadiusFront
                    height = vehicleProps.wheelWidthFront
                }
            }
            shader = wheelsShader()
        }
        addNode(Skybox.cube(ibl.reflectionMap, 1f))

        onUpdate += {
            chassisInstances.clear()
            wheelInstances.clear()
            for (i in vehicleInstances.indices) {
                val vi = vehicleInstances[i]
                vi.addChassisInstance(chassisInstances)
                vi.addWheelInstances(wheelInstances)
            }
        }

        for (j in 0 until 8) {
            val n = 32
            val r = 200f + j * 10
            for (i in 0 until n) {
                val aDeg = (360f / n * i) + j * 17f
                val a = aDeg.toRad()
                val pos = Vec3f(sin(a) * r, 1f, cos(a) * r)
                spawnVehicle(pos, aDeg - 180f, MdColor.PALETTE[i % MdColor.PALETTE.size].toLinear())
            }
        }
    }

    private fun Scene.makeGround() {
        addTextureMesh(isNormalMapped = true, name = "ground") {
            generate {
                isCastingShadow = false
                vertexModFun = {
                    texCoord.set(x / 10f, z / 10f)
                }
                grid {
                    sizeX = 1000f
                    sizeY = 1000f
                    stepsX = sizeX.toInt() / 10
                    stepsY = sizeY.toInt() / 10
                }
            }
            shader = KslPbrShader {
                color { textureColor(groundAlbedo) }
                normalMapping { setNormalMap(groundNormal) }
                lightingCfg.imageBasedAmbientLight(ibl.irradianceMap)
                reflectionMap = ibl.reflectionMap
            }
        }

        val ground = RigidStatic().apply {
            simulationFilterData = groundSimFilterData
            queryFilterData = groundQryFilterData
            attachShape(Shape(PlaneGeometry(), Physics.defaultMaterial))
            setRotation(Mat3f.rotation(90f.deg, Vec3f.Z_AXIS))
        }
        physicsWorld.addActor(ground)
    }

    private fun spawnVehicle(pos: Vec3f, dir: Float, color: Color) {
        val vehicle = Vehicle(vehicleProps, physicsWorld)
        vehicle.position = pos
        vehicle.throttleInput = 0.75f
        vehicle.steerInput = 0f
        vehicle.setRotation(0f.deg, dir.deg, 0f.deg)
        physicsWorld.addActor(vehicle)
        vehicleInstances += VehicleInstance(vehicle, color)
    }

    private fun chassisShader() = KslPbrShader {
        vertices { isInstanced = true }
        color { instanceColor(Attribute.COLORS) }
        roughness(1f)
        lightingCfg.imageBasedAmbientLight(ibl.irradianceMap)
        reflectionMap = ibl.reflectionMap
    }

    private fun wheelsShader() = KslPbrShader {
        vertices { isInstanced = true }
        color { vertexColor() }
        roughness(0.8f)
        lighting { imageBasedAmbientLight(ibl.irradianceMap) }
        reflectionMap = ibl.reflectionMap
    }

    private inner class VehicleInstance(val vehicle: Vehicle, color: Color) {
        private val tmpMat = MutableMat4f()
        private val tmpMat2 = MutableMat4f()
        private val color = MutableColor(color)

        fun addChassisInstance(instanceData: MeshInstanceList) {
            instanceData.addInstance {
                vehicle.transform.matrixF.putTo(this)
                color.putTo(this)
            }
        }

        fun addWheelInstances(instanceData: MeshInstanceList) {
            for (i in 0..3) {
                val wheelInfo = vehicle.wheelInfos[i]
                tmpMat.set(vehicle.transform.matrixF).translate(vehicleProps.chassisCMOffset).mul(tmpMat2.set(wheelInfo.transform.matrixF))
                instanceData.addInstance {
                    tmpMat.putTo(this)
                }
            }
        }
    }
}