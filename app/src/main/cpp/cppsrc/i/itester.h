/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */

#ifndef ITESTER_H
#define ITESTER_H

#include <stdint.h>

/*
 * Common interface for
 * Property container pattern
 */
class IAlgorithmTester  {
 public:
  IAlgorithmTester()  =  default;
  virtual ~IAlgorithmTester()  {  }
  virtual void  onStart(int32_t capacity)  =  0;
  virtual void  onStop()  =  0;
  virtual  int  get_key_type()  =  0;
  virtual void  insert(void  *elem)  =  0;
  virtual bool  exist(void  *elem)  =  0;
 private:
  IAlgorithmTester(const  IAlgorithmTester&)  =  delete;
  IAlgorithmTester& operator=(const  IAlgorithmTester&)  =  delete;
};

#endif // ITESTER_H
