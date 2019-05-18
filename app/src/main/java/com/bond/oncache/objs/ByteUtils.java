package com.bond.oncache.objs;
/*
 * This is the source code of SpecNet project
 * It is licensed under MIT License.
 *
 * Copyright (c) Dmitriy Bondarenko
 * feel free to contact me: specnet.messenger@gmail.com
 */

import java.nio.ByteBuffer;

public class ByteUtils  {
    public final int sizeof_long  =  Long.SIZE / Byte.SIZE;
    private ByteBuffer buffer = ByteBuffer.allocate(sizeof_long);

    public byte[] longToBytes(long x) {
        buffer.putLong(0, x);
        return buffer.array();
    }

    public long bytesToLong(byte[] bytes) {
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        return buffer.getLong();
    }

    public byte[] arraysLongToByte(long[] long_arr) {
        ByteBuffer buf = ByteBuffer.allocate(sizeof_long * long_arr.length);
        for (int i=0, j=0; i<long_arr.length; ++i, j+=sizeof_long) {
            buf.putLong(j, long_arr[i]);
        }
        return buf.array();
    }

    public long[] arraysByteToLong(byte[] byte_arr) {
        int len = byte_arr.length/sizeof_long;
        if (0==len) { return null;}
        long[] re=new long[len];
        ByteBuffer buf = ByteBuffer.wrap(byte_arr);
        for (int i=0, j=0; i<re.length; ++i, j+=sizeof_long) {
            re[i]=buf.getLong(j);
        }
        return re;
    }

    public static long flagOn(long masterFlags, int flagPos) {
        masterFlags = masterFlags | (1L << flagPos);
        return masterFlags;
    }

    public static long flagOff(long masterFlags, int flagPos) {
        masterFlags = masterFlags & ~(1L << flagPos);
        return masterFlags;
    }

    public static long flagSet(long masterFlags, int flagPos, boolean is) {
        if (is) {
            masterFlags = masterFlags | (1L << flagPos);
        } else {
            masterFlags = masterFlags & ~(1L << flagPos);
        }
        return masterFlags;
    }

    public static boolean getFlag(long masterFlags, int flagPos) {
        return 1L==((masterFlags >> flagPos) & 1L);
    }

    /* Get message send helper count */
    public static final long MASK_HOPS = 16256L;// 1111111 0000000
    public static long getHops (long masterFlags) {
        return (masterFlags & MASK_HOPS) >> 7;
    }
    public static long setHops (long masterFlags, long hops) {
        return  (masterFlags & ~MASK_HOPS) | (hops << 7);
    }

    public static long decHops (long masterFlags) {
        long hops = (masterFlags & MASK_HOPS) >> 7;
        hops = hops  <  1L  ?  0L  :  --hops;
        return  (masterFlags & ~MASK_HOPS) | (hops << 7);
    }
    public static final long MASK_LOADING = 2080768L;//1111111 0000000 0000000
    public static long getLOADING (long masterFlags) {
        return (masterFlags & MASK_LOADING) >> 14;
    }
    public static long setLOADING (long masterFlags, long percent) {
        return  (masterFlags & ~MASK_LOADING) | (percent << 14);
    }


  public static final int MASK_HOPSi  =  16256;// 1111111 0000000
  public static int get7bits (int masterFlags) {
    return (masterFlags & MASK_HOPSi) >> 7;
  }

  public static int set7bits (int masterFlags, int n7) {
    return  (masterFlags & ~MASK_HOPSi) | (n7 << 7);
  }

  public static int flagSet_i(int masterFlags, int flagPos, boolean is) {
    if (is) {
      masterFlags = masterFlags | (1 << flagPos);
    } else {
      masterFlags = masterFlags & ~(1 << flagPos);
    }
    return masterFlags;
  }

  public static boolean getFlag_i(int masterFlags, int flagPos) {
    return 1==((masterFlags >> flagPos) & 1);
  }
}