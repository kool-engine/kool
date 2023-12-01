package de.fabmax.kool.math

import kotlin.math.sqrt

// <template> Changes made within the template section will also affect the other type variants of this class

//
// point to line functions (no end, infinite length)
//

fun Vec3f.distanceToLine(lineA: Vec3f, lineB: Vec3f) = sqrt(sqrDistanceToLine(lineA, lineB))

fun Vec3f.sqrDistanceToLine(lineA: Vec3f, lineB: Vec3f) =
        sqrDistancePointToLine(x, y, z, lineA, lineB)

fun sqrDistancePointToLine(x: Float, y: Float, z: Float, lineA: Vec3f, lineB: Vec3f): Float {
    // vec math would be nice here, but we don't want to create a temporary MutableVec3f
    val rx = lineB.x - lineA.x
    val ry = lineB.y - lineA.y
    val rz = lineB.z - lineA.z

    val dotPt = x*rx + y*ry + z*rz
    val dotLineA = lineA.x*rx + lineA.y*ry + lineA.z*rz
    val dotR = rx*rx + ry*ry + rz*rz

    val l = (dotPt - dotLineA) / dotR
    val nx = rx * l + lineA.x
    val ny = ry * l + lineA.y
    val nz = rz * l + lineA.z

    val dx = nx - x
    val dy = ny - y
    val dz = nz - z
    return dx*dx + dy*dy + dz*dz
}

fun Vec3f.nearestPointOnLine(lineA: Vec3f, lineB: Vec3f, result: MutableVec3f): MutableVec3f {
    lineB.subtract(lineA, result)
    val l = (dot(result) - lineA.dot(result)) / result.dot(result)
    return result.mul(l).add(lineA)
}

//
// point to ray functions (one end, infinite length)
//

fun Vec3f.distanceToRay(ray: RayF) = distanceToRay(ray.origin, ray.direction)

fun Vec3f.distanceToRay(origin: Vec3f, direction: Vec3f) = sqrt(sqrDistanceToRay(origin, direction))

fun Vec3f.sqrDistanceToRay(ray: RayF) = sqrDistanceToRay(ray.origin, ray.direction)

fun Vec3f.sqrDistanceToRay(origin: Vec3f, direction: Vec3f) = sqrDistancePointToRay(x, y, z, origin, direction)

fun sqrDistancePointToRay(x: Float, y: Float, z: Float, origin: Vec3f, direction: Vec3f): Float {
    val nx: Float
    val ny: Float
    val nz: Float
    val dot = x * direction.x + y * direction.y + z * direction.z
    val l = (dot - origin.dot(direction)) / direction.dot(direction)
    if (l <= 0) {
        nx = origin.x - x
        ny = origin.y - y
        nz = origin.z - z
    } else {
        nx = direction.x * l + origin.x - x
        ny = direction.y * l + origin.y - y
        nz = direction.z * l + origin.z - z
    }
    return nx*nx + ny*ny + nz*nz
}

fun Vec3f.nearestPointOnRay(origin: Vec3f, direction: Vec3f, result: MutableVec3f): MutableVec3f {
    val l = (dot(direction) - origin.dot(direction)) / direction.dot(direction)
    return if (l <= 0) {
        result.set(origin)
    } else {
        result.set(direction).mul(l).add(origin)
    }
}

//
// point to edge functions (two ends, finite length)
//

fun Vec2f.distanceToEdge(edgeA: Vec2f, edgeB: Vec2f) = sqrt(sqrDistanceToEdge(edgeA, edgeB))

fun Vec2f.sqrDistanceToEdge(edgeA: Vec2f, edgeB: Vec2f) = sqrDistancePointToEdge(x, y, edgeA, edgeB)

fun sqrDistancePointToEdge(x: Float, y: Float, edgeA: Vec2f, edgeB: Vec2f): Float {
    // vec math would be nice here, but we don't want to create a temporary MutableVec3f
    val rx = edgeB.x - edgeA.x
    val ry = edgeB.y - edgeA.y

    val dotPt = x*rx + y*ry
    val dotEdgeA = edgeA.x*rx + edgeA.y*ry
    val dotR = rx*rx + ry*ry

    val l = (dotPt - dotEdgeA) / dotR
    val nx: Float
    val ny: Float
    when {
        l <= 0 -> { nx = edgeA.x; ny = edgeA.y }
        l >= 1 -> { nx = edgeB.x; ny = edgeB.y }
        else -> { nx = rx * l + edgeA.x; ny = ry * l + edgeA.y }
    }

    val dx = nx - x
    val dy = ny - y
    return dx*dx + dy*dy
}

