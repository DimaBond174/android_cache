/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */

#ifndef TESTONCACHEMMRU_H
#define TESTONCACHEMMRU_H

#include  "../i/itester.h"
#include "../i/ikey.h"
#include "../caches/oncachemmru.h"

class TestOnCacheMMRU : public IAlgorithmTester {
 public:
  TestOnCacheMMRU();
  ~TestOnCacheMMRU() override;
  void  onStart(int32_t capacity)  override ;
  void  onStop()  override;
  int  get_key_type()  override ;
  void  insert(void  *elem)  override;
  bool  exist(void  *elem)  override;
 private:
  OnCacheMMRU<TKey *, Elem *>  *cache  =  nullptr;
};

#endif // TESTONCACHEMMRU_H
