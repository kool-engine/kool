package de.fabmax.kool.platform.sdl

import org.lwjgl.sdl.*
import org.lwjgl.sdl.SDLEvents.*

private val sdlEvent = SDL_Event.create()

internal fun drainSdlEventQueue(): List<SdlEvent> = buildList {
    while (SDL_PollEvent(sdlEvent)) {
        add(SdlEvent.of(sdlEvent))
    }
}

sealed interface SdlEvent {
    val type: SdlEventType
    val name: String get() = type.name

    data class Button(
        val button: Int,
        val down: Boolean,
        override val type: SdlEventType
    ) : SdlEvent

    data class Drop(
        val x: Float,
        val y: Float,
        val data: String?,
        override val type: SdlEventType
    ) : SdlEvent

    data class Key(
        val scancode: Int,
        val key: Int,
        val mod: Int,
        val down: Boolean,
        val repeat: Boolean,
        override val type: SdlEventType
    ) : SdlEvent

    data class Motion(
        val x: Float,
        val y: Float,
        val xRel: Float,
        val yRel: Float,
        override val type: SdlEventType
    ) : SdlEvent

    data class Text(
        val text: String,
        override val type: SdlEventType
    ) : SdlEvent

    data class Wheel(
        val x: Float,
        val y: Float,
        override val type: SdlEventType
    ) : SdlEvent

    data class Window(
        val data1: Int,
        val data2: Int,
        override val type: SdlEventType
    ) : SdlEvent

    data object Quit : SdlEvent { override val type: SdlEventType = SdlEventType.QUIT }

    data class Gamepad(
        val which: Int,
        override val type: SdlEventType
    ) : SdlEvent

    data class GamepadButton(
        val button: Int,
        val down: Boolean,
        val which: Int,
        override val type: SdlEventType
    ) : SdlEvent

    data class GamepadAxis(
        val axis: Int,
        val value: Short,
        val which: Int,
        override val type: SdlEventType
    ) : SdlEvent

    data object JoystickEvent : SdlEvent { override val type: SdlEventType = SdlEventType.JOYSTICK_AXIS_MOTION }

    data class Other(override val type: SdlEventType) : SdlEvent

