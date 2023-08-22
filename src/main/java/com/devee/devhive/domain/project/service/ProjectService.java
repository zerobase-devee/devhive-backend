package com.devee.devhive.domain.project.service;

import static com.devee.devhive.domain.project.type.ProjectStatus.COMPLETE;
import static com.devee.devhive.domain.project.type.ProjectStatus.RECRUITING;
import static com.devee.devhive.domain.project.type.ProjectStatus.RECRUITMENT_COMPLETE;
import static com.devee.devhive.domain.project.type.RecruitmentType.OFFLINE;
import static com.devee.devhive.domain.user.type.Role.ADMIN;
import static com.devee.devhive.global.exception.ErrorCode.NOT_FOUND_PROJECT;
import static com.devee.devhive.global.exception.ErrorCode.PROJECT_CANNOT_DELETED;
import static com.devee.devhive.global.exception.ErrorCode.UNAUTHORIZED;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.entity.dto.CreateProjectDto;
import com.devee.devhive.domain.project.entity.dto.UpdateProjectDto;
import com.devee.devhive.domain.project.entity.dto.UpdateProjectStatusDto;
import com.devee.devhive.domain.project.repository.ProjectRepository;
import com.devee.devhive.domain.project.type.ProjectStatus;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.global.exception.CustomException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectService {

  private final ProjectRepository projectRepository;

  public Project findById(Long projectId) {
    return projectRepository.findById(projectId)
        .orElseThrow(() -> new CustomException(NOT_FOUND_PROJECT));
  }

  // 내가 생성한 프로젝트 목록 페이지
  public Page<Project> getWriteProjects(Long userId, Pageable pageable) {
    return projectRepository.findByWriterUserIdOrderByCreatedDateDesc(userId, pageable);

  }

  // 프로젝트 작성
  public Project createProject(
      CreateProjectDto createProjectDto,
      User user) {
    Project project = Project.builder()
        .writerUser(user)
        .title(createProjectDto.getTitle())
        .name(createProjectDto.getProjectName())
        .content(createProjectDto.getContent())
        .teamSize(createProjectDto.getTeamSize())
        .recruitmentType(createProjectDto.getRecruitmentType())
        .developmentType(createProjectDto.getDevelopmentType())
        .deadline(createProjectDto.getDeadline())
        .status(RECRUITING)
        .build();

    if (createProjectDto.getRecruitmentType() == OFFLINE) {
      project.setRegion(createProjectDto.getRegion());
    }

    return projectRepository.save(project);
  }

  // 프로젝트 상태변경
  public void updateProjectStatus(
      User user,
      Long projectId,
      UpdateProjectStatusDto statusDto) {
    Project project = findById(projectId);

    User writerUser = project.getWriterUser();

    if (writerUser != null && writerUser.getId().equals(user.getId())) {
      ProjectStatus status = statusDto.getStatus();

      if (status == RECRUITMENT_COMPLETE) {
        project.setStartDate(LocalDateTime.now());
      } else if (status == COMPLETE) {
        project.setEndDate(LocalDateTime.now());
      }

      project.setStatus(status);

      projectRepository.save(project);
    } else {
      throw new CustomException(UNAUTHORIZED);
    }
  }

  // 프로젝트 수정
  @Transactional
  public Project updateProject(
      User user,
      Long projectId,
      UpdateProjectDto updateProjectDto) {

    Project project = findById(projectId);

    User writerUser = project.getWriterUser();

    if (writerUser != null && writerUser.getId().equals(user.getId())) {

      updateProjectFields(updateProjectDto);
      return projectRepository.save(project);
    } else {
      throw new CustomException(UNAUTHORIZED);
    }
  }

  // 프로젝트 필드 업데이트
  private void updateProjectFields(UpdateProjectDto updateProjectDto) {

    Project project = Project.builder()
        .title(updateProjectDto.getTitle())
        .name(updateProjectDto.getProjectName())
        .teamSize(updateProjectDto.getTeamSize())
        .recruitmentType(updateProjectDto.getRecruitmentType())
        .developmentType(updateProjectDto.getDevelopmentType())
        .deadline(updateProjectDto.getDeadline())
        .build();

    if (updateProjectDto.getRecruitmentType() == OFFLINE) {
      project.setRegion(updateProjectDto.getRegion());
    }
  }

  // 프로젝트 삭제
  @Transactional
  public void deleteProject(User user, Long projectId) {
    Project project = findById(projectId);

    User writerUser = project.getWriterUser();

    if (user.getRole() == ADMIN || writerUser.getId().equals(user.getId())) {
      if (project.getStatus() != ProjectStatus.RECRUITING) {
        throw new CustomException(PROJECT_CANNOT_DELETED);
      }
      projectRepository.delete(project);
    } else {
      throw new CustomException(UNAUTHORIZED);
    }
  }
}