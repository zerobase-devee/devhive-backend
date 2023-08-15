package com.devee.devhive.global.oauth2.domain.dto;

import com.devee.devhive.domain.user.entity.User;
import java.io.Serializable;
import lombok.Getter;

@Getter
public class SessionUserDto implements Serializable {

  private final String name;
  private final String email;
  private final String profile;

  public SessionUserDto(User user) {
    this.name = user.getNickName();
    this.email = user.getEmail();
    this.profile = user.getProfileImage();
  }
}
