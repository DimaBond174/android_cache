package com.bond.oncache.caches;
/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */

import com.bond.oncache.i.IKey;
import com.bond.oncache.objs.ThreadLocalByte;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/*
 * Most used cache,  multithreaded (thread safe)
 * Those who are more actively used are more likely to survive the recircle.
 *  It used thread local landscape pointer
 *
 * Usage:
 *  OnCacheMMRU<>( capacity,  uselessness)
 *  capacity - how much items to store in RAM
 *  uselessness - limit of uselessness = do not evict if used count bigger
 *
 * Your key must implement interface IKey
*/

public class OnCacheMMRU<K extends IKey, V>  {
  // Public Java Interface :
  /////////////////////////////////////////////////////////////////
  public OnCacheMMRU(int  capacity,  int  uselessness)  {
    l_capacity  =  capacity;
    l_uselessness = uselessness;
    init();
  }


  public V getData(K key)  {
    long  l_hash  =  key.get_hash();//get_hash(key);
    //const uint32_t basketID  =  l_hash % l_capacity;
    int  basketID  =  (int)(l_hash  % l_capacity);
    //std::shared_lock<std::shared_mutex> lk(basket_locks[basketID]);
    basket_locks[basketID].readLock().lock();
    TONode  cur  =  baskets[basketID];
    V re = null == cur? null : getDataLocal(key, l_hash, cur);
    basket_locks[basketID].readLock().unlock();
    return re;
  }

  //void  insertNode  (const TKey  &key,  std::shared_ptr<TData>  &&data)  {
  public void  insertNode  (K  key,  V  data)  {
    long  l_hash  =  key.get_hash();
    int  basketID  =  (int)(l_hash % l_capacity);
    TONode  new_node  =  allocNode();
    new_node.hash  =  l_hash;
    new_node.key  =  key;
    new_node.data  =  data;
    insertN(new_node,  basketID);
    new_node.in_use.set(false); //.clear(std::memory_order_release);
  }  //  insertNode

  //  Private Incapsulation :
  /////////////////////////////////////////////////////////////////
  final  int  l_capacity;
  final  int  l_uselessness;
  TONode  baskets[];  //**baskets;
  ReentrantReadWriteLock  basket_locks[];

  //Allocations:
  AtomicInteger node_pool_id  =  new AtomicInteger() ;
  TONode  node_pool[]; //*node_pool;

  //Landscapes
  final  byte  landscape_l[]  =  new byte[256];
  final  ThreadLocalByte land_l_p  = new ThreadLocalByte();

  void  init()  {
    node_pool_id.set(0);
    for (int  i  =  0;  i  <  255;  ++i)  {
      if  (0  ==  i % 25)  {
        landscape_l[i]  =  2;
      } else if (0  ==  i % 5)  {
        landscape_l[i]  =  1;
      }  else  {
        landscape_l[i]  =  0;
      }
    }

    //new hash head always 2 hardcoded landscape_l[0]  =  2;
    landscape_l[255]  =  2;

    // init baskets:
    baskets  =  new TONode[l_capacity];
    node_pool  =  new TONode[l_capacity];
    basket_locks  =  new ReentrantReadWriteLock[l_capacity];
    for (int  i = l_capacity - 1;  i >= 0;  --i) {
      baskets[i]  =  null;
      basket_locks[i]  =  new ReentrantReadWriteLock();
      node_pool[i]  =  new TONode();
      node_pool[i].clear();
      node_pool[i].in_use.set(false);
    }
  } //init

  V getDataLocal(K key, long  l_hash, TONode  cur)  {
      while (null != cur  &&  cur.hash != l_hash) {
        cur  =  cur.fwdPtrH;
      }
      if (null  !=  cur)  {
//        int  cmp  =  compare(key,  cur->key);
        int  cmp  =  key.cmp(cur.key);
        if (0  ==  cmp)  {
          cur.used.getAndIncrement();
          return (V)cur.data;
        }
        if (cmp  <  0)  {
          //head is bigger, nothing to search
          return null;
        }
        int  h  =  2;
        while (h  >=  0)  {
          while (null != cur.fwdPtrsL[h])  {
//            cmp  =  compare(key,  cur->fwdPtrsL[h]->key);
            cmp  =  key.cmp(cur.fwdPtrsL[h].key);
            if (cmp  <  0)  {
              //found who bigger
              break;
            }  else  if (0  ==  cmp)  {
              cur.fwdPtrsL[h].used.getAndIncrement();
              return (V)cur.fwdPtrsL[h].data;  // found
            }
            cur  =  cur.fwdPtrsL[h];  //step on it
          }
          --h;
        }
      }
      return null;
  }

