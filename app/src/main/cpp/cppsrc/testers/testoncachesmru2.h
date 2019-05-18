/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */

#ifndef TESTONCACHESMRU2_H
#define TESTONCACHESMRU2_H

#include  "../i/itester.h"
#include "../i/ikey2.h"
#include "../caches/oncachesmru.h"

class TestOnCacheSMRU2 : public IAlgorithmTester {
public:
 TestOnCacheSMRU2();
 ~TestOnCacheSMRU2() override;
  void  onStart(int32_t capacity)  override ;
  void  onStop()  override;
  int  get_key_type()  override ;
  void  insert(void  *elem)  override;
  bool  exist(void  *elem)  override;
private:
 OnCacheSMRU<ElemNSizeKey *, ElemNSizeKey *>  *cache  =  nullptr;
};

#endif // TESTONCACHESMRU2_H
