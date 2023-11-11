package com.devee.devhive.domain.user.entity.dto;

import com.devee.devhive.domain.user.badge.entity.dto.UserBadgeDto;
import com.devee.devhive.domain.user.entity.User;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RankUserDto {
    private Long userId;
    private String nickName;
    private String profileImage;
    private double rankPoint;
    private List<UserBadgeDto> userBadges;

    public static RankUserDto of(User user, List<UserBadgeDto> userBadges) {
        return RankUserDto.builder()
            .userId(user.getId())
            .nickName(user.getNickName())
            .profileImage(user.getProfileImage())
            .rankPoint(user.getRankPoint())
            .userBadges(userBadges)
            .build();
    }
}
