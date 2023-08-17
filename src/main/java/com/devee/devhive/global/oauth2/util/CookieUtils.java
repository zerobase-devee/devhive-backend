package com.devee.devhive.global.oauth2.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Optional;
import org.springframework.util.SerializationUtils;

public class CookieUtils {

  public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
    Cookie[] cookies = request.getCookies();

    if (cookies != null && cookies.length > 0) {
      for (Cookie c : cookies) {
        if (c.getName().equals(name)) {
          return Optional.of(c);
        }
      }
    }
    return Optional.empty();
  }

  public static void addCookie(HttpServletResponse response, String name,
      String value, int maxAge) {
    Cookie cookie = new Cookie(name, value);
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    cookie.setMaxAge(maxAge);
    response.addCookie(cookie);
  }

  public static void deleteCookie(HttpServletRequest request,
      HttpServletResponse response, String name) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null && cookies.length > 0) {
      for (Cookie c : cookies) {
        if (c.getName().equals(name)) {
          c.setValue("");
          c.setPath("/");
          c.setMaxAge(0);
          response.addCookie(c);
        }
      }
    }
  }

  public static String serialize(Object o) {
    return Base64.getUrlEncoder()
        .encodeToString(SerializationUtils.serialize(o));
  }

  public static <T> T deserialize(Cookie cookie, Class<T> cls) {
    return cls.cast(SerializationUtils.deserialize(
        Base64.getUrlDecoder().decode(cookie.getValue())
    ));
  }
}
