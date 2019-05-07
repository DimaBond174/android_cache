/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */

#ifndef TESTONCACHEMLRU_H
#define TESTONCACHEMLRU_H

#include  "../i/itester.h"
#include "../i/ikey.h"
#include "../caches/oncachemlru.h"

class TestOnCacheMLRU : public IAlgorithmTester {
 public:
  TestOnCacheMLRU();
  ~TestOnCacheMLRU() override;
  void  onStart(int32_t capacity)  override ;
  void  onStop()  override;
  int  get_key_type()  override ;
  void  insert(void  *elem)  override;
  bool  exist(void  *elem)  override;
 private:
  OnCacheMLRU<TKey *, Elem *>  *cache  =  nullptr;
};

#endif // TESTONCACHEMLRU_H