fun Vec2f.nearestPointOnEdge(edgeA: Vec2f, edgeB: Vec2f, result: MutableVec2f): MutableVec2f {
    edgeB.subtract(edgeA, result)
    val l = (dot(result) - edgeA.dot(result)) / dot(result * result)
    return when {
        l <= 0 -> result.set(edgeA)
        l >= 1 -> result.set(edgeB)
        else -> result.mul(l).add(edgeA)
    }
}

fun Vec3f.distanceToEdge(edgeA: Vec3f, edgeB: Vec3f) = sqrt(sqrDistanceToEdge(edgeA, edgeB))

fun Vec3f.sqrDistanceToEdge(edgeA: Vec3f, edgeB: Vec3f) = sqrDistancePointToEdge(x, y, z, edgeA, edgeB)

fun sqrDistancePointToEdge(x: Float, y: Float, z: Float, edgeA: Vec3f, edgeB: Vec3f): Float {
    // vec math would be nice here, but we don't want to create a temporary MutableVec3f
    val rx = edgeB.x - edgeA.x
    val ry = edgeB.y - edgeA.y
    val rz = edgeB.z - edgeA.z

    val dotPt = x*rx + y*ry + z*rz
    val dotEdgeA = edgeA.x*rx + edgeA.y*ry + edgeA.z*rz
    val dotR = rx*rx + ry*ry + rz*rz

    val l = (dotPt - dotEdgeA) / dotR
    val nx: Float
    val ny: Float
    val nz: Float
    when {
        l <= 0 -> { nx = edgeA.x; ny = edgeA.y; nz = edgeA.z }
        l >= 1 -> { nx = edgeB.x; ny = edgeB.y; nz = edgeB.z }
        else -> { nx = rx * l + edgeA.x; ny = ry * l + edgeA.y; nz = rz * l + edgeA.z }
    }

    val dx = nx - x
    val dy = ny - y
    val dz = nz - z
    return dx*dx + dy*dy + dz*dz
}

fun Vec3f.nearestPointOnEdge(edgeA: Vec3f, edgeB: Vec3f, result: MutableVec3f): MutableVec3f {
    edgeB.subtract(edgeA, result)
    val l = (dot(result) - edgeA.dot(result)) / result.dot(result)
    return when {
        l <= 0 -> result.set(edgeA)
        l >= 1 -> result.set(edgeB)
        else -> result.mul(l).add(edgeA)
    }
}

// </template> End of template section, DO NOT EDIT BELOW THIS!


//
// point to line functions (no end, infinite length)
//

fun Vec3d.distanceToLine(lineA: Vec3d, lineB: Vec3d) = sqrt(sqrDistanceToLine(lineA, lineB))

fun Vec3d.sqrDistanceToLine(lineA: Vec3d, lineB: Vec3d) =
        sqrDistancePointToLine(x, y, z, lineA, lineB)

fun sqrDistancePointToLine(x: Double, y: Double, z: Double, lineA: Vec3d, lineB: Vec3d): Double {
    // vec math would be nice here, but we don't want to create a temporary MutableVec3d
    val rx = lineB.x - lineA.x
    val ry = lineB.y - lineA.y
    val rz = lineB.z - lineA.z

    val dotPt = x*rx + y*ry + z*rz
    val dotLineA = lineA.x*rx + lineA.y*ry + lineA.z*rz
    val dotR = rx*rx + ry*ry + rz*rz

    val l = (dotPt - dotLineA) / dotR
    val nx = rx * l + lineA.x
    val ny = ry * l + lineA.y
    val nz = rz * l + lineA.z

    val dx = nx - x
    val dy = ny - y
    val dz = nz - z
    return dx*dx + dy*dy + dz*dz
}

fun Vec3d.nearestPointOnLine(lineA: Vec3d, lineB: Vec3d, result: MutableVec3d): MutableVec3d {
    lineB.subtract(lineA, result)
    val l = (dot(result) - lineA.dot(result)) / result.dot(result)
    return result.mul(l).add(lineA)
}

//
// point to ray functions (one end, infinite length)
//

fun Vec3d.distanceToRay(ray: RayD) = distanceToRay(ray.origin, ray.direction)

fun Vec3d.distanceToRay(origin: Vec3d, direction: Vec3d) = sqrt(sqrDistanceToRay(origin, direction))

fun Vec3d.sqrDistanceToRay(ray: RayD) = sqrDistanceToRay(ray.origin, ray.direction)

fun Vec3d.sqrDistanceToRay(origin: Vec3d, direction: Vec3d) = sqrDistancePointToRay(x, y, z, origin, direction)