  TONode  allocNode()  {
    TONode  re  =  null;
    do {
      //uint32_t  id  =  node3_pool_id.fetch_add(1,  std::memory_order_relaxed);
      int id = node_pool_id.getAndAdd(1);
      if (id >= l_capacity) {
        //node3_pool_id.store(0, std::memory_order_relaxed);
        node_pool_id.set(0);
        id = 0; // data races will prevent through in_use.test_and_set
      }
      re = node_pool[id];
      int  used  =  re.used.get();//load(std::memory_order_acquire);
      if  (used  >  l_uselessness)  {
        //re->used.store(used - l_uselessness,  std::memory_order_relaxed);
        re.used.set(used - l_uselessness);
        continue;
      }
    } while (re.in_use.getAndSet(true));
    //} while (re->in_use.test_and_set(std::memory_order_acq_rel));

    if (null  !=  re.key) {
      //in case of data races here will be serialization:
      {
        //uint32_t basketID  =  re->hash % l_capacity;
        int  basketID  =  (int)(re.hash  % l_capacity);
        //std::unique_lock<std::shared_mutex> lk(basket_locks[basketID]);
        basket_locks[basketID].writeLock().lock();
        if (baskets[basketID].hash  ==  re.hash)  {
          delOnBasketHead(re,  baskets[basketID],  basketID);
        }  else  {
          delWithOtherHash(re,  baskets[basketID]);
        }
        basket_locks[basketID].writeLock().unlock();
      }
      re.clear();
    }
    //re->in_use.clear();
    return re;
  }  //  allocNode



  void delWithOtherHash(TONode  nodeToDel,  TONode  startSearch)  {
    //1. must find same hash:
    long  hash  =  nodeToDel.hash;
    TONode  cur  =  startSearch;
    while (null != cur.fwdPtrH  &&  cur.fwdPtrH.hash != hash) {
      cur  =  cur.fwdPtrH;
    }

    //2. if nodeToDel is head of hash queue:
    if  (nodeToDel  ==  cur.fwdPtrH)  {
      //This is the head of hash queue:
      cur.fwdPtrH = nodeToDel.fwdPtrsL[0]; // new hash head
      if (null != cur.fwdPtrH)  {
        cur  =  cur.fwdPtrH;  // aka tmp for work
        //  passing pointers to a new head:
        cur.fwdPtrH  =  nodeToDel.fwdPtrH;
        for (int  h  =  2;  h  >  cur.curHeight;  --h)  {
          // catchNode(cur, h, nodeToDel->fwdPtrsL[h]) ;
          cur.fwdPtrsL[h]  =  nodeToDel.fwdPtrsL[h];  // new head see what nodeToDel see now
        }
        cur.curHeight  =  2;
      }  else  {
        //nodeToDel was last with same hash
        cur.fwdPtrH  =  nodeToDel.fwdPtrH;
      }
    }  else  {
      //3. regular del:
      delInSameHashCmp3(nodeToDel,  cur.fwdPtrH) ;
    }
    return;
  }

  void delOnBasketHead(TONode  nodeToDel,  TONode  cur,
       int  basketID)  {
    if  (nodeToDel  ==  cur)  {
      //This is the head of hash queue:
      cur = nodeToDel.fwdPtrsL[0]; // new hash head
      if (null != cur)  {
        baskets[basketID] = cur;
        cur.fwdPtrH  =  nodeToDel.fwdPtrH;
        //assert (2 == nodeToDel->curHeight);
        for (int  h  =  2;  h  >  cur.curHeight;  --h)  {
          // catchNode(cur, h, nodeToDel->fwdPtrsL[h]) ;
          cur.fwdPtrsL[h]  =  nodeToDel.fwdPtrsL[h];  // new head see what nodeToDel see now
        }
        cur.curHeight  =  2;
      }  else  {
        baskets[basketID]  =  nodeToDel.fwdPtrH;
      }
    }  else  {
      //3. regular del:
      delInSameHashCmp3(nodeToDel,  cur) ;
    }
  }  //  delOnBasketHead

