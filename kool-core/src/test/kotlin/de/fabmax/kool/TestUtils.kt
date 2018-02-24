package de.fabmax.kool

/**
 * Test util functions
 */

/**
 * Replacement for default assert() function which doesn't work if the corresponding JVM option isn't set (which
 * is tedious in gradle)
 */
fun testAssert(check: Boolean, msg: String = "Assertion failed") {
    if (!check) {
        throw AssertionError(msg)
    }
}
