package de.fabmax.kool.demo

import de.fabmax.kool.currentTimeMillis
import de.fabmax.kool.math.random
import de.fabmax.kool.util.*
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.pow

class TreeGenerator {

    var radiusOfInfluence = 1.0f
    var growDistance = 0.15f
    var killDistance = 1.5f
    var numberOfAttractionPoints = 3000

    val actualKillDistance: Float
        get() = growDistance * killDistance

    private val attractionPoints = mutableListOf<AttractionPoint>()
    private var attractionPointsTree = pointTree(listOf<AttractionPoint>())
    private val nearAttractionPoints = mutableListOf<AttractionPoint>()
    private val treeNodes = mutableListOf<TreeNode>()

    private var root = TreeNode()

    fun seedTree() {
        populateAttractionPoints()

        treeNodes.clear()
        root = TreeNode()
        treeNodes += root

        var prev = root
        var y = growDistance
        while (y < 1f) {
            val newNd = TreeNode()
            newNd.y = y
            y += growDistance
            prev.addChild(newNd)
            treeNodes += newNd
            prev = newNd
        }
    }

    fun generate(maxIterations: Int = 1000) {
        val t = currentTimeMillis()
        seedTree()
        var i = 0
        while (i++ < maxIterations) {
            if (!growSingleStep()) {
                break
            }
        }
        println("Generation done, took $i iterations, ${treeNodes.size} nodes, took ${currentTimeMillis() - t} ms")
        finalizeTree()
    }

    fun growSingleStep(): Boolean {
        // clear nearest
        attractionPoints.forEach { it.nearestNode = null }

        // find current nearest
        for (node in treeNodes) {
            node.influencingPts.clear()
            if (!node.isFinished) {
                attractionPointsTree.inRadius(nearAttractionPoints, node, radiusOfInfluence)
                for (attracPt in nearAttractionPoints) {
                    if (attracPt.isOpen) {
                        attracPt.checkNearest(node)
                    }
                }
            }
        }

        // collect influencing attraction points
        for (attracPt in attractionPoints) {
            if (attracPt.isOpen) {
                attracPt.nearestNode?.influencingPts?.add(attracPt)
            }
        }

        val newNodes = mutableListOf<TreeNode>()
        var changed = false
        for (node in treeNodes) {
            if (!node.influencingPts.isEmpty()) {
                val growDir = MutableVec3f()
                for (attracPt in node.influencingPts) {
                    growDir += attracPt.subtract(node, MutableVec3f()).norm()
                }
                growDir.norm().scale(growDistance)

                val newNode = TreeNode()
                newNode.set(node).add(growDir)
                if (!node.containsChild(newNode)) {
                    node.addChild(newNode)
                    newNodes += newNode

                    attractionPointsTree.inRadius(nearAttractionPoints, newNode, actualKillDistance)
                    nearAttractionPoints.forEach { it.isOpen = false }
                    changed = true
                }
            } else {
                node.isFinished = true
            }
        }
        treeNodes.addAll(newNodes)
        return changed
    }

    fun finalizeTree() {
        root.forEachTopDown {
            if (parent != null) {
                // shift nodes towards base to make branches nicer
                this += MutableVec3f(parent!!).subtract(this).norm().scale(growDistance * 0.5f)

                // add a small random offset
                x += (random().toFloat() - 0.5f) * 0.02f
                y += (random().toFloat() - 0.5f) * 0.02f
                z += (random().toFloat() - 0.5f) * 0.02f
            }
            computeTrunkRadiusAndDepth()
            computeCircumPoints()
        }

        root.forEachBottomUp {
            if (parent != null) {
                val baseV = parent!!.texV
                texV = baseV + distance(parent!!) / (radius * 2f * PI.toFloat() * 1.5f)
            }
        }
    }

    fun buildTrunkMesh(target: MeshBuilder) {
        treeNodes.forEach { it.buildTrunkMesh(target) }
    }

    fun buildLeafMesh(target: MeshBuilder) {
        treeNodes.forEach { it.buildLeafMesh(target) }
    }

    private fun populateAttractionPoints() {
        val center = Vec3f(0f, 3f, 0f)
        val radius = 2f
        attractionPoints.clear()
        for (i in 1..numberOfAttractionPoints) {
            attractionPoints += AttractionPoint().apply {
                var l = radius * 2
                while (l > radius) {
                    x = (random().toFloat() - 0.5f) * 2f * radius
                    y = (random().toFloat() - 0.5f) * 2f * radius
                    z = (random().toFloat() - 0.5f) * 2f * radius
                    l = length()
                }
                add(center)
            }
        }
        attractionPointsTree = pointTree(attractionPoints)
    }

    private class AttractionPoint : MutableVec3f() {
        var nearestNode: TreeNode? = null
            set(value) {
                field = value
                if (value == null) {
                    nearestNodeDist = Float.POSITIVE_INFINITY
                }
            }

        var nearestNodeDist = Float.POSITIVE_INFINITY
            private set

        var isOpen = true

        var vertexIdx = 0

        fun checkNearest(node: TreeNode) {
            val dist = distance(node)
            if (dist < nearestNodeDist) {
                nearestNode = node
                nearestNodeDist = dist
            }
        }
    }

    private class TreeNode : MutableVec3f() {
        val children = mutableListOf<TreeNode>()
        var parent: TreeNode? = null
        var branchDepth = 0