  // for setll cmp==3 or 4, no need  updatePathOutL
  void  delInSameHashCmp3(TONode  nodeToDel, TONode  hashHead)  {
    int  h  =  2;
    TONode updatePath[]  =  new TONode[3];
    TONode  cur  =  hashHead;  //updatePathOutH->fwdPtrH;
    //int  cmp  =  1;
    IKey  p_key  =  nodeToDel.key;
    while (h  >=  0)  {
      updatePath[h]  =  cur;
      while (nodeToDel != cur.fwdPtrsL[h]  &&  null != cur.fwdPtrsL[h])  {
//        if (compare(p_key,  cur->fwdPtrsL[h]->key)  <=  0)  {
        if (p_key.cmp(cur.fwdPtrsL[h].key)  <=  0)  {
          //found who bigger or target, go level down
          break;
        }
        updatePath[h]  =  cur;
        cur  =  cur.fwdPtrsL[h];  //step on it
      }
      //if found exactly, put on path:
      if (nodeToDel == cur.fwdPtrsL[h])  {
        updatePath[h]  =  cur;
      }
      --h;
    }

    for (h  =  nodeToDel.curHeight;  h  >=  0;  --h)  {
      updatePath[h].fwdPtrsL[h]  =  nodeToDel.fwdPtrsL[h];
    }
    return;
  } // delInSameHashCmp3

  void  insertN(TONode  new_node,  int  basketID)  {
    //std::unique_lock<std::shared_mutex> lk(basket_locks[basketID]);
    basket_locks[basketID].writeLock().lock();
    TONode  updatePathOutH  =  baskets[basketID];
    if (null  !=  updatePathOutH) {
      if (updatePathOutH.hash  ==  new_node.hash) {
        setOnBasketHead(new_node,  updatePathOutH,  basketID);
      }  else if (updatePathOutH.hash  >  new_node.hash)  {
        baskets[basketID]  =  new_node;
        new_node.fwdPtrH  =  updatePathOutH;
        new_node.curHeight  =  2;
      }  else  {
        setOther(new_node,  updatePathOutH);
      }
    } else {
      baskets[basketID]  =  new_node;
      new_node.curHeight  =  2;
    }
    basket_locks[basketID].writeLock().unlock();
    return;
  }  //  insertN



  void  setOther(TONode  new_node,  TONode  cur)  {
    final long  hash  =  new_node.hash;
    IKey  p_key  =  new_node.key;
    //TONode3  *cur  =  updatePathOutH;
    while (null  !=  cur.fwdPtrH  &&  hash > cur.fwdPtrH.hash)  {
      cur  =  cur.fwdPtrH;  //step on it
    }

    if (null  !=  cur.fwdPtrH  &&  cur.fwdPtrH.hash  ==  hash) {
      // same key jumps
//      int  cmp  =  compare(p_key,  cur->fwdPtrH->key);
      int  cmp  =  p_key.cmp(cur.fwdPtrH.key);
      if (cmp  <  0)  {
        TONode  prevHead  =  cur.fwdPtrH;
        cur.fwdPtrH  =  new_node;
        new_node.fwdPtrH  =  prevHead.fwdPtrH;
        prevHead.fwdPtrH  =  null;
        new_node.curHeight  =  2;
        prevHead.curHeight  =  landscape_l[land_l_p.getNextByte()];
        for (int  i  =  0; i  <=  2;  ++i)  {
          if (prevHead.curHeight  >=  i)  {
            new_node.fwdPtrsL[i]  =  prevHead;
          }  else  {
            new_node.fwdPtrsL[i]  =  prevHead.fwdPtrsL[i];
            prevHead.fwdPtrsL[i]  =  null;
          }
        }
        return;// 3; //must replace hash head in place
      }
      cur  =  cur.fwdPtrH;  //step on it
      if (0  ==  cmp) {
        cur.data  =  new_node.data;
        cur.key  =  new_node.key;
        new_node.clear();
        return;//  0;
      }
      int  h  =  2;
      TONode  updatePathOutL[]  =  new  TONode[3];
      while (h  >=  0)  {
        updatePathOutL[h]  =  cur;
        while (null  !=  cur.fwdPtrsL[h])  {
//          cmp  =  compare(p_key,  cur->fwdPtrsL[h]->key);
          cmp  =  p_key.cmp(cur.fwdPtrsL[h].key);
          if (cmp  <  0)  {
            //found who bigger
            break;
          }  else  if (0  ==  cmp)  {
            cur  =  cur.fwdPtrsL[h];
            cur.data  =  new_node.data;
            cur.key  =  new_node.key;
            new_node.clear();
            return;// 0;  // must replace at place
          }
          cur  =  cur.fwdPtrsL[h];  //step on it
          updatePathOutL[h]  =  cur;
        }
        --h;
      }
      new_node.curHeight  =  landscape_l[land_l_p.getNextByte()];
      for (int  i  =  0; i  <=  new_node.curHeight;  ++i)  {
        // catchNode(re, i, updatePathOutL[i]->fwdPtrsL[i]) ;
        new_node.fwdPtrsL[i]  =  updatePathOutL[i].fwdPtrsL[i];
        // catchNode(updatePathOutL[i], i, re) ;
        updatePathOutL[i].fwdPtrsL[i]  =  new_node;
      }
      return;// (cmp < 0) ? -1 : 1;
    }  else  {
      // here no a same hash
      //updatePathOutL[0] = updatePathOutL[1] = updatePathOutL[2] = nullptr;
      new_node.curHeight  =  2;
      new_node.fwdPtrH  =  cur.fwdPtrH;
      cur.fwdPtrH  =  new_node;
      return;// 4;
    }
    //return;// 8;
  }  //  setOther


