package com.hemingway.mbprintservice;

import android.text.TextUtils;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Description:
 * Data：2018/7/27-9:35
 * <p>
 * Author: hemingway
 */
public class DeviceUtils {

    public static final String PRE_DEVICE_NAME ="Hemingway_";
    public static final String DEVICE_NAME_PAPER = "9d9feb19591d8c068fd63d98cb35fcae";//Hemingway_paper   小写rang
    public static final String DEVICE_NAME_PAPER2 = "9106e90629cd19547f76f0e85a981d05";//Hemingway_paper   小写rang p2
    public static final String DEVICE_NAME_MM = "dcbd8c17445468f5715cd3d62e48e33a";//Hemingway_miao -x miao 小写ji

    private static final String TAG = "DeviceUtils";

    public static boolean isLegalDevice(String deviceName){
        if(TextUtils.isEmpty(deviceName)){
            return false;
        }
        boolean isLegal = false;
        deviceName = deviceName.toLowerCase();
        String deviceMd5Name = md5(PRE_DEVICE_NAME + deviceName);
        Log.d(TAG,deviceMd5Name);
        switch (deviceMd5Name){
            case DEVICE_NAME_MM:
            case DEVICE_NAME_PAPER:
            case DEVICE_NAME_PAPER2:
                isLegal = true;
                break;
                default:
        }
        return isLegal;
    }

    public static String md5(String content) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(content.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException",e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UnsupportedEncodingException", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10){
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }
}

