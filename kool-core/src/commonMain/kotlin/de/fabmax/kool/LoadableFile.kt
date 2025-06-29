package de.fabmax.kool

import de.fabmax.kool.util.Uint8Buffer

interface LoadableFile {
    val name: String
    val size: Long
    val mimeType: MimeType

    suspend fun read(): Uint8Buffer
}