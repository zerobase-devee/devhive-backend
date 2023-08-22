package com.devee.devhive.domain.project.apply.entity.dto;

import com.devee.devhive.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicantUserDto {

    private Long userId;
    private String nickName;
    private String profileImage;
    private Long applicationId;

    public static ApplicantUserDto of(User user, Long applicationId) {
        return ApplicantUserDto.builder()
            .userId(user.getId())
            .nickName(user.getNickName())
            .profileImage(user.getProfileImage())
            .applicationId(applicationId)
            .build();
    }
}
