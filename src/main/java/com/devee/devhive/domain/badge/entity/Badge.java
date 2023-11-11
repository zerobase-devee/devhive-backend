package com.devee.devhive.domain.badge.entity;

import com.devee.devhive.domain.badge.entity.dto.BadgeDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Badge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String imageUrl;

    public static Badge from(BadgeDto dto) {
        return Badge.builder()
            .id(dto.getId())
            .name(dto.getName())
            .imageUrl(dto.getImage())
            .build();
    }
}
