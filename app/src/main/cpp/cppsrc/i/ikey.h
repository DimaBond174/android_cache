/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */

#ifndef IKEY_H
#define IKEY_H

#include <stdlib.h>
#include <stdint.h>
#include <string>


class TKey  {
 public:  
  TKey(uint64_t  p0  =  0,  uint64_t  p1  =  0,  uint64_t  p2  =  0)  {
    keyArray[0]  =  p0;
    keyArray[1]  =  p1;
    keyArray[2]  =  p2;
  }

  TKey  & operator=(TKey const  &rhl)  {
    keyArray[0]  =  rhl.keyArray[0];
    keyArray[1]  =  rhl.keyArray[1];
    keyArray[2]  =  rhl.keyArray[2];
    return  *this;
  }

  TKey(const  TKey  &rhl)  {
    keyArray[0]  =  rhl.keyArray[0];
    keyArray[1]  =  rhl.keyArray[1];
    keyArray[2]  =  rhl.keyArray[2];
  }

  bool  operator==(TKey const  &rhl) const  {
    return  keyArray[0]  ==  rhl.keyArray[0]
            && keyArray[1]  ==  rhl.keyArray[1]
            && keyArray[2]  ==  rhl.keyArray[2];
  }

//  static bool  do_worst_case;
  static constexpr int  keyLongSize  =  3;
  uint64_t  keyArray[keyLongSize];
  uint64_t  hash() const  {
    const uint64_t re  =  keyArray[0]  +  keyArray[1]  +  keyArray[2];
//    if  (do_worst_case)  {
//      return  (re % 1000);
//    }
    return  (re < 9223372036854775807ll)?  re  :  (re >> 1);
  }

  int  cmp(TKey const  *other)  const  {
    if  (keyArray[2]  >  other->keyArray[2])  return  1;
    if  (keyArray[2]  <  other->keyArray[2])  return  -1;
    if  (keyArray[1]  >  other->keyArray[1])  return  1;
    if  (keyArray[1]  <  other->keyArray[1])  return  -1;
    if  (keyArray[0]  >  other->keyArray[0])  return  1;
    if  (keyArray[0]  <  other->keyArray[0])  return  -1;
    return 0;
  }

  bool operator<(const TKey  &r)  const  {
    if  (keyArray[0]  <  r.keyArray[0])  return true;
    if  (keyArray[0]  >  r.keyArray[0])  return false;
    if  (keyArray[1]  <  r.keyArray[1])  return true;
    if  (keyArray[1]  >  r.keyArray[1])  return false;
    if  (keyArray[2]  <  r.keyArray[2])  return true;
    if  (keyArray[2]  >  r.keyArray[2])  return false;
        // Otherwise both are equal
    return false;
  }
};  //  TKey


  struct TEqual
  {
   bool operator()(const TKey&  l, const TKey&  r) const noexcept {
     return 0  ==  l.cmp(&r);
   }
  };

  struct  Elem  {
    Elem() {}
    Elem(uint64_t  p0,  uint64_t  p1,  uint64_t  p2)
          : key(p0,  p1,  p2) { }
    Elem(const Elem  &other)  :  key(other.key)  {  }
    Elem(const Elem  *other)  :  key(other->key)  {  }
    Elem & operator=(const  Elem &rhv)  {
      if (&rhv  !=  this) {
        key  =  rhv.key;
      }
      return *this;
    }

    TKey key;
  };

  inline int compare (const TKey  *lhv,  const TKey  *rhv) {
    return  lhv->cmp(rhv);
  }

  inline uint64_t  get_hash(const TKey  *lhv) {
    return lhv->hash();
  }
#endif // IKEY_H
