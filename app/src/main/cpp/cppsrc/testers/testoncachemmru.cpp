/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */

#include "testoncachemmru.h"
#include <memory>

TestOnCacheMMRU::TestOnCacheMMRU()
{

}


TestOnCacheMMRU::~TestOnCacheMMRU() {
  onStop();
}

void  TestOnCacheMMRU::onStart(int32_t capacity)  {
  onStop() ;
  cache  =  new OnCacheMMRU<TKey *, Elem *>(capacity,  10);
  return;
}

void  TestOnCacheMMRU::onStop()  {
  if (cache) {
    delete  cache;
    cache  =  nullptr;
  }
  return;
}

void  TestOnCacheMMRU::insert(void  *elem)  {
  Elem  *e  =  static_cast<Elem *>(elem);
  cache->insertNode(&(e->key),
                    std::make_shared<Elem *>(e));
  return;
}

bool  TestOnCacheMMRU::exist(void  *elem)  {
  bool  re  =  false;
  Elem  *e  =  static_cast<Elem *>(elem);
  std::shared_ptr<Elem *> p_elem  =  cache->getData(&e->key);
  if (p_elem  &&  *p_elem.get() == e) {
     re  =  true;
  }
  return  re;
}

int  TestOnCacheMMRU::get_key_type()  {  return  0;  }
