package com.devee.devhive.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
public class EmailDto {
  @NotBlank(message = "필수 입력")
  @Pattern(regexp = "^.+@.+\\..+$", message = "이메일 형식에 맞게 입력해 주세요.")
  private String email;
}
