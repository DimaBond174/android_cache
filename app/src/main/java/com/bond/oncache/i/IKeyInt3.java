package com.bond.oncache.i;
/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */

public class IKeyInt3  implements  IKey {
  public final int  k1;
  public final int  k2;
  public final int  k3;
  private final int  hash;

  public IKeyInt3(int  k1,  int  k2,  int  k3)  {
    this.k1  =  k1;
    this.k2  =  k2;
    this.k3  =  k3;

    long  summ  =  k1  +  k2  +  k3;
    //  Java standard:
    hash = (int)(summ ^ (summ >>> 32));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof IKeyInt3)) return false;
    IKeyInt3 key = (IKeyInt3) o;
    return k1 == key.k1  &&  k2 == key.k2  &&  k3 == key.k3;
  }

  @Override
  public int hashCode() {
    return hash;
  }

  @Override
  public int  cmp(Object o)  {
    if (this == o)  return  0;
    IKeyInt3 other = (IKeyInt3) o;
    if  (k3  >  other.k3)  return  1;
    if  (k3  <  other.k3)  return  -1;
    if  (k2  >  other.k2)  return  1;
    if  (k2  <  other.k2)  return  -1;
    if  (k1  >  other.k1)  return  1;
    if  (k1  <  other.k1)  return  -1;
    return 0;
  }

  @Override
  public long get_hash()  {
    long  re  = k1  +  k2  +  k3;
    return  re >= 0? re : - re;
  }
}
