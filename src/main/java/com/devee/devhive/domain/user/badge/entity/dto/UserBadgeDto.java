package com.devee.devhive.domain.user.badge.entity.dto;

import com.devee.devhive.domain.badge.entity.dto.BadgeDto;
import com.devee.devhive.domain.user.badge.entity.UserBadge;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBadgeDto {
    private BadgeDto badgeDto;
    private int score;

    public static UserBadgeDto from(UserBadge userBadge) {
        return UserBadgeDto.builder()
            .badgeDto(BadgeDto.from(userBadge.getBadge()))
            .score(userBadge.getTotalScore())
            .build();
    }
}
