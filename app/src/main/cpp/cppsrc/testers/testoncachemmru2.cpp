/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */

#include "testoncachemmru2.h"
#include <memory>

TestOnCacheMMRU2::TestOnCacheMMRU2()
{

}

TestOnCacheMMRU2::~TestOnCacheMMRU2() {
  onStop();
}

void  TestOnCacheMMRU2::onStop()  {
  if (cache) {
    delete  cache;
    cache  =  nullptr;
  }
  return;
}

void  TestOnCacheMMRU2::onStart(int32_t capacity)  {
  onStop() ;
  cache  =  new OnCacheMMRU<ElemNSizeKey *, ElemNSizeKey *>(capacity,  10);
  return;
}


void  TestOnCacheMMRU2::insert(void  *elem)  {
  ElemNSizeKey  *e  =  static_cast<ElemNSizeKey *>(elem);
  cache->insertNode(e,  std::make_shared<ElemNSizeKey *>(e));
  return;
}

bool  TestOnCacheMMRU2::exist(void  *elem)  {
  bool  re  =  false;
  ElemNSizeKey  *e  =  static_cast<ElemNSizeKey *>(elem);
  std::shared_ptr<ElemNSizeKey *> p_elem  =  cache->getData(e);
  if (p_elem  &&  *p_elem.get() == e) {
     re  =  true;
  }
  return  re;
}

int  TestOnCacheMMRU2::get_key_type()  {  return  1;  }


