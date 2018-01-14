package de.fabmax.kool.demo

import de.fabmax.kool.loadAsset
import de.fabmax.kool.scene.*
import de.fabmax.kool.shading.ColorModel
import de.fabmax.kool.shading.LightModel
import de.fabmax.kool.shading.basicShader
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Vec3f
import de.fabmax.kool.util.lineMesh
import de.fabmax.kool.util.serialization.loadModel

/**
 * @author fabmax
 */

fun modelScene(): Scene = scene {
    // add some sort of ground plane
    val groundExt = 20
    +colorMesh {
        generator = {
            withTransform {
                rotate(-90f, Vec3f.X_AXIS)
                color = Color.LIGHT_GRAY.withAlpha(0.1f)
                rect {
                    origin.set(-groundExt.toFloat(), -groundExt.toFloat(), 0f)
                    width = groundExt * 2f
                    height = groundExt * 2f
                }
            }
        }
    }
    +lineMesh {
        isXray = true
        for (i in -groundExt..groundExt) {
            val color = Color.LIGHT_GRAY.withAlpha(0.5f)
            addLine(Vec3f(i.toFloat(), 0f, -groundExt.toFloat()), color,
                    Vec3f(i.toFloat(), 0f, groundExt.toFloat()), color)
            addLine(Vec3f(-groundExt.toFloat(), 0f, i.toFloat()), color,
                    Vec3f(groundExt.toFloat(), 0f, i.toFloat()), color)
        }
    }

    // add animated character model
    +transformGroup {
        loadAsset("player.kmf") { data ->
            val model = loadModel(data)
            model.activeAnimation = "Armature|walk"
            model.shader = basicShader {
                lightModel = LightModel.PHONG_LIGHTING
                colorModel = ColorModel.STATIC_COLOR
                staticColor = Color.GRAY
            }
            +model

            model.onRender += { ctx ->
                // translation is in model coordinates -> front direction is -y, not z
                translate(0f, -ctx.deltaT.toFloat() * 1.2f, 0f)
                rotate(ctx.deltaT.toFloat() * 10f, Vec3f.Z_AXIS)
            }
        }

        // model uses z-axis as up-axis, rotate it accordingly
        rotate(-90f, Vec3f.X_AXIS)

        // Camera is added to the model transform group to make it move together with the model
        +sphericalInputTransform {
            // adjust transform to model coordinates
            verticalAxis = Vec3f.Z_AXIS
            minHorizontalRot = 0f
            maxHorizontalRot = 180f
            // disable panning (doesn't work as expected in nested camera transform groups)
            zoomMethod = SphericalInputTransform.ZoomMethod.ZOOM_CENTER
            rightDragMethod = SphericalInputTransform.DragMethod.NONE
            // move camera up a little so we look at the center of the model
            translation.set(0f, 0f, 1f)
            // Set some initial rotation so that we look down on the scene
            setMouseRotation(20f, 75f)
            // Zoom in a little
            resetZoom(2f)
            // Add camera to the transform group
            +camera
        }
    }

    /*val model = Model("cube").apply {
        // Set default shader properties used for all meshes in this model, which don't define own properties
        shaderFab = { basicShader {
            colorModel = ColorModel.VERTEX_COLOR
            lightModel = LightModel.PHONG_LIGHTING
        }}
        addColorGeometry {
            meshData.generator = {
                // Generate centered cube mesh with every face set to a different color
                cube {
                    colorCube()
                    centerOrigin()
                }
            }
        }

        addSubModel(Model("text").apply {
            val font = Font(FontProps(Font.SYSTEM_FONT, 72f, Font.PLAIN, 0.75f))
            shaderFab = { fontShader(font) }
            addTextGeometry {
                meshData.generator = {
                    color = Color.LIME
                    text(font) {
                        // Set the text to be rendered, for now only characters defined in [Font.STD_CHARS] can be rendered
                        text = "Shared Model"
                    }
                }
            }
        })
    }

    for (i in -1..1) {
        val inst = model.copyInstance()
        inst.translate(3f*i, -3f, 0f)
        (inst["text"] as TransformGroup).apply {
            translate(.25f, 2f + i, 0f)
            rotate(90f, Vec3f.Z_AXIS)
        }
        +inst
    }*/
}