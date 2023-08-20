package com.devee.devhive.domain.user.entity.dto;

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
    private double totalAverageScore;

    public static ProjectHistoryDto of(String projectName, double totalAverageScore) {
        return ProjectHistoryDto.builder()
            .projectName(projectName)
            .totalAverageScore(totalAverageScore)
            .build();
    }
}
