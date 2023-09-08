package com.devee.devhive.domain.badge.entity.dto;

import com.devee.devhive.domain.badge.entity.Badge;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BadgeDto {
    private Long id;
    private String name;
    private String image;

    public static BadgeDto from(Badge badge) {
        return BadgeDto.builder()
            .id(badge.getId())
            .name(badge.getName())
            .image(badge.getImageUrl())
            .build();
    }
}
