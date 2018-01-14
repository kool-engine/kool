package de.fabmax.kool.demo

import de.fabmax.kool.loadAsset
import de.fabmax.kool.math.clamp
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.shading.ColorModel
import de.fabmax.kool.shading.LightModel
import de.fabmax.kool.shading.basicShader
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Vec3f
import de.fabmax.kool.util.lineMesh
import de.fabmax.kool.util.serialization.loadModel
import kotlin.math.round
import kotlin.math.sqrt

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
        var asyncModel: Model? = null
        var movementSpeed = 0.25f
        var slowMotion = 1f

        loadAsset("player.kmf") { data ->
            val model = loadModel(data)
            asyncModel = model
            model.armatures[0].getAnimation("Armature|walk")?.weight = 1f
            model.shader = basicShader {
                lightModel = LightModel.PHONG_LIGHTING
                colorModel = ColorModel.STATIC_COLOR
                staticColor = Color.GRAY
            }
            +model

            model.onRender += { ctx ->
                // translation is in model coordinates -> front direction is -y, not z
                val dt = ctx.deltaT.clamp(0.0, 0.1).toFloat()
                translate(0f, -dt * movementSpeed * slowMotion * 5f, 0f)
                rotate(dt * movementSpeed * slowMotion * 50f, Vec3f.Z_AXIS)
                model.armatures[0].animationSpeed = slowMotion
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
            translation.set(0.5f, 0f, 1f)
            // Set some initial rotation so that we look down on the scene
            setMouseRotation(20f, 75f)
            // Zoom in a little
            resetZoom(2f)
            // Add camera to the transform group
            +camera
        }

        +embeddedUi(dps(400f)) {
            globalWidth = 0.75f
            globalHeight = 1f

            content.apply {
                rotate(90f, Vec3f.X_AXIS)
                translate(0.5f, 1.2f, 0f)

                +Label("label1", root).apply {
                    layoutSpec.setOrigin(uns(0f), dps(140f), uns(0f))
                    layoutSpec.setSize(pcs(75f), dps(35f), uns(0f))
                    textAlignment = Gravity(Alignment.START, Alignment.END)
                    padding.bottom = dps(4f)
                    text = "Movement Speed:"
                }
                val speedLabel = Label("speedLabel", root).apply {
                    layoutSpec.setOrigin(pcs(75f), dps(140f), uns(0f))
                    layoutSpec.setSize(pcs(25f), dps(35f), uns(0f))
                    textAlignment = Gravity(Alignment.START, Alignment.END)
                    padding.bottom = dps(4f)
                    text = formatFloat(movementSpeed)
                }
                +speedLabel
                +Slider("speedSlider", 0.0f, 1f, sqrt(movementSpeed), root).apply {
                    layoutSpec.setOrigin(uns(0f), dps(90f), uns(0f))
                    layoutSpec.setSize(pcs(100f), dps(50f), uns(0f))
                    onValueChanged += { value ->
                        movementSpeed = value * value

                        val armature = asyncModel?.armatures?.get(0)
                        if (armature != null) {
                            val idleWeight = (1f - value * 2f).clamp(0f, 1f)
                            val runWeight = ((value - 0.5f) * 2f).clamp(0f, 1f)
                            val walkWeight = when {
                                runWeight > 0f -> 1f - runWeight
                                else -> 1f - idleWeight
                            }
                            speedLabel.text = formatFloat(value)

                            armature.getAnimation("Armature|idle")?.weight = idleWeight
                            armature.getAnimation("Armature|walk")?.weight = walkWeight
                            armature.getAnimation("Armature|run")?.weight = runWeight
                        }
                    }
                }

                +Label("label2", root).apply {
                    layoutSpec.setOrigin(uns(0f), dps(50f), uns(0f))
                    layoutSpec.setSize(pcs(75f), dps(40f), uns(0f))
                    textAlignment = Gravity(Alignment.START, Alignment.END)
                    padding.bottom = dps(4f)
                    text = "Slow Motion:"
                }
                val slowMoLabel = Label("slowMotion", root).apply {
                    layoutSpec.setOrigin(pcs(75f), dps(50f), uns(0f))
                    layoutSpec.setSize(pcs(25f), dps(40f), uns(0f))
                    textAlignment = Gravity(Alignment.START, Alignment.END)
                    padding.bottom = dps(4f)
                    text = formatFloat(slowMotion)
                }
                +slowMoLabel
                +Slider("slowMoSlider", 0.0f, 1f, slowMotion, root).apply {
                    layoutSpec.setOrigin(uns(0f), uns(0f), uns(0f))
                    layoutSpec.setSize(pcs(100f), dps(50f), uns(0f))
                    onValueChanged += { value ->
                        slowMotion = value
                        slowMoLabel.text = formatFloat(slowMotion)
                    }
                }
            }
        }
    }
}

private fun formatFloat(value: Float): String {
    val i = round(value.clamp(0f, 1f) * 100).toInt()
    val str = when {
        i < 10 -> "0.0$i"
        else -> "${i / 100}.${i % 100}0"
    }
    return str.substring(0, str.indexOf('.') + 3)
}
