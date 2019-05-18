/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */

#ifndef TESTONCACHEMLRU2_H
#define TESTONCACHEMLRU2_H

#include  "../i/itester.h"
#include "../i/ikey2.h"
#include "../caches/oncachemlru.h"

class TestOnCacheMLRU2 : public IAlgorithmTester {
public:
 TestOnCacheMLRU2();
 ~TestOnCacheMLRU2() override;
  void  onStart(int32_t capacity)  override ;
  void  onStop()  override;
  int  get_key_type()  override ;
  void  insert(void  *elem)  override;
  bool  exist(void  *elem)  override;
private:
 OnCacheMLRU<ElemNSizeKey *, ElemNSizeKey *>  *cache  =  nullptr;
};

#endif // TESTONCACHEMLRU2_H
