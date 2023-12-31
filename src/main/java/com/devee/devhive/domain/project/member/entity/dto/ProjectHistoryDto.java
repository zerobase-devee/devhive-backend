package com.devee.devhive.domain.project.member.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectHistoryDto {

    private String projectName;
    private Double totalAverageScore;

    public static ProjectHistoryDto of(String projectName, Double totalAverageScore) {
        return ProjectHistoryDto.builder()
            .projectName(projectName)
            .totalAverageScore(totalAverageScore)
            .build();
    }
}
