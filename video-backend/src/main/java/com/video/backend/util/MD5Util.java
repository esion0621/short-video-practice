package com.video.backend.util;

import org.springframework.util.DigestUtils;

public class MD5Util {
    public static String md5(String text) {
        return DigestUtils.md5DigestAsHex(text.getBytes());
    }
}
