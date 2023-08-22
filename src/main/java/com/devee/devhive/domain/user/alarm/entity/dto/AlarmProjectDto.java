package com.devee.devhive.domain.user.alarm.entity.dto;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.user.type.RelatedUrlType;
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
    private String relatedProjectUrl;

    public static AlarmProjectDto of(Project project, RelatedUrlType urlType) {
        Long projectId = project.getId();
        return AlarmProjectDto.builder()
            .projectId(projectId)
            .projectName(project.getName())
            .relatedProjectUrl(urlType.getValue() + projectId)
            .build();
    }
}
