package com.bond.oncache.cases;
/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */

import android.util.Log;

import com.bond.oncache.TestPresenter;
import com.bond.oncache.gui.SpecTheme;
import com.bond.oncache.i.IKeyInt3;
import com.bond.oncache.i.ITestCase;
import com.bond.oncache.i.ITester;
import com.bond.oncache.i.ITesterThread;
import com.bond.oncache.objs.FileAdapter;
import com.bond.oncache.objs.StaticConsts;
import com.bond.oncache.objs.TJsonToCfg;
import com.bond.oncache.objs.TestParam;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CyclicBarrier;

public class CaseKeyInt3  implements ITestCase {
  // Public Java Interface :
  /////////////////////////////////////////////////////////////////
  @Override
  public String get_case_name() {
    return "Case Key = Int[3]";
  }

  @Override
  public int get_key_type() {
    return 0;
  }

  @Override
  public TestParam[] get_required_params() {
    return new TestParam[] {
       new TestParam( TestParam.TYPE_BOOL,
           StaticConsts.PARM_repeat_previous, "0"),
        new TestParam( TestParam.TYPE_NUM,
            StaticConsts.PARM_insert_threads, "1", 0, 100),
        new TestParam( TestParam.TYPE_NUM,
            StaticConsts.PARM_search_threads, "0", 0, 100),
        new TestParam( TestParam.TYPE_ITEMS,
            StaticConsts.PARM_max_items, "100000",
            1000,  1000000),
        new TestParam( TestParam.TYPE_NUM,
            StaticConsts.PARM_capacity_percent, "10",
            1, 100),
    };
  }

  @Override
  public boolean startTestCase(TJsonToCfg cfg) {
    boolean  re  =  false;
    try {
      //faux loop
      stop();
      do  {
        boolean repeat_previous = false;
        String str  =  cfg.getParam(StaticConsts.PARM_repeat_previous);
        if (null == str)  {
          repeat_previous = false;
        }  else  {
          repeat_previous  =  1 == Integer.parseInt(str);
        }
        if (repeat_previous  &&
            !FileAdapter.existsFile(TestCaseFile, SpecTheme.context)) {
          repeat_previous  =  false;
        }
        str  =  cfg.getParam(StaticConsts.PARM_insert_threads);
        if (null == str)  {  break; }
        int  insert_threads  =  Integer.parseInt(str);
        str  =  cfg.getParam(StaticConsts.PARM_search_threads);
        if (null == str)  {  break; }
        int  search_threads  =  Integer.parseInt(str);
        str  =  cfg.getParam(StaticConsts.PARM_max_items);
        if (null == str)  {  break; }
        int max_items  =  Integer.parseInt(str);
        str  =  cfg.getParam(StaticConsts.PARM_capacity_percent);
        if (null == str)  {  break; }
        int  capacity_percent  =  Integer.parseInt(str);
        synchronized (TAG) {
          caseThread  =  new CaseThread(id_case_start,  repeat_previous,
              max_items, insert_threads, search_threads, capacity_percent, cfg);
        }
        caseThread.start();
        re = true;
      }  while(false);
    } catch (Exception e) {
      Log.e(TAG, "startTestCase  error:", e);
    }
    return re;
  }


  @Override
  public void stop() {
    synchronized (TAG) {
      ++id_case_start;
      if (id_case_start > 1000000) id_case_start  =  1;
      if (null  !=  caseThread) {
        caseThread.stop();
        caseThread  =  null;
      }
    }
  }

  @Override
  public String get_settings_for_JSON() {
    return caseThread.get_settings_for_JSON();
  }

  //  Private Incapsulation :
  /////////////////////////////////////////////////////////////////
  static final String TAG = "CaseKeyInt3";
  static final String TestCaseFile = "testCase.txt";

  volatile int  id_case_start  =  0;
  CaseThread  caseThread  =  null;


  private class CaseThread implements Runnable  {
    // Public Java Interface :
    /////////////////////////////////////////////////////////////////
    CaseThread(int  start_id,  boolean repeat,  int  max_items_,
               int  insert_threads_,  int  search_threads_,
               int  capacity_percent_, TJsonToCfg cfg_)  {
      i_work_for_id  =  start_id;
      repeat_previous  =  repeat;
      max_items  =  max_items_;
      insert_threads = insert_threads_;
      search_threads = search_threads_;
      capacity_percent = capacity_percent_;
      raw_data  =  new int[max_items];
      keys  =  new IKeyInt3[max_items];
      cfg  =  cfg_;
      max_threads  =  insert_threads  +  search_threads;
      int jumps  =  0;
      int items  =  StaticConsts.START_ITEMS;
      while (items <= max_items) {
        ++jumps;
        items *= 10;
      }
      int progress_step_ = 900000 / (jumps * cfg.getTesters_count() * max_threads);
      if (0 == progress_step_) progress_step_  =  1;
      progress_step = progress_step_;
    }

