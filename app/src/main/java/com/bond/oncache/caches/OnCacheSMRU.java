package com.bond.oncache.caches;
/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */


import com.bond.oncache.i.IKey;



/*
 * Most used cache,  single threaded (thread UNsafe)
 * Those who are more actively used are more likely to survive the recircle.
 *
 * Usage:
 *  OnCacheMMRU<>(int   capacity,  int  uselessness)
 *  capacity - how much items to store in RAM
 *  uselessness - limit of uselessness = do not evict if used count bigger
 *
 * Your key must implement interface IKey
*/
public class OnCacheSMRU<K extends IKey, V> {
  // Public Java Interface :
  /////////////////////////////////////////////////////////////////
  public OnCacheSMRU(int  capacity,  int  uselessness)  {
    l_uselessness  =  uselessness;
    l_capacity  =  capacity;
    init();
  }

  public void  clear() {
    node_pool  =  null;
    baskets  =  null;
    updatePathOutH  =  null;
    updatePathOutL  =  null;
  }


  public V getData(K key)  {
    long  l_hash  =  key.get_hash();
    PTONode  pcur  =  baskets[(int)(l_hash % l_capacity)];
    if (null != pcur.ptr)  {
//      while (cur  &&  cur->hash != l_hash) {
//        cur  =  cur->fwdPtrH;
//      }
      while (null != pcur.ptr  &&  pcur.ptr.hash != l_hash)  {
        pcur  =  pcur.ptr.fwdPtrH;
      }
      if (null != pcur  &&  null != pcur.ptr)  {
        TONode  cur  =  pcur.ptr;
        int  cmp  =  key.cmp(cur.key);
        if (0  ==  cmp)  {
          ++cur.used;
          return (V)cur.data;
        }
        if (cmp  <  0)  {
          //head is bigger, nothing to search
          return null;
        }
        int  h  =  2;
        while (h  >=  0)  {
          while (null != cur.fwdPtrsL[h])  {
            cmp  =  key.cmp(cur.fwdPtrsL[h].key);
            if (cmp  <  0)  {
              //found who bigger
              break;
            }  else  if (0  ==  cmp)  {
              ++cur.fwdPtrsL[h].used;
              return (V)(cur.fwdPtrsL[h].data);  // found
            }
            cur  =  cur.fwdPtrsL[h];  //step on it
          }
          --h;
        }
      }
    }
    return null;
  }

  public void  insertNode  (K  key,  V  data)  {
//    if (key->keyArray[0]==139106
//        || key->keyArray[0]==139326
//        || key->keyArray[0]==142556) {
//        std::cout<<"\n";
//    }
    long  hash  =  key.get_hash();//get_hash(key);
    int  basketID  =  (int)(hash % l_capacity);
    updatePathOutH  =  baskets[basketID];
    int  cmp  =  (null  !=  updatePathOutH.ptr)? setll(hash,  key) : 5;
    if  (0  ==  cmp)  {
      TONode  cur  =  updatePathOutL[0];//*updatePathOutH;
      cur.key  =  key;
      cur.data  =  data;
      cur.used  =  l_uselessness;
    }  else  {
      //insert new node:
      allocNode(hash,  key,  data,  basketID,  cmp);
    }
  }  //  insertNode

  //  Private Incapsulation :
  /////////////////////////////////////////////////////////////////
  final  int  l_uselessness;
  final  int  l_capacity;
  PTONode  baskets[];  //**baskets;
  PTONode  updatePathOutH; //**updatePathOutH;  //pointer to pointer
  TONode  updatePathOutL[]; //*updatePathOutL[3];

  //Allocations:
  int  node_pool_id  =  0 ;
  TONode  node_pool[]; //*node_pool;

  //Landscapes
  final byte  landscape_l[]  =  new byte[256];
  int  land_l_p  =  0;

  byte getNextLand () {
    ++land_l_p;
    if (256 == land_l_p)  land_l_p = 0;
    return landscape_l[land_l_p];
  }

  void  init()  {
    updatePathOutL  =  new TONode[3];
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
    baskets  =  new PTONode[l_capacity];
    node_pool  =  new TONode[l_capacity];
    for (int  i = l_capacity - 1;  i >= 0;  --i) {
      baskets[i]  =  new PTONode();
      node_pool[i]  =  new TONode();
      node_pool[i].clear();
    }
  } //init


