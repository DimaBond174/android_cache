/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */

#ifndef TESTONCACHEMMRU2_H
#define TESTONCACHEMMRU2_H

#include  "../i/itester.h"
#include "../i/ikey2.h"
#include "../caches/oncachemmru.h"

class TestOnCacheMMRU2 : public IAlgorithmTester {
public:
 TestOnCacheMMRU2();
 ~TestOnCacheMMRU2() override;
  void  onStart(int32_t capacity)  override ;
  void  onStop()  override;
  int  get_key_type()  override ;
  void  insert(void  *elem)  override;
  bool  exist(void  *elem)  override;
private:
 OnCacheMMRU<ElemNSizeKey *, ElemNSizeKey *>  *cache  =  nullptr;
};

#endif // TESTONCACHEMMRU2_H
