package com.devee.devhive.domain.project.entity.dto;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.member.entity.dto.ProjectMemberDto;
import com.devee.devhive.domain.project.type.ProjectStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyProjectInfoDto {

    private String name;
    private Long projectId;
    private Long userId;
    private boolean leader;
    private LocalDateTime deadline; // 모집 마감 종료일자
    private ProjectStatus status;
    private LocalDateTime startDate; // 프로젝트 시작일자
    private LocalDateTime endDate;   // 프로젝트 종료일자
    private List<ProjectMemberDto> projectMembers;
    private Double totalAverageScore;
    private Long roomId;

    public static MyProjectInfoDto of(Long userId, Project project, List<ProjectMemberDto> projectMemberDtoList,
        double totalAverageScore, boolean leader, Long roomId) {

        return MyProjectInfoDto.builder()
            .userId(userId)
            .name(project.getName())
            .projectId(project.getId())
            .deadline(project.getDeadline())
            .status(project.getStatus())
            .startDate(project.getStartDate())
            .endDate(project.getEndDate())
            .projectMembers(projectMemberDtoList)
            .totalAverageScore(totalAverageScore)
            .leader(leader)
            .roomId(roomId)
            .build();
    }
}
