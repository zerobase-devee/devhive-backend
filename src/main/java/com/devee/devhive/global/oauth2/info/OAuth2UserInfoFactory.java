package com.devee.devhive.global.oauth2.info;

import com.devee.devhive.domain.user.type.ProviderType;
import java.util.Map;

public class OAuth2UserInfoFactory {
  public static OAuth2UserInfo getOAuth2UserInfo(ProviderType providerType, Map<String, Object> attributes) {
    return switch (providerType) {
      case GOOGLE -> new GoogleOAuth2UserInfo(attributes);
      case NAVER -> new NaverOAuth2UserInfo(attributes);
      case KAKAO -> new KakaoOAuth2UserInfo(attributes);
      default -> throw new IllegalArgumentException("Invalid Provider Type.");
    };
  }
}
