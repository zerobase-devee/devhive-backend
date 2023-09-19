package com.devee.devhive.domain.project.member.controller;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.member.entity.ProjectMember;
import com.devee.devhive.domain.project.member.entity.dto.ProjectHistoryDto;
import com.devee.devhive.domain.project.member.service.ProjectMemberService;
import com.devee.devhive.domain.project.review.service.ProjectReviewService;
import com.devee.devhive.domain.project.type.ProjectStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members/users")
@RequiredArgsConstructor
@Tag(name = "PROJECT MEMBER API", description = "프로젝트 멤버 API")
public class ProjectMemberController {

  private final ProjectMemberService projectMemberService;
  private final ProjectReviewService projectReviewService;

  @GetMapping("/{userId}/project-histories")
  @Operation(summary = "유저의 프로젝트 히스토리 목록 조회")
  public ResponseEntity<List<ProjectHistoryDto>> getUserProjectHistories(@PathVariable("userId") Long userId) {
    List<Project> projects = projectMemberService.findAllByUserId(userId).stream()
        .map(ProjectMember::getProject)
        .toList();
    List<ProjectHistoryDto> projectHistoryList = new ArrayList<>();

    for (Project project : projects) {
      if (project.getStatus() == ProjectStatus.COMPLETE) {
        int memberCount = projectMemberService.getProjectMemberByProjectId(project.getId()).size();
        ProjectHistoryDto projectHistory = ProjectHistoryDto.of(project.getName(),
            projectReviewService.getAverageTotalScoreByTargetUserAndProject(userId, project.getId(), memberCount));
        projectHistoryList.add(projectHistory);
      }
    }

    return ResponseEntity.ok(projectHistoryList);
  }

  @GetMapping("/{userId}/hive-level")
  @Operation(summary = "유저의 벌집 레벨 조회")
  public ResponseEntity<Integer> getUserHiveLevel(@PathVariable("userId") Long userId) {
    return ResponseEntity.ok(projectMemberService.countCompletedProjectsByUserId(userId));
  }

  @DeleteMapping("/{userId}/projects/{projectId}")
  @Operation(summary = "퇴출팀원 삭제, 퇴출알림 발생")
  public void deleteProjectMember(@PathVariable("userId") Long userId, @PathVariable("projectId") Long projectId) {
    projectMemberService.deleteMemberFromProjectAndSendAlarm(projectId, userId);
  }
}