    public void start() {
      local_thread = new Thread(this);
      local_thread.start();
    }

    public void stop() {
      keep_run  =  false;
      local_thread  =  null;
      for (ITesterThread test_thread : testerThreads) {
        test_thread.stop();
      }
    }

    public synchronized  int[] get_raw_data() {
      return  raw_data;
    }

    public synchronized  IKeyInt3[] get_keys() {
      return  keys;
    }

    //  Private Incapsulation :
    /////////////////////////////////////////////////////////////////
    volatile boolean keep_run  =  true;

    // Protected by the sequence of the algorithm: read only after all writes completed
    final int  raw_data[];
    final IKeyInt3  keys[];
    final TJsonToCfg cfg;
    final int  insert_threads;
    final int  search_threads;
    final int  max_items;
    final int  capacity_percent;
    final int  max_threads;
    final int  progress_step;

    volatile int  cur_progress  =  0;  // 0  to 1000000  (/10000 for %)

    Thread local_thread  =  null;

    // Local usage only:
    final int i_work_for_id;
    final boolean repeat_previous;
    static final String HTAG = "CaseThread";
    final CopyOnWriteArraySet<ITesterThread> testerThreads =  new CopyOnWriteArraySet<>();

    String get_settings_for_JSON() {
      StringBuilder sb = new StringBuilder(1048);
          sb.append(",\"insert threads\":\"")
              .append(insert_threads).append("\"")
          .append(",\"search threads\":\"")
              .append(search_threads).append("\"")
          .append(",\"repeat previous\":\"")
              .append(repeat_previous? '1' : '0').append("\"")
          .append(",\"max items\":\"")
              .append(max_items).append("\"")
          .append(",\"capacity percent\":\"")
              .append(capacity_percent).append("\"");
      return sb.toString();
    }

    @Override
    public void run() {
      try {
        loadData();
        provideTests();
      } catch (Exception e) {
        Log.e(HTAG,"Exception:", e);
      }
    }

    private void provideTests() {
      Map<ITester, ArrayList<Entry>>  results = new HashMap<>();
      ITester cur_tester;
      cfg.setllTesters();
      while (keep_run  && null  != (cur_tester = cfg.getNextTester())) {
         int  cur_items_mult  =  1;  // cur_items = 100 * cur_items_mult;
         ArrayList<Entry>  cur_times = new ArrayList<>();
         int  cur_max_items = StaticConsts.START_ITEMS * cur_items_mult;
         int  index  =  0;
         do  {
           int  capacity = cur_max_items * capacity_percent / 100;
           cur_times.add(new Entry(index,
               provide1Test(cur_tester, cur_max_items, capacity) / cur_items_mult));
           cur_items_mult *= 10;
           cur_max_items = StaticConsts.START_ITEMS * cur_items_mult;
           ++index;
           if (keep_run  &&  i_work_for_id  ==  id_case_start) {
             cur_progress += progress_step;
             int progress = cur_progress / 10000;
             if (progress > 99) progress = 99;
             TestPresenter.setProgress(progress);
           }
         } while (keep_run  &&  cur_max_items <= max_items);
        results.put(cur_tester, cur_times);
      } // tester
      if (keep_run  &&  i_work_for_id  ==  id_case_start) {
        cfg.putResults(results);
        TestPresenter.saveResultsToHistory(cfg);
        TestPresenter.setProgress(100);
      }
    }

    long  provide1Test(ITester cur_tester, int  cur_max_items, int capacity )  {
      testerThreads.clear();
      if  (cur_tester.amJavaTester()) {
        return provide1JavaTest(cur_tester,  cur_max_items, capacity );
      }  else {
        return provide1CppTest(cur_tester,  cur_max_items, capacity );
      }
    }

