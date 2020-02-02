package de.fabmax.kool.util

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.scene.BillboardMesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene

class ParticleSystem(particleTex: Texture, val maxParticles: Int = 10_000, name: String? = null) : Node() {

    private val mesh = BillboardMesh(name)

    private val particles = Array(maxParticles) { Particle(it, TYPE_DEAD) }
    private val sortedIndices = mutableListOf<ParticleIndex>()

    var numParticles = 0
        private set
    var drawOrder = BillboardMesh.DrawOrder.FAR_FIRST
    var isDepthMask = true

    init {
        mesh.parent = this
        // don't let billboard mesh do any z-sorting, instead ParticleSystem does the sorting, this way only alive particles can be sorted
        mesh.drawOrder = BillboardMesh.DrawOrder.AS_IS
        // mesh data is frequently updated
        //mesh.meshData.usage = GL_DYNAMIC_DRAW
        // disable frustum checking, since particle bounds are not exactly known (and expensive to compute)
        isFrustumChecked = false
        mesh.isFrustumChecked = false

        //(mesh.shader as BasicShader).texture = particleTex

        particles.forEach {
            mesh.addQuad(it.position, it.size)
        }
    }

    fun spawnParticle(type: Type): Particle? {
        if (numParticles == maxParticles) {
            logW { "Maximum number of particles reached" }
            return null
        }

        val p = particles[numParticles++]
        p.replaceBy(type)
        return p
    }

    override fun onSceneChanged(oldScene: Scene?, newScene: Scene?) {
        super.onSceneChanged(oldScene, newScene)
        mesh.scene = newScene
    }

    override fun preRender(ctx: KoolContext) {
        super.preRender(ctx)

        //timedMs("particles update took") {
            for (i in 0 until numParticles) {
                particles[i].update(ctx)
            }
            if (drawOrder != BillboardMesh.DrawOrder.AS_IS) {
                zSortParticles()
            }
        //}

        mesh.preRender(ctx)
    }

    override fun render(ctx: KoolContext) {
        super.render(ctx)
        if (isRendered) {
            var restoreAttribs = false
//            if (isDepthMask != ctx.isDepthMask) {
//                ctx.pushAttributes()
//                ctx.isDepthMask = isDepthMask
//                ctx.applyAttributes()
//                restoreAttribs = true
//            }
//
//            mesh.render(ctx)
//
//            if (restoreAttribs) {
//                ctx.popAttributes()
//            }
        }
    }

    override fun postRender(ctx: KoolContext) {
        super.postRender(ctx)
        mesh.postRender(ctx)
    }

    override fun dispose(ctx: KoolContext) {
        super.dispose(ctx)
        mesh.dispose(ctx)
    }

    private fun zSortParticles() {
        val camPos = scene?.camera?.globalPos ?: return

        setupSortList()

        val s = if (drawOrder == BillboardMesh.DrawOrder.FAR_FIRST) -1 else 1
        sortedIndices.sortBy { idx ->
            particles[idx.listIndex].position.sqrDistance(camPos) * s
        }

        mesh.clearIndices()
        for (i in sortedIndices.indices) {
            val idx = sortedIndices[i]
            mesh.addQuadIndex(idx.meshIndex)
            particles[idx.listIndex].drawIndex = i
        }
        mesh.geometry.isSyncRequired = true

        // swap particles into draw order, will make following sort operations much faster (assuming that order won't
        // drastically change between consecutive frames)
        for (i in 0 until numParticles) {
            val drawIdx = particles[i].drawIndex
            if (drawIdx != i) {
                // swapping particles by value is actually faster than swapping object references (data locality!)
                particles[i].swap(particles[drawIdx])
            }
        }
    }

    private fun setupSortList() {
        if (sortedIndices.size < numParticles) {
            for (i in 1..(numParticles - sortedIndices.size)) {
                sortedIndices.add(ParticleIndex(0, 0))
            }
        } else if (sortedIndices.size > numParticles) {
            for (i in 1..(sortedIndices.size - numParticles)) {
                sortedIndices.removeAt(sortedIndices.lastIndex)
            }
        }
        for (i in 0 until numParticles) {
            sortedIndices[i].listIndex = i
            sortedIndices[i].meshIndex = particles[i].meshIndex
        }
    }

    private fun Particle.update(ctx: KoolContext) {
        type.update(this, ctx)

        lifeTime += ctx.deltaT
        position.x += velocity.x * ctx.deltaT
        position.y += velocity.y * ctx.deltaT
        position.z += velocity.z * ctx.deltaT
        rotation += angularVelocity * ctx.deltaT

        mesh.updateQuad(meshIndex, position, size, rotation, type.texCenter, type.texSize, color)

        if (lifeTime > type.maxLifeTime) {
            die()
        }
    }

    private data class ParticleIndex(var meshIndex: Int, var listIndex: Int)

    data class Type(val name: String, val texCenter: Vec2f, val texSize: Vec2f, val maxLifeTime: Float = Float.MAX_VALUE, val init: Particle.() -> Unit, val update: Particle.(KoolContext) -> Unit = { })

    inner class Particle(index: Int, type: Type) {
        var type = type
            private set
        var meshIndex = index
            private set

        internal var drawIndex = 0

        var lifeTime = 0f
        val position = MutableVec3f()
        val size = MutableVec2f()
        var rotation = 0f
        val color = MutableColor(Color.WHITE)
        val velocity = MutableVec3f()
        var angularVelocity = 0f

        fun die() {
            replaceBy(TYPE_DEAD)

            if (numParticles > 1) {
                swap(particles[numParticles-1])
            }
            numParticles--
        }

        fun replaceBy(newType: Type) {
            type = newType
            newType.init(this)
            mesh.updateQuad(meshIndex, position, size, rotation, type.texCenter, type.texSize, color)
        }

        internal fun swap(other: Particle) {
            type = other.type.also { other.type = type }
            meshIndex = other.meshIndex.also { other.meshIndex = meshIndex }
            drawIndex = other.drawIndex.also { other.drawIndex = drawIndex }

            lifeTime = other.lifeTime.also { other.lifeTime = lifeTime }
            position.swap(other.position)
            size.swap(other.size)
            rotation = other.rotation.also { other.rotation = rotation }
            color.swap(other.color)
            velocity.swap(other.velocity)
            angularVelocity = other.angularVelocity.also { other.angularVelocity = angularVelocity }
        }

        private fun MutableVec2f.swap(other: MutableVec2f) {
            x = other.x.also { other.x = x }
            y = other.y.also { other.y = y }
        }

        private fun MutableVec3f.swap(other: MutableVec3f) {
            x = other.x.also { other.x = x }
            y = other.y.also { other.y = y }
            z = other.z.also { other.z = z }
        }

        private fun MutableColor.swap(other: MutableColor) {
            r = other.r.also { other.r = r }
            g = other.g.also { other.g = g }
            b = other.b.also { other.b = b }
            a = other.a.also { other.a = a }
        }
    }

    companion object {
        private val TYPE_DEAD = Type("dead", Vec2f.ZERO, Vec2f.ZERO, init = {
            // reset all particle properties to default values
            lifeTime = 0f
            position.set(Vec3f.ZERO)
            size.set(Vec2f.ZERO)
            rotation = 0f
            color.set(Color.WHITE)
            velocity.set(Vec3f.ZERO)
            angularVelocity = 0f
        })
    }
}