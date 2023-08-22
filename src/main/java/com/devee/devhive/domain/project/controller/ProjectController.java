package com.devee.devhive.domain.project.controller;

import com.devee.devhive.domain.project.comment.reply.service.ReplyService;
import com.devee.devhive.domain.project.comment.service.CommentService;
import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.entity.dto.CreateProjectDto;
import com.devee.devhive.domain.project.entity.dto.UpdateProjectDto;
import com.devee.devhive.domain.project.entity.dto.UpdateProjectStatusDto;
import com.devee.devhive.domain.project.service.ProjectService;
import com.devee.devhive.domain.project.service.ProjectTechStackService;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.global.security.service.PrincipalDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
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
  private final CommentService commentService;
  private final ReplyService replyService;
  private final ProjectTechStackService projectTechStackService;

  // 프로젝트 작성
  @PostMapping
  public void createProject(
      @AuthenticationPrincipal PrincipalDetails principal,
      @RequestBody CreateProjectDto createProjectDto) {

    User user = principal.getUser();

    Project project = projectService.createProject(createProjectDto, user);
    projectTechStackService.createProjectTechStacks(project, createProjectDto.getTechStacks());
  }

  // 상태변경
  @PutMapping("/{projectId}/status")
  public void updateProjectStatus(
      @AuthenticationPrincipal PrincipalDetails principal,
      @PathVariable Long projectId,
      @RequestBody UpdateProjectStatusDto statusDto) {
    User user = principal.getUser();

    projectService.updateProjectStatus(user, projectId, statusDto);
  }

  // 프로젝트 수정
  @PutMapping("/{projectId}")
  public void updateProject(
      @AuthenticationPrincipal PrincipalDetails principal,
      @PathVariable Long projectId,
      @RequestBody UpdateProjectDto updateProjectDto) {

    User user = principal.getUser();
    Project project = projectService.updateProject(user, projectId, updateProjectDto);
    projectTechStackService.updateProjectTechStacks(project, updateProjectDto.getTechStacks());
  }

  // 프로젝트 삭제
  @DeleteMapping("/{projectId}")
  public void deleteProject(
      @AuthenticationPrincipal PrincipalDetails principal,
      @PathVariable Long projectId
  ) {
    User user = principal.getUser();

    List<Long> commentIdList = commentService.deleteCommentsByProjectId(projectId);
    replyService.deleteRepliesByCommentList(commentIdList);
    projectTechStackService.deleteProjectTechStacksByProjectId(projectId);
    projectService.deleteProject(user, projectId);
  }
}