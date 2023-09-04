package com.devee.devhive.domain.project.entity.dto;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.type.ProjectStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimpleProjectDto {
    private Long projectId;
    private String name;
    private ProjectStatus status;

    public static SimpleProjectDto from(Project project) {
        return SimpleProjectDto.builder()
            .projectId(project.getId())
            .name(project.getName())
            .status(project.getStatus())
            .build();
    }

}
