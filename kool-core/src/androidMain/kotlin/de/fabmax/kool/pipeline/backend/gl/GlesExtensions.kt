package de.fabmax.kool.pipeline.backend.gl

import dalvik.annotation.optimization.CriticalNative

/**
 * Native wrapper functions to OpenGL ES extensions not mapped by the Android SDK.
 *
 * Mapped extensions:
 * - [EXT_clip_control](https://registry.khronos.org/OpenGL/extensions/EXT/EXT_clip_control.txt)
 * - [EXT_disjoint_timer_query](https://registry.khronos.org/OpenGL/extensions/EXT/EXT_disjoint_timer_query.txt)
 *
 * Extensions need to be enabled via their enable function before they can be used
 * (e.g. {@link #enableEXTclipControl()} for EXT_clip_control).
 */
object GlesExtensions {

    init {
        System.loadLibrary("koolandroidnative")
    }

    // https://registry.khronos.org/OpenGL/extensions/EXT/EXT_clip_control.txt

    const val LOWER_LEFT_EXT: Int = 0x8CA1
    const val UPPER_LEFT_EXT: Int = 0x8CA2
    const val NEGATIVE_ONE_TO_ONE_EXT: Int = 0x935E
    const val ZERO_TO_ONE_EXT: Int = 0x935F
    const val CLIP_ORIGIN_EXT: Int = 0x935C
    const val CLIP_DEPTH_MODE_EXT: Int = 0x935D

    external fun enableEXTclipControl(): Boolean

    @JvmStatic
    @CriticalNative
    external fun clipControl(origin: Int, depth: Int)

    // https://registry.khronos.org/OpenGL/extensions/EXT/EXT_disjoint_timer_query.txt

    const val QUERY_COUNTER_BITS_EXT: Int = 0x8864
    const val CURRENT_QUERY_EXT: Int = 0x8865
    const val QUERY_RESULT_EXT: Int = 0x8866
    const val QUERY_RESULT_AVAILABLE_EXT: Int = 0x8867
    const val TIME_ELAPSED_EXT: Int = 0x88BF
    const val TIMESTAMP_EXT: Int = 0x8E28
    const val GPU_DISJOINT_EXT: Int = 0x8FBB

    external fun enableEXTdisjointTimerQuery(): Boolean

    @JvmStatic
    @CriticalNative
    external fun queryCounter(id: Int, target: Int)

    @JvmStatic
    @CriticalNative
    external fun getQueryObjecti64(id: Int, pname: Int): Long

    @JvmStatic
    @CriticalNative
    external fun getQueryObjectui64(id: Int, pname: Int): Long
}