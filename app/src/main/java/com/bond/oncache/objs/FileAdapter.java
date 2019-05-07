package com.bond.oncache.objs;

/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */

import android.content.Context;
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
