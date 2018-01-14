package de.fabmax.kool.demo

import de.fabmax.kool.loadAsset
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.scene
import de.fabmax.kool.scene.sphericalInputTransform
import de.fabmax.kool.scene.transformGroup
import de.fabmax.kool.shading.BasicShader
import de.fabmax.kool.shading.ColorModel
import de.fabmax.kool.shading.LightModel
import de.fabmax.kool.shading.basicShader
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Vec3f
import de.fabmax.kool.util.serialization.loadModel

/**
 * @author fabmax
 */

fun modelScene(): Scene = scene {
    // Add a mouse-controlled camera manipulator (actually a specialized TransformGroup)
    +sphericalInputTransform {
        translation.set(0f, 1f, 0f)
        // Set some initial rotation so that we look down on the scene
        setMouseRotation(20f, -30f)
        // Zoom in a little
        zoom = 2f
        // Add camera to the transform group
        +camera
    }

    +transformGroup {
        rotate(-90f, Vec3f.X_AXIS)

        loadAsset("player.kmf") { data ->
            val model = loadModel(data)
            model.shader = basicShader {
                lightModel = LightModel.PHONG_LIGHTING
                colorModel = ColorModel.STATIC_COLOR
            }
            (model.shader as BasicShader).staticColor.set(Color.GRAY)
            model.setActiveAnimation("Armature|walk")
            +model
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