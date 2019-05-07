#include <jni.h>
#include <string>
#include "cppsrc/TestPresenter.h"


extern "C" JNIEXPORT jstring JNICALL
Java_com_bond_oncache_MainActivity_stringFromJNI(
    JNIEnv *env,
    jobject /* this */) {
  std::string hello = "Hello from C++";
  return env->NewStringUTF(hello.c_str());
}


TestPresenter  testPresenter;

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
  return  testPresenter.jni_OnLoad(vm);
}

JNIEXPORT void JNICALL Java_com_bond_oncache_TestPresenter_setNDKtestCaseInt3  (
    JNIEnv *env,
    jobject /* this */,
    jintArray rawData,
    jint rawDataLen)
{
  jint * cData = env->GetIntArrayElements(rawData, 0);
  testPresenter.setNDKtestCaseInt3(cData, rawDataLen);
  env->ReleaseIntArrayElements(rawData, cData, JNI_ABORT);

  return;
}  //  setNDKtestCaseInt3

JNIEXPORT void JNICALL Java_com_bond_oncache_TestPresenter_warmUP  (
    JNIEnv *env,
    jobject /* this */,
    jint cppTesterID,
    jint capacity)
{
  testPresenter.warmUP(cppTesterID, capacity);
  return;
}  //  warmUP


JNIEXPORT void JNICALL Java_com_bond_oncache_TestPresenter_runCppTest  (
    JNIEnv *env,
    jobject /* this */,
    jint insert_threads,
    jint search_threads,
    jint max_items)
{
  testPresenter.doTest(insert_threads, search_threads,  max_items);
  return;
}  //  runCppTest



JNIEXPORT void JNICALL Java_com_bond_oncache_TestPresenter_stopCppTest  (
    JNIEnv *env,
    jobject /* this */)
{
  testPresenter.stopTest();
  return;
}  //  stopCppTest

#ifdef __cplusplus
}
#endif