  void setOnBasketHead(TONode  new_node, TONode  cur,  int  basketID)  {
    IKey  p_key  =  new_node.key;
//    int  cmp  =  compare(p_key,  cur->key);
    int  cmp  =  p_key.cmp(cur.key);
    if (cmp  <  0)  {
      //updatePathOutL[0]  =  updatePathOutL[1] =  updatePathOutL[2] =  cur;
      //new_node->curHeight  =  landscape_l[get_land_l_p()++];
      //TONode3  *prevHead  =  updatePathOutH->fwdPtrH;
      baskets[basketID]  =  new_node;
      new_node.fwdPtrH  =  cur.fwdPtrH;
      cur.fwdPtrH  =  null;
      new_node.curHeight  =  2;
      cur.curHeight  =  landscape_l[land_l_p.getNextByte()];
      for (int  i  =  0; i  <=  2;  ++i)  {
        if (cur.curHeight  >=  i)  {
          // catchNode(re, i, prevHead) ;
          new_node.fwdPtrsL[i]  =  cur;
        }  else  {
          // catchNode(re, i, prevHead->fwdPtrsL[i]) ;
          new_node.fwdPtrsL[i]  =  cur.fwdPtrsL[i];
          cur.fwdPtrsL[i]  =  null;
        }
      }
      return;// 3; //must replace hash head in place
    }
    if (0  ==  cmp) {
      cur.data  =  new_node.data;
      cur.key  =  new_node.key;
      new_node.clear();
      return;
    }
    TONode  updatePathOutL[] = new TONode[3];
    int  h  =  2;
    while (h  >=  0)  {
      updatePathOutL[h]  =  cur;
      while (null  !=  cur.fwdPtrsL[h])  {
        //cmp  =  compare(p_key,  cur->fwdPtrsL[h]->key);
        cmp  =  p_key.cmp(cur.fwdPtrsL[h].key);
        if (cmp  <  0)  {
          //found who bigger
          break;
        }  else  if (0  ==  cmp)  {
          cur  =  cur.fwdPtrsL[h];
          cur.data  =  new_node.data;
          cur.key  =  new_node.key;
          new_node.clear();
          return;// 0;  // must replace at place
        }
        cur  =  cur.fwdPtrsL[h];  //step on it
        updatePathOutL[h]  =  cur;
      }
      --h;
    }
    new_node.curHeight  =  landscape_l[land_l_p.getNextByte()];
    for (int  i  =  0; i  <=  new_node.curHeight;  ++i)  {
      // catchNode(re, i, updatePathOutL[i]->fwdPtrsL[i]) ;
      new_node.fwdPtrsL[i]  =  updatePathOutL[i].fwdPtrsL[i];
      // catchNode(updatePathOutL[i], i, re) ;
      updatePathOutL[i].fwdPtrsL[i]  =  new_node;
    }
    return;// (cmp < 0) ? -1 : 1;
  }

  class  TONode<K extends IKey, V>  {
    //std::atomic<uint32_t>  used  { 0 };
    final AtomicInteger used = new AtomicInteger();
    TONode  fwdPtrH = null;
    TONode  fwdPtrsL[] =  new TONode[3]; // cmp jumps

    K  key;
    V  data;

    long  hash;
    byte  curHeight;  // ==SKIPHEIGHT-1 to CPU economy

    //volatile boolean in_use  =  false;
    final AtomicBoolean in_use = new AtomicBoolean();

    void  clear() {
      key  =  null;
      fwdPtrH  =  null;
      fwdPtrsL[0]  =  null;
      fwdPtrsL[1]  =  null;
      fwdPtrsL[2]  =  null;
      used.set(0);
    }
  };  //  TONode


}  //  OnCacheMLRU