    companion object {
        private fun Button(ev: SDL_MouseButtonEvent): Button = Button(
            button = ev.button().toInt(),
            down = ev.down(),
            type = SdlEventType(ev.type()),
        )

        private fun Drop(ev: SDL_DropEvent): Drop = Drop(
            x = ev.x(),
            y = ev.y(),
            data = ev.dataString(),
            type = SdlEventType(ev.type()),
        )

        private fun Key(ev: SDL_KeyboardEvent): Key = Key(
            scancode = ev.scancode(),
            key = ev.key(),
            mod = ev.mod().toInt(),
            down = ev.down(),
            repeat = ev.repeat(),
            type = SdlEventType(ev.type()),
        )

        private fun Motion(ev: SDL_MouseMotionEvent): Motion = Motion(
            x = ev.x(),
            y = ev.y(),
            xRel = ev.xrel(),
            yRel = ev.yrel(),
            type = SdlEventType(ev.type()),
        )

        private fun Text(ev: SDL_TextInputEvent): Text = Text(ev.textString().orEmpty(), SdlEventType(ev.type()))

        private fun Wheel(ev: SDL_MouseWheelEvent): Wheel = Wheel(
            x = ev.x(),
            y = ev.y(),
            type = SdlEventType(ev.type()),
        )

        private fun Gamepad(ev: SDL_GamepadDeviceEvent): Gamepad = Gamepad(
            which = ev.which(),
            type = SdlEventType(ev.type())
        )

        private fun GamepadButton(ev: SDL_GamepadButtonEvent): GamepadButton = GamepadButton(
            button = ev.button().toInt(),
            down = ev.down(),
            which = ev.which(),
            type = SdlEventType(ev.type())
        )

        private fun GamepadAxis(ev: SDL_GamepadAxisEvent): GamepadAxis = GamepadAxis(
            axis = ev.axis().toInt(),
            value = ev.value(),
            which = ev.which(),
            type = SdlEventType(ev.type())
        )

        private fun Window(ev: SDL_WindowEvent): Window = Window(
            data1 = ev.data1(),
            data2 = ev.data2(),
            type = SdlEventType(ev.type()),
        )

        fun of(sdl: SDL_Event): SdlEvent = when (val type = SdlEventType(sdl.type())) {
            SdlEventType.MOUSE_MOTION -> Motion(sdl.motion())
            SdlEventType.MOUSE_WHEEL -> Wheel(sdl.wheel())
            SdlEventType.MOUSE_BUTTON_DOWN -> Button(sdl.button())
            SdlEventType.MOUSE_BUTTON_UP -> Button(sdl.button())

            SdlEventType.KEY_DOWN -> Key(sdl.key())
            SdlEventType.KEY_UP -> Key(sdl.key())
            SdlEventType.TEXT_INPUT -> Text(sdl.text())

            SdlEventType.GAMEPAD_ADDED -> Gamepad(sdl.gdevice())
            SdlEventType.GAMEPAD_REMOVED -> Gamepad(sdl.gdevice())
            SdlEventType.GAMEPAD_UPDATE_COMPLETE -> Gamepad(sdl.gdevice())
            SdlEventType.GAMEPAD_BUTTON_UP -> GamepadButton(sdl.gbutton())
            SdlEventType.GAMEPAD_BUTTON_DOWN -> GamepadButton(sdl.gbutton())
            SdlEventType.GAMEPAD_AXIS_MOTION -> GamepadAxis(sdl.gaxis())

            SdlEventType.JOYSTICK_AXIS_MOTION -> JoystickEvent
            SdlEventType.JOYSTICK_BUTTON_UP -> JoystickEvent
            SdlEventType.JOYSTICK_BUTTON_DOWN -> JoystickEvent
            SdlEventType.JOYSTICK_BALL_MOTION -> JoystickEvent
            SdlEventType.JOYSTICK_HAT_MOTION -> JoystickEvent
            SdlEventType.JOYSTICK_UPDATE_COMPLETE -> JoystickEvent

            SdlEventType.WINDOW_SHOWN -> Window(sdl.window())
            SdlEventType.WINDOW_HIDDEN -> Window(sdl.window())
            SdlEventType.WINDOW_EXPOSED -> Window(sdl.window())
            SdlEventType.WINDOW_MOVED -> Window(sdl.window())
            SdlEventType.WINDOW_RESIZED -> Window(sdl.window())
            SdlEventType.WINDOW_PIXEL_SIZE_CHANGED -> Window(sdl.window())
            SdlEventType.WINDOW_METAL_VIEW_RESIZED -> Window(sdl.window())
            SdlEventType.WINDOW_MINIMIZED -> Window(sdl.window())
            SdlEventType.WINDOW_MAXIMIZED -> Window(sdl.window())
            SdlEventType.WINDOW_RESTORED -> Window(sdl.window())
            SdlEventType.WINDOW_MOUSE_ENTER -> Window(sdl.window())
            SdlEventType.WINDOW_MOUSE_LEAVE -> Window(sdl.window())
            SdlEventType.WINDOW_FOCUS_GAINED -> Window(sdl.window())
            SdlEventType.WINDOW_FOCUS_LOST -> Window(sdl.window())
            SdlEventType.WINDOW_CLOSE_REQUESTED -> Window(sdl.window())
            SdlEventType.WINDOW_HIT_TEST -> Window(sdl.window())
            SdlEventType.WINDOW_ICCPROF_CHANGED -> Window(sdl.window())
            SdlEventType.WINDOW_DISPLAY_CHANGED -> Window(sdl.window())
            SdlEventType.WINDOW_DISPLAY_SCALE_CHANGED -> Window(sdl.window())
            SdlEventType.WINDOW_SAFE_AREA_CHANGED -> Window(sdl.window())
            SdlEventType.WINDOW_OCCLUDED -> Window(sdl.window())
            SdlEventType.WINDOW_ENTER_FULLSCREEN -> Window(sdl.window())
            SdlEventType.WINDOW_LEAVE_FULLSCREEN -> Window(sdl.window())
            SdlEventType.WINDOW_DESTROYED -> Window(sdl.window())
            SdlEventType.WINDOW_HDR_STATE_CHANGED -> Window(sdl.window())

            SdlEventType.DROP_FILE -> Drop(sdl.drop())
            SdlEventType.DROP_TEXT -> Drop(sdl.drop())
            SdlEventType.DROP_BEGIN -> Drop(sdl.drop())
            SdlEventType.DROP_COMPLETE -> Drop(sdl.drop())
            SdlEventType.DROP_POSITION -> Drop(sdl.drop())

            SdlEventType.QUIT -> Quit

            else -> Other(type)
        }
    }
}

