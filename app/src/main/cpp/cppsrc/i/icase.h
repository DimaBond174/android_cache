/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */

#ifndef ITESTCASE_H
#define ITESTCASE_H
#include <string>
#include <memory>
#include "itester.h"

class ITestCase  {
 public:
  ITestCase()  =  default;
  virtual ~ITestCase()  {  }
  virtual  void  stop()  =  0;
  virtual  void  warmUP (int32_t capacity, std::shared_ptr<IAlgorithmTester>  tester)  =  0;
  virtual  int  get_key_type()  =  0;
  virtual  void  doTest(int32_t  insert_threads,
      int32_t  search_threads,  int32_t  max_items) = 0;
 private:
  ITestCase(const  ITestCase&)  =  delete;
  ITestCase& operator=(const  ITestCase&)  =  delete;
};

#endif // ITESTCASE_H
