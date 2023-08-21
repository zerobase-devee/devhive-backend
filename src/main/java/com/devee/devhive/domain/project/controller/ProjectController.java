package com.devee.devhive.domain.project.controller;

import com.devee.devhive.domain.project.entity.dto.CreateProjectDto;
import com.devee.devhive.domain.project.entity.dto.UpdateProjectStatusDto;
import com.devee.devhive.domain.project.service.ProjectService;
import com.devee.devhive.global.security.service.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectController {

  private final ProjectService projectService;

  // 프로젝트 작성
  @PostMapping
  public void createProject(
      @AuthenticationPrincipal PrincipalDetails principal,
      @RequestBody CreateProjectDto createProjectDto) {

    projectService.createProject(principal, createProjectDto);
  }

  // 상태변경
  @PutMapping("/{projectId}/status")
  public void updateProjectStatus(
      @AuthenticationPrincipal PrincipalDetails principal,
      @PathVariable Long projectId,
      @RequestBody UpdateProjectStatusDto statusDto) {

    projectService.updateProjectStatus(principal, projectId, statusDto);
  }
}
