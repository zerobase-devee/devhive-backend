package com.devee.devhive.domain.project.service;

import static com.devee.devhive.domain.project.type.ProjectStatus.COMPLETE;
import static com.devee.devhive.domain.project.type.ProjectStatus.RECRUITING;
import static com.devee.devhive.domain.project.type.ProjectStatus.RECRUITMENT_COMPLETE;
import static com.devee.devhive.domain.project.type.RecruitmentType.OFFLINE;
import static com.devee.devhive.global.exception.ErrorCode.NOT_FOUND_PROJECT;
import static com.devee.devhive.global.exception.ErrorCode.UNAUTHORIZED;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.entity.ProjectTechStack;
import com.devee.devhive.domain.project.entity.dto.CreateProjectDto;
import com.devee.devhive.domain.project.entity.dto.UpdateProjectDto;
import com.devee.devhive.domain.project.entity.dto.UpdateProjectStatusDto;
import com.devee.devhive.domain.project.repository.ProjectRepository;
import com.devee.devhive.domain.project.repository.ProjectTechStackRepository;
import com.devee.devhive.domain.project.type.ProjectStatus;
import com.devee.devhive.domain.techstack.entity.TechStack;
import com.devee.devhive.domain.techstack.entity.dto.TechStackDto;
import com.devee.devhive.domain.techstack.repository.TechStackRepository;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.global.exception.CustomException;
import com.devee.devhive.global.security.service.PrincipalDetails;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectService {

  private final ProjectRepository projectRepository;
  private final TechStackRepository techStackRepository;
  private final ProjectTechStackRepository projectTechStackRepository;

  public Project findById(Long projectId) {
    return projectRepository.findById(projectId)
        .orElseThrow(() -> new CustomException(NOT_FOUND_PROJECT));
  }

  // 내가 생성한 프로젝트 목록 페이지
  public Page<Project> getWriteProjects(Long userId, Pageable pageable) {
    return projectRepository.findByWriterUserIdOrderByCreatedDateDesc(userId, pageable);

  }

  // 프로젝트 작성
  @Transactional
  public void createProject(
      PrincipalDetails principal,
      CreateProjectDto createProjectDto) {
    User user = principal.getUser();

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

    projectRepository.save(project);

    List<ProjectTechStack> projectTechStacks = createProjectDto.getTechStacks().stream()
        .map(techStackDto -> {
          String techStackName = techStackDto.getName();
          TechStack techStack = techStackRepository.findByName(techStackName);
          return ProjectTechStack.of(project, techStack);
        })
        .collect(Collectors.toList());

    projectTechStackRepository.saveAll(projectTechStacks);
  }

  // 프로젝트 상태변경
  public void updateProjectStatus(
      PrincipalDetails principal,
      Long projectId,
      UpdateProjectStatusDto statusDto) {
    Project project = findById(projectId);

    User writerUser = project.getWriterUser();

    User currentUser = principal.getUser();

    if (writerUser != null && writerUser.getId().equals(currentUser.getId())) {
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
  public void updateProject(
      PrincipalDetails principal,
      Long projectId,
      UpdateProjectDto updateProjectDto) {

    Project project = findById(projectId);

    User writerUser = project.getWriterUser();
    User currentUser = principal.getUser();

    if (writerUser != null && writerUser.getId().equals(currentUser.getId())) {

      updateProjectFields(updateProjectDto);
      updateTechStacks(project, updateProjectDto.getTechStacks());
      projectRepository.save(project);
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

  // 프로젝트 기술 삭제후 추가
  private void updateTechStacks(Project project, List<TechStackDto> techStacks) {
    List<ProjectTechStack> existingTechStacks = projectTechStackRepository.findByProject(project);
    projectTechStackRepository.deleteAll(existingTechStacks);

    List<ProjectTechStack> techStacksToAdd = techStacks.stream()
        .map(techStackDto -> {
          String techStackName = techStackDto.getName();
          TechStack techStack = techStackRepository.findByName(techStackName);
          return ProjectTechStack.of(project, techStack);
        })
        .collect(Collectors.toList());

    projectTechStackRepository.saveAll(techStacksToAdd);
  }
}
