package com.devee.devhive.domain.user.entity.dto;

import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.type.ProviderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyInfoDto {

    private Long userId;
    private String email;
    private String region;
    private String nickName;
    private boolean isLocalLogin;
    private String profileImage;
    private String intro;

    public static MyInfoDto of(User user) {
        return MyInfoDto.builder()
            .userId(user.getId())
            .email(user.getEmail())
            .region(user.getRegion())
            .nickName(user.getNickName())
            .isLocalLogin(user.getProviderType() == ProviderType.LOCAL)
            .profileImage(user.getProfileImage())
            .intro(user.getIntro())
            .build();
    }

}