  void  allocNode(long  hash,   K  key, V  data,  int  basketID,  int  cmp)  {
    TONode  re  =  null;
    do {
      ++node_pool_id;
      if  (node_pool_id >= l_capacity )  {
        node_pool_id  =  0;
      }
      re  =  node_pool[node_pool_id];
      if  (re.used  >=  l_uselessness)  {
        re.used -= l_uselessness;
        continue;
      }
    }  while (null  ==  re);

    if (null  !=  re.key)  {
//      if (re->key->keyArray[0]==30810) {
//          std::cout<<"\n";
//      }
      if (hash  ==  re.hash)  {
        // Same  hash case
        if (re  ==  updatePathOutH.ptr)  {
          //re is head of hash queue
          if (3 == cmp  ||  updatePathOutL[0]  ==  re)  {
            //easy: key must be new head, just replace
            //cmp  =  777;
            re.key  =  key;
            re.data  =  data;
            return;
          }  else  {
            // swap re, re->fwdPtrsL[0]:
            TONode  cur  =  re.fwdPtrsL[0];
            re.key  =  cur.key;
            re.data  =  cur.data;
            for (int  h  =  0;  h  <=  2;  ++h)  {
              if (h  <=  cur.curHeight) {
                re.fwdPtrsL[h]  =  cur.fwdPtrsL[h];
                cur.fwdPtrsL[h]  =  null;
              }
              if (updatePathOutL[h]  ==  cur) {
                updatePathOutL[h]  =  re;
              }
            }
            re  =  cur;
            re.used  =  l_uselessness; // because we are here broke age
            //next do regular alhorithm..
          }
        }  else  {
          // re not a head of the hash queue
          if (3  ==  cmp)  {
            delInSameHashCmp3(re, updatePathOutH.ptr);
          }  else  {
            // assert (4  !=  cmp);
            delInSameHash(re);
          }
          re.clear();
        }
      }  else  {
        // Other hash case
        int  re_basketID  =  (int)(re.hash % l_capacity);
        delWithOtherHash(re,
            (re.hash > hash  &&  re_basketID == basketID)  ?
                updatePathOutH  :  baskets[re_basketID]);
        re.clear();
      } // else

    }  //  if (!re)

    re.hash  =  hash;

    switch (cmp)  {
      case -1:
      case 1:
        //using update path
        // 1 == not found who bigger with the same hash, use updatePathOut
        // -1 == found who bigger at the queue end, use updatePathOut
        re.key  =  key;
        re.data  =  data;
        re.curHeight  =  getNextLand();
        for (int  i  =  0; i  <=  re.curHeight;  ++i)  {
          re.fwdPtrsL[i]  =  updatePathOutL[i].fwdPtrsL[i];
          updatePathOutL[i].fwdPtrsL[i]  =  re;
        }
        break;
      case 3:
        //3 == replace head of hash queue, use updatePathOutH
      {
        TONode  prevHead  =  updatePathOutH.ptr;
        updatePathOutH.ptr  =  re;
        re.fwdPtrH.ptr  =  prevHead.fwdPtrH.ptr;
        prevHead.fwdPtrH.ptr  =  null;
        re.key  =  key;
        re.data  =  data;
        re.curHeight  =  2;
        prevHead.curHeight  =  getNextLand();//landscape_l[land_l_p++];
        for (int  i  =  0; i  <=  2;  ++i)  {
          if (prevHead.curHeight  >=  i)  {
            re.fwdPtrsL[i]  =  prevHead;
          }  else  {
            re.fwdPtrsL[i]  =  prevHead.fwdPtrsL[i];
            prevHead.fwdPtrsL[i]  =  null;
          }
        }
      }
      break;
      case 4:
        //4 == here no a same hash, create new hash queue, use updatePathOutH
        re.key  =  key;
        re.data  =  data;
        re.curHeight  =  2;
//        if (null == updatePathOutH) {
//          updatePathOutH = null;
//        }
        re.fwdPtrH.ptr  =  updatePathOutH.ptr ;//*updatePathOutH;
        updatePathOutH.ptr  =  re;
        break;
      case 5:
        //New head of empty basket
        re.key  =  key;
        re.data  =  data;
        re.curHeight  =  2;
        baskets[basketID].ptr  =  re;
        break;
      default:
        assert(false);
    }
    return;
  }  //  allocNode

