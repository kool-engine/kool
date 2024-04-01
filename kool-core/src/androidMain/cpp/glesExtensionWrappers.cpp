#include <jni.h>
#include <EGL/egl.h>
#include <string>

extern "C" {

typedef uint32_t glsizei;
typedef uint32_t glenum;

void (*ClipControlEXT)(glenum, glenum) = NULL;

void (*QueryCounterEXT)(uint32_t, glenum) = NULL;
void (*GetQueryObjecti64vEXT)(uint32_t, glenum, int64_t*) = NULL;
void (*GetQueryObjectui64vEXT)(uint32_t, glenum, uint64_t*) = NULL;

JNIEXPORT jboolean JNICALL enableEXTclipControl(JNIEnv *env, jobject) {
    ClipControlEXT = reinterpret_cast<void (*)(uint32_t, glenum)>(eglGetProcAddress("glClipControlEXT"));
    return ClipControlEXT != NULL;
}

JNIEXPORT void JNICALL clipControl(jint origin, jint depth) {
    if (ClipControlEXT != NULL) {
        ClipControlEXT(static_cast<glenum>(origin), static_cast<glenum>(depth));
    }
}

JNIEXPORT jboolean JNICALL enableEXTdisjointTimerQuery(JNIEnv *env, jobject) {
    QueryCounterEXT = reinterpret_cast<void (*)(uint32_t, glenum)>(eglGetProcAddress("glQueryCounterEXT"));
    GetQueryObjecti64vEXT = reinterpret_cast<void (*)(uint32_t, glenum, int64_t*)>(eglGetProcAddress("glGetQueryObjecti64vEXT"));
    GetQueryObjectui64vEXT = reinterpret_cast<void (*)(uint32_t, glenum, uint64_t*)>(eglGetProcAddress("glGetQueryObjectui64vEXT"));
    return
            QueryCounterEXT != NULL &&
            GetQueryObjecti64vEXT != NULL &&
            GetQueryObjectui64vEXT != NULL;
}

JNIEXPORT void JNICALL queryCounter(jint id, jint target) {
    if (QueryCounterEXT != NULL) {
        QueryCounterEXT(static_cast<uint32_t>(id), static_cast<glenum>(target));
    }
}

JNIEXPORT jlong JNICALL getQueryObjecti64(jint id, jint pname) {
    if (GetQueryObjecti64vEXT == NULL) {
        return 0;
    }
    int64_t param;
    GetQueryObjecti64vEXT(static_cast<uint32_t>(id), static_cast<glenum>(pname), &param);
    return param;
}

JNIEXPORT jlong JNICALL getQueryObjectui64(jint id, jint pname) {
    if (GetQueryObjectui64vEXT == NULL) {
        return 0;
    }
    uint64_t param;
    GetQueryObjectui64vEXT(static_cast<uint32_t>(id), static_cast<glenum>(pname), &param);
    return param;
}

JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* env;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }

    // Find your class. JNI_OnLoad is called from the correct class loader context for this to work.
    jclass c = env->FindClass("de/fabmax/kool/pipeline/backend/gl/GlesExtensions");
    if (c == nullptr) return JNI_ERR;

    // Register your class' native methods.
    static const JNINativeMethod methods[] = {
        { "enableEXTclipControl", "()Z", reinterpret_cast<void*>(enableEXTclipControl) },
        { "clipControl", "(II)V", reinterpret_cast<void*>(clipControl) },

        { "enableEXTdisjointTimerQuery", "()Z", reinterpret_cast<void*>(enableEXTdisjointTimerQuery) },
        { "queryCounter", "(II)V", reinterpret_cast<void*>(queryCounter) },
        { "getQueryObjecti64", "(II)J", reinterpret_cast<void*>(getQueryObjecti64) },
        { "getQueryObjectui64", "(II)J", reinterpret_cast<void*>(getQueryObjectui64) },
    };
    int rc = env->RegisterNatives(c, methods, sizeof(methods) / sizeof(JNINativeMethod));
    if (rc != JNI_OK) return rc;

    return JNI_VERSION_1_6;
}

} // extern "C"
