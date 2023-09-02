package com.devee.devhive.domain.user.entity.dto;

import com.devee.devhive.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoDto {

    private Long userId;
    private boolean isFavorite;
    private String nickName;
    private String profileImage;
    private String intro;

    public static UserInfoDto of(User user, boolean isFavorite) {
        return UserInfoDto.builder()
            .userId(user.getId())
            .isFavorite(isFavorite)
            .nickName(user.getNickName())
            .profileImage(user.getProfileImage())
            .intro(user.getIntro())
            .build();
    }
}