    long  provide1JavaTest(ITester cur_tester, int  cur_max_items, int capacity )  {
      cur_tester.onStart(capacity,  cfg);
      //Warm up:
      int  len  =  keys.length;
      int  i = 0;
      while (keep_run  &&  len > 0  && i < capacity) {
        --len;
        ++i;
        cur_tester.insert(keys[len]);
      }  //  Warm up

      //Go testing:
      CyclicBarrier barrier = new CyclicBarrier(insert_threads + search_threads);
      for ( i  = 0; keep_run  &&  i < insert_threads; ++i) {
        testerThreads.add(
            new InsertThread(keys, cur_max_items, barrier, cur_tester));
      }

      for ( i  = 0; keep_run  &&  i < search_threads; ++i) {
        testerThreads.add(
            new SearchThread(keys, cur_max_items, barrier, cur_tester));
      }

      long start_time = System.nanoTime();
      for (ITesterThread test_thread : testerThreads) {
        test_thread.start();
      }
      for (ITesterThread test_thread : testerThreads) {
        test_thread.join();
      }
      long stop_time =  System.nanoTime();
      cur_tester.onStop();
      return ((stop_time - start_time) / 1000);
    }

    long  provide1CppTest(ITester cur_tester, int  cur_max_items, int capacity )  {
      //Warm up:
      cur_tester.onStart(capacity,  cfg);
      testerThreads.add(
          new  CppThread(insert_threads,  search_threads, cur_max_items));
      int  len  =  keys.length;
      int  i = 0;
      while (keep_run  &&  len > 0  && i < cur_max_items) {
        --len;
        ++i;
        cur_tester.insert(keys[len]);
      }  //  Warm up

      //CppThread
      //Go testing:
      CyclicBarrier barrier = new CyclicBarrier(insert_threads + search_threads);
      for ( i  = 0; keep_run  &&  i < insert_threads; ++i) {
        testerThreads.add(
            new InsertThread(keys, cur_max_items, barrier, cur_tester));
      }

      for ( i  = 0; keep_run  &&  i < search_threads; ++i) {
        testerThreads.add(
            new SearchThread(keys, cur_max_items, barrier, cur_tester));
      }

      long start_time = System.nanoTime();
      for (ITesterThread test_thread : testerThreads) {
        test_thread.start();
      }
      for (ITesterThread test_thread : testerThreads) {
        test_thread.join();
      }
      long stop_time =  System.nanoTime();
      cur_tester.onStop();
      return ((stop_time - start_time) / 1000);
    }

    private void loadData() {
      int  i = 0;
      int  len  =  keys.length;
      if (repeat_previous) {
        String  str  =  FileAdapter.readFile(TestCaseFile,  SpecTheme.context);
        int str_len = str.length();
        int j  =  0;
        while (keep_run  &&  j  < str_len  &&  i < len)  {
          long  num  =  0L;
          do {
            char ch = str.charAt(j);
            if (','  ==  ch) {
              ++j;
              break;
            }  else  {
              num  =  10 * num  +  ch  -  '0';
            }
            ++j;
          } while (j  < str_len  &&  num < StaticConsts.MAX_INT);
          raw_data[i]  =  (int)num;
          keys[i]  =  new IKeyInt3(raw_data[i], raw_data[i], raw_data[i]);
          ++i;
        } //while
      }
      Random r = new Random();
      while (keep_run  &&  i < len) {
        raw_data[i]  =  r.nextInt(StaticConsts.MAX_INT);
        keys[i]  =  new IKeyInt3(raw_data[i], raw_data[i], raw_data[i]);
        ++i;
      } //while

      if (keep_run  && !repeat_previous) {
        save_raw_data();
      }

      if (keep_run  &&  i_work_for_id  ==  id_case_start) {
        cur_progress = 50000;
        TestPresenter.setProgress(5);
        // prepare NDK
        TestPresenter.setNDKtestCaseInt3(raw_data, raw_data.length);
      }

      if (keep_run  &&  i_work_for_id  ==  id_case_start) {
        cur_progress = 100000;
        TestPresenter.setProgress(10);
      }

    }  // loadData

    void save_raw_data() {
      StringBuilder sb = new StringBuilder(keys.length * 11);
      for (IKeyInt3 key : keys) {
        sb.append(key.k1).append(',');
      }
      if (keep_run) {
        FileAdapter.saveFile(TestCaseFile, sb.toString(), SpecTheme.context);
      }

    }
  } // CaseThread


