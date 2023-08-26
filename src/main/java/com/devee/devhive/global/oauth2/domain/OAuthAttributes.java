package com.devee.devhive.global.oauth2.domain;

import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.type.ProviderType;
import com.devee.devhive.global.oauth2.info.GoogleOAuth2UserInfo;
import com.devee.devhive.global.oauth2.info.KakaoOAuth2UserInfo;
import com.devee.devhive.global.oauth2.info.NaverOAuth2UserInfo;
import com.devee.devhive.global.oauth2.info.OAuth2UserInfo;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuthAttributes {

  private final String nameAttributeKey; // OAuth2 로그인 진행 시 키가 되는 필드 값, PK와 같은 의미
  private final OAuth2UserInfo oauth2UserInfo; // 소셜 타입별 로그인 유저 정보(닉네임, 이메일, 프로필 사진 등등)

  @Builder
  public OAuthAttributes(String nameAttributeKey, OAuth2UserInfo oauth2UserInfo) {
    this.nameAttributeKey = nameAttributeKey;
    this.oauth2UserInfo = oauth2UserInfo;
  }

  public static OAuthAttributes of(ProviderType providerType,
      String userNameAttributeName, Map<String, Object> attributes) {

    if (providerType == ProviderType.NAVER) {
      return ofNaver(userNameAttributeName, attributes);
    }
    if (providerType == ProviderType.KAKAO) {
      return ofKakao(userNameAttributeName, attributes);
    }
    return ofGoogle(userNameAttributeName, attributes);
  }

  private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
    return OAuthAttributes.builder()
        .nameAttributeKey(userNameAttributeName)
        .oauth2UserInfo(new KakaoOAuth2UserInfo(attributes))
        .build();
  }

  public static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
    return OAuthAttributes.builder()
        .nameAttributeKey(userNameAttributeName)
        .oauth2UserInfo(new GoogleOAuth2UserInfo(attributes))
        .build();
  }

  public static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
    return OAuthAttributes.builder()
        .nameAttributeKey(userNameAttributeName)
        .oauth2UserInfo(new NaverOAuth2UserInfo(attributes))
        .build();
  }

  public User toEntity(ProviderType providerType, OAuth2UserInfo oauth2UserInfo) {
    return new User(
        providerType.getValue() + "_" + oauth2UserInfo.getProviderId(),
        oauth2UserInfo.getProviderId(),
        oauth2UserInfo.getEmail(),
        providerType,
        oauth2UserInfo.getProviderId()
        );
  }
}
