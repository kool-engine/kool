package de.fabmax.kool.demo

import de.fabmax.kool.math.*
import de.fabmax.kool.math.spatial.InRadiusTraverser
import de.fabmax.kool.math.spatial.pointKdTree
import de.fabmax.kool.scene.LineMesh
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.timedMs
import kotlin.math.*

class TreeGenerator(val distribution: PointDistribution,
                    val baseTop: Vec3f = Vec3f(0f, 1f, 0f),
                    val baseBot: Vec3f = Vec3f.ZERO,
                    val primaryLightDir: Vec3f? = null,
                    val random: Random = defaultRandomInstance) {

    var radiusOfInfluence = 1.0f
    var growDistance = 0.15f
    var killDistance = 1.5f
    var numberOfAttractionPoints = 3000

    val actualKillDistance: Float
        get() = growDistance * killDistance

    private val attractionPoints = mutableListOf<AttractionPoint>()
    private var attractionPointsTree = pointKdTree(listOf<AttractionPoint>())
    private val attractionPointTrav = InRadiusTraverser<AttractionPoint>()
    private val treeNodes = mutableListOf<TreeNode>()

    private var root = TreeNode()

    fun seedTree() {
        populateAttractionPoints()

        treeNodes.clear()
        root = TreeNode()
        root.set(baseBot)
        treeNodes += root

        val d = baseTop.subtract(baseBot, MutableVec3f()).norm().scale(growDistance)
        var prev = root
        while (prev.distance(baseTop) > growDistance) {
            val newNd = TreeNode()
            newNd.set(prev).add(d)
            prev.addChild(newNd)
            treeNodes += newNd
            prev = newNd
        }
    }

    fun generate(maxIterations: Int = 1000) {
        var i = 0
        timedMs({"Generation done, took $i iterations, ${treeNodes.size} nodes in"}) {
            seedTree()
            while (i++ < maxIterations) {
                if (!growSingleStep()) {
                    break
                }
            }
            finishTree()
        }
    }

    fun growSingleStep(): Boolean {
        // clear nearest
        attractionPoints.forEach { it.nearestNode = null }

        // find current nearest
        for (node in treeNodes) {
            node.influencingPts.clear()
            if (!node.isFinished) {
                attractionPointTrav.setup(node, radiusOfInfluence).traverse(attractionPointsTree)
                for (attracPt in attractionPointTrav.result) {
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
            if (node.influencingPts.isNotEmpty()) {
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

                    attractionPointTrav.setup(newNode, actualKillDistance).traverse(attractionPointsTree)
                    attractionPointTrav.result.forEach { it.isOpen = false }
                    changed = true
                }
            } else {
                node.isFinished = true
            }
        }
        treeNodes.addAll(newNodes)
        return changed
    }

    fun finishTree() {
        root.forEachTopDown {
            if (parent != null) {
                // shift nodes towards base to make branches nicer
                this += MutableVec3f(parent!!).subtract(this).norm().scale(growDistance * 0.5f)

                // add a small random offset
                x += random.randomF(-0.01f, 0.01f)
                y += random.randomF(-0.01f, 0.01f)
                z += random.randomF(-0.01f, 0.01f)
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
        target.geometry.removeDegeneratedTriangles()
    }

    fun buildLeafMesh(target: MeshBuilder) {
        treeNodes.forEach { it.buildLeafMesh(target) }
        target.geometry.forEach {
            if (primaryLightDir != null) {
                // ensure normals point towards light
                if (it.normal.dot(primaryLightDir) > 0) {
                    it.normal.scale(-1f)
                }
            } else {
                if (it.normal.y < 0) {
                    it.normal.scale(-1f)
                }
            }
        }
        target.geometry.removeDegeneratedTriangles()
    }

    private fun populateAttractionPoints() {
        attractionPoints.clear()
        for (pt in distribution.nextPoints(numberOfAttractionPoints)) {
            attractionPoints += AttractionPoint(pt)
        }
        attractionPointsTree = pointKdTree(attractionPoints)
    }

    private class AttractionPoint(pt: Vec3f) : MutableVec3f(pt) {
        var nearestNode: TreeNode? = null
            set(value) {
                field = value
                if (value == null) {
                    nearestNodeDist = Float.MAX_VALUE
                }
            }

        var nearestNodeDist = Float.MAX_VALUE
            private set

        var isOpen = true

        fun checkNearest(node: TreeNode) {
            val dist = distance(node)
            if (dist < nearestNodeDist) {
                nearestNode = node
                nearestNodeDist = dist
            }
        }
    }

    private inner class TreeNode : MutableVec3f() {
        val children = mutableListOf<TreeNode>()
        var parent: TreeNode? = null
        var branchDepth = 0

        val influencingPts = mutableListOf<AttractionPoint>()
        var isFinished = false

        var radius = 0.005f
        var texV = 0f
        var uScale = 1f
        var vScale = 3f
        val circumPts = mutableListOf<Vec3f>()

        fun addChild(node: TreeNode) {
            children += node
            node.parent = this
        }

        fun containsChild(node: TreeNode): Boolean {
            for (c in children) {
                if (c.isFuzzyEqual(node)) {
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
                radius = children.sumOf { it.radius.toDouble().pow(p) }.pow(1.0 / p).toFloat()
                branchDepth = if (children.size == 1) {
                    children[0].branchDepth
                } else {
                    (children.maxByOrNull { it.branchDepth }?.branchDepth ?: 0) + 1
                }
            }
            if (parent == null) {
                // tree root
                radius *= 3f
                children[0].radius *= 1.5f
                children[0].computeCircumPoints()
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
            val uScale = if (radius > 0.05f) { 2f } else { 1f } * this.uScale

            val idcs = mutableListOf<Int>()
            if (parent != null) {
                if (children.isEmpty()) {
                    val tipIdx = target.geometry.addVertex {
                        position.set(this@TreeNode)
                        this@TreeNode.subtract(parent!!, normal).norm()
                        texCoord.set(0f, texV * vScale)
                    }
                    for (i in 0..8) {
                        idcs += target.geometry.addVertex {
                            position.set(parent!!.circumPts[i%8])
                            parent!!.circumPts[i%8].subtract(parent!!, normal).norm()
                            texCoord.set(i / 8f * uScale, parent!!.texV * vScale)
                        }
                    }
                    for (i in 0 until 8) {
                        target.geometry.addTriIndices(tipIdx, idcs[i], idcs[i + 1])
                    }

                } else {
                    for (i in 0..8) {
                        idcs += target.geometry.addVertex {
                            position.set(circumPts[i%8])
                            circumPts[i%8].subtract(this@TreeNode, normal).norm()
                            texCoord.set(i / 8f * uScale, texV * vScale)
                        }
                        idcs += target.geometry.addVertex {
                            position.set(parent!!.circumPts[i%8])
                            parent!!.circumPts[i%8].subtract(parent!!, normal).norm()
                            texCoord.set(i / 8f * uScale, parent!!.texV * vScale)
                        }
                    }
                    for (i in 0 until 8) {
                        target.geometry.addTriIndices(idcs[i * 2], idcs[i * 2 + 1], idcs[i * 2 + 2])
                        target.geometry.addTriIndices(idcs[i * 2 + 1], idcs[i * 2 + 3], idcs[i * 2 + 2])
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
                        val r = MutableVec3f(circumPts[0]).subtract(this@TreeNode).norm().scale(radius + random.randomF(0f, 0.15f))
                        r.rotate(random.randomF(0f, 360f), n)
                        val p = MutableVec3f(n).scale(random.randomF(0f, len)).add(r).add(this@TreeNode)

                        translate(p)

                        var tries = 0
                        do {
                            if (n.dot(X_AXIS) < n.dot(Z_AXIS)) {
                                rotate(random.randomF(0f, 360f), X_AXIS)
                            } else {
                                rotate(random.randomF(0f, 360f), Z_AXIS)
                            }
                            rotate(random.randomF(0f, 360f), n)
                        } while (primaryLightDir != null &&
                                abs(transform.transform(MutableVec3f(NEG_Z_AXIS), 0f).dot(primaryLightDir)) < 0.1 &&
                                tries++ < 3)

                        val i0 = vertex(Vec3f(0f, -0.022f, 0f), NEG_Z_AXIS, Vec2f(0f, 0f))
                        val i1 = vertex(Vec3f(0f, 0.022f, 0f), NEG_Z_AXIS, Vec2f(0f, 1f))
                        val i2 = vertex(Vec3f(0.1f, 0.022f, 0f), NEG_Z_AXIS, Vec2f(1f, 1f))
                        val i3 = vertex(Vec3f(0.1f, -0.022f, 0f), NEG_Z_AXIS, Vec2f(1f, 0f))
                        geometry.addIndices(i0, i1, i2, i0, i2, i3)
                    }
                }
            }
        }
    }
}

class TreeTopPointDistribution(val centerY: Float, val width: Float, val height: Float,
                               private val random: Random = defaultRandomInstance) : PointDistribution() {

    private val borders = mutableListOf<MutableList<Vec2f>>()
    private val tmpPt1 = MutableVec3f()
    private val tmpPt2 = MutableVec2f()
    private val e00 = MutableVec2f()
    private val e01 = MutableVec2f()
    private val e10 = MutableVec2f()
    private val e11 = MutableVec2f()

    private val seedPts = mutableListOf<Vec3f>()

    init {
        for (j in 1..8) {
            val spline = BSplineVec2f(3)
            val n = 7
            for (i in 0..n) {
                val a = i / n.toFloat() * PI.toFloat()
                val f = if (i in 1 until n) { random.randomF(0.4f, 0.6f) } else { 0.5f }
                val x = sin(a) * (width - 0.4f) * f + 0.2f
                val y = cos(a) * height * f + centerY
                spline.ctrlPoints += MutableVec2f(x, y)
            }
            spline.ctrlPoints.add(0, MutableVec2f(0f, centerY + height * 0.5f))
            spline.ctrlPoints.add(MutableVec2f(0f, centerY - height * 0.5f))
            spline.addInterpolationEndpoints()

            val pts = mutableListOf<Vec2f>()
            val m = 20
            for (i in 0..m) {
                pts += spline.evaluate(i.toFloat() / m, MutableVec2f())
            }
            borders += pts
        }
        seed()
    }

    private fun seed() {
        seedPts.clear()
        for (i in 1..10) {
            seedPts += nextPointInBounds()
        }
    }

    fun drawBorders(target: LineMesh) {
        for (i in borders.indices) {
            val a = (i.toFloat() / borders.size) * 2f * PI.toFloat()
            val pts = borders[i]
            for (j in 1 until pts.size) {
                val p0 = Vec3f(-cos(a) * pts[j-1].x, pts[j-1].y, -sin(a) * pts[j-1].x)
                val p1 = Vec3f(-cos(a) * pts[j].x, pts[j].y, -sin(a) * pts[j].x)
                target.addLine(p0, Color.ORANGE, p1, Color.ORANGE)
            }
        }
    }

    override fun nextPoints(n: Int): List<Vec3f> {
        seed()
        return super.nextPoints(n)
    }

    override fun nextPoint(): Vec3f {
        var pt: Vec3f
        while (true) {
            pt = nextPointInBounds()
            val d = seedPts.minByOrNull { it.sqrDistance(pt) }!!.distance(pt)
            if (d < random.randomF()) {
                break
            }
        }
        return pt
    }

    private fun nextPointInBounds(): Vec3f {
        val w = width * 0.5f
        val h = height * 0.5f

        while (true) {
            tmpPt1.set(random.randomF(-w, w), centerY + random.randomF(-h, h), random.randomF(-w, w))

            val px = sqrt(tmpPt1.x * tmpPt1.x + tmpPt1.z * tmpPt1.z)
            val py = tmpPt1.y

            val a = (atan2(tmpPt1.z, tmpPt1.x) / (2f * PI.toFloat()) + 0.5f).clamp(0f, 1f) * borders.size
            val i0 = min(a.toInt(), borders.size-1)
            val i1 = (i0 + 1) % borders.size
            val w1 = a - i0
            val w0 = 1f - w1

            nearestEdge(px, py, borders[i0], e00, e01)
            nearestEdge(px, py, borders[i1], e10, e11)

            e00.scale(w0).add(e10.scale(w1))
            e01.scale(w0).add(e11.scale(w1))

            val d = (px - e00.x) * (e01.y - e00.y) - (py - e00.y) * (e01.x - e00.x)
            if (d > 0) {
                return Vec3f(tmpPt1)
            }
        }
    }

    private fun nearestEdge(px: Float, py: Float, pts: List<Vec2f>, e0: MutableVec2f, e1: MutableVec2f) {
        var minDist = Float.MAX_VALUE
        var ni = 0
        for (i in 0 until pts.size-1) {
            val d = edgeDist(px, py, e0.set(pts[i]), e1.set(pts[i + 1]))
            if (d < minDist) {
                minDist = d
                ni = i
            }
        }
        e0.set(pts[ni])
        e1.set(pts[ni + 1])
    }

    private fun edgeDist(px: Float, py: Float, e0: MutableVec2f, e1: MutableVec2f): Float {
        e1.subtract(e0, tmpPt2)
        val l = ((px * tmpPt2.x + py * tmpPt2.y) - e0 * tmpPt2) / (tmpPt2 * tmpPt2)
        return if (l < 0) {
            val dx = e0.x - px
            val dy = e0.y - py
            sqrt(dx * dx + dy * dy)
        } else if (l > 1) {
            val dx = e1.x - px
            val dy = e1.y - py
            sqrt(dx * dx + dy * dy)
        } else {
            tmpPt2.scale(l).add(e0)
            val dx = tmpPt2.x - px
            val dy = tmpPt2.y - py
            sqrt(dx * dx + dy * dy)
        }
    }
}
