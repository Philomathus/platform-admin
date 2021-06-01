package com.qiqilm.server.admin.utils;

import java.io.Serializable;

/**
 * @author axing
 * @version 1.0
 * @date 2021/6/1/001 14:01
 */
public class PhoneUtil implements Serializable {

    private static final long serialVersionUID = 1L;

    public static String getEncPhone(String str){
        String str1 = str.substring(0, 3);
        String str2 = str.substring(7, 11);
        return str1+"****"+str2;
    }
}
