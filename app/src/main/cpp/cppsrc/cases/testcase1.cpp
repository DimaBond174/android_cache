/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */

#include "testcase1.h"


TestCase1::TestCase1 (jint  *cData, jint  rawDataLen)  {
  test_data.reserve(rawDataLen);
  for (int32_t  i = 0;  i <  rawDataLen; ++i) {
    test_data.emplace_back(new Elem(cData[i],  cData[i],  cData[i]));
  }
  return ;
}

TestCase1::~TestCase1() {
  int32_t len = test_data.size();
  for (int32_t  i = 0;  i <  len; ++i) {
    delete test_data[i];
  }
  test_data.clear();
  return;
}

void  TestCase1::stop()  {
  ++tester_run_id;
  keep_run.store(false, std::memory_order_release);
  return;
}

void  TestCase1::warmUP (int32_t capacity,
    std::shared_ptr<IAlgorithmTester>  tester)  {
  set_cur_tester(tester);
  if (capacity > 0  &&  tester)  {
    IAlgorithmTester  *p_tester =  tester.get();
    p_tester->onStart(capacity);
    int32_t  len  =  test_data.size() - 1;
    for (int32_t i  =  0;  keep_run.load(std::memory_order_acquire)
        &&  i < capacity  &&  len >= 0;  --len,  ++i)  {
      p_tester->insert(test_data[len]);
    }
  }
  return;
}

int  TestCase1::get_key_type()  {
  return  0;
}

void  TestCase1::set_cur_tester(std::shared_ptr<IAlgorithmTester>  _cur_tester) {
  std::unique_lock<std::shared_mutex> lk(cur_tester_mutex);
  cur_tester = _cur_tester;
  return;
}

std::shared_ptr<IAlgorithmTester>  TestCase1::get_cur_tester() {
    std::shared_lock<std::shared_mutex> lk(cur_tester_mutex);
  return cur_tester;
}

void  TestCase1::doTest(int32_t  insert_threads,
    int32_t  search_threads,  int32_t  max_items)  {
  std::shared_ptr<IAlgorithmTester>  cur_tester  =  get_cur_tester();
  if (cur_tester)  {
    std::deque<std::thread>  threads;
    std::shared_ptr<ParamsPack>  params
        =  std::make_shared<ParamsPack>(max_items,
            ++tester_run_id,  cur_tester);
    for (int32_t  i  = insert_threads;  i;  --i)  {
      threads.emplace_back(std::thread(&TestCase1::s_run_insert, this, params));
    }
    for (int32_t  i  = search_threads;  i;  --i)  {
      threads.emplace_back(std::thread(&TestCase1::s_run_search, this, params));
    }
    //BANZAI
    semaphore.signal(insert_threads + search_threads);
    std::for_each(threads.begin(), threads.end(), std::mem_fn(&std::thread::join));
  }
  return;
}

void  TestCase1::run_insert(std::shared_ptr<ParamsPack> params)  {
  const int32_t  my_run_id  =  params.get()->my_run_id;
  const int32_t  max_items  =  params.get()->max_items;
  IAlgorithmTester  *p_tester  =  params.get()->cur_tester.get();
  semaphore.wait();
  for (int32_t  i = 0;  my_run_id == tester_run_id.load(std::memory_order_acquire)
      &&  i < max_items;  ++i) {
    p_tester->insert(test_data[i]);
  }
  return;
}

void * TestCase1::s_run_insert(void  *ptr, std::shared_ptr<ParamsPack> params)  {
  TestCase1  *th  = static_cast<TestCase1  *>(ptr);
  th->run_insert(params);
  return 0;
}

void  TestCase1::run_search(std::shared_ptr<ParamsPack> params)  {
  const int32_t  my_run_id  =  params.get()->my_run_id;
  const int32_t  max_items  =  params.get()->max_items;
  IAlgorithmTester  *p_tester  =  params.get()->cur_tester.get();
  semaphore.wait();
  volatile int32_t  found  =  0;
  for (int32_t  i = 0;  my_run_id == tester_run_id.load(std::memory_order_acquire)
                        &&  i < max_items;  ++i) {
    if  (p_tester->exist(test_data[i]) )  {
      ++found;
    }
  }
  return;
}

void * TestCase1::s_run_search(void  *ptr, std::shared_ptr<ParamsPack> params)  {
  TestCase1  *th  = static_cast<TestCase1  *>(ptr);
  th->run_search(params);
  return 0;
}
