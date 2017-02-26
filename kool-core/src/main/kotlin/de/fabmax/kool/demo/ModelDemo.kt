package de.fabmax.kool.demo

import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.scene.Model
import de.fabmax.kool.scene.TransformGroup
import de.fabmax.kool.scene.group
import de.fabmax.kool.scene.sphericalInputTransform
import de.fabmax.kool.shading.ColorModel
import de.fabmax.kool.shading.LightModel
import de.fabmax.kool.shading.ShaderProps
import de.fabmax.kool.shading.basicShader
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Font
import de.fabmax.kool.util.Vec3f
import de.fabmax.kool.util.fontShader

/**
 * @author fabmax
 */

fun modelDemo(ctx: RenderContext) {
    // Create scene contents
    ctx.scene.root = group {
        // Add a mouse-controlled camera manipulator (actually a specialized TransformGroup)
        +sphericalInputTransform {
            // Set some initial rotation so that we look down on the scene
            setRotation(20f, -30f)
            // Add camera to the transform group
            +ctx.scene.camera
        }

        val model = Model("cube").apply {
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
                val textFont = Font("sans-serif", 72f, 0.75f)
                shaderFab = { fontShader(textFont) }
                addTextGeometry {
                    meshData.generator = {
                        color = Color.LIME
                        text(textFont) {
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
        }
    }
    ctx.run()
}