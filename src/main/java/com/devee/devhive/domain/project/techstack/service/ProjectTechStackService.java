package com.devee.devhive.domain.project.techstack.service;

import com.devee.devhive.domain.project.entity.Project;
import com.devee.devhive.domain.project.techstack.entity.ProjectTechStack;
import com.devee.devhive.domain.project.techstack.repository.ProjectTechStackRepository;
import com.devee.devhive.domain.techstack.entity.TechStack;
import com.devee.devhive.domain.techstack.entity.dto.TechStackDto;
import com.devee.devhive.domain.techstack.repository.TechStackRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectTechStackService {

  private final ProjectTechStackRepository projectTechStackRepository;
  private final TechStackRepository techStackRepository;

  public List<ProjectTechStack> getTechStacks(Long projectId) {
    return projectTechStackRepository.findAllByProjectId(projectId);
  }

  public void createProjectTechStacks(Project project, List<TechStackDto> techStacks) {
    List<ProjectTechStack> projectTechStacks = mapToProjectTechStackList(project, techStacks);
    projectTechStackRepository.saveAll(projectTechStacks);
  }

  public void updateProjectTechStacks(Project project, List<TechStackDto> techStacks) {
    // 기존 기술 스택 삭제
    List<ProjectTechStack> projectTechStacks = getTechStacks(project.getId());
    projectTechStackRepository.deleteAll(projectTechStacks);

    // 새로운 기술 스택 추가
    List<ProjectTechStack> newProjectTechStacks = mapToProjectTechStackList(project, techStacks);
    projectTechStackRepository.saveAll(newProjectTechStacks);
  }

  public void deleteProjectTechStacksByProjectId(Long projectId) {
    List<ProjectTechStack> projectTechStacks = getTechStacks(projectId);
    projectTechStackRepository.deleteAll(projectTechStacks);
  }

  private List<ProjectTechStack> mapToProjectTechStackList(Project project,
      List<TechStackDto> techStacks) {
    return techStacks.stream().map(techStackDto -> {
          String techStackName = techStackDto.getName();
          TechStack techStack = techStackRepository.findByName(techStackName);
          return ProjectTechStack.of(project, techStack);
        })
        .collect(Collectors.toList());
  }

  public List<ProjectTechStack> getProjectTechStacksByProject(Project project) {
    return projectTechStackRepository.findByProject(project);
  }

}