package com.devee.devhive.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JoinDto {
  @NotBlank(message = "필수 입력")
  @Pattern(regexp = "^.+@.+\\..+$", message = "이메일 형식에 맞게 입력해 주세요.")
  private String email;

  @NotBlank(message = "필수 입력")
  @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,}",
      message = "비밀번호는 최소 8자 이상, 영문 대 소문자, 숫자, 특수문자를 사용하세요. 특수문자는 반드시 포함 해주세요.")
  private String password;

  @NotBlank
  @Size(min = 1, max = 6) // 1~6자
  @Pattern(regexp = "^(?!.*\\s)[a-zA-Z0-9가-힣]*$",
      message = "닉네임 형식을 확인해주세요. 공백이나 특수문자는 사용할 수 없습니다.")
  private String nickName;

  private String verificationCode;
}
