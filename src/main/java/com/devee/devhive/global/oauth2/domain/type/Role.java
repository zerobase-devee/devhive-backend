package com.devee.devhive.global.oauth2.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
  GUEST("ROLE_GUEST", "게스트"),
  USER("ROLE_USER", "유저");

  private final String key;
  private final String title;
}
