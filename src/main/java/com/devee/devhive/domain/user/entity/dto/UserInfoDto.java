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
    private Long favoriteId;
    private String nickName;
    private String profileImage;
    private String intro;

    public static UserInfoDto of(User user, Long favoriteId) {
        return UserInfoDto.builder()
            .userId(user.getId())
            .favoriteId(favoriteId)
            .nickName(user.getNickName())
            .profileImage(user.getProfileImage())
            .intro(user.getIntro())
            .build();
    }
}
