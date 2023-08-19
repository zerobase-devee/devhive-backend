package com.devee.devhive.domain.techstack.entity;

import com.devee.devhive.domain.techstack.entity.dto.TechStackDto;
import jakarta.persistence.Column;
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
public class TechStack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String image;

    public static TechStack from(TechStackDto techStackDto) {
        return TechStack.builder()
            .id(techStackDto.getId())
            .name(techStackDto.getName())
            .image(techStackDto.getImage())
            .build();
    }
}
