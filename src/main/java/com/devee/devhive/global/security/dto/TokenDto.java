package com.devee.devhive.global.security.dto;

import com.devee.devhive.domain.auth.dto.LoginUserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class TokenDto {

  private String accessToken;
  private String refreshToken;
  private LoginUserDto userDto;
}
