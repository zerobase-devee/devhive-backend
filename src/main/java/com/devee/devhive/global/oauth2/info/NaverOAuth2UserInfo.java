package com.devee.devhive.global.oauth2.info;

import java.util.Map;

public class NaverOAuth2UserInfo extends OAuth2UserInfo {

  public NaverOAuth2UserInfo(Map<String, Object> attributes) {
    super(attributes);
  }

  @Override
  public String getName() {
    Map<String, Object> response = (Map<String, Object>) attributes.get("response");
    if (response == null) {
      return null;
    }
    return (String) response.get("name");
  }

  @Override
  public String getEmail() {
    Map<String, Object> response = (Map<String, Object>) attributes.get("response");
    if (response == null) {
      return null;
    }
    return (String) response.get("email");
  }
}