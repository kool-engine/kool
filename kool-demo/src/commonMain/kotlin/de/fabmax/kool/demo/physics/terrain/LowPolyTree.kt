package de.fabmax.kool.demo.physics.terrain

import de.fabmax.kool.math.*
import de.fabmax.kool.math.spatial.InRadiusTraverser
import de.fabmax.kool.math.spatial.pointKdTree
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.scene.geometry.simpleShape
import de.fabmax.kool.util.ColorGradient
import de.fabmax.kool.util.MdColor
import kotlin.math.*
import kotlin.random.Random

class LowPolyTree(seed: Int = 1337) {

    val rand = Random(seed)
    var baseStrength = 300f

    fun generateNodes(pose: Mat4f): Node {
        val root = Node(null, 0)
        root.setStrength(baseStrength, baseStrength)
        root.pose.set(pose)
        root.pose.rotate(rand.randomF(-3f, 3f).deg, 0f.deg, rand.randomF(-3f, 3f).deg)

        var tips = listOf(root)
        while (tips.isNotEmpty()) {
            tips = grow(tips)
        }

        return root
    }

    fun trunkMesh(root: Node, tint: Float, target: MeshBuilder) {
        target.apply {
            color = MdColor.BROWN toneLin (500 + (tint * 300f).toInt())

            profile {
                simpleShape(true) {
                    for (i in 0..5) {
                        val ang = (i / 6f) * 2f * PI.toFloat()
                        xz(sin(ang), cos(ang))
                    }
                }

                fun sampleNode(node: Node, connect: Boolean) {
                    val r = max(node.strength / 2000f, 0.002f).pow(0.5f)
                    vertexModFun = {
                        val nodeHeight = (node.y - root.y)
                        val senseByHeight = nodeHeight / 50f
                        val senseByStrength = (1f - node.relStrength).pow(2) * (nodeHeight / 5f).clamp(0f, 1f)
                        getFloatAttribute(Wind.WIND_SENSITIVITY)?.f = (senseByStrength + senseByHeight).clamp(0f, 1f)
                    }
                    withTransform {
                        transform.set(node.pose)
                        transform.scale(r)
                        sample(connect)
                    }
                }

                val startNodes = mutableListOf(root)
                while (startNodes.isNotEmpty()) {
                    var it = startNodes.removeAt(startNodes.lastIndex)
                    it.parent?.let { sampleNode(it, false) }

                    while (true) {
                        sampleNode(it, true)
                        if (it.children.isNotEmpty()) {
                            if (it.children.size > 1) {
                                startNodes += it.children[1]
                            }
                            it = it.children[0]
                        } else {
                            break
                        }
                    }
                }
            }
        }
    }

    fun leafMesh(root: Node, tint: Float, target: MeshBuilder) {
        val nodes = mutableListOf<Node>()
        fun traverseTree(node: Node) {
            if (node.relStrength < 0.3f && node.y - root.y > 1.5f) {
                nodes += node
            }
            node.children.forEach { traverseTree(it) }
        }
        traverseTree(root)

        val tree = pointKdTree(nodes)
        val trav = InRadiusTraverser<Node>()

        val leafColorRange = ColorGradient(0f to (MdColor.LIGHT_GREEN tone 900), 0.8f to MdColor.LIGHT_GREEN, 1f to MdColor.LIME)
        target.apply {
            vertexModFun = {
                getFloatAttribute(Wind.WIND_SENSITIVITY)?.f = 1f
            }
            nodes.forEach {
                trav.setup(it, 3f).traverse(tree)
                val p = 1f - (trav.result.size / 17f).clamp(0.0f, 0.97f)
                val pop = rand.randomF() < p
                if (pop || it.children.isEmpty()) {
                    color = leafColorRange.getColor((1f - tint) + rand.randomF(-0.25f, 0.25f)).toLinear()
                    addLeafSphere(it, 1.35f + p * p * p, this)
                }
            }
        }
    }

