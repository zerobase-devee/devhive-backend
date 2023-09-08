package com.devee.devhive.domain.auth.dto;

import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.type.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginUserDto {
  private Long userId;
  private String nickName;
  private String profileImage;
  private Role role;

  public static LoginUserDto from(User user) {
    return LoginUserDto.builder()
        .userId(user.getId())
        .nickName(user.getNickName())
        .profileImage(user.getProfileImage())
        .role(user.getRole())
        .build();
  }
}
