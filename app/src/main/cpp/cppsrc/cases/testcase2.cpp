/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */
#include "testcase2.h"


TestCase2::TestCase2(const char * strData,  int32_t  strLen,  int32_t  maxItems)  {
  const char *ch  =  strData;
  const char *end  =  ch  +  strLen;
  std::string  str;
  str.reserve(ElemNSizeKey::key_ElemNSizeKey_size);
  int32_t  curSize  =  0;
  while (ch  <  end  &&  curSize < maxItems) {
    if (0x0A  ==  *ch  ||  '.'  ==  *ch)  {
      if (str.length() > 0) {
        test_data.emplace_back(new ElemNSizeKey(str));
        str.clear();
        ++curSize;
      }
    }  else  {
      str.push_back(*ch);
    }
    ++ch;
  }
  return;
}

TestCase2::~TestCase2() {
  int32_t len = test_data.size();
  for (int32_t  i = 0;  i <  len; ++i) {
    delete test_data[i];
  }
  test_data.clear();
  return;
}

void  TestCase2::stop()  {
  ++tester_run_id;
  keep_run.store(false, std::memory_order_release);
  return;
}

void  TestCase2::warmUP (int32_t capacity,
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

int  TestCase2::get_key_type()  {
  return  1;
}

void  TestCase2::set_cur_tester(std::shared_ptr<IAlgorithmTester>  _cur_tester) {
  std::unique_lock<std::shared_mutex> lk(cur_tester_mutex);
  cur_tester = _cur_tester;
  return;
}

std::shared_ptr<IAlgorithmTester>  TestCase2::get_cur_tester() {
  std::shared_lock<std::shared_mutex> lk(cur_tester_mutex);
  return cur_tester;
}

void  TestCase2::doTest(int32_t  insert_threads,
                        int32_t  search_threads,  int32_t  max_items)  {
  std::shared_ptr<IAlgorithmTester>  cur_tester  =  get_cur_tester();
  if (cur_tester)  {
    std::deque<std::thread>  threads;
    std::shared_ptr<ParamsPack>  params
        =  std::make_shared<ParamsPack>(max_items,
                                        ++tester_run_id,  cur_tester);
    for (int32_t  i  = insert_threads;  i;  --i)  {
      threads.emplace_back(std::thread(&TestCase2::s_run_insert, this, params));
    }
    for (int32_t  i  = search_threads;  i;  --i)  {
      threads.emplace_back(std::thread(&TestCase2::s_run_search, this, params));
    }
    //BANZAI
    semaphore.signal(insert_threads + search_threads);
    std::for_each(threads.begin(), threads.end(), std::mem_fn(&std::thread::join));
  }
  return;
}


void  TestCase2::run_insert(std::shared_ptr<ParamsPack> params)  {
  const uint32_t  my_run_id  =  params.get()->my_run_id;
  const int32_t  max_items  =  params.get()->max_items;
  IAlgorithmTester  *p_tester  =  params.get()->cur_tester.get();
  semaphore.wait();
  for (int32_t  i = 0;  my_run_id == tester_run_id.load(std::memory_order_acquire)
                        &&  i < max_items;  ++i) {
    p_tester->insert(test_data[i]);
  }
  return;
}

void * TestCase2::s_run_insert(void  *ptr, std::shared_ptr<ParamsPack> params)  {
  TestCase2  *th  = static_cast<TestCase2  *>(ptr);
  th->run_insert(params);
  return 0;
}


void  TestCase2::run_search(std::shared_ptr<ParamsPack> params)  {
  const uint32_t  my_run_id  =  params.get()->my_run_id;
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

void * TestCase2::s_run_search(void  *ptr, std::shared_ptr<ParamsPack> params)  {
  TestCase2  *th  = static_cast<TestCase2  *>(ptr);
  th->run_search(params);
  return 0;
}

