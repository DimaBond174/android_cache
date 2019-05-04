package com.bond.oncache;

import com.bond.oncache.caches.OnCacheSMRU;
import com.bond.oncache.i.IKeyInt3;
import com.bond.oncache.objs.StaticConsts;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class OnCacheSMRU_UnitTest {
  final int TEST_SIZE  =  1000;
  final int CAPACITY  =  TEST_SIZE / 10;

  @Test
  public void cache_works() {
    final IKeyInt3 keys[]  =  new IKeyInt3[TEST_SIZE];
    OnCacheSMRU<IKeyInt3,  IKeyInt3> cache  =
        new OnCacheSMRU<IKeyInt3,  IKeyInt3>(CAPACITY, 10);

    Random r = new Random();
    for (int  i  = 0;  i < TEST_SIZE;  ++i) {
      int num  =  r.nextInt(StaticConsts.MAX_INT);
      keys[i]  =  new IKeyInt3(num, num, num);
      cache.insertNode(keys[i], keys[i]);
    } //while

    int found = 0;
    for (int  i  = 0;  i < TEST_SIZE;  ++i) {
      if (null  !=  cache.getData(keys[i])) {  ++found ;  }
    } //while

    assertEquals(CAPACITY, found);
  }

}