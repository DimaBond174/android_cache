package com.bond.oncache;

import android.content.Context;
import android.content.Intent;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.bond.oncache.gui.FragmentKey;
import com.bond.oncache.gui.MainWindow;
import com.bond.oncache.gui.SpecTheme;
import com.bond.oncache.gui.UiFragment;
import com.bond.oncache.gui.UiMainFrag;
import com.bond.oncache.i.IView;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements IView {
  static final String TAG = "MainActivity";
  final GuiHandler guiHandler = new GuiHandler(Looper.getMainLooper());
  Toolbar toolbar  =  null;
  FloatingActionButton fab  =  null;
  static final String FirstFragTAG = "UiMainFrag";
  static final FragmentKey FirstFragKey = new FragmentKey(FirstFragTAG);
  final Map<FragmentKey,  UiFragment>  uiFrags  =  new HashMap<FragmentKey,UiFragment>();
  final Deque<FragmentKey>  uiFragsControl  =  new ArrayDeque<FragmentKey>();
  UiFragment curActiveFrag  =  null;
  MainWindow mainWindow  =  null;
  boolean guiNotStarted  =  true;
  Drawable play_icon;
  Drawable stop_icon;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mainWindow = (MainWindow) findViewById(R.id.mainWindow);
    toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        onFABclick(view);
      }
    });

    // Example of a call to a native method
//    TextView tv = (TextView) findViewById(R.id.sample_text);
//    tv.setText(TestPresenter.stringFromJNI());
    restoreState(savedInstanceState);
//    onNewIntent(getIntent());
    play_icon = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_play_circle_outline_black_24dp);
    play_icon.setColorFilter(new LightingColorFilter( 0, 0xffffffff));
    stop_icon = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_stop_black_24dp);
    stop_icon.setColorFilter(new LightingColorFilter( 0, 0xffffffff));

    setFABicon();
  }

  void setFABicon() {
    int prog = TestPresenter.getProgress();
    if  (0 == prog || 100 == prog)  {
      fab.setImageDrawable(play_icon);
    }  else {
      fab.setImageDrawable(stop_icon);
    }
  }

  void onFABclick(View view) {
    //need for stop fab.hide();
    if  (0 == TestPresenter.getProgress())  {
      Snackbar.make(view, TestPresenter.getRstring(R.string.strTest_try_start), Snackbar.LENGTH_LONG)
          .setAction("Action", null).show();
      TestPresenter.startProgress();
    }  else {
      Snackbar.make(view, TestPresenter.getRstring(R.string.strTest_try_stop), Snackbar.LENGTH_LONG)
          .setAction("Action", null).show();
      TestPresenter.stopProgress();
    }

  }

  @Override
  public void onPresenterChange() {
    setFABicon();
    curActiveFrag.onPresenterChange();
  }

  @Override
  public void showMessage(String str) {
    Snackbar.make(fab, str, Snackbar.LENGTH_LONG)
        .setAction("Action", null).show();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }


  @Override
  public void onSaveInstanceState(Bundle outState) {
    if (null  !=  curActiveFrag) {
      outState.putString("curActiveFrag.fragTAG",
          curActiveFrag.getFragmentKey().fragTAG);
    }
    super.onSaveInstanceState(outState);
  }

  private void restoreState (Bundle savedInstanceState) {
    if  (null  ==  savedInstanceState) {  return ;  }
    String fragTAG = savedInstanceState.getString("curActiveFrag.fragTAG");
    if  (null  !=  fragTAG)  {
      setCurActiveFrag(new FragmentKey(fragTAG));
    }//if (null!=fragTAG)
  }

  private void setCurActiveFrag(FragmentKey key)  {
    if  (null  ==  key)  return;
    UiFragment frg  =  uiFrags.get(key);
    if  (null  ==  frg  ||  frg.isDestroyed)  {
      frg  =  createUiFragment(key);
    }
    if  (frg  !=  curActiveFrag)  {

      if (null  !=  curActiveFrag )  {
        mainWindow.checkDelCurFrag(curActiveFrag);
        uiFrags.remove(curActiveFrag.getFragmentKey());
        uiFragsControl.remove(curActiveFrag.getFragmentKey());
        curActiveFrag.onDestroyCommon();
      }
      curActiveFrag = frg;
      mainWindow.setCurActiveFrag(curActiveFrag);

      if (curActiveFrag.getTAG().contains(FirstFragTAG)) {
        clearUiFrags(FirstFragKey);
      }

      uiFrags.put(curActiveFrag.getFragmentKey(), curActiveFrag);
      uiFragsControl.add(curActiveFrag.getFragmentKey());
    }

    if (null  !=  curActiveFrag)  {
      curActiveFrag.onResume();
    }
  }

  UiFragment createUiFragment(FragmentKey key)  {
    Context  context  =  MainActivity.this;
    UiFragment frg  =  null;
    switch (key.fragTAG) {
//      case "UiSettingsFrag":
//        frg=new UiSettingsFrag(context, fragmentKey);
//        break;
      case "UiLentaFrag":
      default:
        frg=new UiMainFrag(context,  key);
    }
    return frg;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }


  @Override
  public void setToolbarTittle(String tittle) {
    getSupportActionBar().setTitle(tittle);
  }

  @Override
  public Handler getGuiHandler() {
    return  guiHandler;
  }

  @Override
  public Context getForDialogCtx() {
    return MainActivity.this;
  }