fun sqrDistancePointToRay(x: Double, y: Double, z: Double, origin: Vec3d, direction: Vec3d): Double {
    val nx: Double
    val ny: Double
    val nz: Double
    val dot = x * direction.x + y * direction.y + z * direction.z
    val l = (dot - origin.dot(direction)) / direction.dot(direction)
    if (l <= 0) {
        nx = origin.x - x
        ny = origin.y - y
        nz = origin.z - z
    } else {
        nx = direction.x * l + origin.x - x
        ny = direction.y * l + origin.y - y
        nz = direction.z * l + origin.z - z
    }
    return nx*nx + ny*ny + nz*nz
}

fun Vec3d.nearestPointOnRay(origin: Vec3d, direction: Vec3d, result: MutableVec3d): MutableVec3d {
    val l = (dot(direction) - origin.dot(direction)) / direction.dot(direction)
    return if (l <= 0) {
        result.set(origin)
    } else {
        result.set(direction).mul(l).add(origin)
    }
}

//
// point to edge functions (two ends, finite length)
//

fun Vec2d.distanceToEdge(edgeA: Vec2d, edgeB: Vec2d) = sqrt(sqrDistanceToEdge(edgeA, edgeB))

fun Vec2d.sqrDistanceToEdge(edgeA: Vec2d, edgeB: Vec2d) = sqrDistancePointToEdge(x, y, edgeA, edgeB)

fun sqrDistancePointToEdge(x: Double, y: Double, edgeA: Vec2d, edgeB: Vec2d): Double {
    // vec math would be nice here, but we don't want to create a temporary MutableVec3d
    val rx = edgeB.x - edgeA.x
    val ry = edgeB.y - edgeA.y

    val dotPt = x*rx + y*ry
    val dotEdgeA = edgeA.x*rx + edgeA.y*ry
    val dotR = rx*rx + ry*ry

    val l = (dotPt - dotEdgeA) / dotR
    val nx: Double
    val ny: Double
    when {
        l <= 0 -> { nx = edgeA.x; ny = edgeA.y }
        l >= 1 -> { nx = edgeB.x; ny = edgeB.y }
        else -> { nx = rx * l + edgeA.x; ny = ry * l + edgeA.y }
    }

    val dx = nx - x
    val dy = ny - y
    return dx*dx + dy*dy
}

fun Vec2d.nearestPointOnEdge(edgeA: Vec2d, edgeB: Vec2d, result: MutableVec2d): MutableVec2d {
    edgeB.subtract(edgeA, result)
    val l = (dot(result) - edgeA.dot(result)) / dot(result * result)
    return when {
        l <= 0 -> result.set(edgeA)
        l >= 1 -> result.set(edgeB)
        else -> result.mul(l).add(edgeA)
    }
}

fun Vec3d.distanceToEdge(edgeA: Vec3d, edgeB: Vec3d) = sqrt(sqrDistanceToEdge(edgeA, edgeB))

fun Vec3d.sqrDistanceToEdge(edgeA: Vec3d, edgeB: Vec3d) = sqrDistancePointToEdge(x, y, z, edgeA, edgeB)

fun sqrDistancePointToEdge(x: Double, y: Double, z: Double, edgeA: Vec3d, edgeB: Vec3d): Double {
    // vec math would be nice here, but we don't want to create a temporary MutableVec3d
    val rx = edgeB.x - edgeA.x
    val ry = edgeB.y - edgeA.y
    val rz = edgeB.z - edgeA.z

    val dotPt = x*rx + y*ry + z*rz
    val dotEdgeA = edgeA.x*rx + edgeA.y*ry + edgeA.z*rz
    val dotR = rx*rx + ry*ry + rz*rz

    val l = (dotPt - dotEdgeA) / dotR
    val nx: Double
    val ny: Double
    val nz: Double
    when {
        l <= 0 -> { nx = edgeA.x; ny = edgeA.y; nz = edgeA.z }
        l >= 1 -> { nx = edgeB.x; ny = edgeB.y; nz = edgeB.z }
        else -> { nx = rx * l + edgeA.x; ny = ry * l + edgeA.y; nz = rz * l + edgeA.z }
    }

    val dx = nx - x
    val dy = ny - y
    val dz = nz - z
    return dx*dx + dy*dy + dz*dz
}

fun Vec3d.nearestPointOnEdge(edgeA: Vec3d, edgeB: Vec3d, result: MutableVec3d): MutableVec3d {
    edgeB.subtract(edgeA, result)
    val l = (dot(result) - edgeA.dot(result)) / result.dot(result)
    return when {
        l <= 0 -> result.set(edgeA)
        l >= 1 -> result.set(edgeB)
        else -> result.mul(l).add(edgeA)
    }
}
