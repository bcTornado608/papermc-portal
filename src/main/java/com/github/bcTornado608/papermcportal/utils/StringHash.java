package com.github.bcTornado608.papermcportal.utils;

import java.nio.charset.StandardCharsets;

public class StringHash {
    public static int hash(String str){
        int res = 0;
        byte[] bytes = str.getBytes(StandardCharsets.US_ASCII);
        int mult = 1;
        for(byte i : bytes){
            res += (int)i * mult;
            mult*=7;
        }
        return res%2147479573;
    }
}
