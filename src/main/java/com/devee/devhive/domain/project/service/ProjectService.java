package com.devee.devhive.domain.project.service;

import static com.devee.devhive.domain.project.type.ProjectStatus.RECRUITING;
import static com.devee.devhive.domain.project.type.RecruitmentType.OFFLINE;
import static com.devee.devhive.global.exception.ErrorCode.NOT_FOUND_PROJECT;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.entity.ProjectTechStack;
import com.devee.devhive.domain.project.entity.dto.CreateProjectDto;
import com.devee.devhive.domain.project.repository.ProjectRepository;
import com.devee.devhive.domain.project.repository.ProjectTechStackRepository;
import com.devee.devhive.domain.techstack.entity.TechStack;
import com.devee.devhive.domain.techstack.repository.TechStackRepository;
import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.global.exception.CustomException;
import com.devee.devhive.global.security.service.PrincipalDetails;
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
}