  /*
   * return:
   * 0 == node with equal key found, use only updatePathOut[0]
   * 3 == replace head of hash queue, use updatePathOutH
   * 4 == here no a same hash, create new hash queue, use updatePathOutH
   * 1 == not found who bigger with the same hash, use updatePathOut
   * -1 == found who bigger at the queue end, use updatePathOut
   */
  int  setll(long  hash,  K key)  {
    int  cmp  =  0;
    //TONode  *cur  =  *updatePathOutH;
    PTONode  pcur  =  updatePathOutH;
    if (pcur.ptr.hash  ==  hash)  {
      //cmp = compare(key,  cur->key) ;
      cmp = key.cmp(pcur.ptr.key);
      if (0  ==  cmp)  {
        updatePathOutL[0] = pcur.ptr;
        return  0;
      }
      if (cmp  <  0) {  return  3;  }
    }  else  {
//      while (*updatePathOutH   &&  hash > (*updatePathOutH)->hash) {
//        updatePathOutH = &((*updatePathOutH)->fwdPtrH);
//      }
      while (null != updatePathOutH.ptr
          && hash > updatePathOutH.ptr.hash) {
        updatePathOutH  =  updatePathOutH.ptr.fwdPtrH;
      }
      //if (*updatePathOutH  &&  (*updatePathOutH)->hash  ==  hash) {
      if (null != updatePathOutH.ptr  &&
          updatePathOutH.ptr.hash == hash)  {
        //cmp  =  compare(key,  (*updatePathOutH)->key) ;
        cmp  =  key.cmp(updatePathOutH.ptr.key) ;
        if (cmp  <  0)  {
          return 3; //must replace hash head in place
        }
        if (0  ==  cmp) {
          updatePathOutL[0] = updatePathOutH.ptr;
          return  0;
        }
        //cur  =  *updatePathOutH;  //step on it
        pcur  =  updatePathOutH;  //step on it
      }  else  {
        pcur  =  null;
      }
    }

    if (null  !=  pcur  &&  null  !=  pcur.ptr)  {
      // same key jumps
      int  h  =  2;
      TONode  cur  = pcur.ptr;
      while (h  >=  0)  {
        updatePathOutL[h]  =  cur;
        while (null  !=  cur.fwdPtrsL[h])  {
          //cmp  =  compare(key,  cur->fwdPtrsL[h]->key) ; //key->cmp(cur->fwdPtrsL[h]->key);
          cmp  =  key.cmp(cur.fwdPtrsL[h].key) ; //key->cmp(cur->fwdPtrsL[h]->key);
          if (cmp  <  0)  {
            //found who bigger
            break;
          }  else  if (0  ==  cmp)  {
            //updatePathOutH  =  &(cur->fwdPtrsL[h]);
            updatePathOutL[0] = cur.fwdPtrsL[h];
            return 0;  // must replace at place
          }
          cur  =  cur.fwdPtrsL[h];  //step on it
          updatePathOutL[h]  =  cur;
        }
        --h;
      }
      return (cmp < 0) ? -1 : 1;
    }  else  {
      // here no a same hash
      return 4;
    }
    //return 8;
  }  //  setll