  private class InsertThread implements Runnable, ITesterThread   {
    // Public Java Interface :
    /////////////////////////////////////////////////////////////////
    InsertThread(IKeyInt3  keys_[],  int  max_items_,
                 CyclicBarrier barrier_,  ITester tester_)  {
      max_items  =  max_items_;
      keys  =  keys_;
      barrier  =  barrier_;
      tester = tester_;
    }

    public void start() {
      local_thread = new Thread(this);
      local_thread.start();
    }

    @Override
    public void stop() {
      keep_run  =  false;
      local_thread  =  null;
    }

    @Override
    public void join() {
      try {
        local_thread.join();
      } catch (Exception e) {}
      local_thread  =  null;
    }


    //  Private Incapsulation :
    /////////////////////////////////////////////////////////////////
    volatile boolean keep_run  =  true;

    // Protected by the sequence of the algorithm: read only after all writes completed
    final int  max_items;
    final IKeyInt3  keys[];
    final CyclicBarrier  barrier;
    final ITester tester;

    Thread local_thread  =  null;

    static final String HTAG = "InsertThread";

    @Override
    public void run() {
      try {
        barrier.await();
        int  i  =  0;
        for (IKeyInt3  key  :  keys)  {
          tester.insert(key);
          ++i;
          if  (!keep_run  ||  i >= max_items)  {  break;  }
        }  //  for
      } catch (Exception e) {
        Log.e(HTAG,"Exception:", e);
      }
    }
  } // InsertThread

  private class SearchThread implements Runnable, ITesterThread  {
    // Public Java Interface :
    /////////////////////////////////////////////////////////////////
    SearchThread(IKeyInt3  keys_[],  int  max_items_,
                 CyclicBarrier barrier_,  ITester tester_)  {
      max_items  =  max_items_;
      keys  =  keys_;
      barrier  =  barrier_;
      tester = tester_;
    }

    public void start() {
      local_thread = new Thread(this);
      local_thread.start();
    }

    @Override
    public void stop() {
        keep_run  =  false;
        local_thread  =  null;
    }

    @Override
    public void join() {
      try {
        local_thread.join();
      } catch (Exception e) {}
      local_thread  =  null;
    }

    int get_found () {
      return found;
    }

    int get_not_found () {
      return not_found;
    }

    //  Private Incapsulation :
    /////////////////////////////////////////////////////////////////
    volatile boolean keep_run  =  true;
    volatile int  found = 0;
    volatile int  not_found = 0;
    // Protected by the sequence of the algorithm: read only after all writes completed
    final int  max_items;
    final IKeyInt3  keys[];
    final CyclicBarrier  barrier;
    final ITester tester;

    Thread local_thread  =  null;

    static final String HTAG = "SearchThread";

    @Override
    public void run() {
      try {
        barrier.await();
        int  i  =  0;
        int  found_ = 0;
        int  not_found_ = 0;
        for (IKeyInt3  key  :  keys)  {
          if  (tester.exist(key)) {
            ++found_;
          }  else  {
            ++not_found_;
          }
          ++i;
          if  (!keep_run || i >= max_items)  {  break;  }
        }  //  for
        found  =  found_;
        not_found  =  not_found_;
      } catch (Exception e) {
        Log.e(HTAG,"Exception:", e);
      }
    }
  } // SearchThread

  private class CppThread implements Runnable, ITesterThread  {
    // Public Java Interface :
    /////////////////////////////////////////////////////////////////
    CppThread(int  insert_threads_, int  search_threads_,
              int  max_items_)  {
      insert_threads  =  insert_threads_;
      search_threads  =  search_threads_;
      max_items  =  max_items_;
    }

    public void start() {
      local_thread = new Thread(this);
      local_thread.start();
    }

    @Override
    public void stop() {
      TestPresenter.stopCppTest();
      local_thread  =  null;
    }

    @Override
    public void join() {
      try {
        local_thread.join();
      } catch (Exception e) {}
      local_thread  =  null;
    }

    //  Private Incapsulation :
    /////////////////////////////////////////////////////////////////
    // Protected by the sequence of the algorithm: read only after all writes completed
    final int  insert_threads;
    final int  search_threads;
    final int  max_items;

    Thread local_thread  =  null;

    static final String HTAG = "CppThread";

    @Override
    public void run() {
      try {
        TestPresenter.runCppTest(insert_threads,  search_threads,  max_items);
      } catch (Exception e) {
        Log.e(HTAG,"Exception:", e);
      }
    }
  } // CppThread


}