        val influencingPts = mutableListOf<AttractionPoint>()
        var isFinished = false

        var radius = 0.005f
        var texV = 0f
        val circumPts = mutableListOf<Vec3f>()

        fun addChild(node: TreeNode) {
            children += node
            node.parent = this
        }

        fun containsChild(node: TreeNode): Boolean {
            for (c in children) {
                if (c.isEqual(node)) {
                    return true
                }
            }
            return false
        }

        fun forEachBottomUp(block: TreeNode.() -> Unit) {
            this.block()
            children.forEach { it.forEachBottomUp(block) }
        }

        fun forEachTopDown(block: TreeNode.() -> Unit) {
            children.forEach { it.forEachTopDown(block) }
            this.block()
        }

        fun computeTrunkRadiusAndDepth() {
            val p = 2.25
            if (children.isEmpty()) {
                radius = 0.01f
                branchDepth = 0

            } else {
                radius = children.sumByDouble { it.radius.toDouble().pow(p) }.pow(1.0 / p).toFloat()
                branchDepth = if (children.size == 1) {
                    children[0].branchDepth
                } else {
                    (children.maxBy { it.branchDepth }?.branchDepth ?: 0) + 1
                }
            }
        }

        fun computeCircumPoints() {
            circumPts.clear()
            val n = if (parent != null) {
                subtract(parent!!, MutableVec3f()).norm()
            } else {
                children[0].subtract(this, MutableVec3f()).norm()
            }

            val c = MutableVec3f(n).scale(-(n * Z_AXIS)).add(Z_AXIS).norm().scale(radius)
            c.rotate(-atan2(c.z, c.x), n)
            for (i in 0 until 8) {
                val pt = MutableVec3f(c).add(this)
                circumPts.add(Vec3f(pt))
                c.rotate(360f / 8, n)
            }
        }

        fun buildTrunkMesh(target: MeshBuilder) {
            val idcs = mutableListOf<Int>()
            if (parent != null) {
                if (children.isEmpty()) {
                    val tipIdx = target.meshData.addVertex {
                        position.set(this@TreeNode)
                        this@TreeNode.subtract(parent!!, normal).norm()
                        texCoord.set(0f, texV)
                    }
                    for (i in 0..8) {
                        idcs += target.meshData.addVertex {
                            position.set(parent!!.circumPts[i%8])
                            parent!!.circumPts[i%8].subtract(parent!!, normal).norm()
                            texCoord.set(i / 8f, parent!!.texV)
                        }
                    }
                    for (i in 0 until 8) {
                        target.meshData.addTriIndices(tipIdx, idcs[i], idcs[i + 1])
                    }

                } else {
                    for (i in 0..8) {
                        idcs += target.meshData.addVertex {
                            position.set(circumPts[i%8])
                            circumPts[i%8].subtract(this@TreeNode, normal).norm()
                            texCoord.set(i / 8f, texV)
                        }
                        idcs += target.meshData.addVertex {
                            position.set(parent!!.circumPts[i%8])
                            parent!!.circumPts[i%8].subtract(parent!!, normal).norm()
                            texCoord.set(i / 8f, parent!!.texV)
                        }
                    }
                    for (i in 0 until 8) {
                        target.meshData.addTriIndices(idcs[i * 2], idcs[i * 2 + 1], idcs[i * 2 + 2])
                        target.meshData.addTriIndices(idcs[i * 2 + 1], idcs[i * 2 + 3], idcs[i * 2 + 2])
                    }
                }
            }
        }

        fun buildLeafMesh(target: MeshBuilder) {
            if (branchDepth <= 1 && parent != null) {
                // add leafs
                val n = this@TreeNode.subtract(parent!!, MutableVec3f())
                val len = n.length()
                n.norm()
                for (i in 1..20) {
                    target.withTransform {
                        val r = MutableVec3f(circumPts[0]).subtract(this@TreeNode).norm().scale(radius + random().toFloat() * 0.15f)
                        r.rotate(random().toFloat() * 360, n)
                        val p = MutableVec3f(n).scale(random().toFloat() * len).add(r).add(this@TreeNode)

                        translate(p)
                        rotate(random().toFloat() * 360, n)

                        var i0 = vertex(Vec3f(0f, -0.022f, 0f), NEG_Z_AXIS, Vec2f(0f, 0f))
                        var i1 = vertex(Vec3f(0f, 0.022f, 0f), NEG_Z_AXIS, Vec2f(0f, 1f))
                        var i2 = vertex(Vec3f(0.1f, 0.022f, 0f), NEG_Z_AXIS, Vec2f(1f, 1f))
                        var i3 = vertex(Vec3f(0.1f, -0.022f, 0f), NEG_Z_AXIS, Vec2f(1f, 0f))
                        meshData.addIndices(i0, i1, i2, i0, i2, i3)
                        i0 = vertex(Vec3f(0f, -0.022f, 0f), Z_AXIS, Vec2f(0f, 0f))
                        i1 = vertex(Vec3f(0f, 0.022f, 0f), Z_AXIS, Vec2f(0f, 1f))
                        i2 = vertex(Vec3f(0.1f, 0.022f, 0f), Z_AXIS, Vec2f(1f, 1f))
                        i3 = vertex(Vec3f(0.1f, -0.022f, 0f), Z_AXIS, Vec2f(1f, 0f))
                        meshData.addIndices(i0, i2, i1, i0, i3, i2)
                    }
                }
            }
        }
    }
}
