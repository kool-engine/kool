package de.fabmax.js

/**
 * Ripped out of kotlin-js-library, because it's not yet available in version 1.1.0...
 */
public external object Math {
    public val PI: Double
    public fun random(): Double
    public fun abs(value: Double): Double
    public fun acos(value: Double): Double
    public fun asin(value: Double): Double
    public fun atan(value: Double): Double
    public fun atan2(x: Double, y: Double): Double
    public fun cos(value: Double): Double
    public fun sin(value: Double): Double
    public fun exp(value: Double): Double
    public fun max(vararg values: Int): Int
    public fun max(vararg values: Float): Float
    public fun max(vararg values: Double): Double
    public fun min(vararg values: Int): Int
    public fun min(vararg values: Float): Float
    public fun min(vararg values: Double): Double
    public fun sqrt(value: Double): Double
    public fun tan(value: Double): Double
    public fun log(value: Double): Double
    public fun pow(base: Double, exp: Double): Double
    public fun round(value: Number): Int
    public fun floor(value: Number): Int
    public fun ceil(value: Number): Int
}
