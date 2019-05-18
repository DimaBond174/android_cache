/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */

#ifndef ANDROID_CACHE_TESTPRESENTER_H
#define ANDROID_CACHE_TESTPRESENTER_H

#include <jni.h>
#include "spec/JNIHelper.h"
#include "i/icase.h"
#include <memory>
#include <shared_mutex>

class TestPresenter {
 public:
  int  jni_OnLoad(JavaVM* vm);
  void  setNDKtestCaseInt3(jint  *cData, jint  rawDataLen);
  void  setNDKtestCaseKeyString(const char * strData,  jsize  strLen,
      jint  maxItems);
  void  warmUP(jint  cppTesterID, jint  capacity);
  void  doTest(int32_t  insert_threads,
               int32_t  search_threads,  int32_t  max_items);
  void  stopTest();
 private:
  JNIHelper  jniHelper;
  std::shared_ptr<ITestCase>  test_case;
  std::shared_mutex  test_case_mutex;

  std::shared_ptr<ITestCase> get_test_case();
  void  set_test_case(std::shared_ptr<ITestCase>  new_test_case);
};


#endif //ANDROID_CACHE_TESTPRESENTER_H