//  @Override
//  public void onNewIntent(Intent intent){
//
//    if (!UiRoot.onIntentReceived(intent, false)) {
//      super.onNewIntent(intent);
//    }
//  }//onNewIntent(Intent intent)

  @Override
  public void onPause() {
    if (null  !=  curActiveFrag) {
      curActiveFrag.onPause();
    }
    super.onPause();
  }


  private void onFirstStart() {
    //mainWindow.init(SpecNetMain.this, drawerLayout);
    //UiFragment frg=sr.getUiFragment();
    FragmentKey key=uiFragsControl.peekLast();
    if (null  ==   key) {
      setCurActiveFrag(FirstFragKey);
    } else {
      setCurActiveFrag(key);
    }

    guiNotStarted=false;

  }

  @Override
  public void onStart() {
    TestPresenter.setGUInterface(MainActivity.this);
    SpecTheme.applyMetrics(MainActivity.this);
    super.onStart();

    if (guiNotStarted) {
      onFirstStart();
    }
  }

  @Override
  public void onResume() {
    super.onResume();

    if (null  !=  curActiveFrag) {
      curActiveFrag.onResume();
    }

  }

  @Override
  public void onStop() {
    if  (null  !=  curActiveFrag)  {
      curActiveFrag.onStop();
    }
    guiNotStarted  =  true;
    super.onStop();
  }

  @Override
  protected void onDestroy() {
    exitSpecNetMain();
    super.onDestroy();
  }

  private void exitSpecNetMain()  {
    try {
      guiNotStarted = true;
      mainWindow.onDestroy();
      clearUiFrags();
      SpecTheme.onDestroy();
      finish();
      System.gc();
    } catch (Exception e) {}
    try {
      super.onBackPressed();
      System.gc();
    } catch (Exception e) {}
  }

  private void clearUiFrags(FragmentKey exeptFragKey)  {
    for (FragmentKey fragKey : uiFragsControl)  {
      if (fragKey.equals(exeptFragKey)) {
        continue;
      }
      UiFragment frag  =  uiFrags.get(fragKey);
      if (null  !=  frag)  {
        mainWindow.checkDelCurFrag(frag);
        frag.onStop();
        frag.onDestroyCommon();
      }
    }
    uiFrags.clear();
    uiFragsControl.clear();
  }

  private void clearUiFrags() {
    for (FragmentKey fragKey : uiFragsControl) {
      UiFragment frag=uiFrags.get(fragKey);
      if (null!=frag) {
        mainWindow.checkDelCurFrag(frag);
        frag.onStop();
        frag.onDestroyCommon();
      }
    }
    uiFragsControl.clear();
    uiFrags.clear();
    curActiveFrag = null;
  }

  class GuiHandler extends Handler {
    public GuiHandler(Looper looper) {
      super(looper);
    }

    @Override
    public void handleMessage(Message msg) {
      //Важно: сообщения приходят и когда UI на паузе: https://stackoverflow.com/questions/8040280/how-to-handle-handler-messages-when-activity-fragment-is-paused
      //в режиме разделённого экрана пауза - не остановка и не повод прекратить видео
      //Цикл анимации работает на GUI thread в виде циклирующего MsgTAGs.M_ANIMATE
      //и самое удобное место для его реанимации, на мой взгляд, тут:

//      if (!UiRoot.isStarted) {
//        return; //Если UIRoot остановлен, все сообщения исчезают == ОПРЕДЕЛЁННОСТЬ
//      }


      try {
        /* Если дольше секунды не было анимации, пора реанимировать */
//                if (MsgTAGs.M_ANIMATE!=msg.what) {
//                    if (System.currentTimeMillis() - sr.lastAnimationTime > 1000L) {
//                        sr.animate(curActiveFrag);
//                    }
//                }
//                        long curTime2 = System.currentTimeMillis();
//                 if (curTime2 - keepAliveThread.lastTimeAcive.get()
//                                > Constants.MSEC_ANDROID_ANR) {
//                   keepAliveThread.resume();
//                        }
        switch (msg.what) {
//                    case MsgTAGs.M_KEEP_ALIVE:
//                        keepAlive();
//                        break;
//          case MsgTAGs.M_ANIMATE:
//            UiRoot.animate(curActiveFrag);
//            break;
//          case MsgTAGs.M_GO_BACK:
//            onBackPressed();
//            break;
//          case MsgTAGs.M_GO_FRAG_UI:
//            /* В сообщении имя фрагмента куда перейти - внутренние переходы */
//            if (null != msg.obj) {
//              onM_GO_FRAG_UI((MsgTemplate) msg.obj);
//            }
//            //else Log.i(TAG, "handleMessage: receive null in Object obj");
//            break;
//          case MsgTAGs.M_GO_FRAG_UI_MSG:
//            /* Доставка сообщения во фрагмент (с пересозданием если его нет) */
//            if (null != msg.obj) goFragmentMsg((MsgTemplate) msg.obj);
//            //else Log.i(TAG, "handleMessage: receive null in Object obj");
//            break;
//          case MsgTAGs.M_SEND_TO_ACTIVE_FRAG:
//            /* Для работы активного фрагмента - операции с СУБД и др.
//             * Если фрагмент не активен, значит ему и не надо */
//            if (null != msg.obj) sendMsgIfActive((MsgTemplate) msg.obj);
//            break;
//          case MsgTAGs.M_SHOW_ERR:
//            if (null != msg.obj) {
//              showErrorDlg((MsgTemplate) msg.obj);
//            }
//            break;
//          case MsgTAGs.M_SHOW_WAIT:
//            setWaitBtn();
//            break;
////                    case MsgTAGs.M_ASK_PASSWORD:
////                        if (null != msg.obj) {
////                            showDlgEnterPassword((MsgTemplate) msg.obj);
////                        }
////                        break;
//          case MsgTAGs.M_NEW_MAIL:
//            if (null != msg.obj) {
//              onNewMail((MsgTemplate) msg.obj);
//            }
//            break;
//          case MsgTAGs.M_AVA_CHANGED:
//            onAvatarInfoChanged();
//            break;
////                    case MsgTAGs.M_CHANGE_TITLE:
////                        toolbar.setTitle(curActiveFrag.getTitle());
////                        break;
////                    case MsgTAGs.M_GO_FRAGMENT:
////                        goFragmentMsg(msg.obj);
////                        //else Log.i(TAG, "handleMessage: receive null in Object obj");
////                        break;
//          case MsgTAGs.M_HIDE_R_MENU:
//            hideRightMenu();
//            break;
//          case MsgTAGs.M_KEEP_ALIVE:
//            //lastKeepAlive.set(System.currentTimeMillis());
//            break;
          default:
            super.handleMessage(msg);
        }
      } catch (Exception e) {
        Log.e(TAG, "GuiHandler: error Message handling",e);
      }
    }
  }

}
