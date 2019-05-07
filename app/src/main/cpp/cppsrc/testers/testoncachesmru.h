/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */

#ifndef TESTONCACHESMRU_H
#define TESTONCACHESMRU_H
#include  "../i/itester.h"
#include "../i/ikey.h"
#include "../caches/oncachesmru.h"

class TestOnCacheSMRU : public IAlgorithmTester {
 public:
  TestOnCacheSMRU();
  ~TestOnCacheSMRU() override;
  void  onStart(int32_t capacity)  override ;
  void  onStop()  override;
  int  get_key_type()  override ;
  void  insert(void  *elem)  override;
  bool  exist(void  *elem)  override;

 private:
  OnCacheSMRU<TKey *, Elem *>  *cache  =  nullptr;
};

#endif // TESTONCACHESMRU_H
