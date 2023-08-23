package com.devee.devhive.domain.project.controller;

import static com.devee.devhive.global.exception.ErrorCode.PROJECT_CANNOT_DELETED;

import com.devee.devhive.domain.project.comment.reply.service.ReplyService;
import com.devee.devhive.domain.project.comment.service.CommentService;
import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.entity.dto.CreateProjectDto;
import com.devee.devhive.domain.project.entity.dto.UpdateProjectDto;
import com.devee.devhive.domain.project.entity.dto.UpdateProjectStatusDto;
import com.devee.devhive.domain.project.member.entity.ProjectMember;
import com.devee.devhive.domain.project.member.service.ProjectMemberService;
import com.devee.devhive.domain.project.service.ProjectService;
import com.devee.devhive.domain.project.service.ProjectTechStackService;
import com.devee.devhive.domain.project.type.ProjectStatus;
import com.devee.devhive.domain.user.alarm.entity.dto.AlarmProjectDto;
import com.devee.devhive.domain.user.alarm.entity.dto.AlarmUserDto;
import com.devee.devhive.domain.user.alarm.entity.form.AlarmForm;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.user.type.RelatedUrlType;
import com.devee.devhive.global.exception.CustomException;
import com.devee.devhive.global.security.service.PrincipalDetails;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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
  private final ApplicationEventPublisher eventPublisher;

  private final ProjectService projectService;
  private final CommentService commentService;
  private final ReplyService replyService;
  private final ProjectTechStackService projectTechStackService;
  private final ProjectMemberService projectMemberService;

  // 프로젝트 작성
  @PostMapping
  public void createProject(
      @AuthenticationPrincipal PrincipalDetails principal,
      @RequestBody CreateProjectDto createProjectDto) {

    User user = principal.getUser();

    Project project = projectService.createProject(createProjectDto, user);
    projectTechStackService.createProjectTechStacks(project, createProjectDto.getTechStacks());
    projectMemberService.saveProjectLeader(user, project);
  }

  // 상태변경
  @PutMapping("/{projectId}/status")
  public void updateProjectStatus(
      @AuthenticationPrincipal PrincipalDetails principal,
      @PathVariable Long projectId,
      @RequestBody UpdateProjectStatusDto statusDto) {
    User user = principal.getUser();

    Project saveProject = projectService.updateProjectStatus(user, projectId, statusDto);
    if (saveProject.getStatus() == ProjectStatus.COMPLETE) {
      List<ProjectMember> members = projectMemberService.getProjectMemberByProjectId(projectId);
      // 프로젝트 멤버들에게 팀원 평가 권유 알림 이벤트 발행
      for (ProjectMember projectMember : members) {
        User member = projectMember.getUser();

        AlarmForm alarmForm = AlarmForm.builder()
            .receiverUser(member)
            .projectDto(AlarmProjectDto.of(saveProject, RelatedUrlType.PROJECT_INFO))
            .userDto(AlarmUserDto.of(member, RelatedUrlType.MY_INFO))
            .build();
        eventPublisher.publishEvent(alarmForm);
      }
    }
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
    Project project = projectService.findById(projectId);
    if (!Objects.equals(project.getWriterUser().getId(), user.getId())) {
      throw new CustomException(PROJECT_CANNOT_DELETED);
    }

    List<Long> commentIdList = commentService.deleteCommentsByProjectId(projectId);
    replyService.deleteRepliesByCommentList(commentIdList);
    projectTechStackService.deleteProjectTechStacksByProjectId(projectId);
    projectMemberService.deleteProjectMembers(projectId);
    projectService.deleteProject(project);
  }
}