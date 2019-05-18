/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */

#include "testoncachemlru2.h"
#include <memory>

TestOnCacheMLRU2::TestOnCacheMLRU2()
{

}

TestOnCacheMLRU2::~TestOnCacheMLRU2() {
  onStop();
}

void  TestOnCacheMLRU2::onStop()  {
  if (cache) {
    delete  cache;
    cache  =  nullptr;
  }
  return;
}

void  TestOnCacheMLRU2::onStart(int32_t capacity)  {
  onStop() ;
  cache  =  new OnCacheMLRU<ElemNSizeKey *, ElemNSizeKey *>(capacity);
  return;
}

void  TestOnCacheMLRU2::insert(void  *elem)  {
  ElemNSizeKey  *e  =  static_cast<ElemNSizeKey *>(elem);
  cache->insertNode(e,  std::make_shared<ElemNSizeKey *>(e));
  return;
}

bool  TestOnCacheMLRU2::exist(void  *elem)  {
  bool  re  =  false;
  ElemNSizeKey  *e  =  static_cast<ElemNSizeKey *>(elem);
  std::shared_ptr<ElemNSizeKey *> p_elem  =  cache->getData(e);
  if (p_elem  &&  *p_elem.get() == e) {
     re  =  true;
  }
  return  re;
}

int  TestOnCacheMLRU2::get_key_type()  {  return  1;  }

