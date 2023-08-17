package com.devee.devhive.domain.user.entity.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateBasicInfoForm {

    @NotBlank
    @Size(min = 1, max = 6) // 1~6자
    @Pattern(regexp = "^[a-zA-Z0-9가-힣]*$", message = "닉네임 형식을 확인해주세요.")
    private String nickName;

    private String region;

    @Size(max = 100) // 최대 100자
    private String intro;
}
