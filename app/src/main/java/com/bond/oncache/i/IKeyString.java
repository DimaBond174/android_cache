package com.bond.oncache.i;
/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */

public class IKeyString implements  IKey  {
  public final String key;
  public final int  key_len;
  //private final long  hash;

  public IKeyString(String  key)  {
    this.key  =  key;
    key_len  =  key.length();
    //hash  =  calc(key);
  }

//  long calc(String  key)  {
//    long  re  =  0;
//    int  len  =  key.length();
//    for (int  i  =  0;  i  <  24  &&  i  <  len;  ++i)  {
//      re += 128 + key.charAt(i);
//    }
//    return  re;
//  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof IKeyString)) return false;
    IKeyString other = (IKeyString) o;
    return key.equals(other.key);
  }

  @Override
  public int hashCode() {
    return key.hashCode();
  }

  @Override
  public int  cmp(Object o)  {
    if (this == o)  return  0;
    IKeyString other = (IKeyString) o;
    int  cmp  =  key_len - other.key_len;
    return  0 == cmp? key.compareTo(other.key) : cmp;
  }

  @Override
  public long get_hash()  {
    long re  =  0L;
    final int len = key.length();
//    for (int  i  =  0;   i  <  24  &&  i  <  len;  ++i)  {
//      re += 128 + key.charAt(i);
//    }
//    return re;
    for (int  i  =  0;   i  <  24  &&  i  <  len;  ++i)  {
      re += key.charAt(i);
    }
    return re < 0L ?  -re : re;
  }
}
