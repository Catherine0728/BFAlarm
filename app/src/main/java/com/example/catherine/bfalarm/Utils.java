package com.example.catherine.bfalarm;

/**
 * Created by catherine on 17/3/24.
 */

public class Utils {
    public static boolean isEmpty(String accountName) {
        if (accountName == null || accountName == "")
            return true;
        return false;
    }
}