  // for setll cmp==3 or 4, no need  updatePathOutL
  void  delInSameHashCmp3(TONode  nodeToDel, TONode  hashHead)  {
    int  h  =  2;
    TONode  updatePath[]  =  new TONode[3];
    TONode  cur  =  hashHead;  //updatePathOutH->fwdPtrH;
    while (h  >=  0)  {
      updatePath[h]  =  cur;
      while (nodeToDel != cur.fwdPtrsL[h]  &&  null != cur.fwdPtrsL[h])  {
        //if (compare(nodeToDel->key,  cur->fwdPtrsL[h]->key)  <=  0)  {
        if (nodeToDel.key.cmp(cur.fwdPtrsL[h].key)  <=  0)  {
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

  // need to check if UpdatePath affected
  void  delInSameHash(TONode  nodeToDel)  {
    for (int  h  =  2;  h  >=  0;  --h)  {
      if (nodeToDel  ==  updatePathOutL[h])  {
        //worst case - must do repath
        delInSameHashPathOut(nodeToDel, h);
        return;
      }
      if (nodeToDel  ==  updatePathOutL[h].fwdPtrsL[h])  {
        //best case - know where and how to update
        //just check if not on Path:
        int h2  =  h  -  1;
        while (h2  >=  0)  {
          if (nodeToDel  ==  updatePathOutL[h2])  {
            //worst case - must do repath
            delInSameHashPathOut(nodeToDel, h2);
            return;
          }
          --h2;
        }
        delInSameHashSuperFast(nodeToDel, h);
        return;
      }
    }
    //not on path == path not affected
    delInSameHashCmp3(nodeToDel,  updatePathOutH.ptr) ;
  } // delInSameHash


  void delWithOtherHash(TONode  nodeToDel,  PTONode  startSearch)  {
    //1. must find same hash:
     long  hash  =  nodeToDel.hash;

//    while (*startSearch  &&  (*startSearch)->hash != hash) {
//      startSearch = &((*startSearch)->fwdPtrH);
//    }
    while (null != startSearch.ptr  &&
        hash != startSearch.ptr.hash) {
      startSearch  =  startSearch.ptr.fwdPtrH;
    }
    //2. if nodeToDel is head of hash queue:
    if  (nodeToDel  ==  startSearch.ptr)  {
      //This is the head of hash queue:
       // *startSearch = nodeToDel->fwdPtrsL[0];
      startSearch.ptr  =  nodeToDel.fwdPtrsL[0];
      //if (*startSearch)  {
      if (null  !=  startSearch.ptr)  {
        TONode  cur  =  startSearch.ptr;  // aka tmp for work
        //  passing pointers to a new head:
        cur.fwdPtrH.ptr  =  nodeToDel.fwdPtrH.ptr;
        for (int  h  =  2;  h  >  cur.curHeight;  --h)  {
          cur.fwdPtrsL[h]  =  nodeToDel.fwdPtrsL[h];  // new head see what nodeToDel see now
        }
        cur.curHeight  =  2;
      }  else  {
        //nodeToDel was last with same hash
//          *startSearch  =  nodeToDel->fwdPtrH;
//        if (&(nodeToDel->fwdPtrH)  ==  updatePathOutH)  {
//          updatePathOutH  =  startSearch;
//        }
        startSearch.ptr  =  nodeToDel.fwdPtrH.ptr;
        if (nodeToDel.fwdPtrH  ==  updatePathOutH)  {
          updatePathOutH  =  startSearch;
        }

      }
    }  else  {
      //3. regular del:
      delInSameHashCmp3(nodeToDel,  startSearch.ptr) ;
    }
    return;
  }  //  delWithOtherHash


  void delInSameHashPathOut(TONode  nodeToDel,  int  top_h)  {
    //Node to del in updatePathOut[h], need path to it for update
    //new path may be not the same
    TONode  updatePath[]  =  new  TONode[3];
    int  h  =  2;
    //TONode  *cur  =  *updatePathOutH;
    TONode  cur  =  updatePathOutH.ptr;
    if  (nodeToDel.curHeight  >  top_h)  {
      top_h  =  nodeToDel.curHeight;
    }

    while (h  >  top_h)  {
      cur  =  updatePath[h]  =  updatePathOutL[h];
      --h;
    }

    while (h  >=  0)  {
      updatePath[h]  =  cur;
      while (nodeToDel != cur.fwdPtrsL[h]  &&  null != cur.fwdPtrsL[h])  {
        //if (compare(nodeToDel->key,  cur->fwdPtrsL[h]->key) <=  0)  {
        if (nodeToDel.key.cmp(cur.fwdPtrsL[h].key) <=  0)  {
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

    for (h  =  top_h;  h  >=  0;  --h)  {
      updatePath[h].fwdPtrsL[h]  =  nodeToDel.fwdPtrsL[h];
      //replace deleted node :
      if (nodeToDel  ==  updatePathOutL[h])  {
        updatePathOutL[h]  =  updatePath[h];
      }
    }
    return;
  }  //  delInSameBasketPathOut


  void delInSameHashSuperFast(TONode  nodeToDel,  int  top_h)  {
    //need path before to update pointers
    TONode  updatePath[]  =  new  TONode[3];
    if  (nodeToDel.curHeight  >  top_h)  {
      top_h  =  nodeToDel.curHeight;
    }
    updatePath[top_h]  =  updatePathOutL[top_h];
    TONode  cur  =  updatePathOutL[top_h];
    int  h  =  top_h;
    while (h  >=  0)  {
      updatePath[h]  =  cur;
      while (nodeToDel != cur.fwdPtrsL[h]  &&  null != cur.fwdPtrsL[h])  {
        //if (compare(nodeToDel->key,  cur->fwdPtrsL[h]->key)  <=  0)  {
        if (nodeToDel.key.cmp(cur.fwdPtrsL[h].key)  <=  0)  {
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

    for (h  =  top_h;  h  >=  0;  --h)  {
      updatePath[h].fwdPtrsL[h]  =  nodeToDel.fwdPtrsL[h];
    }
    return;
  }  //  delInSameBasketSuperFast


  class  TONode<K extends IKey, V>  {
    int  used  =  0;  // is it useful? need it in cache?
    PTONode  fwdPtrH = new PTONode();  // hash jumps
    TONode  fwdPtrsL[] =  new TONode[3]; // cmp jumps

    K  key;
    V  data;

    long  hash;
    byte  curHeight;  // ==SKIPHEIGHT-1 to CPU economy

    void  clear() {
      key  =  null;
      fwdPtrH.ptr  =  null;
      fwdPtrsL[0]  =  null;
      fwdPtrsL[1]  =  null;
      fwdPtrsL[2]  =  null;
      used  =  0;
    }
  };  //  TONode

  class  PTONode<K extends IKey, V>  {
    TONode  ptr = null;
  }

}
