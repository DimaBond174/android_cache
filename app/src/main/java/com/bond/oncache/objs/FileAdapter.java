package com.bond.oncache.objs;

/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileAdapter {
  // Public Java Interface :
  /////////////////////////////////////////////////////////////////
  public static File[]  getFileList(String folder,  Context context)  {
    File[] re  =  null;
    StringBuilder sb  = new StringBuilder(256);
    sb.append(context.getFilesDir().getPath())
        .append(File.separator)
        .append(folder);
    try {
      File dir = new File(sb.toString());
      re = dir.listFiles();
    } catch (Exception e) {
      Log.e(TAG, "getFileList error:", e);
    }
    return  re;
  }

  public static boolean existsFile(String file,  Context context)  {
    boolean  re  = false;
    try {
      File f = new File(file);
      re  = f.exists();
      if (!re)  {
        StringBuilder sb  = new StringBuilder(256);
        sb.append(context.getFilesDir().getPath())
            .append("/specnet/").append(file);
        f = new File(sb.toString());
        re  = f.exists();
        if (!re)  {
          InputStream in = null;
          try {
            in = context.getAssets().open(file);
            re = true;
          } catch (Exception e) {}
          if (null  !=  in) {
            try {  in.close();  } catch (Exception e) {}
          }
        }
      }
    } catch (Exception e)  {
      Log.e(TAG, "existsFile() error: ", e);
    }
    return re;
  }

  public static String getFileFullPath(Uri uri,  Context context) {
    if (null  !=  uri) {
      String uriString = uri.toString();
      if (uriString.startsWith("file://")) {
        return (new File(uriString)).getAbsolutePath();
      }  else {
        //Oh Android
        Cursor cursor = null;
        try {
          String[] proj = { MediaStore.Images.Media.DATA };
          cursor = context.getContentResolver().query(uri, proj, null, null, null);
          int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
          cursor.moveToFirst();
          return cursor.getString(column_index);
        } finally {
          if (cursor != null) {
            cursor.close();
          }
        }
      }
    }
    return "";
  }

  public static String getFileName(Uri uri,  AppCompatActivity activity)  {
    String uriString  =  uri.toString();
    String displayName  =  "";
    if (uriString.startsWith("content://"))  {
      Cursor cursor = null;
      try {
        cursor = activity.getContentResolver().query(uri, null, null, null, null);
        if (cursor  !=  null) {
          if (cursor.moveToFirst()) {
            displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
          }
          cursor.close();
        }
      } catch (Exception e) {}
    } else if (uriString.startsWith("file://"))  {
      File myFile = new File(uriString);
      //String path = myFile.getAbsolutePath();
      displayName = myFile.getName();
    }
    return displayName;
  }

    public static boolean checkStoragePermissionGranted(Activity context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if  (PackageManager.PERMISSION_GRANTED
          != context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE))  {
        ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
            StaticConsts.RQS_GET_PERMITIONS);
        return false;
      }
    }
    return  true;
  }


  public static String getFileFromURI(AppCompatActivity activity, Uri mediaUri, String defFileName){
    String re = getFileFullPath(mediaUri, activity);
    try {
      String fileName = defFileName;
      if (null != re  &&  !re.isEmpty()) {
        fileName = FileAdapter.getFileName(mediaUri, activity);
        if (fileName.isEmpty()) {
          fileName = defFileName;
        }
      }
      InputStream inputStream = activity.getBaseContext().getContentResolver().openInputStream(mediaUri);
      StringBuilder sb  = new StringBuilder(256);
      sb.append(activity.getFilesDir().getPath()).append("/specnet");
      if (FileAdapter.saveFileI(sb.toString(), fileName, inputStream)) {
        sb.append(File.separatorChar).append(fileName);
        re = sb.toString();
      }
    } catch (Exception e) {
      Log.e(TAG, "getFileFromURI():", e);
    }
    return re;
  }

  public static boolean saveFileI(String dir, String file, InputStream inputStream) {
    boolean re  =  false;
    final int buffer_size = 4096;
    try {
      File fdir = getDir(dir);
      if (null  !=  fdir) {
        File f = new File(fdir,file);

        if (f.exists()) {
          f.delete();
        }
        f.createNewFile();
        FileOutputStream fOut = new FileOutputStream(f);
        byte[] bytes = new byte[buffer_size];
        for (int count=0;count!=-1;) {
          count = inputStream.read(bytes);
          if(count != -1) {
            fOut.write(bytes, 0, count);
          }
        }
        fOut.flush();
        inputStream.close();
        fOut.close();
        re=true;
      }
    } catch (Exception e) {
      Log.e(TAG,"saveFile err:"+dir+"/"+file,e);
    }
    return re;
  }

  public static String getFileName(String fullPath) {
    int index = fullPath.lastIndexOf('/');
    return fullPath.substring(index + 1);
  }

  public static File getDir(String path) {
    File re=null;
    try {
      File newDir = new File(path);
      if (newDir.exists()) {
        re=newDir;
      } else {
        if (newDir.mkdirs()) {
          re=newDir;
        }
      }
    } catch (Exception e) {
      Log.e(TAG,"Fail to create work dir:"+path);
    }
    return re;
  }

  public static String readFile(String file, Context context)  {
    String  re  =  readFile(file);
    if (re.isEmpty())  {
      StringBuilder sb  = new StringBuilder(256);
      sb.append(context.getFilesDir().getPath())
          .append("/specnet/").append(file);
      re  =  readFile(sb.toString());
      if (re.isEmpty())  {
        re  =  loadAssetString(context,  file);
      }
    }
    return re;
  }

  public static String readFile(File f)  {
    if (!f.exists()) {  return EMPTY_STR;  }
    long llen  =  f.length();
    int  ilen  =  (int) llen;
    if (llen  !=  ilen  ||  ilen  <  1) {  return EMPTY_STR;  }
    ByteArrayOutputStream fbuf  =  new ByteArrayOutputStream(ilen);
    try {
      InputStream in  =  new FileInputStream(f.getAbsolutePath());
      byte[] buf = new byte[2048];
      int len;
      while ((len = in.read(buf)) != -1) {
        fbuf.write(buf, 0,  len);
      }
      in.close();
    } catch (Exception e) {
      Log.e(TAG,"readFile err:",e);
    }
    return fbuf.toString();
  }  //   readFile

  public static boolean saveJsonHistory(String folder,
      String json, Context context)  {
    StringBuilder sb  = new StringBuilder(256);
    sb.append(context.getFilesDir().getPath()).append(File.separator)
        .append(folder);
    File dir = new File(sb.toString());
    dir.mkdirs();
    sb.append(File.separator).append(System.currentTimeMillis());
    File f = new File (sb.toString());
    if (f.exists()) {
      f.delete();
    }
    boolean re = false;
    try {
      f.createNewFile();
      if (f.getFreeSpace() > LoLevel) {
        FileOutputStream fOut = new FileOutputStream(f);
        fOut.write(json.getBytes());
        fOut.flush();
        fOut.close();
        re = true;
      }
    } catch (Exception e) {}
    return  re;
  }

  public static void saveFile(String file, String text, Context context)  {
    StringBuilder sb  = new StringBuilder(256);
    sb.append(context.getFilesDir().getPath())
        .append("/specnet/").append(file);
    saveFile(sb.toString(),  text);
  }

  public static void saveFile(String filePath, String text)  {
    try {
      if (enshurePath(filePath)) {
        File f = new File(filePath);
        if (f.exists()) {
          f.delete();
        }
        f.createNewFile();
        if (f.getFreeSpace() > LoLevel) {
          FileOutputStream fOut = new FileOutputStream(f);
          fOut.write(text.getBytes());
          fOut.flush();
          fOut.close();
        } else {
//          if (null!=callback){
//            callback.onDiskFull();
//          }
        }
      }
    } catch (Exception e) {
      Log.e(TAG,"saveFile err:"+filePath,e);
    }

    return;
  }

  public static String loadAssetString(Context context, String pathRelative)  {
    ByteArrayOutputStream fbuf = new ByteArrayOutputStream();
    try {
      //InputStream in = getAssets().open("sub/sample.txt");
      InputStream in = context.getAssets().open(pathRelative);
      byte[] buf = new byte[2048];
      int len;
      while ((len = in.read(buf)) != -1) {
        fbuf.write(buf, 0, len);
      }
      in.close();
    } catch (Exception e) {
      Log.e(TAG,"readFile err:",e);
    }
    return fbuf.toString();
  }

  //  Private Incapsulation :
  /////////////////////////////////////////////////////////////////
  private static final String TAG = "FileAdapter";
  private static final String EMPTY_STR = "";
  private static final long LoLevel = 32000000L;  // ~30Mb


  private static String readFile(String  file_path)  {
    File f = new File(file_path);
    if (!f.exists()) {  return EMPTY_STR;  }
    long llen  =  f.length();
    int  ilen  =  (int) llen;
    if (llen  !=  ilen  ||  ilen  <  1) {  return EMPTY_STR;  }
    ByteArrayOutputStream fbuf  =  new ByteArrayOutputStream(ilen);
    try {
      InputStream in  =  new FileInputStream(f.getAbsolutePath());
      byte[] buf = new byte[2048];
      int len;
      while ((len = in.read(buf)) != -1) {
        fbuf.write(buf, 0,  len);
      }
      in.close();
    } catch (Exception e) {
      Log.e(TAG,"readFile err:",e);
    }
    return fbuf.toString();
  }  //   readFile

  private static boolean enshurePath(String filePath) {
    boolean  re  = false;
    //try {
      int  len  =  filePath.length();
      do  {
        --len;
        if  (File.separatorChar  ==  filePath.charAt(len))  {
          String dir_path  =  filePath.substring(0,  len);
          File dir = new File(dir_path);
          if (dir.exists())  {
            re  =  true;
          }  else  {
            re  =  dir.mkdirs();
          }
          break;
        }
      } while (len > 0);

    //} catch (Exception e) {}
    return  re;
  }

}
