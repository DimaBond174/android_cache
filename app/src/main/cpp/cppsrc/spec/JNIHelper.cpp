//
// Created by dbond on 18.02.18.
//

#include "JNIHelper.h"
#include "ScopedLocalRef.h"
#include "speclogger.h"

JNIHelper::JNIHelper() {

}

JNIHelper::~JNIHelper() {
}

int JNIHelper::jni_OnLoad(JavaVM* vm) {
    int re = JNI_ERR;

    std::lock_guard<std::mutex> raii (jni_mutex);
    try {
        do {
            JNIEnv* env;
            jint res = vm->GetEnv((void**)&env, JNI_VERSION_1_6);
            if (JNI_OK!=res) {
                LOGE("Error vm->GetEnv((void**)&env, JNI_VERSION_1_6) = %d", res);
                break; // JNI version not supported.
            }

        } while (0);

        p_javaVM.store(vm, std::memory_order_release);
        re = JNI_VERSION_1_6;
    } catch (...) {
        LOGE("Error jni_OnLoad(JavaVM* vm)");
    }

    return re;
}

JNIEnv* JNIHelper::attachCurrentThread() {
    JNIEnv* env = nullptr;
    JavaVM* vm = p_javaVM.load(std::memory_order_acquire);
    if (nullptr != vm) {
        jint res = vm->GetEnv((void**)&env, JNI_VERSION_1_6);
        if (res == JNI_OK) {
            return env;
        } else {
            res = vm->AttachCurrentThread(&env, NULL);
            if (JNI_OK != res) {
                LOGE("Failed to AttachCurrentThread, ErrorCode = %d", res);
            }
        }
    }
    return env;
}

void JNIHelper::detachCurrentThread() {
    JavaVM* vm = p_javaVM.load(std::memory_order_acquire);
    if (nullptr != vm) {
        vm->DetachCurrentThread();
    }
    return;
}

//---------------------------------------------------------------------------
// Init
//---------------------------------------------------------------------------
