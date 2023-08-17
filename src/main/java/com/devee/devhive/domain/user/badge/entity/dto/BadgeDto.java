package com.devee.devhive.domain.user.badge.entity.dto;

import com.devee.devhive.domain.user.badge.entity.Badge;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BadgeDto {
    private String name;
    private String image;

    public static BadgeDto from(Badge badge) {
        return BadgeDto.builder()
            .name(badge.getName())
            .image(badge.getImageUrl())
            .build();
    }
}
