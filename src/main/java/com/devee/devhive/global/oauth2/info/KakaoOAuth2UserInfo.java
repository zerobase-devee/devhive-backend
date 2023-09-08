package com.devee.devhive.global.oauth2.info;

import java.util.Map;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo {

  public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
    super(attributes);
  }

  @Override
  public String getEmail() {
    return (String) getValue("kakao_account", "email");
  }

  @Override
  public String getName() {
    return (String) getValue("kakao_account", "profile", "nickname");
  }

  @Override
  public String getProviderId() {
    return attributes.get("id").toString();
  }

  private Object getValue(String... keys) {
    Map<String, Object> current = attributes;
    for (String key : keys) {
      Object value = current.get(key);
      if (value == null) {
        return null;
      }
      if (value instanceof Map) {
        current = (Map<String, Object>) value;
      } else {
        return value;
      }
    }
    return current;
  }
}