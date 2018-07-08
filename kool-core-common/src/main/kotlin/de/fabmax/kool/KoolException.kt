package de.fabmax.kool

/**
 * @author fabmax
 */
class KoolException(message: String, cause: Exception?) : Exception(message, cause) {
    constructor(message: String): this(message, null)
}
