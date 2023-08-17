package com.devee.devhive.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginDto {

  @NotBlank(message = "필수 입력")
  @Pattern(regexp = "^.+@.+\\..+$", message = "이메일 형식에 맞게 입력해 주세요.")
  private String email;

  @NotBlank(message = "필수 입력")
  @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,}",
      message = "비밀번호는 최소 8자 이상, 영문 대 소문자, 숫자, 특수문자를 사용하세요. 특수문자는 반드시 포함 해주세요.")
  private String password;
}
