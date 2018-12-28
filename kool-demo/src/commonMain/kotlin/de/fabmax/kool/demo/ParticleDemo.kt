package de.fabmax.kool.demo

import de.fabmax.kool.assetTexture
import de.fabmax.kool.math.*
import de.fabmax.kool.scene.defaultCamTransform
import de.fabmax.kool.scene.scene
import de.fabmax.kool.util.BillboardMesh
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ParticleSystem

fun particleDemo() = scene {
    defaultCamTransform()

    +makeGroundGrid(40)

    +ParticleSystem(assetTexture("snowflakes.png")).apply {
        // all snowflakes are white, therefore we can skip depth-sorting and -testing to get a massive speed-up
        drawOrder = BillboardMesh.DrawOrder.AS_IS
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
                        color.set(Color.WHITE)
                        //color.set(ColorGradient.JET_MD.getColor(randomF()))
                        velocity.set(0f, -randomF(0.3f, 1f), 0f)
                        angularVelocity = randomF(-45f, 45f)
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
                            // a melting snowflake is replaced after a few seconds
                            die()
                            // we could also use replaceBy() again...
                            spawnParticle(types[i])
                        }
                    })
        }

        // spawn particles
        for (i in 1..50_000) {
            spawnParticle(types[i % 25])?.apply {
                // usually snow flakes are spawned at the top, but initially we want a uniform distribution
                position.y = randomF(0.5f, 15f)
            }
        }
    }
}