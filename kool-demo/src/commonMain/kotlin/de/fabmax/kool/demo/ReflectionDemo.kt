package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.assetTextureCubeMap
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.scene.*
import de.fabmax.kool.shading.ColorModel
import de.fabmax.kool.shading.LightModel
import de.fabmax.kool.shading.basicShader
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.EnvironmentMapRenderer


fun reflectionDemo(ctx: KoolContext): List<Scene> {
    val reflectedObjects = mutableListOf<Node>()

    val mainScene = scene {
        val envRenderer = EnvironmentMapRenderer().apply { origin.set(0f, 3f, 0f) }

        // setup camera
        +sphericalInputTransform {
            panMethod = yPlanePan()
            translationBounds = BoundingBox(Vec3f(-20f, 0f, -20f), Vec3f(20f, 0f, 20f))
            // Set some initial rotation so that we look down on the scene
            setMouseRotation(20f, -15f)
            // Add camera to the transform group
            +camera
            minZoom = 1f
            maxZoom = 40f
            resetZoom(20f)
        }

        +makeGroundGrid(100).also { reflectedObjects += it }

        val mesh = colorMesh {
            generator = {
                sphere {
                    center.set(0f, 3f, 0f)
                    radius = 2.5f
                    steps = 100
                }
            }
        }
        mesh.shader = basicShader {
            colorModel = ColorModel.STATIC_COLOR
            lightModel = LightModel.PHONG_LIGHTING
            staticColor = Color.MD_ORANGE
            isEnvironmentMapped = true
            reflectivity = 0.75f
            environmentMap = envRenderer.environmentMap
        }
        +mesh

        +transformGroup {
            reflectedObjects += this
            +colorMesh {
                generator = {
                    withTransform {
                        color = Color.MD_PINK
                        translate(-8f, 1f, -8f)
                        rotate(18f, Vec3f.Y_AXIS)
                        cube {
                            size.set(3f, 4f, 3f)
                            origin.set(-size.x/2f, -size.y/2f, 0f)
                        }
                    }
                    withTransform {
                        color = Color.MD_LIGHT_BLUE
                        translate(-8f, 1f, 8f)
                        rotate(36f, Vec3f.Y_AXIS)
                        cube {
                            size.set(4f, 3f, 3f)
                            origin.set(-size.x/2f, -size.y/2f, 0f)
                        }
                    }
                    withTransform {
                        color = Color.MD_LIME
                        translate(8f, 1f, 8f)
                        rotate(54f, Vec3f.Y_AXIS)
                        cube {
                            size.set(3f, 3f, 4f)
                            origin.set(-size.x/2f, -size.y/2f, 0f)
                        }
                    }
                    withTransform {
                        color = Color.MD_DEEP_PURPLE
                        translate(8f, 1f, -8f)
                        rotate(72f, Vec3f.Y_AXIS)
                        cube {
                            size.set(3f, 3f, 3f)
                            origin.set(-size.x/2f, -size.y/2f, 0f)
                        }
                    }
                }
            }
            onPreRender += {
                rotate(ctx.deltaT * 90f, Vec3f.Y_AXIS)
            }
        }

        onPreRender += { ctx ->
            envRenderer.update(reflectedObjects, ctx)
        }
    }

    val skybox = Skybox(mainScene.camera, assetTextureCubeMap("skybox/y-up/sky_ft.jpg", "skybox/y-up/sky_bk.jpg",
            "skybox/y-up/sky_lt.jpg", "skybox/y-up/sky_rt.jpg",
            "skybox/y-up/sky_up.jpg", "skybox/y-up/sky_dn.jpg"))
    reflectedObjects += skybox

    return listOf(mainScene, skybox)
}
