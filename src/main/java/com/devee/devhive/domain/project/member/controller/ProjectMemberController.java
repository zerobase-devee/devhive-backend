package com.devee.devhive.domain.project.member.controller;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.member.entity.ProjectMember;
import com.devee.devhive.domain.project.member.service.ProjectMemberService;
import com.devee.devhive.domain.project.review.service.ProjectReviewService;
import com.devee.devhive.domain.project.member.entity.dto.ProjectHistoryDto;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members/users")
@RequiredArgsConstructor
public class ProjectMemberController {

  private final ProjectMemberService projectMemberService;
  private final ProjectReviewService projectReviewService;

  @GetMapping("/{userId}/project-histories")
  public ResponseEntity<List<ProjectHistoryDto>> getUserProjectHistories(@PathVariable("userId") Long userId) {
    List<Project> projects = projectMemberService.findAllByUserId(userId).stream()
        .map(ProjectMember::getProject)
        .toList();
    return ResponseEntity.ok(projects.stream()
        .map(project -> ProjectHistoryDto.of(project.getName(),
            projectReviewService.getAverageTotalScoreByTargetUserAndProject(userId, project.getId()))
        ).collect(Collectors.toList())
    );
  }

  @GetMapping("/{userId}/hive-level")
  public ResponseEntity<Integer> getUserHiveLevel(@PathVariable("userId") Long userId) {
    return ResponseEntity.ok(projectMemberService.countCompletedProjectsByUserId(userId));
  }
}
