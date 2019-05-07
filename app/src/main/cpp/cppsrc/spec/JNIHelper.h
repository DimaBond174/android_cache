//
// Created by dbond on 18.02.18.
//

#ifndef SPECNET_JNIHELPER_H
#define SPECNET_JNIHELPER_H

#include <jni.h>
#include <atomic>
#include <mutex>

class JNIHelper {
public:
    JNIHelper();
    ~JNIHelper();

    int jni_OnLoad(JavaVM* vm);

    JNIEnv* attachCurrentThread();
    void detachCurrentThread();


private:
    const char* kTAG = "JNIHelper";
    std::mutex jni_mutex;
    std::atomic<JavaVM*> p_javaVM {nullptr};

};


#endif //SPECNET_JNIHELPER_H
