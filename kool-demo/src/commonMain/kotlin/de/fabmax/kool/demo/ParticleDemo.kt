package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.assetTexture
import de.fabmax.kool.math.*
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.scene
import de.fabmax.kool.scene.sphericalInputTransform
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.util.BillboardMesh
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ColorGradient
import de.fabmax.kool.util.ParticleSystem
import kotlin.math.min
import kotlin.math.round

fun particleDemo(ctx: KoolContext): List<Scene> {
    var isColored = false
    var isSorted = false
    var currentParticleCount = 0
    var maxParticleCount = 10000

    val scene = scene {
        +sphericalInputTransform {
            // Set some initial rotation so that we look down on the scene
            setMouseRotation(20f, -30f)
            // Add camera to the transform group
            +camera

            resetZoom(25f)
            translation.set(2.5f, 2.5f, 2.5f)
        }

        +makeGroundGrid(40)

        +ParticleSystem(assetTexture("snowflakes.png"), 50_000).apply {
            // disable depth buffer writing for particles
            isDepthMask = false

            val dist = CubicPointDistribution(40f)
            // snow flake texture contains 25 different snow flakes in a 5x5 grid
            val types = mutableListOf<ParticleSystem.Type>()
            // add falling snow flake types
            for (i in 0..24) {
                val texCenter = Vec2f(0.1f + (i % 5) / 5f, 0.1f + (i / 5) / 5f)
                types += ParticleSystem.Type("FallingSnowflake-$i", texCenter, Vec2f(0.2f, 0.2f),
                        init = {
                            position.set(dist.nextPoint())
                            position.y = 15f
                            val sz = randomF(0.1f, 0.2f)
                            size.set(sz, sz)
                            velocity.set(0f, -randomF(0.6f, 1.4f), 0f)
                            angularVelocity = randomF(-45f, 45f)

                            if (isColored) {
                                color.set(ColorGradient.JET_MD.getColor(randomF()))
                            } else {
                                color.set(Color.WHITE)
                            }
                        },
                        update = {
                            if (position.y < size.y / 2) {
                                // a falling snowflake is replaced by a melting snowflake as soon as it hits the ground
                                replaceBy(types[i + 25])
                                lifeTime = 0f
                            }
                        })
            }

            // add melting snow flake types
            for (i in 0..24) {
                val texCenter = Vec2f(0.1f + (i % 5) / 5f, 0.1f + (i / 5) / 5f)
                types += ParticleSystem.Type("MeltingSnowflake-$i", texCenter, Vec2f(0.2f, 0.2f),
                        init = {
                            position.y = size.y / 2
                            velocity.set(Vec3f.ZERO)
                            angularVelocity = 0f
                        },
                        update = {
                            color.a = (5f - lifeTime).clamp(0f, 1f)
                            if (lifeTime > 5f) {
                                // a melting snowflake is removed after a few seconds
                                die()
                            }
                        })
            }

            onPreRender += { ctx ->
                drawOrder = if (isSorted) BillboardMesh.DrawOrder.FAR_FIRST else BillboardMesh.DrawOrder.AS_IS
                currentParticleCount = numParticles
                if (numParticles < maxParticleCount) {
                    // spawn a few particles every frame as long as there are not enough particles
                    val spawnCount = min((maxParticleCount / 15 * ctx.deltaT).toInt(), maxParticleCount - numParticles)
                    for (i in 1..spawnCount) {
                        spawnParticle(types[randomI(0..24)])
                    }
                }
            }
        }
    }

    fun Slider.disableCamDrag() {
        onHoverEnter += { _, _, _ ->
            // disable mouse interaction on content scene while pointer is over menu
            scene.isPickingEnabled = false
        }
        onHoverExit += { _, _, _->
            // enable mouse interaction on content scene when pointer leaves menu (and nothing else in this scene
            // is hit instead)
            scene.isPickingEnabled = true
        }
    }

    val ui = uiScene(ctx.screenDpi) {
        theme = theme(UiTheme.DARK_SIMPLE) {
            componentUi { BlankComponentUi() }
            containerUi { BlankComponentUi() }
        }

        +container("menu") {
            layoutSpec.setOrigin(dps(-200f, true), zero(), zero())
            layoutSpec.setSize(dps(200f, true), pcs(100f), zero())
            ui.setCustom(SimpleComponentUi(this))

            var posY = -45f
            +label("Settings") {
                layoutSpec.setOrigin(zero(), dps(posY, true), zero())
                layoutSpec.setSize(pcs(100f), dps(40f, true), zero())
                textColor.setCustom(theme.accentColor)
            }
            +component("divider") {
                layoutSpec.setOrigin(pcs(5f), dps(posY, true), zero())
                layoutSpec.setSize(pcs(90f), dps(1f, true), zero())
                val bg = SimpleComponentUi(this)
                bg.color.setCustom(theme.accentColor)
                ui.setCustom(bg)
            }

            posY -= 35f
            +toggleButton("Colorful") {
                layoutSpec.setOrigin(dps(0f, true), dps(posY, true), zero())
                layoutSpec.setSize(pcs(100f), dps(25f, true), zero())
                isEnabled = isColored
                onStateChange += {
                    isColored = isEnabled
                }
            }

            posY -= 35f
            +toggleButton("Z-Sorted") {
                layoutSpec.setOrigin(dps(0f, true), dps(posY, true), zero())
                layoutSpec.setSize(pcs(100f), dps(25f, true), zero())
                isEnabled = isSorted
                onStateChange += {
                    isSorted = isEnabled
                }
            }

            posY -= 35f
            +label("Current:") {
                layoutSpec.setOrigin(zero(), dps(posY, true), zero())
                layoutSpec.setSize(pcs(100f), dps(25f, true), zero())
            }
            +label("currentCnt") {
                layoutSpec.setOrigin(zero(), dps(posY, true), zero())
                layoutSpec.setSize(pcs(100f), dps(25f, true), zero())
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)
                onPreRender += { text = "$currentParticleCount" }
            }

            posY -= 35f
            +label("Max:") {
                layoutSpec.setOrigin(zero(), dps(posY, true), zero())
                layoutSpec.setSize(pcs(100f), dps(25f, true), zero())
            }
            +label("maxCnt") {
                layoutSpec.setOrigin(zero(), dps(posY, true), zero())
                layoutSpec.setSize(pcs(100f), dps(25f, true), zero())
                textAlignment = Gravity(Alignment.END, Alignment.CENTER)
                onPreRender += { text = "$maxParticleCount" }
            }
            posY -= 35f
            +slider("particleCnt") {
                layoutSpec.setOrigin(dps(0f, true), dps(posY, true), zero())
                layoutSpec.setSize(pcs(100f), dps(25f, true), zero())
                setValue(1f, 50f, 10f)
                disableCamDrag()
                onValueChanged += { value ->
                    maxParticleCount = round(value).toInt() * 1000
                }
            }
        }
    }

    return listOf(scene, ui)
}