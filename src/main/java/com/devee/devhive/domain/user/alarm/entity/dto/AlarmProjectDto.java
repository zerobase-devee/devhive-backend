package com.devee.devhive.domain.user.alarm.entity.dto;

import com.devee.devhive.domain.project.entity.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmProjectDto {

    private Long projectId;
    private String projectName;

    public static AlarmProjectDto from(Project project) {
        return AlarmProjectDto.builder()
            .projectId(project.getId())
            .projectName(project.getName())
            .build();
    }
}
