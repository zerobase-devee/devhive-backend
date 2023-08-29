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
public class SimpleUserDto {

    private Long userId;
    private String nickName;
    private String profileImage;

    public static SimpleUserDto from(User user) {
        if (user == null) {
            return null;
        }
        return SimpleUserDto.builder()
            .userId(user.getId())
            .nickName(user.getNickName())
            .profileImage(user.getProfileImage())
            .build();
    }
}
