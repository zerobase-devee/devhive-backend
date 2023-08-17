package com.devee.devhive.global.oauth2.domain;

import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.type.Role;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuthAttributes {

  private final Map<String, Object> attributes;
  private final String nameAttributeKey;
  private final String name;
  private final String email;
  private final String profileImage;

  @Builder
  public OAuthAttributes(Map<String, Object> attributes,
      String nameAttributeKey, String name,
      String email, String picture) {
    this.attributes = attributes;
    this.nameAttributeKey = nameAttributeKey;
    this.name = name;
    this.email = email;
    this.profileImage = picture;
  }

  public static OAuthAttributes of(String registrationId,
      String userNameAttributeName,
      Map<String, Object> attributes) {
    if ("naver".equals(registrationId)) {
      return ofNaver(attributes);
    }

    return ofGoogle(userNameAttributeName, attributes);
  }

  private static OAuthAttributes ofGoogle(String userNameAttributeName,
      Map<String, Object> attributes) {
    return OAuthAttributes.builder()
        .name((String) attributes.get("name"))
        .email((String) attributes.get("email"))
        .picture((String) attributes.get("picture"))
        .attributes(attributes)
        .nameAttributeKey(userNameAttributeName)
        .build();
  }

  private static OAuthAttributes ofNaver(Map<String, Object> attributes) {
    Map<String, Object> response = (Map<String, Object>) attributes.get("response");

    return OAuthAttributes.builder()
        .name((String) response.get("name"))
        .email((String) response.get("email"))
        .picture((String) response.get("profile_image"))
        .attributes(response)
        .nameAttributeKey("id")
        .build();
  }


  public User toEntity() {
    return User.builder()
        .nickName(name)
        .email(email)
        .profileImage(profileImage)
        .password("OAUTH_TEST")
        .role(Role.USER)
        .build();
  }
}
