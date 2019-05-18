/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */

#include "TestPresenter.h"
#include "cases/testcase1.h"
#include "cases/testcase2.h"
#include "testers/testoncachesmru.h"
#include "testers/testoncachemlru.h"
#include "testers/testoncachemmru.h"
#include "testers/testoncachesmru2.h"
#include "testers/testoncachemlru2.h"
#include "testers/testoncachemmru2.h"

int  TestPresenter::jni_OnLoad(JavaVM* vm)  {
  return jniHelper.jni_OnLoad(vm);
}  // jni_OnLoad


void  TestPresenter::setNDKtestCaseInt3(jint  *cData, jint  rawDataLen)  {
  set_test_case(std::make_shared<TestCase1>(cData, rawDataLen));
}  //  setNDKtestCaseInt3

void  TestPresenter::setNDKtestCaseKeyString(const char * strData,
    jsize  strLen,  jint  maxItems)  {
  set_test_case(std::make_shared<TestCase2>(strData,  strLen,  maxItems));
}

std::shared_ptr<ITestCase> TestPresenter::get_test_case()  {
  std::shared_lock<std::shared_mutex> lk(test_case_mutex);
  return  test_case;
}

void  TestPresenter::set_test_case(std::shared_ptr<ITestCase>  new_test_case) {
  std::unique_lock<std::shared_mutex> lk(test_case_mutex);
  if (test_case)  {
    test_case.get()->stop();
  }
  test_case  =  new_test_case;
  return;
}

void  TestPresenter::warmUP(jint  cppTesterID,  jint  capacity)  {
  std::shared_ptr<ITestCase>  test_case  =  get_test_case();
  if (test_case) {
    std::shared_ptr<IAlgorithmTester> tester;
    switch (cppTesterID) {
      case 0:
        tester = std::make_shared<TestOnCacheSMRU>();
        break;
      case 1:
        tester = std::make_shared<TestOnCacheMLRU>();
        break;
      case 2:
        tester = std::make_shared<TestOnCacheMMRU>();
        break;
      case 3:
        tester = std::make_shared<TestOnCacheSMRU2>();
        break;
      case 4:
        tester = std::make_shared<TestOnCacheMLRU2>();
        break;
      case 5:
        tester = std::make_shared<TestOnCacheMMRU2>();
        break;
      default:
        break;
    };
    if (tester) {
      if (test_case.get()->get_key_type()
          == tester.get()->get_key_type()) {
        test_case.get()->warmUP(capacity,  tester);
      }  else  {
        test_case.get()->warmUP(0,  nullptr);
      }
    } else {
      test_case.get()->warmUP(0,  nullptr);
    }
  }
  return;
}


void  TestPresenter::doTest(int32_t  insert_threads,
             int32_t  search_threads,  int32_t  max_items) {
  std::shared_ptr<ITestCase>  test_case  =  get_test_case();
  if (test_case) {
    test_case.get()->doTest(insert_threads,  search_threads,  max_items);
  }
  return;
}

void  TestPresenter::stopTest() {
  std::shared_ptr<ITestCase>  test_case  =  get_test_case();
  if (test_case) {
    test_case.get()->stop();
  }
  set_test_case(nullptr);
  return;
}

