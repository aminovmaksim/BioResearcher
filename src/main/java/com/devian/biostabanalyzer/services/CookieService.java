package com.devian.biostabanalyzer.services;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CookieService {

    public static void setCookie(String key, String value, HttpServletResponse response) {
        response.addCookie(new Cookie(key, URLEncoder.encode(value, StandardCharsets.UTF_8)));
    }

    public static String getCookie(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
