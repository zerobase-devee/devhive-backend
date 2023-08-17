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

    public static ProjectHistoryDto from(Object[] projectData) {
        return ProjectHistoryDto.builder()
            .projectName((String) projectData[0])
            .totalAverageScore(calculateTotalAverageScore((double) projectData[1]))
            .build();
    }

    // 소수 첫째 자리까지 반올림
    private static double calculateTotalAverageScore(double score) {
        return Math.round(score / 5.0 * 10.0) / 10.0;
    }
}