    private fun addLeafSphere(node: Node, leafR: Float, target: MeshBuilder) {
        target.apply {
            withTransform {
                transform.set(node.pose)
                val rotAx = MutableVec3f(rand.randomF(-1f, 1f), rand.randomF(-1f, 1f), rand.randomF(-1f, 1f)).norm()
                rotate(rand.randomF(0f, 360f).deg, rotAx)
                scale(rand.randomF(0.6f, 1f), rand.randomF(0.6f, 1f), rand.randomF(0.6f, 1f))

                icoSphere {
                    steps = 0
                    radius = leafR
                }
            }
        }
    }

    private fun grow(leafs: List<Node>): List<Node> {
        val newLeafs = mutableListOf<Node>()
        leafs.forEach { leaf ->
            if (leaf.strength > 0f) {
                if (shouldFork(leaf)) {
                    val splitLeafs = fork(leaf)
                    leaf.children += splitLeafs
                    newLeafs += splitLeafs
                } else {
                    val next = Node(leaf, leaf.depth + 1)
                    next.setStrength(leaf.strength, baseStrength)
                    leaf.children += next
                    newLeafs += next
                }
            }
        }
        newLeafs.forEach { grow(it) }
        return newLeafs
    }

    private fun grow(node: Node) {
        if (node.parent?.parent != null) {
            val maxTurn = 2f + (1f - node.relStrength).pow(2) * 10f
            node.pose.rotate(rand.randomF(-maxTurn, maxTurn).deg, 0f.deg, rand.randomF(-maxTurn, maxTurn).deg)
        }

        val growLen = 1f + max(0f, node.strength / 200f)
        node.pose.translate(0f, growLen, 0f)
        node.updatePosition()

        val str = min(node.strength - 5f, node.strength * 0.92f)
        node.setStrength(str, baseStrength)
    }

    private fun fork(node: Node): List<Node> {
        var splitW = rand.randomF(0.1f, 0.9f)
        if (splitW < 0.5f) {
            splitW = 1f - splitW
        }

        val forkAng = rand.randomF(25f, 40f) + (1f - node.relStrength) * 40f * rand.randomF(0.5f, 1f)
        val forkAx = MutableVec3f()
        do {
            forkAx.set(rand.randomF(-1f, 1f), 0f, rand.randomF(-1f, 1f))
        } while (forkAx.length() > 1f)
        forkAx.norm()

        val a = Node(node, node.depth + 1)
        a.setStrength(node.strength * splitW, baseStrength)
        a.pose.rotate((forkAng * (1f - splitW)).deg, forkAx)

        val b = Node(node, node.depth + 1)
        b.setStrength(node.strength * (1f - splitW), baseStrength)
        b.pose.rotate((-forkAng * splitW).deg, forkAx)

        return listOf(a, b)
    }

    private fun shouldFork(node: Node): Boolean {
        if (node.strength < 10f) {
            return false
        }

        var noFork = 0
        var it = node.parent
        while (it != null && !it.isFork()) {
            noFork++
            it = it.parent
        }

        val thresh = min(0.9f - noFork / 4f, node.relStrength.pow(2))
        return rand.randomF() > thresh
    }

    class Node(val parent: Node?, val depth: Int) : MutableVec3f() {
        val pose = MutableMat4f()
        val children = mutableListOf<Node>()
        var strength = 0f
        var relStrength = 0f

        init {
            if (parent != null) {
                strength = parent.strength
                pose.set(parent.pose)
            }
        }

        fun updatePosition() {
            pose.transform(set(ZERO))
        }

        fun setStrength(strength: Float, baseStrength: Float) {
            this.strength = strength
            this.relStrength = (strength / baseStrength).clamp(0f, 1f)
        }

        fun isFork() = children.size > 1
        fun isLeaf() = children.isEmpty()
    }
}