@JvmInline
value class SdlEventType(val type: Int) {
    val name: String get() = names[this] ?: "UNKNOWN"

    override fun toString(): String = "SdlEventType($type:$name})"

    companion object {
        val QUIT = SdlEventType(SDL_EVENT_QUIT)
        val TERMINATING = SdlEventType(SDL_EVENT_TERMINATING)
        val LOW_MEMORY = SdlEventType(SDL_EVENT_LOW_MEMORY)
        val WILL_ENTER_BACKGROUND = SdlEventType(SDL_EVENT_WILL_ENTER_BACKGROUND)
        val DID_ENTER_BACKGROUND = SdlEventType(SDL_EVENT_DID_ENTER_BACKGROUND)
        val WILL_ENTER_FOREGROUND = SdlEventType(SDL_EVENT_WILL_ENTER_FOREGROUND)
        val DID_ENTER_FOREGROUND = SdlEventType(SDL_EVENT_DID_ENTER_FOREGROUND)
        val LOCALE_CHANGED = SdlEventType(SDL_EVENT_LOCALE_CHANGED)
        val SYSTEM_THEME_CHANGED = SdlEventType(SDL_EVENT_SYSTEM_THEME_CHANGED)
        val DISPLAY_ORIENTATION = SdlEventType(SDL_EVENT_DISPLAY_ORIENTATION)
        val DISPLAY_ADDED = SdlEventType(SDL_EVENT_DISPLAY_ADDED)
        val DISPLAY_REMOVED = SdlEventType(SDL_EVENT_DISPLAY_REMOVED)
        val DISPLAY_MOVED = SdlEventType(SDL_EVENT_DISPLAY_MOVED)
        val DISPLAY_DESKTOP_MODE_CHANGED = SdlEventType(SDL_EVENT_DISPLAY_DESKTOP_MODE_CHANGED)
        val DISPLAY_CURRENT_MODE_CHANGED = SdlEventType(SDL_EVENT_DISPLAY_CURRENT_MODE_CHANGED)
        val DISPLAY_CONTENT_SCALE_CHANGED = SdlEventType(SDL_EVENT_DISPLAY_CONTENT_SCALE_CHANGED)
        val DISPLAY_USABLE_BOUNDS_CHANGED = SdlEventType(SDL_EVENT_DISPLAY_USABLE_BOUNDS_CHANGED)
        val WINDOW_SHOWN = SdlEventType(SDL_EVENT_WINDOW_SHOWN)
        val WINDOW_HIDDEN = SdlEventType(SDL_EVENT_WINDOW_HIDDEN)
        val WINDOW_EXPOSED = SdlEventType(SDL_EVENT_WINDOW_EXPOSED)
        val WINDOW_MOVED = SdlEventType(SDL_EVENT_WINDOW_MOVED)
        val WINDOW_RESIZED = SdlEventType(SDL_EVENT_WINDOW_RESIZED)
        val WINDOW_PIXEL_SIZE_CHANGED = SdlEventType(SDL_EVENT_WINDOW_PIXEL_SIZE_CHANGED)
        val WINDOW_METAL_VIEW_RESIZED = SdlEventType(SDL_EVENT_WINDOW_METAL_VIEW_RESIZED)
        val WINDOW_MINIMIZED = SdlEventType(SDL_EVENT_WINDOW_MINIMIZED)
        val WINDOW_MAXIMIZED = SdlEventType(SDL_EVENT_WINDOW_MAXIMIZED)
        val WINDOW_RESTORED = SdlEventType(SDL_EVENT_WINDOW_RESTORED)
        val WINDOW_MOUSE_ENTER = SdlEventType(SDL_EVENT_WINDOW_MOUSE_ENTER)
        val WINDOW_MOUSE_LEAVE = SdlEventType(SDL_EVENT_WINDOW_MOUSE_LEAVE)
        val WINDOW_FOCUS_GAINED = SdlEventType(SDL_EVENT_WINDOW_FOCUS_GAINED)
        val WINDOW_FOCUS_LOST = SdlEventType(SDL_EVENT_WINDOW_FOCUS_LOST)
        val WINDOW_CLOSE_REQUESTED = SdlEventType(SDL_EVENT_WINDOW_CLOSE_REQUESTED)
        val WINDOW_HIT_TEST = SdlEventType(SDL_EVENT_WINDOW_HIT_TEST)
        val WINDOW_ICCPROF_CHANGED = SdlEventType(SDL_EVENT_WINDOW_ICCPROF_CHANGED)
        val WINDOW_DISPLAY_CHANGED = SdlEventType(SDL_EVENT_WINDOW_DISPLAY_CHANGED)
        val WINDOW_DISPLAY_SCALE_CHANGED = SdlEventType(SDL_EVENT_WINDOW_DISPLAY_SCALE_CHANGED)
        val WINDOW_SAFE_AREA_CHANGED = SdlEventType(SDL_EVENT_WINDOW_SAFE_AREA_CHANGED)
        val WINDOW_OCCLUDED = SdlEventType(SDL_EVENT_WINDOW_OCCLUDED)
        val WINDOW_ENTER_FULLSCREEN = SdlEventType(SDL_EVENT_WINDOW_ENTER_FULLSCREEN)
        val WINDOW_LEAVE_FULLSCREEN = SdlEventType(SDL_EVENT_WINDOW_LEAVE_FULLSCREEN)
        val WINDOW_DESTROYED = SdlEventType(SDL_EVENT_WINDOW_DESTROYED)
        val WINDOW_HDR_STATE_CHANGED = SdlEventType(SDL_EVENT_WINDOW_HDR_STATE_CHANGED)
        val KEY_DOWN = SdlEventType(SDL_EVENT_KEY_DOWN)
        val KEY_UP = SdlEventType(SDL_EVENT_KEY_UP)
        val TEXT_EDITING = SdlEventType(SDL_EVENT_TEXT_EDITING)
        val TEXT_INPUT = SdlEventType(SDL_EVENT_TEXT_INPUT)
        val KEYMAP_CHANGED = SdlEventType(SDL_EVENT_KEYMAP_CHANGED)
        val KEYBOARD_ADDED = SdlEventType(SDL_EVENT_KEYBOARD_ADDED)
        val KEYBOARD_REMOVED = SdlEventType(SDL_EVENT_KEYBOARD_REMOVED)
        val TEXT_EDITING_CANDIDATES = SdlEventType(SDL_EVENT_TEXT_EDITING_CANDIDATES)
        val SCREEN_KEYBOARD_SHOWN = SdlEventType(SDL_EVENT_SCREEN_KEYBOARD_SHOWN)
        val SCREEN_KEYBOARD_HIDDEN = SdlEventType(SDL_EVENT_SCREEN_KEYBOARD_HIDDEN)
        val MOUSE_MOTION = SdlEventType(SDL_EVENT_MOUSE_MOTION)
        val MOUSE_BUTTON_DOWN = SdlEventType(SDL_EVENT_MOUSE_BUTTON_DOWN)
        val MOUSE_BUTTON_UP = SdlEventType(SDL_EVENT_MOUSE_BUTTON_UP)
        val MOUSE_WHEEL = SdlEventType(SDL_EVENT_MOUSE_WHEEL)
        val MOUSE_ADDED = SdlEventType(SDL_EVENT_MOUSE_ADDED)
        val MOUSE_REMOVED = SdlEventType(SDL_EVENT_MOUSE_REMOVED)
        val JOYSTICK_AXIS_MOTION = SdlEventType(SDL_EVENT_JOYSTICK_AXIS_MOTION)
        val JOYSTICK_BALL_MOTION = SdlEventType(SDL_EVENT_JOYSTICK_BALL_MOTION)
        val JOYSTICK_HAT_MOTION = SdlEventType(SDL_EVENT_JOYSTICK_HAT_MOTION)
        val JOYSTICK_BUTTON_DOWN = SdlEventType(SDL_EVENT_JOYSTICK_BUTTON_DOWN)
        val JOYSTICK_BUTTON_UP = SdlEventType(SDL_EVENT_JOYSTICK_BUTTON_UP)
        val JOYSTICK_ADDED = SdlEventType(SDL_EVENT_JOYSTICK_ADDED)
        val JOYSTICK_REMOVED = SdlEventType(SDL_EVENT_JOYSTICK_REMOVED)
        val JOYSTICK_BATTERY_UPDATED = SdlEventType(SDL_EVENT_JOYSTICK_BATTERY_UPDATED)
        val JOYSTICK_UPDATE_COMPLETE = SdlEventType(SDL_EVENT_JOYSTICK_UPDATE_COMPLETE)
        val GAMEPAD_AXIS_MOTION = SdlEventType(SDL_EVENT_GAMEPAD_AXIS_MOTION)
        val GAMEPAD_BUTTON_DOWN = SdlEventType(SDL_EVENT_GAMEPAD_BUTTON_DOWN)
        val GAMEPAD_BUTTON_UP = SdlEventType(SDL_EVENT_GAMEPAD_BUTTON_UP)
        val GAMEPAD_ADDED = SdlEventType(SDL_EVENT_GAMEPAD_ADDED)
        val GAMEPAD_REMOVED = SdlEventType(SDL_EVENT_GAMEPAD_REMOVED)
        val GAMEPAD_REMAPPED = SdlEventType(SDL_EVENT_GAMEPAD_REMAPPED)
        val GAMEPAD_TOUCHPAD_DOWN = SdlEventType(SDL_EVENT_GAMEPAD_TOUCHPAD_DOWN)
        val GAMEPAD_TOUCHPAD_MOTION = SdlEventType(SDL_EVENT_GAMEPAD_TOUCHPAD_MOTION)
        val GAMEPAD_TOUCHPAD_UP = SdlEventType(SDL_EVENT_GAMEPAD_TOUCHPAD_UP)
        val GAMEPAD_SENSOR_UPDATE = SdlEventType(SDL_EVENT_GAMEPAD_SENSOR_UPDATE)
        val GAMEPAD_UPDATE_COMPLETE = SdlEventType(SDL_EVENT_GAMEPAD_UPDATE_COMPLETE)
        val GAMEPAD_STEAM_HANDLE_UPDATED = SdlEventType(SDL_EVENT_GAMEPAD_STEAM_HANDLE_UPDATED)
        val FINGER_DOWN = SdlEventType(SDL_EVENT_FINGER_DOWN)
        val FINGER_UP = SdlEventType(SDL_EVENT_FINGER_UP)
        val FINGER_MOTION = SdlEventType(SDL_EVENT_FINGER_MOTION)
        val FINGER_CANCELED = SdlEventType(SDL_EVENT_FINGER_CANCELED)
        val PINCH_BEGIN = SdlEventType(SDL_EVENT_PINCH_BEGIN)
        val PINCH_UPDATE = SdlEventType(SDL_EVENT_PINCH_UPDATE)
        val PINCH_END = SdlEventType(SDL_EVENT_PINCH_END)
        val CLIPBOARD_UPDATE = SdlEventType(SDL_EVENT_CLIPBOARD_UPDATE)
        val DROP_FILE = SdlEventType(SDL_EVENT_DROP_FILE)
        val DROP_TEXT = SdlEventType(SDL_EVENT_DROP_TEXT)
        val DROP_BEGIN = SdlEventType(SDL_EVENT_DROP_BEGIN)
        val DROP_COMPLETE = SdlEventType(SDL_EVENT_DROP_COMPLETE)
        val DROP_POSITION = SdlEventType(SDL_EVENT_DROP_POSITION)
        val AUDIO_DEVICE_ADDED = SdlEventType(SDL_EVENT_AUDIO_DEVICE_ADDED)
        val AUDIO_DEVICE_REMOVED = SdlEventType(SDL_EVENT_AUDIO_DEVICE_REMOVED)
        val AUDIO_DEVICE_FORMAT_CHANGED = SdlEventType(SDL_EVENT_AUDIO_DEVICE_FORMAT_CHANGED)
        val SENSOR_UPDATE = SdlEventType(SDL_EVENT_SENSOR_UPDATE)
        val PEN_PROXIMITY_IN = SdlEventType(SDL_EVENT_PEN_PROXIMITY_IN)
        val PEN_PROXIMITY_OUT = SdlEventType(SDL_EVENT_PEN_PROXIMITY_OUT)
        val PEN_DOWN = SdlEventType(SDL_EVENT_PEN_DOWN)
        val PEN_UP = SdlEventType(SDL_EVENT_PEN_UP)
        val PEN_BUTTON_DOWN = SdlEventType(SDL_EVENT_PEN_BUTTON_DOWN)
        val PEN_BUTTON_UP = SdlEventType(SDL_EVENT_PEN_BUTTON_UP)
        val PEN_MOTION = SdlEventType(SDL_EVENT_PEN_MOTION)
        val PEN_AXIS = SdlEventType(SDL_EVENT_PEN_AXIS)
        val CAMERA_DEVICE_ADDED = SdlEventType(SDL_EVENT_CAMERA_DEVICE_ADDED)
        val CAMERA_DEVICE_REMOVED = SdlEventType(SDL_EVENT_CAMERA_DEVICE_REMOVED)
        val CAMERA_DEVICE_APPROVED = SdlEventType(SDL_EVENT_CAMERA_DEVICE_APPROVED)
        val CAMERA_DEVICE_DENIED = SdlEventType(SDL_EVENT_CAMERA_DEVICE_DENIED)
        val RENDER_TARGETS_RESET = SdlEventType(SDL_EVENT_RENDER_TARGETS_RESET)
        val RENDER_DEVICE_RESET = SdlEventType(SDL_EVENT_RENDER_DEVICE_RESET)
        val RENDER_DEVICE_LOST = SdlEventType(SDL_EVENT_RENDER_DEVICE_LOST)
        val PRIVATE0 = SdlEventType(SDL_EVENT_PRIVATE0)
        val PRIVATE1 = SdlEventType(SDL_EVENT_PRIVATE1)
        val PRIVATE2 = SdlEventType(SDL_EVENT_PRIVATE2)
        val PRIVATE3 = SdlEventType(SDL_EVENT_PRIVATE3)
        val POLL_SENTINEL = SdlEventType(SDL_EVENT_POLL_SENTINEL)
        val USER = SdlEventType(SDL_EVENT_USER)

        private val names = mapOf(
            QUIT to "QUIT",
            TERMINATING to "TERMINATING",
            LOW_MEMORY to "LOW_MEMORY",
            WILL_ENTER_BACKGROUND to "WILL_ENTER_BACKGROUND",
            DID_ENTER_BACKGROUND to "DID_ENTER_BACKGROUND",
            WILL_ENTER_FOREGROUND to "WILL_ENTER_FOREGROUND",
            DID_ENTER_FOREGROUND to "DID_ENTER_FOREGROUND",
            LOCALE_CHANGED to "LOCALE_CHANGED",
            SYSTEM_THEME_CHANGED to "SYSTEM_THEME_CHANGED",
            DISPLAY_ORIENTATION to "DISPLAY_ORIENTATION",
            DISPLAY_ADDED to "DISPLAY_ADDED",
            DISPLAY_REMOVED to "DISPLAY_REMOVED",
            DISPLAY_MOVED to "DISPLAY_MOVED",
            DISPLAY_DESKTOP_MODE_CHANGED to "DISPLAY_DESKTOP_MODE_CHANGED",
            DISPLAY_CURRENT_MODE_CHANGED to "DISPLAY_CURRENT_MODE_CHANGED",
            DISPLAY_CONTENT_SCALE_CHANGED to "DISPLAY_CONTENT_SCALE_CHANGED",
            DISPLAY_USABLE_BOUNDS_CHANGED to "DISPLAY_USABLE_BOUNDS_CHANGED",
            WINDOW_SHOWN to "WINDOW_SHOWN",
            WINDOW_HIDDEN to "WINDOW_HIDDEN",
            WINDOW_EXPOSED to "WINDOW_EXPOSED",
            WINDOW_MOVED to "WINDOW_MOVED",
            WINDOW_RESIZED to "WINDOW_RESIZED",
            WINDOW_PIXEL_SIZE_CHANGED to "WINDOW_PIXEL_SIZE_CHANGED",
            WINDOW_METAL_VIEW_RESIZED to "WINDOW_METAL_VIEW_RESIZED",
            WINDOW_MINIMIZED to "WINDOW_MINIMIZED",
            WINDOW_MAXIMIZED to "WINDOW_MAXIMIZED",
            WINDOW_RESTORED to "WINDOW_RESTORED",
            WINDOW_MOUSE_ENTER to "WINDOW_MOUSE_ENTER",
            WINDOW_MOUSE_LEAVE to "WINDOW_MOUSE_LEAVE",
            WINDOW_FOCUS_GAINED to "WINDOW_FOCUS_GAINED",
            WINDOW_FOCUS_LOST to "WINDOW_FOCUS_LOST",
            WINDOW_CLOSE_REQUESTED to "WINDOW_CLOSE_REQUESTED",
            WINDOW_HIT_TEST to "WINDOW_HIT_TEST",
            WINDOW_ICCPROF_CHANGED to "WINDOW_ICCPROF_CHANGED",
            WINDOW_DISPLAY_CHANGED to "WINDOW_DISPLAY_CHANGED",
            WINDOW_DISPLAY_SCALE_CHANGED to "WINDOW_DISPLAY_SCALE_CHANGED",
            WINDOW_SAFE_AREA_CHANGED to "WINDOW_SAFE_AREA_CHANGED",
            WINDOW_OCCLUDED to "WINDOW_OCCLUDED",
            WINDOW_ENTER_FULLSCREEN to "WINDOW_ENTER_FULLSCREEN",
            WINDOW_LEAVE_FULLSCREEN to "WINDOW_LEAVE_FULLSCREEN",
            WINDOW_DESTROYED to "WINDOW_DESTROYED",
            WINDOW_HDR_STATE_CHANGED to "WINDOW_HDR_STATE_CHANGED",
            KEY_DOWN to "KEY_DOWN",
            KEY_UP to "KEY_UP",
            TEXT_EDITING to "TEXT_EDITING",
            TEXT_INPUT to "TEXT_INPUT",
            KEYMAP_CHANGED to "KEYMAP_CHANGED",
            KEYBOARD_ADDED to "KEYBOARD_ADDED",
            KEYBOARD_REMOVED to "KEYBOARD_REMOVED",
            TEXT_EDITING_CANDIDATES to "TEXT_EDITING_CANDIDATES",
            SCREEN_KEYBOARD_SHOWN to "SCREEN_KEYBOARD_SHOWN",
            SCREEN_KEYBOARD_HIDDEN to "SCREEN_KEYBOARD_HIDDEN",
            MOUSE_MOTION to "MOUSE_MOTION",
            MOUSE_BUTTON_DOWN to "MOUSE_BUTTON_DOWN",
            MOUSE_BUTTON_UP to "MOUSE_BUTTON_UP",
            MOUSE_WHEEL to "MOUSE_WHEEL",
            MOUSE_ADDED to "MOUSE_ADDED",
            MOUSE_REMOVED to "MOUSE_REMOVED",
            JOYSTICK_AXIS_MOTION to "JOYSTICK_AXIS_MOTION",
            JOYSTICK_BALL_MOTION to "JOYSTICK_BALL_MOTION",
            JOYSTICK_HAT_MOTION to "JOYSTICK_HAT_MOTION",
            JOYSTICK_BUTTON_DOWN to "JOYSTICK_BUTTON_DOWN",
            JOYSTICK_BUTTON_UP to "JOYSTICK_BUTTON_UP",
            JOYSTICK_ADDED to "JOYSTICK_ADDED",
            JOYSTICK_REMOVED to "JOYSTICK_REMOVED",
            JOYSTICK_BATTERY_UPDATED to "JOYSTICK_BATTERY_UPDATED",
            JOYSTICK_UPDATE_COMPLETE to "JOYSTICK_UPDATE_COMPLETE",
            GAMEPAD_AXIS_MOTION to "GAMEPAD_AXIS_MOTION",
            GAMEPAD_BUTTON_DOWN to "GAMEPAD_BUTTON_DOWN",
            GAMEPAD_BUTTON_UP to "GAMEPAD_BUTTON_UP",
            GAMEPAD_ADDED to "GAMEPAD_ADDED",
            GAMEPAD_REMOVED to "GAMEPAD_REMOVED",
            GAMEPAD_REMAPPED to "GAMEPAD_REMAPPED",
            GAMEPAD_TOUCHPAD_DOWN to "GAMEPAD_TOUCHPAD_DOWN",
            GAMEPAD_TOUCHPAD_MOTION to "GAMEPAD_TOUCHPAD_MOTION",
            GAMEPAD_TOUCHPAD_UP to "GAMEPAD_TOUCHPAD_UP",
            GAMEPAD_SENSOR_UPDATE to "GAMEPAD_SENSOR_UPDATE",
            GAMEPAD_UPDATE_COMPLETE to "GAMEPAD_UPDATE_COMPLETE",
            GAMEPAD_STEAM_HANDLE_UPDATED to "GAMEPAD_STEAM_HANDLE_UPDATED",
            FINGER_DOWN to "FINGER_DOWN",
            FINGER_UP to "FINGER_UP",
            FINGER_MOTION to "FINGER_MOTION",
            FINGER_CANCELED to "FINGER_CANCELED",
            PINCH_BEGIN to "PINCH_BEGIN",
            PINCH_UPDATE to "PINCH_UPDATE",
            PINCH_END to "PINCH_END",
            CLIPBOARD_UPDATE to "CLIPBOARD_UPDATE",
            DROP_FILE to "DROP_FILE",
            DROP_TEXT to "DROP_TEXT",
            DROP_BEGIN to "DROP_BEGIN",
            DROP_COMPLETE to "DROP_COMPLETE",
            DROP_POSITION to "DROP_POSITION",
            AUDIO_DEVICE_ADDED to "AUDIO_DEVICE_ADDED",
            AUDIO_DEVICE_REMOVED to "AUDIO_DEVICE_REMOVED",
            AUDIO_DEVICE_FORMAT_CHANGED to "AUDIO_DEVICE_FORMAT_CHANGED",
            SENSOR_UPDATE to "SENSOR_UPDATE",
            PEN_PROXIMITY_IN to "PEN_PROXIMITY_IN",
            PEN_PROXIMITY_OUT to "PEN_PROXIMITY_OUT",
            PEN_DOWN to "PEN_DOWN",
            PEN_UP to "PEN_UP",
            PEN_BUTTON_DOWN to "PEN_BUTTON_DOWN",
            PEN_BUTTON_UP to "PEN_BUTTON_UP",
            PEN_MOTION to "PEN_MOTION",
            PEN_AXIS to "PEN_AXIS",
            CAMERA_DEVICE_ADDED to "CAMERA_DEVICE_ADDED",
            CAMERA_DEVICE_REMOVED to "CAMERA_DEVICE_REMOVED",
            CAMERA_DEVICE_APPROVED to "CAMERA_DEVICE_APPROVED",
            CAMERA_DEVICE_DENIED to "CAMERA_DEVICE_DENIED",
            RENDER_TARGETS_RESET to "RENDER_TARGETS_RESET",
            RENDER_DEVICE_RESET to "RENDER_DEVICE_RESET",
            RENDER_DEVICE_LOST to "RENDER_DEVICE_LOST",
            PRIVATE0 to "PRIVATE0",
            PRIVATE1 to "PRIVATE1",
            PRIVATE2 to "PRIVATE2",
            PRIVATE3 to "PRIVATE3",
            POLL_SENTINEL to "POLL_SENTINEL",
            USER to "USER",
        )
    }
}
