

#ifndef SPECNET_SCOPEDLOCALREF_H
#define SPECNET_SCOPEDLOCALREF_H

#include <jni.h>

#include <stddef.h>

// A smart pointer that deletes a JNI local reference when it goes out of scope.
template<typename T>
class ScopedLocalRef {
public:
    ScopedLocalRef(JNIEnv* env, T localRef) : mEnv(env), mLocalRef(localRef) {
    }

    ~ScopedLocalRef() {
        reset();
    }

    void reset(T ptr = nullptr) {
        if (ptr != mLocalRef) {
            if (mLocalRef != nullptr) {
                mEnv->DeleteLocalRef(mLocalRef);
            }
            mLocalRef = ptr;
        }
    }


    T release() {
        T localRef = mLocalRef;
        mLocalRef = nullptr;
        return localRef;
    }

    T get() const {
        return mLocalRef;
    }

private:
    JNIEnv* mEnv;
    T mLocalRef;

    // Disallow copy and assignment.
    ScopedLocalRef(const ScopedLocalRef&);
    void operator=(const ScopedLocalRef&);
};


#endif //SPECNET_SCOPEDLOCALREF_H
